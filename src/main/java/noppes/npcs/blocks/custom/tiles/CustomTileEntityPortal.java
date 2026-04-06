package noppes.npcs.blocks.custom.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntitySave;

import javax.annotation.Nonnull;

public class CustomTileEntityPortal extends TileEntityEndPortal {

	protected ResourceLocation SKY_TEXTURE;
	protected ResourceLocation PORTAL_TEXTURE;
	public int dimensionId = 100;
	public int homeDimensionId = 0;
	public int type = 3;
	public float speed = 800.0f;
	public float alpha = 0.5f;
	public BlockPos posTp = new BlockPos(0, -1, 0);
	public BlockPos posHomeTp = new BlockPos(0, -1, 0);

	public ResourceLocation getPortalTexture() {
		if (PORTAL_TEXTURE == null && world != null) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof CustomBlockPortal) {
				PORTAL_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/environment/custom_"
						+ ((CustomBlockPortal) state.getBlock()).getCustomName() + "_portal.png");
			}
		}
		return PORTAL_TEXTURE != null ? PORTAL_TEXTURE : new ResourceLocation("textures/entity/end_portal.png");
	}

	public ResourceLocation getSkyTexture() {
		if (SKY_TEXTURE == null && world != null) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof CustomBlockPortal) {
				SKY_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/environment/custom_"
						+ ((CustomBlockPortal) state.getBlock()).getCustomName() + "_sky.png");
			}
		}
		return SKY_TEXTURE != null ? SKY_TEXTURE : new ResourceLocation("textures/environment/end_sky.png");
	}

	public BlockPos getPosTp(boolean isHome) {
		BlockPos pos = null;
		WorldServer sLevel = null;
		MinecraftServer server = world != null ? world.getMinecraftServer() : CustomNpcs.Server;
		if (isHome) {
			if (DimensionManager.isDimensionRegistered(homeDimensionId)) {
				pos = new BlockPos(posHomeTp);
				if (server != null) { sLevel = server.getWorld(homeDimensionId); }
			}
		}
		else if (DimensionManager.isDimensionRegistered(dimensionId)) {
			pos = new BlockPos(posTp);
			if (server != null) { sLevel = server.getWorld(dimensionId); }
		}
		if (pos == null) { pos  = new BlockPos(0, -1, 0); }
		if (pos.getY() < 0 && sLevel != null) {
			if (sLevel.getSpawnCoordinate() != null) { pos = new BlockPos(sLevel.getSpawnCoordinate()); }
			else { pos = new BlockPos(sLevel.getSpawnPoint()); }
		}
		if (pos.getY() < 0) { pos.up(70 - pos.getY()); }
		return NoppesUtilServer.getSafeTpPos(sLevel, pos, 253, 1);
	}

	public void updateToClient() {
		if (world != null && !world.isRemote) {
			Packets.sendAll(new SPacketTileEntitySave(writeToNBT(new NBTTagCompound())));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderFace(@Nonnull EnumFacing facing) {
		if (type == 3 && world != null) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof CustomBlockPortal) { type = state.getBlock().getMetaFromState(state); }
		}
		switch (type) {
			case 1: return facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH;
			case 2: return facing == EnumFacing.WEST || facing == EnumFacing.EAST;
			default: return facing == EnumFacing.UP || facing == EnumFacing.DOWN;
		}
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (!compound.hasKey("DimensionID", 3)) {
			updateToClient();
			return;
		}
		dimensionId = compound.getInteger("DimensionID");
		homeDimensionId = compound.getInteger("HomeDimensionID");
		speed = compound.getFloat("SecondSpeed");
		if (compound.hasKey("HomePosition", 11)) {
			int[] p = compound.getIntArray("HomePosition");
			if (p.length >= 3) { posHomeTp = new BlockPos(p[0], p[1], p[2]); }
		}
		else { posHomeTp = BlockPos.fromLong(compound.getLong("HomePosition")); }
		if (compound.hasKey("TpPosition", 11)) {
			int[] p = compound.getIntArray("TpPosition");
			if (p.length >= 3) { posTp = new BlockPos(p[0], p[1], p[2]); }
		}
		else { posTp = BlockPos.fromLong(compound.getLong("TpPosition")); }
	}

	@Override
	public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("DimensionID", dimensionId);
		compound.setInteger("HomeDimensionID", homeDimensionId);
		compound.setFloat("SecondSpeed", speed);
		compound.setLong("HomePosition", posHomeTp.toLong());
		compound.setLong("TpPosition", posTp.toLong());
		return compound;
	}

}
