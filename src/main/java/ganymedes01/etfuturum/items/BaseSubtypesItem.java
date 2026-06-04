package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.Tags;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BaseSubtypesItem extends BaseItem {

	protected IIcon[] icons;
	public final String[] types;

	public BaseSubtypesItem(String... types) {
		this.types = types;
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + getNameDomain() + "." + types[stack.getItemDamage() % types.length];
	}

	@Override
	public IIcon getIconFromDamage(int meta) {
		return icons[meta % icons.length];
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
		for (int i = 0; i < types.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[types.length];
		for (int i = 0; i < types.length; i++) {
			icons[i] = reg.registerIcon((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":") + types[i]);
		}
	}

	@Override
	public String getTextureDomain() {
		return "";
	}

	@Override
	public String getNameDomain() {
		return Tags.MOD_ID;
	}
}