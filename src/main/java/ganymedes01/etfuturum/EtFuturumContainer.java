package ganymedes01.etfuturum;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.*;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.world.WorldHeightHandler;
import ganymedes01.etfuturum.world.WorldHeightHandler.NBTTags;
import ganymedes01.etfuturum.world.WorldHeightHandler.WorldHeightMigrator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EtFuturumContainer extends InjectedModContainer implements WorldAccessContainer {

    // This is needed because for whatever reason minecraft creates a lot of different WorldInfo objects so worldinfo.getAdditionalProperty returns null in getDataForWriting()
    // Strangely enough this happens only on servers, not in singleplayer
    private NBTTagCompound etfuturumNBT = new NBTTagCompound();

    public EtFuturumContainer() {

        super(new DummyModContainer(getDummyMetadata()), null);
    }

    private static ModMetadata getDummyMetadata() {

        ModMetadata metadata = new ModMetadata();

        metadata.modId = Reference.MOD_CONTAINER_ID;
        metadata.name = Reference.MOD_CONTAINER_NAME;
        metadata.version = Reference.VERSION_NUMBER;

        return metadata;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {

        bus.register(this);

        return true;
    }

    //This gets triggered at world saving to write nbt tags into level.dat
    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info) {

        // Hopefully this a valid indicator for a newly generated world
        if (!etfuturumNBT.hasKey(NBTTags.MOD_VERSION.toString()) && WorldHeightHandler.isIncreasedWorldHeightEnabled()) {

            etfuturumNBT.setString(NBTTags.MOD_VERSION.toString(), Tags.VERSION);
            etfuturumNBT.setBoolean(WorldHeightHandler.NBTTags.HEIGHT_ENABLED.toString(), true);
            etfuturumNBT.setBoolean(WorldHeightHandler.NBTTags.HEIGHT_NATURALLY.toString(), true);
            etfuturumNBT.setInteger(WorldHeightHandler.NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());
        }

        return etfuturumNBT == null ? new NBTTagCompound() : (NBTTagCompound) etfuturumNBT.copy();
    }

    //This gets triggered at world load to read the level.dat nbt tags
    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound nbt) {

        // Sets the variable to the nbt from current world
        etfuturumNBT = nbt;

        if (!etfuturumNBT.getString(NBTTags.MOD_VERSION.toString()).equals(Tags.VERSION)) {

            etfuturumNBT.setString(NBTTags.MOD_VERSION.toString(), Tags.VERSION);
        }

        int migrationFlag = WorldHeightMigrator.isMigrationNeeded(etfuturumNBT);
        String confirmText;
        boolean confirmMigration;

        switch (migrationFlag) {
            case 1:
                confirmText = String.format("""
                        ######################################################################################################################################################
                        [World Height Migration] You have %s installed and enabled the Increase World Height feature with an height offset %s set for existing worlds.
                        [World Height Migration] That means your complete world will be shifted up by this offset.
                        [World Height Migration] This can serve various purposes i.e. its necessary to use the deep dark caves feature.
                        [World Height Migration] This feature is experimental so please make a backup of your world (we will also make one for you but better have it twice).
                        [World Height Migration] Are you sure that you want to migrate the world?
                        ######################################################################################################################################################""", Reference.MOD_NAME, WorldHeightHandler.getWorldHeightOffset());
                confirmMigration = StartupQuery.confirm(confirmText);
                if (!confirmMigration) { StartupQuery.abort(); }
                migrateWorld(handler.getWorldDirectory(), etfuturumNBT, info);
                break;
            case 2:
                confirmText = String.format("""
                        ######################################################################################################################################################
                        [World Height Migration] You have %s installed and enabled the Increase World Height feature with an height offset %s set for existing worlds.
                        [World Height Migration] That means your complete world will be completely shifted down by this offset.
                        [World Height Migration] Keep in mind that every block below Y:%s gets destroyed.
                        [World Height Migration] This feature is experimental so please make a backup of your world (we will also make one for you but better have it twice).
                        ######################################################################################################################################################""", Reference.MOD_NAME, WorldHeightHandler.getWorldHeightOffset(), Math.abs(WorldHeightHandler.getWorldHeightOffset()));
                confirmMigration = StartupQuery.confirm(confirmText);
                if (!confirmMigration) { StartupQuery.abort(); }
                migrateWorld(handler.getWorldDirectory(), etfuturumNBT, info);
                break;
            case 3:
                confirmText = String.format("""
                        ############################################################################################################################################################
                        [World Height Migration] You have %s installed and enabled the Increase World Height feature with an max world height set to %s.
                        [World Height Migration] But you want to load a world with max world height of %s.
                        [World Height Migration] Everything above Y:%s gets destroyed. Please make a backup of your world (we will also make one for you but better have it twice).
                        [World Height Migration] Are you sure that you want to migrate the world?
                        ############################################################################################################################################################""", Reference.MOD_NAME, WorldHeightHandler.getMaxWorldHeight(), etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString()), etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString()));
                confirmMigration = StartupQuery.confirm(confirmText);
                if (!confirmMigration) { StartupQuery.abort(); }
                etfuturumNBT.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), WorldHeightHandler.isIncreasedWorldHeightEnabled());
                etfuturumNBT.setInteger(NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());
                break;
            case 4:
                confirmText = String.format("""
                        ##################################################################################################################################
                        [World Height Migration] You have disabled the Increase World Height feature but want to load a world
                        [World Height Migration] with Increased World Height enabled and a max world height of %s. Everything above Y:256 gets destroyed.
                        [World Height Migration] Please make a backup of your world (we will also make one for you but better have it twice).
                        ##################################################################################################################################""", etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString()));
                confirmMigration = StartupQuery.confirm(confirmText);
                if(!confirmMigration) { StartupQuery.abort(); }
                etfuturumNBT.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), false);
                etfuturumNBT.setInteger(NBTTags.MAX_HEIGHT.toString(), 256);
                etfuturumNBT.setBoolean(NBTTags.HEIGHT_MIGRATED.toString(), false);
                etfuturumNBT.setInteger(NBTTags.HEIGHT_OFFSET.toString(), 0);
            default:
                etfuturumNBT.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), WorldHeightHandler.isIncreasedWorldHeightEnabled());
                etfuturumNBT.setInteger(NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());
                break;
        }
    }

    private static void migrateWorld(File worldDir, NBTTagCompound etfuturumNBT, WorldInfo info) {

        try {

            WorldHeightMigrator.migrateWorldHeight(worldDir);

            etfuturumNBT.setBoolean(WorldHeightHandler.NBTTags.HEIGHT_ENABLED.toString(), true);
            etfuturumNBT.setInteger(WorldHeightHandler.NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());
            etfuturumNBT.setBoolean(WorldHeightHandler.NBTTags.HEIGHT_MIGRATED.toString(), true);
            etfuturumNBT.setInteger(WorldHeightHandler.NBTTags.HEIGHT_OFFSET.toString(), WorldHeightHandler.getWorldHeightOffset());

            info.setSpawnPosition(info.getSpawnX(), info.getSpawnY() + WorldHeightHandler.getWorldHeightOffset(), info.getSpawnZ());

        } catch (IOException e) {

            StartupQuery.notify("[World Height Migration] The migration is failed! World load gets aborted.");
            StartupQuery.abort();
        }
    }
}
