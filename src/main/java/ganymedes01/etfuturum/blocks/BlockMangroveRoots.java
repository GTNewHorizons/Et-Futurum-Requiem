package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.lib.RenderIDs;

public class BlockMangroveRoots extends BaseBlock {

    private IIcon sideIcon;

    public BlockMangroveRoots() {
        super(Material.wood);
        setLightOpacity(1);
        setResistance(0.7F);
        setHardness(0.7F);
        setNames("mangrove_roots");
        setBlockSound(ModSounds.soundMangroveRoots);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side < 2) {
            return blockIcon;
        }
        return sideIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(getTextureName() + "_top");
        sideIcon = reg.registerIcon(getTextureName() + "_side");
    }

    @Override
    public int getRenderType() {
        return RenderIDs.MANGROVE_ROOTS;
    }
}
