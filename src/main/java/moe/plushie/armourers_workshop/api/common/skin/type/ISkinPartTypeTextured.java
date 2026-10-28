package moe.plushie.armourers_workshop.api.common.skin.type;

import java.awt.Point;

import moe.plushie.armourers_workshop.api.common.IPoint3D;

@SuppressWarnings("unused")
public interface ISkinPartTypeTextured extends ISkinPartType {

	/** UV location of the models base texture. */
	Point getTextureBasePos();

	/** Size of the model the texture is used on. */
	IPoint3D getTextureModelSize();

	/** UV location of the models overlay texture. */
	Point getTextureOverlayPos();

	/** Location of the texture in skin storage. */
	Point getTextureSkinPos();

	/** Should this texture be mirrored? */
	boolean isTextureMirrored();

}
