package ganymedes01.etfuturum.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import ganymedes01.etfuturum.configuration.configs.ConfigSounds;

public class ItemBlockDyedBed extends ItemBlock {

    public ItemBlockDyedBed(Block p_i45328_1_) {
        super(p_i45328_1_);
        setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_,
        int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_3_.isRemote) {
            return true;
        } else if (p_77648_7_ != 1) {
            return false;
        } else {
            ++p_77648_5_;
            BlockBed blockbed = (BlockBed) field_150939_a; // blockInstance
            int i1 = MathHelper.floor_double((double) (p_77648_2_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            byte b0 = 0;
            byte b1 = 0;

            if (i1 == 0) {
                b1 = 1;
            }

            if (i1 == 1) {
                b0 = -1;
            }

            if (i1 == 2) {
                b1 = -1;
            }

            if (i1 == 3) {
                b0 = 1;
            }

            if (p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_)
                && p_77648_2_.canPlayerEdit(p_77648_4_ + b0, p_77648_5_, p_77648_6_ + b1, p_77648_7_, p_77648_1_)) {
                if (p_77648_3_.isAirBlock(p_77648_4_, p_77648_5_, p_77648_6_)
                    && p_77648_3_.isAirBlock(p_77648_4_ + b0, p_77648_5_, p_77648_6_ + b1)
                    && World.doesBlockHaveSolidTopSurface(p_77648_3_, p_77648_4_, p_77648_5_ - 1, p_77648_6_)
                    && World
                        .doesBlockHaveSolidTopSurface(p_77648_3_, p_77648_4_ + b0, p_77648_5_ - 1, p_77648_6_ + b1)) {
                    p_77648_3_.setBlock(p_77648_4_, p_77648_5_, p_77648_6_, blockbed, i1, 3);

                    if (p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_) == blockbed) {
                        p_77648_3_.setBlock(p_77648_4_ + b0, p_77648_5_, p_77648_6_ + b1, blockbed, i1 + 8, 3);
                    }

                    // Disable the sound for continuity, so it doesn't play when the event-based player would not
                    if (ConfigSounds.fixSilentPlacing) p_77648_3_.playSoundEffect(
                        (float) p_77648_4_ + 0.5F,
                        (float) p_77648_5_ + 0.5F,
                        (float) p_77648_6_ + 0.5F,
                        this.field_150939_a/* blockInstance */.stepSound.func_150496_b()/* getPlaceSound */,
                        (this.field_150939_a/* blockInstance */.stepSound.getVolume() + 1.0F) / 2.0F,
                        this.field_150939_a/* blockInstance */.stepSound.getPitch() * 0.8F);
                    --p_77648_1_.stackSize;
                    return true;
                }
                return false;
            }
            return false;
        }
    }

}
