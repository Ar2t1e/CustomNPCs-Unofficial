package noppes.npcs.api.event;

import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.constants.EnumAnimationStages;
import noppes.npcs.constants.EnumScriptType;

public class AnimationEvent extends CustomNPCsEvent {

	@EventName(EnumScriptType.ANIMATION_NEXT_FRAME)
	public static class NextFrameEvent extends AnimationEvent {
		public NextFrameEvent(EntityLivingBase entity, AnimationConfig anim, int fID, long tTicks, EnumAnimationStages stage) {
			super(entity, anim, EnumScriptType.ANIMATION_NEXT_FRAME, fID, tTicks, stage);
		}
	}

	@EventName(EnumScriptType.ANIMATION_PLAY)
	public static class StartEvent extends AnimationEvent {
		public StartEvent(EntityLivingBase entity, AnimationConfig anim, int fID, long tTicks, EnumAnimationStages stage) {
			super(entity, anim, EnumScriptType.ANIMATION_PLAY, fID, tTicks, stage);
		}
	}

	@EventName(EnumScriptType.ANIMATION_STOP)
	public static class StopEvent extends AnimationEvent {
		public StopEvent(EntityLivingBase entity, AnimationConfig anim, int fID, long tTicks, EnumAnimationStages stage) {
			super(entity, anim, EnumScriptType.ANIMATION_STOP, fID, tTicks, stage);
		}
	}

	@EventName(EnumScriptType.ANIMATION_TICK)
	public static class UpdateEvent extends AnimationEvent {
		public UpdateEvent(EntityLivingBase entity, AnimationConfig anim, int fID, long tTicks, EnumAnimationStages stage) {
			super(entity, anim, EnumScriptType.ANIMATION_TICK, fID, tTicks, stage);
		}
	}

	public IEntity<?> entity;
	public AnimationConfig animation;
	public EnumAnimationStages stage;
	public int frameId;
	public long ticks;
	public final String nameEvent;

	public AnimationEvent(EntityLivingBase entityIn, AnimationConfig anim, EnumScriptType name, int fID, long tTicks, EnumAnimationStages aStage) {
		super();
		entity = API != null && entityIn != null ? API.getIEntity(entityIn) : null;
		nameEvent = name.function;
		animation = anim;
		frameId = fID;
		ticks = tTicks;
		stage = aStage;
	}

}
