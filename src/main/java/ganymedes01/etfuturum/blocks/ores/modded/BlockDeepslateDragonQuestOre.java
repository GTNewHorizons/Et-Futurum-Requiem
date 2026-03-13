package ganymedes01.etfuturum.blocks.ores.modded;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;

public class BlockDeepslateDragonQuestOre extends BaseSubtypesDeepslateOre {

    private static final String[] ores = new String[] { "deepslate_bakudanisi_ore", "deepslate_hikarinoisi_ore",
        "deepslate_hosinokakera_ore", "deepslate_inotinoisi_ore", "deepslate_kagaminoisi_ore",
        "deepslate_koorinokessyou_ore", "deepslate_littlemedal_ore", "deepslate_metaru_ore", "deepslate_migakizuna_ore",
        /* "deepslate_misuriru_ore", */ "deepslate_moon_ore", /* "deepslate_puratina_ore", */"deepslate_rubi_ore",
        "deepslate_taiyounoisi_ore", "deepslate_tekkouseki_ore", "deepslate_tokinosuisyou_ore",
        "deepslate_yougansekinokakera_ore" };

    public BlockDeepslateDragonQuestOre() {
        super(ores);
    }

    @Override
    public String getTextureSubfolder() {
        return "dragonquest";
    }

    @Override
    public Block getBase(int meta) {
        switch (meta) {
            case 1:
                return ExternalContent.Blocks.DQ_BRIGHTEN_ORE.get();
            case 2:
                return ExternalContent.Blocks.DQ_LUCIDA_ORE.get();
            case 3:
                return ExternalContent.Blocks.DQ_RESURROCK_ORE.get();
            case 4:
                return ExternalContent.Blocks.DQ_MIRRORSTONE_ORE.get();
            case 5:
                return ExternalContent.Blocks.DQ_ICE_CRYSTAL_ORE.get();
            case 6:
                return ExternalContent.Blocks.DQ_MINIMEDAL_ORE.get();
            case 7:
                return ExternalContent.Blocks.DQ_DENSINIUM_ORE.get();
            case 8:
                return ExternalContent.Blocks.DQ_GLASS_FRIT_ORE.get();
            case 9:
                return ExternalContent.Blocks.DQ_LUNAR_DIAMOND_ORE.get();
            case 10:
                return ExternalContent.Blocks.DQ_CORUNDUM_ORE.get();
            case 11:
                return ExternalContent.Blocks.DQ_SUNSTONE_ORE.get();
            case 12:
                return ExternalContent.Blocks.DQ_ALLOYED_IRON_ORE.get();
            case 13:
                return ExternalContent.Blocks.DQ_CHRONOCRYSTAL_ORE.get();
            case 14:
                return ExternalContent.Blocks.DQ_VOLCANIC_ORE.get();
            default:
            case 0:
                return ExternalContent.Blocks.DQ_ROCKBOMB_ORE.get();
        }
    }
}
