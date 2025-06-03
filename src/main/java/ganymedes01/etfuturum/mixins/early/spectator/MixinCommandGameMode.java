package ganymedes01.etfuturum.mixins.early.spectator;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.command.CommandGameMode;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(CommandGameMode.class)
public class MixinCommandGameMode {
    @Inject(method = "getGameModeFromCommand", at = @At("HEAD"), cancellable = true)
    private void supportSpectator(ICommandSender sender, String arg, CallbackInfoReturnable<WorldSettings.GameType> cir) {
        if (arg.equalsIgnoreCase("sp") || arg.equalsIgnoreCase("spectator")) {
            cir.setReturnValue(SpectatorMode.SPECTATOR_GAMETYPE);
        }
    }

    @ModifyArg(method = "addTabCompletionOptions", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/command/CommandGameMode;getListOfStringsMatchingLastWord([Ljava/lang/String;[Ljava/lang/String;)Ljava/util/List;",
            ordinal = 0), index = 1)
    private String[] addSpectatorToTabCompletion(String[] par1) {
        var ret = new String[par1.length + 1];
        System.arraycopy(par1, 0, ret, 0, par1.length);
        ret[par1.length] = "spectator";

        return ret;
    }
}
