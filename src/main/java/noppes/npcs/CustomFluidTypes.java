package noppes.npcs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import noppes.npcs.fluids.CustomFluidType;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.util.*;

public class CustomFluidTypes {

    protected static final Map<String, CustomFluidType> fluidTypes = new HashMap<>();

    public static FluidType.Properties getFluidTypeProperty(CompoundTag nbtBlock) {
        CompoundTag nbtType = nbtBlock.getCompound("FluidType");
        FluidType.Properties property = FluidType.Properties.create()
                .lightLevel(nbtType.contains("lightLevel", 3) ? ValueUtil.correctInt(nbtType.getInt("lightLevel"), 0, 15) : 0)
                .density(nbtType.contains("density", 3) ? nbtType.getInt("density") : 15)
                .density(nbtType.contains("viscosity", 3) ? ValueUtil.correctInt(nbtType.getInt("viscosity"), 0, Integer.MAX_VALUE) : 5);

        if (nbtType.contains("canPushEntity", 1)) { property.canPushEntity(nbtType.getBoolean("canPushEntity")); }
        if (nbtType.contains("canSwim", 1)) { property.canSwim(nbtType.getBoolean("canSwim")); }
        if (nbtType.contains("canDrown", 1)) { property.canDrown(nbtType.getBoolean("canDrown")); }
        if (nbtType.contains("canExtinguish", 1)) { property.canExtinguish(nbtType.getBoolean("canExtinguish")); }
        if (nbtType.contains("canConvertToSource", 1)) { property.canConvertToSource(nbtType.getBoolean("canConvertToSource")); }
        if (nbtType.contains("supportsBoating", 1)) { property.canConvertToSource(nbtType.getBoolean("supportsBoating")); }
        if (nbtType.contains("canHydrate", 1)) { property.canConvertToSource(nbtType.getBoolean("canHydrate")); }

        if (nbtType.contains("temperature", 3)) { property.temperature(nbtType.getInt("temperature")); }

        if (nbtType.contains("fallDistanceModifier", 5)) { property.fallDistanceModifier(nbtType.getFloat("fallDistanceModifier")); }

        if (nbtType.contains("motionScale", 6)) { property.motionScale(nbtType.getDouble("motionScale")); }

        if (nbtType.contains("descriptionId", 8)) { property.descriptionId(nbtType.getString("descriptionId")); }
        if (nbtType.contains("pathType", 8)) {
            property.pathType(getBlockPathType(nbtType.getString("pathType"), BlockPathTypes.WATER));
        }
        if (nbtType.contains("adjacentPathType", 8)) {
            property.adjacentPathType(getBlockPathType(nbtType.getString("adjacentPathType"), BlockPathTypes.WATER_BORDER));
        }
        if (nbtType.contains("drinkSound", 8)) {
            property.sound(SoundAction.get("drink"), Objects.requireNonNullElse(ForgeRegistries.SOUND_EVENTS
                    .getValue(new ResourceLocation(nbtType.getString("drinkSound"))), SoundEvents.GENERIC_DRINK));
        }
        else { property.sound(SoundAction.get("drink"), SoundEvents.GENERIC_DRINK); }
        if (nbtType.contains("bucket_fill", 8)) {
            @Nullable SoundEvent se = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtType.getString("bucket_fill")));
            if (se != null) { property.sound(SoundActions.BUCKET_FILL, se); }
        }
        if (nbtType.contains("bucket_empty", 8)) {
            @Nullable SoundEvent se = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtType.getString("bucket_empty")));
            if (se != null) { property.sound(SoundActions.BUCKET_EMPTY, se); }
        }
        if (nbtType.contains("fluid_vaporize", 8)) {
            @Nullable SoundEvent se = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(nbtType.getString("fluid_vaporize")));
            if (se != null) { property.sound(SoundActions.FLUID_VAPORIZE, se); }
        }
        if (nbtType.contains("rarity", 8)) { property.rarity(CustomBlocks.getRrarity(nbtType.getString("rarity"))); }
        return property;
    }

    private static @Nonnull BlockPathTypes getBlockPathType(@Nonnull String pathType, @Nonnull BlockPathTypes base) {
        return switch (pathType.toLowerCase()) {
            case "blocked" -> BlockPathTypes.BLOCKED;
            case "open" -> BlockPathTypes.OPEN;
            case "walkable" -> BlockPathTypes.WALKABLE;
            case "walkable_door" -> BlockPathTypes.WALKABLE_DOOR;
            case "trapdoor" -> BlockPathTypes.TRAPDOOR;
            case "powder_snow" -> BlockPathTypes.POWDER_SNOW;
            case "danger_powder_snow" -> BlockPathTypes.DANGER_POWDER_SNOW;
            case "fence" -> BlockPathTypes.FENCE;
            case "lava" -> BlockPathTypes.LAVA;
            case "water_border" -> BlockPathTypes.WATER_BORDER;
            case "rail" -> BlockPathTypes.RAIL;
            case "unpassable_rail" -> BlockPathTypes.UNPASSABLE_RAIL;
            case "danger_fire" -> BlockPathTypes.DANGER_FIRE;
            case "damage_fire" -> BlockPathTypes.DAMAGE_FIRE;
            case "danger_other" -> BlockPathTypes.DANGER_OTHER;
            case "damage_other" -> BlockPathTypes.DAMAGE_OTHER;
            case "door_open" -> BlockPathTypes.DOOR_OPEN;
            case "door_wood_closed" -> BlockPathTypes.DOOR_WOOD_CLOSED;
            case "door_iron_closed" -> BlockPathTypes.DOOR_IRON_CLOSED;
            case "breach" -> BlockPathTypes.BREACH;
            case "leaves" -> BlockPathTypes.LEAVES;
            case "sticky_honey" -> BlockPathTypes.STICKY_HONEY;
            case "cocoa" -> BlockPathTypes.COCOA;
            case "damage_cautious" -> BlockPathTypes.DAMAGE_CAUTIOUS;
            case "water" -> BlockPathTypes.WATER;
            default -> base;
        };
    }

    public static void registerFluidTypes(RegisterEvent event) {
        File blocksFile = new File(CustomNpcs.Dir, "custom_blocks.js");
        CompoundTag nbtBlocks = CustomBlocks.getBlocksNbt(blocksFile);
        boolean resave = nbtBlocks.getBoolean("resave");
        nbtBlocks.remove("resave");

        for (int i = 0; i < nbtBlocks.getList("Blocks", 10).size(); i++) {
            CompoundTag nbtBlock = nbtBlocks.getList("Blocks", 10).getCompound(i);
            if (!nbtBlock.contains("RegistryName", 8) || !nbtBlock.contains("BlockType", 1)
                    || nbtBlock.getString("RegistryName").isEmpty() || nbtBlock.getByte("BlockType") < (byte) 0
                    || nbtBlock.getByte("BlockType") > (byte) 6) {
                LogWriter.error("Attempt to load block pos: " + i + "; name: \"" + nbtBlock.getString("RegistryName") + "\" - failed");
                continue;
            }
            if (nbtBlock.getByte("BlockType") == (byte) 1) {
                String preName = "custom_fluid_" + nbtBlock.getString("RegistryName");
                String name = NoppesUtilServer.validPath(preName);
                if (!preName.equals(name)) {
                    nbtBlock.putString("RegistryName", name);
                    resave = true;
                }
                if (!nbtBlock.contains("Properties", 10)) {
                    nbtBlock.put("Properties", new CompoundTag());
                    resave = true;
                }
                if (nbtBlock.contains("IsOBJModel", 1)) {
                    nbtBlock.remove("IsOBJModel");
                    resave = true;
                }
                CompoundTag propertyNbt = nbtBlock.getCompound("Properties");
                if (!propertyNbt.contains("liquid", 1) || !propertyNbt.getBoolean("liquid")) {
                    propertyNbt.putBoolean("liquid", true);
                    resave = true;
                }
                if (!propertyNbt.contains("replaceable", 1) || !propertyNbt.getBoolean("replaceable")) {
                    propertyNbt.putBoolean("replaceable", true);
                    resave = true;
                }
                if (!propertyNbt.contains("noCollission", 1) || !propertyNbt.getBoolean("noCollission")) {
                    propertyNbt.putBoolean("noCollission", true);
                    resave = true;
                }
                if (!propertyNbt.contains("noLootTable", 1) || !propertyNbt.getBoolean("noLootTable")) {
                    propertyNbt.putBoolean("noLootTable", true);
                    resave = true;
                }
                if (!propertyNbt.contains("sound", 8)) {
                    propertyNbt.putString("sound", "empty");
                    resave = true;
                }
                if (!propertyNbt.contains("pushReaction", 8)) {
                    propertyNbt.putString("pushReaction", "DESTROY");
                    resave = true;
                }
                ResourceLocation location = new ResourceLocation(CustomNpcs.MODID, name);
                if (!fluidTypes.containsKey(location.getPath())) { // register fluid type
                    CompoundTag nbtType = nbtBlock.getCompound("FluidType");
                    Color c = new Color(nbtType.contains("fogColor", 3) ? nbtType.getInt("fogColor") : 0x3C6EDC);
                    CustomFluidType fluidType = new CustomFluidType(new ResourceLocation(CustomNpcs.MODID, "block/" + name + "_still"),
                            new ResourceLocation(CustomNpcs.MODID, "block/" + name + "_flow"),
                            new ResourceLocation(CustomNpcs.MODID, "block/" + name),
                            nbtType.contains("tintColor", 3) ? nbtType.getInt("tintColor") : 0xA1E038D0,
                            new Vector3f((float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F),
                            getFluidTypeProperty(nbtBlock), nbtBlock);
                    RegistryObject<FluidType> key = RegistryObject.createOptional(location, ForgeRegistries.Keys.FLUID_TYPES.location(), CustomNpcs.MODID);
                    LogWriter.debug("Load Custom FluidType: " + location);
                    event.register(ForgeRegistries.Keys.FLUID_TYPES, helper -> helper.register(key.getId(), fluidType));
                    fluidTypes.put(location.getPath(), fluidType);
                }
            } // Liquid
            if (nbtBlock.getBoolean("CreateDefaultFiles")) {
                nbtBlock.remove("CreateDefaultFiles");
                resave = true;
            }
        }
        if (resave) { Util.instance.saveFile(blocksFile, nbtBlocks); }
    }

    public static @Nullable CustomFluidType getFluidType(ResourceLocation location) {
        return location == null || !location.getNamespace().equals(CustomNpcs.MODID) ? null : fluidTypes.get(location.getPath());
    }

}
