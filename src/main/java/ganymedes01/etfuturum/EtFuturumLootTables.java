package ganymedes01.etfuturum;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class EtFuturumLootTables {

    private static final String COMPOSTER = "composting";
    public static final ChestGenHooks COMPOSTER_LOOT = ChestGenHooks.getInfo(COMPOSTER);

    public static void init() {
        ChestGenHooks.getInfo(COMPOSTER)
            .setMin(1);
        ChestGenHooks.getInfo(COMPOSTER)
            .setMax(1);
        COMPOSTER_LOOT.addItem(new WeightedRandomChestContent(new ItemStack(Items.dye, 1, 15), 1, 1, 10));
        // For some reason it is min count, max count and weight, yes weight is the last arg not the first....
    }
}
