package noppes.npcs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import noppes.npcs.potions.PotionData;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import java.io.File;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CustomNpcs.MODID)
public class CustomPotions {

    public static final Map<String, PotionData> CUSTOMS = new HashMap<>();

    @SubscribeEvent
    public static void registers(RegisterEvent event) {
        if (event.getForgeRegistry() == null) { return; }
        if (event.getRegistryKey() == ForgeRegistries.Keys.MOB_EFFECTS) {
            // Custom Items
            File itemsFile = new File(CustomNpcs.Dir, "custom_items.js");
            CompoundTag nbtItems = CustomItems.getItemsNbt(itemsFile);
            boolean resave = nbtItems.getBoolean("resave");
            nbtItems.remove("resave");

            for (int i = 0; i < nbtItems.getList("Potions", 10).size(); i++) {
                CompoundTag nbtPotion = nbtItems.getList("Potions", 10).getCompound(i);
                if (!nbtPotion.contains("RegistryName", 8) || nbtPotion.getString("RegistryName").isEmpty()) {
                    LogWriter.error("Attempt to load potion pos: " + i + "; name: \"" + nbtPotion.getString("RegistryName") + "\" - failed");
                    continue;
                }
                String preName = nbtPotion.getString("RegistryName");
                String name = NoppesUtilServer.validPath(preName);
                if (!preName.equals(name)) {
                    nbtPotion.putString("RegistryName", name);
                    resave = true;
                }
                ResourceLocation location = new ResourceLocation(CustomNpcs.MODID, "custom_potion_" + name);
                if (BuiltInRegistries.MOB_EFFECT.get(location) == null) {
                    PotionData data = new PotionData(location, nbtPotion);
                    CUSTOMS.put(location.getPath(), data);
                    if (name.equals("potionexample") || nbtPotion.getBoolean("CreateAllFiles")) { CustomNpcs.proxy.createAllFiles(data); }
                    if (nbtPotion.contains("CreateAllFiles", 1)) {
                        nbtPotion.remove("CreateAllFiles");
                        resave = true;
                    }
                    LogWriter.info("Load Custom Potion MobEffect \"" + location + "\"");
                    event.getForgeRegistry().register(data.location, data.EFFECT);
                }
                else { LogWriter.error("Attempt to load a registered potion \"" + location + "\""); }
            }
            if (resave) { Util.instance.saveFile(itemsFile, nbtItems); }
        } // 4
        if (event.getRegistryKey() == ForgeRegistries.Keys.POTIONS) {
            for (PotionData data : CUSTOMS.values()) {
                LogWriter.info("Load Custom Potion \"" + data.location + "\"");
                event.getForgeRegistry().register(data.location, data.POTION);
                event.getForgeRegistry().register(new ResourceLocation(data.location.getNamespace(), data.location.getPath() + "_long"), data.LONG);
                event.getForgeRegistry().register(new ResourceLocation(data.location.getNamespace(), data.location.getPath() + "_strong"), data.STRONG);
            }
        } // 10
    }

}
