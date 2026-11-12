package ganymedes01.etfuturum.mixins.early.moderntrading.client;

import com.mojang.authlib.GameProfile;
import ganymedes01.etfuturum.client.gui.inventory.GuiModernMerchant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.IMerchant;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {


    public MixinEntityPlayerSP(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }

    @Inject(method = "displayGUIMerchant", at = @At("HEAD"), cancellable = true)
    private void etfuturum$openModernTradingGui(IMerchant merchant, String title, CallbackInfo ci) {
        ci.cancel();
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(new GuiModernMerchant(this.inventory, merchant, this.worldObj, title));
    }
}
