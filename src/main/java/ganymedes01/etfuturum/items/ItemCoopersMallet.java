package ganymedes01.etfuturum.items;

import com.github.bsideup.jabel.Desugar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.blocks.BlockBarrel;
import ganymedes01.etfuturum.compat.CompatBackhand;
import ganymedes01.etfuturum.compat.CompatIronChest;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Swaps the chest or barrel being clicked for a container held in the offhand or in a
 * hotbar slot next to the mallet, keeping the inventory.
 * Replacement must be equal in capacity or larger.
 */
public class ItemCoopersMallet extends BaseItem {

	/** offhand marker */
	private static final int OFFHAND = -1;

	public ItemCoopersMallet() {
		setNames("coopers_mallet");
		setMaxStackSize(1);
		addTooltip = true;
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip, boolean advancedToolTips) {
		if (shouldAddTooltip()) {
			if (ModsList.BACKHAND.isLoaded()) {
				toolTip.add(StatCollector.translateToLocal(getUnlocalizedName() + ".desc.backhand"));
			} else {
				toolTip.add(StatCollector.translateToLocal(getUnlocalizedName() + ".desc"));
			}
		}
	}

	/**
	 * Both the chest and the barrel open their GUI on click
	 */
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) {
			return false;
		}

		Existing from = read(world, x, y, z);
		if (from == null) {
			return false;
		}

		for (int slot : swapSlots(player)) {
			ItemStack held = getSlot(player, slot);
			if (held == null || held.stackSize <= 0 || held == stack) {
				continue;
			}
			if (swap(world, x, y, z, player, from, held)) {
				pay(player, slot, held, from.drop());
				return true;
			}
		}
		return false;
	}

	/** Barrel metadata packs the facing into the low bits, the rest is the open state. */
	public static int barrelFacing(int meta) {
		return BlockPistonBase.getPistonOrientation(meta);
	}

	/** The container already in the world. */
	@Desugar
	private record Existing(IInventory inventory, int facing, ItemStack drop) {
	}

	/** The container to put in its place, built from the held item. */
	@Desugar
	private record Replacement(Block block, int meta, TileEntity tile) {
	}

	private static Existing read(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack drop = new ItemStack(block, 1, block.damageDropped(meta));

		if (te instanceof TileEntityBarrel barrel) {
			return barrel.numPlayersUsing != 0 ? null : new Existing(barrel, barrelFacing(meta), drop);
		}

		if (te instanceof TileEntityChest chest && block == Blocks.chest) {
			return chest.numPlayersUsing != 0 ? null : new Existing(chest, meta, drop);
		}

		if (ironChestEnabled() && te instanceof IInventory inventory) {
			Integer facing = CompatIronChest.readFacing(te);
			return facing == null ? null : new Existing(inventory, facing, drop);
		}

		return null;
	}

	private static Replacement build(ItemStack held, int facing) {
		Block block = Block.getBlockFromItem(held.getItem());
		if (block == Blocks.chest) {
			return new Replacement(block, 0, new TileEntityChest());
		}

		if (block instanceof BlockBarrel barrel) {
			return new Replacement(block, facing, new TileEntityBarrel(barrel.getType()));
		}

		if (ironChestEnabled()) {
			TileEntity tile = CompatIronChest.newChest(block, held.getItemDamage(), facing);
			return tile == null ? null : new Replacement(block, held.getItemDamage(), tile);
		}

		return null;
	}

	private static boolean swap(World world, int x, int y, int z, EntityPlayer player, Existing from, ItemStack held) {
		Replacement to = build(held, from.facing());
		if (to == null || !(to.tile() instanceof IInventory)) {
			return false;
		}

		// Swapping a container for its own kind would do nothing
		if (from.drop().getItem() == held.getItem() && from.drop().getItemDamage() == held.getItemDamage()) {
			return false;
		}

		// Refuses a spot that would make an oversized chest, such as one beside a double chest
		if (to.block() == Blocks.chest && !Blocks.chest.canPlaceBlockAt(world, x, y, z)) {
			return false;
		}

		if (from.inventory().hasCustomInventoryName()) {
			setName(to.tile(), from.inventory().getInventoryName());
		}

		if (!convert(world, x, y, z, from.inventory(), to.tile(), to.block(), to.meta())) {
			return false;
		}

		if (to.block() == Blocks.chest) {
			// call chest specific placed code
			Blocks.chest.onBlockPlacedBy(world, x, y, z, player, new ItemStack(Blocks.chest));

			// lone chest keeps the old facing
			if (from.facing() > 1 && !hasAdjacentChest(world, x, y, z)) {
				world.setBlockMetadataWithNotify(x, y, z, from.facing(), 3);
			}
		}
		return true;
	}

	/**
	 * Moves every slot into the replacement tile entity, then swaps the block out for it.
	 * Refuses a smaller replacement so no item is lost.
	 * Empty first to prevent dropping items on break
	 * Clearing the block to air first lets a neighboring chest turn back into a single chest.
	 */
	private static boolean convert(World world, int x, int y, int z, IInventory from, TileEntity tile, Block block, int meta) {
		IInventory to = (IInventory) tile;
		if (to.getSizeInventory() < from.getSizeInventory()) {
			return false;
		}

		for (int i = 0; i < from.getSizeInventory(); i++) {
			to.setInventorySlotContents(i, from.getStackInSlot(i));
			from.setInventorySlotContents(i, null);
		}

		world.setBlock(x, y, z, Blocks.air, 0, 3);
		world.setBlock(x, y, z, block, meta, 3);
		world.setTileEntity(x, y, z, tile);
		tile.markDirty();
		world.markBlockForUpdate(x, y, z);

		world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Reference.MOD_ID + ":block.coopers_mallet.convert",
				1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	/** Takes the replacement out of its slot and hands the old container back. */
	private static void pay(EntityPlayer player, int slot, ItemStack held, ItemStack drop) {
		if (player.capabilities.isCreativeMode) {
			return;
		}

		if (--held.stackSize <= 0) {
			setSlot(player, slot, null);
		}

		// Merge with a matching stack first, the freed slot is only a fallback
		if (!player.inventory.addItemStackToInventory(drop) && drop.stackSize > 0) {
			if (getSlot(player, slot) == null) {
				setSlot(player, slot, drop);
			} else {
				player.entityDropItem(drop, 0);
			}
		}

		// The slots are written server side, so the client needs telling
		player.inventoryContainer.detectAndSendChanges();
	}

	/**
	 * The offhand first, then the hotbar slots either side of the mallet.
	 */
	private static List<Integer> swapSlots(EntityPlayer player) {
		List<Integer> slots = new ArrayList<>(3);
		if (ModsList.BACKHAND.isLoaded()) {
			slots.add(OFFHAND);
		}

		int held = player.inventory.currentItem;
		if (held > 0) {
			slots.add(held - 1);
		}
		if (held < InventoryPlayer.getHotbarSize() - 1) {
			slots.add(held + 1);
		}
		return slots;
	}

	private static ItemStack getSlot(EntityPlayer player, int slot) {
		return slot == OFFHAND ? CompatBackhand.getOffhandItem(player) : player.inventory.getStackInSlot(slot);
	}

	private static void setSlot(EntityPlayer player, int slot, ItemStack stack) {
		if (slot == OFFHAND) {
			CompatBackhand.setOffhandItem(player, stack);
		} else {
			player.inventory.setInventorySlotContents(slot, stack);
		}
	}

	/** Iron chests have no custom name, so it is dropped there. */
	private static void setName(TileEntity tile, String name) {
		if (tile instanceof TileEntityBarrel barrel) {
			barrel.setCustomName(name);
		} else if (tile instanceof TileEntityChest chest) {
			chest.func_145976_a(name);
		}
	}

	private static boolean ironChestEnabled() {
		return ModsList.IRON_CHEST.isLoaded() && ConfigModCompat.barrelIronChest;
	}

	private static boolean hasAdjacentChest(World world, int x, int y, int z) {
		return world.getBlock(x - 1, y, z) == Blocks.chest || world.getBlock(x + 1, y, z) == Blocks.chest
				|| world.getBlock(x, y, z - 1) == Blocks.chest || world.getBlock(x, y, z + 1) == Blocks.chest;
	}
}
