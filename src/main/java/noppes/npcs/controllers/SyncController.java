package noppes.npcs.controllers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.config.ConfigLoader;
import noppes.npcs.controllers.data.*;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.util.BuilderData;

public class SyncController {

	// New from Unofficial (BetaZavr)
	public static final Map<Integer, BuilderData> dataBuilder = new HashMap<>();

	public static void syncPlayer(EntityPlayerMP player) {
		// send all faction settings
		syncAllFactions(player);
		// player data quests
		syncAllQuests(player);
		// player data dialogs
		syncAllDialogs(player);
		// dimension data
		syncAllDimensions(player);
		// global recipes
		syncAllRecipes(player);
		// main player data
		PlayerData data = PlayerData.get(player);
		Packets.send(player, new PacketSync(8, data.getNBT(), true));

		// New from Unofficial (BetaZavr)
		syncScriptItems(player); // item script textures
		BorderController.getInstance().sendTo(player); // borders data
		MarcetController.getInstance().sendTo(player, -1); // markets
		ScriptController.Instance.sendClientTo(player); // send all client scripts
		CustomNpcsPermissions.sendTo(player); // permissions
		AnimationController.getInstance().sendTo(player); // custom animations
		ConfigLoader.sendTo(player); // mod data
		Packets.send(player, new PacketSync(17, KeyController.getInstance().getNBT(), true)); // custom keys
	}

	private static void syncAllFactions(EntityPlayerMP player) {
		NBTTagList list = new NBTTagList();
		NBTTagCompound compound = new NBTTagCompound();
		for (Faction faction : FactionController.instance.factions.values()) { list.appendTag(faction.save(new NBTTagCompound())); }
		compound.setTag("Data", list);
		Packets.send(player, new PacketSync(1, compound, true));
	}

	public static void syncAllQuests(EntityPlayerMP player) {
		for (QuestCategory category : QuestController.instance.categories.values()) {
			Packets.send(player, new PacketSync(3, category.save(new NBTTagCompound()), false));
		}
		Packets.send(player, new PacketSync(3, new NBTTagCompound(), true));
	}

	public static void syncAllDialogs(EntityPlayerMP player) {
		for (DialogCategory category : DialogController.instance.categories.values()) {
			Packets.send(player, new PacketSync(5, category.save(new NBTTagCompound()), false));
		}
		Packets.send(player, new PacketSync(5, new NBTTagCompound(), true));
		Packets.send(player, new PacketSyncUpdate(0, 11, DialogController.instance.getGuiSettings().save()));
	}

	private static void syncAllDimensions(EntityPlayerMP player) {
		DimensionController.load();
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (WorldServer world : CustomNpcs.Server.worlds) {
			DimensionData data = DimensionController.get(world);
			NBTTagCompound nbt = data.save();
			nbt.setBoolean("loaded", world.isBlockLoaded(BlockPos.ORIGIN));
			nbt.setInteger("name", world.provider.getDimension());
			list.appendTag(nbt);
		}
		compound.setTag("Data", list);
		Packets.send(player, new PacketSync(9, compound, true));
	}

	private static void syncAllRecipes(EntityPlayerMP player) {
		RecipeController.getInstance().sendTo(player);
		player.unlockRecipes(RecipeController.instance.getKnownRecipes());
	}

	// New from Unofficial (BetaZavr)
	public static void syncScriptItems(EntityPlayerMP player) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Resources));
		Packets.send(player, new PacketSync(16, compound, false));
	}

	public static void syncScriptItemsEverybody() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Resources));
		for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
			Packets.send(player, new PacketSync(16, compound, false));
		}
	}

}
