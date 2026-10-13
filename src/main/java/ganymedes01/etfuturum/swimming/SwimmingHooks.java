package ganymedes01.etfuturum.swimming;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;

public final class SwimmingHooks {
	private SwimmingHooks() {
	}

	public static boolean isEnabled() {
		return ConfigMixins.enableModernSwimming && isDataWatcherFlagAvailable();
	}

	public static boolean isDataWatcherFlagAvailable() {
		int flag = ConfigFunctions.swimmingDataWatcherFlag;
		return flag >= 6 && flag <= 31
				&& (!ConfigMixins.enableElytra || flag != ConfigFunctions.elytraDataWatcherFlag);
	}
}
