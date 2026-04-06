package noppes.npcs.packets.client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.level.pathfinder.IPathMixin;
import noppes.npcs.shared.common.PacketBasic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PacketNpcNavigation extends PacketBasic {

    protected static int channelId;
    private final int entityId;
    private final Path path;

    public PacketNpcNavigation(int entityIdIn, Path pathIn) {
        entityId = entityIdIn;
        path = pathIn;
    }

    public static void encode(PacketNpcNavigation msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.path != null);
        if (msg.path != null) {
            IPathMixin pt = (IPathMixin) msg.path;
            buf.writeBoolean(pt.getReached());
            buf.writeInt(pt.getNextNodeIndex());
            Set<Target> targetNodes = pt.getTargetNodes();
            buf.writeInt(targetNodes == null ? 0 : pt.getTargetNodes().size());
            if (targetNodes != null) { targetNodes.forEach((target) -> target.writeToStream(buf)); }
            buf.writeInt(pt.getTarget().getX());
            buf.writeInt(pt.getTarget().getY());
            buf.writeInt(pt.getTarget().getZ());
            // Nodes
            buf.writeInt(pt.getNodes().size());
            for(Node node : pt.getNodes()) { node.writeToStream(buf); }
            // OpenSet
            buf.writeInt(pt.getOpenSet().length);
            for(Node node : pt.getOpenSet()) { node.writeToStream(buf); }
            // ClosedSet
            buf.writeInt(pt.getClosedSet().length);
            for(Node node : pt.getClosedSet()) { node.writeToStream(buf); }
        }
    }

    public static PacketNpcNavigation decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        Path path = null;
        if (buf.readBoolean()) {
            path = new Path(new ArrayList<>(), BlockPos.ZERO, buf.readBoolean());
            IPathMixin pt = (IPathMixin) path;
            pt.setNextNodeIndex(buf.readInt());
            int size = buf.readInt();
            if (size > 0) {
                Set<Target> targetNodes = new HashSet<>();
                for(int i = 0; i < size; ++i) { targetNodes.add(Target.createFromStream(buf)); }
                pt.setTargetNodes(targetNodes);
            }
            pt.setTarget(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
            // Nodes
            pt.getNodes().clear();
            size = buf.readInt();
            for(int i = 0; i < size; ++i) { pt.getNodes().add(Node.createFromStream(buf)); }
            // OpenSet
            Node[] openSets = new Node[buf.readInt()];
            for(int i = 0; i < openSets.length; ++i) { openSets[i] = Node.createFromStream(buf); }
            pt.setOpenSet(openSets);
            // ClosedSet
            Node[] closedSet = new Node[buf.readInt()];
            for(int i = 0; i < closedSet.length; ++i) { closedSet[i] = Node.createFromStream(buf); }
            pt.setOpenSet(closedSet);
        }
        return new PacketNpcNavigation(id, path);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(entityId);
        if (entity instanceof EntityNPCInterface npc) {
            npc.navigating = path;
            if (path == null) { npc.getNavigation().stop(); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}