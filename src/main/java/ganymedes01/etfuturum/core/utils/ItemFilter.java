package ganymedes01.etfuturum.core.utils;

import net.minecraft.item.ItemStack;

public interface ItemFilter {
	boolean matches(ItemStack var1);

	interface ItemFilterProvider {
		ItemFilter getFilter();
	}
}
