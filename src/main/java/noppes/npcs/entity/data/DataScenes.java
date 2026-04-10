package noppes.npcs.entity.data;

import java.util.*;

import com.mojang.brigadier.StringReader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.shared.common.CommonUtil;
import noppes.npcs.util.ValueUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DataScenes {

   private final @Nonnull EntityNPCInterface npc;
   public List<DataScenes.SceneContainer> scenes = new ArrayList<>();
   public static Map<String, DataScenes.SceneState> StartedScenes = new HashMap<>();
   public static List<DataScenes.SceneContainer> ScenesToRun = new ArrayList<>();
   private LivingEntity owner = null;
   private String ownerScene = null;

   public DataScenes(@Nonnull EntityNPCInterface npcIn) { npc = npcIn; }

   public CompoundTag save(CompoundTag compound) {
      ListTag list = new ListTag();
      for (SceneContainer scene : scenes) {
         list.add(scene.save(new CompoundTag()));
      }
      compound.put("Scenes", list);
      return compound;
   }

   public void load(CompoundTag compound) {
      ListTag list = compound.getList("Scenes", 10);
      scenes.clear();
      for(int i = 0; i < list.size(); ++i) {
         DataScenes.SceneContainer scene = new DataScenes.SceneContainer();
         scene.load(list.getCompound(i));
         scenes.add(scene);
      }
   }

   private static Component getNPCEvent(EntityNPCInterface npc) {
      if (npc == null) { return Component.translatable("scene.in.command").withStyle(ChatFormatting.DARK_GRAY); }
      MutableComponent posClick = Component.literal(npc.getName().getString());
      posClick.setStyle(posClick.getStyle().withColor(ChatFormatting.BLUE)
              .withUnderlined(true)
              .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " +
                      npc.level().dimension().location() +
                      " " + npc.getX() +
                      " " + (npc.getY() + 0.25d) +
                      " " + npc.getZ()))
              .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp"))));
      return Component.empty()
              .append(Component.literal(" (NPC: ").withStyle(ChatFormatting.DARK_GRAY))
              .append(posClick)
              .append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY));
   }

   public LivingEntity getOwner() { return owner; }

   public static void Toggle(String id, @Nullable EntityNPCInterface npc) {
      DataScenes.SceneState state = StartedScenes.get(id.toLowerCase());
      if (state != null && !state.paused) {
         state.paused = true;
         CommonUtil.NotifyOPs(Component.translatable("scene.paused", id, state.ticks).append(getNPCEvent(npc)), false);
      }
      else { Start(id, npc); }
   }

   public static void Start(String id, @Nullable EntityNPCInterface npc) {
      DataScenes.SceneState state = StartedScenes.get(id.toLowerCase());
      if (state == null) {
         CommonUtil.NotifyOPs(Component.translatable("scene.started", id).append(getNPCEvent(npc)), false);
         StartedScenes.put(id.toLowerCase(), new DataScenes.SceneState());
      } else if (state.paused) {
         state.paused = false;
         CommonUtil.NotifyOPs(Component.translatable("scene.started.from", id, state.ticks).append(getNPCEvent(npc)), false);
      }
   }

   public static void Pause(String id, @Nullable EntityNPCInterface npc) {
      if (id == null) {
         DataScenes.SceneState state;
         for(Iterator<SceneState> var2 = StartedScenes.values().iterator(); var2.hasNext(); state.paused = true) {
            state = var2.next();
         }
         CommonUtil.NotifyOPs(Component.translatable("scene.paused.all").append(getNPCEvent(npc)), false);
      } else {
         DataScenes.SceneState state = StartedScenes.get(id.toLowerCase());
         if (state == null) {
            CommonUtil.NotifyOPs(Component.translatable("scene.unknown", id).append(getNPCEvent(npc)), false);
         } else {
            state.paused = true;
            CommonUtil.NotifyOPs(Component.translatable("scene.paused", id, state.ticks).append(getNPCEvent(npc)), false);
         }
      }
   }

   public static void Reset(String id, @Nullable EntityNPCInterface npc) {
      if (id == null) {
         if (StartedScenes.isEmpty()) {
            return;
         }
         StartedScenes = new HashMap<>();
         CommonUtil.NotifyOPs(Component.translatable("scene.reset.all").append(getNPCEvent(npc)), false);
      } else if (StartedScenes.remove(id.toLowerCase()) == null) {
         CommonUtil.NotifyOPs(Component.translatable("scene.unknown", id).append(getNPCEvent(npc)), false);
      } else {
         CommonUtil.NotifyOPs(Component.translatable("scene.reset", id).append(getNPCEvent(npc)), false);
      }
   }

   public void update() {
      for (SceneContainer scene : scenes) {
         if (scene.validState()) {
            ScenesToRun.add(scene);
         }
      }
      if (owner != null && !StartedScenes.containsKey(ownerScene.toLowerCase())) {
         owner = null;
         ownerScene = null;
      }
   }

   public void addScene(String name) {
      if (!name.isEmpty()) {
         DataScenes.SceneContainer scene = new DataScenes.SceneContainer();
         scene.name = name;
         scenes.add(scene);
      }
   }

   public class SceneContainer {

      public int btn = 0;
      public String name = "";
      public String lines = "";
      public boolean enabled = false;
      public int ticks = -1;
      private DataScenes.SceneState state = null;
      private final List<DataScenes.SceneEvent> events = new ArrayList<>();

      public CompoundTag save(CompoundTag compound) {
         compound.putBoolean("Enabled", enabled);
         compound.putString("Name", name);
         compound.putString("Lines", lines);
         compound.putInt("Button", btn);
         compound.putInt("Ticks", ticks);
         return compound;
      }

      public boolean validState() {
         if (!enabled) {
            return false;
         }
         if (state != null) {
            if (DataScenes.StartedScenes.containsValue(state)) { return !state.paused; }
            state = null;
         }
         state = DataScenes.StartedScenes.get(name.toLowerCase());
         if (state == null) { state = DataScenes.StartedScenes.get(btn + "btn"); }
         if (state != null) { return !state.paused; }
         return false;
      }

      public void load(CompoundTag compound) {
         enabled = compound.getBoolean("Enabled");
         name = compound.getString("Name");
         lines = compound.getString("Lines");
         btn = compound.getInt("Button");
         ticks = compound.getInt("Ticks");
         events.clear();
         for (String line : lines.split("\r\n|\r|\n")) {
            SceneEvent event = SceneEvent.parse(line);
            if (event != null) {
               events.add(event);
            }
         }
         Collections.sort(events);
      }

      public void update() {
         if (enabled && !events.isEmpty() && state != null) {
            for (SceneEvent event : events) {
               if (event.ticks > state.ticks) {
                  break;
               }
               if (event.ticks == state.ticks) {
                  try {
                     handle(event);
                  } catch (Exception ignored) {}
               }
            }
            ticks = state.ticks;
         }
      }

      private LivingEntity getEntity(String name) {
         try {
            EntitySelector selector = new EntitySelectorParser(new StringReader(name)).parse();
            Level level = npc.level();
            CommandSourceStack commandSource = getCommandSource(name, level);
            Entity entity = selector.findSingleEntity(commandSource);
            if (entity instanceof LivingEntity){ return (LivingEntity) entity; }
         }
         catch (Exception ignored) { }
         UUID uuid = null;
         try { uuid = UUID.fromString(name); } catch (Exception ignored) { }
         for (Entity entity : ((ServerLevel) npc.getCommandSenderWorld()).getEntities().getAll()) {
            if (entity instanceof LivingEntity) {
               if (uuid != null && entity.getUUID() == uuid) { return (LivingEntity) entity; }
               if (name.equalsIgnoreCase(entity.getName().getString())) { return (LivingEntity) entity; }
            }
         }
         return null;
      }

      private @NotNull CommandSourceStack getCommandSource(String name, Level level) {
         Vec3 point = new Vec3(npc.getX() + 0.5, npc.getY() + 0.5D, npc.getZ() + 0.5D);
         return new CommandSourceStack(npc.getFakeChatPlayer(), point, Vec2.ZERO, (ServerLevel) level, CustomNpcs.NpcUseOpCommands ? 4 : 2, "@CustomNPCs-" + name, Component.literal("@CustomNPCs-" + name), level.getServer(), npc){
            @Override
            public void sendFailure(@NotNull Component textIn) {
               super.sendFailure(textIn);
               CommonUtil.NotifyOPs((textIn instanceof MutableComponent mutableComponent ? mutableComponent.append(getNPCEvent(npc)) : textIn), false);
            }
         };
      }

      private BlockPos parseBlockPos(BlockPos blockpos, String[] args, int startIndex, boolean centerBlock) throws Exception {
         return new BlockPos((int)this.parseDouble(blockpos.getX(), args[startIndex], -30000000, 30000000, centerBlock), (int)this.parseDouble(blockpos.getY(), args[startIndex + 1], -64, 319, false), (int)this.parseDouble(blockpos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock));
      }

      private double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws Exception {
         boolean flag = input.startsWith("~");
         if (flag && Double.isNaN(base)) {
            throw new Exception("invalid number");
         } else {
            double d0 = flag ? base : 0.0D;
            if (!flag || input.length() > 1) {
               boolean flag1 = input.contains(".");
               if (flag) {
                  input = input.substring(1);
               }
               d0 += Double.parseDouble(input);
               if (!flag1 && !flag && centerBlock) {
                  d0 += 0.5D;
               }
            }
            if (min != 0 || max != 0) {
               if (d0 < (double)min) {
                  throw new Exception("number too small");
               }
               if (d0 > (double)max) {
                  throw new Exception("number too big");
               }
            }
            return d0;
         }
      }

      private void handle(DataScenes.SceneEvent event) throws Exception {
         String[] args;
         if (event.type == DataScenes.SceneType.MOVE) {
            args = event.param.split(" ");
            while(args.length > 1) {
               boolean move = false;
               if (args[0].startsWith("to")) {
                  move = true;
               } else if (!args[0].startsWith("tp")) {
                  break;
               }
               BlockPos pos = null;
               if (args[0].startsWith("@")) {
                  LivingEntity entityLivingBase = this.getEntity(args[0]);
                  if (entityLivingBase != null) {
                     pos = entityLivingBase.blockPosition();
                  }
                  args = Arrays.copyOfRange(args, 2, args.length);
               } else {
                  if (args.length < 4) {
                     return;
                  }

                  pos = this.parseBlockPos(npc.blockPosition(), args, 1, false);
                  args = Arrays.copyOfRange(args, 4, args.length);
               }

               if (pos != null) {
                  npc.ais.setStartPos(pos);
                  npc.getNavigation().stop();
                  if (move) {
                     Path pathEntity = npc.getNavigation().createPath(pos, 0);
                     npc.getNavigation().moveTo(pathEntity, 1.0D);
                  } else if (!npc.isInRange(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 2.0D)) {
                     npc.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                  }
               }
            }
         } else if (event.type == DataScenes.SceneType.SAY) {
            npc.saySurrounding(new Line(event.param));
         } else {
            LivingEntity entity;
            if (event.type == DataScenes.SceneType.ROTATE) {
               if (event.param.startsWith("@")) {
                  entity = this.getEntity(event.param);
                  if (entity != null) { npc.lookAi.rotate(npc.level().getNearestPlayer(entity, 30.0D)); }
               } else if (event.param.equals("clear")) {
                  npc.lookAi.stop();
               } else {
                  npc.lookAi.rotate(Integer.parseInt(event.param));
               }
            } else if (event.type == DataScenes.SceneType.EQUIP) {
               args = event.param.split(" ");
               if (args.length < 2) {
                  return;
               }

               IItemStack itemstack = null;
               if (!args[1].equalsIgnoreCase("none")) {
                  ResourceLocation resourcelocation = ResourceLocation.tryParse(args[1]);
                  Item item = ForgeRegistries.ITEMS.getValue(resourcelocation);
                  if (item != null) {
                     int ix = args.length >= 3 ? ValueUtil.correctInt(Integer.parseInt(args[2]), 1, 64) : 1;
                     itemstack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(item, ix));
                  } else {
                     itemstack = ItemStackWrapper.AIR;
                  }
               }
               if (args[0].equalsIgnoreCase("main")) {
                  npc.inventory.weapons.put(0, itemstack);
               } else if (args[0].equalsIgnoreCase("off")) {
                  npc.inventory.weapons.put(2, itemstack);
               } else if (args[0].equalsIgnoreCase("proj")) {
                  npc.inventory.weapons.put(1, itemstack);
               } else if (args[0].equalsIgnoreCase("head")) {
                  npc.inventory.armor.put(0, itemstack);
               } else if (args[0].equalsIgnoreCase("body")) {
                  npc.inventory.armor.put(1, itemstack);
               } else if (args[0].equalsIgnoreCase("legs")) {
                  npc.inventory.armor.put(2, itemstack);
               } else if (args[0].equalsIgnoreCase("boots")) {
                  npc.inventory.armor.put(3, itemstack);
               }
            } else if (event.type == DataScenes.SceneType.ATTACK) {
               if (event.param.equals("none")) {
                  npc.setTarget(null);
               } else {
                  entity = this.getEntity(event.param);
                  if (entity != null) {
                     npc.setTarget(entity);
                  }
               }
            } else if (event.type == DataScenes.SceneType.THROW) {
               args = event.param.split(" ");
               LivingEntity entityX = this.getEntity(args[0]);
               if (entityX == null) {
                  return;
               }
               float damage = Float.parseFloat(args[1]);
               if (damage <= 0.0F) {
                  damage = 0.01F;
               }

               ItemStack stack = ItemStackWrapper.MCItem(npc.inventory.getProjectile());
               if (args.length > 2) {
                  ResourceLocation resourceLocationX = ResourceLocation.tryParse(args[2]);
                  Item itemX = ForgeRegistries.ITEMS.getValue(resourceLocationX);
                  stack = itemX != null ? new ItemStack(itemX, 1) : ItemStack.EMPTY;
               }
               EntityProjectile projectile = npc.shoot(entityX, 100, stack, false);
               projectile.damage = damage;
            } else if (event.type == DataScenes.SceneType.ANIMATE) {
               npc.animateAi.temp = AnimationType.get(event.param);
            } else if (event.type == DataScenes.SceneType.COMMAND) {
               NoppesUtilServer.runCommand(npc, npc.getName().getString(), event.param, null);
            } else if (event.type == DataScenes.SceneType.STATS) {
               int i = event.param.indexOf(" ");
               if (i <= 0) {
                  return;
               }

               String type = event.param.substring(0, i).toLowerCase();
               String value = event.param.substring(i).trim();

               try {
                  if (type.equals("walking_speed")) {
                     npc.ais.setWalkingSpeed(ValueUtil.correctInt(Integer.parseInt(value), 0, 10));
                  } else if (type.equals("size")) {
                     npc.display.setSize(ValueUtil.correctInt(Integer.parseInt(value), 1, 30));
                  } else {
                     CommonUtil.NotifyOPs(Component.translatable("scene.unknown.stat", type).append(getNPCEvent(npc)), false);
                  }
               } catch (NumberFormatException var8) {
                  CommonUtil.NotifyOPs(Component.translatable("scene.unknown.stat.value", type, value).append(getNPCEvent(npc)), false);
               }
            } else if (event.type == DataScenes.SceneType.FACTION) {
               npc.setFaction(Integer.parseInt(event.param));
            } else if (event.type == DataScenes.SceneType.FOLLOW) {
               if (event.param.equalsIgnoreCase("none")) {
                  owner = null;
                  ownerScene = null;
               }
               else {
                  entity = this.getEntity(event.param);
                  if (entity == null) { return; }
                  owner = entity;
                  ownerScene = this.name;
               }
            }
         }

      }
   }

   public static class SceneState {
      public boolean paused = false;
      public int ticks = -1;
   }

   public enum SceneType {

      ANIMATE,
      MOVE,
      FACTION,
      COMMAND,
      EQUIP,
      THROW,
      ATTACK,
      FOLLOW,
      SAY,
      ROTATE,
      STATS

   }

   public static class SceneEvent implements Comparable<DataScenes.SceneEvent> {
      public int ticks = 0;
      public DataScenes.SceneType type;
      public String param = "";

      public String toString() {
         int var10000 = this.ticks;
         return var10000 + " " + this.type.name() + " " + this.param;
      }

      public static DataScenes.SceneEvent parse(String str) {
         DataScenes.SceneEvent event = new DataScenes.SceneEvent();
         int i = str.indexOf(" ");
         if (i <= 0) {
            return null;
         } else {
            try {
               event.ticks = Integer.parseInt(str.substring(0, i));
               str = str.substring(i + 1);
            } catch (NumberFormatException var8) {
               return null;
            }

            i = str.indexOf(" ");
            if (i <= 0) {
               return null;
            } else {
               String name = str.substring(0, i);
               for (SceneType type : DataScenes.SceneType.values()) {
                  if (name.equalsIgnoreCase(type.name())) {
                     event.type = type;
                  }
               }
               if (event.type == null) {
                  return null;
               } else {
                  event.param = str.substring(i + 1);
                  return event;
               }
            }
         }
      }

      public int compareTo(DataScenes.SceneEvent o) {
         return this.ticks - o.ticks;
      }
   }
}
