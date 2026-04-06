package noppes.npcs;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.registries.DeferredRegister;
import noppes.npcs.client.particles.CustomParticleType;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ModData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomParticleTypes {

    public static final DeferredRegister<ParticleType<?>> CUSTOM_PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, CustomNpcs.MODID);
    public static final Map<String, ParticleType<CustomParticleType>> CUSTOMS = new HashMap<>();

    static {
        File prtcsFile = new File(CustomNpcs.Dir, "custom_particles.js");
        CompoundTag nbtParticles = getParticleNbt(prtcsFile);
        boolean resave = nbtParticles.getBoolean("resave");
        nbtParticles.remove("resave");

        for (int i = 0; i < nbtParticles.getList("Particles", 10).size(); i++) {
            CompoundTag nbtParticle = nbtParticles.getList("Particles", 10).getCompound(i);
            if (!nbtParticle.contains("RegistryName", 8)) {
                LogWriter.error("Attempt to load particle pos: " + i + " - failed");
                continue;
            }
            String preName = nbtParticle.getString("RegistryName");
            String name = NoppesUtilServer.validPath(preName);
            if (!preName.equals(name)) {
                nbtParticle.putString("RegistryName", name);
                resave = true;
            }
            if (!CUSTOMS.containsKey(name)) {
                CustomParticleType particleType = new CustomParticleType(nbtParticle.getBoolean("OverrideLimiter"), nbtParticle);
                CUSTOM_PARTICLES.register(name, () -> particleType);
                CUSTOMS.put(name, particleType);

                if (name.equalsIgnoreCase("PARTICLE_EXAMPLE") ||
                        name.equalsIgnoreCase("PARTICLE_OBJ_EXAMPLE") ||
                        nbtParticle.getBoolean("CreateAllFiles")) {
                    CustomNpcs.proxy.createAllFiles(particleType);
                    nbtParticle.remove("CreateAllFiles");
                    resave = true;
                }
                LogWriter.info("Load Custom Particle \"" +name + "\"");
            }
        }
        if (resave) { Util.instance.saveFile(prtcsFile, nbtParticles); }
    }

    private static CompoundTag getParticleNbt(File file) {
        CompoundTag nbtInFile = new CompoundTag();
        CompoundTag compound = ModData.getExampleParticles().copy();
        try {
            if (file.exists()) { nbtInFile = NBTJsonUtil.LoadFile(file); }
        }
        catch (Exception e) { LogWriter.error("Try Load " + file.getName() + ": ", e); }

        List<String> names = new ArrayList<>();
        ListTag listInFile = nbtInFile.getList("Particles", 10);
        ListTag listParticles = compound.getList("Particles", 10);
        ListTag exampleParticles = listParticles.copy();
        boolean resave = false;
        for (int i = 0; i < listInFile.size(); i++) {
            CompoundTag nbtItem = listInFile.getCompound(i);
            String name = nbtItem.getString("RegistryName");
            boolean isExample = false;
            for (int j = 0; j < exampleParticles.size(); j++) {
                if (name.equals(exampleParticles.getCompound(j).getString("RegistryName"))) {
                    isExample = true;
                    break;
                }
            }
            if (!names.contains(name)) {
                names.add(name);
                if (!isExample) { listParticles.add(nbtItem); }
            }
        }
        compound.putBoolean("resave", resave);
        return compound;
    }

}
