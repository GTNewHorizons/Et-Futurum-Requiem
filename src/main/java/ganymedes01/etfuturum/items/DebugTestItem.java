package ganymedes01.etfuturum.items;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.handlers.WorldEventHandler;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.world.generate.feature.WorldGenFossil;
import ganymedes01.etfuturum.world.generate.feature.WorldGenGeode;
import ganymedes01.etfuturum.world.generate.structure.WorldGenNBTStructureTesting;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Used while in the dev environment to run random code and test certain features such as structure placement.
 * This is intentionally English-only for easy of development, so we don't go back and fourth between lang files.
 * Please do not change the text displayed by this item to be translatable.
 */
public class DebugTestItem extends BaseItem {

	private static final String langkey = "debug_test_item"; //Ignores IntellIJ's suggestion of giving it a lang key; The debug item doesn't use lang keys.

	public DebugTestItem() {
		setUnlocalizedName(langkey);//Placeholder lang key just to make it not null as a safeguard in case a mod grabs the unlocalizedName field directly and does null-unsafe operations on it.
		setTextureName("ruby");
		setHasSubtypes(true);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!canUse(player)) {
			return false;
		}
		if (stack.getItemDamage() == 0) {
			return false;
		}
		TestTypes testSubject = TestTypes.values()[stack.getItemDamage() % TestTypes.values().length];
		if (world.isRemote && testSubject.isServerOnly()) {
			return true;
		}
		world.theProfiler.startSection(Tags.MOD_NAME + " debugging item task");

		boolean flag = testSubject.runAction(stack, player, world, x, y, z, side, hitX, hitY, hitZ);

		world.theProfiler.endSection();
		return flag;
	}

	/**
	 * Apparently NOBODY thought we would need LEFT_CLICK_AIR so for now you have to hit a block to change the mode.
	 * Sneak + Right click also doesn't work because Creative mode resets item damage on right click.
	 * And I want this to be mapped to left click anyways.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent event) {
		if (event.entityPlayer != null && event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && canUse(event.entityPlayer)) {
			if (event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() == ModItems.DEBUGGING_TOOL.get()) {
				event.entityPlayer.getCurrentEquippedItem().setItemDamage((event.entityPlayer.getCurrentEquippedItem().getItemDamage() + 1) % TestTypes.values().length);
				event.setCanceled(true);
			}
		}
	}

	private boolean canUse(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			return FMLClientHandler.instance().getClientPlayerEntity().capabilities.isCreativeMode;
		} else if (player instanceof EntityPlayerMP entityPlayerMP) {
			return entityPlayerMP.theItemInWorldManager.isCreative();
		}
		return false;
	}

	@Override
	public String getItemStackDisplayName(ItemStack p_77653_1_) {
		return "Debugging Item" + TestTypes.values()[p_77653_1_.getItemDamage() % TestTypes.values().length].getSuffix();
	}

	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return EnumRarity.epic;
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return true;
	}

	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List<String> p_77624_3_, boolean p_77624_4_) {
		if (canUse(p_77624_2_)) {
			p_77624_3_.add("\u00a7b\u00a7oA debug item used to test mechanics certain code.");
			p_77624_3_.add("\u00a7a\u00a7oAttack\u00a7a\u00a7o a block to change the test subject.");
			p_77624_3_.add("\u00a7b\u00a7oClick the \u00a7a\u00a7ouse item button\u00a7b\u00a7o to run the test.");
		} else {
			p_77624_3_.add("\u00a74\u00a7oYou must be in Creative mode to use this item.");
		}
		p_77624_3_.add("\u00a76\u00a7oDev environment only.");
		super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
		return 0;
	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		if (event.getPlayer().getHeldItem() != null && event.getPlayer().getHeldItem().getItem() == ModItems.DEBUGGING_TOOL.get()) {
			event.setCanceled(true);
		}
	}


	private enum TestTypes {
		NONE(null, false),
		FOSSIL("Fossil", true) {

			private final Supplier<WorldGenFossil> fossilSupplier = () -> new WorldGenFossil() {
						@Override
						protected boolean canFossilGenerateHere (World world,int x, int y, int z, BlockPos corners){
							return true;
						}
					};

			private WorldGenFossil fossil;

			@Override
			protected boolean runAction(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
				if(fossil == null) {
					fossil = fossilSupplier.get();
				}
				return fossil.generate(world, world.rand, x, y, z);
			}
		},
		GEODE("Geode", true) {

			private final Supplier<WorldGenGeode> geodeSupplier = () -> new WorldGenGeode(ConfigWorld.amethystOuterBlock, ConfigWorld.amethystMiddleBlock) {
				@Override
				protected boolean isInvalidCorner(World world, int x, int y, int z) {
					return false;
				}
			};

			private WorldGenGeode geode;

			@Override
			protected boolean runAction(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
				if(geode == null) {
					geode = geodeSupplier.get();
				}
				return geode.generate(world, world.rand, x, y, z);
			}
		},
		BEE_NEST("Bee Nest", true) {
			@Override
			protected boolean runAction(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
				WorldEventHandler.tryPlaceBeeNest(world, x, y, z, world.rand, 3);
				return true;
			}
		},
		NBT_STRUCTURE_TEST("NBT Structure Test", true) {
			private final WorldGenNBTStructureTesting struct = new WorldGenNBTStructureTesting();

			@Override
			protected boolean runAction(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
				boolean run = struct.generate(world, world.rand, x, y, z);
				world.setBlock(x, y, z, Blocks.redstone_block);
				return run;
			}
		};

		private final String suffix;
		private final boolean serverOnly;

		TestTypes(String name, boolean server) {
			suffix = name;
			serverOnly = server;
		}

		private String getSuffix() {
			return suffix == null ? "" : " | Test Subject: \u00a7o" + suffix + "\u00a7r";
		}

		protected boolean runAction(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
			return false;
		}

		private boolean isServerOnly() {
			return serverOnly;
		}
	}
}
