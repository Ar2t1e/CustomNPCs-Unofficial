package noppes.npcs.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.Zone3D;

@Cancelable
public class ForgeEvent extends CustomNPCsEvent {

   public final Event event;

   public ForgeEvent(Event eventIn) { event = eventIn; }

   @Cancelable
   public static class LevelEvent extends ForgeEvent {
      public final IWorld world;

      public LevelEvent(net.minecraftforge.event.level.LevelEvent event, IWorld world) {
         super(event);
         this.world = world;
      }
   }

   @Cancelable
   public static class EntityEvent extends ForgeEvent {
      public final IEntity<?> entity;
      public EntityEvent(net.minecraftforge.event.entity.EntityEvent event, Entity entityIn) {
         super(event);
         entity = API != null && entityIn != null ? API.getIEntity(entityIn) : null;
      }
   }

   @EventName(EnumScriptType.INIT)
   public static class InitEvent extends ForgeEvent {
      public InitEvent() { super(null); }
   }

   // New from Unofficial (BetaZavr)
   @Cancelable
   @EventName(EnumScriptType.REGION_ENTER)
   public static class EnterToRegion extends CustomNPCsEvent {
      public final Entity entity;
      public final Zone3D region;
      public EnterToRegion(Entity entityIn, Zone3D zone) {
         super();
         entity = entityIn;
         region = zone;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.REGION_LEAVE)
   public static class LeaveRegion extends CustomNPCsEvent {
      public final Entity entity;
      public final Zone3D region;
      public LeaveRegion(Entity entityIn, Zone3D zone) {
         super();
         entity = entityIn;
         region = zone;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.PLAY_SOUND)
   public static class ClientSoundPlayEvent extends ClientSoundEvent {
      public ClientSoundPlayEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn, float volumeIn, float pitchIn, double milliSecondsIn, double totalSecondIn) {
         super(eventIn, playerIn, nameIn, resourceIn, posIn, volumeIn, pitchIn, milliSecondsIn, totalSecondIn);
      }
   }

   @Cancelable
   @EventName(EnumScriptType.SOUND_TICK_EVENT)
   public static class ClientSoundTickEvent extends ClientSoundEvent {
      public ClientSoundTickEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn, float volumeIn, float pitchIn, double milliSecondsIn, double totalSecondIn) {
         super(eventIn, playerIn, nameIn, resourceIn, posIn, volumeIn, pitchIn, milliSecondsIn, totalSecondIn);
      }
   }

   @EventName(EnumScriptType.STOP_SOUND)
   public static class ClientSoundStopEvent extends ClientSoundEvent {
      public ClientSoundStopEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn, float volumeIn, float pitchIn, double milliSecondsIn, double totalSecondIn) {
         super(eventIn, playerIn, nameIn, resourceIn, posIn, volumeIn, pitchIn, milliSecondsIn, totalSecondIn);
      }
   }

   private static class ClientSoundEvent extends ForgeEvent {

      public double currentTime;
      public double duration;
      public float volume;
      public float pitch;
      public String name;
      public String resource;
      public IPlayer<?> player;
      public IPos pos;

      public ClientSoundEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn,
                              float volumeIn, float pitchIn, double currentTimeIn, double durationIn) {
         super(eventIn);
         currentTime = currentTimeIn;
         duration = durationIn;
         name = nameIn;
         resource = resourceIn;
         volume = volumeIn;
         pitch = pitchIn;
         pos = posIn;
         player = playerIn;
      }

   }

}
