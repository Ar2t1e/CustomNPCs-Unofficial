package noppes.npcs.api.wrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.SPacketCustomNBT;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

public class PlayerWrapper<T extends Player> extends EntityLivingBaseWrapper<T> implements IPlayer<T> {

   // New from Unofficial (BetaZavr)
   public static WrapperEntityData clientWrapperPlayerData;

   private IContainer inventory;
   private Object pixelmonPartyStorage;
   private Object pixelmonPCStorage;

   private PlayerData data;

   public PlayerWrapper(T player) { super(player); }

   public String getName() { return entity.getName().getString(); }

   public String getDisplayName() { return entity.getDisplayName().getString(); }

   public int getHunger() { return entity.getFoodData().getFoodLevel(); }

   public void setHunger(int level) { entity.getFoodData().setFoodLevel(level); }

   public boolean hasFinishedQuest(int id) { return getData().questData.hasFinishedQuest(id); }

   public boolean hasActiveQuest(int id) {
      PlayerQuestData data = getData().questData;
      return data.activeQuests.containsKey(id);
   }

   public IQuest[] getActiveQuests() {
      PlayerQuestData data = getData().questData;
      List<IQuest> quests = new ArrayList<>();
      for (int id : data.activeQuests.keySet()) {
         IQuest quest = QuestController.instance.quests.get(id);
         if (quest != null) {
            quests.add(quest);
         }
      }
      return quests.toArray(new IQuest[0]);
   }

   public IQuest[] getFinishedQuests() {
      PlayerQuestData data = getData().questData;
      List<IQuest> quests = new ArrayList<>();
      for (int id : data.getFinishedQuest()) {
         IQuest quest = QuestController.instance.quests.get(id);
         if (quest != null) { quests.add(quest); }
      }
      return quests.toArray(new IQuest[0]);
   }

   public void startQuest(int id) {
      Quest quest = QuestController.instance.quests.get(id);

      if (quest == null) { return; }
      PlayerQuestController.addActiveQuest(quest, entity, true);
   }

   public void sendNotification(String title, String msg, int type) {
      if (type >= 0 && type <= 3) {
         Packets.send((ServerPlayer) entity, new PacketAchievement(Component.translatable(title), Component.translatable(msg), type, new CompoundTag()));
      } else {
         throw new CustomNPCsException("Wrong type value given " + type);
      }
   }

   public void finishQuest(int id) {
      Quest quest = QuestController.instance.quests.get(id);
      if (quest != null && entity instanceof ServerPlayer player && data.questData.finish(quest, player)) {
         Packets.send(player, new PacketAchievement(Component.translatable("quest.completed"), Component.translatable(quest.title), 2, new CompoundTag()));
         Packets.send(player, new PacketChat(Component.translatable("quest.completed").append(": ").append(Component.translatable(quest.title))));
      }
   }

   public void stopQuest(int id) {
      Quest quest = QuestController.instance.quests.get(id);
      if (quest != null) {
         PlayerData data = getData();
         data.questData.activeQuests.remove(id);
         data.updateClient = true;
      }
   }

   public void removeQuest(int id) {
      Quest quest = QuestController.instance.quests.get(id);
      if (quest != null) {
         PlayerData data = getData();
         data.questData.activeQuests.remove(id);
         data.questData.removeFinishedQuest(id);
         data.updateClient = true;
      }
   }

   public boolean hasReadDialog(int id) {
      PlayerDialogData data = getData().dialogData;
      return data.has(id);
   }

   public void showDialog(int id, String name) {
      Dialog dialog = DialogController.instance.dialogs.get(id);
      if (dialog == null) {
         throw new CustomNPCsException("Unknown Dialog id: " + id);
      }
      if (!dialog.availability.isAvailable(entity)) { return; }
      EntityDialogNpc npc = new EntityDialogNpc(getWorld().getMCLevel());
      npc.display.setName(name);
      EntityUtil.Copy(entity, npc);
      npc.dialogs = new int[] { id };
      NoppesUtilServer.openDialog(entity, npc, dialog);
   }

   public void addFactionPoints(int faction, int points) {
      PlayerData data = getData();
      data.factionData.increasePoints(entity, faction, points);
      data.updateClient = true;
   }

   public int getFactionPoints(int faction) {
      return getData().factionData.getFactionPoints(entity, faction);
   }

   public float getRotation() {
      return entity.getYRot();
   }

   public void setRotation(float rotation) {
      entity.setYRot(rotation);
   }

   public void message(String message) {
      entity.sendSystemMessage(Component.translatable(NoppesStringUtils.formatText(message, entity)));
   }

