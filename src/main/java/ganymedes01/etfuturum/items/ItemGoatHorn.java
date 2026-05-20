package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.Tags;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemGoatHorn extends BaseItem {

	public static final int VARIANT_COUNT = 8;
	public static final int USE_DURATION = 140;
	private static final String COOLDOWN_TAG = "GoatHornCooldown";
	private static final String[] INSTRUMENTS = new String[]{
			"ponder_goat_horn", "sing_goat_horn", "seek_goat_horn", "feel_goat_horn",
			"admire_goat_horn", "call_goat_horn", "yearn_goat_horn", "dream_goat_horn"
	};

	@SideOnly(Side.CLIENT)
	private IIcon tootingIcon;

	public ItemGoatHorn() {
		super("goat_horn");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.etfuturum.goat_horn";
	}

	@Override
	public int getMetadata(int meta) {
		return getVariant(meta);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < VARIANT_COUNT; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (getCooldown(stack) <= 0) {
			setCooldown(stack, USE_DURATION);
			player.setItemInUse(stack, getMaxItemUseDuration(stack));

			if (!world.isRemote) {
				world.playSoundAtEntity(player, Tags.MC_ASSET_VER + ":item.goat_horn.sound." + getVariant(stack), 16.0F, 1.0F);
			}
		}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
		int cooldown = getCooldown(stack);
		if (cooldown > 0) {
			setCooldown(stack, cooldown - 1);
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return USE_DURATION;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.none;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.uncommon;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean debug) {
		String key = "instrument.minecraft." + INSTRUMENTS[getVariant(stack)];
		tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(key));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return usingItem == stack && tootingIcon != null ? tootingIcon : super.getIcon(stack, pass, player, usingItem, useRemaining);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		tootingIcon = register.registerIcon("goat_horn");
	}

	public static int getVariant(ItemStack stack) {
		return stack == null ? 0 : getVariant(stack.getItemDamage());
	}

	public static int getVariant(int meta) {
		return meta < 0 ? 0 : meta % VARIANT_COUNT;
	}

	public static String getInstrumentName(int meta) {
		return INSTRUMENTS[getVariant(meta)];
	}

	private static int getCooldown(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger(COOLDOWN_TAG) : 0;
	}

	private static void setCooldown(ItemStack stack, int cooldown) {
		if (cooldown <= 0) {
			if (stack.hasTagCompound()) {
				stack.getTagCompound().removeTag(COOLDOWN_TAG);
			}
			return;
		}

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger(COOLDOWN_TAG, cooldown);
	}
}
