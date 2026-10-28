package noppes.npcs.mixin.minecraftforge.fml.common.network.internal;

import net.minecraftforge.fml.common.network.internal.EntitySpawnHandler;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntitySpawnHandler.class, priority = 502, remap = false)
public interface IEntitySpawnHandlerMixin {

    @Invoker("spawnEntity") void invokeSpawnEntity(FMLMessage.EntitySpawnMessage spawnMsg);

}
