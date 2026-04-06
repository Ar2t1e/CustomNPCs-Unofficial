package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.constants.EnumScriptType;

public class CustomPotionEvent extends CustomNPCsEvent {

	public ICustomElement potion;

	public CustomPotionEvent(ICustomElement potionIn) {
		super();
		potion = potionIn;
	}

	@EventName(EnumScriptType.POTION_AFFECT)
	public static class AffectEntity extends CustomPotionEvent {

		public IEntity<?> source, indirectSource, entity;
		public int amplifier;
		public double health;

		public AffectEntity(ICustomElement potion, Entity sourceIn, Entity indirectSourceIn,
							EntityLivingBase entityIn, int amplifierIn, double healthIn) {
			super(potion);
			source = API != null && sourceIn != null ? API.getIEntity(sourceIn) : null;
			indirectSource = API != null && indirectSource != null ? API.getIEntity(indirectSourceIn) : null;
			entity = API != null && entityIn != null ? API.getIEntity(entityIn) : null;
			amplifier = amplifierIn;
			health = healthIn;
		}

	}

	@EventName(EnumScriptType.POTION_END)
	public static class EndEffect extends CustomPotionEvent {

		public IEntity<?> entity;
		public int amplifier;

		public EndEffect(ICustomElement potion, EntityLivingBase entityIn, int amplifierIn) {
			super(potion);
			entity = API != null && entityIn != null ? API.getIEntity(entityIn) : null;
			amplifier = amplifierIn;
		}

	}

	@EventName(EnumScriptType.POTION_IS_READY)
	public static class IsReadyEvent extends CustomPotionEvent {

		public boolean isReady;
		public int duration;
		public int amplifier;

		public IsReadyEvent(ICustomElement potion, boolean isReadyIn, int durationIn, int amplifierIn) {
			super(potion);
			isReady = isReadyIn;
			duration = durationIn;
			amplifier = amplifierIn;
		}

	}

	@EventName(EnumScriptType.POTION_PERFORM)
	public static class PerformEffect extends CustomPotionEvent {

		public IEntity<?> entity;
		public int amplifier;

		public PerformEffect(ICustomElement potion, EntityLivingBase entityIn, int amplifierIn) {
			super(potion);
			entity = API != null && entityIn != null ? API.getIEntity(entityIn) : null;
			amplifier = amplifierIn;
		}

	}

}
