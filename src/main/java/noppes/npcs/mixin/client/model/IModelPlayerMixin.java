package noppes.npcs.mixin.client.model;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModelPlayer.class, priority = 502)
public interface IModelPlayerMixin {

    @Accessor
    void setBipedCape(ModelRenderer newBipedCape);

}
