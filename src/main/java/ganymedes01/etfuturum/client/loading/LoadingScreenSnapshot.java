package ganymedes01.etfuturum.client.loading;

public class LoadingScreenSnapshot {

    private final String title;
    private final String subtitle;
    private final float progress;
    private final boolean done;
    private final LoadingScreenChunkSnapshot chunkSnapshot;

    public LoadingScreenSnapshot(String title, String subtitle, float progress, boolean done,
                                 LoadingScreenChunkSnapshot chunkSnapshot) {
        this.title = title;
        this.subtitle = subtitle;
        this.progress = progress;
        this.done = done;
        this.chunkSnapshot = chunkSnapshot;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public float getProgress() {
        return progress;
    }

    public boolean isDone() {
        return done;
    }

    public LoadingScreenChunkSnapshot getChunkSnapshot() {
        return chunkSnapshot;
    }

    public boolean hasChunkSnapshot() {
        return chunkSnapshot != null;
    }
}
