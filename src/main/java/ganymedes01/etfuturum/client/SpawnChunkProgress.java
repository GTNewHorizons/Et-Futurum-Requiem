package ganymedes01.etfuturum.client;

import java.util.concurrent.ConcurrentHashMap;

public class SpawnChunkProgress {

    public static final byte EMPTY = 0;
    public static final byte GENERATED = 1;
    public static final byte POPULATED = 2;

    private static final int COLOR_EMPTY = 0xFF545454;
    private static final int COLOR_GENERATED = 0xFF80B252;
    private static final int COLOR_POPULATED = 0xFFFFFFFF;
    private static final float SPAWN_CHUNK_COUNT = 625.0F;

    private static volatile boolean active;
    private static volatile float lastProgress;
    private static volatile int spawnChunkX;
    private static volatile int spawnChunkZ;
    private static final ConcurrentHashMap<Long, Byte> chunks = new ConcurrentHashMap<>();

    public static void begin(int spawnBlockX, int spawnBlockZ) {
        chunks.clear();
        lastProgress = 0;
        spawnChunkX = spawnBlockX >> 4;
        spawnChunkZ = spawnBlockZ >> 4;
        active = true;
    }

    public static void end() {
        active = false;
    }

    public static void markGenerated(int chunkX, int chunkZ) {
        if (active) {
            chunks.putIfAbsent(key(chunkX, chunkZ), GENERATED);
        }
    }

    public static void markPopulated(int chunkX, int chunkZ) {
        if (active) {
            chunks.put(key(chunkX, chunkZ), POPULATED);
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static int getColor(int chunkX, int chunkZ) {
        Byte state = chunks.get(key(chunkX, chunkZ));
        if (state == null) return COLOR_EMPTY;
        switch (state) {
            case GENERATED: return COLOR_GENERATED;
            case POPULATED: return COLOR_POPULATED;
            default: return COLOR_EMPTY;
        }
    }

    public static int getSpawnChunkX() {
        return spawnChunkX;
    }

    public static int getSpawnChunkZ() {
        return spawnChunkZ;
    }

    public static void setProgress(float progress) {
        lastProgress = progress;
    }

    public static boolean hasData() {
        return !chunks.isEmpty() || lastProgress > 0;
    }

    public static float getProgress() {
        float chunkProgress;
        if (chunks.isEmpty()) {
            chunkProgress = 0;
        } else if (!active) {
            chunkProgress = 1.0F;
        } else {
            chunkProgress = Math.min(1.0F, chunks.size() / SPAWN_CHUNK_COUNT);
        }
        return Math.max(lastProgress, chunkProgress);
    }

    public static void reset() {
        active = false;
        lastProgress = 0;
        chunks.clear();
    }

    private static long key(int x, int z) {
        return ((long) x & 0xFFFFFFFFL) | (((long) z & 0xFFFFFFFFL) << 32);
    }
}
