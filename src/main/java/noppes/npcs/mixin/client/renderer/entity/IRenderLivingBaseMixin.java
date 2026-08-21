package noppes.npcs.mixin.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.*;

@Mixin(value = RenderLivingBase.class, priority = 498)
public interface IRenderLivingBaseMixin<T extends EntityLivingBase> {

    @Accessor List<LayerRenderer<T>> getLayerRenderers();

}
