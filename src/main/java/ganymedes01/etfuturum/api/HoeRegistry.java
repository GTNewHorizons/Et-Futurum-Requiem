package ganymedes01.etfuturum.api;

import java.util.ArrayList;

import net.minecraft.block.Block;

public class HoeRegistry {

    private static final ArrayList<Block> hoeBlocks = new ArrayList<>();

    public static void addToHoeArray(Block block) {
        hoeBlocks.add(block);
    }

    public static boolean hoeArrayHas(Block block) {
        return hoeBlocks.contains(block);
    }

    public static ArrayList<Block> getHoeArray() {
        return hoeBlocks;
    }
}
