package ganymedes01.etfuturum.blocks;

public class BlockChiseledCopper extends BlockCopper {

    public BlockChiseledCopper() {
        super(
            "chiseled_copper",
            "exposed_chiseled_copper",
            "weathered_chiseled_copper",
            "oxidized_chiseled_copper",
            "waxed_chiseled_copper",
            "waxed_exposed_chiseled_copper",
            "waxed_weathered_chiseled_copper",
            "waxed_oxidized_chiseled_copper");
    }

    @Override
    public int getCopperMeta(int meta) {
        return meta + (meta > 3 ? 4 : 0);
    }
}
