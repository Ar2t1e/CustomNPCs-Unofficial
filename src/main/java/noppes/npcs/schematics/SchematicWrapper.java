package noppes.npcs.schematics;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.BlockBanner.BlockBannerStanding;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemBuilder;
import noppes.npcs.items.ItemPlacer;
import noppes.npcs.util.BuilderData;
import noppes.npcs.util.ValueUtil;

public class SchematicWrapper {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IBlockState rotationState(IBlockState state, int rotation) {
		if (rotation == 0) { return state; }
		Rotation rot;
		switch (rotation) {
			case 1: rot = Rotation.CLOCKWISE_90; break;
			case 2: rot = Rotation.CLOCKWISE_180; break;
			default: rot = Rotation.COUNTERCLOCKWISE_90; break;
		}
		if (state.getBlock() instanceof BlockVine ||
				state.getBlock() instanceof BlockBanner ||
				state.getBlock() instanceof BlockRailBase ||
				state.getBlock() instanceof BlockLever ||
				state.getBlock() instanceof BlockRotatedPillar) {
			return ((BlockVine) state.getBlock()).withRotation(state, rot);
		}
		for (IProperty property : state.getProperties().keySet()) {
			if (property.getValueClass() == EnumFacing.class) {
				EnumFacing d = (EnumFacing) state.getValue(property);
				if (d != EnumFacing.UP && d != EnumFacing.DOWN) {
					for (int i = 0; i < rotation; ++i) { d = d.rotateY(); }
					return state.withProperty(property, d);
				}
			}
		}
		return state;
	}

	// New from Unofficial (BetaZavr)
	public static Entity rotatePos(Entity entity, int rotation, BlockPos pos, BlockPos offset) {
		if (entity == null) {
			return null;
		}
		double x, y, z;
		if (entity instanceof EntityHanging) {
			EntityHanging eh = (EntityHanging) entity;
			x = eh.posX;
			y = eh.posY - offset.getY();
			z = eh.posZ;
			eh.rotationYaw = (eh.rotationYaw + (float) rotation * 90.0f) % 360.0f;
			switch (rotation) {
				case 1:
				case 2:
					x += offset.getX() * -1.0d;
					z += -1.0d - offset.getZ();
					break;
				case 3:
					x += 1.0d + offset.getX() * -1.0d;
					z += -1.0d - offset.getZ();
					break;
				default:
					x -= offset.getX();
					z -= offset.getZ();
					break;
			}
			x += pos.getX();
			y += pos.getY();
			z += pos.getZ();
			for (int i = 0; i < rotation; i++) {
				assert eh.facingDirection != null;
				eh.facingDirection = eh.facingDirection.rotateY();
			}
			entity.setPosition(x, y, z);
			return entity;
		}
		x = entity.posX;
		y = entity.posY;
		z = entity.posZ;
		switch (rotation) {
			case 1:
				x = 1.0d + offset.getZ() - entity.posZ;
				z = 1.0d + entity.posX + offset.getX() * -1.0d;
				break;
			case 2:
				x = 1.0d + entity.posX * -1.0d + offset.getX();
				z = 1.0d + entity.posZ * -1.0d + offset.getZ();
				break;
			case 3:
				x = 1.0d + entity.posZ - offset.getZ();
				z = 1.0d + entity.posX * -1.0d + offset.getX();
				break;
			default:
				x += 1.0d - offset.getX();
				z += 1.0d - offset.getZ();
				break;
		}
		entity.rotationYaw = (entity.rotationYaw + (float) rotation * 90.0f) % 360.0f;
		entity.posX = x + pos.getX() + 0.5d;
		entity.posY = y + pos.getY();
		entity.posZ = z + pos.getZ() + 0.5d;
		if (entity instanceof EntityCreature) {
			((EntityCreature) entity).setHomePosAndDistance(entity.getPosition(), (int) ((EntityCreature) entity).getMaximumHomeDistance());
		}
		if (entity instanceof EntityNPCInterface) {
			((EntityNPCInterface) entity).ais.orientation = (((EntityNPCInterface) entity).ais.orientation + rotation * 90) % 360;
		}
		return entity;
	}

	protected final TreeMap<Integer, HashMap<ChunkPos, NBTTagCompound>> tileEntities = new TreeMap<>();
	protected World world;
	public BlockPos start = BlockPos.ORIGIN;

