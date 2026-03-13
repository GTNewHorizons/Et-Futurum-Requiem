package ganymedes01.etfuturum.entities;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockPointedDripstone;

public class EntityFallingDripstone extends EntityFallingBlock {

    private int fallHurtMax;
    private float fallHurtAmount;
    private boolean hurtEntities;

    public EntityFallingDripstone(World p_i45318_1_) {
        super(p_i45318_1_);
        this.fallHurtMax = 40;
        this.fallHurtAmount = 4.0F;
        hurtEntities = true;
    }

    public EntityFallingDripstone(World p_i45318_1_, double p_i45318_2_, double p_i45318_4_, double p_i45318_6_) {
        super(p_i45318_1_, p_i45318_2_, p_i45318_4_, p_i45318_6_, ModBlocks.POINTED_DRIPSTONE.get());
        this.fallHurtMax = 40;
        this.fallHurtAmount = 4.0F;
        hurtEntities = true;
    }

    @Override
    public void onUpdate() {

        Block block = ModBlocks.POINTED_DRIPSTONE.get();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.field_145812_b; // fallTime
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posY);
        int k = MathHelper.floor_double(this.posZ);
        if (this.field_145812_b == 1) { // fallTime
            field_145814_a/* metadata */ = worldObj.getBlockMetadata(i, j, k);
        }

        if (!this.worldObj.isRemote) {

            if (this.field_145812_b == 1) { // fallTime
                if (this.worldObj.getBlock(i, j, k) != block) {
                    this.setDead();
                    return;
                }

                this.worldObj.setBlockToAir(i, j, k);
            }

            if (this.onGround && worldObj.getBlock(i, j - 1, k) != block) {
                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;
                this.motionY *= -0.5D;
                this.setDead();

                if (this.field_145813_c) { // shouldDropItem
                    this.entityDropItem(
                        new ItemStack(block, 1, block.damageDropped(this.field_145814_a/* metadata */)),
                        0.0F);
                }
            } else if (this.field_145812_b/* fallTime */ > 100 && !this.worldObj.isRemote && (j < 1 || j > 256)
                || this.field_145812_b/* fallTime */ > 600) {
                    if (this.field_145813_c) { // shouldDropItem
                        this.entityDropItem(
                            new ItemStack(block, 1, block.damageDropped(this.field_145814_a/* metadata */)),
                            0.0F);
                    }

                    this.setDead();
                }
        }
    }

    @Override
    protected void fall(float distance) {
        if (this.hurtEntities) {
            int i = MathHelper.ceiling_float_int(distance - 1.0F);

            if (i > 0) {
                ArrayList<Entity> arraylist = new ArrayList<>(
                    this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
                DamageSource damagesource = BlockPointedDripstone.STALACTITE_DAMAGE;
                Iterator<Entity> iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    Entity entity = iterator.next();
                    entity.attackEntityFrom(
                        damagesource,
                        (float) Math.min(MathHelper.floor_float((float) i * this.fallHurtAmount), this.fallHurtMax));
                }
            }
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("HurtEntities", this.hurtEntities);
        tagCompound.setFloat("FallHurtAmount", this.fallHurtAmount);
        tagCompound.setInteger("FallHurtMax", this.fallHurtMax);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.hurtEntities = tagCompund.getBoolean("HurtEntities");
        this.fallHurtAmount = tagCompund.getFloat("FallHurtAmount");
        this.fallHurtMax = tagCompund.getInteger("FallHurtMax");
    }
}
