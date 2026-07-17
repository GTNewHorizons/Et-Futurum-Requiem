package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockBanner;
import ganymedes01.etfuturum.blocks.BlockPotionCauldron;
import ganymedes01.etfuturum.blocks.BlockShulkerBox;
import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.ducks.IWaxableSign;
import ganymedes01.etfuturum.tileentities.TileEntityBanner;
import ganymedes01.etfuturum.tileentities.TileEntityCauldronPotion;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CompatWaila {

	public static void wailaCallback(IWailaRegistrar registrar) {
		registrar.registerStackProvider(new ShulkerDataProvider(), BlockShulkerBox.class);
		registrar.registerStackProvider(new BannerDataProvider(), BlockBanner.class);
		registrar.registerBodyProvider(new BannerDataProvider(), BlockBanner.class);
		registrar.registerStackProvider(new PotionCauldronDataProvider(), BlockPotionCauldron.class);
		registrar.registerBodyProvider(new PotionCauldronDataProvider(), BlockPotionCauldron.class);
		registrar.registerBodyProvider(new SignDataProvider(), BlockSign.class);
		registrar.registerBodyProvider(new SignDataProvider(), BlockWoodSign.class);
	}

	public static void register() {
		FMLInterModComms.sendMessage("Waila", "register", CompatWaila.class.getName() + ".wailaCallback");
	}

	public static class BannerDataProvider implements IWailaDataProvider {

		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			TileEntityBanner banner = (TileEntityBanner) accessor.getTileEntity();
			return banner.createStack();
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			return null;
		}

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			List<String> strings = new ArrayList<>();
			Item.getItemFromBlock(ModBlocks.BANNER.get()).addInformation(itemStack, null, strings, false);
			return strings;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			return null;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
			return tag;
		}

	}

	public static class PotionCauldronDataProvider implements IWailaDataProvider {

		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			TileEntityCauldronPotion cauldron = (TileEntityCauldronPotion) accessor.getTileEntity();
			if (cauldron.potion != null)
				return cauldron.potion;
			return new ItemStack(Blocks.cauldron);
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			return null;
		}

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			List<String> strings = new ArrayList<>();
			Items.potionitem.addInformation(itemStack, null, strings, false);
			return strings;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			return null;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
			return tag;
		}

	}

	public static class ShulkerDataProvider implements IWailaDataProvider {
		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			ItemStack stack = accessor.getStack();
			if (accessor.getTileEntity() instanceof TileEntityShulkerBox box) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				stack.getTagCompound().setByte("Type", (byte) box.type.ordinal());
				stack.getTagCompound().setByte("Color", box.color);
			}
			return stack;
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP arg0, TileEntity arg1, NBTTagCompound arg2, World arg3, int arg4, int arg5, int arg6) {
			return arg2;
		}
	}

	public static class SignDataProvider implements IWailaDataProvider {
		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return accessor.getStack();
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			if (accessor.getTileEntity() instanceof IWaxableSign sign && sign.isWaxed()) {
				currenttip.add("Waxed");
			}
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
										 IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
			return tag;
		}
	}
}
