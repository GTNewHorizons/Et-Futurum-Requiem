package ganymedes01.etfuturum.api.mappings;

import net.minecraft.item.Item;

@Deprecated
public class ItemAndMetadataMapping extends RegistryMapping<Item> {

    @Deprecated
    public ItemAndMetadataMapping(Item ore, int meta) {
        super(ore, meta);
    }

    public Item getItem() {
        return super.getObject();
    }
}
