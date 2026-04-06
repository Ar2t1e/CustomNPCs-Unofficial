package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.wrapper.ContainerCustomChestWrapper;
import noppes.npcs.api.wrapper.ContainerWrapper;

import javax.annotation.Nonnull;

public class ContainerNpcInterface extends Container {

	public static IContainer getOrCreateIContainer(ContainerNpcInterface container) {
		if (container.scriptContainer != null) { return container.scriptContainer; }
		if (container instanceof ContainerCustomChest) { return container.scriptContainer = new ContainerCustomChestWrapper(container); }
		return container.scriptContainer = new ContainerWrapper(container);
	}

	public EntityPlayer player;
	private final int posX;
	private final int posZ;

	public IContainer scriptContainer;

	public ContainerNpcInterface(EntityPlayer playerIn) {
		player = playerIn;
		posX = MathHelper.floor(playerIn.posX);
		posZ = MathHelper.floor(playerIn.posZ);
		playerIn.motionX = 0.0;
		playerIn.motionZ = 0.0;
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player) {
		return !player.isDead && this.posX == MathHelper.floor(player.posX) && this.posZ == MathHelper.floor(player.posZ);
	}

}
