package moe.plushie.armourers_workshop.api.common.skin.type;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author RiskyKen
 */
@SuppressWarnings("unused")
public interface ISkinType {

	/**
	 * Is this skin enabled?
	 * 
	 * @return Is enabled?
	 */
    boolean enabled();

	@SideOnly(Side.CLIENT)
	ResourceLocation getIcon();

	/**
	 * This only exists for backwards compatibility with old world saves. Just
	 * return getRegistryName().
	 * 
	 * @return name
	 */
	String getName();

	ArrayList<ISkinProperty<?>> getProperties();

	/**
	 * Gets the name this skin will be registered with. Armourer's Workshop uses the
	 * format armourers:skinName. Example armourers:head is the registry name of
	 * Armourer's Workshop head armour skin.
	 * 
	 * @return registryName
	 */
	String getRegistryName();

	ArrayList<ISkinPartType> getSkinParts();

	@SideOnly(Side.CLIENT)
	ResourceLocation getSlotIcon();

	/**
	 * If this skin is for vanilla armour return the slot id here, otherwise return
	 * -1.
	 * 
	 * @return slotId
	 */
	int getVanillaArmourSlotId();

	boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew);

	/**
	 * Should this skin be hidden from the user?
	 * 
	 * @return Is hidden?
	 */
	boolean isHidden();

	/**
	 * Should the helper check box be shown in the armourer and mini armourer.
	 */
	boolean showHelperCheckbox();

	/**
	 * Should the show skin overlay check box be shown in the armourer and mini
	 * armourer.
	 */
	boolean showSkinOverlayCheckbox();

}
