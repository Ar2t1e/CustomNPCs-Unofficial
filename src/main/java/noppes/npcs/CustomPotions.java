package noppes.npcs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.potions.PotionData;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import java.io.File;
import java.util.*;

public class CustomPotions {

    public static final Map<String, PotionData> CUSTOMS = new HashMap<>();

    @SubscribeEvent
    public void registerPotion(RegistryEvent.Register<Potion> event) {
        // Custom Items
        File itemsFile = new File(CustomNpcs.Dir, "custom_items.js");
        NBTTagCompound nbtItems = CustomItems.getItemsNbt(itemsFile);
        boolean resave = nbtItems.getBoolean("resave");
        nbtItems.removeTag("resave");
        for (int i = 0; i < nbtItems.getTagList("Potions", 10).tagCount(); i++) {
            NBTTagCompound nbtPotion = nbtItems.getTagList("Potions", 10).getCompoundTagAt(i);
            if (!nbtPotion.hasKey("RegistryName", 8) || nbtPotion.getString("RegistryName").isEmpty()) {
                LogWriter.error("Attempt to load potion pos: " + i + "; name: \"" + nbtPotion.getString("RegistryName")
                        + "\" - failed");
                continue;
            }
            String preName = nbtPotion.getString("RegistryName");
            String name = NoppesUtilServer.validPath(preName);
            if (!preName.equals(name)) {
                nbtPotion.setString("RegistryName", name);
                resave = true;
            }
            ResourceLocation location = new ResourceLocation(CustomNpcs.MODID, "custom_potion_" + name);
            if (Potion.REGISTRY.getObject(location) == null) {
                PotionData data = new PotionData(location, nbtPotion);
                CUSTOMS.put(location.getResourcePath(), data);
                if (name.equals("potionexample") || nbtPotion.getBoolean("CreateAllFiles")) { CustomNpcs.proxy.createAllFiles(data); }
                if (nbtPotion.hasKey("CreateAllFiles", 1)) {
                    nbtPotion.removeTag("CreateAllFiles");
                    resave = true;
                }
                LogWriter.info("Load Custom Potion \"" + location + "\"");
                event.getRegistry().register(data.POTION);
            }
            else { LogWriter.error("Attempt to load a registered potion \"" + location + "\""); }
        }
        if (resave) { Util.instance.saveFile(itemsFile, nbtItems); }
    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        for (PotionData data : CUSTOMS.values()) {
            LogWriter.info("Load Custom Potion type \"" + data.location + "\"");
            event.getRegistry().register(data.POTION_TYPE);
        }
    }

}
