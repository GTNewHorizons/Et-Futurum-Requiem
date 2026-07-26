package ganymedes01.etfuturum;

import baubles.common.lib.PlayerHandler;
import ganymedes01.etfuturum.blocks.BlockSoulSoil;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigEnchantsPotions;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.enchantment.FrostWalker;
import ganymedes01.etfuturum.enchantment.Mending;
import ganymedes01.etfuturum.enchantment.SoulSpeed;
import ganymedes01.etfuturum.enchantment.SwiftSneak;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import java.util.*;

public class ModEnchantments {

	public static Enchantment frostWalker;
	public static Enchantment mending;
	public static Enchantment swiftSneak;
	public static Enchantment soulSpeed;

	public static final UUID SOUL_SPEED_UUID = UUID.fromString("7f382a10-21a4-4a87-8df0-aa542031bcbb");

	private static final Map<EntityLivingBase, double[]> prevMoveCache = new WeakHashMap<>();

	public static void init() {
		if (ConfigEnchantsPotions.enableFrostWalker)
			frostWalker = new FrostWalker();
		if (ConfigEnchantsPotions.enableMending)
			mending = new Mending();
		if (ConfigEnchantsPotions.enableSwiftSneak)
			swiftSneak = new SwiftSneak();
		if (ConfigEnchantsPotions.enableSoulSpeed)
			soulSpeed = new SoulSpeed();
	}

	// Frost Walker logic
	public static void onLivingUpdate(EntityLivingBase entity) {
		if (!ConfigEnchantsPotions.enableFrostWalker && !ConfigEnchantsPotions.enableSoulSpeed)
			return;
		if (entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(SOUL_SPEED_UUID) != null) {
			if(Math.abs(entity.motionX) + Math.abs(entity.motionZ) > 0.01) {
				CustomParticles.spawnSoulSpeedOrb(entity.worldObj, entity.posX, entity.posY, entity.posZ);
				float volume = entity.worldObj.rand.nextFloat() > 0.3F ? 0.0F : 0.15F;
				entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, Reference.MCAssetVer + ":particle.soul_escape", volume, entity.worldObj.rand.nextFloat() * 0.4F + 0.6F, true);
			}
		}
		if (entity.worldObj.isRemote)
			return;

		ItemStack boots = entity.getEquipmentInSlot(1);
		int frostWalkerLevel = EnchantmentHelper.getEnchantmentLevel(frostWalker.effectId, boots);
		int soulSpeedLevel = EnchantmentHelper.getEnchantmentLevel(soulSpeed.effectId, boots);
		if (frostWalkerLevel > 0 || soulSpeedLevel > 0) {
			double[] prevCoords = prevMoveCache.get(entity);
			if (prevCoords == null || (Math.abs(prevCoords[0] - entity.posX) > 0.003D && Math.abs(prevCoords[1] - entity.posZ) > 0.003D)) {
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;

				if (frostWalkerLevel > 0 && entity.onGround) {
					int radius = Math.min(16, 2 + frostWalkerLevel);
					for (int i = -radius; i <= radius; i++) {
						for (int j = -radius; j <= radius; j++) {
							if (i * i + j * j <= radius * radius) {
								Block block = entity.worldObj.getBlock(x + i, y - 1, z + j);
								Block blockUp = entity.worldObj.getBlock(x + i, y, z + j);
								if (!blockUp.isNormalCube() && blockUp.getMaterial() != Material.water && (block == Blocks.water || block == Blocks.flowing_water)) {
									if (entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, AxisAlignedBB.getBoundingBox(x + i, y - 1, z + j, x + i + 1, y, z + j + 1)).isEmpty()) {
										entity.worldObj.setBlock(x + i, y - 1, z + j, ModBlocks.FROSTED_ICE.get());
									}
								}
							}
						}
					}
				}
				if (soulSpeedLevel > 0) {
					Block inBlock = entity.worldObj.getBlock(x, y, z);
					Block underBlock = entity.worldObj.getBlock(x, y - 1, z);
					IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
					if(inBlock == Blocks.soul_sand || inBlock == ModBlocks.SOUL_SOIL.get() || underBlock == Blocks.soul_sand || underBlock == ModBlocks.SOUL_SOIL.get()) {
						if (attribute.getModifier(SOUL_SPEED_UUID) == null) {
							attribute.applyModifier(new AttributeModifier(SOUL_SPEED_UUID, "Soul Speed Boost", 1.3D + 0.105 * soulSpeedLevel, 2).setSaved(false));
						}
						if (prevCoords != null) {
							double dx = (prevCoords[0] - x);
							double dz = (prevCoords[1] - z);
							double dist = Math.sqrt(dx * dx + dz * dz);
							if (entity.worldObj.rand.nextFloat() < 0.04F * dist) {
								boots.damageItem(1, entity);
								if (boots.stackSize <= 0) {
									entity.setCurrentItemOrArmor(1, null);
								}
							}
						}
					} else {
						if(attribute.getModifier(SOUL_SPEED_UUID) != null) {
							attribute.removeModifier(new AttributeModifier(SOUL_SPEED_UUID, "Soul Speed Boost", 1.3D + 0.105*soulSpeedLevel, 2).setSaved(false));
						}
					}
				}
				prevMoveCache.put(entity, new double[]{entity.posX, entity.posZ});
			}
		} else {
			prevMoveCache.remove(entity);
		}
	}

	// Mending logic
	public static void onPlayerPickupXP(PlayerPickupXpEvent event) {
		EntityPlayer player = event.entityPlayer;
		EntityXPOrb orb = event.orb;
		if (player.worldObj.isRemote)
			return;
		if (!ConfigEnchantsPotions.enableMending)
			return;

		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(player.getCurrentEquippedItem()); // held
		stacks.add(player.getEquipmentInSlot(1)); // boots
		stacks.add(player.getEquipmentInSlot(2)); // leggings
		stacks.add(player.getEquipmentInSlot(3)); // chestplate
		stacks.add(player.getEquipmentInSlot(4)); // helmet
		if (ModsList.BAUBLES.isLoaded() && ConfigModCompat.baublesMending) {
			ItemStack[] baubles = PlayerHandler.getPlayerBaubles(player).stackList;
			if (baubles != null) {
				Collections.addAll(stacks, baubles);
			}
		}

		for (ItemStack stack : stacks)
			if (stack != null && stack.getItemDamage() > 0 && EnchantmentHelper.getEnchantmentLevel(mending.effectId, stack) > 0) {
				Item item = stack.getItem();
				if (item == null || !item.isRepairable()) continue;
				int xp = orb.xpValue;
				while (xp > 0 && stack.getItemDamage() > 0) {
					stack.setItemDamage(stack.getItemDamage() - 2);
					xp--;
				}
				if (xp <= 0) {
					orb.setDead();
					event.setCanceled(true);
					return;
				}
			}
	}
}
