package noppes.npcs.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.wrapper.WrapperNpcAPI;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// DefaultPermissionHandler
public class CustomPermissionHandler implements IPermissionHandler {

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(CustomNpcs.MODID, CustomNpcs.MODID + "_handler");

    public CustomPermissionHandler(Collection<PermissionNode<?>> permissions) {
        CustomNpcsPermissions.permissionHandler = this;
    }

    @Override
    public ResourceLocation getIdentifier() { return IDENTIFIER; }

    @Override
    public Set<PermissionNode<?>> getRegisteredNodes() { return new HashSet<>(CustomNpcsPermissions.permissions.keySet()); }

    @Override
    public <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
        return node.getDefaultResolver().resolve(player, player.getUUID(), context);
    }

    @Override
    public <T> T getOfflinePermission(UUID uuid, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
        return node.getDefaultResolver().resolve(null, uuid, context);
    }

}
