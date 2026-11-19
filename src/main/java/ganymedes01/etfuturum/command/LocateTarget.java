package ganymedes01.etfuturum.command;

import net.minecraft.world.World;

/**
 * A structure that /locate can search for. Register it with {@link CommandLocate#register(LocateTarget)}.
 */
public interface LocateTarget {

    /** The lowercase argument of /locate, also the prefix of the lang keys {@code commands.etfuturum.locate.<id>.*}. */
    String getId();

    /** Reads the config at call time, so the command reflects the current setting. */
    boolean isEnabled();

    /** If this returns false, /locate reports {@code <id>.overworldOnly}. */
    boolean isValidWorld(World world);

    /** Returns {x, z} in blocks of the structure that generates in this chunk, or null if there is none. */
    int[] findInChunk(World world, int chunkX, int chunkZ);
}
