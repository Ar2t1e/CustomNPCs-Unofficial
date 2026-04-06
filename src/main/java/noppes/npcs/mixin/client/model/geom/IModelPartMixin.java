package noppes.npcs.mixin.client.model.geom;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ModelPart.class, priority = 502)
public interface IModelPartMixin {

    @Accessor Map<String, ModelPart> getChildren();

}
