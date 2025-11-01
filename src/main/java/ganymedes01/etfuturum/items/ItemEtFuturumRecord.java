package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;

public class ItemEtFuturumRecord extends ItemRecord {

	public ItemEtFuturumRecord(String recordName) {
		super(recordName);
		setTextureName("music_disc_" + recordName);
		setUnlocalizedName("record");
		setCreativeTab(EtFuturum.creativeTabItems);
	}

	@Override
	public ResourceLocation getRecordResource(String name) {
		return new ResourceLocation(Tags.MC_ASSET_VER + ":music_disc." + recordName);
	}

}
