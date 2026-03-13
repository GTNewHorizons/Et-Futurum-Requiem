package ganymedes01.etfuturum.entities;

import java.util.ArrayList;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.items.ItemSuspiciousStew;

public class EntityBrownMooshroom extends EntityMooshroom {

    private byte effectID;
    private int effectDuration;

    public EntityBrownMooshroom(World p_i1687_1_) {
        super(p_i1687_1_);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();

        // boolean flag = false; // unused variable

        if (EtFuturum.getSuspiciousStewEffect(itemstack) != null && effectID <= 0) {
            effectID = (byte) EtFuturum.getSuspiciousStewEffect(itemstack)
                .getPotionID();
            effectDuration = EtFuturum.getSuspiciousStewEffect(itemstack)
                .getDuration();
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            for (int i = 0; i < 4; i++) {
                this.worldObj.spawnParticle(
                    "mobSpell",
                    this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
                    this.posY + this.rand.nextDouble() * this.height - this.yOffset,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * this.width,
                    1,
                    1,
                    1);
            }
            return true;
        }
        if (effectID > 0 && itemstack != null && itemstack.getItem() == Items.bowl && this.getGrowingAge() >= 0) {
            ItemStack stew = ModItems.SUSPICIOUS_STEW.newItemStack(1, 0);
            stew.stackTagCompound = new NBTTagCompound();
            NBTTagList effectsList = new NBTTagList();
            stew.stackTagCompound.setTag(ItemSuspiciousStew.effectsList, effectsList);
            NBTTagCompound potionEffect = new NBTTagCompound();
            potionEffect.setByte(ItemSuspiciousStew.stewEffect, effectID);
            if (effectDuration <= 0) effectDuration = 160;
            potionEffect.setInteger(ItemSuspiciousStew.stewEffectDuration, effectDuration);
            effectsList.appendTag(potionEffect);

            if (itemstack.stackSize == 1) {
                clearEffects();
                player.inventory.setInventorySlotContents(player.inventory.currentItem, stew);
                return true;
            }

            if (player.inventory.addItemStackToInventory(stew) && !player.capabilities.isCreativeMode) {
                clearEffects();
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
                return true;
            }

            player.inventoryContainer.detectAndSendChanges();
        }
        ItemStack stack = player.getCurrentEquippedItem();
        if (effectID > 0) {
            player.destroyCurrentEquippedItem();
        }
        boolean flag2 = super.interact(player);
        if (effectID > 0) {
            player.inventory.mainInventory[player.inventory.currentItem] = stack;
        }
        return flag2;
    }

    private void clearEffects() {
        effectID = 0;
        effectDuration = 0;
        getEntityData().setByte("EffectId", (byte) 0);
        getEntityData().setInteger("EffectDuration", 0);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
    }

    @Override
    public EntityMooshroom createChild(EntityAgeable p_90011_1_) {
        return new EntityBrownMooshroom(this.worldObj);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        getEntityData().setByte("EffectId", this.effectID);
        getEntityData().setInteger("EffectDuration", this.effectDuration);
        super.writeEntityToNBT(tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.effectID = getEntityData().getByte("EffectId");
        this.effectDuration = getEntityData().getInteger("EffectDuration");
        super.readEntityFromNBT(tagCompund);
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        setDead();
        EntityCow entitycow = new EntityCow(worldObj);
        entitycow.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
        entitycow.setHealth(this.getHealth());
        entitycow.renderYawOffset = renderYawOffset;
        worldObj.spawnEntityInWorld(entitycow);
        worldObj.spawnParticle("largeexplode", posX, posY + height / 2.0F, posZ, 0.0D, 0.0D, 0.0D);

        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (int i = 0; i < 5; i++) {
            ret.add(new ItemStack(Blocks.brown_mushroom));
        }
        playSound("mob.sheep.shear", 1.0F, 1.0F);
        return ret;
    }
}
