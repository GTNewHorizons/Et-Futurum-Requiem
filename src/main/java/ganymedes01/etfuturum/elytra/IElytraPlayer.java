package ganymedes01.etfuturum.elytra;

public interface IElytraPlayer extends ganymedes01.etfuturum.api.elytra.IElytraPlayer {

    void etfu$setElytraFlying(boolean flag);

    float etfu$getTicksElytraFlying();

    boolean etfu$lastElytraFlying();

    void etfu$setLastElytraFlying(boolean flag);

    void tickElytra();
}
