package ganymedes01.etfuturum.compat.nei;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.lib.Reference;

public class IMCSenderGTNH {

    public static void IMCSender() {

        if (ModBlocks.SMOKER.isEnabled()) {
            sendHandler("ganymedes01.etfuturum.compat.nei.SmokerRecipeHandler", "etfuturum:smoker");
            sendCatalyst("etfuturum.smoker", "etfuturum:smoker");

            sendCatalyst("fuel", "etfuturum:smoker");
        }

        if (ModBlocks.BLAST_FURNACE.isEnabled()) {
            sendHandler("ganymedes01.etfuturum.compat.nei.BlastFurnaceRecipeHandler", "etfuturum:blast_furnace");
            sendCatalyst("etfuturum.blastfurnace", "etfuturum:blast_furnace");

            sendCatalyst("fuel", "etfuturum:blast_furnace");
        }

        if (ModBlocks.BANNER.isEnabled()) {
            sendHandler("ganymedes01.etfuturum.compat.nei.BannerPatternHandler", "etfuturum:banner");
        }

        if (ModBlocks.COMPOSTER.isEnabled()) {
            sendHandler("ganymedes01.etfuturum.compat.nei.ComposterHandler", "etfuturum:composter", 1);
            sendCatalyst("etfuturum.composter", "etfuturum:composter");
        }
    }

    /*
     * These were copied from GTNewHorizons/GoodGenerator (Fork of GlodBlock/GoodGenerator)
     * Author: GlodBlock
     */

    private static void sendHandler(String aName, String aBlock) {
        sendHandler(aName, aBlock, 5);
    }

    private static void sendHandler(String aName, String aBlock, int maxRecipesPerPage) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handler", aName);
        aNBT.setString("modName", Reference.MOD_NAME);
        aNBT.setString("modId", Reference.MOD_ID);
        aNBT.setBoolean("modRequired", true);
        aNBT.setString("itemName", aBlock);
        aNBT.setInteger("handlerHeight", 65);
        aNBT.setInteger("handlerWidth", 166);
        aNBT.setInteger("maxRecipesPerPage", maxRecipesPerPage);
        aNBT.setInteger("yShift", 6);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack, int aPriority) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handlerID", aName);
        aNBT.setString("itemName", aStack);
        aNBT.setInteger("priority", aPriority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack) {
        sendCatalyst(aName, aStack, 0);
    }
}