	public ISchematic schema;
	public int buildPos;
	public int size;
	public int rotation = 0;
	public boolean isBuilding = false;

	// New from Unofficial (BetaZavr)
	protected List<SchematicBlockData> listB = new ArrayList<>();
	protected List<Entity> listE = new ArrayList<>();
	protected boolean isBlock = true;
	protected BuilderData builder = null;
	protected long time = 0L;
	public ICommandSender sender = null;
	public int buildingPercentage;
	public int layer = 0;

	public SchematicWrapper(ISchematic schematic) {
		schema = schematic;
		size = schematic.getWidth() * schematic.getHeight() * schematic.getLength();
		for (int i = 0; i < schematic.getTileEntitySize(); ++i) {
			NBTTagCompound teTag = schematic.getTileEntity(i);
			int x = teTag.getInteger("x");
			int y = teTag.getInteger("y");
			int z = teTag.getInteger("z");
			if (!tileEntities.containsKey(y)) { tileEntities.put(y, new HashMap<>()); }
			tileEntities.get(y).put(new ChunkPos(x, z), teTag);
		}
	}

	public void build() {
		if (world == null || !isBuilding) { return; }
		long endPos = ValueUtil.correctLong(buildPos + CustomNpcs.MaxBuilderBlocks, 0, size);
		// blocks first and next types
		if (layer < 2) {
			if (layer == 0 && builder != null) {
				listB = new ArrayList<>();
				listE = new ArrayList<>();
				BlockPos ps = start;
				BlockPos pe = start.add(rotation % 2 == 0 ? schema.getWidth() : schema.getLength(), schema.getHeight(), rotation % 2 == 0 ? schema.getLength() : schema.getWidth());
				List<Entity> list = new ArrayList<>();
				try {
					list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(ps.getX() - 0.5d,
							ps.getY() - 0.5d, ps.getZ() - 0.5d, pe.getX() + 0.5d, pe.getY() + 0.5d, pe.getZ() + 0.5d));
				}
				catch (Exception ignored) { }
				for (Entity e : list) {
					if (e instanceof EntityThrowable || e instanceof EntityArrow || e instanceof EntityPlayer) { continue; }
					listE.add(e);
					e.isDead = true;
				}
			} // remove Entity
			long t = System.currentTimeMillis();
			while (buildPos < endPos) {
				int x = buildPos % schema.getWidth();
				int z = (buildPos - x) / schema.getWidth() % schema.getLength();
				int y = ((buildPos - x) / schema.getWidth() - z) / schema.getLength();
				SchematicBlockData sbd = place(x, y, z, layer == 0);
				if (sbd != null) { listB.add(sbd); }
				++buildPos;
			}
			time += System.currentTimeMillis() - t;
		}
		if (buildPos >= size) {
			switch (layer) {
				case 0: {
					layer = 1;
					buildPos = 0;
					break;
				} // next blocks
				case 1: {
					if (schema.hasEntitys()) {
						NBTTagList list = schema.getEntitys();
						for (int i = 0; i < list.tagCount(); i++) { spawn(list.getCompoundTagAt(i)); }
					}
					layer = 2;
					SchematicController.time = time / ((long) schema.getHeight() * schema.getLength() * schema.getWidth());
					time = 0L;
					break;
				} // entitys
				default: {
					layer = 3;
					isBuilding = false;
					if (builder != null) { builder.add(listB, listE); }
				}
			}
		}
	}

	public NBTTagCompound getNBTSmall() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setShort("Width", schema.getWidth());
		compound.setShort("Height", schema.getHeight());
		compound.setShort("Length", schema.getLength());
		compound.setString("SchematicName", schema.getName());
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < size && i < 25000; ++i) {
			IBlockState state = schema.getBlockState(i);
			if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.STRUCTURE_VOID) { list.appendTag(new NBTTagCompound()); }
			else { list.appendTag(NBTUtil.writeBlockState(new NBTTagCompound(), schema.getBlockState(i))); }
		}
		compound.setTag("Data", list);
		return compound;
	}

	public int getPercentage() {
		double l = buildPos + (layer == 0 ? 0 : size);
		return (int) (l / size * 50.0);
	}

	public NBTTagCompound getTileEntity(int x, int y, int z, BlockPos pos) {
		if (y < tileEntities.size() && tileEntities.containsKey(y)) {
			NBTTagCompound compound = tileEntities.get(y).get(new ChunkPos(x, z));
			if (compound == null) { return null; }
			compound = compound.copy();
			compound.setInteger("x", pos.getX());
			compound.setInteger("y", pos.getY());
			compound.setInteger("z", pos.getZ());
			return compound;
		}
		return null;
	}

	public void init(BlockPos pos, World worldIn, int rotationIn) {
		start = pos;
		world = worldIn;
		rotation = rotationIn;
		isBuilding = true;
		buildingPercentage = 0;
		layer = 0;
		isBlock = true;
		time = 0L;
	}

	/**
	 * place block in world
	 * 
	 * @param x,y,z
	 *            - BlockPos
	 * @param firstLayer
	 *            - not Air and FullBlock, next vice versa
	 */
	public SchematicBlockData place(int x, int y, int z, boolean firstLayer) {
		IBlockState state = schema.getBlockState(x, y, z);
		if (state == null || (firstLayer && !state.isFullBlock() && state.getBlock() != Blocks.AIR)
				|| (!firstLayer && (state.isFullBlock() || state.getBlock() == Blocks.AIR))) {
			return null;
		}
		int rot = rotation / 90;
		BlockPos pos = start.add(rotatePos(x, y, z, rot));
		SchematicBlockData sbd = new SchematicBlockData(world, world.getBlockState(pos), pos);
		state = SchematicWrapper.rotationState(state, rot);
		if (builder != null) {
			if (state.getBlock() == Blocks.AIR && !builder.addAir) { return null; } // not place air
			if (sbd.state != null) {
				if (!builder.replaceAir && sbd.state.getBlock() != Blocks.AIR && sbd.state.getBlock().canSpawnInBlock()) { return null; } // not place solid
				@SuppressWarnings("deprecation")
				Material mat = sbd.state.getBlock().getMaterial(sbd.state);
				if (mat.isReplaceable() && builder.isSolid) { return null; } // not solid place
			}
		}
		world.setBlockState(pos, state, 2);
		if (state.getBlock() instanceof ITileEntityProvider) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null) {
				NBTTagCompound comp = getTileEntity(x, y, z, pos);
				if (comp != null) {
					if (rot != 0 && state.getBlock() instanceof BlockSkull && comp.hasKey("Rot", 1)) {
						byte d = comp.getByte("Rot");
						for (int i = 0; i < rot; ++i) { d += (byte) 4; }
						d %= (byte) 16;
						comp.setByte("Rot", d);
					}
					tile.readFromNBT(comp);
				}
			}
		}
		world.setBlockState(pos, state, 2);
		return sbd;
	}

	public BlockPos rotatePos(int x, int y, int z, int rotation) {
		switch (rotation) {
			case 1:
				return new BlockPos(schema.getLength() - z - 1, y, x);
			case 2:
				return new BlockPos(schema.getWidth() - x - 1, y, schema.getLength() - z - 1);
			case 3:
				return new BlockPos(z, y, schema.getWidth() - x - 1);
			default:
				return new BlockPos(x, y, z);
		}
	}

	public void setBuilder(ICommandSender senderIn) {
		sender = senderIn;
		isBuilding = true;
		buildingPercentage = 0;
		isBlock = false;
		if (sender instanceof EntityPlayer && ((EntityPlayer) sender).getHeldItemMainhand().getItem() instanceof ItemPlacer) {
			builder = ItemBuilder.getBuilder(((EntityPlayer) sender).getHeldItemMainhand(), (EntityPlayer) sender);
		}
	}

	public void spawn(NBTTagCompound entityNbt) {
		Entity entity = EntityList.createEntityFromNBT(entityNbt, this.world);
		if (entity != null) {
			UUID uuid = entity.getUniqueID();
			while (uuid != null) {
				boolean has = false;
				for (Entity e : world.loadedEntityList) {
					if (e.getUniqueID().equals(entity.getUniqueID())) {
						uuid = UUID.randomUUID();
						entity.setUniqueId(uuid);
						has = true;
						break;
					}
				}
				if (has) { continue; }
				uuid = null;
			}
			entity = SchematicWrapper.rotatePos(entity, rotation / 90, start, schema.getOffset().getMCBlockPos());
			world.spawnEntity(entity);
			if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).reset(50); }
		}

	}

}
