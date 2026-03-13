package ganymedes01.etfuturum.blocks;

import java.util.Random;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.world.World;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.lib.GUIIDs;
import ganymedes01.etfuturum.tileentities.TileEntityNewBrewingStand;

public class BlockNewBrewingStand extends BlockBrewingStand {

    public BlockNewBrewingStand() {
        setHardness(0.5F);
        setLightLevel(0.125F);
        setBlockTextureName("brewing_stand");
        setCreativeTab(ConfigWorld.tileReplacementMode == -1 ? EtFuturum.creativeTabBlocks : null);
    }

    @Override
    public String getUnlocalizedName() {
        return Items.brewing_stand.getUnlocalizedName();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntityBrewingStand tile = (TileEntityBrewingStand) world.getTileEntity(x, y, z);
        if (tile != null) player.openGui(EtFuturum.instance, GUIIDs.BREWING_STAND, world, x, y, z);

        return true;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        if (ConfigWorld.tileReplacementMode == -1) return Item.getItemFromBlock(ModBlocks.BREWING_STAND.get());
        return Items.brewing_stand;
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        if (ConfigWorld.tileReplacementMode == -1) return Item.getItemFromBlock(ModBlocks.BREWING_STAND.get());
        return Items.brewing_stand;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityNewBrewingStand();
    }

    @Override
    public String getItemIconName() {
        return "brewing_stand";
    }
}
