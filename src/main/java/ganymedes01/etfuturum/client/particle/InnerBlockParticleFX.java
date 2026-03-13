package ganymedes01.etfuturum.client.particle;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.ModBlocks;

public class InnerBlockParticleFX extends EtFuturumFXParticle {

    private Block spawnedIn;
    private int lastMeta;

    public InnerBlockParticleFX(World world, double x, double y, double z) {
        super(world, x, y, z, 0, 0, 0, 80 + particleRand.nextInt(20), 4.5F, 0xFFFFFFFF, null, 1);
    }

    @Override
    protected ResourceLocation[] loadTextures(World world, double x, double y, double z, ResourceLocation texture) {
        spawnedIn = world.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
        lastMeta = worldObj.getBlockMetadata(
            MathHelper.floor_double(posX),
            MathHelper.floor_double(posY),
            MathHelper.floor_double(posZ));
        if (spawnedIn == ModBlocks.LIGHT.get()) {
            ResourceLocation[] newResourceLocation = new ResourceLocation[16];
            textures = 16;
            for (int i = 0; i < 16; i++) {
                newResourceLocation[i] = new ResourceLocation(
                    "textures/blocks/" + spawnedIn.getIcon(2, i)
                        .getIconName() + ".png");
            }
            return newResourceLocation;
        }
        textures = 1;
        return new ResourceLocation[] { new ResourceLocation(
            "textures/blocks/" + spawnedIn.getIcon(2, 0)
                .getIconName() + ".png") };
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz,
        float rxy) {
        currentTexture = textures == 0 ? 0 : lastMeta % textures;
        super.renderParticle(tessellator, partialTicks, rx, rxz, rz, ryz, rxy);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        EntityPlayer player = FMLClientHandler.instance()
            .getClient().thePlayer;
        ItemStack heldItem = player.getHeldItem();

        boolean decay = (worldObj
            .getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))
            != spawnedIn)
            || (!player.capabilities.isCreativeMode || heldItem == null
                || heldItem.getItem() != Item.getItemFromBlock(spawnedIn));
        if (decay) {
            if (this.particleAge++ >= this.particleMaxAge) {
                this.setDead();
            }
        } else {
            lastMeta = worldObj.getBlockMetadata(
                MathHelper.floor_double(posX),
                MathHelper.floor_double(posY),
                MathHelper.floor_double(posZ));
            particleAge = 0;
        }
    }
}
