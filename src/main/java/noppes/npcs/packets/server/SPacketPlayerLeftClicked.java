package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerLeftClicked extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (CustomNpcs.EnableScripting && !ScriptController.Instance.languages.isEmpty()) {
         ItemStack item = player.getHeldItemMainhand();
         PlayerScriptData handler = PlayerData.get(player).scriptData;
         PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getIPlayer(), 0, null);
         EventHooks.onPlayerAttack(handler, ev);
         if (item.getItem() == CustomItems.scripter_item) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(item);
            ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getIPlayer(), 0, null);
            EventHooks.onScriptItemAttack(isw, eve);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
