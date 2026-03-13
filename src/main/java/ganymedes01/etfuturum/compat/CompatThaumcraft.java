package ganymedes01.etfuturum.compat;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;

public class CompatThaumcraft {

    public static void doAspects() {
        // ThaumcraftApi.registerObjectTag(new ItemStack(), new AspectList() );
        // ThaumcraftApi.registerEntityTag("etfuturum.endermite", new AspectList(), new ThaumcraftApi.EntityTagsNBT[0]
        // );

        // entity
        ThaumcraftApi.registerEntityTag(
            "etfuturum.rabbit",
            new AspectList().add(Aspect.BEAST, 1)
                .add(Aspect.EARTH, 1)
                .add(Aspect.MOTION, 1));

        ThaumcraftApi.registerEntityTag(
            "etfuturum.endermite",
            new AspectList().add(Aspect.BEAST, 1)
                .add(Aspect.ELDRITCH, 1)
                .add(Aspect.TRAVEL, 1));
        ThaumcraftApi.registerEntityTag(
            "etfuturum.snow_golem",
            new AspectList().add(Aspect.COLD, 3)
                .add(Aspect.WATER, 1));

        ThaumcraftApi.registerEntityTag(
            "etfuturum.end_crystal",
            new AspectList().add(Aspect.ELDRITCH, 3)
                .add(Aspect.MAGIC, 3)
                .add(Aspect.HEAL, 3));
        ThaumcraftApi.registerEntityTag(
            "etfuturum.ender_dragon",
            new AspectList().add(Aspect.ELDRITCH, 20)
                .add(Aspect.BEAST, 20)
                .add(Aspect.ENTROPY, 20));

        ThaumcraftApi.registerEntityTag(
            "etfuturum.villager_zombie",
            new AspectList().add(Aspect.UNDEAD, 2)
                .add(Aspect.MAN, 1)
                .add(Aspect.EARTH, 1));

        ThaumcraftApi.registerEntityTag(
            "etfuturum.husk",
            new AspectList().add(Aspect.UNDEAD, 2)
                .add(Aspect.MAN, 1)
                .add(Aspect.FIRE, 1));
        ThaumcraftApi.registerEntityTag(
            "etfuturum.stray",
            new AspectList().add(Aspect.UNDEAD, 3)
                .add(Aspect.MAN, 1)
                .add(Aspect.TRAP, 1));

        // items
        // ThaumcraftApi.registerObjectTag(new ItemStack(ModItems), new AspectList() );
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.BEETROOT.get()),
            new AspectList().add(Aspect.CROP, 1)
                .add(Aspect.HUNGER, 1));
        ThaumcraftApi
            .registerObjectTag(new ItemStack(ModItems.BEETROOT_SEEDS.get()), new AspectList().add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.CHORUS_FRUIT.get()),
            new AspectList().add(Aspect.ELDRITCH, 1)
                .add(Aspect.SENSES, 1)
                .add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.CHORUS_FRUIT_POPPED.get()),
            new AspectList().add(Aspect.ELDRITCH, 1)
                .add(Aspect.SENSES, 1)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.DRAGON_BREATH.get()),
            new AspectList().add(Aspect.DARKNESS, 2)
                .add(Aspect.ENTROPY, 2)
                .add(Aspect.FIRE, 2));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.ELYTRA.get()),
            new AspectList().add(Aspect.FLIGHT, 4)
                .add(Aspect.MOTION, 3));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.PRISMARINE_CRYSTALS.get()),
            new AspectList().add(Aspect.WATER, 1)
                .add(Aspect.CRYSTAL, 1)
                .add(Aspect.LIGHT, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.PRISMARINE_SHARD.get()),
            new AspectList().add(Aspect.WATER, 1)
                .add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.TOTEM_OF_UNDYING.get()),
            new AspectList().add(Aspect.ORDER, 2)
                .add(Aspect.ENTROPY, 2)
                .add(Aspect.LIFE, 5)
                .add(Aspect.UNDEAD, 2));

        ThaumcraftApi.registerObjectTag(
            ModItems.MUTTON_RAW.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.BEAST, 2)
                .add(Aspect.LIFE, 1)
                .add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(
            ModItems.MUTTON_COOKED.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.CRAFT, 1)
                .add(Aspect.BEAST, 1)
                .add(Aspect.LIFE, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.RABBIT_RAW.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.BEAST, 2)
                .add(Aspect.LIFE, 1)
                .add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(
            ModItems.RABBIT_COOKED.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.CRAFT, 1)
                .add(Aspect.BEAST, 1)
                .add(Aspect.LIFE, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.RABBIT_HIDE.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.BEAST, 1)
                .add(Aspect.ARMOR, 1));
        ThaumcraftApi.registerObjectTag(
            ModItems.RABBIT_FOOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.BEAST, 1)
                .add(Aspect.ARMOR, 1)
                .add(Aspect.MOTION, 2));

        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.TIPPED_ARROW.get()),
            new AspectList().add(Aspect.WEAPON, 1)
                .add(Aspect.MAGIC, 2));

        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.PIGSTEP_RECORD.get()),
            new AspectList().add(Aspect.SENSES, 4)
                .add(Aspect.AIR, 4)
                .add(Aspect.GREED, 4)
                .add(Aspect.FIRE, 2)
                .add(Aspect.BEAST, 2));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModItems.OTHERSIDE_RECORD.get()),
            new AspectList().add(Aspect.SENSES, 4)
                .add(Aspect.AIR, 4)
                .add(Aspect.GREED, 4)
                .add(Aspect.FIRE, 2)
                .add(Aspect.ELDRITCH, 2));

        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList().add(Aspect.TRAP, 1)
                .add(Aspect.WATER, 1)
                .add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 0),
            new AspectList().add(Aspect.CRYSTAL, 1)
                .add(Aspect.WATER, 1));
        // regen
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8193),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.HEAL, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8225),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.HEAL, 6)
                .add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8257),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.HEAL, 3));
        // speed
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8194),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.MOTION, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8226),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.MOTION, 6)
                .add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8258),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.MOTION, 3));
        // fire res
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8227),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.FIRE, 2)
                .add(Aspect.ARMOR, 1));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8259),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.FIRE, 2)
                .add(Aspect.ARMOR, 1));
        // poison
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8196),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.POISON, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8228),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.POISON, 6)
                .add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8260),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.POISON, 3));
        // healing
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8229),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.HEAL, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8261),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.HEAL, 3));
        // NV
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8230),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.SENSES, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8262),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.SENSES, 3));
        // weakness
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8232),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.DEATH, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8264),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.DEATH, 3));
        // strength
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8201),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.WEAPON, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8233),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.WEAPON, 6)
                .add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8264),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.WEAPON, 3));
        // slowness
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8234),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.TRAP, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8266),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.TRAP, 3));
        // leaping
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8235),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.FLIGHT, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8267),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.FLIGHT, 3));
        // damage
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8236),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.DEATH, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8268),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.DEATH, 3));
        // water breathing
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8237),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.AIR, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8269),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.AIR, 3));
        // invis
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8238),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.SENSES, 3));
        ThaumcraftApi.registerObjectTag(
            ModItems.LINGERING_POTION.newItemStack(1, 8270),
            new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE))
                .add(Aspect.SENSES, 3));

        // blocks
        // ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.), new AspectList() );
        ThaumcraftApi
            .registerObjectTag(new ItemStack(ModBlocks.BEACON.get()), new AspectList(new ItemStack(Blocks.beacon)));
        ThaumcraftApi
            .registerObjectTag(new ItemStack(ModBlocks.BEETROOTS.get()), new AspectList().add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.BREWING_STAND.get()),
            new AspectList(new ItemStack(Blocks.brewing_stand)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.CHORUS_FLOWER.get()),
            new AspectList().add(Aspect.ELDRITCH, 1)
                .add(Aspect.SENSES, 1)
                .add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.CHORUS_PLANT.get()),
            new AspectList().add(Aspect.ELDRITCH, 1)
                .add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DAYLIGHT_DETECTOR.get()),
            new AspectList(new ItemStack(Blocks.daylight_detector)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.ENCHANTMENT_TABLE.get()),
            new AspectList(new ItemStack(Blocks.enchanting_table)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.END_ROD.get()),
            new AspectList().add(Aspect.LIGHT, 2)
                .add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.GRASS_PATH.get()),
            new AspectList(new ItemStack(Blocks.dirt)).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.FROSTED_ICE.get()),
            new AspectList(new ItemStack(Blocks.ice)).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(
            ModBlocks.LOG_STRIPPED.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList(new ItemStack(Blocks.log)));
        ThaumcraftApi.registerObjectTag(
            ModBlocks.LOG2_STRIPPED.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList(new ItemStack(Blocks.log2)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.MAGMA.get()),
            new AspectList().add(Aspect.FIRE, 2)
                .add(Aspect.EARTH, 1));
        ThaumcraftApi
            .registerObjectTag(new ItemStack(ModBlocks.ROSE.get()), new AspectList(new ItemStack(Blocks.red_flower)));

        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.BROWN_MUSHROOM.get()),
            new AspectList(new ItemStack(Blocks.brown_mushroom)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.RED_MUSHROOM.get()),
            new AspectList(new ItemStack(Blocks.red_mushroom)));

        ThaumcraftApi.registerObjectTag(
            ModBlocks.RED_SANDSTONE.newItemStack(1, 0),
            new AspectList(new ItemStack(Blocks.sandstone)));
        // ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.red_sandstone, 1, 1), new AspectList(new
        // ItemStack(Blocks.sandstone,1,1)) );
        // ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.red_sandstone, 1, 2), new AspectList(new
        // ItemStack(Blocks.sandstone,1,2)) );

        ThaumcraftApi.registerObjectTag(
            ModBlocks.SPONGE.newItemStack(1, 0),
            new AspectList().add(Aspect.EARTH, 1)
                .add(Aspect.TRAP, 1)
                .add(Aspect.VOID, 1));
        ThaumcraftApi.registerObjectTag(
            ModBlocks.SPONGE.newItemStack(1, 1),
            new AspectList().add(Aspect.EARTH, 1)
                .add(Aspect.TRAP, 1)
                .add(Aspect.WATER, 1));

        ThaumcraftApi.registerObjectTag(
            ModBlocks.STONE.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            new AspectList(new ItemStack(Blocks.stone)));
        // ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.stone,1,3), new AspectList(new
        // ItemStack(Blocks.stone)) );
        // ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.stone,1,5), new AspectList(new
        // ItemStack(Blocks.stone)) );

        // copies

        for (int i = 0; i < ModBlocks.TRAPDOORS.length; i++) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.TRAPDOORS[i].get()),
                new AspectList(new ItemStack(Blocks.trapdoor)));
        }

        for (int i = 0; i < ModBlocks.FENCE_GATES.length; i++) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.FENCE_GATES[i].get()),
                new AspectList(new ItemStack(Blocks.fence_gate)));
        }

        for (int i = 0; i < ModBlocks.DOORS.length; i++) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.DOORS[i].get()),
                new AspectList(new ItemStack(Items.wooden_door)));
        }

        for (int i = 0; i < ModBlocks.BUTTONS.length; i++) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.BUTTONS[i].get()),
                new AspectList(new ItemStack(Blocks.wooden_button)));
        }

        for (int i = 0; i < ModBlocks.PRESSURE_PLATES.length; i++) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModBlocks.PRESSURE_PLATES[i].get()),
                new AspectList(new ItemStack(Blocks.wooden_pressure_plate)));
        }

        for (int i = 0; i < ModItems.BOATS.length; i++) {
            ThaumcraftApi
                .registerObjectTag(new ItemStack(ModItems.BOATS[i].get()), new AspectList(new ItemStack(Items.boat)));
        }

        for (int i = 0; i < ModItems.CHEST_BOATS.length; i++) {
            ThaumcraftApi.registerObjectTag(
                new ItemStack(ModItems.CHEST_BOATS[i].get()),
                new AspectList(new ItemStack(Items.boat)));
        }

        for (int i = 0; i < 16; i++) {
            // ConcreteRegistry.concretePowders.put(color, new BlockConcretePowder(color));
            // GameRegistry.registerBlock(ConcreteRegistry.concretePowders.get(color), ItemBlockConcrete.class,
            // "concrete_powder_" + color.getUnlocalizedName());
            // ConcreteRegistry.concretes.put(color, new BlockConcrete(color));
            // GameRegistry.registerBlock(ConcreteRegistry.concretes.get(color), ItemBlockConcrete.class, "concrete_" +
            // color.getUnlocalizedName());

            ThaumcraftApi.registerObjectTag(
                ModBlocks.CONCRETE_POWDER.newItemStack(8, i),
                new AspectList().add(Aspect.EARTH, 3)
                    .add(Aspect.ENTROPY, 2));
            ThaumcraftApi.registerObjectTag(
                ModBlocks.CONCRETE.newItemStack(8, i),
                new AspectList(ModBlocks.CONCRETE_POWDER.newItemStack(8, i)).add(Aspect.WATER, 1)
                    .add(Aspect.ORDER, 1));

        }

        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.TINTED_GLASS.get()),
            new AspectList(new ItemStack(Blocks.glass)));

        ThaumcraftApi
            .registerObjectTag(new ItemStack(ModBlocks.ANVIL.get()), new AspectList(new ItemStack(Blocks.anvil)));

        // ores

        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_COAL_ORE.get()),
            new AspectList(new ItemStack(Blocks.coal_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_IRON_ORE.get()),
            new AspectList(new ItemStack(Blocks.iron_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_GOLD_ORE.get()),
            new AspectList(new ItemStack(Blocks.gold_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_REDSTONE_ORE.get()),
            new AspectList(new ItemStack(Blocks.redstone_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_LAPIS_ORE.get()),
            new AspectList(new ItemStack(Blocks.lapis_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_DIAMOND_ORE.get()),
            new AspectList(new ItemStack(Blocks.diamond_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_EMERALD_ORE.get()),
            new AspectList(new ItemStack(Blocks.emerald_ore)));
        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.DEEPSLATE_COPPER_ORE.get()),
            new AspectList(new ItemStack(ModBlocks.COPPER_ORE.get())));

        ThaumcraftApi.registerObjectTag(
            new ItemStack(ModBlocks.NETHER_GOLD_ORE.get()),
            new AspectList(new ItemStack(Blocks.gold_ore)));
    }

    public static void doRecipes() {
        // TODO is there supposed to be Thaumcraft related Recipes? If so, what would they be?
    }

    public static int getHasteEnchID() {
        return Config.enchHaste.effectId;
    }

}
