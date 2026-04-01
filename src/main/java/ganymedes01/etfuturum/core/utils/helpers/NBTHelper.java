package ganymedes01.etfuturum.core.utils.helpers;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class NBTHelper {

    /*
    Can be used to read NBT data from level.dat or playerdata files for example.
     */
    public static NBTTagCompound readNBTFromFile(File file) {

        NBTTagCompound nbt = null;

        try {

            nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));

        } catch (Exception e) {

            FMLLog.warning("There appears to be a problem writing the NBT to file: %s.", file.getAbsolutePath());
        }

        return nbt;
    }

    /*
    Can be used to write NBT data to level.dat or playerdata files for example.
     */
    public static void writeNBTToFile(NBTTagCompound nbt, File file) {

        try {

            CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));

        } catch (Exception e) {

            FMLLog.warning("There appears to be a problem writing the NBT to file: %s.", file.getAbsolutePath());
        }
    }
}
