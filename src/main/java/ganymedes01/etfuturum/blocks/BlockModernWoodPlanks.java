package ganymedes01.etfuturum.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;

public class BlockModernWoodPlanks extends BaseSubtypesBlock {

    public BlockModernWoodPlanks() {
        super(Material.wood, "crimson_planks", "warped_planks", "mangrove_planks", "cherry_planks", "bamboo_planks");
        setHardness(2.0F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (ConfigExperiments.enableCrimsonBlocks) {
            list.add(new ItemStack(item, 1, 0));
        }
        if (ConfigExperiments.enableWarpedBlocks) {
            list.add(new ItemStack(item, 1, 1));
        }
        if (ConfigExperiments.enableMangroveBlocks) {
            list.add(new ItemStack(item, 1, 2));
        }
        if (ConfigBlocksItems.enableCherryBlocks) {
            list.add(new ItemStack(item, 1, 3));
        }
        if (ConfigBlocksItems.enableBambooBlocks) {
            list.add(new ItemStack(item, 1, 4));
        }
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return aWorld.getBlockMetadata(aX, aY, aZ) % getTypes().length > 1;
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return isFlammable(aWorld, aX, aY, aZ, aSide) ? 20 : 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return isFlammable(aWorld, aX, aY, aZ, aSide) ? 5 : 0;
    }
}
