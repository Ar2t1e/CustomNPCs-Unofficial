package noppes.npcs;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.api.event.ForgeEvent;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.commons.lang3.StringUtils;

public class ForgeEventHandler {

   public static final Map<Class<?>, String> eventNames = new HashMap<>();
   public static final Map<Class<?>, String> clientEventNames = new HashMap<>();

   private Event lastSeenEvent;

   @SubscribeEvent
   public void forgeEntity(Event event) {
      if (CustomNpcs.EnableForgeScripting &&
              ScriptController.Instance.forgeScripts.isEnabled() &&
              (!ScriptController.Instance.forgeScripts.isClient() || ScriptController.Instance.clientScripts.isEnabled())) {
         if (lastSeenEvent != event) {
            CustomNpcs.debugData.start("Mod");
            lastSeenEvent = event;
            try { EventHooks.onForgeEvent(new ForgeEvent(event)); }
            catch (Throwable t) { LogWriter.error("Error in " + event.getClass().getName(), t); }
            CustomNpcs.debugData.end("Mod");
         }
      }
   }

   public static String getEventName(Class<?> c) {
      String eventName = c.getName();
      int i = eventName.lastIndexOf(".");
      return StringUtils.uncapitalize(eventName.substring(i + 1).replace("$", ""));
   }

}
