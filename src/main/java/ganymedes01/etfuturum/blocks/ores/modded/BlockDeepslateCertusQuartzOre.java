package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class BlockDeepslateCertusQuartzOre extends BaseSubtypesDeepslateOre {

	public BlockDeepslateCertusQuartzOre() {
		super("deepslate_certus_quartz_ore", "deepslate_charged_certus_quartz_ore");
	}

	@Override
	public String getTextureSubfolder() {
		return "ae2";
	}

	@Override
	public Block getBase(int meta) {
		if (meta == 1) {
			return ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get();
		}
		return ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get();
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return getBase(meta).getIcon(side, meta);
	}

	@Override
	public int getRenderType() {
		return RenderIDs.DEEPSLATE_CERTUS_QUARTZ_ORE;
	}
}
