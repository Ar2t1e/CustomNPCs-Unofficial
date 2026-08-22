package noppes.npcs.api.event;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.ai.CombatHandler;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.entity.data.ILine;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumScriptType;

public class NpcEvent extends CustomNPCsEvent {

	public ICustomNpc<?> npc;

	public NpcEvent(ICustomNpc<?> npcIn) {
		super();
		npc = npcIn;
	}

	@EventName(EnumScriptType.TIMER)
	public static class TimerEvent extends NpcEvent {
		public int id;

		public TimerEvent(ICustomNpc<?> npc, int idIn) {
			super(npc);
			id = idIn;
		}
	}

	@EventName(EnumScriptType.COLLIDE)
	public static class CollideEvent extends NpcEvent {
		public IEntity<?> entity;

		public CollideEvent(ICustomNpc<?> npc, Entity entityIn) {
			super(npc);
			entity = API.getIEntity(entityIn);
		}
	}

	@Cancelable
	@EventName(EnumScriptType.NEED_BLOCK_DAMAGED)
	public static class NeedBlockDamage extends NpcEvent {

		public IDamageSource damageSource;
		public boolean isBlocked;
		public int type;

		public NeedBlockDamage(ICustomNpc<?> npc, DamageSource damagesource, boolean isBlockedIn, int typeIn) {
			super(npc);
			damageSource = API.getIDamageSource(damagesource);
			isBlocked = isBlockedIn;
			type = typeIn;
		}
	}

	@Cancelable
	@EventName(EnumScriptType.DAMAGED)
	public static class DamagedEvent extends NpcEvent {
		public boolean clearTarget;
		public float damage;
		public IDamageSource damageSource;
		public IEntity<?> source;

		public DamagedEvent(ICustomNpc<?> npc, Entity sourceIn, float damageIn, DamageSource damageSourceIn) {
			super(npc);
			clearTarget = false;
			source = API.getIEntity(sourceIn);
			damage = damageIn;
			damageSource = API.getIDamageSource(damageSourceIn);
		}
	}

	@EventName(EnumScriptType.RANGED_LAUNCHED)
	public static class RangedLaunchedEvent extends NpcEvent {

		public float damage;
		public List<IProjectile<?>> projectiles = new ArrayList<>();
		public IEntityLivingBase<?> target;

		public RangedLaunchedEvent(ICustomNpc<?> npc, EntityLivingBase targetIn, float damageIn) {
			super(npc);
			target = (IEntityLivingBase<?>) API.getIEntity(targetIn);
			damage = damageIn;
		}
	}

	@Cancelable
	@EventName(EnumScriptType.ATTACK_MELEE)
	public static class MeleeAttackEvent extends NpcEvent {
		public float damage;
		public IEntityLivingBase<?> target;

		public MeleeAttackEvent(ICustomNpc<?> npc, EntityLivingBase targetIn, float damageIn) {
			super(npc);
			target = (IEntityLivingBase<?>) API.getIEntity(targetIn);
			damage = damageIn;
		}
	}

	@EventName(EnumScriptType.KILL)
	public static class KilledEntityEvent extends NpcEvent {
		public IEntityLivingBase<?> entity;

		public KilledEntityEvent(ICustomNpc<?> npc, EntityLivingBase entityIn) {
			super(npc);
			entity = (IEntityLivingBase<?>) API.getIEntity(entityIn);
		}
	}

	@EventName(EnumScriptType.DIED)
	public static class DiedEvent extends NpcEvent {

		public int expDropped = 0;
		public double totalDamage = 0.0d;
		public double totalDamageOnlyPlayers = 0.0d;
		public IDamageSource damageSource;
		public IItemStack[] droppedItems;
		public Map<IEntity<?>, List<IItemStack>> lootedItems;
		public Map<IEntity<?>, List<IItemStack>> inventoryItems;
		public ILine line;
		public IEntity<?> source;
		public String type;
		public final Map<IEntity<?>, Double> damageMap = new HashMap<>();

		public DiedEvent(ICustomNpc<?> npc, DamageSource damagesource, Entity entity, CombatHandler combatHandler) {
			super(npc);
			type = damagesource.damageType;
			source = API.getIEntity(entity);
			damageSource = API.getIDamageSource(damagesource);
			for (EntityLivingBase e : combatHandler.aggressors.keySet()) {
				double damage = combatHandler.aggressors.get(e);
				damageMap.put(API.getIEntity(e), damage);
				totalDamage += damage;
				if (e instanceof EntityPlayer) { totalDamageOnlyPlayers = damage; }
			}
		}
		
		public IEntity<?>[] getEntitys() { return damageMap.keySet().toArray(new IEntity<?>[0]); }

		@SuppressWarnings("unused")
		public double getDamageFromEntity(@ParamName("entity") IEntity<?> entity) {
			if (damageMap.containsKey(entity)) { return damageMap.get(entity); }
			else if (entity != null) {
				for (IEntity<?> ie : damageMap.keySet()) {
					if (entity.getMCEntity().equals(ie.getMCEntity())) {
						return damageMap.get(ie);
					}
				}
			}
			return 0.0d;
		}
		
	}

	@Cancelable
	@EventName(EnumScriptType.INTERACT)
	public static class InteractEvent extends NpcEvent {
		public IPlayer<?> player;

		public InteractEvent(ICustomNpc<?> npc, EntityPlayer playerIn) {
			super(npc);
			player = (IPlayer<?>) API.getIEntity(playerIn);
		}
	}

	@Cancelable
	@EventName(EnumScriptType.TARGET_LOST)
	public static class TargetLostEvent extends NpcEvent {
		public IEntityLivingBase<?> entity;

		public TargetLostEvent(ICustomNpc<?> npc, EntityLivingBase entityIn) {
			super(npc);
			entity = (IEntityLivingBase<?>) API.getIEntity(entityIn);
		}
	}

	@Cancelable
	@EventName(EnumScriptType.TARGET)
	public static class TargetEvent extends NpcEvent {
		public IEntityLivingBase<?> entity;

		public TargetEvent(ICustomNpc<?> npc, EntityLivingBase entityIn) {
			super(npc);
			entity = (IEntityLivingBase<?>) API.getIEntity(entityIn);
		}
	}

	@EventName(EnumScriptType.TICK)
	public static class UpdateEvent extends NpcEvent {
		public UpdateEvent(ICustomNpc<?> npc) { super(npc); }
	}

	@EventName(EnumScriptType.INIT)
	public static class InitEvent extends NpcEvent {
		public InitEvent(ICustomNpc<?> npc) { super(npc); }
	}

	// New from Unofficial (BetaZavr)
	@Cancelable
	@EventName(EnumScriptType.CUSTOM_TELEPORT)
	public static class CustomNpcTeleport extends NpcEvent {

		public IPos pos;
		public IPos portal;
		public int dimension;

		public CustomNpcTeleport(ICustomNpc<?> npc, IPos portalIn, IPos posIn, int dimensionIn) {
			super(npc);
			pos = posIn;
			portal = portalIn;
			dimension = dimensionIn;
		}

	}

}
