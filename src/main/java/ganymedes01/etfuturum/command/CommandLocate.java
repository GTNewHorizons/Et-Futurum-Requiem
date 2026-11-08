package ganymedes01.etfuturum.command;

import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandLocate extends CommandBase {

    // Max search radius in chunks (~3200 blocks)
    private static final int MAX_SEARCH_RADIUS = 200;

    @Override
    public String getCommandName() {
        return "locate";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/locate shipwreck";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "shipwreck");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String target = args[0].toLowerCase();
        if (!target.equals("shipwreck")) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (!ConfigWorld.enableShipwrecks) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Shipwrecks are disabled in the config."
            ));
            return;
        }

        World world = sender.getEntityWorld();
        if (world.provider.dimensionId != 0) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Shipwrecks only generate in the overworld."
            ));
            return;
        }

        int playerX = (int) sender.getPlayerCoordinates().posX;
        int playerZ = (int) sender.getPlayerCoordinates().posZ;
        int centerChunkX = playerX >> 4;
        int centerChunkZ = playerZ >> 4;

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "Searching for the nearest shipwreck..."
        ));

        List<BiomeGenBase> validBiomes = Arrays.asList(
            BiomeDictionary.getBiomesForType(BiomeDictionary.Type.OCEAN)
        );
        // Also add rivers
        for (BiomeGenBase b : BiomeDictionary.getBiomesForType(BiomeDictionary.Type.RIVER)) {
            if (!validBiomes.contains(b)) {
                validBiomes = new java.util.ArrayList<>(validBiomes);
                validBiomes.add(b);
            }
        }

        long worldSeed = world.getSeed();
        int foundX = Integer.MIN_VALUE;
        int foundZ = Integer.MIN_VALUE;
        double bestDist = Double.MAX_VALUE;

        // Spiral search outward from player chunk
        outer:
        for (int radius = 0; radius <= MAX_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue; // only check ring
                    int chunkX = centerChunkX + dx;
                    int chunkZ = centerChunkZ + dz;

                    Random r = EtFuturumWorldGenerator.getShipwreckRandom(worldSeed, chunkX, chunkZ);
                    if (r.nextInt(ConfigWorld.shipwreckRarity) != 0) continue;

                    int candidateX = (chunkX << 4) + r.nextInt(16) + 8;
                    int candidateZ = (chunkZ << 4) + r.nextInt(16) + 8;

                    BiomeGenBase biome = world.getBiomeGenForCoords(candidateX, candidateZ);
                    if (!validBiomes.contains(biome)) continue;

                    double dist = Math.sqrt((double)(candidateX - playerX) * (candidateX - playerX)
                                         + (double)(candidateZ - playerZ) * (candidateZ - playerZ));
                    if (dist < bestDist) {
                        bestDist = dist;
                        foundX = candidateX;
                        foundZ = candidateZ;
                    }
                    // Once we've found something on this ring, keep checking the ring then stop
                    if (radius > 0 && bestDist < Double.MAX_VALUE) break outer;
                }
            }
            if (bestDist < Double.MAX_VALUE) break;
        }

        if (foundX == Integer.MIN_VALUE) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "No shipwreck found within " + (MAX_SEARCH_RADIUS * 16) + " blocks."
            ));
        } else {
            int distance = (int) bestDist;
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GREEN + "Nearest Shipwreck: "
                + EnumChatFormatting.WHITE + foundX + ", ~, " + foundZ
                + EnumChatFormatting.GRAY + " (~" + distance + " blocks away)"
            ));
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).addChatMessage(new ChatComponentText(
                    EnumChatFormatting.GRAY + "Tip: use "
                    + EnumChatFormatting.YELLOW + "/tp " + foundX + " ~ " + foundZ
                    + EnumChatFormatting.GRAY + " to teleport there."
                ));
            }
        }
    }
}
