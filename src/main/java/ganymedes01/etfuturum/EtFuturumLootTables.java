package ganymedes01.etfuturum;

import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.generate.structure.WorldGenRuinedPortal;
import ganymedes01.etfuturum.world.generate.structure.WorldGenShipwreck;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

		if (ConfigWorld.enableRuinedPortals) {
			initRuinedPortalLoot();
		}
	}

	// Mirrors the 1.21.1 chests/ruined_portal loot table: 4-8 rolls, the weight is the last arg of WeightedRandomChestContent.
	// The bell is left out because Et Futurum has no bell.
	private static void initRuinedPortalLoot() {
		ChestGenHooks portal = ChestGenHooks.getInfo(WorldGenRuinedPortal.RUINED_PORTAL_LOOT);
		portal.setMin(4);
		portal.setMax(8);
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Blocks.obsidian), 1, 2, 40));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.flint), 1, 4, 40));
		if (ModItems.NUGGET_IRON.isEnabled()) {
			portal.addItem(new WeightedRandomChestContent(new ItemStack(ModItems.NUGGET_IRON.get()), 9, 18, 40));
		}
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.flint_and_steel), 1, 1, 40));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.fire_charge), 1, 1, 40));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_apple), 1, 1, 15));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_nugget), 4, 24, 15));
		Item[] goldGear = {Items.golden_sword, Items.golden_axe, Items.golden_hoe, Items.golden_shovel, Items.golden_pickaxe,
			Items.golden_boots, Items.golden_chestplate, Items.golden_helmet, Items.golden_leggings};
		for (Item item : goldGear) {
			portal.addItem(new EnchantRandomlyContent(item, 15));
		}
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.speckled_melon), 4, 12, 5));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_horse_armor), 1, 1, 5));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Blocks.light_weighted_pressure_plate), 1, 1, 5));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_carrot), 4, 12, 5));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.clock), 1, 1, 5));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_ingot), 2, 8, 5));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_apple, 1, 1), 1, 1, 1));
		portal.addItem(new WeightedRandomChestContent(new ItemStack(Blocks.gold_block), 1, 2, 1));
	}

	/**
	 * Equivalent of the enchant_randomly loot function: one random applicable enchantment at a random level.
	 */
	private static class EnchantRandomlyContent extends WeightedRandomChestContent {

		EnchantRandomlyContent(Item item, int weight) {
			super(new ItemStack(item), 1, 1, weight);
		}

		@Override
		protected ItemStack[] generateChestContent(Random random, IInventory inventory) {
			ItemStack[] stacks = super.generateChestContent(random, inventory);
			for (ItemStack stack : stacks) {
				List<Enchantment> applicable = new ArrayList<>();
				for (Enchantment enchantment : Enchantment.enchantmentsList) {
					if (enchantment != null && enchantment.canApply(stack)) {
						applicable.add(enchantment);
					}
				}
				if (!applicable.isEmpty()) {
					Enchantment enchantment = applicable.get(random.nextInt(applicable.size()));
					stack.addEnchantment(enchantment, MathHelper.getRandomIntegerInRange(random, enchantment.getMinLevel(), enchantment.getMaxLevel()));
				}
			}
			return stacks;
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
