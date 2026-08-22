package noppes.npcs.api.event;

import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.wrapper.DamageSourceWrapper;
import noppes.npcs.constants.EnumScriptType;

public class ItemEvent extends CustomNPCsEvent {

	public IItemScripted item;

	public ItemEvent(IItemScripted itemIn) {
		super();
		item = itemIn;
	}

	@Cancelable
	@EventName(EnumScriptType.ATTACK)
	public static class AttackEvent extends ItemEvent {
		public final int type;
		public final Object target;
		public final IPlayer<?> player;
		public final IDamageSource damageSource;

		public AttackEvent(IItemScripted item, IPlayer<?> playerIn, int typeIn, Object targetIn) {
			super(item);
			type = typeIn;
			target = targetIn;
			player = playerIn;
			damageSource = null;
		}

		public AttackEvent(IItemScripted item, IPlayer<?> playerIn, IEntity<?> targetIn, DamageSource damageSourceIn) {
			super(item);
			type = 1;
			target = targetIn;
			player = playerIn;
			damageSource = new DamageSourceWrapper(damageSourceIn);
		}

	}

	@Cancelable
	@EventName(EnumScriptType.INTERACT)
	public static class InteractEvent extends ItemEvent {
		public IPlayer<?> player;
		public Object target;
		public int type;

		public InteractEvent(IItemScripted item, IPlayer<?> playerIn, int typeIn, Object targetIn) {
			super(item);
			type = typeIn;
			target = targetIn;
			player = playerIn;
		}
	}

	@EventName(EnumScriptType.PICKEDUP)
	public static class PickedUpEvent extends ItemEvent {
		public IEntityItem<?> entity;
		public IPlayer<?> player;

		public PickedUpEvent(IItemScripted item, IPlayer<?> playerIn, IEntityItem<?> entityIn) {
			super(item);
			player = playerIn;
			entity = entityIn;
		}
	}

	@Cancelable
	@EventName(EnumScriptType.TOSSED)
	public static class TossedEvent extends ItemEvent {
		public IEntityItem<?> entity;
		public IPlayer<?> player;

		public TossedEvent(IItemScripted item, IPlayer<?> playerIn, IEntityItem<?> entityIn) {
			super(item);
			player = playerIn;
			entity = entityIn;
		}
	}

	@Cancelable
	@EventName(EnumScriptType.SPAWN)
	public static class SpawnEvent extends ItemEvent {
		public IEntityItem<?> entity;

		public SpawnEvent(IItemScripted item, IEntityItem<?> entityIn) {
			super(item);
			entity = entityIn;
		}
	}

	@EventName(EnumScriptType.TICK)
	public static class UpdateEvent extends ItemEvent {
		public IPlayer<?> player;

		public UpdateEvent(IItemScripted item, IPlayer<?> playerIn) {
			super(item);
			player = playerIn;
		}
	}

	@EventName(EnumScriptType.INIT)
	public static class InitEvent extends ItemEvent {
		public InitEvent(IItemScripted item) { super(item); }
	}

}
