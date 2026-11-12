package ganymedes01.etfuturum.command;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandLocate extends CommandBase {

    // Max search radius in chunks (~3200 blocks)
    private static final int MAX_SEARCH_RADIUS = 200;

    private static final String LANG_PREFIX = "commands.etfuturum.locate.";

    private static final Map<String, LocateTarget> TARGETS = new LinkedHashMap<String, LocateTarget>();

    /** Makes /locate accept the target's id as an argument. */
    public static void register(LocateTarget target) {
        TARGETS.put(target.getId(), target);
    }

    /** Returns true if at least one target is registered, so the command is worth adding. */
    public static boolean hasTargets() {
        return !TARGETS.isEmpty();
    }

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
            return getListOfStringsMatchingLastWord(args, TARGETS.keySet().toArray(new String[0]));
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        LocateTarget target = TARGETS.get(args[0].toLowerCase());
        if (target == null) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String id = target.getId();

        if (!target.isEnabled()) {
            sender.addChatMessage(message(EnumChatFormatting.RED, id + ".disabled"));
            return;
        }

        World world = sender.getEntityWorld();
        if (!target.isValidWorld(world)) {
            sender.addChatMessage(message(EnumChatFormatting.RED, id + ".overworldOnly"));
            return;
        }

        int playerX = (int) sender.getPlayerCoordinates().posX;
        int playerZ = (int) sender.getPlayerCoordinates().posZ;
        int centerChunkX = playerX >> 4;
        int centerChunkZ = playerZ >> 4;

        sender.addChatMessage(message(EnumChatFormatting.YELLOW, id + ".searching"));

        int foundX = Integer.MIN_VALUE;
        int foundZ = Integer.MIN_VALUE;
        double bestDistSq = Double.MAX_VALUE;

        // Spiral search outward from player chunk
        outer:
        for (int radius = 0; radius <= MAX_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue; // only check ring
                    int chunkX = centerChunkX + dx;
                    int chunkZ = centerChunkZ + dz;

                    int[] pos = target.findInChunk(world, chunkX, chunkZ);
                    if (pos == null) continue;

                    int candidateX = pos[0];
                    int candidateZ = pos[1];

                    double distSq = (double)(candidateX - playerX) * (candidateX - playerX)
                                  + (double)(candidateZ - playerZ) * (candidateZ - playerZ);
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        foundX = candidateX;
                        foundZ = candidateZ;
                    }
                    // Once we've found something on this ring, keep checking the ring then stop
                    if (radius > 0 && bestDistSq < Double.MAX_VALUE) break outer;
                }
            }
            if (bestDistSq < Double.MAX_VALUE) break;
        }

        if (foundX == Integer.MIN_VALUE) {
            sender.addChatMessage(message(EnumChatFormatting.RED, id + ".notFound", MAX_SEARCH_RADIUS * 16));
        } else {
            int distance = (int) Math.sqrt(bestDistSq);
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
