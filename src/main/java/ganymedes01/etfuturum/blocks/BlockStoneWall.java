package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockStoneWall extends BaseWall {

    private IIcon[] sandstoneSides;

    public BlockStoneWall() {
        super(Material.rock, "stone_bricks", "mossy_stone_bricks", "sandstone", "brick");
        setHardnesses(Blocks.stonebrick.blockHardness, 0, 1);
        setResistances(Blocks.stonebrick.blockResistance, 0, 1);
        setHardnesses(Blocks.sandstone.blockHardness, 2);
        setResistances(Blocks.sandstone.blockResistance, 2);
        setHardnesses(Blocks.brick_block.blockHardness, 3);
        setResistances(Blocks.brick_block.blockResistance, 3);
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        setIcons(new IIcon[getTypes().length]);
        sandstoneSides = new IIcon[2];
        getIcons()[0] = Blocks.stonebrick.getIcon(0, 0);
        getIcons()[1] = Blocks.stonebrick.getIcon(0, 1);
        getIcons()[2] = Blocks.sandstone.getIcon(2, 0);
        sandstoneSides[0] = Blocks.sandstone.getIcon(0, 0);
        sandstoneSides[1] = Blocks.sandstone.getIcon(1, 0);
        getIcons()[3] = Blocks.brick_block.getIcon(0, 0);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 2 && side <= 1) {
            return sandstoneSides[side];
        }
        return super.getIcon(side, meta);
    }
}
