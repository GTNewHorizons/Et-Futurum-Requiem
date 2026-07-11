package ganymedes01.etfuturum.swimming;

public interface IPlayerSwimming {
	boolean etfu$isSwimming();

	boolean etfu$isActuallySwimming();

	boolean etfu$isActuallySneaking();

	boolean etfu$isEyeInWater();

	PlayerPose etfu$getPose();

	float etfu$getSwimAnimation(float partialTicks);
}
