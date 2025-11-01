package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import net.minecraft.item.Item;

public class BaseItem extends Item {

	public BaseItem() {
		super();
		setCreativeTab(EtFuturum.creativeTabItems);
	}

	public BaseItem(String names) {
		this();
		setNames(names);
	}

	public BaseItem setNames(String name) {
		setUnlocalizedNameWithPrefix(name);
		setTextureName(name);
		return this;
	}


	public BaseItem setUnlocalizedNameWithPrefix(String name) {
		setUnlocalizedName((getNameDomain().isEmpty() ? "" : getNameDomain() + ".") + name);
		return this;
	}

	@Override
	public Item setTextureName(String name) {
		return super.setTextureName((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":")
				+ (getTextureSubfolder().isEmpty() ? "" : getTextureSubfolder() + "/") + name);
	}

	public String getTextureDomain() {
		return "";
	}

	public String getTextureSubfolder() {
		return "";
	}

	public String getNameDomain() {
		return Tags.MOD_ID;
	}
}
