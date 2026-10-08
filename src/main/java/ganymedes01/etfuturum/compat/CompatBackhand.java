package ganymedes01.etfuturum.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xonin.backhand.api.core.BackhandUtils;

public final class CompatBackhand {

	private CompatBackhand() {
	}

	public static ItemStack getOffhandItem(EntityPlayer player) {
		return BackhandUtils.getOffhandItem(player);
	}

	public static void setOffhandItem(EntityPlayer player, ItemStack stack) {
		BackhandUtils.setPlayerOffhandItem(player, stack);
	}
}
