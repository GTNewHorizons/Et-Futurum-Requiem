package ganymedes01.etfuturum.core.utils.helpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Chest loot entry that gives each generated stack one random applicable enchantment at a random level,
 * like the enchant_randomly loot function. Constructor arguments match WeightedRandomChestContent.
 */
public class EnchantRandomlyChestContent extends WeightedRandomChestContent {

	public EnchantRandomlyChestContent(ItemStack stack, int minStack, int maxStack, int weight) {
		super(stack, minStack, maxStack, weight);
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
