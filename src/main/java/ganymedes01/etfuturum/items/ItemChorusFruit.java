package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class ItemChorusFruit extends BaseFood {

	public ItemChorusFruit() {
		super(4, 0.3F, false);
		setAlwaysEdible();
		setNames("chorus_fruit");
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		ItemStack result = super.onEaten(stack, world, player);
		for (int i = 0; i < 16; i++) {
			double xx = player.posX + (player.getRNG().nextDouble() - 0.5D) * 64.0D;
			double yy = player.posY + (player.getRNG().nextInt(64) - 32);
			double zz = player.posZ + (player.getRNG().nextDouble() - 0.5D) * 64.0D;
			if (teleportTo(player, xx, yy, zz)) {
				player.fallDistance = 0;
				break;
			}
		}
		return result;
	}

	// copied from EntityEnderman.teleportTo
	private static boolean teleportTo(EntityLivingBase entity, double xx, double yy, double zz) {
		EnderTeleportEvent event = new EnderTeleportEvent(entity, xx, yy, zz, 0);
		if (MinecraftForge.EVENT_BUS.post(event))
			return false;

		double d3 = entity.posX;
		double d4 = entity.posY;
		double d5 = entity.posZ;
		entity.posX = event.targetX;
		entity.posY = event.targetY;
		entity.posZ = event.targetZ;
		boolean flag = false;
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);

		if (entity.worldObj.blockExists(i, j, k)) {
			boolean flag1 = false;
			while (!flag1 && j > 0) {
				Block block = entity.worldObj.getBlock(i, j - 1, k);
				if (block.getMaterial().blocksMovement())
					flag1 = true;
				else {
					entity.posY--;
					j--;
				}
			}

			if (flag1) {
				entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
				if (entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() && !entity.worldObj.isAnyLiquid(entity.boundingBox))
					flag = true;
			}
		}

		if (!flag) {
			entity.setPosition(d3, d4, d5);
			return false;
		}
		short short1 = 128;

		for (int l = 0; l < short1; l++) {
			double d6 = l / (short1 - 1.0D);
			float f = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
			float f1 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
			float f2 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
			double d7 = d3 + (entity.posX - d3) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.width * 2.0D;
			double d8 = d4 + (entity.posY - d4) * d6 + entity.getRNG().nextDouble() * entity.height;
			double d9 = d5 + (entity.posZ - d5) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.width * 2.0D;
			entity.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
		}

		entity.worldObj.playSoundEffect(d3, d4, d5, Tags.MC_ASSET_VER + ":item.chorus_fruit.teleport", 1.0F, 1.0F);
		entity.playSound(Tags.MC_ASSET_VER + ":item.chorus_fruit.teleport", 1.0F, 1.0F);
		return true;
	}
}