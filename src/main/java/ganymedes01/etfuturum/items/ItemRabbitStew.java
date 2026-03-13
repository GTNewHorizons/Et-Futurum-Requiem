package ganymedes01.etfuturum.items;

import net.minecraft.item.ItemSoup;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class ItemRabbitStew extends ItemSoup {

    public ItemRabbitStew() {
        super(10);
        setTextureName("rabbit_stew");
        setUnlocalizedName(Utils.getUnlocalisedName("rabbit_stew"));
        setCreativeTab(EtFuturum.creativeTabItems);
    }
}
