package ganymedes01.etfuturum.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityCushion extends Entity {

	public int x;
	public int y;
	public int z;

	public EntityCushion(World world) {
		super(world);
        this.yOffset = 0.0F;
        this.setSize(0.5F, 0.5F);
	}

	public EntityCushion(World world, int x, int y, int z) {
		this(world);
		this.x = x;
		this.y = y;
		this.z = z;
		posX = x + 0.5;
		posY = y;
		posZ = z + 0.5;
	}

	@Override
	protected void entityInit() {
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}

	public boolean onValidSurface() {
		return true;
	}

	public double getSurfaceHeight() {
		Block block = worldObj.getBlock(x, y - 1, z);
		return y - 1 + block.getBlockBoundsMaxY();
	}
	
}
