package ganymedes01.etfuturum.client.sound;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;

import java.util.ArrayList;
import java.util.List;

public class ModSounds {

	private static final List<CustomSound> SOUNDS_REGISTRY = new ArrayList<>();

	public static List<CustomSound> getSounds() {
		return SOUNDS_REGISTRY;
	}

	public static final SoundType soundSlime = new CustomSound("slime_block") {
		@Override
		public SoundType getDisabledSound() {
			return this;
		}
	};
	public static final SoundType soundLantern = new CustomSound("lantern", true).setDisabledSound(Block.soundTypeMetal);
	public static final SoundType soundSoulSand = new CustomSound("soul_sand").setDisabledSound(Block.soundTypeSand);
	public static final SoundType soundSoulSoil = new CustomSound("soul_soil").setDisabledSound(Block.soundTypeGravel);
	public static final SoundType soundNetherBricks = new CustomSound("nether_bricks");
	public static final SoundType soundBoneBlock = new CustomSound("bone_block");
	public static final SoundType soundNetherrack = new CustomSound("netherrack");
	public static final SoundType soundNetherOre = new CustomSound("nether_ore");
	public static final SoundType soundGildedBlackstone = new CustomSound("gilded_blackstone");
	public static final SoundType soundAncientDebris = new CustomSound("ancient_debris");
	public static final SoundType soundBasalt = new CustomSound("basalt");
	public static final SoundType soundNetherite = new CustomSound("netherite_block").setDisabledSound(Block.soundTypeMetal);
	public static final SoundType soundCrops = new CustomSound("crop") {
		@Override
		public String getStepResourcePath() {
			return Block.soundTypeGrass.getStepResourcePath();
		}

		@Override
		public String func_150496_b() {
			return Tags.MC_ASSET_VER + ":item." + soundName + ".plant";
		}
	}.setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundCropWarts = new CustomSound("nether_wart") {
		@Override
		public String getStepResourcePath() {
			return Tags.MC_ASSET_VER + ":block.fungus.step";
		}

		@Override
		public String func_150496_b() {
			return Tags.MC_ASSET_VER + ":item." + soundName + ".plant";
		}
	}.setDisabledSound(Block.soundTypeStone);
	public static final SoundType soundCopper = new CustomSound("copper").setDisabledSound(Block.soundTypeMetal);
	public static final SoundType soundCopperGrate = new CustomSound("copper_grate").setDisabledSound(Block.soundTypeMetal);
	public static final SoundType soundCopperBulb = new CustomSound("copper_bulb", true).setDisabledSound(Block.soundTypeMetal);
	public static final SoundType soundBerryBush = new CustomSound("sweet_berry_bush", true) {
		@Override
		public String getStepResourcePath() {
			return Block.soundTypeGrass.getStepResourcePath();
		}
	}.setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundDeepslate = new CustomSound("deepslate", true);
	public static final SoundType soundDeepslateBricks = new CustomSound("deepslate_bricks");
	public static final SoundType soundDeepslateTiles = new CustomSound("deepslate_tiles");
	public static final SoundType soundTuff = new CustomSound("tuff");
	public static final SoundType soundPolishedTuff = new CustomSound("polished_tuff");
	public static final SoundType soundTuffBricks = new CustomSound("tuff_bricks");
	public static final SoundType soundSculk = new CustomSound("sculk").setDisabledSound(soundSlime);
	public static final SoundType soundSculkCatalyst = new CustomSound("sculk_catalyst");
	public static final SoundType soundVines = new CustomSound("vine").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundCalcite = new CustomSound("calcite", true);
	public static final SoundType soundAmethystBlock = new CustomSound("amethyst_block", true);
	public static final SoundType soundAmethystBudSmall = new CustomSound("small_amethyst_bud", true) {
		@Override
		public String getStepResourcePath() {
			return soundAmethystCluster.getStepResourcePath();
		}
	}.setDisabledSound(Block.soundTypeGlass);
	public static final SoundType soundAmethystBudMed = new CustomSound("medium_amethyst_bud", true) {
		@Override
		public String getStepResourcePath() {
			return soundAmethystCluster.getStepResourcePath();
		}
	}.setDisabledSound(Block.soundTypeGlass);
	public static final SoundType soundAmethystBudLrg = new CustomSound("large_amethyst_bud", true) {
		@Override
		public String getStepResourcePath() {
			return soundAmethystCluster.getStepResourcePath();
		}
	}.setDisabledSound(Block.soundTypeGlass);
	public static final SoundType soundAmethystCluster = new CustomSound("amethyst_cluster", true).setDisabledSound(Block.soundTypeGlass);
	public static final SoundType soundLodestone = new CustomSound("lodestone", true);
	public static final SoundType soundDripstoneBlock = new CustomSound("dripstone_block");
	public static final SoundType soundPointedDripstone = new CustomSound("pointed_dripstone");
	public static final SoundType soundWetGrass = new CustomSound("wet_grass", true).setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundHoneyBlock = new CustomSound("honey_block").setDisabledSound(soundSlime);
	public static final SoundType soundCoralBlock = new CustomSound("coral_block").setDisabledSound(soundSlime);
	public static final SoundType soundChain = new CustomSound("chain").setDisabledSound(Block.soundTypeMetal);
	public static final SoundType soundSponge = new CustomSound("sponge").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundWetSponge = new CustomSound("wet_sponge").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundNylium = new CustomSound("nylium");
	public static final SoundType soundNetherRoots = new CustomSound("roots").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundNetherSprouts = new CustomSound("nether_sprouts").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundWartBlock = new CustomSound("wart_block").setDisabledSound(Block.soundTypeWood);
	public static final SoundType soundShroomlight = new CustomSound("shroomlight").setDisabledSound(Block.soundTypeWood);
	public static final SoundType soundWeepingVines = new CustomSound("weeping_vines").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundStem = new CustomSound("stem").setDisabledSound(Block.soundTypeWood);
	public static final SoundType soundFungus = new CustomSound("fungus").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundNetherWood = new CustomSound("nether_wood").setDisabledSound(Block.soundTypeWood);

