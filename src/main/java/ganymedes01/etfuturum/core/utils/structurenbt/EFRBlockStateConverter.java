package ganymedes01.etfuturum.core.utils.structurenbt;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

public class EFRBlockStateConverter extends BlockStateConverter {

	public static final BlockStateConverter INSTANCE = new EFRBlockStateConverter();

	@Override
	public int getMetaFromState(String blockName, Map<String, String> blockStates, ForgeDirection dir) {
		if (blockName.equals("minecraft:bone_block") && ConfigWorld.fossilBlock != null) {
			if (ConfigWorld.fossilBlock.getObject() == Blocks.quartz_block && ConfigWorld.fossilBlock.getMeta() == 2) {
				return super.getMetaFromState("minecraft:quartz_pillar", blockStates, dir);
			}
		}
		//TODO: Beetroot should be age * 2
		return super.getMetaFromState(blockName, blockStates, dir);
	}

	@Override
	public int getMetaFromStateWithSubtypeAdditions(String blockName, Map<String, String> blockStates, ForgeDirection dir) {
		int meta = getMetaFromState(blockName, blockStates, dir);
		String truncatedName = blockName.substring(blockName.indexOf(":") + 1);
		switch (truncatedName) {
			case "bone_block":
				if (ConfigWorld.fossilBlock != null) {
					if (ConfigWorld.fossilBlock.getObject() == Blocks.quartz_block && ConfigWorld.fossilBlock.getMeta() == 2) {
						return meta;
					}
					return meta + ConfigWorld.fossilBlock.getMeta();
				}
				break;
			default:
				break;
		}
		return super.getMetaFromStateWithSubtypeAdditions(blockName, blockStates, dir);
	}

	@Override
	public Block getBlockFromNamespace(String blockName, Map<String, String> blockStates) {
		String truncatedName = blockName.substring(blockName.indexOf(":") + 1);
		String nameToFind = truncatedName;

		switch (truncatedName) { //Blocks we don't have but are used in a structure.
			case "dragon_head":
			case "dragon_wall_head":
				return Blocks.air;
		}

		switch (truncatedName) {
			case "stone":
				return Blocks.stone;
			case "bone_block":
				if (ConfigWorld.fossilBlock != null) {
					return ConfigWorld.fossilBlock.getObject();
				}
				break;
			case "end_stone_bricks":
				nameToFind = "end_bricks";
				break;
			case "white_banner":
			case "orange_banner":
			case "magenta_banner":
			case "light_blue_banner":
			case "yellow_banner":
			case "lime_banner":
			case "pink_banner":
			case "gray_banner":
			case "light_gray_banner":
			case "cyan_banner":
			case "purple_banner":
			case "blue_banner":
			case "brown_banner":
			case "green_banner":
			case "red_banner":
			case "black_banner":
			case "white_wall_banner":
			case "orange_wall_banner":
			case "magenta_wall_banner":
			case "light_blue_wall_banner":
			case "yellow_wall_banner":
			case "lime_wall_banner":
			case "pink_wall_banner":
			case "gray_wall_banner":
			case "light_gray_wall_banner":
			case "cyan_wall_banner":
			case "purple_wall_banner":
			case "blue_wall_banner":
			case "brown_wall_banner":
			case "green_wall_banner":
			case "red_wall_banner":
			case "black_wall_banner":
				nameToFind = "banner";
		}
		// Spruce-specific blocks not in base converter (1.7.10 has no separate spruce door/trapdoor/fence)
		switch (truncatedName) {
			case "spruce_door":
			case "birch_door":
			case "jungle_door":
			case "acacia_door":
			case "dark_oak_door":
				return Blocks.wooden_door;
			case "spruce_trapdoor":
			case "birch_trapdoor":
			case "jungle_trapdoor":
			case "acacia_trapdoor":
			case "dark_oak_trapdoor":
				return Blocks.trapdoor;
			case "spruce_fence":
			case "birch_fence":
			case "jungle_fence":
			case "acacia_fence":
			case "dark_oak_fence":
				return Blocks.fence;
			case "spruce_fence_gate":
			case "birch_fence_gate":
			case "jungle_fence_gate":
			case "acacia_fence_gate":
			case "dark_oak_fence_gate":
				return Blocks.fence_gate;
			case "spruce_sign":
			case "birch_sign":
			case "jungle_sign":
			case "acacia_sign":
			case "dark_oak_sign":
				return Blocks.standing_sign;
			case "spruce_wall_sign":
			case "birch_wall_sign":
			case "jungle_wall_sign":
			case "acacia_wall_sign":
			case "dark_oak_wall_sign":
				return Blocks.wall_sign;
			case "spruce_pressure_plate":
			case "birch_pressure_plate":
			case "jungle_pressure_plate":
			case "acacia_pressure_plate":
			case "dark_oak_pressure_plate":
				return Blocks.wooden_pressure_plate;
			case "spruce_button":
			case "birch_button":
			case "jungle_button":
			case "acacia_button":
			case "dark_oak_button":
				return Blocks.wooden_button;
			case "lantern":
				return ModBlocks.LANTERN.isEnabled() ? ModBlocks.LANTERN.get() : Blocks.torch;
			case "soul_lantern":
				return ModBlocks.SOUL_LANTERN.isEnabled() ? ModBlocks.SOUL_LANTERN.get() : Blocks.torch;
			case "kelp":
			case "kelp_plant":
			case "seagrass":
			case "tall_seagrass":
				return Blocks.air;
		}

		Block efrBlock = GameRegistry.findBlock("etfuturum", nameToFind);
		if (efrBlock != null) {
			return efrBlock;
		}
		return super.getBlockFromNamespace(blockName, blockStates);
	}
}
