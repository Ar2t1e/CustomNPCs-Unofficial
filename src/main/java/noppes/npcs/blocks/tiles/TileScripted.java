package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.entity.data.TextBlock;
import noppes.npcs.util.ValueUtil;
import org.jetbrains.annotations.NotNull;

public class TileScripted extends TileNpcEntity implements IScriptBlockHandler {

   protected IBlock blockDummy = null;
   protected short tickCount = 0;

   public List<ScriptContainer> scripts = new ArrayList<>();
   public BlockEntityTicker<BlockEntity> renderTileUpdate = null;
   public String scriptLanguage = "ECMAScript";
   public DataTimers timers = new DataTimers(this);
   public BlockEntity renderTile;
   public BlockState renderState;

   public boolean renderTileErrored = true;
   public boolean needsClientUpdate = false;
   public boolean enabled = false;
   public boolean isPassible = false;
   public boolean isLadder = false;

   public int powering = 0;
   public int activePowering = 0;
   public int newPower = 0;
   public int prevPower = 0;
   public int lightValue = 0;

   public float blockHardness = 5.0f;
   public float blockResistance = 10.0f;

   public long lastInited = -1L;

   // Texts
   public TileScripted.TextPlane text1 = new TileScripted.TextPlane();
   public TileScripted.TextPlane text2 = new TileScripted.TextPlane();
   public TileScripted.TextPlane text3 = new TileScripted.TextPlane();
   public TileScripted.TextPlane text4 = new TileScripted.TextPlane();
   public TileScripted.TextPlane text5 = new TileScripted.TextPlane();
   public TileScripted.TextPlane text6 = new TileScripted.TextPlane();

   // Model
   public int rotationX = 0;
   public int rotationY = 0;
   public int rotationZ = 0;
   public float scaleX = 1.0f;
   public float scaleY = 1.0f;
   public float scaleZ = 1.0f;
   public ItemStack itemModel = new ItemStack(CustomBlocks.scripted);
   public Block blockModel = null;

   public TileScripted(BlockPos pos, BlockState state) {
      super(CustomBlocks.tile_scripted, pos, state);
   }

   public IBlock getBlock() {
      if (blockDummy == null) { blockDummy = new BlockScriptedWrapper(getLevel(), CustomBlocks.scripted, getBlockPos()); }
      return blockDummy;
   }

   public void load(@NotNull CompoundTag compound) {
      super.load(compound);
      setNBT(compound);
      setDisplayNBT(compound);
      timers.load(compound);
   }

   public void setNBT(CompoundTag compound) {
      scripts = NBTTags.getScript(compound.getList("Scripts", 10), this);
      scriptLanguage = compound.getString("ScriptLanguage");
      enabled = compound.getBoolean("ScriptEnabled");
      activePowering = powering = compound.getInt("BlockPowering");
      prevPower = compound.getInt("BlockPrevPower");
      if (compound.contains("BlockHardness")) {
         blockHardness = compound.getFloat("BlockHardness");
         blockResistance = compound.getFloat("BlockResistance");
      }
   }

