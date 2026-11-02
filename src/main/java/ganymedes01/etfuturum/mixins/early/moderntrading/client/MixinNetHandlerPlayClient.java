package ganymedes01.etfuturum.mixins.early.moderntrading.client;

import ganymedes01.etfuturum.client.gui.inventory.GuiModernMerchant;
import ganymedes01.etfuturum.core.utils.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.IMerchant;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow
    private Minecraft gameController;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void etfuturum$receiveTradeList(S3FPacketCustomPayload packetIn, CallbackInfo ci) {
        if ("MC|TrList".equals(packetIn.func_149169_c()) && this.gameController.currentScreen instanceof GuiModernMerchant modernMerchantScreen) {
            ByteBuf bytebuf = Unpooled.wrappedBuffer(packetIn.func_149168_d());

            try {
                int windowId = bytebuf.readInt();

                if (windowId == this.gameController.thePlayer.openContainer.windowId) {
                    IMerchant merchant = modernMerchantScreen.getMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.func_151390_b(new PacketBuffer(bytebuf));
                    merchant.setRecipes(merchantrecipelist);
                }
            } catch (IOException ioexception) {
                Logger.error(ioexception);
            } finally {
                bytebuf.release();
            }

            ci.cancel();
        }

    }

}
