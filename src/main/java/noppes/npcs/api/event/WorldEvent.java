package noppes.npcs.api.event;

import net.minecraftforge.event.TickEvent;
import noppes.npcs.api.interfaces.EventFunction;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.constants.EnumScriptType;

public class WorldEvent extends CustomNPCsEvent {

   public final IWorld world;

   public WorldEvent(IWorld world) { this.world = world; }

   @EventName(EnumScriptType.SCRIPT_TRIGGER)
   public static class ScriptTriggerEvent extends WorldEvent {
      public final Object[] arguments;
      public final IPos pos;
      public final IEntity<?> entity;
      public final int id;

      public ScriptTriggerEvent(int id, IWorld level, IPos pos, IEntity<?> entity, Object[] arguments) {
         super(level);
         this.id = id;
         this.arguments = arguments;
         this.pos = pos;
         this.entity = entity;
      }
   }

   // New from Unofficial (BetaZavr)
   @EventName(EnumScriptType.SCRIPT_COMMAND)
   public static class ScriptCommandEvent extends WorldEvent {
      public String[] arguments;
      public IPos pos;

      public ScriptCommandEvent(IWorld world, IPos pos, String[] arguments) {
         super(world);
         this.arguments = arguments;
         this.pos = pos;
      }
   }

   @EventFunction("worldtick")
   public static class ServerTickEvent extends WorldEvent {

      public TickEvent.ServerTickEvent event;

      public ServerTickEvent(TickEvent.ServerTickEvent event) {
         super(null);
         this.event = event;
      }

   }

}
