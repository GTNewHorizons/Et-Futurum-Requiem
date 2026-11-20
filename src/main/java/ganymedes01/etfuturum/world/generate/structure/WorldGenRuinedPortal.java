package ganymedes01.etfuturum.world.generate.structure;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockBubbleColumn;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.structurenbt.BlockStateContainer;
import ganymedes01.etfuturum.core.utils.structurenbt.EFRBlockStateConverter;
import ganymedes01.etfuturum.core.utils.structurenbt.NBTStructure;
import ganymedes01.etfuturum.world.EtFuturumWorldListener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Overworld ruined portals. Setups, probabilities and structures come from the 1.21.1 data pack
 * (worldgen/structure/ruined_portal*.json) and from RuinedPortalStructure / RuinedPortalPiece.
 */
public class WorldGenRuinedPortal extends WorldGenerator {

    public static final String RUINED_PORTAL_LOOT = "ruined_portal";

    private static final String LOOT_TABLE_ID = "minecraft:chests/ruined_portal";
    private static final int PORTAL_COUNT = 10;
    private static final int GIANT_PORTAL_COUNT = 3;
    private static final float GIANT_PORTAL_CHANCE = 0.05F;
    private static final int MIN_Y = 15;
    private static final float COLD_TEMPERATURE = 0.15F;
    // Chance to place netherrack by distance (Manhattan) from the portal center
    private static final float[] SPREAD_PROBABILITY = {1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};

    private final List<PortalStructure> portals = new ArrayList<>();
    private final List<PortalStructure> giantPortals = new ArrayList<>();

    public WorldGenRuinedPortal() {
        for (int i = 1; i <= PORTAL_COUNT; i++) {
            portals.add(new PortalStructure("/data/structure/ruined_portal/portal_" + i + ".nbt"));
        }
        for (int i = 1; i <= GIANT_PORTAL_COUNT; i++) {
            giantPortals.add(new PortalStructure("/data/structure/ruined_portal/giant_portal_" + i + ".nbt"));
        }
    }

    /**
     * Returns whether a portal can generate in this biome. Used by /locate.
     */
    public static boolean canGenerateIn(BiomeGenBase biome) {
        return Variant.of(biome) != null;
    }

    /**
     * Places a portal centered on the given column. The structure spans at most 16 blocks, so a center picked
     * at chunk offset +8 keeps every placed block inside the four chunks that are loaded during population.
     */
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        Variant variant = Variant.of(biome);
        if (variant == null) return false;

        Setup setup = variant.pickSetup(rand);
        boolean airPocket = sample(rand, setup.airPocketProbability);
        List<PortalStructure> pool = rand.nextFloat() < GIANT_PORTAL_CHANCE ? giantPortals : portals;
        PortalStructure structure = pool.get(rand.nextInt(pool.size()));
        ForgeDirection dir = ForgeDirection.getOrientation(rand.nextInt(4) + 2);

        BlockPos size = structure.getSize(dir);
        int minX = x - size.getX() / 2;
        int minZ = z - size.getZ() / 2;
        int maxX = minX + size.getX() - 1;
        int maxZ = minZ + size.getZ() - 1;

        boolean oceanFloor = setup.placement == Placement.ON_OCEAN_FLOOR;
        int height = surfaceTop(world, x, z, oceanFloor);
        int placeY = findSuitableY(world, rand, setup.placement, height, size.getY(), minX, maxX, minZ, maxZ);
        if (placeY < 1 || placeY + size.getY() > world.getActualHeight()) return false;

        boolean cold = setup.canBeCold && biome.getFloatTemperature(x, placeY, z) < COLD_TEMPERATURE;
        Build build = new Build(world, rand, setup, airPocket, cold, x, z, minX, placeY, minZ, maxX, placeY + size.getY() - 1, maxZ);

