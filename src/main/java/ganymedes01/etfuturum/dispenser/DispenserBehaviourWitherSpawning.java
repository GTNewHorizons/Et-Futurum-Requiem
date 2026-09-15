package ganymedes01.etfuturum.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSkull;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import static ganymedes01.etfuturum.wither.WitherSpawnHelper.checkWitherPattern;
import static ganymedes01.etfuturum.wither.WitherSpawnHelper.checkWitherSpawnPotential;

public class DispenserBehaviourWitherSpawning extends BehaviorDefaultDispenseItem {
    @Override
    protected ItemStack dispenseStack(IBlockSource coords, ItemStack stack) {
        if (stack.getItemDamage() != 1) {
            return super.dispenseStack(coords, stack);
        }

        World world = coords.getWorld();
        EnumFacing facing = BlockDispenser.func_149937_b(coords.getBlockMetadata());
        int x = coords.getXInt() + facing.getFrontOffsetX();
        int y = coords.getYInt() + facing.getFrontOffsetY();
        int z = coords.getZInt() + facing.getFrontOffsetZ();

        if (!world.isAirBlock(x, y, z)
                || !checkWitherSpawnPotential(world, x, y, z)) {
            return super.dispenseStack(coords, stack);
        }

        world.setBlock(x, y, z, Blocks.skull, 0, 2);
        boolean flag = false;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntitySkull) {
            flag = true;
            TileEntitySkull skull = (TileEntitySkull) te;
            // wither skeleton skull
            skull.func_152107_a(1);
            // rotate
            skull.func_145903_a(facing.ordinal());
            // try to spawn the wither
            ((BlockSkull) Blocks.skull).func_149965_a(world, x, y, z, skull);
        }

        for (int i = x; i < x + 3; i++) {
            for (int j = y; j < y + 3; j++) {
                for (int k = z; k < z + 3; k++) {
                    if ((i == x && j == y && k == z) && world.getBlock(i, j, k) != Blocks.skull) {
                        continue;
                    }
                    if (world.getTileEntity(i, j, k) instanceof TileEntitySkull) {
                        TileEntitySkull skull = (TileEntitySkull) world.getTileEntity(i, j, k);
                        if (skull.func_145904_a() == 1) {
                            flag |= checkWitherPattern(world, i, j, k, skull);
                        }
                    }
                }
            }
        }

        if (!flag) {
            world.setBlockToAir(x, y, z);
            return super.dispenseStack(coords, stack);
        }

        stack.splitStack(1);
        return stack;
    }
}