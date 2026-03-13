package ganymedes01.etfuturum.entities;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;

public class EntityRespawnedDragon extends EntityDragon {

    public EntityRespawnedDragon(World world) {
        super(world);
    }

    @Override
    protected void onDeathUpdate() {
        deathTicks++;

        if (deathTicks >= 180 && deathTicks <= 200) {
            float f = (rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (rand.nextFloat() - 0.5F) * 8.0F;
            worldObj.spawnParticle("hugeexplosion", posX + f, posY + 2.0D + f1, posZ + f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!worldObj.isRemote) {
            if (deathTicks > 150 && deathTicks % 5 == 0) {
                i = 1000;

                while (i > 0) {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, j));
                }
            }

            if (deathTicks == 1) worldObj.playBroadcastSound(1018, (int) posX, (int) posY, (int) posZ, 0);
        }

        moveEntity(0.0D, 0.10000000149011612D, 0.0D);
        renderYawOffset = rotationYaw += 20.0F;

        if (deathTicks == 200 && !worldObj.isRemote) {
            i = 2000;

            while (i > 0) {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, j));
            }

            setDead();
        }
    }
}
