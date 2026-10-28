package moe.plushie.armourers_workshop.api.common.skin.type;

import java.util.ArrayList;

/**
 * Skin type registry is used to register new ISkinType's and get register
 * ISkinType's and ISkinPart's.
 * 
 * @author RiskyKen
 */
@SuppressWarnings("unused")
public interface ISkinTypeRegistry {

	ArrayList<ISkinType> getRegisteredSkinTypes();

	ISkinPartType getSkinPartFromRegistryName(String registryName);

	ISkinType getSkinTypeFromRegistryName(String registryName);

	/**
	 * Register a new skin type.
	 */
    boolean registerSkin(ISkinType skinType);

}
