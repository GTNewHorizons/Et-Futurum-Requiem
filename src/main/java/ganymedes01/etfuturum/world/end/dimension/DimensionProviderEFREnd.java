package ganymedes01.etfuturum.world.end.dimension;

import net.minecraftforge.common.DimensionManager;

public class DimensionProviderEFREnd {

    public static void init() {
        unregisterProviders();
        registerProviders();
        registerDimensions();
    }

    private static void unregisterProviders() {
        DimensionManager.unregisterProviderType(1);
    }

    private static void registerProviders() {
        DimensionManager.registerProviderType(1, WorldProviderEFREnd.class, true);
    }

    private static void registerDimensions() {}

}
