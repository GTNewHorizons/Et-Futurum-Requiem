package ganymedes01.etfuturum.world.generate.structure;

import java.util.Random;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.structurenbt.BlockStateContainer;
import ganymedes01.etfuturum.core.utils.structurenbt.EFRBlockStateConverter;
import ganymedes01.etfuturum.core.utils.structurenbt.NBTStructure;
import ganymedes01.etfuturum.entities.EntityShulker;

public class WorldGenNBTStructureTesting extends WorldGenerator {

    /**
     * NOTE: DO NOT USE THE STRUCTURE NBT SYSTEM THIS WAY! CREATE THE INSTANCE AND THEN CALL PLACESTRUCTURE ON THE SAME
     * INSTANCE!
     * We create a new instance every time for the sake of making hot swapping easier, you definitely want to STORE A
     * SINGLE INSTANCE FOR YOUR STRUCTURES!
     * Creating a new NBT structure is very expensive, it reads a file and bakes four or more entire maps of block data.
     */
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        try {
            NBTStructure test = new Test();

            BlockPos pos = test.getSize(ForgeDirection.NORTH);
            test.placeStructure(world, rand, x + 1, y + 1, z + 1, ForgeDirection.NORTH);
            world.setBlock(x + 1, y + pos.getY() + 1, z + 1, Blocks.nether_brick);

            pos = test.getSize(ForgeDirection.SOUTH);
            test.placeStructure(world, rand, x - pos.getX() - 1, y + 1, z - pos.getZ() - 1, ForgeDirection.SOUTH);
            world.setBlock(x - pos.getX(), y + pos.getY() + 1, z - pos.getZ(), Blocks.sponge);

            pos = test.getSize(ForgeDirection.WEST);
            test.placeStructure(world, rand, x + 1, y + 1, z - pos.getZ(), ForgeDirection.WEST);
            world.setBlock(x + 1, y + pos.getY() + 1, z - pos.getZ(), Blocks.wool);

            pos = test.getSize(ForgeDirection.EAST);
            test.placeStructure(world, rand, x - pos.getX() - 1, y + 1, z, ForgeDirection.EAST);
            world.setBlock(x - pos.getX(), y + pos.getY() + 1, z + 1, Blocks.end_stone);
            return true;
        } catch (Exception e) { // In case we make a mistake when specifying a file name, just so the game won't crash
            e.printStackTrace();
            return false;
        }
    }

    private class Test extends NBTStructure {

        public Test() {
            super("/data/structure/shipwreck/rightsideup_backhalf_new.nbt", EFRBlockStateConverter.INSTANCE);
        }

        @Override
        public BlockStateContainer setStructureBlockAction(BlockPos pos, BlockStateContainer below, String data,
            ForgeDirection dir) {
            if (data.equals("Elytra")) {
                NBTTagCompound comp = new NBTTagCompound();
                comp.setByte("Direction", (byte) (converter.getItemFrameRotationFromDir(dir)));
                return new BlockStateContainer(EntityItemFrame.class, comp);
            }
            if (data.equals("Chest")) {
                below.setLootTable(ChestGenHooks.getInfo("netherFortress"));
            }
            if (data.equals("Sentry")) {
                NBTTagCompound comp = new NBTTagCompound();
                comp.setByte("Color", (byte) 16);
                return new BlockStateContainer(EntityShulker.class, comp);
            }
            return new BlockStateContainer(Blocks.air, 0);
        }
    }
}
