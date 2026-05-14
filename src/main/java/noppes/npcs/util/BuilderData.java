package noppes.npcs.util;

import java.util.*;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.api.IPos;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.items.ItemBuilder;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSaveSchematic;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.schematics.Schematic;
import noppes.npcs.schematics.SchematicBlockData;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.common.util.LogWriter;

public class BuilderData {

	// General
	private final Random rnd = new Random();
	private int type; // 0:remover; 1:builder; 2:replace; 3:placer; 4:saver
	private int id;
	
	public int[] region = new int[] { 5, 2, 3 };
	public int facing = 0;
	public NpcMiscInventory inv = new NpcMiscInventory(10);
	public EntityPlayer player = null;
	public boolean addAir = false;
	public boolean replaceAir = false;
	public boolean isSolid = false;
	public Map<Integer, Integer> chances = new TreeMap<>();
	// Schematic
	public Map<Integer, BlockPos> schMap = new TreeMap<>();
	public String schematicName = "";
	public SchematicWrapper schema;
	// undo / redo
	public int doPos = 0;
	public Map<Integer, List<SchematicBlockData>> doMap = new TreeMap<>();
	public Map<Integer, List<Entity>> enMap = new TreeMap<>();
	// technical
	private long lastWork = 0L;
	private long lastMessage = 0L;

	public BuilderData(int idIn, int typeIn) {
		id = idIn;
		type = typeIn;
	}

	public void add(List<SchematicBlockData> listB, List<Entity> listE) {
		if (doPos == 9) {
			doMap.remove(0);
			enMap.remove(0);
			Map<Integer, List<SchematicBlockData>> db = new TreeMap<>();
			Map<Integer, List<Entity>> de = new TreeMap<>();
			for (int i = 0; i < 9; i++) {
				db.put(i, doMap.get(i + 1));
				de.put(i, enMap.get(i + 1));
			}
			doMap = db;
			enMap = de;
		}
		else {
			doPos++;
			if (doMap.containsKey(doPos + 1)) {
				for (int i = doPos + 1; doMap.containsKey(i); i++) {
					doMap.remove(i);
					enMap.remove(i);
				}
			}
		}
		doMap.put(doPos, listB);
		enMap.put(doPos, listE);
	}

