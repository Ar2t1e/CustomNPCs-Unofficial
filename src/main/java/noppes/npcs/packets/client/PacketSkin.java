package noppes.npcs.packets.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.SkinUtil;
import noppes.npcs.client.util.CustomTexturesPayload;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.controllers.data.SkinData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSkin;
import noppes.npcs.shared.common.PacketBasic;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

public class PacketSkin extends PacketBasic {

    private static final Gson gson = new Gson();
    protected static int channelId;

    private int type;
    private NBTTagCompound data;

    public PacketSkin() { }

    public PacketSkin(int typeIn, NBTTagCompound dataIn) {
        type = typeIn;
        data = dataIn;
    }


    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        switch (type) {
            case 0: {
                GameProfile profile = player.getGameProfile();
                Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ImmutableMap.of();
                if (property != null) {
                    try {
                        String json = new String(Base64.getDecoder().decode(property.getValue()), StandardCharsets.UTF_8);
                        CustomTexturesPayload ctp = (CustomTexturesPayload) gson.fromJson(json, MinecraftTexturesPayload.class);
                        map = ctp.textures;
                    }
                    catch (JsonParseException ignored) {}
                }
                NBTTagCompound compound = new NBTTagCompound();
                compound.setUniqueId("UUID", player.getUniqueID());
                NBTTagList list = new NBTTagList();
                for (MinecraftProfileTexture.Type t : MinecraftProfileTexture.Type.values()) {
                    ResourceLocation location;
                    if (map.containsKey(t)) {
                        MinecraftProfileTexture mpt = map.get(t);
                        String sha1 = Hashing.sha1().hashUnencodedChars(mpt.getHash()).toString();
                        switch (t) {
                            case CAPE: location = new ResourceLocation("capes/" + sha1); break;
                            case ELYTRA: location = new ResourceLocation("elytra/" + sha1); break;
                            default: location = new ResourceLocation("skins/" + sha1); break;
                        }
                        SkinData skinData = SkinData.create(t, location);
                        skinData.setIsDefault();
                        list.appendTag(skinData.save());
                    }
                    else if (t == MinecraftProfileTexture.Type.SKIN) {
                        UUID uuid = profile.getId();
                        if (uuid == null) { uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + profile.getName()).getBytes(StandardCharsets.UTF_8)); }
                        location = DefaultPlayerSkin.getDefaultSkin(uuid);
                        SkinData skinData = SkinData.create(t, location);
                        skinData.setIsDefault();
                        list.appendTag(skinData.save());
                    }
                }
                compound.setTag("Textures", list);
                Packets.sendServer(new SPacketSkin(compound));
                break;
            } // get skin
            case 1: {
                SkinUtil.resetSkin(PlayerSkinController.getInstance().loadPlayerSkin(data));
                break;
            } // set
        }
        CustomNpcs.debugData.end("Packets");
    }

}
