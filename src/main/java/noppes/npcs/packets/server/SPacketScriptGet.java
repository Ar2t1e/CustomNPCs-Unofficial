package noppes.npcs.packets.server;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketScriptConsole;
import noppes.npcs.packets.client.PacketScriptText;
import noppes.npcs.util.Util;

public class SPacketScriptGet extends PacketServerBasic {

   protected static int channelId;
   private int type;

   public SPacketScriptGet() { }

   public SPacketScriptGet(int typeIn) { type = typeIn; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.scripted_item || item.getItem() == CustomItems.scripter || item.getItem() == CustomItems.wand ||
              item.getItem() == CustomBlocks.scripted_door_item || item.getItem() == CustomBlocks.scripted_item;
   }

   @Override
   public boolean requiresNpc() { return type == 0; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(type); }

   @Override
   public void decode(FriendlyByteBuf buf) { type = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NBTTagCompound compound = new NBTTagCompound();
      switch (type) {
         case 0: {
            npc.script.save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.npcScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // NPC scripted methods
         case 1: {
            PlayerData data = PlayerData.get(player);
            TileEntity tile = player.world.getTileEntity(data.scriptBlockPos);
            if (!(tile instanceof TileScripted)) {
               CustomNpcs.debugData.end("Packets");
               return;
            }
            ((TileScripted) tile).save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.blockScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // Tile scripted methods
         case 2: {
            ItemScriptedWrapper iw = (ItemScriptedWrapper) Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(player.getHeldItemMainhand());
            compound = iw.getMCNbt();
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.itemScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // Item scripted methods
         case 3: {
            ScriptController.Instance.forgeScripts.save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(new ArrayList<>(ForgeEventHandler.eventNames.values())));
            break;
         } // Forge scripted methods
         case 4: {
            ScriptController.Instance.playerScripts.save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.playerScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // Player scripted methods
         case 5: {
            PlayerData data = PlayerData.get(player);
            TileEntity tile = player.world.getTileEntity(data.scriptBlockPos);
            if (!(tile instanceof TileScriptedDoor)) {
               CustomNpcs.debugData.end("Packets");
               return;
            }
            ((TileScriptedDoor)tile).getNBT(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.doorScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // Tile scripted door methods
         case 6: {
            ScriptController.Instance.clientScripts.save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(new ArrayList<>(ForgeEventHandler.clientEventNames.values())));
            break;
         } // Client scripted methods
         case 7: {
            ScriptController.Instance.potionScripts.save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.potionScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // Potion scripted methods
         case 8: {
            ScriptController.Instance.npcsScripts.save(compound);
            compound.setTag("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.npcScripts).map((type) -> type.function).collect(Collectors.toList())));
            break;
         } // To all NPC's scripted methods
      }
      compound.setTag("Languages", ScriptController.Instance.nbtLanguages(type == 6));
      compound.setString("DirPath", ScriptController.Instance.dir.getAbsolutePath());
      // collect and clear scripts and consoles
      Map<Integer, List<String>> mapScripts = new TreeMap<>();
      Map<Integer, Map<Long, List<String>>> mapConsoles = new TreeMap<>();
      NBTTagList scripts = compound.getTagList("Scripts", 10);
      for (int i = 0; i < scripts.tagCount(); i++) {
         NBTTagCompound scriptNbt = scripts.getCompoundTagAt(i);
         // Script
         if (scriptNbt.hasKey("Script", 8)) { mapScripts.put(i, Util.instance.getStringData(scriptNbt.getString("Script"))); }
         else {
            mapScripts.put(i, new ArrayList<>());
            NBTTagList list = scriptNbt.getTagList("Script", 8);
            for (int k = 0; k < list.tagCount(); k++) { mapScripts.get(i).add(list.getStringTagAt(k)); }
         }
         scriptNbt.setTag("Script", new NBTTagList());
         // Console
         NBTTagList consoles = scripts.getCompoundTagAt(i).getTagList("Console", 10);
         for (int j = 0; j < consoles.tagCount(); j++) {
            if (!mapConsoles.containsKey(i)) { mapConsoles.put(i, new LinkedHashMap<>()); }
            NBTTagCompound errorNbt = consoles.getCompoundTagAt(j);
            long time = errorNbt.getLong("Long");
            if (errorNbt.hasKey("String", 8)) {
               mapConsoles.get(i).put(time, Util.instance.getStringData(errorNbt.getString("String")));
            }
            else {
               mapConsoles.get(i).put(time, new ArrayList<>());
               NBTTagList list = errorNbt.getTagList("String", 8);
               for (int k = 0; k < list.tagCount(); k++) { mapConsoles.get(i).get(time).add(list.getStringTagAt(k)); }
            }
            errorNbt.setTag("String", new NBTTagList());
         }
      }
      Packets.send(player, new PacketGuiData(compound));
      for (int tab : mapScripts.keySet()) {
         List<String> scriptStrings = mapScripts.get(tab);
         int i = 0;
         for (String part : scriptStrings) {
            Packets.send(player, new PacketScriptText(tab, i++, scriptStrings.size(), part, false));
         }
      }
      for (int tab : mapConsoles.keySet()) {
         for (long time : mapConsoles.get(tab).keySet()) {
            List<String> consoleStrings = mapConsoles.get(tab).get(time);
            int i = 0;
            for (String part : consoleStrings) {
               Packets.send(player, new PacketScriptConsole(tab, time, i++, consoleStrings.size(), part, false));
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
