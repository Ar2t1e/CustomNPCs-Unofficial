package noppes.npcs.util;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.EntitySpawnHandler;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import noppes.npcs.mixin.minecraftforge.fml.common.network.internal.IEntitySpawnHandlerMixin;
import noppes.npcs.mixin.minecraftforge.fml.common.network.internal.IEntitySpawnMessageMixin;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

public class EntitySpawnMessageHelper {

	private static final IEntitySpawnHandlerMixin HANDLER = (IEntitySpawnHandlerMixin) new EntitySpawnHandler();

	public static void spawn(FMLMessage.EntitySpawnMessage msg) {
		try { HANDLER.invokeSpawnEntity(msg); }
		catch (Exception e) { LogWriter.error(e); }
	}

	public static void toBytes(@Nonnull FMLMessage.EntitySpawnMessage msg, @Nonnull ByteBuf buf) {
		((IEntitySpawnMessageMixin) msg).invokeToBytes(buf);
	}

	public static FMLMessage.EntitySpawnMessage fromBytes(@Nonnull ByteBuf buffer) {
		FMLMessage.EntitySpawnMessage msg = new FMLMessage.EntitySpawnMessage();
		((IEntitySpawnMessageMixin) msg).invokeFromBytes(buffer);
		return msg;
	}

}
