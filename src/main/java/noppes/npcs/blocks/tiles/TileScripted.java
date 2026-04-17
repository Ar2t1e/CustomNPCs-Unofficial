package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.*;
import noppes.npcs.api.ILayerBlockModel;
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

import javax.annotation.Nonnull;

public class TileScripted extends TileNpcEntity implements ITickable, IScriptBlockHandler {

	public class TextPlane implements ITextPlane {
		public float offsetX;
		public float offsetY;
		public float offsetZ;
		public int rotationX;
		public int rotationY;
		public int rotationZ;
		public float scale;
		public String text;
		public TextBlock textBlock;
		public boolean textHasChanged;

		public TextPlane() {
			this.textHasChanged = true;
			this.text = "";
			this.rotationX = 0;
			this.rotationY = 0;
			this.rotationZ = 0;
			this.offsetX = 0.0f;
			this.offsetY = 0.0f;
			this.offsetZ = 0.5f;
			this.scale = 1.0f;
		}

		public NBTTagCompound getNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("Text", this.text);
			compound.setInteger("RotationX", this.rotationX);
			compound.setInteger("RotationY", this.rotationY);
			compound.setInteger("RotationZ", this.rotationZ);
			compound.setFloat("OffsetX", this.offsetX);
			compound.setFloat("OffsetY", this.offsetY);
			compound.setFloat("OffsetZ", this.offsetZ);
			compound.setFloat("Scale", this.scale);
			return compound;
		}

		@Override
		public float getOffsetX() {
			return this.offsetX;
		}

		@Override
		public float getOffsetY() {
			return this.offsetY;
		}

		@Override
		public float getOffsetZ() {
			return this.offsetZ;
		}

		@Override
		public int getRotationX() {
			return this.rotationX;
		}

		@Override
		public int getRotationY() {
			return this.rotationY;
		}

		@Override
		public int getRotationZ() {
			return this.rotationZ;
		}

		@Override
		public float getScale() {
			return this.scale;
		}

		@Override
		public String getText() {
			return this.text;
		}

		public void setNBT(NBTTagCompound compound) {
			this.setText(compound.getString("Text"));
			this.rotationX = compound.getInteger("RotationX");
			this.rotationY = compound.getInteger("RotationY");
			this.rotationZ = compound.getInteger("RotationZ");
			this.offsetX = compound.getFloat("OffsetX");
			this.offsetY = compound.getFloat("OffsetY");
			this.offsetZ = compound.getFloat("OffsetZ");
			this.scale = compound.getFloat("Scale");
		}

