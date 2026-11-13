package ganymedes01.etfuturum.world.generate.structure;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.structurenbt.BlockStateContainer;
import ganymedes01.etfuturum.core.utils.structurenbt.EFRBlockStateConverter;
import ganymedes01.etfuturum.core.utils.structurenbt.NBTStructure;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenShipwreck extends WorldGenerator {

    public static final String SHIPWRECK_SUPPLY = "shipwreck_supply";
    public static final String SHIPWRECK_TREASURE = "shipwreck_treasure";
    public static final String SHIPWRECK_MAP = "shipwreck_map";

    private static final String[] VARIANTS = {
        "rightsideup_backhalf", "rightsideup_backhalf_degraded",
        "rightsideup_fronthalf", "rightsideup_fronthalf_degraded",
        "rightsideup_full", "rightsideup_full_degraded",
        "sideways_backhalf", "sideways_backhalf_degraded",
        "sideways_fronthalf", "sideways_fronthalf_degraded",
        "sideways_full", "sideways_full_degraded",
        "upsidedown_backhalf", "upsidedown_backhalf_degraded",
        "upsidedown_fronthalf", "upsidedown_fronthalf_degraded",
        "upsidedown_full", "upsidedown_full_degraded",
        "with_mast", "with_mast_degraded"
    };

    private final List<ShipwreckStructure> structures = new ArrayList<>();

    public WorldGenShipwreck() {
        for (String variant : VARIANTS) {
            structures.add(new ShipwreckStructure("/data/structure/shipwreck/" + variant + ".nbt"));
        }
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        ShipwreckStructure structure = structures.get(rand.nextInt(structures.size()));
        ForgeDirection dir = ForgeDirection.getOrientation(rand.nextInt(4) + 2);

        // Find the seafloor: go down from surface until we hit a solid/liquid block
        int seafloor = world.getTopSolidOrLiquidBlock(x, z);

        BlockPos size = structure.getSize(dir);
        // Place ship so its bottom is 2 blocks above the seafloor
        int placeY = seafloor - 2;
        // Ensure we're not clipping into bedrock
        if (placeY < 5) return false;

        structure.placeStructure(world, rand, x, placeY, z, dir);
        return true;
    }

    private static class ShipwreckStructure extends NBTStructure {

        public ShipwreckStructure(String loc) {
            super(loc, EFRBlockStateConverter.INSTANCE);
        }

        @Override
        protected void setBlockState(World world, Random rand, int x, int y, int z, BlockStateContainer state, float integrity) {
            Block block = state.getBlock();
            // MalisisDoors replaces vanilla doors and trapdoors with tile entity blocks. Reading the empty NBT into them
            // wipes their descriptor, which removes collision and selection, so place them without touching the tile entity.
            if (block instanceof BlockTrapDoor || block instanceof BlockDoor) {
                boolean wasWater = isWaterSource(world, x, y, z);
                world.setBlock(x, y, z, block, state.getMeta(), 2);
                fillWaterIfNeeded(world, x, y, z, wasWater);
                return;
            }
            super.setBlockState(world, rand, x, y, z, state, integrity);
        }

        // Structure blocks are placed directly above chests and contain the loot table identifier
        @Override
        public BlockStateContainer setStructureBlockAction(BlockPos pos, BlockStateContainer below, String data, ForgeDirection dir) {
            if (below == null) return null;
            ChestGenHooks hooks = null;
            switch (data) {
                case "supply_chest":
                    hooks = ChestGenHooks.getInfo(SHIPWRECK_SUPPLY);
                    break;
                case "treasure_chest":
                    hooks = ChestGenHooks.getInfo(SHIPWRECK_TREASURE);
                    break;
                case "map_chest":
                    hooks = ChestGenHooks.getInfo(SHIPWRECK_MAP);
                    break;
            }
            if (hooks != null) {
                below.setLootTable(hooks);
            }
            return null; // structure block itself should not be placed
        }
    }
}
