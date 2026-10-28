package moe.plushie.armourers_workshop.api.common.capability;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.player.EntityPlayerMP;

@SuppressWarnings("unused")
public interface IWardrobeCap {

	ISkinDye getDye();

	IExtraColours getExtraColours();

	ISkinnableEntity getSkinnableEntity();

	void sendUpdateToServer();

	/**
	 * Syncs capability data to all players tracking the entity.
	 */
    void syncToAllTracking();

	/**
	 * Syncs capability data to a player.
	 * 
	 * @param entityPlayer
	 *            Player to sync to.
	 */
    void syncToPlayer(EntityPlayerMP entityPlayer);

}
