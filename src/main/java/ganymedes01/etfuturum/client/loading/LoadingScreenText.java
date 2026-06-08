package ganymedes01.etfuturum.client.loading;

import net.minecraft.client.resources.I18n;

public class LoadingScreenText {

    public static String getLoadingWorldTitle() {
        return I18n.format("menu.loadingLevel");
    }

    public static String getBuildingTerrainSubtitle() {
        return I18n.format("menu.generatingTerrain");
    }

    public static String getPreparingTerrainSubtitle() {
        return I18n.format("multiplayer.downloadingTerrain");
    }

    public static String getDownloadingTerrainTitle() {
        return I18n.format("multiplayer.downloadingTerrain");
    }
}