	public static final SoundType soundMud = new CustomSound("mud").setDisabledSound(Block.soundTypeGravel);
	public static final SoundType soundMangroveRoots = new CustomSound("mangrove_roots").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundMuddyMangroveRoots = new CustomSound("muddy_mangrove_roots").setDisabledSound(Block.soundTypeGravel);
	public static final SoundType soundPackedMud = new CustomSound("packed_mud");
	public static final SoundType soundMudBricks = new CustomSound("mud_bricks");

	public static final SoundType soundAzalea = new CustomSound("azalea").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundAzaleaLeaves = new CustomSound("azalea_leaves").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundHangingRoots = new CustomSound("hanging_roots").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundRootedDirt = new CustomSound("rooted_dirt").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundMoss = new CustomSound("moss").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundMossCarpet = new CustomSound("moss_carpet").setDisabledSound(Block.soundTypeGrass);

	public static final SoundType soundCherrySapling = new CustomSound("cherry_sapling").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundCherryLeaves = new CustomSound("cherry_leaves").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundCherryWood = new CustomSound("cherry_wood").setDisabledSound(Block.soundTypeWood);
	public static final SoundType soundPinkPetals = new CustomSound("pink_petals").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundBamboo = new CustomSound("bamboo").setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundBambooWood = new CustomSound("bamboo_wood").setDisabledSound(Block.soundTypeWood);
	public static final SoundType soundBambooSapling = new CustomSound("bamboo_sapling") {
		@Override
		public String getStepResourcePath() {
			return Block.soundTypeGrass.getStepResourcePath();
		}
	}.setDisabledSound(Block.soundTypeGrass);
	public static final SoundType soundPainting = new CustomSound("painting") {
		@Override
		public String getBreakSound() {
			return Tags.MC_ASSET_VER + ":entity.painting.break";
		}

		@Override
		public String getStepResourcePath() {
			return Block.soundTypeWood.getStepResourcePath();
		}

		@Override
		public String func_150496_b() {
			return Tags.MC_ASSET_VER + ":entity.painting.place";
		}
	}.setDisabledSound(Block.soundTypeWood);

	public static class CustomSound extends SoundType {

		private final boolean placeSound;
		private SoundType disabledSound;

		public CustomSound(String name, boolean placeSound) {
			this(name, 1, 1, placeSound);
		}

		public CustomSound(String name, float volume, float pitch, boolean placeSound) {
			super(name, volume, pitch);
			this.placeSound = placeSound;
			SOUNDS_REGISTRY.add(this);
			disabledSound = Block.soundTypeStone;
		}

		public CustomSound(String name, float volume, float pitch) {
			this(name, volume, pitch, false);
		}

		public CustomSound(String name) {
			this(name, 1f, 1f);
		}

		@Override
		public String getBreakSound() {
			return Tags.MC_ASSET_VER + ":block." + soundName + ".break";
		}

		@Override
		public String getStepResourcePath() {
			return Tags.MC_ASSET_VER + ":block." + soundName + ".step";
		}

		/**
		 * MCP name: {@code getPlaceSound}
		 */
		@Override
		public String func_150496_b() {
			return placeSound ? Tags.MC_ASSET_VER + ":block." + soundName + ".place" : this.getBreakSound();
		}

		public SoundType getDisabledSound() {
			return disabledSound;
		}

		protected SoundType setDisabledSound(SoundType type) {
			disabledSound = type;
			return this;
		}
	}
}