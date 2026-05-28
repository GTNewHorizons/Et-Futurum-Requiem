package ganymedes01.etfuturum.world.end.gen;

import ganymedes01.etfuturum.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

/**
 * Registers End city treasure loot using Forge's ChestGenHooks system.
 * Loot contents match vanilla End city treasure loot table.
 */
public class EndCityLoot {

	public static final String END_CITY_TREASURE = "endCityTreasure";
	public static final String END_CITY_ELYTRA = "endCityElytra";

	public static void init() {
		ChestGenHooks info = ChestGenHooks.getInfo(END_CITY_TREASURE);
		info.setMin(2);
		info.setMax(6);

		ChestGenHooks elytra = ChestGenHooks.getInfo(END_CITY_ELYTRA);
		elytra.setMin(1);
		elytra.setMax(1);
		if (ModItems.ELYTRA.isEnabled()) {
			elytra.addItem(new WeightedRandomChestContent(ModItems.ELYTRA.newItemStack(), 1, 1, 1));
		}

		// Basic treasures
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond), 2, 7, 5));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_ingot), 4, 8, 10));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_ingot), 2, 7, 15));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.emerald), 2, 6, 2));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.wheat_seeds), 1, 10, 5)); // Beetroot seeds substitute

		// Equipment
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.saddle), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_horse_armor), 1, 1, 1));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_horse_armor), 1, 1, 1));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_horse_armor), 1, 1, 1));

		// Diamond gear (vanilla enchants these, but we add them unenchanted for simplicity)
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_sword), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_pickaxe), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_shovel), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_helmet), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_chestplate), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_leggings), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_boots), 1, 1, 3));

		// Iron gear
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_sword), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_pickaxe), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_shovel), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_helmet), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_chestplate), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_leggings), 1, 1, 3));
		info.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_boots), 1, 1, 3));
	}
}
