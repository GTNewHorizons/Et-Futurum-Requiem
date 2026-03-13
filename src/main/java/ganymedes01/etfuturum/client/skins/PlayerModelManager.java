package ganymedes01.etfuturum.client.skins;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import ganymedes01.etfuturum.lib.Reference;

public class PlayerModelManager {

    public static final String MODEL_KEY = Reference.MOD_ID + "_model";

    public static Map<UUID, Boolean> alexCache = new WeakHashMap<>();

    public static boolean isPlayerModelAlex(EntityPlayer player) {
        if (player == null || player.getUniqueID() == null) return false;

        Boolean isAlex = alexCache.get(player.getUniqueID());
        if (isAlex == null) {
            NBTTagCompound nbt = player.getEntityData();
            if (nbt.hasKey(MODEL_KEY, Constants.NBT.TAG_BYTE)) {
                nbt.removeTag(MODEL_KEY);
            }
            ThreadCheckAlex skinthread = new ThreadCheckAlex();
            skinthread.startWithArgs(player.getUniqueID());
            return false;
        }
        return isAlex;
    }
}
