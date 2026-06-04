package ganymedes01.etfuturum.items.rawore.modded;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.BaseSubtypesItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Modded raw ore class that doesn't add the raw ores to the Creative menu if there would be no ore for them to base off of.
 */
public class ItemGeneralModdedRawOre extends BaseSubtypesItem implements IInitAction {
	public final String[] ores;

	public ItemGeneralModdedRawOre(String... names) {
		super(names);
		ores = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			ores[i] = "ingot" + StringUtils.capitalize(names[i].replaceFirst("^raw_", ""));
		}
		setNames("modded_raw_ore");
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
		for (int i = 0; i < ores.length; i++) {
			ItemStack stack = new ItemStack(item, 1, i);
			if (Utils.listGeneralModdedRawOre(ores[i]) || Utils.listGeneralModdedRawOre(ores[i].replace("Mythril", "Mithril"))) {
				list.add(stack);
			}
		}
	}

	@Override
	public String getTextureDomain() {
		return Tags.MOD_ID;
	}


	public static final List<ItemGeneralModdedRawOre> loaded = Lists.newLinkedList();

	@Override
	public void postInitAction() {
		loaded.add(this);
	}
}
