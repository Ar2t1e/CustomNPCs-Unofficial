package noppes.npcs.mixin.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.mixin.client.renderer.entity.IRenderLivingBaseMixin;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(value = RenderLivingBase.class, priority = 498)
public class RenderLivingBaseMixin<T extends EntityLivingBase> implements IRenderLivingBaseMixin {

    @Shadow protected List<LayerRenderer<T>> layerRenderers;

    @Override
    public LayerRenderer<T> npcs$getLayer(Class<?> layerClass) {
        for (LayerRenderer<T> layer : layerRenderers) {
            if (layerClass.isAssignableFrom(layer.getClass())) {
                return layer;
            }
        }
        return null;
    }

    @Override
    public List<LayerRenderer<?>> npcs$getLayers() { return new ArrayList<>(layerRenderers); }

}
