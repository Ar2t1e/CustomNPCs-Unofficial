package noppes.npcs.mixin.client.network;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = NetworkPlayerInfo.class, priority = 502)
public interface INetworkPlayerInfoMixin {

    @Accessor Map<MinecraftProfileTexture.Type, ResourceLocation> getPlayerTextures();

}
