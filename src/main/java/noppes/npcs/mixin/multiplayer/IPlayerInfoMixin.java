package noppes.npcs.mixin.multiplayer;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = PlayerInfo.class, priority = 502)
public interface IPlayerInfoMixin {

    @Accessor Map<MinecraftProfileTexture.Type, ResourceLocation> getTextureLocations();

}