   public int getGamemode() {
      return ((ServerPlayer)entity).gameMode.getGameModeForPlayer().getId();
   }

   public void setGamemode(int type) {
      ((ServerPlayer)entity).setGameMode(GameType.byId(type));
   }

   public int inventoryItemCount(IItemStack item) {
      return inventoryItemCount(item.getMCItemStack());
   }

   private int inventoryItemCount(ItemStack stack) {
      int count = 0;
      for(int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
         ItemStack is = entity.getInventory().getItem(i);
         if (isItemEqual(stack, is)) {
            count += is.getCount();
         }
      }
      return count;
   }

   private boolean isItemEqual(ItemStack stack, ItemStack other) {
      if (other.isEmpty()) {
         return false;
      } else {
         return stack.getItem() == other.getItem();
      }
   }

   public int inventoryItemCount(String id) {
      Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
      if (item == null) {
         throw new CustomNPCsException("Unknown item id: " + id);
      } else {
         return inventoryItemCount(new ItemStack(item, 1));
      }
   }

   public IContainer getInventory() {
      if (inventory == null) {
         inventory = new ContainerWrapper(entity.getInventory());
      }
      return inventory;
   }

   public IItemStack getInventoryHeldItem() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.containerMenu.getCarried());
   }

   public boolean removeItem(IItemStack item, int amount) {
      int count = inventoryItemCount(item.getMCItemStack());
      if (amount > count) {
         return false;
      } else {
         if (count == amount) {
            removeAllItems(item);
         } else {
            for(int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
               ItemStack is = entity.getInventory().getItem(i);
               if (isItemEqual(item.getMCItemStack(), is)) {
                  if (amount < is.getCount()) {
                     is.split(amount);
                     break;
                  }
                  entity.getInventory().setItem(i, ItemStack.EMPTY);
                  amount -= is.getCount();
               }
            }
         }

         updatePlayerInventory();
         return true;
      }
   }

   public boolean removeItem(String id, int amount) {
      Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
      if (item == null) {
         throw new CustomNPCsException("Unknown item id: " + id);
      } else {
         return removeItem(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(item, 1)), amount);
      }
   }

   public boolean giveItem(IItemStack item) {
      ItemStack mcItem = item.getMCItemStack();
      if (mcItem.isEmpty()) {
         return false;
      } else {
         boolean bo = entity.getInventory().add(mcItem.copy());
         if (bo) {
            NoppesUtilServer.playSound(entity, SoundEvents.ITEM_PICKUP, 0.2F, ((entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            updatePlayerInventory();
         }
         return bo;
      }
   }

   public boolean giveItem(String id, int amount) {
      Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
      if (item == null) {
         return false;
      } else {
         ItemStack mcStack = new ItemStack(item);
         IItemStack itemStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(mcStack);
         itemStack.setStackSize(amount);
         return giveItem(itemStack);
      }
   }

   public void updatePlayerInventory() {
      entity.inventoryMenu.broadcastChanges();
      if (entity instanceof ServerPlayer player) {
         player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, player.getInventory().selected, player.getInventory().getItem(player.getInventory().selected)));
         PlayerQuestData playerdata = getData().questData;
         CustomNPCsScheduler.runTack(() -> {
            for (QuestData data : playerdata.activeQuests.values()) {
               for (QuestObjective obj : data.quest.getObjectives(entity)) {
                  if (obj.getEnumType() != EnumQuestTask.ITEM) { continue; }
                  playerdata.checkQuestCompletion(entity, data);
               }
            }
         });
      }
   }

   public IBlock getSpawnPoint() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIBlock(entity.level(), ((ServerPlayer)entity).getRespawnPosition());
   }

   public void setSpawnPoint(IBlock block) {
      setSpawnpoint(block.getX(), block.getY(), block.getZ());
   }

   public void setSpawnpoint(int x, int y, int z) {
      x = ValueUtil.correctInt(x, -30000000, 30000000);
      z = ValueUtil.correctInt(z, -30000000, 30000000);
      y = ValueUtil.correctInt(y, 0, 256);
      ((ServerPlayer)entity).setRespawnPosition(getWorld().getMCLevel().dimension(), new BlockPos(x, y, z), 0.0F, true, false);
   }

   public void resetSpawnpoint() {
      ((ServerPlayer)entity).setRespawnPosition(getWorld().getMCLevel().dimension(), null, 0.0F, true, false);
   }

   public void removeAllItems(IItemStack item) {
      for(int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
         ItemStack is = entity.getInventory().getItem(i);
         if (ItemStack.isSameItem(is, item.getMCItemStack())) {
            entity.getInventory().setItem(i, ItemStack.EMPTY);
         }
      }

   }

   public boolean hasAdvancement(String achievement) {
      Advancement advancement = Objects.requireNonNull(entity.getServer()).getAdvancements().getAdvancement(Objects.requireNonNull(ResourceLocation.tryParse(achievement)));
      if (advancement == null) {
         throw new CustomNPCsException("Advancement doesnt exist");
      } else {
         AdvancementProgress progress = entity.getServer().getPlayerList().getPlayerAdvancements((ServerPlayer)entity).getOrStartProgress(advancement);
         return progress.isDone();
      }
   }

   public int getExpLevel() {
      return entity.experienceLevel;
   }

   public void setExpLevel(int level) {
      entity.giveExperienceLevels(level - entity.experienceLevel);
   }

   public void setPosition(double x, double y, double z) {
      if (!(entity instanceof ServerPlayer)) { return; }
      SPacketDimensionTeleport.teleportPlayer((ServerPlayer) entity, entity.level().dimension(), x, y, z, entity.getYRot(), entity.getXRot());
   }

   public void setPos(IPos pos) {
      setPosition(pos.getX(), pos.getY(), pos.getZ());
   }

   public int getType() {
      return 1;
   }

   public boolean typeOf(int type) {
      return type == 1 || super.typeOf(type);
   }

   @SuppressWarnings("unchecked")
   public boolean hasPermission(String permission) {
      for (PermissionNode<?> node : PermissionAPI.getRegisteredNodes()) {
         if (node.getNodeName().equals(permission)) {
            try {
               return CustomNpcsPermissions.hasPermission((ServerPlayer)entity, (PermissionNode<Boolean>) node);
            } catch (Throwable ignored) {
               break;
            }
         }
      }
      return false;
   }

   public IPixelmonPlayerData getPixelmonData() {
      if (!PixelmonHelper.Enabled) {
         throw new CustomNPCsException("Pixelmon is not installed");
      } else {
         return new IPixelmonPlayerData() {
            public Object getParty() {
               if (pixelmonPartyStorage == null) {
                  pixelmonPartyStorage = PixelmonHelper.getParty(entity);
               }
               return pixelmonPartyStorage;
            }

            public Object getPC() {
               if (pixelmonPCStorage == null) {
                  pixelmonPCStorage = PixelmonHelper.getPc(entity);
               }
               return pixelmonPCStorage;
            }
         };
      }
   }

   private PlayerData getData() {
      if (data == null) { data = PlayerData.get(entity); }
      return data;
   }

   public ITimers getTimers() {
      return getData().timers;
   }

   public void removeDialog(int id) {
      PlayerData data = getData();
      data.dialogData.dialogsRead.remove(id);
      data.updateClient = true;
   }

   public void addDialog(int id) {
      PlayerData data = getData();
      data.dialogData.read(id);
      data.updateClient = true;
   }

   public void closeGui() {
      entity.closeContainer();
      Packets.send((ServerPlayer)entity, new PacketGuiClose(new CompoundTag()));
   }

   public int factionStatus(int factionId) {
      Faction faction = FactionController.instance.getFaction(factionId);
      if (faction == null) { throw new CustomNPCsException("Unknown faction: " + factionId); }
      return faction.playerStatus(this);
   }

   public void kick(String message) {
      ((ServerPlayer)entity).connection.disconnect(Component.translatable(message));
   }

   public boolean canQuestBeAccepted(int questId) {
      return PlayerQuestController.canQuestBeAccepted(entity, questId);
   }

   public void showCustomGui(ICustomGui gui) {
      NoppesUtilServer.openContainerGui((ServerPlayer)getMCEntity(), EnumGuiType.CustomGui, (buf) -> buf.writeNbt(((CustomGuiWrapper)gui).toNBT()));
      ((ContainerCustomGui) getMCEntity().containerMenu).setGui((CustomGuiWrapper)gui, entity);
   }

   public ICustomGui getCustomGui() {
      return entity.containerMenu instanceof ContainerCustomGui ? ((ContainerCustomGui) entity.containerMenu).customGui : null;
   }

   public void clearData() {
      PlayerData data = getData();
      data.setNBT(new CompoundTag());
      data.save(true);
   }

   public IContainer getOpenContainer() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIContainer(entity.containerMenu);
   }

   public void playSound(String sound, float volume, float pitch) {
      Packets.send((ServerPlayer) entity, new PacketPlaySound(sound, SoundSource.PLAYERS, entity.getX(), entity.getY(), entity.getZ(), volume, pitch));
   }

   public void playMusic(String sound, boolean background, boolean loops) {
      Packets.send((ServerPlayer)entity, new PacketPlayMusic(new ResourceLocation(NoppesUtilServer.validLocation(sound)), !background, loops));
   }

   public void sendMail(IPlayerMail mail) {
      PlayerDataController.instance.addPlayerMessage(entity.getServer(), entity.getName().getString(), (PlayerMail) mail);
   }

   public void trigger(int id, Object... arguments) {
      EventHooks.onScriptTriggerEvent(PlayerData.get(entity).scriptData, id, getWorld(), getPos(), null, arguments);
   }

   // New from Unofficial (Goodbird)
   @Override
   public void showOverlay(IOverlay overlay) {
      data.overlay.add(overlay.getId());
      Packets.send((ServerPlayer)entity, new PacketOverlayShow(overlay.save().getMCNBT()));
   }

   @Override
   public void showSoundSelectionGUI() { Packets.send((ServerPlayer) entity, new PacketSoundGUIOpen()); }

   @Override
   public void hideOverlay(int id) {
      data.overlay.remove(id);
      Packets.send((ServerPlayer)entity, new PacketOverlayHide(id));
   }

   @Override
   public void hideAllOverlays() {
      data.overlay.clearOverlays();
      Packets.send((ServerPlayer)entity, new PacketHideAllOverlays());
   }

   @Override
   public IPlayerSkin getSkin() { return PlayerSkinController.getInstance().getData(entity.getUUID(), 0); }

   @Override
   public IPlayerSkin getSkin(int type) {
      return PlayerSkinController.getInstance().getData(entity.getUUID(), type);
   }

   // New from Unofficial (BetaZavr)
   @Override
   public void addMoney(long value) {
      getData().game.addMoney(value);
   }

   @Override
   public void cameraShakingPlay(int time, int amplitude, int type, boolean isFading) {
      if (time <= 1 || time > 1200) {
         throw new CustomNPCsException("Camera shake time should be between 1 and 1200 ticks. You have: " + time);
      }
      if (amplitude <= 1 || amplitude > 25) {
         throw new CustomNPCsException("Amplitude should be between 1 and 25 value. You have: " + amplitude);
      }
      if (type < 0 || type > 5) {
         throw new CustomNPCsException("Type should be between 0 and 5 value. You have: " + type);
      }
      //Server.sendData((EntityPlayerMP) entity, EnumPacketClient.PLAY_CAMERA_SHAKING, time, amplitude, type, isFading);
   }

   @Override
   public void cameraShakingStop() {
      //Server.sendData((EntityPlayerMP) entity, EnumPacketClient.STOP_CAMERA_SHAKING);
   }

   @Override
   public void completeQuest(int id) {
      Quest quest = QuestController.instance.quests.get(id);
      if (quest == null) { return; }
      PlayerData data = getData();
      data.questData.finish(quest, entity);
      data.questData.activeQuests.remove(id);
      if (this.entity instanceof ServerPlayer player) {
         Packets.send(player, new PacketAchievement(Component.translatable("quest.completed"), Component.translatable(quest.title), 2, new CompoundTag()));
         Packets.send(player, new PacketChat(Component.translatable("quest.completed").append(": ").append(Component.translatable(quest.title))));
         QuestData qData = new QuestData(quest);
         if (data.questData.activeQuests.containsKey(id)) { qData = data.questData.activeQuests.get(id); }
         quest.complete(player, qData);
      }
   }

   @Override
   public IContainer getBubblesInventory() {
      IContainer invBubbles = null;
      try {
         Class<?> apiBubbles = Class.forName("baubles.api.BaublesApi");
         for (Method m : apiBubbles.getDeclaredMethods()) {
            if (m.getName().equals("getBaubles")) {
               m.trySetAccessible();
               invBubbles = new ContainerWrapper((Container) m.invoke(apiBubbles, entity));
               break;
            }
         }
      }
      catch (Exception e) {
         throw new CustomNPCsException("Mod \"Bubbles\" - not found");
      }
      return invBubbles;
   }

   @Override
   public int[] getKeyPressed() {
      return getData().overlay.getKeyPressed();
   }

   @Override
   public String getLanguage() {
      return CustomNpcs.proxy.getLanguage(entity);
   }

   @Override
   public long getMoney() {
      return getData().game.getMoney();
   }

   @Override
   public int[] getMousePressed() {
      return getData().overlay.getMousePressed();
   }

   @Override
   public String getSkinType(int type) {
      return PlayerSkinController.getInstance().get((ServerPlayer) entity, type);
   }

   @Override
   public IScreenSize getWindowSize() {
      return data.overlay.getWindowSize();
   }

   @Override
   public boolean hasMousePress(int key) {
      return getData().overlay.hasMousePress(key);
   }

   @Override
   public boolean hasOrKeyPressed(int[] key) {
      return getData().overlay.hasOrKeysPressed(key);
   }

   @Override
   public int inventoryItemCount(IItemStack stack, boolean ignoreDamage, boolean ignoreNBT) {
      return Util.instance.inventoryItemCount(entity, stack.getMCItemStack(), null, ignoreDamage, ignoreNBT);
   }

   @Override
   public int inventoryItemCount(String id, int amount) {
      Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
      if (item == null) {
         throw new CustomNPCsException("Unknown item id: " + id);
      }
      return inventoryItemCount(new ItemStack(item, 1));
   }

   @Override
   public boolean isCompleteQuest(int id) {
      PlayerQuestData data = getData().questData;
      if (data.hasFinishedQuest(id)) { return true; }
      if (!data.activeQuests.containsKey(id)) { return false; }
      QuestData qData = data.activeQuests.get(id);
      if (qData.isCompleted) { return true; }
      Quest quest = (Quest) Objects.requireNonNull(NpcAPI.Instance()).getQuests().get(id);
      return quest.questInterface.isCompleted(getMCEntity());
   }

   @Override
   public boolean isMoved() { return getData().overlay.isMoved; }

   @Override
   public void playSound(int category, IPos pos, String sound, float volume, float pitch) {
      Packets.send((ServerPlayer) entity, new PacketPlaySound(sound, SoundSource.PLAYERS, pos.getX(), pos.getY(), pos.getZ(), volume, pitch));
   }

   @Override
   public void sendTo(INbt nbt) {
      CustomNPCsScheduler.runTack(() -> {
         if (entity instanceof ServerPlayer) { Packets.send((ServerPlayer) entity, new PacketCustomNBT(nbt.getMCNBT())); }
         else { Packets.sendServer(new SPacketCustomNBT(nbt.getMCNBT())); }
      }, 10);
   }

   @Override
   public void setMoney(long value) {
      getData().game.setMoney(value);
   }

   @Override
   public void setSkin(int type, int gender, int body, int bodyColor, int hair, int hairColor, int face, int eyesColor, int leg, int jacket, int shoes, int... peculiarities) {
      PlayerSkinController.getInstance().set(entity.getUUID().toString(), type, gender, body, bodyColor, hair, hairColor, face, eyesColor, leg, jacket, shoes, peculiarities);
   }

   @Override
   public void setSkinType(int type, String location) {
      PlayerSkinController.getInstance().set(entity.getUUID().toString(), location, type);
   }

   @Override
   public void stopSound(int category, String sound) {
      if (!(entity instanceof ServerPlayer)) {
         MusicController.Instance.stopSound(new ResourceLocation(NoppesUtilServer.validLocation(sound)),
                 SoundSource.values()[ValueUtil.onlyPositiveInt(category, SoundSource.values().length)]);
         return;
      }
      if (sound == null) { sound = ""; }
      if (category < 0) { category = -1; }
      Packets.send((ServerPlayer) entity, new PacketStopSound(new ResourceLocation(sound), category));
   }

   @Override
   public IEntity<?> getRidingEntity() {
      if (entity.getVehicle() == null) { return null; }
      return Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getVehicle());
   }

   @Override
   public IEntity<?> getLookingEntity() {
      Entity target = Util.instance.getLookEntity(entity, null, true);
      return target == null ? null : Objects.requireNonNull(NpcAPI.Instance()).getIEntity(target);
   }

   @Override
   public IBlock getLookingBlock() {
      IRayTrace rt = rayTraceBlock(data.game.blockReachDistance, false, false);
      if (rt.getBlock() == null) { return null; }
      return rt.getBlock();
   }

   @Override
   public double getBlockReachDistance() {
      return data.game.blockReachDistance;
   }

   @Override
   public void showMarket(int marcetId) {
      Marcet market = MarcetController.getInstance().getMarcet(marcetId);
      if (market != null && entity instanceof ServerPlayer player) {
         NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerTrader, (buffer) -> buffer.writeInt(marcetId));
      }
   }

   @Override
   public IScreenSize getScreenSize() {
      PlayerData data = PlayerData.get(entity);
      return data.overlay.screenSize;
   }

   @Override
   public void showBank(int bankId) {
      Bank bank = BankController.getInstance().getBank(bankId);
      if (bank != null && entity instanceof ServerPlayer sPlayer) {
         Packets.send(sPlayer, new PacketBankClearPos());
         data.bankData.get(bankId).openToPlayer(sPlayer, 0, 0, 0, 1);
      }
   }

}
