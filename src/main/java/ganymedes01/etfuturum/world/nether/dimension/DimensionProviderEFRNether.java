package ganymedes01.etfuturum.world.nether.dimension;

import net.minecraftforge.common.DimensionManager;

public class DimensionProviderEFRNether {

    public static void init() {
        unregisterProviders();
        registerProviders();
    }

    private static void unregisterProviders() {
        DimensionManager.unregisterProviderType(-1);
    }

    private static void registerProviders() {
        DimensionManager.registerProviderType(-1, WorldProviderEFRNether.class, true);
    }
}
