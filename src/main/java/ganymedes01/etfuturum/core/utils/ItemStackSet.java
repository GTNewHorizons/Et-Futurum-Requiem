package ganymedes01.etfuturum.core.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackSet extends ItemStackMap<ItemStack> implements ItemFilter {

    public ItemStackSet() {}

    public ItemStackSet with(ItemStack... items) {
        ItemStack[] arr$ = items;
        int len$ = items.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ItemStack item = arr$[i$];
            this.add(item);
        }

        return this;
    }

    public ItemStackSet with(Item... items) {
        Item[] arr$ = items;
        int len$ = items.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            Item item = arr$[i$];
            this.add(ItemStackMap.wildcard(item));
        }

        return this;
    }

    public ItemStackSet with(Block... blocks) {
        Block[] arr$ = blocks;
        int len$ = blocks.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            Block block = arr$[i$];
            this.add(ItemStackMap.wildcard(Item.getItemFromBlock(block)));
        }

        return this;
    }

    public void add(ItemStack item) {
        this.put(item, item);
    }

    public boolean contains(ItemStack item) {
        return this.get(item) != null;
    }

    public boolean containsAll(Item item) {
        return this.get(ItemStackMap.wildcard(item)) != null;
    }

    public boolean matches(ItemStack item) {
        return this.contains(item);
    }

    public static ItemStackSet of(Block... blocks) {
        return (new ItemStackSet()).with(blocks);
    }

    public static ItemStackSet of(Item... items) {
        return (new ItemStackSet()).with(items);
    }

    public static ItemStackSet of(ItemStack... items) {
        return (new ItemStackSet()).with(items);
    }
}
