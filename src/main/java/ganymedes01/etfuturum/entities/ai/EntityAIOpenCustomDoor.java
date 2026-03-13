package ganymedes01.etfuturum.entities.ai;

import net.minecraft.entity.EntityLiving;

/*
 * Copy paste from vanilla and adapted to work with other doors
 */
public class EntityAIOpenCustomDoor extends EntityAICustomDoorInteract {

    /**
     * If the entity close the door
     */
    boolean closeDoor;
    /**
     * The temporisation before the entity close the door (in ticks, always 20 = 1 second)
     */
    int closeDoorTemporisation;

    public EntityAIOpenCustomDoor(EntityLiving p_i1644_1_, boolean p_i1644_2_) {
        super(p_i1644_1_);
        theEntity = p_i1644_1_;
        closeDoor = p_i1644_2_;
    }

    @Override
    public boolean continueExecuting() {
        return closeDoor && closeDoorTemporisation > 0 && super.continueExecuting();
    }

    @Override
    public void startExecuting() {
        closeDoorTemporisation = 20;
        doorBlock.func_150014_a(theEntity.worldObj, entityPosX, entityPosY, entityPosZ, true);
    }

    @Override
    public void resetTask() {
        if (closeDoor) doorBlock.func_150014_a(theEntity.worldObj, entityPosX, entityPosY, entityPosZ, false);
    }

    @Override
    public void updateTask() {
        --closeDoorTemporisation;
        super.updateTask();
    }
}
