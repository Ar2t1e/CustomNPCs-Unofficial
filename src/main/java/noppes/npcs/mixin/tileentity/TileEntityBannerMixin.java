package noppes.npcs.mixin.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.mixin.tileentity.ITileEntityBanner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = TileEntityBanner.class, priority = 499)
public class TileEntityBannerMixin implements ITileEntityBanner {

    @Shadow private EnumDyeColor baseColor;
    @Shadow @Nullable private List<BannerPattern> patternList;
    @Shadow @Nullable private List<EnumDyeColor> colorList;

    @Unique public int npcs$factionId = -1;
    @Unique public ResourceLocation npcs$resourceFlag = null;

    @Override
    public int npcs$getFactionId(){ return npcs$factionId; }

    @Override
    public void npcs$setFactionId(int newFactionId) {
        if (newFactionId < -1) { newFactionId = -1; }
        npcs$factionId = newFactionId;
    }

    @Override
    public ResourceLocation npcs$getResourceFlag() { return npcs$resourceFlag; }

    @Override
    public void npcs$setResourceFlag(ResourceLocation newResourceFlag) {
        npcs$resourceFlag = newResourceFlag;
        if (npcs$resourceFlag != null) { npcs$clearMain(); }
    }

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(method = "readFromNBT", at = @At("TAIL"))
    public void npcs$readFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        boolean needClear = false;
        if (compound.hasKey("FactionID", 3)) {
            npcs$factionId = compound.getInteger("FactionID");
            needClear = true;
        }
        if (compound.hasKey("FlagResource", 8)) {
            npcs$resourceFlag = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("FlagResource")));
            needClear = true;
        }
        if (needClear) { npcs$clearMain(); }
    }

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(method = "setItemValues", at = @At("TAIL"))
    public void npcs$setItemValues(ItemStack stack, boolean isCustomColor, CallbackInfo ci) {
        npcs$factionId = -1;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey("BlockEntityTag", 10)) {
            NBTTagCompound compound = nbt.getCompoundTag("BlockEntityTag");
            boolean needClear = false;
            if (compound.hasKey("FactionID", 3)) {
                npcs$factionId = compound.getInteger("FactionID");
                needClear = true;
            }
            if (compound.hasKey("FlagResource", 8)) {
                npcs$resourceFlag = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("FlagResource")));
                needClear = true;
            }
            if (needClear) { npcs$clearMain(); }
        }
    }

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(method = "writeToNBT", at = @At("TAIL"))
    public void npcs$writeToNBT(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
        compound.setInteger("FactionID", npcs$factionId);
        if (npcs$resourceFlag != null) { compound.setString("FlagResource", npcs$resourceFlag.toString()); }
    }

    @Unique
    private void npcs$clearMain() {
        baseColor = EnumDyeColor.WHITE;
        patternList = Lists.newArrayList();
        colorList = Lists.newArrayList();
    }

}
