package ganymedes01.etfuturum.gamerule;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayersSleepingPercentage {

    public static final PlayersSleepingPercentage INSTANCE = new PlayersSleepingPercentage();
    public static final String GAMERULE_NAME = "playersSleepingPercentage";
    public static final String DEFAULT_VALUE = "100";

    public int percentrillo = 100;
    public List<EntityPlayer> sleepyPlayers = new ArrayList<>();

    public static void registerGamerule(World world) {
        if (!world.isRemote && !world.getGameRules()
            .hasRule(GAMERULE_NAME)) {
            world.getGameRules()
                .addGameRule(GAMERULE_NAME, DEFAULT_VALUE);
        }
    }
}
