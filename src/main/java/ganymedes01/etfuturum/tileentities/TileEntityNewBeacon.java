package ganymedes01.etfuturum.tileentities;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;

import com.google.common.collect.Lists;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.recipes.ModRecipes;

public class TileEntityNewBeacon extends TileEntityBeacon {

    private final List<BeamSegment> segments = Lists.newArrayList();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, Double.POSITIVE_INFINITY, zCoord + 1);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote && worldObj.getTotalWorldTime() % 80L == 0L) updateSegments();
    }

    private void updateSegments() {
        int x = xCoord;
        int y = yCoord;
        int z = zCoord;
        segments.clear();
        TileEntityNewBeacon.BeamSegment beamsegment = new TileEntityNewBeacon.BeamSegment(
            EntitySheep.fleeceColorTable[0]);
        segments.add(beamsegment);
        boolean flag = true;

        for (int i = y + 1; i < worldObj.getActualHeight(); i++) {
            Block iblockstate = worldObj.getBlock(x, i, z);
            float[] colors = null;

            Item itemBlock = Item.getItemFromBlock(iblockstate);
            if (itemBlock != null) {
                for (String tag : EtFuturum
                    .getOreStrings(new ItemStack(itemBlock, 1, worldObj.getBlockMetadata(x, i, z)))) {
                    if (colors != null) break;
                    String tagLower = tag.toLowerCase();
                    for (int j = 0; j < 16; j++) {
                        String oreDye = ModRecipes.ore_dyes[15 - j].substring(3)
                            .toLowerCase();
                        if (tagLower.contains("glass") && tagLower.contains(oreDye)) {
                            if (!oreDye.contains("light") && tagLower.contains("light")) {
                                // Prevents Light Gray glass from getting the regular Gray color
                                continue;
                            }
                            colors = EntitySheep.fleeceColorTable[j];
                            break;
                        }
                    }
                }
            }

            if (colors == null) {
                if (iblockstate.getLightOpacity() >= 15) {
                    segments.clear();
                    break;
                }

                beamsegment.incrementHeight();
                continue;
            }

            if (!flag) colors = new float[] { (beamsegment.getColor()[0] + colors[0]) / 2.0F,
                (beamsegment.getColor()[1] + colors[1]) / 2.0F, (beamsegment.getColor()[2] + colors[2]) / 2.0F };

            if (Arrays.equals(colors, beamsegment.getColor())) beamsegment.incrementHeight();
            else {
                beamsegment = new TileEntityNewBeacon.BeamSegment(colors);
                segments.add(beamsegment);
            }

            flag = false;
        }
    }

    public List<BeamSegment> getSegments() {
        return segments;
    }

    public static class BeamSegment {

        private final float[] colours;
        private int height;

        public BeamSegment(float[] colours) {
            this.colours = colours;
            height = 1;
        }

        protected void incrementHeight() {
            height++;
        }

        public float[] getColor() {
            return colours;
        }

        public int getHeight() {
            return height;
        }
    }
}
