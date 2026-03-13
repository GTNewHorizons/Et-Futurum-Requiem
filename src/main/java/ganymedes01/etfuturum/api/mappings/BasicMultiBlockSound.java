package ganymedes01.etfuturum.api.mappings;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

import ganymedes01.etfuturum.api.MultiBlockSoundRegistry;

public class BasicMultiBlockSound extends MultiBlockSoundContainer {

    private final Map<Integer, Block.SoundType> types = Maps.newHashMap();

    public BasicMultiBlockSound setTypes(Block.SoundType type, int... metas) {
        for (int meta : metas) {
            types.put(meta, type);
        }
        return this;
    }

    @Override
    public String getSound(World world, int x, int y, int z, String oldSoundIn,
        MultiBlockSoundRegistry.BlockSoundType type) {
        Block.SoundType soundType = types.get(world.getBlockMetadata(x, y, z));

        if (soundType == null) return null;

        return switch (type) {
            case WALK, HIT -> soundType.getStepResourcePath();
            case PLACE -> soundType.func_150496_b(); // getPlaceSound
            default -> soundType.getBreakSound();
        };
    }
}
