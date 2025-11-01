package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

public interface IDegradable {

	int CHECK_RANGE = 4;

	/**
	 * Returns an int relative to the meta values regular coppers use.
	 * Used to streamline the process of copper waxing, dewaxing and degrading.
	 * <p>
	 * Default copper blocks:
	 * 0:  Regular, No degredation
	 * 1:  Regular, Exposed
	 * 2:  Regular, Weathered
	 * 3:  Regular, Oxidized
	 * 4:  Variant, No degredation
	 * 5:  Variant, Exposed
	 * 6:  Variant, Weathered
	 * 7:  Variant, Oxidized
	 * 8:  Regular Waxed, No degredation
	 * 9:  Regular Waxed, Exposed
	 * 10: Regular Waxed, Weathered
	 * 11: Regular Waxed, Oxidized
	 * 12: Variant Waxed, No degredation
	 * 13: Variant Waxed, Exposed
	 * 14: Variant Waxed, Weathered
	 * 15: Variant Waxed, Oxidized
	 *
	 * @param meta World meta
	 * @return A copper meta value from above.
	 */
	int getCopperMeta(int meta);

	/**
	 * BlockAccess-sensitive version of getCopperMeta
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 * @return
	 */
	default int getCopperMeta(IBlockAccess world, int x, int y, int z, int meta) {
		return getCopperMeta(meta);
	}

	/**
	 * Deprecated, use getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int meta, int worldMeta)
	 */
	@Deprecated
	default int getFinalCopperMeta(int meta, int worldMeta) {
		return getCopperMeta(meta);
	}


