package noppes.npcs.client.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;

import java.util.Map;
import java.util.UUID;

public class CustomTexturesPayload
        extends MinecraftTexturesPayload {

    public long timestamp;
    public UUID profileId;
    public String profileName;
    public boolean isPublic;
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;

    @Override
    public long getTimestamp() { return timestamp; }

    @Override
    public UUID getProfileId() { return profileId; }

    @Override
    public String getProfileName() { return profileName; }

    @Override
    public boolean isPublic() { return isPublic; }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures() { return textures; }

}
