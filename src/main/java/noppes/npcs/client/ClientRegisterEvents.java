package noppes.npcs.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomParticles;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.blocks.custom.CustomBlockLiquid;
import noppes.npcs.client.particles.CustomParticle;
import noppes.npcs.client.particles.CustomParticleSettings;
import noppes.npcs.creativetab.CustomCreativeTabs;
import noppes.npcs.fluids.CustomFluid;

import java.util.ArrayList;
import java.util.List;

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

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        List<Block> liquidBlocks = new ArrayList<>();
        for (ICustomElement element : CustomBlocks.customblocks.keySet()) {
            if (element instanceof CustomBlockLiquid) {
                liquidBlocks.add((Block) element);
            }
        }
        if (liquidBlocks.isEmpty()) return;
        event.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (state.getBlock() instanceof CustomBlockLiquid) {
                Fluid fluid = ((BlockFluidClassic) state.getBlock()).getFluid();
                if (fluid instanceof CustomFluid) { return fluid.getColor(); }
            }
            return 0xFFFFFFFF;
        }, liquidBlocks.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            FluidStack fluidStack = FluidUtil.getFluidContained(stack);
            if (fluidStack != null && fluidStack.getFluid() instanceof CustomFluid) {
                return fluidStack.getFluid().getColor();
            }
            return 0xFFFFFFFF;
        }, Items.WATER_BUCKET);
    }

}
