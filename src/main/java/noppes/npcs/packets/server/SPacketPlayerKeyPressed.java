package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerOverlayData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerKeyPressed extends PacketServerBasic {

   protected static int channelId;
   private int button;
   private boolean ctrlDown;
   private boolean shiftDown;
   private boolean altDown;
   private boolean metaDown;
   private boolean pressed;
   private String openGui;

   public SPacketPlayerKeyPressed() { }

   public SPacketPlayerKeyPressed(int buttonIn, boolean ctrlDownIn, boolean shiftDownIn, boolean altDownIn, boolean metaDownIn, boolean pressedIn, String openGuiIn) {
      button = buttonIn;
      ctrlDown = ctrlDownIn;
      shiftDown = shiftDownIn;
      altDown = altDownIn;
      metaDown = metaDownIn;
      pressed = pressedIn;
      openGui = openGuiIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(button);
      buf.writeBoolean(ctrlDown);
      buf.writeBoolean(shiftDown);
      buf.writeBoolean(altDown);
      buf.writeBoolean(metaDown);
      buf.writeBoolean(pressed);
      buf.writeUtf(openGui == null ? "" : openGui);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      button = buf.readInt();
      ctrlDown = buf.readBoolean();
      shiftDown = buf.readBoolean();
      altDown = buf.readBoolean();
      metaDown = buf.readBoolean();
      pressed = buf.readBoolean();
      openGui = buf.readUtf();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerOverlayData data = PlayerData.get(player).overlay;
      if (button < 0) {
         if (CustomNpcs.EnableScripting && !ScriptController.Instance.languages.isEmpty()) {
            for (int k : data.keyPress) {
               EventHooks.onPlayerKeyEvent(player, k, false, false, false, false, false, "");
            }
         }
         data.keyPress.clear();
         return;
      }
      if (pressed) { data.keyPress.add(button); }
      else if (data.hasOrKeysPressed(button)) { data.keyPress.remove((Integer) button); }
      if (CustomNpcs.EnableScripting && !ScriptController.Instance.languages.isEmpty()) {
         EventHooks.onPlayerKeyEvent(player, button, ctrlDown, shiftDown, altDown, metaDown, pressed, openGui);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