   public void setDisplayNBT(CompoundTag compound) {
      itemModel = ItemStack.of(compound.getCompound("ScriptBlockModel"));
      if (itemModel.isEmpty()) { itemModel = new ItemStack(CustomBlocks.scripted); }
      if (compound.contains("ScriptBlockModelBlock")) {
         blockModel = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(compound.getString("ScriptBlockModelBlock")));
      }
      renderTileUpdate = null;
      renderTile = null;
      renderTileErrored = false;
      lightValue = compound.getInt("LightValue");
      isLadder = compound.getBoolean("IsLadder");
      isPassible = compound.getBoolean("IsPassible");
      rotationX = compound.getInt("RotationX");
      rotationY = compound.getInt("RotationY");
      rotationZ = compound.getInt("RotationZ");
      scaleX = compound.getFloat("ScaleX");
      scaleY = compound.getFloat("ScaleY");
      scaleZ = compound.getFloat("ScaleZ");
      if (scaleX <= 0.0F) { scaleX = 1.0F; }
      if (scaleY <= 0.0F) { scaleY = 1.0F; }
      if (scaleZ <= 0.0F) { scaleZ = 1.0F; }
      if (compound.contains("Text3")) {
         text1.setNBT(compound.getCompound("Text1"));
         text2.setNBT(compound.getCompound("Text2"));
         text3.setNBT(compound.getCompound("Text3"));
         text4.setNBT(compound.getCompound("Text4"));
         text5.setNBT(compound.getCompound("Text5"));
         text6.setNBT(compound.getCompound("Text6"));
      }
   }

   public void saveAdditional(@NotNull CompoundTag compound) {
      getNBT(compound);
      getDisplayNBT(compound);
      timers.save(compound);
      super.saveAdditional(compound);
   }

   public CompoundTag getNBT(CompoundTag compound) {
      compound.put("Scripts", NBTTags.nbtScript(scripts));
      compound.putString("ScriptLanguage", scriptLanguage);
      compound.putBoolean("ScriptEnabled", enabled);
      compound.putInt("BlockPowering", powering);
      compound.putInt("BlockPrevPower", prevPower);
      compound.putFloat("BlockHardness", blockHardness);
      compound.putFloat("BlockResistance", blockResistance);
      return compound;
   }

   public void getDisplayNBT(CompoundTag compound) {
      CompoundTag itemCompound = new CompoundTag();
      itemModel.save(itemCompound);
      if (blockModel != null) {
         ResourceLocation resourcelocation = ForgeRegistries.BLOCKS.getKey(blockModel);
         compound.putString("ScriptBlockModelBlock", resourcelocation == null ? "" : resourcelocation.toString());
      }
      compound.put("ScriptBlockModel", itemCompound);
      compound.putInt("LightValue", lightValue);
      compound.putBoolean("IsLadder", isLadder);
      compound.putBoolean("IsPassible", isPassible);
      compound.putInt("RotationX", rotationX);
      compound.putInt("RotationY", rotationY);
      compound.putInt("RotationZ", rotationZ);
      compound.putFloat("ScaleX", scaleX);
      compound.putFloat("ScaleY", scaleY);
      compound.putFloat("ScaleZ", scaleZ);
      compound.put("Text1", text1.getNBT());
      compound.put("Text2", text2.getNBT());
      compound.put("Text3", text3.getNBT());
      compound.put("Text4", text4.getNBT());
      compound.put("Text5", text5.getNBT());
      compound.put("Text6", text6.getNBT());
   }

   @Override
   public boolean isEnabled() {
      return CustomNpcs.EnableScripting && enabled && ScriptController.HasStart && !scripts.isEmpty() && level != null && !level.isClientSide;
   }

   @Override
   public void clearConsoleText(Long key) {
      for (ScriptContainer script : getScripts()) { script.console.remove(key); }
   }

   @Override
   public void setLastInited(long timeMC) { lastInited = timeMC; }

   public static void tick(Level level, BlockPos pos, BlockState state, TileScripted tile) {
      if (tile.renderTileUpdate != null) {
         try {
            tile.renderTileUpdate.tick(level, pos, tile.renderState, tile.renderTile);
         } catch (Exception var5) {
            tile.renderTileUpdate = null;
         }
      }

      ++tile.tickCount;
      if (tile.prevPower != tile.newPower && tile.powering <= 0) {
         EventHooks.onScriptBlockRedstonePower(tile, tile.prevPower, tile.newPower);
         tile.prevPower = tile.newPower;
      }

      tile.timers.update();
      if (tile.tickCount >= 10) {
         EventHooks.onScriptBlockUpdate(tile);
         tile.tickCount = 0;
         if (tile.needsClientUpdate) {
            tile.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            tile.needsClientUpdate = false;
         }
      }

   }

   @Override
   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      handleUpdateTag(pkt.getTag());
   }

   @Override
   public void handleUpdateTag(CompoundTag tag) {
      int light = lightValue;
      setDisplayNBT(tag);
      if (light != lightValue && level != null) {
         level.getLightEngine().checkBlock(worldPosition);
      }
   }

   @Override
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public @NotNull CompoundTag getUpdateTag() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("x", worldPosition.getX());
      compound.putInt("y", worldPosition.getY());
      compound.putInt("z", worldPosition.getZ());
      getDisplayNBT(compound);
      return compound;
   }

   public void setItemModel(ItemStack item, Block b) {
      if (item == null || item.isEmpty()) {
         item = new ItemStack(CustomBlocks.scripted);
      }

      if (!NoppesUtilPlayer.compareItems(item, itemModel, false, false) || b == blockModel) {
         itemModel = item;
         blockModel = b;
         needsClientUpdate = true;
      }
   }

   public void setLightValue(int value) {
      if (value != lightValue) {
         lightValue = ValueUtil.correctInt(value, 0, 15);
         needsClientUpdate = true;
      }
   }

   public void setRedstonePower(int strength) {
      if (powering != strength) {
         prevPower = activePowering = ValueUtil.correctInt(strength, 0, 15);
         if (level != null) { level.updateNeighborsAt(worldPosition, CustomBlocks.scripted); }
         powering = activePowering;
      }
   }

   public void setScale(float x, float y, float z) {
      if (scaleX != x || scaleY != y || scaleZ != z) {
         scaleX = ValueUtil.correctFloat(x, 0.0F, 10.0F);
         scaleY = ValueUtil.correctFloat(y, 0.0F, 10.0F);
         scaleZ = ValueUtil.correctFloat(z, 0.0F, 10.0F);
         needsClientUpdate = true;
      }
   }

   public void setRotation(int x, int y, int z) {
      if (rotationX != x || rotationY != y || rotationZ != z) {
         rotationX = ValueUtil.correctInt(x, 0, 359);
         rotationY = ValueUtil.correctInt(y, 0, 359);
         rotationZ = ValueUtil.correctInt(z, 0, 359);
         needsClientUpdate = true;
      }
   }

   public void runScript(String type, Event event) {
      if (isEnabled()) {
         if (ScriptController.Instance.lastLoaded > lastInited) {
            lastInited = ScriptController.Instance.lastLoaded;
            if (!type.equals(EnumScriptType.INIT.function)) {
               EventHooks.onScriptBlockInit(this);
            }
         }
         for (ScriptContainer script : scripts) {
            script.run(type, event);
         }
      }
   }

   @Override
   public boolean isClient() {
      return getLevel() == null || getLevel().isClientSide;
   }

   @Override
   public boolean getEnabled() {
      return this.enabled;
   }

   @Override
   public void setEnabled(boolean bo) {
      this.enabled = bo;
   }

   @Override
   public MutableComponent noticeString(String type, Object event) {
      MutableComponent message = Component.literal("Scripted Block")
              .withStyle(ChatFormatting.DARK_GRAY);
      if (type != null) {
         message.append(Component.literal(" hook \"").withStyle(ChatFormatting.DARK_GRAY))
                 .append(Component.literal(type).withStyle(ChatFormatting.GRAY))
                 .append(Component.literal("\"; ").withStyle(ChatFormatting.DARK_GRAY));
      }
      else { message.append(Component.literal("; ").withStyle(ChatFormatting.DARK_GRAY)); }
      String dimID = level == null ? "null" : level.dimensionTypeId().location().toString();
      double x = 0.5d + Math.round(worldPosition.getX() * 100.0d) / 100.0d;
      double y = 0.5d + Math.round(worldPosition.getY() * 100.0d) / 100.0d;
      double z = 0.5d + Math.round(worldPosition.getZ() * 100.0d) / 100.0d;
      MutableComponent posClick = Component.literal("dimension ID:" + dimID + "; X:" + x + "; Y:" + y + "; Z:" + z);
      Style style = posClick.getStyle().withColor(ChatFormatting.BLUE);
      style = style.withUnderlined(true);
      style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " + dimID + " " + x + " " + y + " "+z));
      style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp")));
      posClick.setStyle(style);
      message.append(Component.literal("in ").withStyle(ChatFormatting.DARK_GRAY))
              .append(posClick);
      return message.append(Component.literal("; Side: " + (isClient() ? "Client" : "Server")).withStyle(ChatFormatting.DARK_GRAY));
   }

   @Override
   public String getLanguage() {
      return this.scriptLanguage;
   }

   @Override
   public void setLanguage(String lang) {
      this.scriptLanguage = lang;
   }

   @Override
   public List<ScriptContainer> getScripts() {
      return this.scripts;
   }

   @Override
   public Map<Long, String> getConsoleText() {
      Map<Long, String> map = new TreeMap<>();
      int tab = 0;
      for (ScriptContainer script : this.getScripts()) {
         ++tab;
         for (Entry<Long, String> entry : script.console.entrySet()) {
            map.put(entry.getKey(), " tab " + tab + ":\n" + entry.getValue());
         }
      }
      return map;
   }

   @Override
   public void clearConsole() {
      for (ScriptContainer script : this.getScripts()) {
         script.console.clear();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return Shapes.block().bounds().move(this.getBlockPos());
   }

   public class TextPlane implements ITextPlane {
      public boolean textHasChanged = true;
      public TextBlock textBlock;
      public String text = "";
      public int rotationX = 0;
      public int rotationY = 0;
      public int rotationZ = 0;
      public float offsetX = 0.0F;
      public float offsetY = 0.0F;
      public float offsetZ = 0.5F;
      public float scale = 1.0F;

      public String getText() {
         return this.text;
      }

      public void setText(String text) {
         if (!this.text.equals(text)) {
            this.text = text;
            this.textHasChanged = true;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public int getRotationX() {
         return this.rotationX;
      }

      public int getRotationY() {
         return this.rotationY;
      }

      public int getRotationZ() {
         return this.rotationZ;
      }

      public void setRotationX(int x) {
         x = ValueUtil.correctInt(x % 360, 0, 359);
         if (this.rotationX != x) {
            this.rotationX = x;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public void setRotationY(int y) {
         y = ValueUtil.correctInt(y % 360, 0, 359);
         if (this.rotationY != y) {
            this.rotationY = y;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public void setRotationZ(int z) {
         z = ValueUtil.correctInt(z % 360, 0, 359);
         if (this.rotationZ != z) {
            this.rotationZ = z;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public float getOffsetX() {
         return this.offsetX;
      }

      public float getOffsetY() {
         return this.offsetY;
      }

      public float getOffsetZ() {
         return this.offsetZ;
      }

      public void setOffsetX(float x) {
         x = ValueUtil.correctFloat(x, -1.0F, 1.0F);
         if (this.offsetX != x) {
            this.offsetX = x;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public void setOffsetY(float y) {
         y = ValueUtil.correctFloat(y, -1.0F, 1.0F);
         if (this.offsetY != y) {
            this.offsetY = y;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public void setOffsetZ(float z) {
         z = ValueUtil.correctFloat(z, -1.0F, 1.0F);
         if (this.offsetZ != z) {
            this.offsetZ = z;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public float getScale() {
         return this.scale;
      }

      public void setScale(float scaleIn) {
         if (scale != scaleIn) {
            scale = scaleIn;
            TileScripted.this.needsClientUpdate = true;
         }
      }

      public CompoundTag getNBT() {
         CompoundTag compound = new CompoundTag();
         compound.putString("Text", this.text);
         compound.putInt("RotationX", this.rotationX);
         compound.putInt("RotationY", this.rotationY);
         compound.putInt("RotationZ", this.rotationZ);
         compound.putFloat("OffsetX", this.offsetX);
         compound.putFloat("OffsetY", this.offsetY);
         compound.putFloat("OffsetZ", this.offsetZ);
         compound.putFloat("Scale", this.scale);
         return compound;
      }

      public void setNBT(CompoundTag compound) {
         this.setText(compound.getString("Text"));
         this.rotationX = compound.getInt("RotationX");
         this.rotationY = compound.getInt("RotationY");
         this.rotationZ = compound.getInt("RotationZ");
         this.offsetX = compound.getFloat("OffsetX");
         this.offsetY = compound.getFloat("OffsetY");
         this.offsetZ = compound.getFloat("OffsetZ");
         this.scale = compound.getFloat("Scale");
      }
   }

   public void init() { lastInited = -1; }

}
