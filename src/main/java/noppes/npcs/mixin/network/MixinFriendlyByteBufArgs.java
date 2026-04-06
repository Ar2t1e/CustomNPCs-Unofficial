package noppes.npcs.mixin.network;

import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FriendlyByteBuf.class, priority = 498)
public class MixinFriendlyByteBufArgs {

    @ModifyVariable(
            method = {"readByteArray(I)[B"},
            argsOnly = true,
            at = @At("LOAD")
    )
    private int npcs$adjustReadByteCapacity(int capacity) {
        return capacity == 32600 ? ((FriendlyByteBuf) (Object) this).readableBytes() : capacity;
    }

}
