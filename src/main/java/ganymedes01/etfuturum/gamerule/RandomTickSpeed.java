package ganymedes01.etfuturum.gamerule;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class RandomTickSpeed {

	public static final RandomTickSpeed INSTANCE = new RandomTickSpeed();

	public static final String GAMERULE_NAME = "randomTickSpeed";
	public static final String DEFAULT_VALUE = "3";
	public int CURRENT_VALUE = -1;

	public int getRandomTickSpeed(GameRules gameRulesInstance) {
		if (RandomTickSpeed.INSTANCE.CURRENT_VALUE == -1) {
			RandomTickSpeed.INSTANCE.CURRENT_VALUE = Integer.parseInt(StringUtils.defaultIfEmpty(gameRulesInstance.getGameRuleStringValue(GAMERULE_NAME), DEFAULT_VALUE));
		}
		return RandomTickSpeed.INSTANCE.CURRENT_VALUE;
	}

	public static void registerGamerule(World world) {
		if (!world.isRemote && !world.getGameRules().hasRule(GAMERULE_NAME)) {
			world.getGameRules().addGameRule(GAMERULE_NAME, DEFAULT_VALUE);
		}
	}
}
