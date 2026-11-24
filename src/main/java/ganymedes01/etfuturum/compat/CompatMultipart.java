package ganymedes01.etfuturum.compat;

import codechicken.multipart.minecraft.ButtonPart;
import net.minecraftforge.common.util.ForgeDirection;

/** Only load after checking that ForgeMultipart 2.0.0+ is present. */
public final class CompatMultipart {

	public static void init() {
		// Floor and ceiling button orientations
		ButtonPart.setOrientation(0, ForgeDirection.UP);
		ButtonPart.setOrientation(5, ForgeDirection.DOWN);
	}
}
