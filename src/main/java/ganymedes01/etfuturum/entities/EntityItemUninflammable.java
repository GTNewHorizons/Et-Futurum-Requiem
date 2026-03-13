package ganymedes01.etfuturum.entities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

@Deprecated
/**
 * No longer used in favor of a mixin.
 */
public class EntityItemUninflammable extends EntityItem {

    public EntityItemUninflammable(World p_i1711_1_) {
        super(p_i1711_1_);
        this.isImmuneToFire = true;
        this.fireResistance = Integer.MAX_VALUE;
    }

    public EntityItemUninflammable(World worldIn, double d, double e, double f, ItemStack itemIn) {
        super(worldIn, d, e, f, itemIn);
        this.isImmuneToFire = true;
        this.fireResistance = Integer.MAX_VALUE;
    }

    @Override
    protected void dealFireDamage(int amount) {
        // ignore
    }

    @Override
    public void onUpdate() {
        EntityItem item = new EntityItem(worldObj, posX, posY, posZ, getEntityItem());
        NBTTagCompound comp = new NBTTagCompound();
        writeToNBT(comp);
        item.readEntityFromNBT(comp);
        worldObj.onEntityAdded(item);
        setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.inFire || source == DamageSource.onFire || source == DamageSource.lava) return false;

        return super.attackEntityFrom(source, amount);
    }

}
