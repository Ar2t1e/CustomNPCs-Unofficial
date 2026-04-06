package noppes.npcs.api.mixin.tileentity;

import net.minecraft.util.ResourceLocation;

public interface ITileEntityBanner {

    int npcs$getFactionId();

    void npcs$setFactionId(int newFactionId);

    ResourceLocation npcs$getResourceFlag();

    void npcs$setResourceFlag(ResourceLocation newFactionId);

}
