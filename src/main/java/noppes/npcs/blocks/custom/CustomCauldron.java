package noppes.npcs.blocks.custom;

import net.minecraft.block.BlockCauldron;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.fluids.CustomFluid;

import javax.annotation.Nonnull;

public class CustomCauldron extends BlockCauldron implements ICustomElement {

    protected final @Nonnull NBTTagCompound nbtData;
    protected final @Nonnull CustomFluid fluid;

    public CustomCauldron(@Nonnull CustomFluid fluidIn, @Nonnull NBTTagCompound nbtBlock) {
        super();
        this.fluid = fluidIn;
        nbtData = nbtBlock;
        String name = "custom_cauldron_" + nbtBlock.getString("RegistryName");
        setRegistryName(CustomNpcs.MODID, name.toLowerCase());
        setUnlocalizedName(name.toLowerCase());
    }

    @Override
    public String getCustomName() { return nbtData.getString("RegistryName"); }

    @Override
    public INbt getCustomNbt() { return new NBTWrapper(nbtData); }

    @Override
    public int getElementType() { return 1; }

    @Override
    public boolean showInCreative() { return false; }

}