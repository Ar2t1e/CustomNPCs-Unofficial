package noppes.npcs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;
import noppes.npcs.client.particles.CustomParticleSettings;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ModData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class CustomParticles {

    public static Map<Integer, CustomParticleSettings> customparticles = new TreeMap<>();

    @SuppressWarnings("unchecked")
    public static void registerParticle() {
        File prtcsFile = new File(CustomNpcs.Dir, "custom_particles.js");
        NBTTagCompound nbtParticles = getParticleNbt(prtcsFile);
        boolean resave = nbtParticles.getBoolean("resave");
        nbtParticles.removeTag("resave");

        int maxId = -1; // maxID
        for (EnumParticleTypes ept : EnumParticleTypes.values()) {
            if (ept.getParticleID() >= maxId) { maxId = ept.getParticleID(); }
        }
        maxId += 1;
        Class<?>[] additionalTypes = { String.class, int.class, boolean.class, int.class }; // particleName, particleID,
        // create new
        Map<Integer, EnumParticleTypes> particles = new HashMap<>();
        Map<String, EnumParticleTypes> byName = new HashMap<>();

        Field field = null;
        try { field = EnumParticleTypes.class.getDeclaredField("field_179365_U"); }
        catch (Exception ignored) {
            try { field = EnumParticleTypes.class.getDeclaredField("PARTICLES"); }
            catch (Exception e) { LogWriter.error(e); }
        }
        if (field != null) {
            try { particles = (Map<Integer, EnumParticleTypes>) field.get(EnumParticleTypes.class); }
            catch (Exception e) { LogWriter.error(e); }

        }
        field = null;
        try { field = EnumParticleTypes.class.getDeclaredField("field_186837_Z"); }
        catch (Exception ignored) {
            try { field = EnumParticleTypes.class.getDeclaredField("BY_NAME"); }
            catch (Exception e) { LogWriter.error(e); }
        }
        if (field != null) {
            try { byName = (Map<String, EnumParticleTypes>) field.get(EnumParticleTypes.class); }
            catch (Exception e) { LogWriter.error(e); }

        }

        for (int i = 0; i < nbtParticles.getTagList("Particles", 10).tagCount(); i++) {
            NBTTagCompound nbtParticle = nbtParticles.getTagList("Particles", 10).getCompoundTagAt(i);
            if (!nbtParticle.hasKey("RegistryName", 8)) {
                LogWriter.error("Attempt to load particle pos: " + i + " - failed");
                continue;
            }
            String preName = nbtParticle.getString("RegistryName");
            String name = NoppesUtilServer.validPath(preName);
            if (!preName.equals(name)) {
                nbtParticle.setString("RegistryName", name);
                resave = true;
            }
            CustomParticleSettings particleSettings = new CustomParticleSettings(nbtParticle, maxId);
            if (EnumParticleTypes.getParticleNames().contains(particleSettings.enumName)) {
                LogWriter.error("Attempt to load a registered particle \"" + particleSettings.name + "\"");
                continue;
            }
            maxId++;
            EnumHelper.addEnum(EnumParticleTypes.class, particleSettings.enumName, additionalTypes, particleSettings.name, particleSettings.id, particleSettings.shouldIgnoreRange, particleSettings.argumentCount);

            EnumParticleTypes enumparticletypes = EnumParticleTypes.valueOf(particleSettings.enumName);
            int idT = enumparticletypes.getParticleID();

            particles.put(idT, enumparticletypes);
            byName.put(particleSettings.name, enumparticletypes);
            customparticles.put(particleSettings.id, particleSettings);
            boolean isExample = name.contains("PARTICLE_EXAMPLE") || name.contains("PARTICLE_OBJ_EXAMPLE");
            if (isExample || nbtParticle.getBoolean("CreateAllFiles")) {
                CustomNpcs.proxy.createAllFiles(particleSettings);
                nbtParticle.removeTag("CreateAllFiles");
                resave = true;
            }
            LogWriter.info("Load Custom Particle \"" + particleSettings.name + "\"");
        }
        if (resave) { Util.instance.saveFile(prtcsFile, nbtParticles); }
    }

    private static NBTTagCompound getParticleNbt(File file) {
        NBTTagCompound nbtInFile = new NBTTagCompound();
        NBTTagCompound compound = ModData.getExampleParticles().copy();
        try {
            if (file.exists()) { nbtInFile = NBTJsonUtil.LoadFile(file); }
        }
        catch (Exception e) { LogWriter.error("Try Load " + file.getName() + ": ", e); }

        List<String> names = new ArrayList<>();
        NBTTagList listInFile = nbtInFile.getTagList("Particles", 10);
        NBTTagList listParticles = compound.getTagList("Particles", 10);
        NBTTagList exampleParticles = listParticles.copy();
        boolean resave = false;
        for (int i = 0; i < listInFile.tagCount(); i++) {
            NBTTagCompound nbtItem = listInFile.getCompoundTagAt(i);
            String name = nbtItem.getString("RegistryName");
            boolean isExample = false;
            for (int j = 0; j < exampleParticles.tagCount(); j++) {
                if (name.equals(exampleParticles.getCompoundTagAt(j).getString("RegistryName"))) {
                    isExample = true;
                    break;
                }
            }
            if (names.contains(name)) {
                if (isExample) {
                    name = name.replace("example", "custom");
                    isExample = false;
                }
                while (names.contains(name)) { name += "_"; }
                nbtItem.setString("RegistryName", name);
                resave = true;
            }
            names.add(name);
            if (!isExample) { listParticles.appendTag(nbtItem); }
        }
        compound.setBoolean("resave", resave);
        return compound;
    }

}
