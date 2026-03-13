package ganymedes01.etfuturum.api.mappings;

import net.minecraft.block.Block;

@Deprecated
public class BlockAndMetadataMapping extends RegistryMapping<Block> {

    @Deprecated
    public BlockAndMetadataMapping(Block ore, int meta) {
        super(ore, meta);
    }

    public Block getBlock() {
        return super.getObject();
    }
}
