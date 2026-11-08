package ganymedes01.etfuturum;

import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.generate.structure.WorldGenShipwreck;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class EtFuturumLootTables {

	private static final String COMPOSTER = "composting";
	public static final ChestGenHooks COMPOSTER_LOOT = ChestGenHooks.getInfo(COMPOSTER);

	public static void init() {
		ChestGenHooks.getInfo(COMPOSTER).setMin(1);
		ChestGenHooks.getInfo(COMPOSTER).setMax(1);
		COMPOSTER_LOOT.addItem(new WeightedRandomChestContent(new ItemStack(Items.dye, 1, 15), 1, 1, 10));
		//For some reason it is min count, max count and weight, yes weight is the last arg not the first....

		if (ConfigWorld.enableShipwrecks) {
			initShipwreckLoot();
		}
	}

	private static void initShipwreckLoot() {
		// Supply chest: food, basic materials (3-10 items)
		ChestGenHooks supply = ChestGenHooks.getInfo(WorldGenShipwreck.SHIPWRECK_SUPPLY);
		supply.setMin(3);
		supply.setMax(10);
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.paper), 1, 12, 8));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.potato), 2, 6, 7));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.poisonous_potato), 2, 6, 7));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.carrot), 4, 8, 7));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.wheat), 8, 21, 7));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.coal), 2, 8, 6));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.rotten_flesh), 5, 24, 5));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Blocks.pumpkin), 1, 3, 2));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.gunpowder), 1, 5, 3));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Blocks.tnt), 1, 2, 1));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.leather_helmet), 1, 1, 3));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.leather_chestplate), 1, 1, 3));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.leather_leggings), 1, 1, 3));
		supply.addItem(new WeightedRandomChestContent(new ItemStack(Items.leather_boots), 1, 1, 3));
		if (ModBlocks.BAMBOO.isEnabled()) {
			supply.addItem(new WeightedRandomChestContent(new ItemStack(ModBlocks.BAMBOO.get()), 1, 3, 2));
		}

		// Treasure chest: valuable loot (3-6 items)
		ChestGenHooks treasure = ChestGenHooks.getInfo(WorldGenShipwreck.SHIPWRECK_TREASURE);
		treasure.setMin(3);
		treasure.setMax(6);
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_ingot), 1, 5, 90));
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_ingot), 1, 5, 10));
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.emerald), 1, 5, 40));
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond), 1, 1, 5));
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.experience_bottle), 1, 1, 5));
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_nugget), 1, 10, 10));
		treasure.addItem(new WeightedRandomChestContent(new ItemStack(Items.dye, 1, 4), 1, 10, 20)); // lapis

		// Map chest: navigation items (3 items)
		ChestGenHooks map = ChestGenHooks.getInfo(WorldGenShipwreck.SHIPWRECK_MAP);
		map.setMin(3);
		map.setMax(3);
		map.addItem(new WeightedRandomChestContent(new ItemStack(Items.compass), 1, 1, 1));
		map.addItem(new WeightedRandomChestContent(new ItemStack(Items.map), 1, 1, 1));
		map.addItem(new WeightedRandomChestContent(new ItemStack(Items.clock), 1, 1, 1));
		map.addItem(new WeightedRandomChestContent(new ItemStack(Items.paper), 1, 10, 20));
		map.addItem(new WeightedRandomChestContent(new ItemStack(Items.feather), 1, 5, 10));
		map.addItem(new WeightedRandomChestContent(new ItemStack(Items.book), 1, 5, 5));
	}
}
