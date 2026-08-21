package noppes.npcs.mixin.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;

@Mixin(value = ModelBox.class, priority = 502)
public interface IModelBoxMixin {

    @Accessor PositionTextureVertex[] getVertexPositions();

    @Accessor void setQuadList(@Nonnull TexturedQuad[] newQuadList);

}
