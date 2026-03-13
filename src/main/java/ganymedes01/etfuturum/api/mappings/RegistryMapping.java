package ganymedes01.etfuturum.api.mappings;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class RegistryMapping<T> {

    private static final RegistryMapping keyInstance = new RegistryMapping<>();
    private T object;
    private transient int meta;

    /**
     * Used by the mutable key instance to create a mutable key instance without a parameterized type, bypassing the
     * below restrictions.
     * Not intended for general use.
     */
    private RegistryMapping() {
        object = null;
        meta = 0;
    }

    /**
     * Used by certain areas of Et Futurum to store a block and meta mapping that can be matched with a new instance.
     * Used by raw ores and deepslate generation.
     */
    public RegistryMapping(T obj, int meta) {
        if (!(obj instanceof Block) && !(obj instanceof Item)) {
            throw new IllegalArgumentException("RegistryMapping must be either an item or a block!");
        }
        setObject(obj);
        setMeta(meta);
    }

    public T getObject() {
        return object;
    }

    public int getMeta() {
        return meta;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegistryMapping<?> && object == ((RegistryMapping<?>) obj).object
            && (meta == OreDictionary.WILDCARD_VALUE || ((RegistryMapping<?>) obj).meta == OreDictionary.WILDCARD_VALUE
                || meta == ((RegistryMapping<?>) obj).meta);
    }

    @Override
    public int hashCode() {
        return object.hashCode(); // Do not hash meta so wildcards and metas all get placed into the same bucket
    }

    /**
     * Returns a recycled RegistryMapping instance of the specified type. This does NOT CREATE A NEW INSTANCE!
     * This is used by things like deepslate registry as a key object without spamming the garbage collector with tons
     * of new instances.
     * So you should use "new RegistryMapping<?></?>(obj, meta) to create a new RegistryMapping, again, this DOES NOT
     * CREATE A NEW INSTANCE!
     *
     * @param object
     * @param meta
     * @param <E>
     * @return
     */
    public static <E> RegistryMapping<E> getKeyFor(E object, int meta) {
        return keyInstance.setObject(object)
            .setMeta(meta);
    }

    private RegistryMapping<T> setObject(T obj) {
        this.object = obj;
        return this;
    }

    private RegistryMapping<T> setMeta(int meta) {
        if (object instanceof Item || meta == OreDictionary.WILDCARD_VALUE) {
            this.meta = meta;
        } else {
            this.meta = meta & 15;
        }
        return this;
    }
}
