package ganymedes01.etfuturum.mixins.early.backlytra;

import net.minecraft.entity.EntityTrackerEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ganymedes01.etfuturum.elytra.IElytraEntityTrackerEntry;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry implements IElytraEntityTrackerEntry {

    @Unique
    private boolean etfu$wasSendingVelUpdates;

    @Override
    public boolean etfu$getWasSendingVelUpdates() {
        return etfu$wasSendingVelUpdates;
    }

    @Override
    public void etfu$setWasSendingVelUpdates(boolean b) {
        etfu$wasSendingVelUpdates = b;
    }
}
