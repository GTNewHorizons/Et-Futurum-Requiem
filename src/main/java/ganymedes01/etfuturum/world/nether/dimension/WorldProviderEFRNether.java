package ganymedes01.etfuturum.world.nether.dimension;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderEFRNether extends WorldProviderHell {

    @Override
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new NetherWorldChunkManager(worldObj);
        this.isHellWorld = true;
        this.hasNoSky = true;
        this.dimensionId = -1;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new NetherChunkProvider(this.worldObj, this.worldObj.getSeed());
    }

    @Override
    protected void generateLightBrightnessTable() {
        float minBrightness = (float) (1.0F / 10000000.0F * Math.pow(75, 3.2F) + 0.002F);
        for (int i = 0; i <= 15; i++) {
            float f1 = 1F - (i * i) / (15F * 15F);
            this.lightBrightnessTable[i] = ((1F - f1) / (f1 * 6F + 1F) * (1F - minBrightness) + minBrightness);
        }
        System.arraycopy(this.lightBrightnessTable, 0, new float[16], 0, 16);
    }

    @Override
    public boolean getWorldHasVoidParticles() {
        return false;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return this.worldObj.getTopBlock(x, z) != Blocks.bedrock && this.worldObj.getTopBlock(x, z) != Blocks.air;
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return super.getRespawnDimension(player);

    }

    @Override
    public int getActualHeight() {
        return 128;
    }

}