		@Override
		public void setOffsetX(float x) {
			x = ValueUtil.correctFloat(x, -1.0f, 1.0f);
			if (this.offsetX == x) {
				return;
			}
			this.offsetX = x;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setOffsetY(float y) {
			y = ValueUtil.correctFloat(y, -1.0f, 1.0f);
			if (this.offsetY == y) {
				return;
			}
			this.offsetY = y;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setOffsetZ(float z) {
			z = ValueUtil.correctFloat(z, -1.0f, 1.0f);
			if (this.offsetZ == z) {
				return;
			}
			this.offsetZ = z;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setRotationX(int x) {
			x = ValueUtil.correctInt(x % 360, 0, 359);
			if (this.rotationX == x) {
				return;
			}
			this.rotationX = x;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setRotationY(int y) {
			y = ValueUtil.correctInt(y % 360, 0, 359);
			if (this.rotationY == y) {
				return;
			}
			this.rotationY = y;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setRotationZ(int z) {
			z = ValueUtil.correctInt(z % 360, 0, 359);
			if (this.rotationZ == z) {
				return;
			}
			this.rotationZ = z;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setScale(float scale) {
			if (this.scale == scale) {
				return;
			}
			this.scale = scale;
			TileScripted.this.needsClientUpdate = true;
		}

		@Override
		public void setText(String text) {
			if (this.text.equals(text)) {
				return;
			}
			this.text = text;
			this.textHasChanged = true;
			TileScripted.this.needsClientUpdate = true;
		}
	}
	public int activePowering = 0;
	private IBlock blockDummy = null;
	public float blockHardness = 5.0f;
	public Block blockModel = null;
	public float blockResistance = 10.0f;
	public boolean enabled = false;
	public boolean isLadder = false;
	public boolean isPassable = false;
	public ItemStack itemModel = new ItemStack(CustomBlocks.scripted);
	public int metaModel = 0;
	public long lastInited = -1L;
	public int lightValue = 0;
	public boolean needsClientUpdate = false;
	public int newPower = 0;
	public int powering = 0;
	public int prevPower = 0;
	public TileEntity renderTile;
	public boolean renderTileErrored = true;
	public ITickable renderTileUpdate = null;
	public int rotationX = 0;
	public int rotationY = 0;
	public int rotationZ = 0;
	public float scaleX = 1.0f;
	public float scaleY = 1.0f;
	public float scaleZ = 1.0f;
	public String scriptLanguage = "ECMAScript";
	public List<ScriptContainer> scripts = new ArrayList<>();
	public TextPlane text1 = new TextPlane();
	public TextPlane text2 = new TextPlane();
	public TextPlane text3 = new TextPlane();
	public TextPlane text4 = new TextPlane();
	public TextPlane text5 = new TextPlane();
	public TextPlane text6 = new TextPlane();

	private short ticksExisted = 0;
	public DataTimers timers;

	public ILayerBlockModel[] layers = new ILayerBlockModel[0];

	public TileScripted() { timers = new DataTimers(this); }

	public void clearConsole() {
		for (ScriptContainer script : this.getScripts()) {
			script.console.clear();
		}
	}

	public IBlock getBlock() {
		if (this.blockDummy == null) {
			this.blockDummy = new BlockScriptedWrapper(this.getWorld(), this.getBlockType(), this.getPos());
		}
		return this.blockDummy;
	}

	public TreeMap<Long, String> getConsoleText() {
		TreeMap<Long, String> map = new TreeMap<>();
		int tab = 0;
		for (ScriptContainer script : this.getScripts()) {
			++tab;
			for (Map.Entry<Long, String> entry : script.console.entrySet()) {
				String log;
				if (map.containsKey(entry.getKey())) { log = map.get(entry.getKey()) + "\n\n" + "ScriptTab " + tab + ":\n" + entry.getValue(); }
				else { log = " ScriptTab " + tab + ":\n" + entry.getValue(); }
				map.put(entry.getKey(), log);
			}
		}
		return map;
	}

	@Override
	public void clearConsoleText(Long key) {
		for (ScriptContainer script : this.getScripts()) {
			script.console.remove(key);
		}
	}

	public void writeDisplayNBT(NBTTagCompound compound) {
		NBTTagCompound stackcompound = new NBTTagCompound();
		this.itemModel.writeToNBT(stackcompound);
		if (this.blockModel != null) {
			ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.blockModel);
			compound.setString("ScriptBlockModelBlock", resourcelocation.toString());
		}
		compound.setTag("ScriptBlockModel", stackcompound);
		compound.setInteger("LightValue", this.lightValue);
		compound.setBoolean("IsLadder", this.isLadder);
		compound.setBoolean("IsPassable", this.isPassable);
		compound.setInteger("RotationX", this.rotationX);
		compound.setInteger("RotationY", this.rotationY);
		compound.setInteger("RotationZ", this.rotationZ);
		compound.setFloat("ScaleX", this.scaleX);
		compound.setFloat("ScaleY", this.scaleY);
		compound.setFloat("ScaleZ", this.scaleZ);
		compound.setTag("Text1", this.text1.getNBT());
		compound.setTag("Text2", this.text2.getNBT());
		compound.setTag("Text3", this.text3.getNBT());
		compound.setTag("Text4", this.text4.getNBT());
		compound.setTag("Text5", this.text5.getNBT());
		compound.setTag("Text6", this.text6.getNBT());
		compound.setInteger("ModelMeta", this.metaModel);
		NBTTagList l = new NBTTagList();
        for (ILayerBlockModel layer : this.layers) {
            l.appendTag(layer.getNbt().getMCNBT());
        }
		compound.setTag("Layers", l);
	}

	@Override
	public boolean getEnabled() {
		return this.enabled;
	}

	public String getLanguage() {
		return this.scriptLanguage;
	}

	public NBTTagCompound getNBT(NBTTagCompound compound) {
		compound.setTag("Scripts", NBTTags.nbtScript(this.scripts));
		compound.setString("ScriptLanguage", this.scriptLanguage);
		compound.setBoolean("ScriptEnabled", this.enabled);
		compound.setInteger("BlockPowering", this.powering);
		compound.setInteger("BlockPrevPower", this.prevPower);
		compound.setFloat("BlockHardness", this.blockHardness);
		compound.setFloat("BlockResistance", this.blockResistance);
		compound.setInteger("BlockMeta", this.metaModel);
		return compound;
	}

	@SideOnly(Side.CLIENT)
	public @Nonnull AxisAlignedBB getRenderBoundingBox() {
		return Block.FULL_BLOCK_AABB.offset(this.getPos());
	}

	public List<ScriptContainer> getScripts() {
		return this.scripts;
	}

	@SuppressWarnings("deprecation")
	public IBlockState getState() {
		IBlockState state = null;
		if (this.blockModel != null && this.itemModel != null) {
			state = this.blockModel.getStateFromMeta(this.itemModel.getItemDamage());
			if (this.metaModel > 0) {
				try {
					state = this.blockModel.getStateFromMeta(this.metaModel);
					int i = 0;
					for (IBlockState ibs : this.blockModel.getBlockState().getValidStates()) {
						if (i == this.metaModel) {
							state = ibs;
							break;
						}
						i++;
					}
					if (state != null) {
						this.blockModel = state.getBlock();
					}
				} catch (Exception e) {
					this.metaModel = 0;
				}
			}
		}
		return state;
	}

	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
	}

	public @Nonnull NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("x", this.pos.getX());
		compound.setInteger("y", this.pos.getY());
		compound.setInteger("z", this.pos.getZ());
		this.writeDisplayNBT(compound);
		this.getNBT(compound);
		return compound;
	}

	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		int light = this.lightValue;
		this.setDisplayNBT(tag);
		this.setNBT(tag);
		if (light != this.lightValue) {
			this.world.checkLight(this.pos);
		}
	}

	public boolean isClient() {
		return this.getWorld().isRemote;
	}

	public boolean isEnabled() {
		return this.enabled && ScriptController.HasStart && !this.scripts.isEmpty();
	}

	public Component noticeString(String type, Object event) {
		Component message = Component.literal("Scripted Block")
				.withStyle(TextFormatting.DARK_GRAY);
		if (type != null) {
			message.append(Component.literal(" hook \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(type).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; ").withStyle(TextFormatting.DARK_GRAY));
		}
		else { message.append(Component.literal("; ").withStyle(TextFormatting.DARK_GRAY)); }
		int dimID = world == null ? 0 : world.provider.getDimension();
		double x = 0.5d + Math.round(pos.getX() * 100.0d) / 100.0d;
		double y = 0.5d + Math.round(pos.getY() * 100.0d) / 100.0d;
		double z = 0.5d + Math.round(pos.getZ() * 100.0d) / 100.0d;
		Component posClick = Component.literal("dimension ID:" + dimID + "; X:" + x + "; Y:" + y + "; Z:" + z);
		posClick.getStyle().setColor(TextFormatting.BLUE)
				.setUnderlined(true)
				.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " + dimID + " " + x + " " + (y + 1) + " "+z))
				.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp")));
		message.append(Component.literal("in ").withStyle(TextFormatting.DARK_GRAY))
				.append(posClick);
		return message.append(Component.literal("; Side: " + (isClient() ? "Client" : "Server")).withStyle(TextFormatting.DARK_GRAY));
	}

	public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
		this.handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public void runScript(String type, Event event) {
		if (!this.isEnabled()) {
			return;
		}
		if (ScriptController.Instance.lastLoaded > this.lastInited) {
			this.lastInited = ScriptController.Instance.lastLoaded;
			if (!type.equalsIgnoreCase(EnumScriptType.INIT.function)) {
				EventHooks.onScriptBlockInit(this);
			}
		}
		for (ScriptContainer script : this.scripts) {
			script.run(type, event);
		}
	}

	public void setDisplayNBT(NBTTagCompound compound) {
		this.itemModel = new ItemStack(compound.getCompoundTag("ScriptBlockModel"));
		if (this.itemModel.isEmpty()) {
			this.itemModel = new ItemStack(CustomBlocks.scripted);
		}
		if (compound.hasKey("ScriptBlockModelBlock")) {
			this.blockModel = Block.getBlockFromName(compound.getString("ScriptBlockModelBlock"));
		}
		this.renderTileUpdate = null;
		this.renderTile = null;
		this.renderTileErrored = false;
		this.lightValue = compound.getInteger("LightValue");
		this.isLadder = compound.getBoolean("IsLadder");
		this.isPassable = compound.getBoolean("IsPassable");
		this.rotationX = compound.getInteger("RotationX");
		this.rotationY = compound.getInteger("RotationY");
		this.rotationZ = compound.getInteger("RotationZ");
		this.scaleX = compound.getFloat("ScaleX");
		this.scaleY = compound.getFloat("ScaleY");
		this.scaleZ = compound.getFloat("ScaleZ");
		if (this.scaleX <= 0.0f) {
			this.scaleX = 1.0f;
		}
		if (this.scaleY <= 0.0f) {
			this.scaleY = 1.0f;
		}
		if (this.scaleZ <= 0.0f) {
			this.scaleZ = 1.0f;
		}
		if (compound.hasKey("Text3")) {
			this.text1.setNBT(compound.getCompoundTag("Text1"));
			this.text2.setNBT(compound.getCompoundTag("Text2"));
			this.text3.setNBT(compound.getCompoundTag("Text3"));
			this.text4.setNBT(compound.getCompoundTag("Text4"));
			this.text5.setNBT(compound.getCompoundTag("Text5"));
			this.text6.setNBT(compound.getCompoundTag("Text6"));
		}
		this.metaModel = compound.getInteger("ModelMeta");
		this.layers = new ILayerBlockModel[compound.getTagList("Layers", 10).tagCount()];
		for (int i = 0; i < compound.getTagList("Layers", 10).tagCount(); i++) {
			this.layers[i] = new LayerModel(compound.getTagList("Layers", 10).getCompoundTagAt(i));
		}
	}

	public void setEnabled(boolean bo) {
		this.enabled = bo;
	}

	public void setItemModel(ItemStack item, Block b) {
		if (item == null || item.isEmpty()) {
			item = new ItemStack(CustomBlocks.scripted);
		}
		if (NoppesUtilPlayer.compareItems(item, this.itemModel, false, false) && b != this.blockModel) {
			return;
		}
		this.itemModel = item;
		this.metaModel = b != null ? item.getItemDamage() : 0;
		this.blockModel = b;
		this.needsClientUpdate = true;
	}

	public void setItemModel(ItemStack item, Block b, int meta) {
		this.setItemModel(item, b);
		this.metaModel = meta;
	}

	public void setLanguage(String lang) {
		this.scriptLanguage = lang;
	}

	@Override
	public void setLastInited(long timeMC) {
		this.lastInited = timeMC;
	}

	@Override
	public void init() { lastInited = -1; }

	public void setLightValue(int value) {
		if (value == this.lightValue) {
			return;
		}
		this.lightValue = ValueUtil.correctInt(value, 0, 15);
		this.needsClientUpdate = true;
	}

	public void setRedstonePower(int strength) {
		if (this.powering == strength) {
			return;
		}
		int correctInt = ValueUtil.correctInt(strength, 0, 15);
		this.activePowering = correctInt;
		this.prevPower = correctInt;
		this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
		this.powering = this.activePowering;
	}

	public void setRotation(int x, int y, int z) {
		if (this.rotationX == x && this.rotationY == y && this.rotationZ == z) {
			return;
		}
		this.rotationX = ValueUtil.correctInt(x, 0, 359);
		this.rotationY = ValueUtil.correctInt(y, 0, 359);
		this.rotationZ = ValueUtil.correctInt(z, 0, 359);
		this.needsClientUpdate = true;
	}

	public void setScale(float x, float y, float z) {
		if (this.scaleX == x && this.scaleY == y && this.scaleZ == z) {
			return;
		}
		this.scaleX = ValueUtil.correctFloat(x, 0.0f, 10.0f);
		this.scaleY = ValueUtil.correctFloat(y, 0.0f, 10.0f);
		this.scaleZ = ValueUtil.correctFloat(z, 0.0f, 10.0f);
		this.needsClientUpdate = true;
	}

	public void update() {
		if (this.renderTileUpdate != null) {
			try {
				this.renderTileUpdate.update();
			} catch (Exception e) {
				this.renderTileUpdate = null;
			}
		}
		++this.ticksExisted;
		if (this.prevPower != this.newPower && this.powering <= 0) {
			EventHooks.onScriptBlockRedstonePower(this, this.prevPower, this.newPower);
			this.prevPower = this.newPower;
		}
		this.timers.update();
		if (this.ticksExisted >= 10) {
			if (isEnabled()) {
				ScriptController.Instance.tryAdd(0, this);
				EventHooks.onScriptBlockUpdate(this);
			}
			this.ticksExisted = 0;
		}
		if (this.needsClientUpdate) {
			this.markDirty();
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, state, state, 3);
			this.needsClientUpdate = false;
		}
	}

	public void setNBT(NBTTagCompound compound) {
		this.scripts = NBTTags.getScript(compound.getTagList("Scripts", 10), this);
		this.scriptLanguage = compound.getString("ScriptLanguage");
		this.enabled = compound.getBoolean("ScriptEnabled");
		int pw = compound.getInteger("BlockPowering");
		this.powering = pw;
		this.activePowering = pw;
		this.prevPower = compound.getInteger("BlockPrevPower");
		if (compound.hasKey("BlockHardness")) {
			this.blockHardness = compound.getFloat("BlockHardness");
			this.blockResistance = compound.getFloat("BlockResistance");
		}
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.setNBT(compound);
		this.setDisplayNBT(compound);
		this.timers.readFromNBT(compound);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		this.getNBT(compound);
		this.writeDisplayNBT(compound);
		this.timers.writeToNBT(compound);
		super.writeToNBT(compound);
		return compound;
	}

}
