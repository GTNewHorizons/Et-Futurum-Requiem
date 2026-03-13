package ganymedes01.etfuturum.items.rawore.modded;

import ganymedes01.etfuturum.items.BaseItem;
import ganymedes01.etfuturum.lib.Reference;

public class BaseRawOre extends BaseItem {

    private final String subfolder;

    public BaseRawOre(String subfolder, String name) {
        this.subfolder = subfolder;
        setNames("raw_" + name);
    }

    @Override
    public String getTextureSubfolder() {
        return subfolder;
    }

    @Override
    public String getTextureDomain() {
        return Reference.MOD_ID;
    }

    @Override
    public String getNameDomain() {
        return super.getNameDomain() + (getTextureSubfolder().isEmpty() ? ""
            : (super.getNameDomain().isEmpty() ? "" : ".") + getTextureSubfolder());
    }
}
