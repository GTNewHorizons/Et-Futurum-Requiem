package ganymedes01.etfuturum.client.loading;

public enum LoadingScreenChunkStage {
    EMPTY(0xFF545454),
    STRUCTURE_STARTS(0xFF999999),
    STRUCTURE_REFERENCES(0xFF5F6191),
    BIOMES(0xFF80B252),
    NOISE(0xFFD1D1D1),
    SURFACE(0xFF726809),
    CARVERS(0xFF303572),
    FEATURES(0xFF21C600),
    INITIALIZE_LIGHT(0xFFCCCCCC),
    LIGHT(0xFFFFE0A0),
    SPAWN(0xFFF26060),
    FULL(0xFFFFFFFF);

    private final int color;

    LoadingScreenChunkStage(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
