package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerOverlayData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerKeyPressed extends PacketServerBasic {

   protected static int channelId;
   private final int button;
   private final boolean ctrlDown;
   private final boolean shiftDown;
   private final boolean altDown;
   private final boolean metaDown;
   private final boolean pressed;
   private final String openGui;

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

   public static void encode(SPacketPlayerKeyPressed msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.button);
      buf.writeBoolean(msg.ctrlDown);
      buf.writeBoolean(msg.shiftDown);
      buf.writeBoolean(msg.altDown);
      buf.writeBoolean(msg.metaDown);
      buf.writeBoolean(msg.pressed);
      buf.writeUtf(msg.openGui == null ? "" : msg.openGui);
   }

   public static SPacketPlayerKeyPressed decode(FriendlyByteBuf buf) {
      return new SPacketPlayerKeyPressed(buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
              buf.readBoolean(), buf.readBoolean(), buf.readUtf());
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
