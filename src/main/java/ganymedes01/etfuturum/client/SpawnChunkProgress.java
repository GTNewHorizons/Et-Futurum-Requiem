package ganymedes01.etfuturum.client;

import java.util.concurrent.ConcurrentHashMap;

public class SpawnChunkProgress {

    public static final byte EMPTY = 0;
    public static final byte GENERATED = 1;
    public static final byte POPULATED = 2;
    public static final int SPAWN_CHUNK_RADIUS = 12;

    private static final float SPAWN_CHUNK_COUNT = 625.0F;
    private static final ConcurrentHashMap<Long, Byte> chunks = new ConcurrentHashMap<Long, Byte>();
    private static volatile boolean active;
    private static volatile int spawnChunkX;
    private static volatile int spawnChunkZ;

    public static void begin(int spawnBlockX, int spawnBlockZ) {
        chunks.clear();
        spawnChunkX = spawnBlockX >> 4;
        spawnChunkZ = spawnBlockZ >> 4;
        active = true;
    }

    public static void end() {
        active = false;
    }

    public static void markGenerated(int chunkX, int chunkZ) {
        if (active) {
            chunks.putIfAbsent(key(chunkX, chunkZ), Byte.valueOf(GENERATED));
        }
    }

    public static void markPopulated(int chunkX, int chunkZ) {
        if (active) {
            chunks.put(key(chunkX, chunkZ), Byte.valueOf(POPULATED));
        }
    }

    public static int getSpawnChunkX() {
        return spawnChunkX;
    }

    public static int getSpawnChunkZ() {
        return spawnChunkZ;
    }

    public static float getProgress() {
        if (chunks.isEmpty()) {
            return 0.0F;
        }
        if (!active) {
            return 1.0F;
        }
        return Math.min(1.0F, chunks.size() / SPAWN_CHUNK_COUNT);
    }

    public static void reset() {
        active = false;
        chunks.clear();
    }

    private static long key(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }
}
