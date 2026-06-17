package noppes.npcs.util;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import noppes.npcs.constants.EnumParts;

import java.util.Objects;
import java.util.UUID;

public class ModData {

    private static NBTTagCompound exampleBlocks;
    private static NBTTagCompound exampleItems;
    private static NBTTagCompound exampleParticles;
    private static final String t = "                ";

    public static NBTTagCompound getExampleBlocks() {
        if (exampleBlocks == null) {
            exampleBlocks = new NBTTagCompound();
                NBTTagList listBlocks = new NBTTagList();
                listBlocks.appendTag(getExampleBlock());
                listBlocks.appendTag(getExampleFacingBlock());
                listBlocks.appendTag(getExampleLiquid());
                listBlocks.appendTag(getExampleChest());
                listBlocks.appendTag(getExampleContainer());
                listBlocks.appendTag(getExampleStairs());
                listBlocks.appendTag(getExampleSlab());
                listBlocks.appendTag(getExamplePortal());
                listBlocks.appendTag(getExampleDoor());
            exampleBlocks.setTag("Blocks", listBlocks);
        }
        return exampleBlocks;
    }

    private static NBTTagCompound getExampleBlock() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "blockexample");
        compound.setByte("BlockType", (byte) 0);
        compound.setFloat("Hardness", 5.0f);
        compound.setFloat("Resistance", 10.0f);
        compound.setFloat("LightLevel", 0.0f);
        compound.setString("SoundType", "GROUND");
        compound.setString("Material", "STONE");
            NBTTagList aabb = new NBTTagList();
            aabb.appendTag(new NBTTagDouble(0.0625d));
            aabb.appendTag(new NBTTagDouble(0.0625d));
            aabb.appendTag(new NBTTagDouble(0.0625d));
            aabb.appendTag(new NBTTagDouble(0.9375d));
            aabb.appendTag(new NBTTagDouble(0.9375d));
            aabb.appendTag(new NBTTagDouble(0.9375d));
        compound.setTag("AABB", aabb);
        compound.setString("BlockRenderType", "MODEL");
        compound.setBoolean("IsLadder", false);
        compound.setBoolean("IsPassable", false);
        compound.setBoolean("IsOpaqueCube", false);
        compound.setBoolean("IsFullCube", false);

        String sb = "Tags for creating a simple block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- format of tag values must be respected;\n" +
                t + "1 key 'RegistryName'; type: 'String'; format: '\"value\"'; des - 'Required' Specified name for block registration;\n" +
                t + "2 key 'BlockType'; type: 'Byte'; format: '0b'<>'255b'; des - 'Required' Used to determine the block type during registration;\n" +
                t + "3 key 'Hardness'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; default: '1.5f'; des - 'Can be excluded' Time it takes for a player to destroy a block in seconds;\n" +
                t + "4 key 'Resistance'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; default: '10.0f'; des - 'Can be excluded' Value of resistance to the force of destruction of a block by an explosion;\n" +
                t + "5 key 'LightLevel'; type: 'Float'; format: '0.0f'<>'1.0f'; default: '0.0f'; des - 'Can be excluded' Block is a light source, where the value is the illumination range (0.0=none, 1.0=full brightness like glowstone);\n" +
                t + "6 key 'SoundType'; type: 'String'; format: '\"value\"'; default: 'STONE'; des - 'Can be excluded' Sound type for the block. Options: WOOD, GROUND, PLANT, METAL, GLASS, CLOTH, SAND, SNOW, LADDER, ANVIL, SLIME, STONE;\n" +
                t + "7 key 'Material'; type: 'String'; format: '\"value\"'; default: 'STONE'; des - 'Can be excluded' Block material type. Options: AIR, GRASS, GROUND, WOOD, IRON, ANVIL, WATER, LAVA, LEAVES, PLANTS, VINE, SPONGE, CLOTH, FIRE, SAND, CIRCUITS, CARPET, GLASS, REDSTONE_LIGHT, TNT, CORAL, ICE, PACKED_ICE, SNOW, CRAFTED_SNOW, CACTUS, CLAY, GOURD, DRAGON_EGG, PORTAL, CAKE, WEB, PISTON, BARRIER, STRUCTURE_VOID, ROCK;\n" +
                t + "8 key 'BlockRenderType'; type: 'String'; format: '\"value\"'; default: 'MODEL'; des - 'Can be excluded' Block render type. Options: MODEL, INVISIBLE, ENTITYBLOCK_ANIMATED;\n" +
                t + "9 key 'BlockLayer'; type: 'String'; format: '\"value\"'; default: 'SOLID'; des - 'Can be excluded' Render layer for transparency. Options: SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT;\n" +
                t + "10 key 'IsPassable'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Entity passes through the block without collision;\n" +
                t + "11 key 'IsLadder'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is a vertical ladder;\n" +
                t + "12 key 'IsOpaqueCube'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block is opaque and blocks light;\n" +
                t + "13 key 'IsFullCube'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Block occupies a full 1x1x1 cube;\n" +
                t + "14 key 'ShowInCreative'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b' (true); des - 'Can be excluded' Show this block in the creative inventory tab;\n" +
                t + "15 key 'AABB'; type: 'ListTag'; format: '[]'; listType: 'Double'; tagFormat: '4.9e-324'<>'0.000000000'<>'1.7976931348623157e+308'; size: '6 min'; des - 'Can be excluded' Use this if you want to specify the exact dimensions of a block's hitbox:\n" +
                t + " [X min, Y min, Z min, X max, Y max, Z max]\n" +
                t + "16 key 'Property'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' Custom block state property for advanced block behavior:\n" +
                t + " 16.01 key 'Type'; type: 'Byte'; format: '1b'<>'4b'; des - 'Required if specified' Property type: 1=BooleanProperty, 3=IntegerProperty, 4=DirectionProperty (horizontal facing);\n" +
                t + " 16.02 key 'Name'; type: 'String'; format: '\"value\"'; des - 'Required if specified' Property name for blockstate; example: 'facing', 'powered', 'age';\n" +
                t + " 16.03 key 'Min'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; des - 'Required for Type=3' Minimum value for IntegerProperty;\n" +
                t + " 16.04 key 'Max'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; des - 'Required for Type=3' Maximum value for IntegerProperty;\n" +
                t + "- Note: When 'Property' is used, the block automatically creates blockstates. For Type=4 (Direction), hitbox rotates with facing (NORTH default). For Type=1 (Boolean), default is false. For Type=3 (Integer), default is Min value;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleFacingBlock() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "facingblockexample");
        compound.setByte("BlockType", (byte) 0);
        compound.setString("BlockRenderType", "MODEL");
            NBTTagCompound nbtProperty = new NBTTagCompound();
            nbtProperty.setByte("Type", (byte) 4);
            nbtProperty.setString("Name", "facing");
        compound.setTag("Property", nbtProperty);

        String sb = "Tags for creating a facing block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Hardness', 'Resistance', 'LightLevel', 'SoundType', 'Material', 'BlockRenderType', 'BlockLayer', 'IsPassable', 'IsLadder', 'IsOpaqueCube', 'IsFullCube', 'ShowInCreative', 'AABB' - see description of block 'blockexample';\n" +
                t + "16 key 'Property'; type: 'CompoundTag'; format: '{}'; des - 'Required' Block state property defining the facing direction:\n" +
                t + " 16.01 key 'Type'; type: 'Byte'; format: '4b'; des - 'Required' Must be 4 for DirectionProperty (horizontal facing);\n" +
                t + " 16.02 key 'Name'; type: 'String'; format: '\"value\"'; default: 'facing'; des - 'Required' Property name for blockstate; standard: 'facing';\n" +
                t + "- The block will automatically rotate its hitbox (AABB) based on facing direction (NORTH default, rotates for EAST/SOUTH/WEST);";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleLiquid() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "liquidexample");
        compound.setByte("BlockType", (byte) 1);
        compound.setFloat("Resistance", 2.0f);
        compound.setInteger("Density", 1100);
        compound.setBoolean("IsGaseous", false);
        compound.setInteger("Luminosity", 5);
        compound.setInteger("Viscosity", 900);
        compound.setInteger("Temperature", 300);
        compound.setInteger("Color", 0xFFFFFFFF);
        compound.setString("Material", "WATER");

        String sb = "Tags for creating a liquid block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'ShowInCreative' - see description of block 'blockexample';\n" +
                t + "3 key 'Material'; type: 'String'; format: '\"value\"'; default: 'WATER'; des - 'Required' Must be WATER for fluid blocks;\n" +
                t + "4 key 'Resistance'; type: 'Float'; format: '0.0f'<>'3.4028235e+38f'; default: '2.0f'; des - 'Can be excluded' Resistance of the fluid block to explosions;\n" +
                t + "5 key 'Density'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 1100; des - 'Can be excluded' Fluid density in kg/m3;\n" +
                t + "6 key 'IsGaseous'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' If true, fluid flows upward like gas;\n" +
                t + "7 key 'Luminosity'; type: 'Integer'; format: '0'<>'2147483647'; default: 5; des - 'Can be excluded' Light level emitted by the fluid block;\n" +
                t + "8 key 'Viscosity'; type: 'Integer'; format: '0'<>'2147483647'; default: 900; des - 'Can be excluded' Fluid viscosity; higher = slower flow;\n" +
                t + "9 key 'Temperature'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 300; des - 'Can be excluded' Fluid temperature in Kelvin; affects interactions;\n" +
                t + "10 key 'Color'; type: 'Integer'; format: '0'<>'4294967295'; default: 0xFFFFFFFF; des - 'Can be excluded' ARGB color of the fluid (hex); example: 0xFFFFFFFF = white, 0xFF0000FF = blue;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleChest() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "chestexample");
        compound.setByte("BlockType", (byte) 2);
        compound.setString("Material", "WOOD");
        compound.setBoolean("IsChest", true);
        compound.setBoolean("IsOBJModel", true);
        compound.setInteger("Size", 14);
        compound.setInteger("GUIColor", 0x46AB86);
        compound.setString("Name", "Custom Chest");

        String sb = "Tags for creating a chest/container block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Hardness', 'Resistance', 'LightLevel', 'SoundType', 'Material', 'ShowInCreative' - see description of block 'blockexample';\n" +
                t + "8 key 'IsChest'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' If true, renders as a chest model with lid animation; if false, renders as a simple container block;\n" +
                t + "9 key 'IsOBJModel'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Use custom OBJ model for rendering;\n" +
                t + "10 key 'Size'; type: 'Integer'; format: '1'<>'96'; default: 27; des - 'Can be excluded' Number of inventory slots;\n" +
                t + "11 key 'GUIColor'; type: 'Integer'; format: '0'<>'16777215'; default: 0x46AB86; des - 'Can be excluded' Hex color for the GUI background; for container can be 'IntArray' [topColor, bottomColor];\n" +
                t + "12 key 'Name'; type: 'String'; format: '\"value\"'; default: 'Custom Chest'; des - 'Can be excluded' Display name for the chest/container GUI;\n" +
                t + "13 key 'AABB'; type: 'ListTag'; format: '[]'; listType: 'Double'; size: '6 min'; des - 'Can be excluded' Hitbox dimensions; only used when 'IsChest' is false;\n" +
                t + "14 key 'SoundOpen'; type: 'String'; format: '\"value\"'; default: 'block.wooden_door.open'; des - 'Can be excluded' Registered key of the sound event for opening;\n" +
                t + "15 key 'SoundClose'; type: 'String'; format: '\"value\"'; default: 'block.wooden_door.close'; des - 'Can be excluded' Registered key of the sound event for closing;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleContainer() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "containerexample");
        compound.setByte("BlockType", (byte) 2);
        compound.setString("Material", "STONE");
        compound.setInteger("Size", 96);
        compound.setIntArray("GUIColor", new int[] { 0x00DC8C, 0xDC8000 });
        compound.setString("Name", "Custom Container");
            NBTTagList aabb = new NBTTagList();
            aabb.appendTag(new NBTTagDouble(0.0625d));
            aabb.appendTag(new NBTTagDouble(0.0d));
            aabb.appendTag(new NBTTagDouble(0.0625d));
            aabb.appendTag(new NBTTagDouble(0.9375d));
            aabb.appendTag(new NBTTagDouble(1.0d));
            aabb.appendTag(new NBTTagDouble(0.9375d));
        compound.setTag("AABB", aabb);

        String sb = "Tags for creating a container block (non-chest):\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Hardness', 'Resistance', 'LightLevel', 'SoundType', 'Material', 'ShowInCreative', 'IsChest', 'IsOBJModel', 'Size', 'GUIColor', 'Name', 'AABB', 'SoundOpen', 'SoundClose' - see description of block 'chestexample';\n" +
                t + "- Set 'IsChest' to false to create a container block instead of chest;\n" +
                t + "- 'AABB' is required for container blocks (not chests);";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleStairs() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "stairsexample");
        compound.setByte("BlockType", (byte) 3);
        compound.setString("Material", "STONE");
        compound.setBoolean("IsFullCube", false);
        compound.setBoolean("IsOpaqueCube", false);

        String sb = "Tags for creating a stairs block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Hardness', 'Resistance', 'LightLevel', 'SoundType', 'Material', 'ShowInCreative' - see description of block 'blockexample';\n" +
                t + "8 key 'IsFullCube'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Stairs are not full cubes;\n" +
                t + "9 key 'IsOpaqueCube'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Stairs are not opaque;\n" +
                t + "- Stairs automatically support all orientations and inner/outer corner variants via block states;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleSlab() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "slabexample");
        compound.setByte("BlockType", (byte) 4);
        compound.setString("Material", "STONE");
        compound.setBoolean("IsFullCube", false);
        compound.setBoolean("IsOpaqueCube", false);

        String sb = "Tags for creating a slab block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Hardness', 'Resistance', 'LightLevel', 'SoundType', 'Material', 'ShowInCreative' - see description of block 'blockexample';\n" +
                t + "8 key 'IsFullCube'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Single slab is not a full cube;\n" +
                t + "9 key 'IsOpaqueCube'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Single slab is not opaque;\n" +
                t + "- Slab automatically supports bottom/top/double placement via block states;\n" +
                t + "- A double slab variant is automatically registered with prefix 'custom_double_';";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExamplePortal() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "portalexample");
        compound.setByte("BlockType", (byte) 5);
        compound.setString("Material", "PORTAL");
            NBTTagCompound nbtRender = new NBTTagCompound();
            nbtRender.setFloat("SecondSpeed", 800.0f);
            nbtRender.setString("SpawnParticle", "CRIT");
            nbtRender.setFloat("Transparency", 0.5f);
        compound.setTag("RenderData", nbtRender);
        compound.setInteger("DimensionID", 100);
        compound.setInteger("HomeDimensionID", 0);

        String sb = "Tags for creating a portal block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Material', 'LightLevel', 'ShowInCreative' - see description of block 'blockexample';\n" +
                t + "6 key 'DimensionID'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 100; des - 'Required' Target dimension ID for teleportation;\n" +
                t + "7 key 'HomeDimensionID'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 0; des - 'Can be excluded' Return dimension ID (0 = Overworld, -1 = Nether, 1 = End);\n" +
                t + "8 key 'RenderData'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' Visual effect settings for the portal:\n" +
                t + " 8.01 key 'SecondSpeed'; type: 'Float'; format: '10.0f'<>'10000.0f'; min: '10.0f'; max: '10000.0f'; default: '800.0f'; des - 'Can be excluded' Animation speed of portal texture; values below 10.0 clamped to 10.0, above 10000.0 clamped to 10000.0;\n" +
                t + " 8.02 key 'SpawnParticle'; type: 'String'; format: '\"value\"'; default: 'CRIT'; des - 'Can be excluded' Particle type spawned inside portal; any registered EnumParticleTypes name;\n" +
                t + " 8.03 key 'Transparency'; type: 'Float'; format: '0.15f'<>'1.0f'; min: '0.15f'; max: '1.0f'; default: '0.5f'; des - 'Can be excluded' Portal block transparency (0.15 = nearly invisible, 1.0 = opaque); values clamped to range;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleDoor() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "doorexample");
        compound.setByte("BlockType", (byte) 6);
        compound.setString("Material", "IRON");
        compound.setFloat("Hardness", 1.0f);
        compound.setFloat("Resistance", 25.0f);
        compound.setBoolean("InteractOpen", true);
        compound.setFloat("LightLevel", 2.0f);

        String sb = "Tags for creating a door block:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'BlockType', 'Hardness', 'Resistance', 'LightLevel', 'SoundType', 'Material', 'BlockRenderType', 'BlockLayer', 'IsFullCube', 'IsOpaqueCube', 'IsLadder', 'ShowInCreative' - see description of block 'blockexample';\n" +
                t + "14 key 'InteractOpen'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b' (true); des - 'Can be excluded' Whether the door can be opened by hand (false = requires redstone/power);\n" +
                t + "- Door automatically supports upper/lower half, open/closed, hinge left/right via block states;";
        compound.setString("-Description", sb);
        return compound;
    }

    public static NBTTagCompound getExampleItems() {
        if (exampleItems == null) {
            exampleItems = new NBTTagCompound();
            NBTTagList listItems = new NBTTagList();
                listItems.appendTag(getExampleItem());
                listItems.appendTag(getExampleWeapon());
                listItems.appendTag(getExampleTool());
                listItems.appendTag(getExampleAxe());
                listItems.appendTag(getExampleArmor());
                listItems.appendTag(getExampleOBJArmor());
                listItems.appendTag(getExampleShield());
                listItems.appendTag(getExampleBow());
                listItems.appendTag(getExampleFood());
                listItems.appendTag(getExampleFishingRod());
            exampleItems.setTag("Items", listItems);

            NBTTagList listPotion = new NBTTagList();
                listPotion.appendTag(getExamplePotion());
            exampleItems.setTag("Potions", listPotion);
        }
        return exampleItems;
    }

    private static NBTTagCompound getExampleItem() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "itemexample");
        compound.setByte("ItemType", (byte) 0);
        compound.setInteger("MaxStackSize", 64);

        String sb = "Tags for creating a simple item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- format of tag values must be respected;\n" +
                t + "1 key 'RegistryName'; type: 'String'; format: '\"value\"'; des - 'Required' Specified name for item registration;\n" +
                t + "2 key 'ItemType'; type: 'Byte'; format: '0b'<>'255b'; des - 'Required' Used to determine the item type during registration;\n" +
                t + "3 key 'MaxStackSize'; type: 'Integer'; format: '1'<>'64'; default: 64; des - 'Can be excluded' Maximum stack size;\n" +
                t + "4 key 'IsFull3D'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Item renders in 3D in hand (like swords/tools);\n" +
                t + "5 key 'MaxStackDamage'; type: 'Integer'; format: '0'<>'2147483647'; default: 'not used'; des - 'Can be excluded' Durability of the item; if set, item becomes damageable and MaxStackSize is ignored;\n" +
                t + "6 key 'SpeedAttack'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '-2.4d'; des - 'Can be excluded' Attack speed modifier when used as a weapon; negative = slower;\n" +
                t + "7 key 'EntityDamage'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '0.0d'; des - 'Can be excluded' Attack damage when used as a weapon; if > 0, creates attribute modifiers;\n" +
                t + "8 key 'Efficiency'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; default: '1.0f'; des - 'Can be excluded' Mining speed multiplier when breaking blocks;\n" +
                t + "9 key 'Enchantability'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 10; des - 'Can be excluded' Enchantability level for enchanting table;\n" +
                t + "10 key 'RepairItem'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' ItemStack NBT for repair material; example: '{id:\"minecraft:iron_ingot\",Count:1b}';\n" +
                t + "11 key 'CollectionMaterial'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' Material with custom destroy speed:\n" +
                t + " 11.01 key 'Material'; type: 'String'; format: '\"value\"'; des - 'Required if specified' Material name; example: 'WOOD', 'STONE', 'IRON';\n" +
                t + " 11.02 key 'Speed'; type: 'Float'; format: '1.17549435E-38f'<>'0.000000f'<>'3.4028235e+38f'; des - 'Required if specified' Destroy speed for blocks of this material;\n" +
                t + "12 key 'CollectionBlocks'; type: 'ListTag'; format: '[]'; listType: 'String'; default: 'not used'; des - 'Can be excluded' List of block registry names this item is effective against; example: ['minecraft:stone', 'minecraft:cobblestone'];\n" +
                t + "13 key 'ShowInCreative'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b' (true); des - 'Can be excluded' Show this item in the creative inventory tab;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleWeapon() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "weaponexample");
        compound.setByte("ItemType", (byte) 1);
        compound.setInteger("MaxStackDamage", 2500);
        compound.setDouble("EntityDamage", 2.5d);
        compound.setDouble("SpeedAttack", -2.4d);
        compound.setBoolean("IsFull3D", true);
        compound.setString("Material", "GOLD");
        compound.setTag("RepairItem", (new ItemStack(Items.GOLD_NUGGET)).writeToNBT(new NBTTagCompound()));
            NBTTagCompound collectionMaterial = new NBTTagCompound();
            collectionMaterial.setString("Material", "WEB");
            collectionMaterial.setFloat("Speed", 15.0f);
        compound.setTag("CollectionMaterial", collectionMaterial);

        String sb = "Tags for creating a weapon item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'MaxStackDamage', 'SpeedAttack', 'EntityDamage', 'Efficiency', 'Enchantability', 'RepairItem', 'CollectionMaterial', 'CollectionBlocks', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "14 key 'Material'; type: 'String'; format: '\"value\"'; default: 'WOOD'; des - 'Required' Tool material for the weapon. Options: wood, stone, iron, diamond, gold;\n" +
                t + "- 'EntityDamage' sets the sword attack damage bonus added to material base damage;\n" +
                t + "- 'CollectionBlocks' defines which blocks the weapon breaks efficiently;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleTool() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "toolexample");
        compound.setByte("ItemType", (byte) 2);
        compound.setInteger("MaxStackDamage", 2000);
        compound.setBoolean("IsFull3D", true);
        compound.setFloat("Efficiency", 4.0f);
        compound.setDouble("EntityDamage", 0.0d);
        compound.setString("ToolClass", "pickaxe");
        compound.setString("Material", "GOLD");
        compound.setTag("RepairItem", (new ItemStack(Items.GOLD_NUGGET)).writeToNBT(new NBTTagCompound()));
        compound.setInteger("HarvestLevel", 2);
        compound.setInteger("Enchantability", 25);
            NBTTagList collectionBlocks = new NBTTagList();
            collectionBlocks.appendTag(new NBTTagString(Objects.requireNonNull(Blocks.STONE.getRegistryName()).toString()));
            collectionBlocks.appendTag(new NBTTagString(Objects.requireNonNull(Blocks.OBSIDIAN.getRegistryName()).toString()));
        compound.setTag("CollectionBlocks", collectionBlocks);

        String sb = "Tags for creating a tool item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'MaxStackDamage', 'SpeedAttack', 'EntityDamage', 'Efficiency', 'Enchantability', 'RepairItem', 'CollectionMaterial', 'CollectionBlocks', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "14 key 'ToolClass'; type: 'String'; format: '\"value\"'; default: 'pickaxe'; des - 'Required' Tool class type. Options: pickaxe, axe, hoe, shovel;\n" +
                t + "15 key 'Material'; type: 'String'; format: '\"value\"'; default: 'WOOD'; des - 'Required' Tool material. Options: wood, stone, iron, diamond, gold;\n" +
                t + "16 key 'HarvestLevel'; type: 'Integer'; format: '0'<>'2147483647'; default: 0; des - 'Can be excluded' Mining level (0=wood, 1=stone, 2=iron, 3=diamond, 4=netherite);\n" +
                t + "- 'SpeedAttack' default for tools is -2.8d;\n" +
                t + "- 'CollectionBlocks' defines which blocks the tool breaks efficiently;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleAxe() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "axeexample");
        compound.setByte("ItemType", (byte) 2);
        compound.setInteger("MaxStackDamage", 2200);
        compound.setBoolean("IsFull3D", true);
        compound.setFloat("Efficiency", 4.25f);
        compound.setDouble("EntityDamage", 5.0d);
        compound.setString("ToolClass", "axe");
        compound.setString("Material", "GOLD");
        compound.setTag("RepairItem", (new ItemStack(Items.GOLD_INGOT)).writeToNBT(new NBTTagCompound()));
        compound.setInteger("HarvestLevel", 2);
        compound.setInteger("Enchantability", 28);

        String sb = "Tags for creating an axe item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'MaxStackDamage', 'SpeedAttack', 'EntityDamage', 'Efficiency', 'Enchantability', 'RepairItem', 'CollectionMaterial', 'CollectionBlocks', 'ToolClass', 'Material', 'HarvestLevel', 'ShowInCreative' - see description of items 'itemexample' and 'toolexample';\n" +
                t + "- Set 'ToolClass' to 'axe' to create an axe;\n" +
                t + "- All other tags are identical to 'toolexample';";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleArmor() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "armorexample");
        compound.setByte("ItemType", (byte) 3);
        compound.setString("Material", "GOLD");
        compound.setTag("RepairItem", (new ItemStack(Items.GOLD_NUGGET)).writeToNBT(new NBTTagCompound()));

        NBTTagList slots = new NBTTagList();
        NBTTagCompound part = new NBTTagCompound();
            part.setString("Slot", "HEAD");
            part.setInteger("MaxStackDamage", 2250);
            part.setInteger("Defense", 5);
            part.setFloat("Toughness", 2.2f);
            part.setInteger("Enchantability", 22);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        part = new NBTTagCompound();
            part.setString("Slot", "Chest");
            part.setInteger("MaxStackDamage", 3100);
            part.setInteger("Defense", 7);
            part.setFloat("Toughness", 3.5f);
            part.setInteger("Enchantability", 25);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        part = new NBTTagCompound();
            part.setString("Slot", "feet");
            part.setInteger("MaxStackDamage", 1800);
            part.setInteger("Defense", 4);
            part.setFloat("Toughness", 1.8f);
            part.setInteger("Enchantability", 22);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        compound.setTag("EquipmentSlots", slots);

        String sb = "Tags for creating an armor item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "6 key 'Material'; type: 'String'; format: '\"value\"'; default: 'LEATHER'; des - 'Can be excluded' Armor material type. Options: LEATHER, CHAIN, IRON, GOLD, DIAMOND;\n" +
                t + "7 key 'RepairItem'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' ItemStack NBT for repair material of all armor pieces;\n" +
                t + "8 key 'EquipmentSlots'; type: 'ListTag'; format: '[]'; listType: 'CompoundTag'; tagFormat: '{}'; des - 'Required' List of armor parts to create:\n" +
                t + " 8.01 key 'Slot'; type: 'String'; format: '\"value\"'; des - 'Required' Equipment slot. Options: HEAD, CHEST, LEGS, FEET;\n" +
                t + " 8.02 key 'MaxStackDamage'; type: 'Integer'; format: '0'<>'2147483647'; des - 'Can be excluded' Durability of this armor piece;\n" +
                t + " 8.03 key 'Defense'; type: 'Integer'; format: '0'<>'2147483647'; des - 'Can be excluded' Armor defense points;\n" +
                t + " 8.04 key 'Toughness'; type: 'Float'; format: '0.0f'<>'3.4028235e+38f'; des - 'Can be excluded' Armor toughness value;\n" +
                t + " 8.05 key 'Enchantability'; type: 'Integer'; format: '0'<>'2147483647'; des - 'Can be excluded' Enchantability level for this piece;\n" +
                t + " 8.06 key 'RenderIndex'; type: 'Integer'; format: '0'<>'2147483647'; default: 0; des - 'Can be excluded' Render index for armor model layer;\n" +
                t + "9 key 'OBJData'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' OBJ model mesh assignments for custom armor rendering:\n" +
                t + " 9.01 key 'Head Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for head part;\n" +
                t + " 9.02 key 'Body Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for body part;\n" +
                t + " 9.03 key 'Arm Right Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for right arm;\n" +
                t + " 9.04 key 'Wrist Right Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for right wrist;\n" +
                t + " 9.05 key 'Arm Left Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for left arm;\n" +
                t + " 9.06 key 'Wrist Left Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for left wrist;\n" +
                t + " 9.07 key 'Belt Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for belt;\n" +
                t + " 9.08 key 'Leg Right Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for right leg;\n" +
                t + " 9.09 key 'Foot Right Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for right foot;\n" +
                t + " 9.10 key 'Leg Left Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for left leg;\n" +
                t + " 9.11 key 'Foot Left Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for left foot;\n" +
                t + " 9.12 key 'Boot Right Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for right boot;\n" +
                t + " 9.13 key 'Boot Left Mesh Names'; type: 'ListTag'; listType: 'String'; des - 'Can be excluded' Mesh names for left boot;\n" +
                t + "10 key 'Display'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' Camera transform overrides per slot and display context:\n" +
                t + " 10.01 key 'HEAD'/'CHEST'/'LEGS'/'FEET'; type: 'CompoundTag'; des - 'Can be excluded' Slot-specific transforms:\n" +
                t + "  10.01.1 key 'thirdperson_lefthand'/'thirdperson_righthand'/'firstperson_lefthand'/'firstperson_righthand'/'head'/'gui'/'ground'/'fixed'; type: 'CompoundTag'; des - 'Can be excluded' Display context:\n" +
                t + "   10.01.1.1 key 'rotation'; type: 'ListTag'; listType: 'Float'; size: 3; des - 'Can be excluded' [x, y, z] rotation in degrees;\n" +
                t + "   10.01.1.2 key 'translation'; type: 'ListTag'; listType: 'Float'; size: 3; des - 'Can be excluded' [x, y, z] translation;\n" +
                t + "   10.01.1.3 key 'scale'; type: 'ListTag'; listType: 'Float'; size: 3; des - 'Can be excluded' [x, y, z] scale;\n" +
                t + "- Note: Each armor piece is registered as a separate item with suffix _helmet, _chestplate, _leggings, _boots;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleOBJArmor() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "armorobjexample");
        compound.setByte("ItemType", (byte) 3);
        compound.setString("Material", "IRON");
        compound.setTag("RepairItem", (new ItemStack(Items.IRON_INGOT)).writeToNBT(new NBTTagCompound()));

        NBTTagList slots = new NBTTagList();
        NBTTagCompound part = new NBTTagCompound();
            part.setString("Slot", "HEAD");
            part.setInteger("MaxStackDamage", 2250);
            part.setInteger("Defense", 5);
            part.setFloat("Toughness", 2.2f);
            part.setInteger("Enchantability", 22);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        part = new NBTTagCompound();
            part.setString("Slot", "Chest");
            part.setInteger("MaxStackDamage", 3100);
            part.setInteger("Defense", 7);
            part.setFloat("Toughness", 3.5f);
            part.setInteger("Enchantability", 25);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        part = new NBTTagCompound();
            part.setString("Slot", "LeGs");
            part.setInteger("MaxStackDamage", 2700);
            part.setInteger("Defense", 6);
            part.setFloat("Toughness", 2.6f);
            part.setInteger("Enchantability", 23);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        part = new NBTTagCompound();
            part.setString("Slot", "feet");
            part.setInteger("MaxStackDamage", 1800);
            part.setInteger("Defense", 4);
            part.setFloat("Toughness", 1.8f);
            part.setInteger("Enchantability", 22);
            part.setInteger("RenderIndex", 4);
        slots.appendTag(part);
        compound.setTag("EquipmentSlots", slots);

        NBTTagCompound objData = new NBTTagCompound();
            NBTTagList meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.HEAD.name));
            objData.setTag("Head Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.BODY.name));
            objData.setTag("Body Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.ARM_RIGHT.name));
            objData.setTag("Arm Right Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.WRIST_RIGHT.name));
            objData.setTag("Wrist Right Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.ARM_LEFT.name));
            objData.setTag("Arm Left Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.WRIST_LEFT.name));
            objData.setTag("Wrist Left Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.BELT.name));
            objData.setTag("Belt Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.LEG_RIGHT.name));
            objData.setTag("Leg Right Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.FEET_RIGHT.name));
            objData.setTag("Foot Right Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.LEG_LEFT.name));
            objData.setTag("Leg Left Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.FEET_LEFT.name));
            objData.setTag("Foot Left Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.FEET_LEFT.name));
            objData.setTag("Boot Left Mesh Names", meshes);

            meshes = new NBTTagList();
            meshes.appendTag(new NBTTagString(EnumParts.FEET_RIGHT.name));
        objData.setTag("Boot Right Mesh Names", meshes);
        compound.setTag("OBJData", objData);

        NBTTagCompound display = new NBTTagCompound();
        for (int s = 0; s < 4; s++) {
            String slot = s == 0 ? "CHEST" : s == 1 ? "LEGS" : s == 2 ? "FEET" : "HEAD";
            NBTTagCompound cameraData = new NBTTagCompound();
            for (int i = 0; i < 8; i++) {
                String p;
                NBTTagList rotation = new NBTTagList();
                NBTTagList translation = new NBTTagList();
                NBTTagList scale = new NBTTagList();
                switch(i) {
                    case 0: { // THIRD_PERSON_LEFT_HAND
                        p = "thirdperson_lefthand";
                        switch(slot) {
                            case "CHEST": {
                                translation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                translation.appendTag(new NBTTagFloat(-0.15f));
                                translation.appendTag(new NBTTagFloat(0.35f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.65f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(90.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(1.15f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.65f)); }
                                break;
                            }
                            default: {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(1.0f));
                                translation.appendTag(new NBTTagFloat(-0.375f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 1: { // THIRD_PERSON_RIGHT_HAND
                        p = "thirdperson_righthand";
                        switch(slot) {
                            case "CHEST": {
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.35f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.65f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(90.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.65f)); }
                                break;
                            }
                            default: {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(-0.375f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 2: { // FIRST_PERSON_LEFT_HAND
                        p = "firstperson_lefthand";
                        switch(slot) {
                            case "CHEST": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.57f));
                                translation.appendTag(new NBTTagFloat(0.1f));
                                translation.appendTag(new NBTTagFloat(-0.085f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.65f));
                                translation.appendTag(new NBTTagFloat(0.4f));
                                translation.appendTag(new NBTTagFloat(-0.085f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.72f));
                                translation.appendTag(new NBTTagFloat(0.435f));
                                translation.appendTag(new NBTTagFloat(-0.585f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.85f)); }
                                break;
                            }
                            default: {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.57f));
                                translation.appendTag(new NBTTagFloat(-0.225f));
                                translation.appendTag(new NBTTagFloat(-0.085f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 3: { // FIRST_PERSON_RIGHT_HAND
                        p = "firstperson_righthand";
                        switch(slot) {
                            case "CHEST": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.85f));
                                translation.appendTag(new NBTTagFloat(-0.1f));
                                translation.appendTag(new NBTTagFloat(0.2f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.6f)); }
                                break;
                            }
                            case "LEGS": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.95f));
                                translation.appendTag(new NBTTagFloat(0.25f));
                                translation.appendTag(new NBTTagFloat(0.2f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.6f)); }
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.95f));
                                translation.appendTag(new NBTTagFloat(0.4f));
                                translation.appendTag(new NBTTagFloat(0.2f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.85f)); }
                                break;
                            }
                            default: {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(280.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.85f));
                                translation.appendTag(new NBTTagFloat(-0.5f));
                                translation.appendTag(new NBTTagFloat(0.2f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.6f)); }
                                break;
                            }
                        }
                        break;
                    }
                    case 4: { // HEAD
                        p = "head";
                        switch(slot) {
                            case "CHEST": {
                                rotation.appendTag(new NBTTagFloat(270.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(1.0f));
                                translation.appendTag(new NBTTagFloat(1.65f));
                                break;
                            }
                            case "LEGS": {
                                rotation.appendTag(new NBTTagFloat(270.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(1.0f));
                                translation.appendTag(new NBTTagFloat(1.0f));
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.925f));
                                translation.appendTag(new NBTTagFloat(0.4f));
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
                                rotation.appendTag(new NBTTagFloat(30.0f));
                                rotation.appendTag(new NBTTagFloat(45.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.49f));
                                translation.appendTag(new NBTTagFloat(-0.41f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.9f)); }
                                break;
                            }
                            case "LEGS": {
                                rotation.appendTag(new NBTTagFloat(30.0f));
                                rotation.appendTag(new NBTTagFloat(45.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.05f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(30.0f));
                                rotation.appendTag(new NBTTagFloat(45.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.3f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                break;
                            }
                            default: {
                                rotation.appendTag(new NBTTagFloat(30.0f));
                                rotation.appendTag(new NBTTagFloat(45.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(-1.0f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                break;
                            }
                        }
                        break;
                    }
                    case 6: { // GROUND
                        p = "ground";
                        switch(slot) {
                            case "CHEST": {
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                            case "LEGS": {
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.25f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.6f)); }
                                break;
                            }
                            case "FEET": {
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.35f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.65f)); }
                                break;
                            }
                            default: {
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(-0.375f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.5f)); }
                                break;
                            }
                        }
                        break;
                    }
                    default: { // FIXED
                        p = "fixed";
                        switch(slot) {
                            case "CHEST": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(-0.65f));
                                translation.appendTag(new NBTTagFloat(0.45f));
                                break;
                            }
                            case "LEGS": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.05f));
                                translation.appendTag(new NBTTagFloat(0.475f));
                                break;
                            }
                            case "FEET": {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(0.2f));
                                translation.appendTag(new NBTTagFloat(0.475f));
                                break;
                            }
                            default: {
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                rotation.appendTag(new NBTTagFloat(180.0f));
                                rotation.appendTag(new NBTTagFloat(0.0f));
                                translation.appendTag(new NBTTagFloat(0.5f));
                                translation.appendTag(new NBTTagFloat(-0.85f));
                                translation.appendTag(new NBTTagFloat(0.4f));
                                for (int l = 0; l < 3; l++) { scale.appendTag(new NBTTagFloat(0.75f)); }
                                break;
                            }
                        }
                        break;
                    }
                }
                NBTTagCompound transform = new NBTTagCompound();
                if (rotation.tagCount() > 0) { transform.setTag("rotation", rotation); }
                if (translation.tagCount() > 0) { transform.setTag("translation", translation); }
                if (scale.tagCount() > 0) { transform.setTag("scale", scale); }
                cameraData.setTag(p, transform);
            }
            display.setTag(slot, cameraData);
        }
        compound.setTag("Display", display);

        String sb = "Tags for creating an OBJ armor item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'Material', 'RepairItem', 'EquipmentSlots', 'OBJData', 'Display', 'ShowInCreative' - see description of item 'armorexample';\n" +
                t + "- Set 'IsOBJModel' in properties to true for OBJ rendering;\n" +
                t + "- 'OBJData' mesh names map to custom OBJ model parts for each body slot;\n" +
                t + "- 'Display' transforms are especially important for OBJ armor to position correctly in hand/GUI;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleShield() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "shieldexample");
        compound.setByte("ItemType", (byte) 4);
        compound.setInteger("MaxStackDamage", 6500);
        compound.setDouble("EntityDamage", 0.0d);
        compound.setString("Material", "IRON");
        compound.setTag("RepairItem", (new ItemStack(Items.IRON_NUGGET)).writeToNBT(new NBTTagCompound()));

        String sb = "Tags for creating a shield item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'MaxStackDamage', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "7 key 'Material'; type: 'String'; format: '\"value\"'; default: 'WOOD'; des - 'Required' Tool material for the shield. Options: wood, stone, iron, diamond, gold;\n" +
                t + "8 key 'RepairItem'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' ItemStack NBT for repair material;\n" +
                t + "9 key 'Enchantability'; type: 'Integer'; format: '0'<>'2147483647'; default: 0; des - 'Can be excluded' Shield enchantability level;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleBow() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "bowexample");
        compound.setByte("ItemType", (byte) 5);
        compound.setInteger("MaxStackDamage", 1250);
        compound.setDouble("EntityDamage", 2.0d);
        compound.setString("Material", "WOOD");
        compound.setTag("RepairItem", (new ItemStack(Blocks.PLANKS)).writeToNBT(new NBTTagCompound()));
        compound.setBoolean("SetFlame", false);
        compound.setFloat("CritChance", 0.25f);
        compound.setFloat("DrawstringSpeed", 20.0f);

        String sb = "Tags for creating a bow item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'MaxStackDamage', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "7 key 'Material'; type: 'String'; format: '\"value\"'; default: 'WOOD'; des - 'Required' Tool material for the bow. Options: wood, stone, iron, diamond, gold;\n" +
                t + "8 key 'RepairItem'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' ItemStack NBT for repair material;\n" +
                t + "9 key 'Enchantability'; type: 'Integer'; format: '0'<>'2147483647'; default: 1; des - 'Can be excluded' Bow enchantability level;\n" +
                t + "10 key 'Bullet'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' ItemStack NBT for custom projectile; if not set, uses default arrow;\n" +
                t + "11 key 'SetFlame'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' All shots are flaming arrows (like Flame enchantment);\n" +
                t + "12 key 'CritChance'; type: 'Float'; format: '0.0f'<>'1.0f'; default: '0.0f'; des - 'Can be excluded' Chance for critical hit; 0.0=never, 1.0=always;\n" +
                t + "13 key 'EntityDamage'; type: 'Double'; format: '0.0d'<>'1.7976931348623157E308'; default: '2.0d'; des - 'Can be excluded' Base arrow damage; scales with draw time (full draw = 100% damage);\n" +
                t + "14 key 'DrawstringSpeed'; type: 'Float'; format: '0.0f'<>'3.4028235e+38f'; default: '30.0f'; des - 'Can be excluded' Bow draw speed; higher = faster full draw;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleFood() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "foodexample");
        compound.setByte("ItemType", (byte) 6);
        compound.setInteger("MaxStackSize", 32);
        compound.setInteger("UseDuration", 32);
        compound.setInteger("HealAmount", 1);
        compound.setFloat("SaturationModifier", 0.1f);
        compound.setBoolean("IsWolfFood", false);
        compound.setBoolean("AlwaysEdible", true);
            NBTTagCompound potionEffect = new NBTTagCompound();
            potionEffect.setString("Potion", "minecraft:fire_resistance");
            potionEffect.setInteger("DurationTicks", 45);
            potionEffect.setInteger("Amplifier", 0);
            potionEffect.setBoolean("Ambient", true);
            potionEffect.setBoolean("ShowParticles", false);
            potionEffect.setFloat("Probability", 0.95f);
        compound.setTag("PotionEffect", potionEffect);

        String sb = "Tags for creating a food item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "6 key 'UseDuration'; type: 'Integer'; format: '0'<>'1200'; min: 0; max: 1200; default: 32; des - 'Can be excluded' Ticks to eat the food; lower = faster eating;\n" +
                t + "7 key 'HealAmount'; type: 'Integer'; format: '0'<>'2147483647'; default: 3; des - 'Can be excluded' Hunger points (food value) restored when eaten;\n" +
                t + "8 key 'SaturationModifier'; type: 'Float'; format: '0.0f'<>'3.4028235e+38f'; default: '0.1f'; des - 'Can be excluded' Saturation restored when eaten;\n" +
                t + "9 key 'IsWolfFood'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Whether wolves can eat this food;\n" +
                t + "10 key 'AlwaysEdible'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Can be eaten even when hunger is full (like golden apple);\n" +
                t + "11 key 'PotionEffect'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' Potion effect applied when eaten:\n" +
                t + " 11.01 key 'Potion'; type: 'String'; format: '\"value\"'; des - 'Required if specified' Potion registry name; example: 'minecraft:fire_resistance';\n" +
                t + " 11.02 key 'DurationTicks'; type: 'Integer'; format: '0'<>'2147483647'; default: 100; des - 'Can be excluded' Effect duration in ticks;\n" +
                t + " 11.03 key 'Amplifier'; type: 'Integer'; format: '0'<>'2147483647'; default: 0; des - 'Can be excluded' Effect level (0=I, 1=II, etc.);\n" +
                t + " 11.04 key 'Ambient'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b'; des - 'Can be excluded' Whether effect is ambient (beacon-like);\n" +
                t + " 11.05 key 'ShowParticles'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b'; des - 'Can be excluded' Show effect particles;\n" +
                t + " 11.06 key 'Probability'; type: 'Float'; format: '0.0f'<>'1.0f'; default: '1.0f'; des - 'Can be excluded' Chance to apply this effect; 1.0f = 100%;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleFishingRod() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "fishingrodexample");
        compound.setByte("ItemType", (byte) 8);
        compound.setInteger("MaxStackSize", 1);
        compound.setTag("RepairItem", (new ItemStack(Items.STICK)).writeToNBT(new NBTTagCompound()));
        compound.setInteger("MaxStackDamage", 150);
        compound.setInteger("Enchantability", 5);
        compound.setInteger("FishingLineColor", 0xFF00EA);
        compound.setString("FishingHookTexture", "custom_fishing_hook");

        String sb = "Tags for creating a fishing rod item:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ItemType', 'MaxStackSize', 'IsFull3D', 'MaxStackDamage', 'ShowInCreative' - see description of item 'itemexample';\n" +
                t + "7 key 'Enchantability'; type: 'Integer'; format: '0'<>'2147483647'; default: 1; des - 'Can be excluded' Fishing rod enchantability level;\n" +
                t + "8 key 'RepairItem'; type: 'CompoundTag'; format: '{}'; des - 'Can be excluded' ItemStack NBT for repair material;\n" +
                t + "9 key 'FishingHookTexture'; type: 'String'; format: '\"value\"'; default: 'not used'; des - 'Can be excluded' Custom texture name for the fishing hook; file: assets/customnpcs/textures/entity/{name}.png;\n" +
                t + "10 key 'AddSpeedBonus'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 0; des - 'Can be excluded' Bonus to fishing speed (added to Lure enchantment); negative = slower;\n" +
                t + "11 key 'AddLuckBonus'; type: 'Integer'; format: '-2147483648'<>'0'<>'2147483647'; default: 0; des - 'Can be excluded' Bonus to fishing luck (added to Luck of the Sea enchantment);\n" +
                t + "12 key 'FishingLineColor'; type: 'Integer'; format: '0'<>'16777215'; default: 0; des - 'Can be excluded' Hex color of the fishing line; 0xFF00EA = magenta;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExamplePotion() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "potionexample");
        compound.setByte("ItemType", (byte) 7);
        compound.setBoolean("IsBadEffect", false);
        compound.setBoolean("IsInstant", false);
        compound.setBoolean("IsBeneficial", true);
        compound.setInteger("LiquidColor", 0xFFFFFF);
        compound.setInteger("MaxStackSize", 16);
        compound.setInteger("BaseDelay", 200);
        compound.setInteger("Duration", 20);
        compound.setTag("CureItem", (new ItemStack(Items.CARROT)).writeToNBT(new NBTTagCompound()));
            NBTTagList potionModifiers = new NBTTagList();
            potionModifiers.appendTag(getExamplePotionModifier());
        compound.setTag("Modifiers", potionModifiers);

        String sb = "Tags for creating a custom potion effect:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- format of tag values must be respected;\n" +
                t + "- Potions are loaded from 'custom_items.js' file, from 'Potions' list;\n" +
                t + "1 key 'RegistryName'; type: 'String'; format: '\"value\"'; des - 'Required' Specified name for potion registration;\n" +
                t + "2 key 'ItemType'; type: 'Byte'; format: '7b'; des - 'Required' Must be 7 for potion type;\n" +
                t + "3 key 'ShowInCreative'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b' (true); des - 'Can be excluded' Show this potion in the creative inventory tab;\n" +
                t + "4 key 'IsBadEffect'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Whether the effect is harmful (like poison);\n" +
                t + "5 key 'IsBeneficial'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' Whether the effect is beneficial (like speed);\n" +
                t + "6 key 'IsInstant'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' If true, effect applies instantly (like Instant Health); BaseDelay is ignored;\n" +
                t + "7 key 'LiquidColor'; type: 'Integer'; format: '0'<>'16777215'; default: 0; des - 'Can be excluded' Color of the potion liquid in GUI and particle effects (hex);\n" +
                t + "8 key 'MaxStackSize'; type: 'Integer'; format: '1'<>'64'; default: 1; des - 'Can be excluded' Maximum stack size for potion items;\n" +
                t + "9 key 'BaseDelay'; type: 'Integer'; format: '20'<>'2147483647'; min: 20; default: 200; des - 'Can be excluded' Base duration in ticks for the standard potion variant;\n" +
                t + "10 key 'Duration'; type: 'Integer'; format: '1'<>'2147483647'; default: 10; des - 'Can be excluded' Tick step for effect application; lower = more frequent;\n" +
                t + "11 key 'CureItem'; type: 'CompoundTag'; format: '{}'; default: 'empty'; des - 'Can be excluded' ItemStack NBT that cures this effect (like milk bucket); example: '{id:\"minecraft:milk_bucket\",Count:1b}';\n" +
                t + "12 key 'Modifiers'; type: 'ListTag'; format: '[]'; listType: 'CompoundTag'; tagFormat: '{}'; default: 'not used'; des - 'Can be excluded' Attribute modifiers applied while the effect is active:\n" +
                t + " 12.01 key 'AttributeName'; type: 'String'; format: '\"value\"'; des - 'Required if specified' Attribute registry name; example: 'minecraft:generic.attack_damage';\n" +
                t + " 12.02 key 'AttributeDefValue'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '2.0d'; des - 'Required if specified' Default value for the attribute;\n" +
                t + " 12.03 key 'AttributeMinValue'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '0.0d'; des - 'Required if specified' Minimum allowed value for the attribute;\n" +
                t + " 12.04 key 'AttributeMaxValue'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '2048.0d'; des - 'Required if specified' Maximum allowed value for the attribute;\n" +
                t + " 12.05 key 'UUID'; type: 'String'; format: '\"value\"'; default: 'random UUID'; des - 'Can be excluded' Unique identifier for the modifier; if invalid, random UUID is generated; example: 'CB3F55D3-645C-4F38-A497-9C13A33DB5CF';\n" +
                t + " 12.06 key 'Amount'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '0.0d'; des - 'Required if specified' Modifier amount added to the attribute base value;\n" +
                t + " 12.07 key 'Operation'; type: 'Integer'; format: '0'<>'2'; default: 0; des - 'Can be excluded' Modifier operation. Options: 0=ADDITION, 1=MULTIPLY_BASE, 2=MULTIPLY_TOTAL;\n" +
                t + "13 key 'hasStatusIcon'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b' (true); des - 'Can be excluded' Whether the effect shows an icon in the inventory;";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExamplePotionModifier() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("AttributeName", "generic.maxHealth");
        compound.setString("UUID", UUID.randomUUID().toString());
        compound.setDouble("AttributeDefValue", 5.0d);
        compound.setDouble("AttributeMinValue", -50.0d);
        compound.setDouble("AttributeMaxValue", 50.0d);
        compound.setDouble("Amount", 2.0d);
        compound.setInteger("Operation", 2);

        String sb = "Tags for creating a potion attribute modifier:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- format of tag values must be respected;\n" +
                t + "- This tag is used inside the 'Modifiers' list of a potion;\n" +
                t + "1 key 'AttributeName'; type: 'String'; format: '\"value\"'; des - 'Required' Attribute registry name; example: 'minecraft:generic.attack_damage', 'minecraft:generic.movement_speed';\n" +
                t + "2 key 'AttributeDefValue'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '2.0d'; des - 'Required' Default value for the attribute;\n" +
                t + "3 key 'AttributeMinValue'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '0.0d'; des - 'Required' Minimum allowed value for the attribute;\n" +
                t + "4 key 'AttributeMaxValue'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '2048.0d'; des - 'Required' Maximum allowed value for the attribute;\n" +
                t + "5 key 'UUID'; type: 'String'; format: '\"value\"'; default: 'random UUID'; des - 'Can be excluded' Unique identifier for the modifier; if invalid or omitted, a random UUID is generated; example: 'CB3F55D3-645C-4F38-A497-9C13A33DB5CF';\n" +
                t + "6 key 'Amount'; type: 'Double'; format: '-1.7976931348623157E308'<>'0.0d'<>'1.7976931348623157E308'; default: '0.0d'; des - 'Required' Modifier amount added to the attribute base value;\n" +
                t + "7 key 'Operation'; type: 'Integer'; format: '0'<>'2'; default: 0; des - 'Can be excluded' Modifier operation. Options: 0=ADDITION, 1=MULTIPLY_BASE, 2=MULTIPLY_TOTAL;";
        compound.setString("-Description", sb);
        return compound;
    }

    public static NBTTagCompound getExampleParticles() {
        if (exampleParticles == null) {
            exampleParticles = new NBTTagCompound();
            NBTTagList listItems = new NBTTagList();
            listItems.appendTag(getExampleParticle());
            listItems.appendTag(getExampleOBJParticle());
            exampleParticles.setTag("Particles", listItems);
        }
        return exampleParticles;
    }

    private static NBTTagCompound getExampleParticle() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "PARTICLE_EXAMPLE");
        compound.setBoolean("ShouldIgnoreRange", false);
        compound.setInteger("ArgumentCount", 0);
        compound.setInteger("MaxAge", 60);
        compound.setIntArray("UVpos", new int[]{1, 5});
        compound.setFloat("Gravity", 0.25f);
        compound.setFloat("Scale", 1.5f);
        NBTTagList motion = new NBTTagList();
        motion.appendTag(new NBTTagDouble(0.2d));
        motion.appendTag(new NBTTagDouble(0.1d));
        motion.appendTag(new NBTTagDouble(0.2d));
        compound.setTag("StartMotion", motion);
        compound.setBoolean("IsRandomMotion", true);
        compound.setBoolean("NotMotionY", true);

        String sb = "Tags for creating a custom particle:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- format of tag values must be respected;\n" +
                t + "- Particles are loaded from 'custom_particles.js' file, from 'Particles' list;\n" +
                t + "1 key 'RegistryName'; type: 'String'; format: '\"value\"'; des - 'Required' Specified name for particle registration; example: 'PARTICLE_EXAMPLE';\n" +
                t + "2 key 'ShouldIgnoreRange'; type: 'Boolean'; format: false='0b', true='1b'; default: '1b' (true); des - 'Can be excluded' If true, particle is visible from any distance (ignores vanilla range limit);\n" +
                t + "3 key 'ArgumentCount'; type: 'Integer'; format: '0'<>'2147483647'; default: 0; des - 'Can be excluded' Number of float arguments the particle accepts;\n" +
                t + "4 key 'MaxAge'; type: 'Integer'; format: '0'<>'2147483647'; default: 0; des - 'Can be excluded' Maximum lifetime of the particle in ticks; 0 = use default;\n" +
                t + "5 key 'UVpos'; type: 'IntArray'; format: '[int, int]'; default: '[0, 0]'; des - 'Can be excluded' Texture atlas UV position [u, v] for sprite animation;\n" +
                t + "6 key 'Gravity'; type: 'Float'; format: '0.0f'<>'3.4028235e+38f'; default: '0.0f'; des - 'Can be excluded' Gravity applied to the particle;\n" +
                t + "7 key 'Scale'; type: 'Float'; format: '0.0f'<>'3.4028235e+38f'; default: '1.0f'; des - 'Can be excluded' Base scale of the particle;\n" +
                t + "8 key 'StartMotion'; type: 'ListTag'; format: '[]'; listType: 'Double'; size: 3; default: '[0.0d, 0.0d, 0.0d]'; des - 'Can be excluded' Initial motion vector [x, y, z];\n" +
                t + "9 key 'IsRandomMotion'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' If true, motion is randomized;\n" +
                t + "10 key 'NotMotionY'; type: 'Boolean'; format: false='0b', true='1b'; default: '0b' (false); des - 'Can be excluded' If true, Y motion is ignored (particle does not move vertically);\n" +
                t + "11 key 'Texture'; type: 'String'; format: '\"value\"'; default: 'not used'; des - 'Can be excluded' Texture name for the particle sprite; file: assets/customnpcs/textures/particle/{name}.png;\n" +
                t + "- Particle JSON: assets/customnpcs/particles/{name}.json (defines texture frames for animation);";
        compound.setString("-Description", sb);
        return compound;
    }

    private static NBTTagCompound getExampleOBJParticle() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("RegistryName", "PARTICLE_OBJ_EXAMPLE");
        compound.setBoolean("ShouldIgnoreRange", false);
        compound.setInteger("MaxAge", 60);
        compound.setFloat("Gravity", 1.0f / 3.0f);
        compound.setFloat("Scale", 1.0f);
        compound.setString("OBJModel", "ring");

        String sb = "Tags for creating a custom OBJ particle:\n" +
                t + "- key names must match exactly (even the case of the characters);\n" +
                t + "- Keys: 'RegistryName', 'ShouldIgnoreRange', 'ArgumentCount', 'MaxAge', 'UVpos', 'Gravity', 'Scale', 'StartMotion', 'IsRandomMotion', 'NotMotionY', 'Texture' - see description of particle 'particleexample';\n" +
                t + "12 key 'OBJModel'; type: 'String'; format: '\"value\"'; des - 'Required if specified' OBJ model file name for 3D particle rendering; example: 'my_particle'; file: assets/customnpcs/models/particle/{name}.obj;\n" +
                t + "- OBJ particles render as 3D models instead of billboard sprites;\n" +
                t + "- Supports the same animation and lifecycle as standard particles;";
        compound.setString("-Description", sb);
        return compound;
    }

}
