package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import noppes.npcs.client.Client;
import noppes.npcs.mixin.pathfinding.IPathMixin;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcNavigation extends PacketBasic {

    protected static int channelId;
    public int entityId;
    public Path path;

    public PacketNpcNavigation() { }

    public PacketNpcNavigation(int entityIdIn, Path pathIn) {
        entityId = entityIdIn;
        path = pathIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        path = null;
        if (buf.readBoolean()) {
            PacketBuffer pBuf = new PacketBuffer(buf);
            PathPoint[] points = new PathPoint[pBuf.readInt()];
            for (int i = 0; i < points.length; i++) { points[i] = readPathPoint(pBuf); }
            path = new Path(points);
            path.setCurrentPathIndex(pBuf.readInt());
            PathPoint[] openSet = new PathPoint[pBuf.readInt()];
            for (int i = 0; i < openSet.length; i++) { openSet[i] = readPathPoint(pBuf); }
            ((IPathMixin) path).setOpenSet(openSet);
            PathPoint[] closedSet = new PathPoint[pBuf.readInt()];
            for (int i = 0; i < closedSet.length; i++) { closedSet[i] = readPathPoint(pBuf); }
            ((IPathMixin) path).setClosedSet(closedSet);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(path != null);
        if (path != null) {
            PathPoint[] points = ((IPathMixin) path).getPoints();
            buf.writeInt(points.length);
            for (PathPoint point : points) { writePathPoint(buf, point); }
            buf.writeInt(path.getCurrentPathIndex());
            PathPoint[] openSet = ((IPathMixin) path).getOpenSet();
            buf.writeInt(openSet.length);
            for (PathPoint point : openSet) { writePathPoint(buf, point); }
            PathPoint[] closedSet = ((IPathMixin) path).getClosedSet();
            buf.writeInt(closedSet.length);
            for (PathPoint point : closedSet) { writePathPoint(buf, point); }

        }
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

    private PathPoint readPathPoint(PacketBuffer buf) {
        PathPoint point = new PathPoint(buf.readInt(), buf.readInt(), buf.readInt());
        point.distanceFromOrigin = buf.readFloat();
        point.cost = buf.readFloat();
        point.costMalus = buf.readFloat();
        point.visited = buf.readBoolean();
        point.nodeType = PathNodeType.values()[buf.readInt()];
        point.distanceToTarget = buf.readFloat();
        return point;
    }

    private void writePathPoint(FriendlyByteBuf buf, PathPoint point) {
        buf.writeInt(point.x);
        buf.writeInt(point.y);
        buf.writeInt(point.z);
        buf.writeFloat(point.distanceFromOrigin);
        buf.writeFloat(point.cost);
        buf.writeFloat(point.costMalus);
        buf.writeBoolean(point.visited);
        buf.writeInt(point.nodeType.ordinal());
        buf.writeFloat(point.distanceToTarget);
    }

}