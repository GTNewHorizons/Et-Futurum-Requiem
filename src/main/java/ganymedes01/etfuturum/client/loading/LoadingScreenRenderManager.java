package ganymedes01.etfuturum.client.loading;

public class LoadingScreenRenderManager {

    private static final LoadingScreenRenderer RENDERER = new LoadingScreenRenderer();

    public static LoadingScreenRenderer getRenderer() {
        return RENDERER;
    }

    public static void reset() {
        RENDERER.resetProgress();
    }
}
