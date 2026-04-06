package noppes.npcs.api.event;

import net.minecraftforge.eventbus.api.Cancelable;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.constants.EnumScriptType;

public class ItemEvent extends CustomNPCsEvent {

   public IItemScripted item;

   public ItemEvent(IItemScripted item) {
      this.item = item;
   }

   @Cancelable
   @EventName(EnumScriptType.ATTACK)
   public static class AttackEvent extends ItemEvent {
      public final int type;
      public final Object target;
      public IPlayer<?> player;
      public final IDamageSource damageSource;

      public AttackEvent(IItemScripted item, IPlayer<?> player, int type, Object target) {
         super(item);
         this.type = type;
         this.target = target;
         this.player = player;
         this.damageSource = null;
      }

      public AttackEvent(IItemScripted item, IPlayer<?> player, IEntity<?> target, IDamageSource damageSource) {
         super(item);
         this.type = 1;
         this.target = target;
         this.player = player;
         this.damageSource = damageSource;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.INTERACT)
   public static class InteractEvent extends ItemEvent {
      public final int type;
      public final Object target;
      public IPlayer<?> player;

      public InteractEvent(IItemScripted item, IPlayer<?> player, int type, Object target) {
         super(item);
         this.type = type;
         this.target = target;
         this.player = player;
      }
   }

   @EventName(EnumScriptType.PICKEDUP)
   public static class PickedUpEvent extends ItemEvent {
      public IEntityItem<?> entity;
      public IPlayer<?> player;

      public PickedUpEvent(IItemScripted item, IPlayer<?> player, IEntityItem<?> entity) {
         super(item);
         this.entity = entity;
         this.player = player;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.TOSSED)
   public static class TossedEvent extends ItemEvent {
      public IEntityItem<?> entity;
      public IPlayer<?> player;

      public TossedEvent(IItemScripted item, IPlayer<?> player, IEntityItem<?> entity) {
         super(item);
         this.entity = entity;
         this.player = player;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.SPAWN)
   public static class SpawnEvent extends ItemEvent {
      public IEntityItem<?> entity;

      public SpawnEvent(IItemScripted item, IEntityItem<?> entity) {
         super(item);
         this.entity = entity;
      }
   }

   @EventName(EnumScriptType.TICK)
   public static class UpdateEvent extends ItemEvent {
      public IPlayer<?> player;

      public UpdateEvent(IItemScripted item, IPlayer<?> player) {
         super(item);
         this.player = player;
      }
   }

   @EventName(EnumScriptType.INIT)
   public static class InitEvent extends ItemEvent {
      public InitEvent(IItemScripted item) {
         super(item);
      }
   }

}
