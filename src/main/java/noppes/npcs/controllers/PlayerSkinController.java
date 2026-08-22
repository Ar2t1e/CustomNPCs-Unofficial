package noppes.npcs.controllers;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSkin;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.controllers.data.SkinData;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class PlayerSkinController {

	protected static PlayerSkinController instance = new PlayerSkinController();
	protected static final String filename = "player_skins";
	public static PlayerSkinController getInstance() {
		if (instance == null) { instance = new PlayerSkinController(); }
		return instance;
	}
	private static Type getType(int type) {
		if (type < 0) { type *= -1; }
		return Type.values()[type % Type.values().length];
	}
	public static void unload() {
		if (instance != null) {
			instance.playerNames.clear();
			instance.data.clear();
			instance = null;
		}
	}
	private final Map<UUID, String> playerNames = new HashMap<>();
	private final Map<UUID, Map<Type, SkinData>> data = new HashMap<>();

	public PlayerSkinController() { loadPlayerSkins(); }

	@SuppressWarnings("ConstantConditions")
	public void update(SkinData skinDataIn) {
		if (skinDataIn == null || CustomNpcs.Server == null) { return; }
		for (UUID uuid : data.keySet()) {
			for (SkinData skinData : data.get(uuid).values()) {
				if (skinDataIn.equals(skinData)) {
					EntityPlayerMP player = CustomNpcs.Server.getPlayerList().getPlayerByUUID(uuid);
					if (player != null) { sendToAll(uuid); } // online
					break;
				}
			}
		}
	}

	private void loadPlayerSkins() {
		CustomNpcs.debugData.start("Mod");
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			NBTTagCompound compound = CompressedStreamTools.readCompressed(Files.newInputStream(new File(saveDir, filename + ".dat").toPath()));
			loadPlayerSkins(compound);
		} catch (Exception e) { save(); }
		CustomNpcs.debugData.end("Mod");
	}

	public void loadPlayerSkins(NBTTagCompound compound) {
		playerNames.clear();
		data.clear();
		NBTTagList list = compound.getTagList("Data", 10);
		for (int i = 0; i < list.tagCount(); ++i) { loadPlayerSkin(list.getCompoundTagAt(i)); }
	}

	public UUID loadPlayerSkin(NBTTagCompound nbtSkin) {
		if (nbtSkin == null) { return null; }
		UUID uuid = nbtSkin.getUniqueId("UUID");
		NBTTagList list = nbtSkin.getTagList("Textures", 10);
		if (list.tagCount() == 0) {
			playerNames.remove(uuid);
			data.remove(uuid);
			return null;
		}
		playerNames.put(uuid, nbtSkin.getString("Player"));
		Map<Type, SkinData> skins = new EnumMap<>(Type.class);
		for (int i = 0; i < nbtSkin.getTagList("Textures", 10).tagCount(); i++) {
			SkinData sd = new SkinData();
			sd.load(nbtSkin.getTagList("Textures", 10).getCompoundTagAt(i));
			if (sd.isValid()) { skins.put(sd.type(), sd); }
		}
		data.put(uuid, skins);
		return uuid;
	}

	public void logged(EntityPlayerMP player) {
		UUID uuid = player.getUniqueID();
		String name = player.getName();
		if (data.containsKey(uuid)) {
			playerNames.put(uuid, name);
			sendToAll(uuid);
		}
		else if (playerNames.containsValue(name)) {
			for (UUID id : playerNames.keySet()) {
				if (playerNames.get(id).equals(name)) {
					Map<Type, SkinData> map = new EnumMap<>(Type.class);
					for (Type type : data.get(id).keySet()) { map.put(type, data.get(id).get(type).copy()); }
					data.put(uuid, map);
					sendToAll(uuid);
					break;
				}
			}
		}
		else { Packets.send(player, new PacketSkin(0, new NBTTagCompound())); }
		if (player.getServer() != null) {
			for (EntityPlayerMP pl : player.getServer().getPlayerList().getPlayers()) {
				if (pl.equals(player) || !data.containsKey(pl.getUniqueID())) { continue; }
				Packets.send(player, new PacketSkin(1, getNBT(pl.getUniqueID())));
			}
		}
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList listUUIDs = new NBTTagList();
		for (UUID uuid : data.keySet()) {
			if (uuid == null) { continue; }
			listUUIDs.appendTag(getNBT(uuid));
		}
		compound.setTag("Data", listUUIDs);
		return compound;
	}

	public NBTTagCompound getNBT(UUID uuid) {
		NBTTagCompound nbtPlayer = new NBTTagCompound();
		nbtPlayer.setUniqueId("UUID", uuid);
		NBTTagList textures = new NBTTagList();
		for (Type type : data.get(uuid).keySet()) {
			SkinData sd = data.get(uuid).get(type);
			if (sd == null) { continue; }
			textures.appendTag(sd.save());
		}
		nbtPlayer.setTag("Textures", textures);
		nbtPlayer.setString("Player", playerNames.get(uuid));
		return nbtPlayer;
	}

	@SuppressWarnings("unused")
	public void sendToAll() {
		if (CustomNpcs.Server != null) {
			for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) { sendToAll(player.getUniqueID()); }
		}
	}

	public void sendToAll(UUID uuid) {
		if (data.containsKey(uuid)) {
			NBTTagCompound nbtPlayer = getNBT(uuid);
			Packets.sendAll(new PacketSkin(1, nbtPlayer));
		}
	}

	public @Nonnull SkinData getData(UUID uuid, Type type) {
		if (!data.containsKey(uuid) || type == null) { return new SkinData(); }
		return data.get(uuid).get(type);
	}

	public @Nonnull SkinData getData(UUID uuid, int type) { return getData(uuid, getType(type)); }

	public void save() {
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			File file = new File(saveDir, filename + ".dat_new");
			File file1 = new File(saveDir, filename + ".dat_old");
			File file2 = new File(saveDir, filename + ".dat");
			CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
			if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
			if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
			if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
		}
		catch (Exception e) { LogWriter.except(e); }
	}

	public void set(String uuid, String location, int slot) {
		UUID id;
		try { id = UUID.fromString(uuid); } catch (Exception ignored) { return; }
		SkinData sd = getData(id, slot);
		sd.reset(location);
		if (data.containsKey(id)) { data.put(id, new EnumMap<>(Type.class)); }
		data.get(id).put(sd.type(), sd);
		save();
		update(sd);
	}

	public void set(String uuid, int type, int gender, int body, int bodyColor, int hair, int hairColor, int face, int eyesColor, int leg, int jacket, int shoes, int... peculiarities) {
		UUID id;
		try { id = UUID.fromString(uuid); } catch (Exception ignored) { return; }
		SkinData sd = getData(id, type);
		sd.setGender(gender);
		sd.setBodyType(body);
		sd.setBodyColor(bodyColor);
		sd.setHairType(hair);
		sd.setHairColor(hairColor);
		sd.setFaceType(face);
		sd.setEyesColor(eyesColor);
		sd.setPantsType(leg);
		sd.setJacketType(jacket);
		sd.setShoesType(shoes);
		List<Integer> list = new ArrayList<>();
		if (peculiarities != null) {
			for (int i : peculiarities) { list.add(i); }
		}
		sd.setPeculiarities(list);
		if (data.containsKey(id)) { data.put(id, new EnumMap<>(Type.class)); }
		data.get(id).put(sd.type(), sd);
		save();
		update(sd);
	}

	public String get(EntityPlayer player, int type) {
		SkinData sd = getData(player.getUniqueID(), type);
		return sd.isUrl() ? sd.getUrl() : sd.getLocation().toString();
	}

	public Map<Type, SkinData> get(UUID uuid) { return data.get(uuid); }

	/**
	 * @param playerNameOrUUID name or uuid of player
	 * @param type 0:SKIN, 1:CAPE, 2:ELYTRA;
	 * @return location string
	 */
	public SkinData get(String playerNameOrUUID, int type) {
		if (playerNameOrUUID == null || playerNameOrUUID.isEmpty()) { return null; }
		SkinData sd = null;
		if (playerNames.containsValue(playerNameOrUUID)) {
			for (UUID uuid : playerNames.keySet()) {
				if (playerNames.get(uuid).equals(playerNameOrUUID)) {
					if (data.containsKey(uuid)) { sd = data.get(uuid).get(getType(type)); }
					break;
				}
			}
		}
		else {
			try {
				UUID uuid = UUID.fromString(playerNameOrUUID);
				if (data.containsKey(uuid)) { sd = data.get(uuid).get(getType(type)); }
			}
			catch (Exception ignored) {}
		}
		return sd;
	}

	public boolean hasData(UUID uuid) { return data.containsKey(uuid); }

	@SuppressWarnings("ConstantConditions")
	public void clear(String uuid, int type) {
		Type t = getType(type);
		for (UUID id : data.keySet()) {
			if (uuid == null || id.toString().equals(uuid)) {
				SkinData sd = data.get(id).get(t);
				if (!sd.remove()) { data.get(id).remove(t); }
				if (uuid != null) {
					if (CustomNpcs.Server != null) {
						EntityPlayerMP player = CustomNpcs.Server.getPlayerList().getPlayerByUUID(id);
						if (player != null) { // online
							save();
							sendToAll(player.getUniqueID());
							return;
						}
					}
					break;
				}
			}
		}
		save();
	}

	public SkinData create(UUID uuid, String player, int slot, int type, String location) {
		Type t = getType(slot);
		SkinData skinData = SkinData.create(t, null);
		if (type == 0) { skinData.setUrl(location); }
		else if (type == 1) { skinData.setLocation(location); }
		else { skinData.reset(location); }
		if (!data.containsKey(uuid)) { data.put(uuid, new EnumMap<>(Type.class)); }
		data.get(uuid).put(t, skinData);
		playerNames.put(uuid, player);
		save();
		sendToAll(uuid);
		return skinData;
	}

	public String getName(UUID uuid) {
		String name = playerNames.get(uuid);
		return name == null ? "" : name;
	}

}
