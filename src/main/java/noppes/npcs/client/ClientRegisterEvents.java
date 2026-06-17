package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomParticles;
import noppes.npcs.client.particles.CustomParticle;
import noppes.npcs.client.particles.CustomParticleSettings;
import noppes.npcs.creativetab.CustomCreativeTabs;

public class ClientRegisterEvents {

    public static final CreativeTabs CRAFTING_CUSTOM_GLOBAL_CATEGORY = new CustomCreativeTabs("CRAFTING_CUSTOM_GLOBAL_CATEGORY",
            new ItemStack(CustomItems.wand));
    public static final CreativeTabs CRAFTING_CUSTOM_ANVIL_CATEGORY = new CustomCreativeTabs("CRAFTING_CUSTOM_ANVIL_CATEGORY",
            new ItemStack(CustomBlocks.carpenty));

    public static void load() { cnpcsRegisterParticle(); }

    public static void cnpcsRegisterParticle() {
        ParticleManager manager =  Minecraft.getMinecraft().effectRenderer;
        for (int id : CustomParticles.customparticles.keySet()) {
            manager.registerParticle(id, (particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, parameters) -> {
                CustomParticleSettings ps = CustomParticles.customparticles.get(particleID);
                return new CustomParticle(ps == null ? new NBTTagCompound() : ps.nbtData, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            });
        }
    }

}
