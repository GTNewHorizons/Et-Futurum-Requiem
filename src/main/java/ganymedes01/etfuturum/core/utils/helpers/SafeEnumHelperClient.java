package ganymedes01.etfuturum.core.utils.helpers;

import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.util.EnumHelper;

public class SafeEnumHelperClient extends EnumHelper {

    private static final Class<?>[][] safeTypes = { { WorldSettings.GameType.class, int.class, String.class } };

    public static WorldSettings.GameType addGameType(String name, int id, String displayName) {
        return addEnum(WorldSettings.GameType.class, name, id, displayName);
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Object... paramValues) {
        return addEnum(safeTypes, enumType, enumName, paramValues);
    }
}
