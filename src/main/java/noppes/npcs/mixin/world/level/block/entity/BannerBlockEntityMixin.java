package noppes.npcs.mixin.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.mixin.world.level.block.entity.ITileEntityBanner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = BannerBlockEntity.class, priority = 498)
public class BannerBlockEntityMixin implements ITileEntityBanner {

    @Shadow private DyeColor baseColor;
    @Shadow @Nullable private ListTag itemPatterns;
    @Shadow @Nullable private List<Pair<Holder<BannerPattern>, DyeColor>> patterns;

    @Unique public int npcs$factionId = -1;
    @Unique public ResourceLocation npcs$resourceFlag = null;

    @Override
    public int npcs$getFactionId() { return npcs$factionId; }

    @Override
    public void npcs$setFactionId(int newFactionId) {
        if (newFactionId < 0) { newFactionId = -1; }
        npcs$factionId = newFactionId;
        if (npcs$factionId > -1) { npcs$clearMain(); }
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
    @Inject(method = "load", at = @At("TAIL"))
    public void npcs$load(CompoundTag compound, CallbackInfo ci) {
        boolean needClear = false;
        if (compound.contains("FactionID", 3)) {
            npcs$factionId = compound.getInt("FactionID");
            needClear = true;
        }
        if (compound.contains("FlagResource", 8)) {
            npcs$resourceFlag = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("FlagResource")));
            needClear = true;
        }
        if (needClear) { npcs$clearMain(); }
    }

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(method = "fromItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    public void npcs$fromItem(ItemStack stack, CallbackInfo ci) {
        npcs$factionId = -1;
        CompoundTag compound = BlockItem.getBlockEntityData(stack);
        if (compound != null) {
            boolean needClear = false;
            if (compound.contains("FactionID", 3)) {
                npcs$factionId = compound.getInt("FactionID");
                needClear = true;
            }
            if (compound.contains("FlagResource", 8)) {
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
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void npcs$saveAdditional(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("FactionID", npcs$factionId);
        if (npcs$resourceFlag != null) { compound.putString("FlagResource", npcs$resourceFlag.toString()); }
    }

    @Unique
    private void npcs$clearMain() {
        baseColor = DyeColor.WHITE;
        itemPatterns = new ListTag();
        patterns = Lists.newArrayList();
    }

}
