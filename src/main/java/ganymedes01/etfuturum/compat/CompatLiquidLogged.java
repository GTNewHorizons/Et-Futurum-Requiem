package ganymedes01.etfuturum.compat;

import com.gtnewhorizons.liquidlogged.api.FluidHolderPredicate;
import com.gtnewhorizons.liquidlogged.api.LiquidLoggedAPI;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BaseLeaves;
import ganymedes01.etfuturum.blocks.BaseSlab;
import ganymedes01.etfuturum.blocks.BaseStairs;
import ganymedes01.etfuturum.blocks.BaseTrapdoor;
import ganymedes01.etfuturum.blocks.BaseWall;
import ganymedes01.etfuturum.blocks.BlockAmethystCluster;
import ganymedes01.etfuturum.blocks.BlockBarrier;
import ganymedes01.etfuturum.blocks.BlockChain;
import ganymedes01.etfuturum.blocks.BlockCopperGrate;
import ganymedes01.etfuturum.blocks.BlockGlowLichen;
import ganymedes01.etfuturum.blocks.BlockIronTrapdoor;
import ganymedes01.etfuturum.blocks.BlockLantern;
import ganymedes01.etfuturum.blocks.BlockLightningRod;
import ganymedes01.etfuturum.blocks.BlockMangroveRoots;
import ganymedes01.etfuturum.blocks.BlockModernWoodFence;
import ganymedes01.etfuturum.blocks.BlockPointedDripstone;
import ganymedes01.etfuturum.blocks.BlockWoodFence;
import ganymedes01.etfuturum.blocks.BlockWoodSign;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

public final class CompatLiquidLogged {

	/** Water only, as in vanilla. */
	private static final FluidHolderPredicate WATER_ONLY = (world, x, y, z, block, meta, fluid) -> fluid == FluidRegistry.WATER;

	private CompatLiquidLogged() {
	}

	/** Registers every enabled EFR block that is waterloggable in modern Minecraft. */
	public static void init() {
		for (ModBlocks entry : ModBlocks.VALUES) {
			if (entry.isEnabled() && isWaterloggable(entry.get())) {
				LiquidLoggedAPI.registerPredicate(entry.get(), WATER_ONLY);
			}
		}
	}

	/** Fills the block at this position with water if it can hold it; used by structure generation. */
	public static void fillWater(World world, int x, int y, int z) {
		if (LiquidLoggedAPI.canHold(world, x, y, z, FluidRegistry.WATER)) {
			LiquidLoggedAPI.setFluid(world, x, y, z, FluidRegistry.WATER);
		}
	}

	private static boolean isWaterloggable(Block block) {
		if (block instanceof BaseSlab) {
			return !block.isOpaqueCube(); // double slabs are opaque
		}
		return block instanceof BaseStairs
				|| block instanceof BaseWall
				|| block instanceof BaseTrapdoor
				|| block instanceof BlockIronTrapdoor
				|| block instanceof BlockWoodFence
				|| block instanceof BlockModernWoodFence
				|| block instanceof BlockWoodSign
				|| block instanceof BaseLeaves
				|| block instanceof BlockLantern
				|| block instanceof BlockChain
				|| block instanceof BlockCopperGrate
				|| block instanceof BlockLightningRod
				|| block instanceof BlockPointedDripstone
				|| block instanceof BlockAmethystCluster
				|| block instanceof BlockGlowLichen
				|| block instanceof BlockMangroveRoots
				|| block instanceof BlockBarrier; // also covers BlockLight
	}
}
