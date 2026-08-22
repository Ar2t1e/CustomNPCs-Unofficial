package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class CustomTeleporter extends Teleporter {

	public CustomTeleporter(WorldServer world) {
		super(world != null ? world : CustomNpcs.Server.getWorld(0));
	}

	@Override
	public void placeInPortal(@Nonnull Entity entityIn, float rotationYaw) {}

}
