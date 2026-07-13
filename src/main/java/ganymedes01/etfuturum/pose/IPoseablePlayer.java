package ganymedes01.etfuturum.pose;

public interface IPoseablePlayer extends IPlayerScalable {
    IPlayerPose etfu$getPose();

    void etfu$setPose(IPlayerPose pose);

    float etfu$getCurrentYOffset();

    void etfu$setCurrentYOffset(float offset);
}