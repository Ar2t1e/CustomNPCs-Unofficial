package noppes.npcs.api.handler;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.IAnimation;
import noppes.npcs.api.entity.data.IEmotion;

public interface IAnimationHandler {

	IAnimation createNewAnim();

	IAnimation getAnimation(@ParamName("animationId") int animationId);

	IAnimation getAnimation(@ParamName("animationName") String animationName);

	IAnimation[] getAnimations();

	@SuppressWarnings("UnusedReturnValue")
	boolean removeAnimation(@ParamName("animationId") int animationId);

	@SuppressWarnings("unused")
	boolean removeAnimation(@ParamName("animationName") String animationName);

	@SuppressWarnings("unused")
	IEmotion createNewEmtn();

	IEmotion getEmotion(@ParamName("emotionId") int emotionId);

	@SuppressWarnings("unused")
	IEmotion getEmotion(@ParamName("emotionName") String emotionName);

	@SuppressWarnings("unused")
	IEmotion[] getEmotions();

	@SuppressWarnings("UnusedReturnValue")
	boolean removeEmotion(@ParamName("emotionId") int emotionId);

	@SuppressWarnings("unused")
	boolean removeEmotion(@ParamName("emotionName") String emotionName);

}
