package noppes.npcs;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CustomTeleporter extends PortalForcer {

   private final float yRot;
   private final float xRot;
   private final Vec3 pos;

   public CustomTeleporter(ServerLevel level, Vec3 posIn, float yRotIn, float xRotIn) {
      super(level);
      pos = posIn;
      yRot = yRotIn;
      xRot = xRotIn;
   }

   public @NotNull Optional<FoundRectangle> findPortalAround(@NotNull BlockPos pos, boolean isNether, @NotNull WorldBorder border) {
      return Optional.empty();
   }

   public PortalInfo getPortalInfo(Entity entity, ServerLevel destLevel, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
      return new PortalInfo(pos, Vec3.ZERO, yRot, xRot);
   }
}
