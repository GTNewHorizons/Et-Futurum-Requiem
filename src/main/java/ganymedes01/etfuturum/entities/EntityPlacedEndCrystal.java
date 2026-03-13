package ganymedes01.etfuturum.entities;

import java.util.List;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityPlacedEndCrystal extends EntityEnderCrystal {

    private int blockX, blockY, blockZ;
    private int timer = 20 + rand.nextInt(20);

    public EntityPlacedEndCrystal(World world) {
        super(world);
    }

    public void setBlockPos(int x, int y, int z) {
        blockX = x;
        blockY = y;
        blockZ = z;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        innerRotation++;
        dataWatcher.updateObject(8, Integer.valueOf(health));

        if (worldObj.isRemote || isDead) return;

        if (worldObj.getWorldTime() % timer == 0 && isOnSuitablePlace()) {
            timer = 20 + rand.nextInt(20);
            List<? extends EntityPlacedEndCrystal> crystals = worldObj.getEntitiesWithinAABB(
                getClass(),
                AxisAlignedBB.getBoundingBox(blockX - 6, blockY, blockZ - 6, blockX + 6, blockY + 1, blockZ + 6));
            if (crystals.size() == 4) {
                for (EntityPlacedEndCrystal crystal : crystals)
                    if (crystal.isDead || !crystal.isOnSuitablePlace()) return;

                for (EntityPlacedEndCrystal crystal : crystals) crystal.setDead();
                setDead();
                EntityRespawnedDragon dragon = new EntityRespawnedDragon(worldObj);
                dragon.setPosition(posX, posY + 30, posZ);
                worldObj.spawnEntityInWorld(dragon);
            }
        }
    }

    private boolean isOnSuitablePlace() {
        if (worldObj.provider.dimensionId == 1 && worldObj.getBlock(blockX, blockY, blockZ) == Blocks.bedrock) {
            int portalCount = 0;
            for (int i = -6; i <= 6; i++) for (int j = -6; j <= 6; j++)
                if (worldObj.getBlock(blockX + i, blockY, blockZ + j) == Blocks.end_portal) portalCount++;

            return portalCount == 20;
        }

        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("BlockX", blockX);
        nbt.setInteger("BlockY", blockY);
        nbt.setInteger("BlockZ", blockZ);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        blockX = nbt.getInteger("BlockX");
        blockY = nbt.getInteger("BlockY");
        blockZ = nbt.getInteger("BlockZ");
    }
}
