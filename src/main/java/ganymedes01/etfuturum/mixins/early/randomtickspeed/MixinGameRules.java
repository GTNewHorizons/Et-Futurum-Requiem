package ganymedes01.etfuturum.mixins.early.randomtickspeed;

import net.minecraft.world.GameRules;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.gamerule.RandomTickSpeed;

@Mixin(GameRules.class)
public class MixinGameRules {

    @Inject(method = "setOrCreateGameRule", at = @At("RETURN"))
    private void onGameRuleChanged(String ruleName, String value, CallbackInfo ci) {
        // Your code here to handle gamerule changes
        if (ruleName.equals("randomTickSpeed")) {
            RandomTickSpeed.INSTANCE.CURRENT_VALUE = Integer
                .parseInt(StringUtils.defaultIfEmpty(value, RandomTickSpeed.DEFAULT_VALUE));
        }
    }
}
