package ganymedes01.etfuturum.swimming;

public enum PlayerPose {
	STANDING(1.8F, 1.62F),
	CROUCHING(1.5F, 1.27F),
	SWIMMING(0.6F, 0.4F),
	CRAWLING(0.6F, 0.4F),
	FALL_FLYING(0.6F, 0.4F);

	public final float height;
	public final float eyeHeight;

	PlayerPose(float height, float eyeHeight) {
		this.height = height;
		this.eyeHeight = eyeHeight;
	}

	public float getCameraOffset() {
		return STANDING.eyeHeight - this.eyeHeight;
	}

	public boolean isLowProfile() {
		return this == SWIMMING || this == CRAWLING || this == FALL_FLYING;
	}

	public boolean usesModernEyeHeight() {
		return this == CROUCHING || this.isLowProfile();
	}
}
