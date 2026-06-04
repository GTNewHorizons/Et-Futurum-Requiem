package ganymedes01.etfuturum.world;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockBubbleColumn;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.utils.Utils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.Map;
import java.util.Set;

public class EtFuturumWorldListener implements IWorldAccess {

	private final World world;
	private static final Map<Block, Block> REPLACEMENTS = new Reference2ObjectOpenHashMap<>();

	public EtFuturumWorldListener(World theWorld) {
		world = theWorld;
		if (ModBlocks.BREWING_STAND.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				REPLACEMENTS.put(Blocks.brewing_stand, ModBlocks.BREWING_STAND.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				REPLACEMENTS.put(ModBlocks.BREWING_STAND.get(), Blocks.brewing_stand);
			}
		}


		if (ModBlocks.BEACON.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				REPLACEMENTS.put(Blocks.beacon, ModBlocks.BEACON.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				REPLACEMENTS.put(ModBlocks.BEACON.get(), Blocks.beacon);
			}
		}

		if (ModBlocks.ENCHANTMENT_TABLE.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				REPLACEMENTS.put(Blocks.enchanting_table, ModBlocks.ENCHANTMENT_TABLE.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				REPLACEMENTS.put(ModBlocks.ENCHANTMENT_TABLE.get(), Blocks.enchanting_table);
			}
		}

		if (ModBlocks.ANVIL.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				REPLACEMENTS.put(Blocks.anvil, ModBlocks.ANVIL.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				REPLACEMENTS.put(ModBlocks.ANVIL.get(), Blocks.anvil);
			}
		}

		if (ModBlocks.SPONGE.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				REPLACEMENTS.put(Blocks.sponge, ModBlocks.SPONGE.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				REPLACEMENTS.put(ModBlocks.SPONGE.get(), Blocks.sponge);
			}
		}

		if (ModBlocks.DAYLIGHT_DETECTOR.isEnabled()) {
			if (ConfigWorld.tileReplacementMode != -1) {
				REPLACEMENTS.put(ModBlocks.DAYLIGHT_DETECTOR.get(), Blocks.daylight_detector);
			}
		}

		if (ModsList.BACK_IN_SLIME.isLoaded() && ConfigMixins.betterPistons) {
			REPLACEMENTS.put(GameRegistry.findBlock("bis", "SlimePistonBase"), Blocks.piston);
			REPLACEMENTS.put(GameRegistry.findBlock("bis", "StickySlimePistonBase"), Blocks.sticky_piston);
			REPLACEMENTS.put(GameRegistry.findBlock("bis", "SlimePistonHead"), Blocks.piston_head);
			if (ModBlocks.SLIME.isEnabled()) {
				REPLACEMENTS.put(GameRegistry.findBlock("bis", "SlimeBlock"), ModBlocks.SLIME.get());
			}
		}

		// Validate bubble column registrations, ensuring that disabled columns do not get registered.
		for(Map.Entry<String, BlockBubbleColumn> entry : BUBBLE_COLUMN_TAGS.entrySet()) {
			if(!RecipeHelper.validateItems(entry.getValue())) {
				BUBBLE_COLUMN_TAGS.remove(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {

		Block currentBlock = world.getBlock(x, y, z);
		int currentMeta = world.getBlockMetadata(x, y, z);
		handleBasaltFromLava(x, y, z, currentBlock);
		handleBubbleColumnCreation(x, y, z, currentBlock, currentMeta);

		Block replacement;
		TileEntity tile;

		if (REPLACEMENTS.isEmpty() || (replacement = REPLACEMENTS.get(currentBlock)) == null)
			return;

		tile = world.getTileEntity(x, y, z);
		NBTTagCompound nbt = new NBTTagCompound();
		if (tile != null) {
			tile.writeToNBT(nbt);
			if (tile instanceof IInventory invt) {
				for (int j = 0; j < invt.getSizeInventory(); j++) {
					invt.setInventorySlotContents(j, null);
				}
			}
		}

		int replacementMeta = world.getBlockMetadata(x, y, z);

		world.setBlock(x, y, z, replacement, replacementMeta, 2);
		TileEntity newTile = world.getTileEntity(x, y, z);
		if (newTile != null) {
			newTile.readFromNBT(nbt);
		}

	}

	Block soulsand = ModBlocks.SOUL_SOIL.isEnabled() ? ModBlocks.SOUL_SOIL.get() : Blocks.soul_sand;
	Block ice = ModBlocks.BLUE_ICE.isEnabled() ? ModBlocks.BLUE_ICE.get() : Blocks.packed_ice;

	private void handleBasaltFromLava(int x, int y, int z, Block currentBlock) { //Don't support Netherlicious blocks, this can be handled on their end
		if (ModBlocks.BASALT.isEnabled()) {
			if (currentBlock.getMaterial() == Material.lava) {
				if (world.getBlock(x, y - 1, z) == soulsand) {
					for (EnumFacing facing : Utils.ENUM_FACING_VALUES) {
						if (facing == EnumFacing.DOWN) continue;
						if (world.getBlock(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ()) == ice) {
							world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
							for (int l = 0; l < 8; ++l) {
								world.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + 1.2D, (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
							}
							world.setBlock(x, y, z, ModBlocks.BASALT.get());
							return;
						}
					}
				}
			}
		}
	}


	public static final Map<String, BlockBubbleColumn> BUBBLE_COLUMN_TAGS = new Reference2ObjectOpenHashMap<>();

	/**
	 * This is only needed to create the initial bubble column above the magma or soul sand or custom column above another block.
	 * The column itself handles the creation of the remainder of the column, as well as destroying itself.
	 */
	private void handleBubbleColumnCreation(int x, int y, int z, Block currentBlock, int currentMeta) {
		// TODO: This is probably ineffifcient, may rewrite this later
		if(!world.isRemote && !BUBBLE_COLUMN_TAGS.isEmpty() && !(currentBlock instanceof BlockBubbleColumn)) {
			Block below = world.getBlock(x, y - 1, z);
			if(currentBlock.getMaterial().isLiquid() && !below.getMaterial().isLiquid()) {
				handleBubbleColumnCreation(x, y - 1, z, below, world.getBlockMetadata(x, y - 1, z));
				return;
			}
			Set<String> tags = BlockTags.getTags(currentBlock, currentMeta);
			for (String tag : tags) {
				for (Map.Entry<String, BlockBubbleColumn> tag1 : BUBBLE_COLUMN_TAGS.entrySet()) {
					if (tag1.getKey().equals(tag)) {
						BlockBubbleColumn column = tag1.getValue();
						if (column.isCompatibleWater(world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z))) {
							world.setBlock(x, y + 1, z, column, 0, 3);
						}
						return;
					}
				}
			}
		}
	}

	@Override
	public void markBlockForRenderUpdate(int p_147588_1_, int p_147588_2_, int p_147588_3_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_,
											  int p_147585_5_, int p_147585_6_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSound(String soundName, double x, double y, double z, float volume,
						  float pitch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSoundToNearExcept(EntityPlayer p_85102_1_, String p_85102_2_, double p_85102_3_, double p_85102_5_,
									  double p_85102_7_, float p_85102_9_, float p_85102_10_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawnParticle(String p_72708_1_, double p_72708_2_, double p_72708_4_, double p_72708_6_,
							  double p_72708_8_, double p_72708_10_, double p_72708_12_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityCreate(Entity p_72703_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityDestroy(Entity p_72709_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playRecord(String p_72702_1_, int p_72702_2_, int p_72702_3_, int p_72702_4_) {
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcastSound(int p_82746_1_, int p_82746_2_, int p_82746_3_, int p_82746_4_, int p_82746_5_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playAuxSFX(EntityPlayer p_72706_1_, int p_72706_2_, int p_72706_3_, int p_72706_4_, int p_72706_5_,
						   int p_72706_6_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyBlockPartially(int p_147587_1_, int p_147587_2_, int p_147587_3_, int p_147587_4_,
									  int p_147587_5_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStaticEntitiesChanged() {
		// TODO Auto-generated method stub

	}

}
