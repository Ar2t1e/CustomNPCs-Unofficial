package noppes.npcs.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import noppes.npcs.api.interfaces.IgnoreForAPI;

@IgnoreForAPI
public interface IChatMessages {

   void addMessage(String message, Entity entity);

   void renderMessages(PoseStack poseStack, MultiBufferSource typeBuffer, float textscale, boolean inRange, int lightMapUV, boolean isPlayer);

}
