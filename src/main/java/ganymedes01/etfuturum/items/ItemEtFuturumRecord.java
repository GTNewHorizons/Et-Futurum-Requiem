package ganymedes01.etfuturum.items;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.lib.Reference;

public class ItemEtFuturumRecord extends ItemRecord {

    public ItemEtFuturumRecord(String recordName) {
        super(recordName);
        setTextureName("music_disc_" + recordName);
        setUnlocalizedName("record");
        setCreativeTab(EtFuturum.creativeTabItems);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(Reference.MCAssetVer + ":music_disc." + recordName);
    }

}
