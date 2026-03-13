package ganymedes01.etfuturum.tileentities;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.Lists;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.itemblocks.ItemBlockBanner;
import ganymedes01.etfuturum.lib.EnumColor;

public class TileEntityBanner extends TileEntity {

    private int baseColor;
    private NBTTagList patterns;
    private boolean patternDataSet;
    private List<EnumBannerPattern> patternList;
    private List<EnumColor> colorList;
    /**
     * This is a String representation of this banners pattern and color lists, used for texture caching.
     */
    private String patternResourceLocation;
    public boolean isStanding;

    public void setItemValues(ItemStack stack) {
        patterns = null;

        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("BlockEntityTag", 10)) {
            NBTTagCompound nbttagcompound = stack.getTagCompound()
                .getCompoundTag("BlockEntityTag");

            if (nbttagcompound.hasKey("Patterns")) patterns = (NBTTagList) nbttagcompound.getTagList("Patterns", 10)
                .copy();

            if (nbttagcompound.hasKey("Base", 99)) baseColor = nbttagcompound.getInteger("Base");
            else baseColor = stack.getItemDamage() & 15;
        } else baseColor = stack.getItemDamage() & 15;

        patternList = null;
        colorList = null;
        patternResourceLocation = "";
        patternDataSet = true;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Base", baseColor);
        nbt.setBoolean("IsStanding", isStanding);

        if (patterns != null) nbt.setTag("Patterns", patterns);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        baseColor = nbt.getInteger("Base");
        isStanding = nbt.getBoolean("IsStanding");
        patterns = nbt.getTagList("Patterns", 10);
        patternList = null;
        colorList = null;
        patternResourceLocation = null;
        patternDataSet = true;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (pkt.func_148853_f() == 0) // getTileEntityType
            readFromNBT(pkt.func_148857_g()); // getNbtCompound
    }

    public int getBaseColor() {
        return baseColor;
    }

    public static int getBaseColor(ItemStack stack) {
        NBTTagCompound nbttagcompound = ItemBlockBanner.getSubTag(stack, "BlockEntityTag", false);
        return nbttagcompound != null && nbttagcompound.hasKey("Base") ? nbttagcompound.getInteger("Base")
            : stack.getItemDamage();
    }

    public static int getPatterns(ItemStack stack) {
        NBTTagCompound nbttagcompound = ItemBlockBanner.getSubTag(stack, "BlockEntityTag", false);
        return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getTagList("Patterns", 10)
            .tagCount() : 0;
    }

    public List<EnumBannerPattern> getPatternList() {
        initializeBannerData();
        return patternList;
    }

    public List<EnumColor> getColorList() {
        initializeBannerData();
        return colorList;
    }

    public String getPatternResourceLocation() {
        initializeBannerData();
        return patternResourceLocation;
    }

    public static void removeBannerData(ItemStack stack) {
        NBTTagCompound nbttagcompound = ItemBlockBanner.getSubTag(stack, "BlockEntityTag", false);

        if (nbttagcompound != null && nbttagcompound.hasKey("Patterns", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Patterns", 10);

            if (nbttaglist.tagCount() > 0) {
                nbttaglist.removeTag(nbttaglist.tagCount() - 1);

                if (nbttaglist.tagCount() == 0) {
                    stack.getTagCompound()
                        .removeTag("BlockEntityTag");

                    if (stack.getTagCompound()
                        .hasNoTags()) stack.setTagCompound(null);
                }
            }
        }
    }

    private void initializeBannerData() {
        if (patternList == null || colorList == null || patternResourceLocation == null)
            if (!patternDataSet) patternResourceLocation = "";
            else {
                patternList = Lists.newArrayList();
                colorList = Lists.newArrayList();
                patternList.add(TileEntityBanner.EnumBannerPattern.BASE);
                colorList.add(EnumColor.fromDamage(baseColor));
                patternResourceLocation = "b" + baseColor;

                if (patterns != null) for (int i = 0; i < patterns.tagCount(); i++) {
                    NBTTagCompound nbttagcompound = patterns.getCompoundTagAt(i);
                    EnumBannerPattern pattern = EnumBannerPattern.getPatternByID(nbttagcompound.getString("Pattern"));

                    if (pattern != null) {
                        patternList.add(pattern);
                        int j = nbttagcompound.getInteger("Color");
                        colorList.add(EnumColor.fromDamage(j));
                        patternResourceLocation = patternResourceLocation + pattern.getPatternID() + j;
                    }
                }
            }
    }

    public ItemStack createStack() {
        ItemStack stack = ModBlocks.BANNER.newItemStack(1, getBaseColor());
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        nbt.removeTag("x");
        nbt.removeTag("y");
        nbt.removeTag("z");
        nbt.removeTag("id");
        stack.setTagInfo("BlockEntityTag", nbt);
        return stack;
    }

    public enum EnumBannerPattern {

        BASE("base", "b"),
        SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
        SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
        SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
        SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
        STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
        STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
        STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
        STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
        STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
        STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
        STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
        STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
        STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
        CROSS("cross", "cr", "# #", " # ", "# #"),
        STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
        TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
        TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
        TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
        TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
        DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
        DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
        DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
        DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
        CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
        RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
        HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
        HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
        HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
        HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
        BORDER("border", "bo", "###", "# #", "###"),
        CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.vine)),
        CREEPER("creeper", "cre", new ItemStack(Items.skull, 1, 4)),
        GRADIENT("gradient", "gra", "# #", " # ", " # "),
        GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
        BRICKS("bricks", "bri", new ItemStack(Blocks.brick_block)),
        SKULL("skull", "sku", new ItemStack(Items.skull, 1, 1)),
        FLOWER("flower", "flo", new ItemStack(Blocks.red_flower, 1, 8)),
        MOJANG("mojang", "moj", new ItemStack(Items.golden_apple, 1, 1));

        public static final EnumBannerPattern[] VALUES = values();

        private final String patternName;
        private final String patternID;
        private final String[] craftingLayers;
        private ItemStack patternCraftingStack;

        EnumBannerPattern(String name, String id) {
            craftingLayers = new String[3];
            patternName = name;
            patternID = id;
            patternCraftingStack = null;
        }

        EnumBannerPattern(String name, String id, ItemStack craftingItem) {
            this(name, id);
            patternCraftingStack = craftingItem;
        }

        EnumBannerPattern(String name, String id, String craftingTop, String craftingMid, String craftingBot) {
            this(name, id);
            craftingLayers[0] = craftingTop;
            craftingLayers[1] = craftingMid;
            craftingLayers[2] = craftingBot;
        }

        public String getPatternName() {
            return patternName;
        }

        public String getPatternID() {
            return patternID;
        }

        public String[] getCraftingLayers() {
            return craftingLayers;
        }

        public boolean hasValidCrafting() {
            return patternCraftingStack != null || craftingLayers[0] != null;
        }

        public boolean hasCraftingStack() {
            return patternCraftingStack != null;
        }

        public ItemStack getCraftingStack() {
            return patternCraftingStack;
        }

        public static EnumBannerPattern getPatternByID(String id) {
            for (EnumBannerPattern pattern : EnumBannerPattern.values())
                if (pattern.patternID.equals(id)) return pattern;
            return null;
        }
    }
}
