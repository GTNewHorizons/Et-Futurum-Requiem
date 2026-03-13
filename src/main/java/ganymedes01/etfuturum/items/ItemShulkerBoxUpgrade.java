package ganymedes01.etfuturum.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShulkerBoxUpgrade extends BaseSubtypesItem {

    public ItemShulkerBoxUpgrade() {
        super(
            "vanilla_iron_upgrade",
            "vanilla_copper_upgrade",
            "iron_gold_upgrade",
            "gold_diamond_upgrade",
            "diamond_obsidian_upgrade",
            "diamond_crystal_upgrade",
            "copper_iron_upgrade",
            "copper_silver_upgrade",
            "silver_gold_upgrade");
        setNames("shulker_box_upgrade");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.shulker_" + types[Math.max(Math.min(stack.getItemDamage(), types.length - 1), 0)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[types.length];
        for (int i = 0; i < types.length; i++) {
            icons[i] = reg.registerIcon("ironshulkerbox:" + types[i]);
        }
    }

}
