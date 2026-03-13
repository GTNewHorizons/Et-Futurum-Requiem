package ganymedes01.etfuturum.dispenser;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ganymedes01.etfuturum.entities.EntityTippedArrow;

public class DispenserBehaviourTippedArrow extends BehaviorDefaultDispenseItem {

    @Override
    public ItemStack dispenseStack(IBlockSource block, final ItemStack stack) {
        return new BehaviorProjectileDispense() {

            @Override
            protected IProjectile getProjectileEntity(World world, IPosition pos) {
                EntityTippedArrow entity = new EntityTippedArrow(world, pos.getX(), pos.getY(), pos.getZ());
                entity.canBePickedUp = 1;
                entity.setArrow(stack);
                return entity;
            }
        }.dispense(block, stack);
    }

    @Override
    protected void playDispenseSound(IBlockSource block) {
        block.getWorld()
            .playAuxSFX(1002, block.getXInt(), block.getYInt(), block.getZInt(), 0);
    }
}
