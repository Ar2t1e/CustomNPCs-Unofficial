package noppes.npcs.util;

import net.minecraft.nbt.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import noppes.npcs.constants.EnumParts;

import java.util.UUID;

public class ModData {

    private static CompoundTag exampleBlocks;
    private static CompoundTag exampleItems;
    private static CompoundTag exampleParticles;
    private static final String t = "                ";

    public static CompoundTag getExampleBlocks() {
        if (exampleBlocks == null) {
            exampleBlocks = new CompoundTag();
            ListTag listBlocks = new ListTag();
            listBlocks.add(getExampleBlock());
            listBlocks.add(getExampleFacingBlock());
            listBlocks.add(getExampleLiquid());
            listBlocks.add(getExampleChest());
            listBlocks.add(getExampleContainer());
            listBlocks.add(getExampleStairs());
            listBlocks.add(getExampleSlab());
            listBlocks.add(getExamplePortal());
            listBlocks.add(getExampleDoor());
            exampleBlocks.put("Blocks", listBlocks);
        }
        return exampleBlocks;
    }

    public static CompoundTag getExampleBlock() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "blockexample");
        compound.putByte("BlockType", (byte) 0);
        compound.putBoolean("IsLadder", false);
        compound.putBoolean("IsValidSpawn", true);

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putInt("lightLevel", 0);
        nbtProperties.putFloat("destroyTime", 5.0F);
        nbtProperties.putFloat("explosionResistance", 10.0f);
        nbtProperties.putString("sound", "STONE");
        compound.put("Properties", nbtProperties);

        ListTag aabb = new ListTag();
        aabb.add(DoubleTag.valueOf(0.0625d));
        aabb.add(DoubleTag.valueOf(0.0625d));
        aabb.add(DoubleTag.valueOf(0.0625d));
        aabb.add(DoubleTag.valueOf(0.9375d));
        aabb.add(DoubleTag.valueOf(0.9375d));
        aabb.add(DoubleTag.valueOf(0.9375d));
        compound.put("AABB", aabb);

        String sb = "Tags for creating a simple block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- format of tag values must be respected;\n" +
                t + "1 key 'RegistryName'; type: 'String'; format: '\"value\"'; des - 'Required' Specified name for block registration;\n" +
                t + "2 key 'BlockType'; type: 'Byte'; format: '0b'<>'255b'; des - 'Required' Used to determine the block type during registration.;\n" +
                t + "3 key 'IsLadder'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is a vertical ladder;\n" +
                t + "5 key 'IsValidSpawn'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Mobs may spawn on the block;\n" +
                t + "6 key 'Properties'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' Needed to set properties for your block; code body:\n" +
                t + "    6.01 key 'noCollission'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Avoid collision between entity and block\n" +
                t + "    6.02 key 'noOcclusion'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Is block occlusive and does it block the passage of light or fluid?\n" +
                t + "    6.03 key 'randomTicks'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block randomly triggers a time tick event\n" +
                t + "    6.04 key 'dynamicShape'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block's hitbox shape can change\n" +
                t + "    6.05 key 'ignitedByLava'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is ignited by nearby lava\n" +
                t + "    6.06 key 'liquid'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is liquid\n" +
                t + "    6.07 key 'forceSolidOn'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Force enable block hardness\n" +
                t + "    6.08 key 'forceSolidOff'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Force disable block hardness\n" +
                t + "    6.09 key 'isAir'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is Air\n" +
                t + "    6.10 key 'noParticlesOnBreak'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' No particles when block is destroyed\n" +
                t + "    6.11 key 'replaceable'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is replaced by another when the player attempts to place another block in the same place\n" +
                t + "    6.12 key 'noLootTable'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block does not have a table of item drops when destroyed\n" +
                t + "    6.13 key 'requiresCorrectToolForDrops'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' \n" +
                t + "    6.14 key 'lightLevel'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; min: 0; max: 15; default: 0; des - 'Can be excluded' Block is a light source, where the value is the illumination range of the blocks around it\n" +
                t + "    6.15 key 'mapColor'; type: 'Integer' (has String type below); format: '-2147483648'<>'0'<>'2147483647'; default: '0' (black); des - 'Can be excluded' Hex block color for the minimap\n" +
                t + "    6.16 key 'friction'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.0f'; max: '1.0f'; default: '0.6f'; des - 'Can be excluded' Coefficient of friction of the block is responsible for the degree of braking of entities\n" +
                t + "    6.17 key 'speedFactor'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.05f'; max: '10.0f'; default: '1.0f'; des - 'Can be excluded' Multiplier for the speed of movement of entities in a block\n" +
                t + "    6.18 key 'jumpFactor'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.05f'; max: '10.0f'; default: '1.0f'; des - 'Can be excluded' Multiplier for the jump force of entities from a block\n" +
                t + "    6.19 key 'destroyTime'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.0f'; max: '6000.0f'; default: '0.0f'; des - 'Can be excluded' Time it takes for a player to destroy a block in seconds\n" +
                t + "    6.20 key 'explosionResistance'; 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.0f'; max: '3.4028235e+38f'; default: '0.0f'; des - 'Can be excluded' Value of resistance to the force of destruction of a block by an explosion\n" +
                t + "    6.21 key 'mapColor'; type: 'String' (has Integer type above); format: '\"value\"'; default: 'none' (black); des - 'Can be excluded' Minimap block color from standards. Options:\n" +
                t + "          grass, sand, wool, fire, ice, metal, plant, snow, clay, dirt, stone, water, wood, quartz, color_light_blue, color_light_green, color_light_gray, color_orange, color_magenta, color_yellow, color_pink, color_gray, color_cyan, color_purple, color_blue,\n" +
                t + "          color_brown, color_green, color_red, color_black, gold, diamond, lapis, emerald, podzol, nether, terracotta_light_blue, terracotta_light_green, terracotta_light_gray, terracotta_white, terracotta_orange, terracotta_magenta, terracotta_yellow,\n" +
                t + "          terracotta_pink, terracotta_gray, terracotta_cyan, terracotta_purple, terracotta_blue, terracotta_brown, terracotta_green, terracotta_red, terracotta_black, crimson_nylium, crimson_stem, crimson_hyphae, warped_nylium, warped_stem, warped_hyphae,\n" +
                t + "          warped_wart_block, deepslate, raw_iron, glow_lichen;\n" +
                t + "    6.22 key 'sound'; type: 'String' (has CompoundTag type below); format: '\"value\"'; default: 'stone'; des - 'Can be excluded' Sound settings for the standards block. Options:\n" +
                t + "          wood, gravel, grass, lily_pad, stone, metal, glass, wool, sand, snow, powder_snow, ladder, anvil, slime_block, honey_block, wet_grass, coral_block, bamboo, bamboo_sapling, scaffolding, sweet_berry_bush, crop, hard_crop, vine, nether_wart, lantern,\n" +
                t + "          stem, nylium, fungus, roots, shroomlight, weeping_vines, twisting_vines, soul_sand, soul_soil, basalt, wart_block, \"netherrack, nether_bricks, nether_sprouts, nether_ore, bone_block, netherite_block, ancient_debris, lodestone, chain, nether_gold_ore,\n" +
                t + "          gilded_blackstone, candle, amethyst, amethyst_cluster, small_amethyst_bud, medium_amethyst_bud, large_amethyst_bud, tuff, calcite, dripstone_block, pointed_dripstone, copper, cave_vines, spore_blossom, azalea, flowering_azalea, moss_carpet, pink_petals,\n" +
                t + "          moss, big_dripleaf, small_dripleaf, rooted_dirt, hanging_roots, azalea_leaves, sculk_sensor, sculk_catalyst, sculk, sculk_vein, sculk_shrieker, glow_lichen, deepslate, deepslate_bricks, deepslate_tiles, polished_deepslate, froglight, frogspawn,\n" +
                t + "          mangrove_roots, muddy_mangrove_roots, mud, mud_bricks, packed_mud, hanging_sing, nether_wood_hanging_sing, bamboo_wood_hanging_sing, bamboo_wood, nether_wood, cherry_wood, cherry_sapling, cherry_leaves, cherry_wood_hanging_sing, chiseled_bookshelf,\n" +
                t + "          suspicious_sand, suspicious_gravel, decorated_pot, decorated_pot_cracked;\n" +
                t + "    6.23 key 'sound'; type: 'CompoundTag' (has String type above); format: '{}'; default: 'not used'; des - 'Can be excluded' Create sound settings for the block:\n" +
                t + "        6.23.1 key 'volume'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.05f'; max: '5.0f'; default: '1.0f'; des - 'Can be excluded'\n" +
                t + "        6.23.2 key 'pitch'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; min: '0.05f'; max: '5.0f'; default: '1.0f'; des - 'Can be excluded'\n" +
                t + "        6.23.3 key 'breakSound'; type: 'String'; format: '\"value\"'; default: 'empty'; des - 'Can be excluded' Registered key (name) of the sound event; example: 'minecraft:block.stone.break'\n" +
                t + "        6.23.4 key 'stepSound'; type: 'String'; format: '\"value\"'; default: 'empty'; des - 'Can be excluded' Registered key (name) of the sound event; example: 'block.metal.step'\n" +
                t + "        6.23.5 key 'placeSound'; type: 'String'; format: '\"value\"'; default: 'empty'; des - 'Can be excluded' Registered key (name) of the sound event; example: 'block.glass.place'\n" +
                t + "        6.23.6 key 'hitSound'; type: 'String'; format: '\"value\"'; default: 'empty'; des - 'Can be excluded' Registered key (name) of the sound event; example: 'block.wool.hit'\n" +
                t + "        6.23.7 key 'fallSound'; type: 'String'; format: '\"value\"'; default: 'empty'; des - 'Can be excluded' Registered key (name) of the sound event; example: 'block.sand.fall'\n" +
                t + "    6.24 key 'pushReaction'; type: 'String'; format: '\"value\"'; default: 'normal'; des - 'Can be excluded' Reaction used to push the block (piston, player, etc.). Options:\n" +
                t + "          destroy, block, ignore, push_only, normal;\n" +
                t + "    6.25 key 'isValidSpawn'; type: 'String'; format: '\"value\"'; default: 'not used' (from above, illumination less than 14); des - 'Can be excluded' Permission to spawn entities near a block. Options:\n" +
                t + "          always, never, ocelotOrParrot, polarBear, fireImmune, (EntityType registered key);\n" +
                t + "    6.26 key 'isRedstoneConductor'; type: 'String'; format: '\"value\"'; default: 'not used' (is collision shape full block); des - 'Can be excluded' Uses signaling Redstone liner. Options:\n" +
                t + "          always, never;\n" +
                t + "    6.27 key 'isSuffocating'; type: 'String'; format: '\"value\"'; default: 'not used' (blocks motion and is collision shape full block); des - 'Can be excluded' Conditions under which an entity suffocates inside a block. Options:\n" +
                t + "          never, pistonBase, shulkerBox;\n" +
                t + "    6.28 key 'isViewBlocking'; type: 'String'; format: '\"value\"'; default: 'not used' (blocks motion and is collision shape full block); des - 'Can be excluded' Conditions for blocking line of sight through a block for entities. Options:\n" +
                t + "          always, never, snowLayers, pistonBase, shulkerBox;\n" +
                t + "    6.29 key 'hasPostProcess'; type: 'String'; format: '\"value\"'; default: 'not used' (false); des - 'Can be excluded' Conditions when post-processing of the render is required (for example, shaders). Options:\n" +
                t + "          always;\n" +
                t + "    6.30 key 'emissiveRendering'; type: 'String'; format: '\"value\"'; default: 'not used' (false); des - 'Can be excluded' Conditions where the block will be equally bright regardless of the ambient lighting. Options:\n" +
                t + "          always, sculkSensorBlock;\n" +
                t + "    6.31 key 'offsetType'; type: 'String'; format: '\"value\"'; default: 'not used' (none); des - 'Can be excluded' Texture offset relative to coordinates. Options:\n" +
                t + "          xz, xyz, none;\n" +
                t + "    6.32 key 'instrument'; type: 'String'; format: '\"value\"'; default: 'not used' (harp); des - 'Can be excluded' Sound of the instrument when destroying or attacking a block. Options:\n" +
                t + "          harp, basedrum, snare, hat, bass, flute, bell, guitar, chime, xylophone, iron_xylophone, cow_bell, didgeridoo, bit, banjo, pling, zombie, skeleton, creeper, dragon, wither_skeleton, piglin, custom_head;\n" +
                t + "    6.33 key 'lootFrom'; type: 'String'; format: '\"value\"'; default: 'not used' (empty); des - 'Can be excluded' Use a loot table from another block; example: 'minecraft:stone'\n" +
                t + "    6.34 key 'lootTable'; type: 'String'; format: '\"value\"'; default: 'not used' (empty); des - 'Can be excluded' Use the table specified by the registered key (name); example: 'minecraft:chests/spawn_bonus_chest'; look class: 'net.minecraft.world.level.storage.loot.BuiltInLootTables'\n" +
                t + "    6.35 key 'requiredFeatures'; type: 'ListTag'; format: '[]'; listType: 'CompoundTag'; tagFormat: '{}'; default: 'not used'; des - 'Can be excluded' A list of required features in the world for a block to exist in it:\n" +
                t + "        6.35.1 key 'UniverseId' in 'CompoundTag' option; type: 'String'; format: '\"value\"'; des - 'Required if specified' requirement ID\n" +
                t + "        6.35.2 key 'Name' in 'CompoundTag' option; type: 'String'; format: '\"value\"'; des - 'Required if specified' sign ID\n" +
                t + "7 key 'AABB'; type: 'ListTag'; format: '[]'; listType: 'Double'; tagFormat: '4.9e-324'<>'0.000000000'<>'1.7976931348623157e+308'; size: '6 min'; des - 'Can be excluded' Use this if you want to specify the exact dimensions of a block's hitbox:\n" +
                t + "    [X min, Y min, Z min, X max, Y max, Z max]";
        compound.putString("-Description", sb);
        return compound;
    }

    public static CompoundTag getExampleFacingBlock() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "facingblockexample");
        compound.putByte("BlockType", (byte) 0);

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putBoolean("isAir", false);
        compound.put("Properties", nbtProperties);

        CompoundTag nbtProperty = new CompoundTag();
        nbtProperty.putByte("Type", (byte) 4);
        nbtProperty.putString("Name", "facing");
        compound.put("Property", nbtProperty);

        String sb = "Tags for creating a simple block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'IsLadder', 'IsValidSpawn', 'Properties', 'AABB' - see description of block 'blockexample';";
        compound.putString("-Description", sb);
        return compound;
    }

    public static CompoundTag getExampleLiquid() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "liquidexample");
        compound.putByte("BlockType", (byte) 1);
        compound.putBoolean("HasInGameRules", false);
        compound.putBoolean("AddCauldron", true);
        compound.putInt("SlopeFindDistance", 4);
        compound.putInt("DropOff", 1);
        compound.putInt("TickDelay", 5);
        compound.putFloat("Resistance", 100.0f);
        compound.putString("SoundAmbientFlowing", "block.water.ambient");
        compound.putString("SoundBucketFill", "item.bucket.fill");
        compound.putString("ParticleUnderFluid", "underwater");
        compound.putString("ParticleDripParticle", "dripping_water");

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putBoolean("liquid", true);
        nbtProperties.putBoolean("replaceable", true);
        nbtProperties.putBoolean("noCollission", true);
        nbtProperties.putBoolean("noLootTable", true);
        nbtProperties.putFloat("explosionResistance", 2.0f);
        nbtProperties.putString("mapColor", "WATER");
        nbtProperties.putInt("mapColor", 0x4040FF);
        nbtProperties.putString("sound", "EMPTY");
        nbtProperties.putString("pushReaction", "DESTROY");
        compound.put("Properties", nbtProperties);

        CompoundTag nbtFluidType = new CompoundTag();
        nbtFluidType.putInt("tickRate", 5);
        nbtFluidType.putInt("slopeFindDistance", 4);
        nbtFluidType.putInt("levelDecreasePerBlock", 4);

        nbtFluidType.putInt("fogColor", 0xFFFFFF);
        nbtFluidType.putInt("tintColor", 0xFFFFFF);

        nbtFluidType.putInt("lightLevel", 5);
        nbtFluidType.putInt("density", 1100);
        nbtFluidType.putInt("viscosity", 900);
        nbtFluidType.putInt("temperature", 300);
        compound.put("FluidType", nbtFluidType);

        String sb = "Tags for creating a simple block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'IsLadder', 'IsValidSpawn', 'Properties', 'AABB' - see description of block 'blockexample';\n" +
                t + "8 key 'HasInGameRules'; type: 'Boolean'; format: false='0b', true='1b'; default: 'uses water resolution'; des - 'Can be excluded' Create a flow permission for this fluid for the command block;\n" +
                t + "9 key 'AddCauldron'; type: 'Boolean'; format: false='0b', true='1b'; default: 'false'; des - 'Can be excluded' Create a flow permission for this fluid for the command block;\n"
                ;
        compound.putString("-Description", sb);
        return compound;
    }

    public static CompoundTag getExampleChest() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "chestexample");
        compound.putByte("BlockType", (byte) 2);
        compound.putBoolean("IsChest", true);
        compound.putInt("Size", 14);
        compound.putInt("GUIColor", 0x46AB86);
        compound.putString("Name", "Custom Chest");

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putString("sound", "WOOD");
        compound.put("Properties", nbtProperties);
        return compound;
    }

    public static CompoundTag getExampleContainer() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "containerexample");
        compound.putByte("BlockType", (byte) 2);
        compound.putInt("Size", 96);
        compound.putIntArray("GUIColor", new int[] { 0x00DC8C, 0xDC8000 });
        compound.putString("Name", "Custom Container");
        compound.putBoolean("IsOBJModel", true);

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putString("sound", "STONE");
        compound.put("Properties", nbtProperties);

        ListTag aabb = new ListTag();
        aabb.add(DoubleTag.valueOf(0.0625d));
        aabb.add(DoubleTag.valueOf(0.0d));
        aabb.add(DoubleTag.valueOf(0.0625d));
        aabb.add(DoubleTag.valueOf(0.9375d));
        aabb.add(DoubleTag.valueOf(1.0d));
        aabb.add(DoubleTag.valueOf(0.9375d));
        compound.put("AABB", aabb);
        return compound;
    }

    public static CompoundTag getExampleStairs() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "stairsexample");
        compound.putByte("BlockType", (byte) 3);
        compound.putString("Planks", "oak_planks");

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putBoolean("isAir", false);
        nbtProperties.putString("sound", "STONE");
        compound.put("Properties", nbtProperties);
        return compound;
    }

    public static CompoundTag getExampleSlab() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "slabexample");
        compound.putByte("BlockType", (byte) 4);

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putBoolean("isAir", false);
        nbtProperties.putString("sound", "STONE");
        compound.put("Properties", nbtProperties);
        return compound;
    }

    public static CompoundTag getExamplePortal() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "portalexample");
        compound.putByte("BlockType", (byte) 5);
        compound.putInt("DimensionID", 100);
        compound.putInt("HomeDimensionID", 0);

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putBoolean("isAir", false);
        nbtProperties.putFloat("explosionResistance", 2000.0f);
        nbtProperties.putString("sound", "PORTAL");
        compound.put("Properties", nbtProperties);

        CompoundTag nbtRender = new CompoundTag();
        nbtRender.putFloat("SecondSpeed", 800.0f);
        nbtRender.putString("SpawnParticle", "CRIT");
        nbtRender.putFloat("Transparency", 0.5f);
        compound.put("RenderData", nbtRender);
        return compound;
    }

    public static CompoundTag getExampleDoor() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "doorexample");
        compound.putByte("BlockType", (byte) 6);

        CompoundTag nbtProperties = new CompoundTag();
        nbtProperties.putBoolean("noOcclusion", true);
        nbtProperties.putBoolean("ignitedByLava", true);
        nbtProperties.putFloat("destroyTime", 3.0f);
        nbtProperties.putFloat("explosionResistance", 3.0f);
        nbtProperties.putString("mapColor", "WOOD");
        nbtProperties.putString("instrument", "BASS");
        nbtProperties.putString("pushReaction", "DESTROY");
        compound.put("Properties", nbtProperties);

        CompoundTag blockSetType = new CompoundTag();
        blockSetType.putString("Name", "iron");
        blockSetType.putString("SoundType", "WOOD");
        blockSetType.putString("SoundDoorClose", "block.wooden_door.close");
        blockSetType.putString("SoundDoorOpen", "block.wooden_door.open");
        blockSetType.putString("SoundTrapDoorClose", "block.wooden_trapdoor.close");
        blockSetType.putString("SoundTrapDoorOpen", "block.wooden_trapdoor.open");
        blockSetType.putString("SoundPlateClickOff", "block.wooden_pressure_plate.click_off");
        blockSetType.putString("SoundPlateClickOn", "block.wooden_pressure_plate.click_on");
        blockSetType.putString("SoundButtonClickOff", "block.wooden_button.click_off");
        blockSetType.putString("SoundButtonClickOn", "block.wooden_button.click_on");
        blockSetType.putBoolean("CanOpenByHand", true);
        compound.put("BlockSetType", blockSetType);
        return compound;
    }

    public static CompoundTag getExampleItems() {
        if (exampleItems == null) {
            exampleItems = new CompoundTag();
            ListTag listItems = new ListTag();
            listItems.add(getExampleItem());
            listItems.add(getExampleWeapon());
            listItems.add(getExampleTool());
            listItems.add(getExampleAxe());
            listItems.add(getExampleArmor());
            listItems.add(getExampleOBJArmor());
            listItems.add(getExampleShield());
            listItems.add(getExampleBow());
            listItems.add(getExampleFood());
            listItems.add(getExampleFishingRod());
            exampleItems.put("Items", listItems);

            ListTag listPotion = new ListTag();
            listPotion.add(getExamplePotion());
            exampleItems.put("Potions", listPotion);
        }
        return exampleItems;
    }

    public static CompoundTag getExampleItem() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "itemexample");
        compound.putByte("ItemType", (byte) 0);

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 64);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExampleWeapon() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "weaponexample");
        compound.putByte("ItemType", (byte) 1);
        compound.putBoolean("ShowInCreative", true);
        compound.putDouble("SpeedAttack", -2.4d);

        ListTag list = new ListTag();
        CompoundTag collectionBlock = new CompoundTag();
        collectionBlock.putString("Name", "minecraft:cobweb");
        collectionBlock.putFloat("Speed", 15.0f);
        list.add(collectionBlock);
        compound.put("CollectionBlocks", list);

        list = new ListTag();
        CompoundTag collectionBlockTag = new CompoundTag();
        collectionBlockTag.putString("Name", "SWORD_EFFICIENT");
        collectionBlockTag.putFloat("Speed", 1.5f);
        list.add(collectionBlockTag);
        compound.put("CollectionBlockTags", list);

        CompoundTag tier = new CompoundTag();
        tier.putInt("MaxStackDamage", 2500);
        tier.putInt("HarvestLevel", 2);
        tier.putInt("Enchantability", 25);
        tier.putFloat("Efficiency", 6.0f);
        tier.putDouble("EntityDamage", 2.5d);
        tier.put("RepairItem", (new ItemStack(Blocks.GOLD_ORE)).save(new CompoundTag()));
        tier.putString("RepairItemTag", ItemTags.GOLD_ORES.location().toString());
        compound.put("Tier", tier);

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 1);
        properties.putString("Rarity", "RARE");
        properties.putBoolean("FireResistant", false);
        properties.putBoolean("CanRepair", true);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExampleTool() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "toolexample");
        compound.putByte("ItemType", (byte) 2);
        compound.putDouble("SpeedAttack", -2.8d);
        compound.putString("ToolClass", "pickaxe");

        ListTag list = new ListTag();
        CompoundTag collectionBlock = new CompoundTag();
        collectionBlock.putString("Name", "minecraft:stone");
        collectionBlock.putFloat("Speed", 15.0f);
        list.add(collectionBlock);
        collectionBlock = new CompoundTag();
        collectionBlock.putString("Name", "minecraft:obsidian");
        collectionBlock.putFloat("Speed", 15.0f);
        list.add(collectionBlock);
        compound.put("CollectionBlocks", list);

        list = new ListTag();
        CompoundTag collectionBlockTag = new CompoundTag();
        collectionBlockTag.putString("Name", "needs_stone_tool");
        collectionBlockTag.putFloat("Speed", 1.5f);
        list.add(collectionBlockTag);
        compound.put("CollectionBlockTags", list);

        CompoundTag tier = new CompoundTag();
        tier.putInt("MaxStackDamage", 2000);
        tier.putInt("HarvestLevel", 3);
        tier.putInt("Enchantability", 25);
        tier.putFloat("Efficiency", 4.0f);
        tier.putDouble("EntityDamage", 0.0d);
        tier.put("RepairItem", (new ItemStack(Items.GOLD_NUGGET)).save(new CompoundTag()));
        tier.putString("RepairItemTag", "obsidian");
        compound.put("Tier", tier);

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 1);
        properties.putString("Rarity", "RARE");
        properties.putBoolean("FireResistant", false);
        properties.putBoolean("CanRepair", true);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExampleAxe() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "axeexample");
        compound.putByte("ItemType", (byte) 2);
        compound.putDouble("SpeedAttack", -2.4d);
        compound.putString("ToolClass", "axe");
        compound.putBoolean("IsOBJModel", true);

        CompoundTag tier = new CompoundTag();
        tier.putInt("MaxStackDamage", 2200);
        tier.putInt("HarvestLevel", 2);
        tier.putInt("Enchantability", 28);
        tier.putFloat("Efficiency", 4.25f);
        tier.putDouble("EntityDamage", 5.0d);
        tier.put("RepairItem", (new ItemStack(Items.GOLD_NUGGET)).save(new CompoundTag()));
        tier.putString("RepairItemTag", "obsidian");
        compound.put("Tier", tier);

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 1);
        properties.putString("Rarity", "RARE");
        properties.putBoolean("FireResistant", false);
        properties.putBoolean("CanRepair", true);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExampleArmor() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "armorexample");
        compound.putByte("ItemType", (byte) 3);
        compound.putString("Material", "GOLD");

        compound.put("RepairItem", (new ItemStack(Items.GOLD_NUGGET)).save(new CompoundTag()));

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 1);
        properties.putString("RepairItem", "minecraft:gold_nugget");
        compound.put("Properties", properties);

        ListTag slots = new ListTag();
        CompoundTag part = new CompoundTag();
            part.putString("Slot", "HEAD");
            part.putInt("MaxStackDamage", 2250);
            part.putInt("Defense", 5);
            part.putFloat("Toughness", 2.2f);
            part.putInt("Enchantability", 22);
            part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        part = new CompoundTag();
            part.putString("Slot", "Chest");
            part.putInt("MaxStackDamage", 3100);
            part.putInt("Defense", 7);
            part.putFloat("Toughness", 3.5f);
            part.putInt("Enchantability", 25);
        part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        part = new CompoundTag();
            part.putString("Slot", "feet");
            part.putInt("MaxStackDamage", 1800);
            part.putInt("Defense", 4);
            part.putFloat("Toughness", 1.8f);
            part.putInt("Enchantability", 22);
            part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        compound.put("EquipmentSlots", slots);
        return compound;
    }

    public static CompoundTag getExampleOBJArmor() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "armorobjexample");
        compound.putByte("ItemType", (byte) 3);
        compound.putString("Material", "IRON");

        compound.put("RepairItem", (new ItemStack(Items.IRON_INGOT)).save(new CompoundTag()));

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 1);
        properties.putString("RepairItem", "minecraft:iron_ingot");
        compound.put("Properties", properties);

        ListTag slots = new ListTag();
        CompoundTag part = new CompoundTag();
            part.putString("Slot", "HEAD");
            part.putInt("MaxStackDamage", 2250);
            part.putInt("Defense", 5);
            part.putFloat("Toughness", 2.2f);
            part.putInt("Enchantability", 22);
            part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        part = new CompoundTag();
            part.putString("Slot", "Chest");
            part.putInt("MaxStackDamage", 3100);
            part.putInt("Defense", 7);
            part.putFloat("Toughness", 3.5f);
            part.putInt("Enchantability", 25);
            part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        part = new CompoundTag();
            part.putString("Slot", "LeGs");
            part.putInt("MaxStackDamage", 2700);
            part.putInt("Defense", 6);
            part.putFloat("Toughness", 2.6f);
            part.putInt("Enchantability", 23);
            part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        part = new CompoundTag();
            part.putString("Slot", "feet");
            part.putInt("MaxStackDamage", 1800);
            part.putInt("Defense", 4);
            part.putFloat("Toughness", 1.8f);
            part.putInt("Enchantability", 22);
            part.putFloat("KnockbackResistance", 0.0F);
        slots.add(part);
        compound.put("EquipmentSlots", slots);

        CompoundTag objData = new CompoundTag();
            ListTag meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.HEAD.name));
            objData.put("Head Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.BODY.name));
            objData.put("Body Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.ARM_RIGHT.name));
            objData.put("Arm Right Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.WRIST_RIGHT.name));
            objData.put("Wrist Right Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.ARM_LEFT.name));
            objData.put("Arm Left Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.WRIST_LEFT.name));
            objData.put("Wrist Left Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.BELT.name));
            objData.put("Belt Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.LEG_RIGHT.name));
            objData.put("Leg Right Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.FEET_RIGHT.name));
            objData.put("Foot Right Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.LEG_LEFT.name));
            objData.put("Leg Left Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.FEET_LEFT.name));
            objData.put("Foot Left Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.FEET_LEFT.name));
            objData.put("Boot Left Mesh Names", meshes);

            meshes = new ListTag();
            meshes.add(StringTag.valueOf(EnumParts.FEET_RIGHT.name));
        objData.put("Boot Right Mesh Names", meshes);

        compound.put("OBJData", objData);

        CompoundTag display = new CompoundTag();
        for (int s = 0; s < 4; s++) {
            String slot = s == 0 ? "CHEST" : s == 1 ? "LEGS" : s == 2 ? "FEET" : "HEAD";
            CompoundTag cameraData = new CompoundTag();
            for (int i = 0; i < 8; i++) {
                String p;
                ListTag rotation = new ListTag();
                ListTag translation = new ListTag();
                ListTag scale = new ListTag();
                switch(i) {
                    case 0: { // THIRD_PERSON_LEFT_HAND
                        p = "thirdperson_lefthand";
                        switch(slot) {
                            case "CHEST": {
                                translation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                translation.add(FloatTag.valueOf(-0.15f));
                                translation.add(FloatTag.valueOf(0.35f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.65f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(90.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(1.15f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.65f)); }
                                break;
                            }
                            default: {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(1.0f));
                                translation.add(FloatTag.valueOf(-0.375f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 1: { // THIRD_PERSON_RIGHT_HAND
                        p = "thirdperson_righthand";
                        switch(slot) {
                            case "CHEST": {
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.35f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.65f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(90.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.65f)); }
                                break;
                            }
                            default: {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(-0.375f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 2: { // FIRST_PERSON_LEFT_HAND
                        p = "firstperson_lefthand";
                        switch(slot) {
                            case "CHEST": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.57f));
                                translation.add(FloatTag.valueOf(0.1f));
                                translation.add(FloatTag.valueOf(-0.085f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.65f));
                                translation.add(FloatTag.valueOf(0.4f));
                                translation.add(FloatTag.valueOf(-0.085f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.72f));
                                translation.add(FloatTag.valueOf(0.435f));
                                translation.add(FloatTag.valueOf(-0.585f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.85f)); }
                                break;
                            }
                            default: {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.57f));
                                translation.add(FloatTag.valueOf(-0.225f));
                                translation.add(FloatTag.valueOf(-0.085f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 3: { // FIRST_PERSON_RIGHT_HAND
                        p = "firstperson_righthand";
                        switch(slot) {
                            case "CHEST": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.85f));
                                translation.add(FloatTag.valueOf(-0.1f));
                                translation.add(FloatTag.valueOf(0.2f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.6f)); }
                                break;
                            }
                            case "LEGS": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.95f));
                                translation.add(FloatTag.valueOf(0.25f));
                                translation.add(FloatTag.valueOf(0.2f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.6f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.95f));
                                translation.add(FloatTag.valueOf(0.4f));
                                translation.add(FloatTag.valueOf(0.2f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.85f)); }
                                break;
                            }
                            default: {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(280.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.85f));
                                translation.add(FloatTag.valueOf(-0.5f));
                                translation.add(FloatTag.valueOf(0.2f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.6f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 4: { // HEAD
                        p = "head";
                        switch(slot) {
                            case "CHEST": {
                                rotation.add(FloatTag.valueOf(270.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(1.0f));
                                translation.add(FloatTag.valueOf(1.65f));
                                break;
                            }
                            case "LEGS": {
                                rotation.add(FloatTag.valueOf(270.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(1.0f));
                                translation.add(FloatTag.valueOf(1.0f));
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.925f));
                                translation.add(FloatTag.valueOf(0.4f));
                                break;
                            }
                            default: { break; }
                        }
                        break;
                    }
                    case 5: { // GUI
                        p = "gui";
                        switch(slot) {
                            case "CHEST": {
                                rotation.add(FloatTag.valueOf(30.0f));
                                rotation.add(FloatTag.valueOf(45.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.49f));
                                translation.add(FloatTag.valueOf(-0.41f));
                                translation.add(FloatTag.valueOf(0.0f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.9f)); }
                                break;
                            }
                            case "LEGS": {
                                rotation.add(FloatTag.valueOf(30.0f));
                                rotation.add(FloatTag.valueOf(45.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.05f));
                                translation.add(FloatTag.valueOf(0.0f));
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(30.0f));
                                rotation.add(FloatTag.valueOf(45.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.3f));
                                translation.add(FloatTag.valueOf(0.0f));
                                break;
                            }
                            default: {
                                rotation.add(FloatTag.valueOf(30.0f));
                                rotation.add(FloatTag.valueOf(45.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(-1.0f));
                                translation.add(FloatTag.valueOf(0.0f));
                                break;
                            }
                        }
                        break;
                    }
                    case 6: { // GROUND
                        p = "ground";
                        switch(slot) {
                            case "CHEST": {
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.25f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.6f)); }
                                break;
                            }
                            case "FEET": {
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.35f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.65f)); }
                                break;
                            }
                            default: {
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(-0.375f));
                                translation.add(FloatTag.valueOf(0.5f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    default: { // FIXED
                        p = "fixed";
                        switch(slot) {
                            case "CHEST": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(-0.65f));
                                translation.add(FloatTag.valueOf(0.45f));
                                break;
                            }
                            case "LEGS": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.05f));
                                translation.add(FloatTag.valueOf(0.475f));
                                break;
                            }
                            case "FEET": {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(0.2f));
                                translation.add(FloatTag.valueOf(0.475f));
                                break;
                            }
                            default: {
                                rotation.add(FloatTag.valueOf(0.0f));
                                rotation.add(FloatTag.valueOf(180.0f));
                                rotation.add(FloatTag.valueOf(0.0f));
                                translation.add(FloatTag.valueOf(0.5f));
                                translation.add(FloatTag.valueOf(-0.85f));
                                translation.add(FloatTag.valueOf(0.4f));
                                for (int l = 0; l < 3; l++) { scale.add(FloatTag.valueOf(0.75f)); }
                                break;
                            }
                        }
                        break;
                    }
                }
                CompoundTag transform = new CompoundTag();
                if (!rotation.isEmpty()) { transform.put("rotation", rotation); }
                if (!translation.isEmpty()) { transform.put("translation", translation); }
                if (!scale.isEmpty()) { transform.put("scale", scale); }
                cameraData.put(p, transform);
            }
            display.put(slot, cameraData);
        }
        compound.put("Display", display);
        return compound;
    }

    public static CompoundTag getExampleShield() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "shieldexample");
        compound.putByte("ItemType", (byte) 4);
        compound.putInt("Enchantability", 15);
        compound.put("RepairItem", (new ItemStack(Items.IRON_NUGGET)).save(new CompoundTag()));

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackDamage", 6500);
        properties.putInt("MaxStackSize", 1);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExampleBow() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "bowexample");
        compound.putByte("ItemType", (byte) 5);
        compound.putInt("Enchantability", 2);
        compound.putDouble("EntityDamage", 2.0d);
        compound.putBoolean("SetFlame", false);
        compound.putFloat("CritChance", 0.25f);
        compound.putFloat("DrawstringSpeed", 20.0f);
        compound.put("RepairItem", (new ItemStack(Items.OAK_PLANKS)).save(new CompoundTag()));

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackDamage", 1250);
        properties.putInt("MaxStackSize", 1);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExampleFood() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "foodexample");
        compound.putByte("ItemType", (byte) 6);
        compound.putInt("UseDuration", 32);

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 32);
        compound.put("Properties", properties);

        CompoundTag foodData = new CompoundTag();
        foodData.putInt("Nutrition", 1);
        foodData.putFloat("Saturation", 0.1f);
        foodData.putBoolean("IsMeat", false);
        foodData.putBoolean("IsFastFood", false);
        foodData.putBoolean("AlwaysEdible", true);
        ListTag list = new ListTag();
        CompoundTag effect = new CompoundTag();
        effect.putString("Name", "fire_resistance");
        effect.putInt("Id", 12);
        effect.putInt("DurationTicks", 45);
        effect.putInt("Amplifier", 0);
        effect.putBoolean("Ambient", true);
        effect.putBoolean("ShowParticles", false);
        effect.putBoolean("ShowIcon", false);
        effect.putFloat("Probability", 0.15f);
        list.add(effect);
        foodData.put("Effects", list);
        compound.put("FoodData", foodData);
        return compound;
    }

    public static CompoundTag getExampleFishingRod() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "fishingrodexample");
        compound.putByte("ItemType", (byte) 8);
        compound.putInt("Enchantability", 5);
        compound.put("RepairItem", (new ItemStack(Items.STICK)).save(new CompoundTag()));
        compound.putInt("AddSpeedBonus", -1);
        compound.putInt("AddLuckBonus", 1);
        compound.putInt("FishingLineColor", 0xFF00EA);
        compound.putString("FishingHookTexture", "custom_fishing_hook");

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackDamage", 150);
        properties.putInt("MaxStackSize", 1);
        compound.put("Properties", properties);
        return compound;
    }

    public static CompoundTag getExamplePotion() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "potionexample");
        compound.putByte("ItemType", (byte) 7);

        CompoundTag properties = new CompoundTag();
        properties.putInt("MaxStackSize", 16);
        compound.put("Properties", properties);

        compound.putString("Category", "beneficial");
        compound.putBoolean("IsInstant", false);
        compound.putBoolean("VisibleInInventory", true);
        compound.putBoolean("VisibleInGui", true);
        compound.putInt("LiquidColor", 0xFFFFFF);

        compound.putInt("BaseDelay", 200);
        compound.putInt("Duration", 20);
        compound.put("CureItem", (new ItemStack(Items.CARROT)).save(new CompoundTag()));

        ListTag potionModifiers = new ListTag();
        potionModifiers.add(getExamplePotionModifier());
        compound.put("Modifiers", potionModifiers);

        return compound;
    }

    public static CompoundTag getExamplePotionModifier() {
        CompoundTag compound = new CompoundTag();
        compound.putString("AttributeName", "generic.maxHealth");
        compound.putString("UUID", UUID.randomUUID().toString());
        compound.putDouble("AttributeDefValue", 5.0d);
        compound.putDouble("AttributeMinValue", -50.0d);
        compound.putDouble("AttributeMaxValue", 50.0d);
        compound.putDouble("Amount", 2.0d);
        compound.putInt("Operation", 2);
        return compound;
    }

    public static CompoundTag getExampleParticles() {
        if (exampleParticles == null) {
            exampleParticles = new CompoundTag();
            ListTag listItems = new ListTag();
            listItems.add(getExampleParticle());
            listItems.add(getExampleOBJParticle());
            exampleParticles.put("Particles", listItems);
        }
        return exampleParticles;
    }

    public static CompoundTag getExampleParticle() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "PARTICLE_EXAMPLE");
        compound.putBoolean("OverrideLimiter", false);

        compound.putInt("ArgumentCount", 0);
        compound.putInt("MaxAge", 60);
        compound.putFloat("Gravity", 0.25f);
        compound.putFloat("Scale", 1.5f);
        ListTag motion = new ListTag();
        motion.add(DoubleTag.valueOf(0.2d));
        motion.add(DoubleTag.valueOf(0.1d));
        motion.add(DoubleTag.valueOf(0.2d));
        compound.put("StartMotion", motion);
        compound.putBoolean("IsRandomMotion", true);
        compound.putBoolean("NotMotionY", true);
        return compound;
    }

    public static CompoundTag getExampleOBJParticle() {
        CompoundTag compound = new CompoundTag();
        compound.putString("RegistryName", "PARTICLE_OBJ_EXAMPLE");
        compound.putBoolean("ShouldIgnoreRange", false);
        compound.putInt("MaxAge", 60);
        compound.putFloat("Gravity", 1.0f / 3.0f);
        compound.putFloat("Scale", 1.0f);
        compound.putString("OBJModel", "ring");
        return compound;
    }

}
