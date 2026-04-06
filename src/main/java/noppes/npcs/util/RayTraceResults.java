package noppes.npcs.util;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.util.IRayTraceResults;
import noppes.npcs.api.wrapper.EntityWrapper;
import noppes.npcs.api.wrapper.data.DataBlock;

public class RayTraceResults implements IRayTraceResults {

    public static RayTraceResults EMPTY = new RayTraceResults();

    private final Map<BlockPos, DataBlock> blocks = new HashMap<>();
    private List<Entity> entitys = new ArrayList<>();

    public void add(Entity entity, double distance, Vec3 vecStart, Vec3 vecEnd) {
        entitys = EntityWrapper.findEntityOnPath(entity, distance, vecStart, vecEnd);
    }

    public void add(Level level, BlockPos pos, BlockState state) {
        if (blocks.containsKey(pos)) { return; }
        blocks.put(pos, new DataBlock(level, pos, state));
    }

    @Override
    public IBlock[] getBlocks() {
        List<IBlock> data = new ArrayList<>();
        for (DataBlock db : blocks.values()) { data.add(db.getIBlock()); }
        return data.toArray(new IBlock[0]);
    }

    @Override
    public IEntity<?>[] getEntitys() {
        List<IEntity<?>> result = new ArrayList<>();
        for (Entity e : entitys) { result.add(Objects.requireNonNull(NpcAPI.Instance()).getIEntity(e)); }
        return result.toArray(new IEntity[0]);
    }

    @Override
    public void clear() {
        blocks.clear();
        entitys.clear();
    }

    @Override
    public List<DataBlock> getMCBlocks() { return new ArrayList<>(blocks.values()); }

    @Override
    public List<Entity> getMCEntitys() { return entitys; }

}
