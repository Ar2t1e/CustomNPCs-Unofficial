package noppes.npcs.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IKeySetting;
import noppes.npcs.controllers.KeyController;
import noppes.npcs.controllers.data.KeyConfig;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   bus = Bus.MOD,
   modid = CustomNpcs.MODID,
   value = {Dist.CLIENT}
)
public class CustomKeyBinds {

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   @SuppressWarnings("all")
   public static void registerKeys(RegisterKeyMappingsEvent event) {
      CustomNpcs.debugData.start("Mod");
      ClientProxy.QuestLog = new KeyMapping("Quest Log", 76, "key.categories.gameplay");
      if (CustomNpcs.SceneButtonsEnabled) {
         ClientProxy.Scene1 = new KeyMapping("Scene1 start/pause", 321, "key.categories.gameplay");
         ClientProxy.Scene2 = new KeyMapping("Scene2 start/pause", 322, "key.categories.gameplay");
         ClientProxy.Scene3 = new KeyMapping("Scene3 start/pause", 323, "key.categories.gameplay");
         ClientProxy.SceneReset = new KeyMapping("Scene reset", 320, "key.categories.gameplay");
         event.register(ClientProxy.Scene1);
         event.register(ClientProxy.Scene2);
         event.register(ClientProxy.Scene3);
         event.register(ClientProxy.SceneReset);
      }
      event.register(ClientProxy.QuestLog);
      for (IKeySetting ks : KeyController.getInstance().getKeySettings()) {
         event.register((KeyMapping) ((KeyConfig) ks).getMCKeyBinding());
      }
      CustomNpcs.debugData.end("Mod");
   }
}