        structure.build = build;
        try {
            structure.placeStructure(world, rand, minX, placeY, minZ, dir);
        } finally {
            structure.build = null;
        }
        build.spreadNetherrack();
        build.addDripColumns();
        build.addVinesAndLeaves();
        build.addBubbleColumns();
        return true;
    }

    private static boolean sample(Random rand, float probability) {
        if (probability == 0.0F) return false;
        if (probability == 1.0F) return true;
        return rand.nextFloat() < probability;
    }

    private static int randomWithinInterval(Random rand, int min, int max) {
        return min < max ? min + rand.nextInt(max - min + 1) : max;
    }

    /**
     * Picks the Y of the portal's bottom layer. Starts from the placement-specific height and moves down
     * until at least three of the four footprint corners rest on the ground.
     */
    private static int findSuitableY(World world, Random rand, Placement placement, int height, int ySpan, int minX, int maxX, int minZ, int maxZ) {
        int y;
        switch (placement) {
            case IN_MOUNTAIN:
                y = randomWithinInterval(rand, 70, height - ySpan);
                break;
            case UNDERGROUND:
                y = randomWithinInterval(rand, MIN_Y, height - ySpan);
                break;
            case PARTLY_BURIED:
                y = height - ySpan + 2 + rand.nextInt(7);
                break;
            default:
                y = height;
                break;
        }
        boolean oceanFloor = placement == Placement.ON_OCEAN_FLOOR;
        while (y > MIN_Y) {
            int grounded = 0;
            if (isGround(world, minX, y, minZ, oceanFloor)) grounded++;
            if (isGround(world, maxX, y, minZ, oceanFloor)) grounded++;
            if (isGround(world, minX, y, maxZ, oceanFloor)) grounded++;
            if (isGround(world, maxX, y, maxZ, oceanFloor)) grounded++;
            if (grounded >= 3) break;
            y--;
        }
        return y;
    }

    /**
     * Ground for the surface heightmap is anything but air, plants, leaves and logs (liquids count).
     * Ground for the ocean floor heightmap is a solid block.
     */
    private static boolean isGround(World world, int x, int y, int z, boolean oceanFloor) {
        Block block = world.getBlock(x, y, z);
        if (block.isAir(world, x, y, z) || block.isLeaves(world, x, y, z) || block.isWood(world, x, y, z)) return false;
        if (block.getMaterial().isLiquid()) return !oceanFloor;
        return block.getMaterial().blocksMovement() && !block.getMaterial().isReplaceable();
    }

    private static int surfaceTop(World world, int x, int z, boolean oceanFloor) {
        for (int y = Math.min(world.getHeightValue(x, z), world.getActualHeight() - 1); y > 0; y--) {
            if (isGround(world, x, y, z, oceanFloor)) return y;
        }
        return 0;
    }

    private static boolean isProtected(Block block) {
        return block == Blocks.bedrock || block == Blocks.mob_spawner || block == Blocks.chest || block == Blocks.end_portal_frame;
    }

    private static boolean isLava(Block block) {
        return block == Blocks.lava || block == Blocks.flowing_lava;
    }

    private static Block magmaOrNetherrack() {
        return ModBlocks.MAGMA.isEnabled() ? ModBlocks.MAGMA.get() : Blocks.netherrack;
    }

    private enum Placement {
        ON_LAND_SURFACE, PARTLY_BURIED, ON_OCEAN_FLOOR, IN_MOUNTAIN, UNDERGROUND
    }

    private static final class Setup {
        final Placement placement;
        final float weight;
        final float airPocketProbability;
        final float mossiness;
        final boolean canBeCold;
        final boolean overgrown;
        final boolean vines;

        Setup(Placement placement, float weight, float airPocketProbability, float mossiness, boolean canBeCold, boolean overgrown, boolean vines) {
            this.placement = placement;
            this.weight = weight;
            this.airPocketProbability = airPocketProbability;
            this.mossiness = mossiness;
            this.canBeCold = canBeCold;
            this.overgrown = overgrown;
            this.vines = vines;
        }
    }

    /**
     * The overworld structure variants of the 1.21 structure set, minus the nether one.
     */
    private enum Variant {
        STANDARD(new Setup(Placement.UNDERGROUND, 0.5F, 1.0F, 0.2F, true, false, false),
            new Setup(Placement.ON_LAND_SURFACE, 0.5F, 0.5F, 0.2F, true, false, false)),
        DESERT(new Setup(Placement.PARTLY_BURIED, 1.0F, 0.0F, 0.0F, false, false, false)),
        JUNGLE(new Setup(Placement.ON_LAND_SURFACE, 1.0F, 0.5F, 0.8F, false, true, true)),
        MOUNTAIN(new Setup(Placement.IN_MOUNTAIN, 0.5F, 1.0F, 0.2F, true, false, false),
            new Setup(Placement.ON_LAND_SURFACE, 0.5F, 0.5F, 0.2F, true, false, false)),
        OCEAN(new Setup(Placement.ON_OCEAN_FLOOR, 1.0F, 0.0F, 0.8F, true, false, false)),
        SWAMP(new Setup(Placement.ON_OCEAN_FLOOR, 1.0F, 0.0F, 0.5F, false, false, true));

        private final Setup[] setups;

        Variant(Setup... setups) {
            this.setups = setups;
        }

        Setup pickSetup(Random rand) {
            if (setups.length == 1) return setups[0];
            float total = 0.0F;
            for (Setup setup : setups) {
                total += setup.weight;
            }
            float roll = rand.nextFloat();
            for (Setup setup : setups) {
                roll -= setup.weight / total;
                if (roll < 0.0F) return setup;
            }
            return setups[setups.length - 1];
        }

        /**
         * Maps a 1.7.10 biome to the variant whose 1.21 biome tag it corresponds to. Null if portals do not generate there.
         */
        static Variant of(BiomeGenBase biome) {
            if (biome == null) return null;
            if (is(biome, Type.NETHER) || is(biome, Type.END)) return null;
            if (is(biome, Type.OCEAN) && !is(biome, Type.RIVER)) return OCEAN;
            if (is(biome, Type.SWAMP)) return SWAMP;
            if (is(biome, Type.JUNGLE)) return JUNGLE;
            if (biome == BiomeGenBase.desert) return DESERT;
            // is_mountain, is_hill (windswept), is_badlands, savanna plateau, windswept savanna, stony shore
            if (is(biome, Type.MOUNTAIN) || is(biome, Type.MESA) || biome == BiomeGenBase.savannaPlateau
                || biome == BiomeGenBase.stoneBeach || biome.biomeID == 163 || biome.biomeID == 164) return MOUNTAIN;
            if (is(biome, Type.BEACH) || is(biome, Type.RIVER) || is(biome, Type.CONIFEROUS) || is(biome, Type.FOREST)
                || is(biome, Type.MUSHROOM) || is(biome, Type.SAVANNA) || is(biome, Type.PLAINS) || is(biome, Type.SNOWY)) return STANDARD;
            return null;
        }

        private static boolean is(BiomeGenBase biome, Type type) {
            return BiomeDictionary.isBiomeOfType(biome, type);
        }
    }

    /**
     * State of a single portal placement: block replacement while the template is placed, and the post-processing around it.
     */
    private static final class Build {
        final World world;
        final Random rand;
        final Setup setup;
        final boolean airPocket;
        final boolean cold;
        final int centerX;
        final int centerZ;
        final int minX, minY, minZ, maxX, maxY, maxZ;
        // Only the four chunks around the population origin are guaranteed to be loaded
        final int areaMinX;
        final int areaMinZ;
        final List<int[]> magmaBlocks = new ArrayList<>();

        Build(World world, Random rand, Setup setup, boolean airPocket, boolean cold, int centerX, int centerZ,
              int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.world = world;
            this.rand = rand;
            this.setup = setup;
            this.airPocket = airPocket;
            this.cold = cold;
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.areaMinX = ((centerX - 8) >> 4) << 4;
            this.areaMinZ = ((centerZ - 8) >> 4) << 4;
        }

        boolean inArea(int x, int z) {
            return x >= areaMinX && x < areaMinX + 32 && z >= areaMinZ && z < areaMinZ + 32;
        }

        /**
         * Applies the portal's block rules, then ageing, to one template block. Returns null if the block is skipped.
         */
        BlockStateContainer process(BlockStateContainer state, int x, int y, int z) {
            Block block = state.getBlock();
            int meta = state.getMeta();
            BlockStateContainer result = state;

            if (block == Blocks.air && !airPocket) return null;

            if (block == Blocks.gold_block && rand.nextFloat() < 0.3F) {
                result = new BlockStateContainer(Blocks.air);
            } else if (isLava(block)) {
                if (setup.placement == Placement.ON_OCEAN_FLOOR) {
                    result = new BlockStateContainer(magmaOrNetherrack());
                } else if (cold) {
                    result = new BlockStateContainer(Blocks.netherrack);
                } else if (rand.nextFloat() < 0.2F) {
                    result = new BlockStateContainer(magmaOrNetherrack());
                }
            } else if (block == Blocks.netherrack && !cold && rand.nextFloat() < 0.07F) {
                result = new BlockStateContainer(magmaOrNetherrack());
            }

            if (result == state) {
                BlockStateContainer aged = age(block, meta);
                if (aged != null) result = aged;
            }

            if (isProtected(world.getBlock(x, y, z))) return null;
            // Lava keeps flowing into spaces that a non-full block leaves open
            if (isLava(world.getBlock(x, y, z)) && !result.getBlock().isOpaqueCube()) {
                return new BlockStateContainer(Blocks.lava);
            }
            if (isMagma(result.getBlock())) magmaBlocks.add(new int[]{x, y, z});
            return result;
        }

        private static boolean isMagma(Block block) {
            return ModBlocks.MAGMA.isEnabled() && block == ModBlocks.MAGMA.get();
        }

        private BlockStateContainer age(Block block, int meta) {
            if ((block == Blocks.stone && meta == 0) || (block == Blocks.stonebrick && (meta == 0 || meta == 3))) {
                if (rand.nextFloat() >= 0.5F) return null;
                boolean mossy = rand.nextFloat() < setup.mossiness;
                if (rand.nextInt(2) == 0) {
                    return new BlockStateContainer(Blocks.stonebrick, mossy ? 1 : 2);
                }
                return new BlockStateContainer(mossy ? mossyStairs() : Blocks.stone_brick_stairs, rand.nextInt(4) + (rand.nextBoolean() ? 4 : 0));
            }
            if (block == Blocks.stone_brick_stairs) {
                if (rand.nextFloat() >= 0.5F) return null;
                if (rand.nextFloat() < setup.mossiness) {
                    return rand.nextInt(2) == 0 ? new BlockStateContainer(mossyStairs(), meta) : new BlockStateContainer(Blocks.stone_slab, 5);
                }
                return new BlockStateContainer(Blocks.stone_slab, rand.nextInt(2) == 0 ? 0 : 5);
            }
            if (block == Blocks.cobblestone_wall && meta == 0) {
                return rand.nextFloat() < setup.mossiness ? new BlockStateContainer(block, 1) : null;
            }
            if (block == Blocks.obsidian) {
                return rand.nextFloat() < 0.15F && ModBlocks.CRYING_OBSIDIAN.isEnabled() ? new BlockStateContainer(ModBlocks.CRYING_OBSIDIAN.get()) : null;
            }
            return null;
        }

        private static Block mossyStairs() {
            return ModBlocks.MOSSY_STONE_BRICK_STAIRS.isEnabled() ? ModBlocks.MOSSY_STONE_BRICK_STAIRS.get() : Blocks.stone_brick_stairs;
        }

        /**
         * Spreads netherrack (and magma unless cold) around the portal, fading with distance.
         */
        void spreadNetherrack() {
            boolean oceanFloor = setup.placement == Placement.ON_OCEAN_FLOOR;
            boolean followSurface = setup.placement == Placement.ON_LAND_SURFACE || oceanFloor;
            int spanSum = (maxX - minX + 1 + maxZ - minZ + 1) / 2;
            int offset = rand.nextInt(Math.max(1, 8 - spanSum / 2));
            int range = SPREAD_PROBABILITY.length;
            for (int x = centerX - range; x <= centerX + range; x++) {
                for (int z = centerZ - range; z <= centerZ + range; z++) {
                    int index = Math.max(0, Math.abs(x - centerX) + Math.abs(z - centerZ) + offset);
                    if (index >= range || !inArea(x, z)) continue;
                    if (rand.nextDouble() < SPREAD_PROBABILITY[index]) {
                        int top = surfaceTop(world, x, z, oceanFloor);
                        int y = followSurface ? top : Math.min(minY, top);
                        if (Math.abs(y - minY) <= 3 && canReplace(x, y, z)) {
                            placeNetherrackOrMagma(x, y, z);
                            if (setup.overgrown) maybeAddLeavesAbove(x, y, z);
                            addDripColumn(x, y - 1, z);
                        }
                    }
                }
            }
        }

        private boolean canReplace(int x, int y, int z) {
            Block block = world.getBlock(x, y, z);
            return !block.isAir(world, x, y, z) && block != Blocks.obsidian && !isProtected(block) && !isLava(block);
        }

        private void placeNetherrackOrMagma(int x, int y, int z) {
            if (y < 1 || y >= world.getActualHeight() || !inArea(x, z)) return;
            Block block = !cold && rand.nextFloat() < 0.07F ? magmaOrNetherrack() : Blocks.netherrack;
            world.setBlock(x, y, z, block, 0, 2);
            if (isMagma(block)) magmaBlocks.add(new int[]{x, y, z});
        }

        /**
         * The world listener that starts bubble columns does not fire while a chunk is being populated,
         * so the first column block above each magma block under water is placed here.
         */
        void addBubbleColumns() {
            if (!ModBlocks.BUBBLE_COLUMN.isEnabled()) return;
            for (int[] pos : magmaBlocks) {
                Integer meta = EtFuturumWorldListener.bubbleColumnMap.get(world.getBlock(pos[0], pos[1], pos[2]));
                if (meta != null && inArea(pos[0] - 1, pos[2] - 1) && inArea(pos[0] + 1, pos[2] + 1)
                    && BlockBubbleColumn.isFullVanillaWater(world.getBlock(pos[0], pos[1] + 1, pos[2]), world.getBlockMetadata(pos[0], pos[1] + 1, pos[2]))) {
                    world.setBlock(pos[0], pos[1] + 1, pos[2], ModBlocks.BUBBLE_COLUMN.get(), meta, 2);
                }
            }
        }

        private void addDripColumn(int x, int y, int z) {
            placeNetherrackOrMagma(x, y, z);
            for (int left = 8; left > 0 && rand.nextFloat() < 0.5F; left--) {
                y--;
                placeNetherrackOrMagma(x, y, z);
            }
        }

        /**
         * Hangs netherrack columns under netherrack of the portal's bottom layer.
         */
        void addDripColumns() {
            for (int x = minX + 1; x < maxX; x++) {
                for (int z = minZ + 1; z < maxZ; z++) {
                    if (world.getBlock(x, minY, z) == Blocks.netherrack) {
                        addDripColumn(x, minY - 1, z);
                    }
                }
            }
        }

        void addVinesAndLeaves() {
            if (!setup.vines && !setup.overgrown) return;
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        if (setup.vines) maybeAddVines(x, y, z);
                        if (setup.overgrown) maybeAddLeavesAbove(x, y, z);
                    }
                }
            }
        }

        private void maybeAddVines(int x, int y, int z) {
            Block block = world.getBlock(x, y, z);
            if (block.isAir(world, x, y, z) || block == Blocks.vine) return;
            ForgeDirection dir = ForgeDirection.getOrientation(rand.nextInt(4) + 2);
            int vineX = x + dir.offsetX;
            int vineZ = z + dir.offsetZ;
            if (!inArea(vineX, vineZ) || !world.isAirBlock(vineX, y, vineZ) || !block.isSideSolid(world, x, y, z, dir)) return;
            // The vine's meta bit names the side of the block it hangs on
            int bit;
            switch (dir.getOpposite()) {
                case SOUTH:
                    bit = 1;
                    break;
                case WEST:
                    bit = 2;
                    break;
                case NORTH:
                    bit = 4;
                    break;
                default:
                    bit = 8;
                    break;
            }
            world.setBlock(vineX, y, vineZ, Blocks.vine, bit, 2);
        }

        private void maybeAddLeavesAbove(int x, int y, int z) {
            if (rand.nextFloat() < 0.5F && world.getBlock(x, y, z) == Blocks.netherrack && world.isAirBlock(x, y + 1, z)) {
                // Jungle leaves that do not decay
                world.setBlock(x, y + 1, z, Blocks.leaves, 3 | 4, 2);
            }
        }
    }

    private static class PortalStructure extends NBTStructure {

        Build build;

        PortalStructure(String loc) {
            super(loc, EFRBlockStateConverter.INSTANCE);
        }

        // Jigsaw blocks in the templates only mark a base block; swap them for their final_state before the maps are built
        @Override
        public void init() {
            NBTTagList palette = getCompound().getTagList("palette", 10);
            NBTTagList blocks = getCompound().getTagList("blocks", 10);
            for (int i = 0; i < blocks.tagCount(); i++) {
                NBTTagCompound block = blocks.getCompoundTagAt(i);
                if (!block.hasKey("nbt", 10) || !"minecraft:jigsaw".equals(block.getCompoundTag("nbt").getString("id"))) continue;
                block.setInteger("state", paletteIndex(palette, block.getCompoundTag("nbt").getString("final_state")));
                block.removeTag("nbt");
            }
        }

        private static int paletteIndex(NBTTagList palette, String name) {
            for (int i = 0; i < palette.tagCount(); i++) {
                NBTTagCompound entry = palette.getCompoundTagAt(i);
                if (entry.getString("Name").equals(name) && !entry.hasKey("Properties")) return i;
            }
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("Name", name);
            palette.appendTag(entry);
            return palette.tagCount() - 1;
        }

        @Override
        public void getNBTAction(BlockPos pos, BlockStateContainer state, NBTTagCompound modernCompound, ForgeDirection facing) {
            if (LOOT_TABLE_ID.equals(modernCompound.getString("LootTable"))) {
                state.setLootTable(ChestGenHooks.getInfo(RUINED_PORTAL_LOOT));
            }
        }

        @Override
        protected void setBlockState(World world, Random rand, int x, int y, int z, BlockStateContainer state, float integrity) {
            BlockStateContainer processed = build.process(state, x, y, z);
            if (processed != null) {
                super.setBlockState(world, rand, x, y, z, processed, integrity);
            }
        }
    }
}