	public int[] getDirections(EntityPlayer player) { // startX, startY, startZ
		int[] d = new int[] { 0, 0, 0, 0, 0, 0 };
		if (player == null) { return d; }
		int vertical = player.rotationPitch < -45 ? 1 : player.rotationPitch > 45 ? 2 : 0;
		switch (player.getHorizontalFacing()) {
			case SOUTH: {
				if (vertical == 1) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
						} // away
					}
				} // down
				else if (vertical == 2) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
						} // away
					}
				} // up
				else {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[3] = region[0];
							d[4] = region[2];
							d[5] = region[1];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * region[1] + 1;
							d[3] = region[0];
							d[4] = region[2];
							d[5] = region[1];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[2];
							d[5] = region[1];
						} // away
					}
				} // wall
				break;
			}
			case EAST: {
				if (vertical == 1) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
						} // away
					}
				} // down
				else if (vertical == 2) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
						} // away
					}
				} // up
				else {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[1];
							d[4] = region[2];
							d[5] = region[0];
							break;
						} // center
						case 2: {
							d[0] = -1 * region[1] + 1;
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[1];
							d[4] = region[2];
							d[5] = region[0];
							break;
						} // on yourself
						default: {
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[1];
							d[4] = region[2];
							d[5] = region[0];
						}
					} // away
				} // wall
				break;
			}
			case NORTH: {
				if (vertical == 1) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
						} // away
					}
				} // down
				else if (vertical == 2) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[1];
							d[5] = region[2];
						} // away
					}
				} // up
				else {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[3] = region[0];
							d[4] = region[2];
							d[5] = region[1];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[3] = region[0];
							d[4] = region[2];
							d[5] = region[1];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * region[1] + 1;
							d[3] = region[0];
							d[4] = region[2];
							d[5] = region[1];
						} // away
					}
				} // wall
				break;
			}
			case WEST: {
				if (vertical == 1) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
						} // away
					}
				} // down
				else if (vertical == 2) {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // center
						case 2: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
							break;
						} // on yourself
						default: {
							d[0] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[1] = -1 * region[1] + 1;
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[2];
							d[4] = region[1];
							d[5] = region[0];
						} // away
					}
				} // up
				else {
					switch (facing) {
						case 1: {
							d[0] = -1 * (int) Math.floor((double) region[1] / 2.0d);
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[1];
							d[4] = region[2];
							d[5] = region[0];
							break;
						} // center
						case 2: {
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[1];
							d[4] = region[2];
							d[5] = region[0];
							break;
						} // on yourself
						default: {
							d[0] = -1 * region[1] + 1;
							d[1] = -1 * (int) Math.floor((double) region[2] / 2.0d);
							d[2] = -1 * (int) Math.floor((double) region[0] / 2.0d);
							d[3] = region[1];
							d[4] = region[2];
							d[5] = region[0];
						} // away
					}
				} // wall
				break;
			}
			default: break;
		}
		return d;
	}

	public NBTTagCompound getNbt() {
		NBTTagCompound nbtData = new NBTTagCompound();
		nbtData.setInteger("BuilderType", type);
		nbtData.setInteger("BuilderFasing", facing);
		nbtData.setIntArray("Region", region);
		nbtData.setInteger("ID", id);
		nbtData.setBoolean("AddAir", addAir);
		nbtData.setBoolean("ReplaceAir", replaceAir);
		nbtData.setBoolean("IsSolid", isSolid);
		NBTTagCompound sch = new NBTTagCompound();
		sch.setString("FileName", schematicName);
		NBTTagList selectMap = new NBTTagList();
		for (BlockPos pos : schMap.values()) {
			selectMap.appendTag(new NBTTagIntArray(new int[] { pos.getX(), pos.getY(), pos.getZ() }));
		}
		sch.setTag("SelectMap", selectMap);
		nbtData.setTag("Schematic", sch);
		NBTTagList chList = new NBTTagList();
		for (int slot : chances.keySet()) {
			NBTTagCompound c = new NBTTagCompound();
			c.setInteger("Slot", slot);
			c.setInteger("Value", chances.get(slot));
			chList.appendTag(c);
		}
		nbtData.setTag("Chances", chList);
		if (type < 3) { nbtData.setTag("Inventory", inv.save()); }
		nbtData.setString("PlayerName", player == null ? "null" : player.getName());
		return nbtData;
	}

	public void read(NBTTagCompound nbtData) {
		if (nbtData.hasKey("BuilderType", 3)) { type = nbtData.getInteger("BuilderType"); }
		if (nbtData.hasKey("BuilderFasing", 3)) { facing = nbtData.getInteger("BuilderFasing"); }
		if (nbtData.hasKey("Region", 11)) { region = nbtData.getIntArray("Region"); }
		if (nbtData.hasKey("ID", 8)) { id = nbtData.getInteger("ID"); }
		if (nbtData.hasKey("AddAir", 1)) { addAir = nbtData.getBoolean("AddAir"); }
		if (nbtData.hasKey("ReplaceAir", 1)) { replaceAir = nbtData.getBoolean("ReplaceAir"); }
		if (nbtData.hasKey("IsSolid", 1)) { isSolid = nbtData.getBoolean("IsSolid"); }
		if (nbtData.hasKey("Schematic", 10)) {
			NBTTagCompound sch = nbtData.getCompoundTag("Schematic");
			if (sch.hasKey("FileName", 8)) { schematicName = sch.getString("FileName"); }
			if (sch.hasKey("SelectMap", 9)) {
				schMap.clear();
				for (int i = 0; i < sch.getTagList("SelectMap", 11).tagCount(); i++) {
					int[] pos = sch.getTagList("SelectMap", 11).getIntArrayAt(i);
					schMap.put(i, new BlockPos(pos[0], pos[1], pos[2]));
				}
			}
		}
		if (nbtData.hasKey("Chances", 9)) {
			chances.clear();
			for (int i = 0; i < nbtData.getTagList("Chances", 10).tagCount(); i++) {
				NBTTagCompound c = nbtData.getTagList("Chances", 10).getCompoundTagAt(i);
				chances.put(c.getInteger("Slot"), c.getInteger("Value"));
			}
		}
		if (nbtData.hasKey("Inventory", 10)) { inv.load(nbtData.getCompoundTag("Inventory")); }
	}

	public void redo() {
		if (doPos < 0) { doPos = 0; }
		if (!doMap.containsKey(doPos + 1)) { return; }
		List<SchematicBlockData> listB = new ArrayList<>();
		List<Entity> listE = new ArrayList<>();
		// Get Zone
		int mx = Integer.MAX_VALUE, my = Integer.MAX_VALUE, mz = Integer.MAX_VALUE;
		int nx = Integer.MIN_VALUE, ny = Integer.MIN_VALUE, nz = Integer.MIN_VALUE;
		World world = player != null ? player.world : null;
		for (SchematicBlockData bd : doMap.get(doPos + 1)) {
			if (world == null && bd.world != null) { world = bd.world; }
			if (mx > bd.pos.getX()) { mx = bd.pos.getX(); }
			if (nx < bd.pos.getX()) { nx = bd.pos.getX(); }
			if (my > bd.pos.getY()) { my = bd.pos.getY(); }
			if (ny < bd.pos.getY()) { ny = bd.pos.getY(); }
			if (mz > bd.pos.getZ()) { mz = bd.pos.getZ(); }
			if (nz < bd.pos.getZ()) { nz = bd.pos.getZ(); }
		}
		// remove Entity
		if (world != null) {
			List<Entity> list = new ArrayList<>();
			try {
				list = world.getEntitiesWithinAABB(Entity.class,
						new AxisAlignedBB(mx - 0.5d, my - 0.5d, mz - 0.5d, nx + 0.5d, ny + 1.5d, nz + 1.5d));
			}
			catch (Exception ignored) { }
			for (Entity e : list) {
				if (e instanceof EntityThrowable || e instanceof EntityArrow || e instanceof EntityPlayer) { continue; }
				listE.add(e);
				e.isDead = true;
			}
		}
		// Set Blocks
		for (SchematicBlockData bd : doMap.get(doPos + 1)) {
			listB.add(new SchematicBlockData(bd.world, bd.world.getBlockState(bd.pos), bd.pos));
			bd.set(bd.pos);
		}
		// Spawn Entities
		if (world != null) {
			for (Entity entity : enMap.get(doPos + 1)) {
				entity.isDead = false;
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
				world.spawnEntity(entity);
			}
		}
		enMap.put(doPos + 1, listE);
		doMap.put(doPos + 1, listB);
		if (player != null) { player.sendMessage(Component.translatable("builder.end.redo", "" + (doPos + 2), "" + listB.size())); }
		doPos++;
	}

	public void saveBlocks(EntityPlayerMP player, BlockPos pos, int size) { // Schematic Save
		if (schematicName.isEmpty()) {
			sendMessage("builder.err.file.name");
			return;
		}
		if (schMap.size() != 3) {
			String x = "" + pos.getX();
			String y = "" + pos.getY();
			String z = "" + pos.getZ();
			switch (schMap.size()) {
				case 1: {
					schMap.put(1, pos);
					player.sendMessage(Component.translatable("builder.set.point.1", x, y, z, schematicName));
					break;
				}
				case 2: {
					BlockPos p = schMap.get(1);
					if (p.equals(pos)) {
						return;
					}
					player.sendMessage(Component.translatable("builder.set.point.2", x, y, z, schematicName));
					schMap.put(2, pos);
					break;
				}
				default: {
					player.sendMessage(Component.translatable("builder.set.point.0", x, y, z, schematicName));
					schMap.put(0, pos);
				}
			}
			lastWork = System.currentTimeMillis();
			Packets.send(player, new PacketSyncUpdate(id, 7, getNbt()));
			return;
		}
		lastWork = System.currentTimeMillis() - size;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (player.inventory.getStackInSlot(i).getItem() instanceof ISpecBuilder) {
				BuilderData builder = ItemBuilder.getBuilder(player.inventory.getStackInSlot(i), player);
				if (builder != null) { builder.schema = null; }
			}
		}

		schema = new SchematicWrapper(Schematic.create(player.world, player.getHorizontalFacing(), schematicName + ".schematic", schMap));
		Packets.send(player, new PacketSaveSchematic(schema.schema.getNBT()));
	}

	public void sendMessage(String text, Object... obj) {
		if (player != null && lastMessage + 1000 <= System.currentTimeMillis()) {
			lastMessage = System.currentTimeMillis();
			player.sendMessage(Component.translatable(text, obj));
		}
	}

	public void setBlocks(EntityPlayer player, BlockPos pos) { // Del
		int[] d = getDirections(player);
		int cx = 0, cy = 0, cz = 0;
		int size = region[0] * region[1] * region[2];
		List<SchematicBlockData> listB = new ArrayList<>();
		List<Entity> listE = new ArrayList<>();
		// remove Entity
		List<Entity> list = new ArrayList<>();
		try {
			list = player.world.getEntitiesWithinAABB(Entity.class,
					new AxisAlignedBB(d[0] - 0.25d, d[1] - 0.25d, d[2] - 0.25d, d[3] + 0.25d, d[4] + 0.25d, d[5] + 0.25d).offset(pos));
		}
		catch (Exception ignored) { }
		for (Entity e : list) {
			if (e instanceof EntityThrowable || e instanceof EntityArrow || e instanceof EntityPlayer) { continue; }
			listE.add(e);
			e.isDead = true;
		}
		// Create block data to work
		Map<Integer, SchematicBlockData> tempBlocks = new HashMap<>();
		SchematicBlockData main = null;
		if (type != 0) {
			int total = 0, mPos = -1, max = -1;
			Map<Integer, Integer> bls = new HashMap<>(); // [slot, chance]
			if (!inv.getStackInSlot(0).isEmpty()) { main = new SchematicBlockData(player.world, inv.getStackInSlot(0)); }
			for (int i = 1; i < 10; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty()) {
					int c = 100;
					if (chances.containsKey(i)) { c = chances.get(i); }
					total += c;
					if (max < c) {
						max = c;
						mPos = i;
					}
					bls.put(i, c);
				}
			}
			if (addAir) {
				int airV = 100;
				if (!bls.isEmpty()) { airV = total / bls.size(); }
				total += airV;
				bls.put(mPos + 1, airV);
			}
			if (bls.isEmpty() && (type == 1 || type == 2)) {
				sendMessage("builder.err.not.blocks");
				return;
			}
			// now bls [slot, count block]
			int fix = 0;
			for (int slot : bls.keySet()) {
				int v = size * bls.get(slot) / total;
				fix += v;
				bls.put(slot, v);
			}
			if (fix < size && mPos >= 0) { bls.put(mPos, bls.get(mPos) + size - fix); }
			Map<Integer, SchematicBlockData> amount = new HashMap<>(); // [slot, block]
			List<Integer> slots = new ArrayList<>();
			for (int slot : bls.keySet()) {
				SchematicBlockData bd;
				if (slot >= 10) { bd = new SchematicBlockData(player.world, new ItemStack(Blocks.AIR)); } // Air
				else { bd = new SchematicBlockData(player.world, inv.getStackInSlot(slot)); }
				amount.put(slot, bd);
				slots.add(slot);
			}
			for (int i = 0; i < size; i++) {
				int slot = slots.get(rnd.nextInt(slots.size()));
				SchematicBlockData bd = amount.get(slot);
				bls.put(slot, bls.get(slot) - 1);
				if (bls.get(slot) <= 0) { slots.remove((Integer) slot); }
				tempBlocks.put(i, bd);
			}
		}
		else {
			for (int i = 1; i < 10; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty()) { tempBlocks.put(i, new SchematicBlockData(player.world, stack)); }
			}
		}
		if (tempBlocks.isEmpty() && type != 0) {
			sendMessage("builder.err.not.blocks");
			return;
		}
		int sum = 0;
		// Try set blocks
		while (cy < d[4]) {
			while (cz < d[5]) {
				while (cx < d[3]) {
					BlockPos p = new BlockPos(pos.getX() + d[0] + cx, pos.getY() + d[1] + cy, pos.getZ() + d[2] + cz);
					IBlockState state = player.world.getBlockState(p);
					cx++;
					sum++;
					if (type == 0) {
						if (state.getBlock() == Blocks.AIR) { continue; }
						if (!tempBlocks.isEmpty()) {
							for (SchematicBlockData bd : tempBlocks.values()) {
								if (bd.state.getBlock() == state.getBlock()) {
									listB.add(new SchematicBlockData(player.world, state, p));
									player.world.setBlockState(p, Blocks.AIR.getDefaultState());
									break;
								}
							}
						} else {
							listB.add(new SchematicBlockData(player.world, state, p));
							player.world.setBlockState(p, Blocks.AIR.getDefaultState());
						}
					} // delete
					else if (type == 1) {
						SchematicBlockData bd = tempBlocks.get(sum - 1);
						listB.add(new SchematicBlockData(player.world, state, p));
						bd.pos = new BlockPos(p);
						bd.world = player.world;
						bd.set(bd.pos);
					} // set
					else if (type == 2) {
						if (!replaceAir && state.getBlock() == Blocks.AIR) { continue; }
						if (main != null && !main.state.getBlock().equals(state.getBlock())) { continue; }
						if (!tempBlocks.isEmpty()) {
							SchematicBlockData bd = tempBlocks.get(rnd.nextInt(tempBlocks.size()));
							listB.add(new SchematicBlockData(player.world, state, p));
							bd.pos = new BlockPos(p);
							bd.world = player.world;
							try {
								if (state.getBlock() instanceof BlockSlab) {
									bd.state.withProperty(BlockSlab.HALF, state.getValue(BlockSlab.HALF));
								}
							} catch (Exception e) { LogWriter.error(e); }
							bd.set(bd.pos);
						} else {
							SchematicBlockData bd = new SchematicBlockData(player.world, ItemStack.EMPTY);
							listB.add(new SchematicBlockData(player.world, state, p));
							bd.pos = new BlockPos(p);
							bd.world = player.world;
							bd.set(bd.pos);
						}
					} // replace
				}
				cz++;
				cx = 0;
			}
			cy++;
			cz = 0;
		}
		sendMessage("builder.end.work." + (!listB.isEmpty()), "" + listB.size());
		if (!listB.isEmpty() || !listE.isEmpty()) { add(listB, listE); }
	}

	public void undo() {
		if (doPos > 9) { doPos = 9; }
		if (!doMap.containsKey(doPos)) { return; }
		List<SchematicBlockData> listB = new ArrayList<>();
		List<Entity> listE = new ArrayList<>();
		// Get Zone
		int mx = Integer.MAX_VALUE, my = Integer.MAX_VALUE, mz = Integer.MAX_VALUE;
		int nx = Integer.MIN_VALUE, ny = Integer.MIN_VALUE, nz = Integer.MIN_VALUE;
		World world = null;
		if (player != null) { world = player.world; }
		for (SchematicBlockData bd : doMap.get(doPos)) {
			if (world == null && bd.world != null) { world = bd.world; }
			if (mx > bd.pos.getX()) { mx = bd.pos.getX(); }
			if (nx < bd.pos.getX()) { nx = bd.pos.getX(); }
			if (my > bd.pos.getY()) { my = bd.pos.getY(); }
			if (ny < bd.pos.getY()) { ny = bd.pos.getY(); }
			if (mz > bd.pos.getZ()) { mz = bd.pos.getZ(); }
			if (nz < bd.pos.getZ()) { nz = bd.pos.getZ(); }
		}
		// remove Entity
		if (world != null) {
			List<Entity> list = new ArrayList<>();
			try {
				list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(mx - 0.5d, my - 0.5d, mz - 0.5d, nx + 0.5d, ny + 1.5d, nz + 1.5d));
			}
			catch (Exception ignored) { }
			for (Entity e : list) {
				if (e instanceof EntityThrowable || e instanceof EntityArrow || e instanceof EntityPlayer) {
					continue;
				}
				listE.add(e);
				e.isDead = true;
			}
		}
		// Set Blocks
		for (SchematicBlockData bd : doMap.get(doPos)) {
			listB.add(new SchematicBlockData(bd.world, bd.world.getBlockState(bd.pos), bd.pos));
			bd.set(bd.pos);
		}
		// Spawn Entities
		if (world != null) {
			for (Entity entity : enMap.get(doPos)) {
				entity.isDead = false;
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
				world.spawnEntity(entity);
			}
		}
		enMap.put(doPos, listE);
		doMap.put(doPos, listB);
		doPos--;
		if (player != null) { player.sendMessage(Component.translatable("builder.end.undo", "" + (doPos + 1), "" + listB.size())); }
	}

	public void work(BlockPos pos, EntityPlayerMP playerIn) {
		player = playerIn;
		int size = region[0] * region[1] * region[2];
		if (size > 2000) { size = 2000; }
		size = (int) (0.875d * (double) size + 250.0d);
		if (lastWork + size > System.currentTimeMillis()) {
			sendMessage("builder.wait", Util.instance.ticksToElapsedTime(lastWork + size - System.currentTimeMillis(), true, true, false));
			return;
		}
		lastWork = System.currentTimeMillis();
		if (type == 3) {
			lastWork = System.currentTimeMillis() - size;
			if (schema != null) {
				IPos trPos = ((BlockPosWrapper) schema.schema.getOffset()).rotate(playerIn.getHorizontalFacing());
				int rot;
				switch (playerIn.getHorizontalFacing()) {
					case NORTH: {
						trPos = trPos.offset(-1, 0, -schema.schema.getWidth());
						rot = 2;
						break;
					}
					case WEST: {
						trPos = trPos.offset(-schema.schema.getWidth(), 0, 0);
						rot = 1;
						break;
					}
					case EAST: {
						trPos = trPos.offset(0, 0, -1);
						rot = 3;
						break;
					}
					default: {
						rot = 0;
						break;
					}// SOUTH
				}
				schema.init(pos.add(trPos.getMCBlockPos()), player.world, rot * 90);
				SchematicController.buildBlocks(playerIn, pos, schema);
			}
		}
		else if (type == 4) { saveBlocks(playerIn, pos, size); }
		else { setBlocks(playerIn, pos); }
	}

	public int getID() { return id; }
	
	public int getType() { return type; }

}
