package noppes.npcs.util;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import noppes.npcs.api.IPos;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.api.wrapper.WorldWrapper;
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
    public Player player = null;
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

    public int[] getDirections(Player player) { // startX, startY, startZ
        int[] d = new int[] { 0, 0, 0, 0, 0, 0 };
        if (player == null) { return d; }
        int vertical = player.getXRot() < -45 ? 1 : player.getXRot() > 45 ? 2 : 0;
        switch (player.getDirection()) {
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
                        } // away
                    }
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
                        }// center
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

    public CompoundTag getNbt() {
        CompoundTag nbtData = new CompoundTag();
        nbtData.putInt("BuilderType", type);
        nbtData.putInt("BuilderFasing", facing);
        nbtData.putIntArray("Region", region);
        nbtData.putInt("ID", id);
        nbtData.putBoolean("AddAir", addAir);
        nbtData.putBoolean("ReplaceAir", replaceAir);
        nbtData.putBoolean("IsSolid", isSolid);
        CompoundTag sch = new CompoundTag();
        sch.putString("FileName", schematicName);
        ListTag selectMap = new ListTag();
        for (BlockPos pos : schMap.values()) {
            selectMap.add(new IntArrayTag(new int[] { pos.getX(), pos.getY(), pos.getZ() }));
        }
        sch.put("SelectMap", selectMap);
        nbtData.put("Schematic", sch);
        ListTag chList = new ListTag();
        for (int slot : chances.keySet()) {
            CompoundTag c = new CompoundTag();
            c.putInt("Slot", slot);
            c.putInt("Value", chances.get(slot));
            chList.add(c);
        }
        nbtData.put("Chances", chList);
        if (type < 3) { nbtData.put("Inventory", inv.save()); }
        nbtData.putString("PlayerName", player == null ? "null" : player.getName().getString());
        return nbtData;
    }

    public void read(CompoundTag nbtData) {
        schema = null;
        if (nbtData.contains("BuilderType", 3)) { type = nbtData.getInt("BuilderType"); }
        if (nbtData.contains("BuilderFasing", 3)) { facing = nbtData.getInt("BuilderFasing"); }
        if (nbtData.contains("Region", 11)) { region = nbtData.getIntArray("Region"); }
        if (nbtData.contains("ID", 8)) { id = nbtData.getInt("ID"); }
        if (nbtData.contains("AddAir", 1)) { addAir = nbtData.getBoolean("AddAir"); }
        if (nbtData.contains("ReplaceAir", 1)) { replaceAir = nbtData.getBoolean("ReplaceAir"); }
        if (nbtData.contains("IsSolid", 1)) { isSolid = nbtData.getBoolean("IsSolid"); }
        if (nbtData.contains("Schematic", 10)) {
            CompoundTag sch = nbtData.getCompound("Schematic");
            if (sch.contains("FileName", 8)) { schematicName = sch.getString("FileName"); }
            if (sch.contains("SelectMap", 9)) {
                schMap.clear();
                for (int i = 0; i < sch.getList("SelectMap", 11).size(); i++) {
                    int[] pos = sch.getList("SelectMap", 11).getIntArray(i);
                    schMap.put(i, new BlockPos(pos[0], pos[1], pos[2]));
                }
            }
        }
        if (nbtData.contains("Chances", 9)) {
            chances.clear();
            for (int i = 0; i < nbtData.getList("Chances", 10).size(); i++) {
                CompoundTag c = nbtData.getList("Chances", 10).getCompound(i);
                chances.put(c.getInt("Slot"), c.getInt("Value"));
            }
        }
        if (nbtData.contains("Inventory", 10)) { inv.load(nbtData.getCompound("Inventory")); }
    }

    public void redo() {
        if (doPos < 0) { doPos = 0; }
        if (!doMap.containsKey(doPos + 1)) { return; }
        List<SchematicBlockData> listB = new ArrayList<>();
        List<Entity> listE = new ArrayList<>();
        // Get Zone
        int mx = Integer.MAX_VALUE, my = Integer.MAX_VALUE, mz = Integer.MAX_VALUE;
        int nx = Integer.MIN_VALUE, ny = Integer.MIN_VALUE, nz = Integer.MIN_VALUE;
        Level level = player != null ? player.level() : null;
        for (SchematicBlockData bd : doMap.get(doPos + 1)) {
            if (level == null && bd.level != null) { level = bd.level; }
            if (mx > bd.pos.getX()) { mx = bd.pos.getX(); }
            if (nx < bd.pos.getX()) { nx = bd.pos.getX(); }
            if (my > bd.pos.getY()) { my = bd.pos.getY(); }
            if (ny < bd.pos.getY()) { ny = bd.pos.getY(); }
            if (mz > bd.pos.getZ()) { mz = bd.pos.getZ(); }
            if (nz < bd.pos.getZ()) { nz = bd.pos.getZ(); }
        }
        // remove Entity
        if (level != null) {
            try {
                for (Entity e : level.getEntitiesOfClass(Entity.class,
                        new AABB(mx - 0.5d, my - 0.5d, mz - 0.5d,
                                nx + 0.5d, ny + 1.5d, nz + 1.5d),
                        (entity) -> !(entity instanceof Projectile || entity instanceof Arrow || entity instanceof Player))) {
                    listE.add(e);
                    e.discard();
                }
            }
            catch (Exception ignored) { }
        }
        // Set Blocks
        for (SchematicBlockData bd : doMap.get(doPos + 1)) {
            listB.add(new SchematicBlockData(bd.level, bd.level.getBlockState(bd.pos), bd.pos));
            bd.set(bd.pos);
        }
        // Spawn Entities
        if (level != null) {
            for (Entity entity : enMap.get(doPos + 1)) {
                UUID uuid = entity.getUUID();
                List<Entity> entities = WorldWrapper.createNew(level).getEntities(Entity.class, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                while (uuid != null) {
                    boolean has = false;
                    for (Entity e : entities) {
                        if (e.getUUID().equals(entity.getUUID())) {
                            uuid = UUID.randomUUID();
                            entity.setUUID(uuid);
                            has = true;
                            break;
                        }
                    }
                    if (has) { continue; }
                    uuid = null;
                }
                CompoundTag tag = new CompoundTag();
                entity.save(tag);
                Entity newEntity = EntityType.loadEntityRecursive(tag, level, (e) -> {
                    e.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                    return e;
                });
                if (newEntity != null) { level.addFreshEntity(newEntity); }
            }
        }
        enMap.put(doPos + 1, listE);
        doMap.put(doPos + 1, listB);
        if (player != null) {player.sendSystemMessage(Component.translatable("builder.end.redo", "" + (doPos + 2), "" + listB.size())); }
        doPos++;
    }

    public void saveBlocks(ServerPlayer player, BlockPos pos, int size) { // Schematic Save
        if (schematicName.isEmpty()) {
            sendMessage("builder.err.file.name");
            return;
        }
        if (schMap.size() != 3) {
            String x = "" + pos.getX();
            String y = "" + pos.getY();
            String z = "" + pos.getZ();
            String dimId = player.level().dimension().location().toString();
            switch (schMap.size()) {
                case 1: {
                    schMap.put(1, pos);
                    player.sendSystemMessage(Component.translatable("builder.set.point.1", x, y, z, dimId, schematicName));
                    break;
                }
                case 2: {
                    BlockPos p = schMap.get(1);
                    if (p.equals(pos)) {
                        return;
                    }
                    player.sendSystemMessage(Component.translatable("builder.set.point.2", x, y, z, dimId, schematicName));
                    schMap.put(2, pos);
                    break;
                }
                default: {
                    player.sendSystemMessage(Component.translatable("builder.set.point.0", x, y, z, dimId, schematicName));
                    schMap.put(0, pos);
                }
            }
            lastWork = System.currentTimeMillis();
            Packets.send(player, new PacketSyncUpdate(id, 7, getNbt()));
            return;
        }
        lastWork = System.currentTimeMillis() - size;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() instanceof ISpecBuilder) {
                BuilderData builder = ItemBuilder.getBuilder(player.getInventory().getItem(i), player);
                if (builder != null) { builder.schema = null; }
            }
        }
        schema = new SchematicWrapper(Schematic.create(player.level(), player.getDirection(), schematicName + ".schematic", schMap));
        Packets.send(player, new PacketSaveSchematic(schema.schema.getNBT()));
    }

    public void sendMessage(String text, Object... obj) {
        if (player != null && lastMessage + 1000 <= System.currentTimeMillis()) {
            lastMessage = System.currentTimeMillis();
            player.sendSystemMessage(Component.translatable(text, obj));
        }
    }

    public void setBlocks(Player player, BlockPos pos) { // Del
        int[] d = getDirections(player);
        int cx = 0, cy = 0, cz = 0;
        int size = region[0] * region[1] * region[2];
        List<SchematicBlockData> listB = new ArrayList<>();
        List<Entity> listE = new ArrayList<>();
        // remove Entity
        List<Entity> list = new ArrayList<>();
        try {
            list = player.level().getEntitiesOfClass(Entity.class,
                    new AABB(d[0] - 0.25d, d[1] - 0.25d, d[2] - 0.25d,
                            d[3] + 0.25d, d[4] + 0.25d, d[5] + 0.25d).move(pos),
                    (entity) -> !(entity instanceof Projectile || entity instanceof Arrow || entity instanceof Player));
        }
        catch (Exception ignored) { }
        for (Entity e : list) {
            listE.add(e);
            e.discard();
        }
        // Create block data to work
        Map<Integer, SchematicBlockData> tempBlocks = new HashMap<>();
        SchematicBlockData main = null;
        if (type != 0) {
            int total = 0, mPos = -1, max = -1;
            Map<Integer, Integer> bls = new HashMap<>(); // [slot, chance]
            if (!inv.getItem(0).isEmpty()) { main = new SchematicBlockData(player.level(), inv.getItem(0)); }
            for (int i = 1; i < 10; i++) {
                ItemStack stack = inv.getItem(i);
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
                if (slot >= 10) { bd = new SchematicBlockData(player.level(), new ItemStack(Blocks.AIR)); } // Air
                else { bd = new SchematicBlockData(player.level(), inv.getItem(slot)); }
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
                ItemStack stack = inv.getItem(i);
                if (!stack.isEmpty()) { tempBlocks.put(i, new SchematicBlockData(player.level(), stack)); }
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
                    BlockState state = player.level().getBlockState(p);
                    cx++;
                    sum++;
                    if (type == 0) {
                        if (state.getBlock() == Blocks.AIR) { continue; }
                        if (!tempBlocks.isEmpty()) {
                            for (SchematicBlockData bd : tempBlocks.values()) {
                                if (bd.state.getBlock() == state.getBlock()) {
                                    listB.add(new SchematicBlockData(player.level(), state, p));
                                    player.level().setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                                    break;
                                }
                            }
                        } else {
                            listB.add(new SchematicBlockData(player.level(), state, p));
                            player.level().setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    } // delete
                    else if (type == 1) {
                        SchematicBlockData bd = tempBlocks.get(sum - 1);
                        listB.add(new SchematicBlockData(player.level(), state, p));
                        bd.pos = new BlockPos(p);
                        bd.level = player.level();
                        bd.set(bd.pos);
                    } // set
                    else if (type == 2) { // replace
                        if (!replaceAir && state.getBlock() == Blocks.AIR) { continue; }
                        if (main != null && !main.state.getBlock().equals(state.getBlock())) { continue; }
                        if (!tempBlocks.isEmpty()) {
                            SchematicBlockData bd = tempBlocks.get(rnd.nextInt(tempBlocks.size()));
                            listB.add(new SchematicBlockData(player.level(), state, p));
                            bd.pos = new BlockPos(p);
                            bd.level = player.level();
                            try {
                                if (state.getBlock() instanceof SlabBlock) {
                                    bd.state.setValue(SlabBlock.TYPE, state.getValue(SlabBlock.TYPE));
                                }
                            } catch (Exception e) { LogWriter.error(e); }
                            bd.set(bd.pos);
                        } else {
                            SchematicBlockData bd = new SchematicBlockData(player.level(), ItemStack.EMPTY);
                            listB.add(new SchematicBlockData(player.level(), state, p));
                            bd.pos = new BlockPos(p);
                            bd.level = player.level();
                            bd.set(bd.pos);
                        }
                    }
                }
                cz++;
                cx = 0;
            }
            cy++;
            cz = 0;
        }
        sendMessage("builder.end.work." + !listB.isEmpty(), "" + listB.size(), "0");
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
        Level level = player != null ? player.level() : null;
        for (SchematicBlockData bd : doMap.get(doPos)) {
            if (level == null && bd.level != null) { level = bd.level; }
            if (mx > bd.pos.getX()) { mx = bd.pos.getX(); }
            if (nx < bd.pos.getX()) { nx = bd.pos.getX(); }
            if (my > bd.pos.getY()) { my = bd.pos.getY(); }
            if (ny < bd.pos.getY()) { ny = bd.pos.getY(); }
            if (mz > bd.pos.getZ()) { mz = bd.pos.getZ(); }
            if (nz < bd.pos.getZ()) { nz = bd.pos.getZ(); }
        }
        // remove Entity
        if (level != null) {
            try {
                for (Entity e : level.getEntitiesOfClass(Entity.class,
                        new AABB(mx - 0.5d, my - 0.5d, mz - 0.5d,
                                nx + 0.5d, ny + 1.5d, nz + 1.5d),
                        (entity) -> !(entity instanceof Projectile || entity instanceof Arrow || entity instanceof Player))) {
                    listE.add(e);
                    e.discard();
                }
            }
            catch (Exception ignored) { }
        }
        // Set Blocks
        for (SchematicBlockData bd : doMap.get(doPos)) {
            listB.add(new SchematicBlockData(bd.level, bd.level.getBlockState(bd.pos), bd.pos));
            bd.set(bd.pos);
        }
        // Spawn Entities
        if (level != null) {
            for (Entity entity : enMap.get(doPos)) {
                UUID uuid = entity.getUUID();
                List<Entity> entities = WorldWrapper.createNew(level).getEntities(Entity.class, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                while (uuid != null) {
                    boolean has = false;
                    for (Entity e : entities) {
                        if (e.getUUID().equals(entity.getUUID())) {
                            uuid = UUID.randomUUID();
                            entity.setUUID(uuid);
                            has = true;
                            break;
                        }
                    }
                    if (has) { continue; }
                    uuid = null;
                }
                CompoundTag tag = new CompoundTag();
                entity.save(tag);
                Entity newEntity = EntityType.loadEntityRecursive(tag, level, (e) -> {
                    e.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                    return e;
                });
                if (newEntity != null) { level.addFreshEntity(newEntity); }
            }
        }
        enMap.put(doPos, listE);
        doMap.put(doPos, listB);
        doPos--;
        if (player != null) { player.sendSystemMessage(Component.translatable("builder.end.undo", "" + (doPos + 1), "" + listB.size())); }
    }

    public void work(BlockPos pos, ServerPlayer playerIn) {
        player = playerIn;
        int size = region[0] * region[1] * region[2];
        if (size > 2000) { size = 2000; }
        size = (int) (0.875d * (double) size + 250.0d);
        if (lastWork + size > System.currentTimeMillis()) {
            sendMessage("builder.wait", Util.instance.ticksToElapsedTime(lastWork + size - System.currentTimeMillis(), true, true, false));
            return;
        }
        lastWork = System.currentTimeMillis();
        if (type == 3 && playerIn != null) {
            lastWork = System.currentTimeMillis() - size;
            if (schema != null) {
                IPos trPos = ((BlockPosWrapper) schema.schema.getOffset()).rotate(playerIn.getDirection());
                int rot;
                switch (playerIn.getDirection()) {
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
                schema.init(pos.offset(trPos.getMCBlockPos()), player.level(), rot * 90);
                SchematicController.buildBlocks(playerIn, pos, schema);
            }
        }
        else if (type == 4) { saveBlocks(playerIn, pos, size); }
        else { setBlocks(player, pos); }
    }

    public int getID() { return id; }

    /**
     * @return 0: remover; 1: builder; 2: replacer; 3: placer; 4: saver
     */
    public int getType() { return type; }

}
