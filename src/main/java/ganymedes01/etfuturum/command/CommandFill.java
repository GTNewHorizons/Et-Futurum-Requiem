package ganymedes01.etfuturum.command;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandFill extends CommandBase {

    @Override
    public String getCommandName() {
        return "fill";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.etfuturum.fill.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 7) {
            throw new WrongUsageException("commands.etfuturum.fill.usage");
        }
        EntityPlayerMP entityplayermp = getCommandSenderAsPlayer(sender);
        int i = 0;
        int fromX = MathHelper.floor_double(func_110666_a/* clamp_coord */(sender, entityplayermp.posX, args[i++]));
        int fromY = MathHelper
            .floor_double(func_110665_a/* clamp_double */(sender, entityplayermp.posY, args[i++], 0, 0));
        int fromZ = MathHelper.floor_double(func_110666_a/* clamp_coord */(sender, entityplayermp.posZ, args[i++]));
        int toX = MathHelper.floor_double(func_110666_a/* clamp_coord */(sender, entityplayermp.posX, args[i++]));
        int toY = MathHelper
            .floor_double(func_110665_a/* clamp_double */(sender, entityplayermp.posY, args[i++], 0, 0));
        int toZ = MathHelper.floor_double(func_110666_a/* clamp_coord */(sender, entityplayermp.posZ, args[i++]));
        Block block = CommandBase.getBlockByText(sender, args[i++]);
        int meta = 0;
        if (i < args.length) {
            meta = CommandBase.parseIntBounded(sender, args[i++], 0, 15);
        }
        /* TODO: Implement other fill modes */
        if (toX < fromX) {
            int tmp = toX;
            toX = fromX;
            fromX = tmp;
        }
        if (toY < fromY) {
            int tmp = toY;
            toY = fromY;
            fromY = tmp;
        }
        if (toZ < fromZ) {
            int tmp = toZ;
            toZ = fromZ;
            fromZ = tmp;
        }
        World world = sender.getEntityWorld();
        if (!world.checkChunksExist(fromX, fromY, fromZ, toX, toY, toZ)) {
            throw new CommandException("commands.etfuturum.fill.outOfWorld");
        }
        int volume = (toX - fromX + 1) * (toY - fromY + 1) * (toZ - fromZ + 1);
        for (int z = fromZ; z <= toZ; z++) {
            for (int y = fromY; y <= toY; y++) {
                for (int x = fromX; x <= toX; x++) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if (te instanceof IInventory inv) {
                        int size = inv.getSizeInventory();
                        for (int slot = 0; slot < size; slot++) {
                            inv.setInventorySlotContents(slot, null);
                        }
                    }
                    world.setBlock(x, y, z, block, meta, 2);
                }
            }
        }
        func_152373_a/* notifyOperators */(sender, this, "commands.etfuturum.fill.success", volume);
    }
}
