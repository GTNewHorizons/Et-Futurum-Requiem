package ganymedes01.etfuturum.command;

import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;
import ganymedes01.etfuturum.world.generate.structure.WorldGenRuinedPortal;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandLocate extends CommandBase {

    // Max search radius in chunks (~3200 blocks)
    private static final int MAX_SEARCH_RADIUS = 200;

    private static final String LANG_PREFIX = "commands.etfuturum.locate.";

    private static IChatComponent message(EnumChatFormatting color, String key, Object... args) {
        IChatComponent component = new ChatComponentTranslation(LANG_PREFIX + key, args);
        component.getChatStyle().setColor(color);
        return component;
    }

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
        return LANG_PREFIX + "usage";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "shipwreck", "ruined_portal");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String target = args[0].toLowerCase();
        boolean portal = target.equals("ruined_portal");
        if (!portal && !target.equals("shipwreck")) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String id = portal ? "ruined_portal" : "shipwreck";

        if (!(portal ? ConfigWorld.enableRuinedPortals : ConfigWorld.enableShipwrecks)) {
            sender.addChatMessage(message(EnumChatFormatting.RED, id + ".disabled"));
            return;
        }

        World world = sender.getEntityWorld();
        if (world.provider.dimensionId != 0) {
            sender.addChatMessage(message(EnumChatFormatting.RED, id + ".overworldOnly"));
            return;
        }

        int playerX = (int) sender.getPlayerCoordinates().posX;
        int playerZ = (int) sender.getPlayerCoordinates().posZ;
        int centerChunkX = playerX >> 4;
        int centerChunkZ = playerZ >> 4;

        sender.addChatMessage(message(EnumChatFormatting.YELLOW, id + ".searching"));

        List<BiomeGenBase> validBiomes = Arrays.asList(Utils.excludeBiomesFromTypes(
            BiomeDictionary.getBiomesForType(BiomeDictionary.Type.OCEAN),
            BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END, BiomeDictionary.Type.RIVER
        ));

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

                    Random r = portal ? EtFuturumWorldGenerator.getRuinedPortalRandom(worldSeed, chunkX, chunkZ)
                        : EtFuturumWorldGenerator.getShipwreckRandom(worldSeed, chunkX, chunkZ);
                    if (r.nextInt(portal ? ConfigWorld.ruinedPortalRarity : ConfigWorld.shipwreckRarity) != 0) continue;

                    int candidateX = (chunkX << 4) + r.nextInt(16) + 8;
                    int candidateZ = (chunkZ << 4) + r.nextInt(16) + 8;

                    BiomeGenBase biome = world.getBiomeGenForCoords(candidateX, candidateZ);
                    if (portal ? !WorldGenRuinedPortal.canGenerateIn(biome) : !validBiomes.contains(biome)) continue;

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
            sender.addChatMessage(message(EnumChatFormatting.RED, id + ".notFound", MAX_SEARCH_RADIUS * 16));
        } else {
            int distance = (int) bestDist;
            String teleport = "/tp " + foundX + " ~ " + foundZ;
            IChatComponent coords = new ChatComponentText(foundX + ", ~, " + foundZ);
            coords.getChatStyle()
                .setColor(EnumChatFormatting.WHITE)
                .setUnderlined(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, teleport))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentTranslation(LANG_PREFIX + "click")));
            IChatComponent found = message(EnumChatFormatting.GREEN, id + ".found", coords);
            found.appendSibling(message(EnumChatFormatting.GRAY, "distance", distance));
            sender.addChatMessage(found);
            if (sender instanceof EntityPlayer) {
                IChatComponent command = new ChatComponentText(teleport);
                command.getChatStyle().setColor(EnumChatFormatting.YELLOW);
                sender.addChatMessage(message(EnumChatFormatting.GRAY, "tip", command));
            }
        }
    }
}
