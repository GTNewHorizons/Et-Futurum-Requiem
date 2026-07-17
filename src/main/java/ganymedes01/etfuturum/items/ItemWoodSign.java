package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemWoodSign extends Item {

	private final BlockWoodSign sign;

	public ItemWoodSign(BlockWoodSign sign) {
		this.sign = sign;
		setUnlocalizedName(Utils.getUnlocalisedName(sign.type + "_sign"));
		setTextureName(sign.type + "_sign");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		this.maxStackSize = Items.sign.getItemStackLimit(new ItemStack(Items.sign));
		System.out.println("maxStackSize: " + this.maxStackSize);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName().replaceFirst("item", "tile");
	}

	/**
	 * Used by these legacy sign items to indicate to the creative tab what sign block they represent.
	 * This is so they can be sorted by block ID instead of item ID (not always on the bottom)
	 *
	 * @return
	 */
	public BlockWoodSign getSignBlock() {
		return sign;
	}
}
