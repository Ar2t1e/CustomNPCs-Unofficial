package noppes.npcs.mixin.util.text;

import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ITextComponent.Serializer.class)
public class ITextComponentSerializerMixin {

    @ModifyVariable(
            method = "serialize(Lnet/minecraft/util/text/ITextComponent;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ITextComponent npcs$serialize(ITextComponent component) {
        return component instanceof Component ? ((Component) component).getParent() : component;
    }

}
