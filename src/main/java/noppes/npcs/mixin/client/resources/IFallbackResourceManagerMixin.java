package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mixin(value = FallbackResourceManager.class, priority = 502)
public interface IFallbackResourceManagerMixin {

    @Accessor("resourcePacks")
    List<IResourcePack> getResourcePacks();

}