	/**
	 * A getCopperMeta copy that takes the actual meta value of the block into account.
	 * Used by slabs to wrap the meta data from 0-7 or 8-15.
	 *
	 * @param meta      Copper meta
	 * @param worldMeta World meta (Is always the meta value from before the copper changed and is the actual block meta, not the copper ID)
	 * @return A copper value from getCopperMeta.
	 */
	default int getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int meta, int worldMeta) {
		return getCopperMeta(world, x, y, z, meta);
	}


	/**
	 * Returns a block ID depending on the meta input.
	 * Used for copper stairs to return the blocks, since they're different blocks and not meta.
	 *
	 * @param meta
	 * @return
	 */
	Block getCopperBlockFromMeta(int meta);

	default void tickDegradation(World world, int x, int y, int z, Random random) {
		if (!world.isRemote) {
			if (random.nextFloat() < 0.05688889F) {
				this.tryDegrade(world, x, y, z, random);
			}
		}
	}

	default boolean countTowardsDegredation(int meta, IBlockAccess world, int x, int y, int z) {
		return meta < 8;
	}

	default void tryDegrade(World world, int x, int y, int z, Random random) {
		int i = getCopperMeta(world, x, y, z, world.getBlockMetadata(x, y, z));
		int j = 0;
		int k = 0;

		if (i < 7 && i % 4 != 3) {
			for (int x1 = -CHECK_RANGE; x1 <= CHECK_RANGE; x1++) {
				for (int y1 = -CHECK_RANGE; y1 <= CHECK_RANGE; y1++) {
					for (int z1 = -CHECK_RANGE; z1 <= CHECK_RANGE; z1++) {
						Block block = world.getBlock(x1 + x, y1 + y, z1 + z);
						if (block instanceof IDegradable && (x1 != 0 || y1 != 0 || z1 != 0) && Math.abs(x1) + Math.abs(y1) + Math.abs(z1) <= CHECK_RANGE) {
							int m = ((IDegradable) block).getCopperMeta(world, x, y, z, world.getBlockMetadata(x1 + x, y1 + y, z1 + z));

							if (!countTowardsDegredation(m, world, x + x1, y + y1, z + z1)) {
								continue;
							}

							m %= 4;

							if (m < i % 4) {
								return;
							}

							if (m > i % 4) {
								++k;
							} else {
								++j;
							}
						}
					}
				}
			}

			float f = (float) (k + 1) / (float) (k + j + 1);
			float g = f * f * (i % 4 == 0 ? 0.75F : 1F);
			if (random.nextFloat() < g) {
				setCopperBlock(getCopperBlockFromMeta(i + 1), getFinalCopperMeta(world, x, y, z, i + 1, world.getBlockMetadata(x, y, z)), world, x, y, z);
			}
		}
	}

	default void setCopperBlock(Block newBlock, int newMeta, World world, int x, int y, int z) {
		world.setBlock(x, y, z, newBlock, newMeta, 2);
	}

	String[] waxStrings = new String[]{"materialWax", "materialWaxcomb", "materialHoneycomb", "itemBeeswax"};

	static boolean isWaxableMaterial(ItemStack stack) {
		boolean slimeball = true;
		for (String waxString : waxStrings) {
			if (OreDictionary.doesOreNameExist(waxString)) {
				slimeball = false;
				if (EtFuturum.hasDictTag(stack, waxString)) {
					return true;
				}
			}
		}
		return slimeball && EtFuturum.hasDictTag(stack, "slimeball");
	}

	default boolean tryWaxOnWaxOff(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		boolean flag = false;
		boolean flag2 = false;
		int meta = getCopperMeta(world, x, y, z, world.getBlockMetadata(x, y, z));
		if (entityPlayer.getCurrentEquippedItem() != null) {
			ItemStack heldStack = entityPlayer.getCurrentEquippedItem();
			if (meta < 8) {
				if (isWaxableMaterial(heldStack)) {
					flag = true;

					if (!entityPlayer.capabilities.isCreativeMode && --heldStack.stackSize <= 0) {
						entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
					}

					entityPlayer.inventoryContainer.detectAndSendChanges();
				}
			}
			if (heldStack.getItem().getToolClasses(heldStack).contains("axe") && (meta % 4 != 0 || meta > 7)) {
				heldStack.damageItem(1, entityPlayer);
				if (meta < 8) {
					flag2 = true;
				} else {
					flag = true;
				}
			}
			int waxMeta;
			Block block;
			if (flag && !flag2) {
				waxMeta = meta > 7 ? meta % 8 : (meta % 8 + 8);
				block = getCopperBlockFromMeta(waxMeta);
				setCopperBlock(block, getFinalCopperMeta(world, x, y, z, waxMeta, world.getBlockMetadata(x, y, z)), world, x, y, z);
				spawnParticles(world, x, y, z, meta < 8 ? 0 : 1);
			} else if (!flag && flag2) {
				block = getCopperBlockFromMeta(meta - 1);
				setCopperBlock(block, getFinalCopperMeta(world, x, y, z, meta - 1, world.getBlockMetadata(x, y, z)), world, x, y, z);
				spawnParticles(world, x, y, z, 2);
			}
		}
		return flag || flag2;
	}

	default void playSound(World world, double x, double y, double z, int type) {
		int pitch = world.rand.nextInt(3);
		world.playSound(x, y, z,
				Tags.MC_ASSET_VER + ":item." + (type == 0 ? "honeycomb.wax_on" : type == 1 ? "axe.wax_off" : "axe.scrape"),
				1F, (float) ((pitch == 0 ? 0 : ((double) pitch / 10D)) + 0.9D), false);
	}

	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type  0: Wax on 1: Wax off 2: Oxidation Scrape
	 */
	default void spawnParticles(World world, int x, int y, int z, int type) {
		if (world.isRemote) {
			Random random = world.rand;
			double d0 = 0.0625D;

			playSound(world, x + 0.5D, y + 0.5D, z + 0.5D, type);

			for (int l = 0; l < 10; ++l) {
				double d1 = x + random.nextFloat();
				double d2 = y + random.nextFloat();
				double d3 = z + random.nextFloat();

				if (l == 0 && !world.getBlock(x, y + 1, z).isOpaqueCube()) {
					d2 = y + 1 + d0;
				}

				if (l == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube()) {
					d2 = y - d0;
				}

				if (l == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube()) {
					d3 = z + 1 + d0;
				}

				if (l == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube()) {
					d3 = z - d0;
				}

				if (l == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube()) {
					d1 = x + 1 + d0;
				}

				if (l == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube()) {
					d1 = x - d0;
				}

				if (d1 < x || d1 > x + 1 || d2 < 0.0D || d2 > y + 1 || d3 < z || d3 > z + 1) {
					if (type == 0) {
						CustomParticles.spawnCopperWaxOnParticle(world, d1, d2, d3);
					} else if (type == 1) {
						CustomParticles.spawnCopperWaxOffParticle(world, d1, d2, d3);
					} else {
						CustomParticles.spawnCopperScrapeParticle(world, d1, d2, d3);
					}
				}
			}
		}
	}
}