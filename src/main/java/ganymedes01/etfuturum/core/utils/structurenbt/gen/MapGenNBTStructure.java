package ganymedes01.etfuturum.core.utils.structurenbt.gen;

import java.util.List;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;

import com.google.common.collect.Lists;

public abstract class MapGenNBTStructure extends MapGenStructure {

    protected final List<StructureNBTComponent> structurePieces = Lists.newArrayList();

    public MapGenNBTStructure() {
        registerStructurePieces();
        for (StructureNBTComponent struct : structurePieces) {
            MapGenStructureIO.func_143031_a/* registerStructureComponent */(struct.getClass(), struct.pieceName);
        }
    }

    /**
     * Add stuff to the structurePieces list here
     */
    protected abstract void registerStructurePieces();
}
