package ganymedes01.etfuturum.world;

import cpw.mods.fml.common.ZipperUtil;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.gui.GuiWorldHeightMigrationProgress;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.storage.MapStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class WorldHeightHandler {

    private static final Logger logger = LogManager.getLogger();

    private static boolean isIncreasedWorldHeightEnabled = false;
    private static int maxWorldHeight = 512;
    private static int chunkSections = 32;
    private static int worldHeightOffset = 64;

    public static void initWorldHeightHandler() {

        int max = ConfigExperiments.maxWorldHeight;

        isIncreasedWorldHeightEnabled = ConfigExperiments.enableIncreasedWorldHeight;
        maxWorldHeight = max;
        chunkSections = maxWorldHeight >> 4;

        if(chunkSections < 32 && maxWorldHeight % 16 != 0) chunkSections++;

        if(worldHeightOffset >= -128 && worldHeightOffset <= 128) {

            worldHeightOffset = ConfigExperiments.worldHeightOffset >> 4 << 4;
        }
    }

    public static boolean isIncreasedWorldHeightEnabled() {

        return isIncreasedWorldHeightEnabled;
    }

    public static int getMaxWorldHeight() {

        return isIncreasedWorldHeightEnabled() ? maxWorldHeight : 256;
    }

    public static int getChunkSections() {

        return isIncreasedWorldHeightEnabled() ? chunkSections : 16;
    }

    public static int getWorldHeightOffset() {

        return worldHeightOffset;
    }

    public static class WorldHeightMigrator {

        public static boolean migrateWorldHeight(File worldDir) throws IOException {

            return migrateWorldHeightWithProgress(worldDir, worldHeightOffset, null);
        }

        public static boolean migrateWorldHeightWithProgress(File worldDir, GuiWorldHeightMigrationProgress gui) throws IOException {

            return migrateWorldHeightWithProgress(worldDir, worldHeightOffset, gui);
        }

        /*
            This moves the y level for all blocks in the world up and down for the specified offset.
         */
        public static boolean migrateWorldHeightWithProgress(File worldDir, int offset, GuiWorldHeightMigrationProgress gui) throws IOException {

            int migratedChunks = 0;

            logger.info("[World Height Migration] Started to create a backup of {}.", worldDir.getName());

            NBTTagCompound leveldatNBT = CompressedStreamTools.readCompressed(new FileInputStream(new File(worldDir, "level.dat")));

            if (gui != null) {

                gui.setProgress(-1);
                gui.setMaxProgress(0);
                gui.setProgressText(StatCollector.translateToLocal("gui.worldheight.migration.backup"));
            }

            ZipperUtil.backupWorld(worldDir.getName());

            logger.info("[World Height Migration] Successfully created a backup of {}. Continue with height migration.", worldDir.getName());
            logger.info("[World Height Migration] Starting to migrate the chunks of World {} to height offset {}.", worldDir.getName(), offset);

            if (gui != null) { gui.setProgressText(StatCollector.translateToLocal("gui.worldheight.migration.region")); }

            File regionDir = new File(worldDir, "region");

            if(!regionDir.exists() || !regionDir.isDirectory()) {

                throw new IOException("The world region folder does not exist or is not a directory: " + regionDir.getAbsolutePath());
            }

            File[] regionFiles = regionDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {

                    return name.startsWith("r.") && name.endsWith(".mca");
                }
            });

            if(regionFiles == null || regionFiles.length == 0) {

                throw new IOException("Failed to find region files in " +  regionDir.getAbsolutePath());
            }

            logger.info("[World Height Migration] Found {} region files in world {}", regionFiles.length, worldDir.getName());

            if (gui != null) {

                gui.setProgress(0);
                gui.setMaxProgress(regionFiles.length);
            }

            for(int i = 0; i < regionFiles.length; i++) {

                migratedChunks += migrateRegionFile(regionFiles[i], offset);

                if (gui != null) { gui.setProgress(i + 1); }
            }

            logger.info("[World Height Migration] Successfully migrated {} chunks in world {}", migratedChunks, worldDir.getName());

            if (gui != null) { gui.setProgressText(StatCollector.translateToLocal("gui.worldheight.migration.players")); }

            shiftPlayersWithProgress(worldDir, offset, leveldatNBT, gui);

            if (gui != null) {

                gui.setProgress(-1);
                gui.setMaxProgress(0);
                gui.setProgressText(StatCollector.translateToLocal("gui.worldheight.migration.spawnpoint"));
            }

            shiftSpawnPoint(worldDir, offset, leveldatNBT);

            NBTTagCompound data = leveldatNBT.getCompoundTag(Reference.MOD_ID + ".container");

            data.setBoolean(NBTTags.HEIGHT_MIGRATED.toString(), true);
            data.setInteger(NBTTags.HEIGHT_OFFSET.toString(), WorldHeightHandler.getWorldHeightOffset());
            data.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), true);
            data.setInteger(NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());

            CompressedStreamTools.writeCompressed(leveldatNBT, new FileOutputStream(new File(worldDir, "level.dat")));

            if (gui != null) {

                gui.setProgress(-1);
                gui.setMaxProgress(1);
                gui.setProgressText(StatCollector.translateToLocal("gui.worldheight.migration.successful"));
                gui.setSuccessful((byte) 1);
            }

            return true;
        }

        private static int migrateRegionFile(File regionFile, int offset) throws IOException {

            int migratedChunks = 0;

            RegionFile region = new RegionFile(regionFile);

            for(int xChunk = 0; xChunk < 32; xChunk++) {

                for (int zChunk = 0; zChunk < 32; zChunk++) {

                    DataInputStream chunkIn = region.getChunkDataInputStream(xChunk, zChunk);

                    if (chunkIn == null) { continue; }

                    NBTTagCompound regionNBT;

                    regionNBT = CompressedStreamTools.read(chunkIn);

                    chunkIn.close();

                    if (!regionNBT.hasKey("Level")) { continue; }

                    NBTTagCompound level = regionNBT.getCompoundTag("Level");

                    shiftSections(level, offset);
                    shiftTileEntities(level, offset);
                    shiftEntities(level, offset);
                    shiftHeightMap(level, offset);

                    // Mark dirty for creation of a new lightmap
                    level.setBoolean("LightPopulated", true);

                    DataOutputStream chunkOut = region.getChunkDataOutputStream(xChunk, zChunk);
                    CompressedStreamTools.write(regionNBT, chunkOut);
                    chunkOut.close();

                    migratedChunks++;
                }
            }

            region.close();

            logger.info("[World Height Migration] Migrated {} chunks in region file {}.", migratedChunks, regionFile.getName());

            return migratedChunks;
        }

        private static void shiftSections(NBTTagCompound level, int offset) throws IllegalStateException {

            if(!level.hasKey("Sections")) { return; }

            NBTTagList sections = level.getTagList("Sections", 10);

            for(int i = 0; i < sections.tagCount(); i++) {

                NBTTagCompound section = sections.getCompoundTagAt(i);

                if(!section.hasKey("Y")) { continue; }

                int oldSection = section.getByte("Y") & 255;
                int newSection = oldSection + (offset >> 4);

                if(newSection < 0 || newSection > 32) {

                    throw new IllegalStateException("[World Height Migration] Section Y out of range after migration from " + oldSection + " to " + newSection);
                }

                section.setByte("Y", (byte) newSection);
            }
        }

        private static void shiftTileEntities(NBTTagCompound level, int offset) {

            if(!level.hasKey("TileEntities")) { return; }

            NBTTagList tileEntitiesNBT = level.getTagList("TileEntities", 10);

            for(int i = 0; i < tileEntitiesNBT.tagCount(); i++) {

                NBTTagCompound tileEntityNBT = tileEntitiesNBT.getCompoundTagAt(i);

                if(tileEntityNBT.hasKey("y")) {

                    tileEntityNBT.setInteger("y", tileEntityNBT.getInteger("y") + offset);
                }
            }
        }

        private static void shiftEntities(NBTTagCompound level, int offset) {

            if(!level.hasKey("Entities")) { return; }

            NBTTagList entities = level.getTagList("Entities", 10);

            for(int i = 0; i < entities.tagCount(); i++) {

                NBTTagCompound entityNBT = entities.getCompoundTagAt(i);

                if(entityNBT.hasKey("Pos")) {

                    NBTTagList entityPos = entityNBT.getTagList("Pos", 6);

                    if(entityPos.tagCount() >= 3) {

                        double posX = entityPos.func_150309_d(0);
                        double posY = entityPos.func_150309_d(1);
                        double posZ = entityPos.func_150309_d(2);

                        NBTTagList newEntityPos = new NBTTagList();
                        newEntityPos.appendTag(new NBTTagDouble(posX));
                        newEntityPos.appendTag(new NBTTagDouble(posY + offset));
                        newEntityPos.appendTag(new NBTTagDouble(posZ));

                        entityNBT.setTag("Pos", newEntityPos);

                        if(entityNBT.hasKey("TileY")) {

                            int oldTileY = entityNBT.getInteger("TileY");

                            entityNBT.setInteger("TileY", oldTileY + offset);
                        }

                        if(entityNBT.hasKey("SleepingY")) {

                            int oldSleepingY = entityNBT.getInteger("SleepingY");

                            entityNBT.setInteger("SleepingY", oldSleepingY + offset);
                        }
                    }
                }
            }
        }

        private static void shiftHeightMap(NBTTagCompound level, int offset) {

            if(!level.hasKey("HeightMap")) { return; }

            int[] heightMap = level.getIntArray("HeightMap");

            if(heightMap == null || heightMap.length == 0) { return; }

            for(int i = 0; i < heightMap.length; i++) {

                if(heightMap[i] > 0) {

                    heightMap[i] += offset;
                }
            }

            level.setIntArray("HeightMap", heightMap);
        }

        private static void shiftPlayersWithProgress(File worldDir, int offset, NBTTagCompound leveldatNBT, GuiWorldHeightMigrationProgress gui) throws IOException {

            int playersMigrated = 0;

            if (gui != null) { gui.setProgress(0); }

            File playerDir = new File(worldDir, "playerdata");

            if(!playerDir.isDirectory()) { return; }

            File[] playerFiles = playerDir.listFiles();

            if(playerFiles == null) { return; }

            if (gui != null) { gui.setMaxProgress(playerFiles.length + 1); }

            for(int i = 0; i < playerFiles.length; i++) {

                if(!playerFiles[i].getName().endsWith(".dat")) { continue; }

                NBTTagCompound playerNBT;

                try {

                    playerNBT = CompressedStreamTools.readCompressed(new FileInputStream(playerFiles[i]));

                } catch (IOException e){

                    logger.warn("[World Height Migration] There appears to be a problem reading the player file {}.", playerFiles[i].getAbsolutePath());

                    continue;
                }

                if(playerNBT == null) { continue; }

                if(playerNBT.hasKey("Pos")) {

                    NBTTagList playerPos = playerNBT.getTagList("Pos", 6);

                    if (playerPos.tagCount() >= 3) {

                        NBTTagList newPlayerPos = new NBTTagList();
                        newPlayerPos.appendTag(new NBTTagDouble(playerPos.func_150309_d(0)));
                        newPlayerPos.appendTag(new NBTTagDouble(playerPos.func_150309_d(1) + offset));
                        newPlayerPos.appendTag(new NBTTagDouble(playerPos.func_150309_d(2)));

                        playerNBT.setTag("Pos", newPlayerPos);
                    }
                }

                if(playerNBT.hasKey("SpawnY")) {

                    playerNBT.setInteger("SpawnY", playerNBT.getInteger("SpawnY") + offset);
                }

                if(playerNBT.hasKey("Spawns")) {

                    NBTTagList spawnList = playerNBT.getTagList("Spawns", 10);
                    NBTTagList newSpawnList = new NBTTagList();

                    for (int k = 0; k < spawnList.tagCount(); k++) {

                        NBTTagCompound playerSpawn = spawnList.getCompoundTagAt(k);

                        if (playerSpawn.getInteger("Dim") == 0) {

                            playerSpawn.setInteger("SpawnY", playerSpawn.getInteger("SpawnY") + offset);
                        }

                        newSpawnList.appendTag(playerSpawn);
                    }

                    playerNBT.setTag("Spawns", newSpawnList);

                    playersMigrated++;
                }

                CompressedStreamTools.writeCompressed(playerNBT, new FileOutputStream(playerFiles[i]));

                if (gui != null) { gui.setProgress(i + 1); }
            }

            // For old singleplayer player data
            if(leveldatNBT == null || !leveldatNBT.hasKey("Data")) {

                throw new IOException("Could not find data in level.dat from world " + worldDir.getName() + "!");
            }

            NBTTagCompound data = leveldatNBT.getCompoundTag("Data");

            if(data.hasKey("Player")) {

                NBTTagCompound playerData = data.getCompoundTag("Player");
                NBTTagList playerPos = playerData.getTagList("Pos", 6);

                NBTTagList newPlayerPos = new NBTTagList();
                newPlayerPos.appendTag(new NBTTagDouble(playerPos.func_150309_d(0)));
                newPlayerPos.appendTag(new NBTTagDouble(playerPos.func_150309_d(1) + offset));
                newPlayerPos.appendTag(new NBTTagDouble(playerPos.func_150309_d(2)));

                playerData.setTag("Pos", newPlayerPos);

                playersMigrated++;
            }

            logger.info("[World Height Migration] Successfully migrated the height of {} {}.", playersMigrated, playersMigrated > 1 ? "Players" : "Player");
        }

        private static void shiftSpawnPoint(File worldDir, int offset, NBTTagCompound leveldatNBT) throws IOException {

            NBTTagCompound data = leveldatNBT.getCompoundTag("Data");

            if(data.hasKey("SpawnY")) {

                data.setInteger("SpawnY", data.getInteger("SpawnY") + offset);
            }

            logger.info("[World Height Migration] Successfully migrated the spawn point height.");
        }

        /*
        0 = No Migration needed
        1 = Migration upwards
        2 = Migration downwards
        3 = World gets smaller
        4 = Increased World Height is disabled
        */
        public static int isMigrationNeeded(NBTTagCompound etfuturumNBT) {

            if (WorldHeightHandler.isIncreasedWorldHeightEnabled()) {

                if (etfuturumNBT.getBoolean(NBTTags.HEIGHT_NATURALLY.toString())) { return 0; }

                if (etfuturumNBT.getBoolean(NBTTags.HEIGHT_MIGRATED.toString())) { return 0; }

                if (etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString()) > WorldHeightHandler.getMaxWorldHeight()) {

                    return 3;
                }

                if (WorldHeightHandler.getWorldHeightOffset() > 0) {

                    return 1;

                } else if (WorldHeightHandler.getWorldHeightOffset() < 0) {

                    return 2;

                } else {

                    return 0;
                }

            } else {

                if (etfuturumNBT.getBoolean(NBTTags.HEIGHT_ENABLED.toString())) {

                    return 4;

                } else {

                    return 0;
                }
            }
        }
    }

    public enum NBTTags {

        HEIGHT_ENABLED("IncreasedHeightEnabled"),
        HEIGHT_NATURALLY("IncreasedHeightNaturally"),
        MOD_VERSION("EtFuturumVersion"),
        MAX_HEIGHT("MaxWorldHeight"),
        HEIGHT_MIGRATED("IncreasedHeightMigrated"),
        HEIGHT_OFFSET("HeightOffset");

        final String nbtTag;

        NBTTags(String nbtTag) {

            this.nbtTag = nbtTag;
        }

        @Override
        public String toString() {

            return nbtTag;
        }
    }
}
