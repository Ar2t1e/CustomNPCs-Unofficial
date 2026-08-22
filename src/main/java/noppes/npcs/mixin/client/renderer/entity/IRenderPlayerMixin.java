package noppes.npcs.mixin.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RenderPlayer.class, priority = 502)
public interface IRenderPlayerMixin {

    @Accessor boolean getSmallArms();

}
