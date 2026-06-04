package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Because the standard block constructor is protected...
 * I also provide some extra helper functions and stuff :3
 */
public class BaseBlock extends Block {

	private Block mapColorBase;

	public BaseBlock(Material materialIn) {
		super(materialIn);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	public BaseBlock setUnlocalizedNameWithPrefix(String name) {
		setBlockName((getNameDomain().isEmpty() ? "" : getNameDomain() + ".") + name);
		return this;
	}

	@Override
	public Block setBlockTextureName(String name) {
		return super.setBlockTextureName((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":")
				+ (getTextureSubfolder().isEmpty() ? "" : getTextureSubfolder() + "/") + name);
	}

	public BaseBlock setNames(String name) {
		setUnlocalizedNameWithPrefix(name);
		setBlockTextureName(name);
		return this;
	}

	public BaseBlock setToolClass(String toolClass, int level) {
		for (int m = 0; m < 16; m++) {
			setHarvestLevel(toolClass, level, m);
		}
		return this;
	}

	public BaseBlock setToolClass(String toolClass, int level, int meta) {
		setHarvestLevel(toolClass, level, meta);
		return this;
	}

	public BaseBlock setBlockSound(SoundType type) {
		Utils.setBlockSound(this, type);
		return this;
	}

	public BaseBlock setMapColorBaseBlock(Block block) {
		mapColorBase = block;
		return this;
	}

	@Override
	public MapColor getMapColor(int meta) {
		return mapColorBase == null ? super.getMapColor(meta) : mapColorBase.getMapColor(meta);
	}

	public String getTextureDomain() {
		return "";
	}

	public String getTextureSubfolder() {
		return "";
	}

	public String getNameDomain() {
		return Tags.MOD_ID;
	}
}
