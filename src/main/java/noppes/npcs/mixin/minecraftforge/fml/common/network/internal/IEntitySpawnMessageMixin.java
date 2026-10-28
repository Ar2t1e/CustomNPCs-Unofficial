package noppes.npcs.mixin.minecraftforge.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = FMLMessage.EntitySpawnMessage.class, priority = 502, remap = false)
public interface IEntitySpawnMessageMixin {

    @Invoker("toBytes") void invokeToBytes(ByteBuf buf);
    @Invoker("fromBytes") void invokeFromBytes(ByteBuf buf);

}
