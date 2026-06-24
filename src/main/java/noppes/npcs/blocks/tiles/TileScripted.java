package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.*;
import noppes.npcs.api.ILayerBlockModel;
import noppes.npcs.api.INbt;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.client.layer.block.LayerBlockModel;
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

		public float offsetX = 0.0f;
		public float offsetY = 0.0f;
		public float offsetZ = 0.5f;
		public int rotationX = 0;
		public int rotationY = 0;
		public int rotationZ = 0;
		public float scale = 1.0f;
		public String text = "";
		public TextBlock textBlock;
		public boolean textHasChanged = true;

		@Override
		public INbt getNbt() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("Text", text);
			compound.setInteger("RotationX", rotationX);
			compound.setInteger("RotationY", rotationY);
			compound.setInteger("RotationZ", rotationZ);
			compound.setFloat("OffsetX", offsetX);
			compound.setFloat("OffsetY", offsetY);
			compound.setFloat("OffsetZ", offsetZ);
			compound.setFloat("Scale", scale);
			return new NBTWrapper(compound);
		}

		@Override
		public float getOffsetX() { return offsetX; }

		@Override
		public float getOffsetY() { return offsetY; }

		@Override
		public float getOffsetZ() { return offsetZ; }

		@Override
		public int getRotationX() { return rotationX; }

		@Override
		public int getRotationY() { return rotationY; }

		@Override
		public int getRotationZ() { return rotationZ; }

		@Override
		public float getScale() { return scale; }

		@Override
		public String getText() { return text; }

		@Override
		public void setNbt(INbt nbt) {
			setText(nbt.getString("Text"));
			rotationX = nbt.getInteger("RotationX");
			rotationY = nbt.getInteger("RotationY");
			rotationZ = nbt.getInteger("RotationZ");
			offsetX = nbt.getFloat("OffsetX");
			offsetY = nbt.getFloat("OffsetY");
			offsetZ = nbt.getFloat("OffsetZ");
			scale = nbt.getFloat("Scale");
		}

		@Override
		public void setOffsetX(float x) {
			x = ValueUtil.correctFloat(x, -1.0f, 1.0f);
			if (offsetX != x) {
				offsetX = x;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setOffsetY(float y) {
			y = ValueUtil.correctFloat(y, -1.0f, 1.0f);
			if (offsetY != y) {
				offsetY = y;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setOffsetZ(float z) {
			z = ValueUtil.correctFloat(z, -1.0f, 1.0f);
			if (offsetZ != z) {
				offsetZ = z;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setRotationX(int x) {
			x = ValueUtil.correctInt(x % 360, 0, 359);
			if (rotationX != x) {
				rotationX = x;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setRotationY(int y) {
			y = ValueUtil.correctInt(y % 360, 0, 359);
			if (rotationY != y) {
				rotationY = y;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setRotationZ(int z) {
			z = ValueUtil.correctInt(z % 360, 0, 359);
			if (rotationZ != z) {
				rotationZ = z;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setScale(float scaleIn) {
			if (scale != scaleIn) {
				scale = scaleIn;
				needsClientUpdate = true;
			}
		}

		@Override
		public void setText(String textIn) {
			if (!text.equals(textIn) && textIn != null) {
				text = textIn;
				textHasChanged = true;
				needsClientUpdate = true;
			}
		}

	}

	protected IBlock blockDummy = null;
	protected short tickCount = 0;

	public List<ScriptContainer> scripts = new ArrayList<>();
	public String scriptLanguage = "ECMAScript";
	public DataTimers timers;

	public boolean renderTileErrored = true;
	public boolean needsClientUpdate = false;
	public boolean enabled = false;
	public boolean isPassable = false;
	public boolean isLadder = false;

	public int powering = 0;
	public int activePowering = 0;
	public int newPower = 0;
	public int prevPower = 0;
	public int lightValue = 0;

	public float blockHardness = 5.0f;
	public float blockResistance = 10.0f;

	public long lastInited = -1L;

	// render
	public ITickable renderTileUpdate = null;
	public TileEntity renderTile;
	// New from Unofficial (BetaZavr)
	protected @Nonnull final List<ILayerBlockModel> layers = new ArrayList<>();
	protected @Nonnull final List<ITextPlane> textPlanes = new ArrayList<>();

	public TileScripted() {
		timers = new DataTimers(this);
		layers.add(new LayerBlockModel(0, new ItemStack(CustomBlocks.scripted), this));
		for (int i = 0; i < 7; i++) { textPlanes.add(new TextPlane()); }
	}

	@Override
	public IBlock getBlock() {
		if (blockDummy == null) {
			blockDummy = new BlockScriptedWrapper(world,
					world == null ? CustomBlocks.scripted.getDefaultState() : world.getBlockState(pos),
					pos);
		}
		return blockDummy;
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound) {
		super.readFromNBT(compound);
		setNBT(compound);
		setDisplayNBT(compound);
		timers.readFromNBT(compound);
	}

	public void setNBT(NBTTagCompound compound) {
		scripts = NBTTags.getScript(compound.getTagList("Scripts", 10), this);
		scriptLanguage = compound.getString("ScriptLanguage");
		enabled = compound.getBoolean("ScriptEnabled");
		activePowering = powering = compound.getInteger("BlockPowering");
		prevPower = compound.getInteger("BlockPrevPower");
		if (compound.hasKey("BlockHardness")) {
			blockHardness = compound.getFloat("BlockHardness");
			blockResistance = compound.getFloat("BlockResistance");
		}
	}

	public void setDisplayNBT(NBTTagCompound compound) {
		renderTileUpdate = null;
		renderTile = null;
		renderTileErrored = false;
		lightValue = compound.getInteger("LightValue");
		isLadder = compound.getBoolean("IsLadder");
		isPassable = compound.getBoolean("IsPassable");
		// New from Unofficial (BetaZavr)
		textPlanes.clear();
		// old
		for (int i = 1; i < 7; i++) {
			String key = "Text" + i;
			if (compound.hasKey(key, 10)) {
				TextPlane textPlane = new TextPlane();
				textPlane.setNbt(new NBTWrapper(compound.getCompoundTag(key)));
				textPlanes.add(textPlane);
			}
		}
		// new
		if (compound.hasKey("TextPlanes", 9)) {
			NBTTagList list = compound.getTagList("TextPlanes", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				TextPlane textPlane = new TextPlane();
				textPlane.setNbt(new NBTWrapper(list.getCompoundTagAt(i)));
				textPlanes.add(textPlane);
			}
		}
		layers.clear();
		for (int i = 0; i < compound.getTagList("Layers", 10).tagCount(); i++) {
			LayerBlockModel lbm = new LayerBlockModel(this);
			lbm.setNbt(new NBTWrapper(compound.getTagList("Layers", 10).getCompoundTagAt(i)));
			layers.add(lbm);
		}
		if (layers.isEmpty()) { layers.add(new LayerBlockModel(0, new ItemStack(CustomBlocks.scripted), this)); }
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		save(compound);
		saveDisplayNBT(compound);
		timers.writeToNBT(compound);
		super.writeToNBT(compound);
		return compound;
	}

	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setTag("Scripts", NBTTags.nbtScript(scripts));
		compound.setString("ScriptLanguage", scriptLanguage);
		compound.setBoolean("ScriptEnabled", enabled);
		compound.setInteger("BlockPowering", powering);
		compound.setInteger("BlockPrevPower", prevPower);
		compound.setFloat("BlockHardness", blockHardness);
		compound.setFloat("BlockResistance", blockResistance);
		return compound;
	}

	public void saveDisplayNBT(NBTTagCompound compound) {
		compound.setInteger("LightValue", lightValue);
		compound.setBoolean("IsLadder", isLadder);
		compound.setBoolean("IsPassable", isPassable);

		// New from Unofficial (BetaZavr)
		NBTTagList list = new NBTTagList();
		for (ITextPlane textPlane : new ArrayList<>(textPlanes)) { list.appendTag(textPlane.getNbt().getMCNBT()); }
		compound.setTag("TextPlanes", list);
		list = new NBTTagList();
		for (ILayerBlockModel layer : layers) { list.appendTag(layer.getNbt().getMCNBT()); }
		compound.setTag("Layers", list);
	}

	@Override
	public boolean isEnabled() { return CustomNpcs.EnableScripting && enabled && ScriptController.HasStart && !scripts.isEmpty() && world != null && !world.isRemote; }

	@Override
	public void clearConsoleText(Long key) {
		for (ScriptContainer script : getScripts()) { script.console.remove(key); }
	}

	@Override
	public void setLastInited(long timeMC) { lastInited = timeMC; }

	@Override
	public void update() {
		if (renderTileUpdate != null) {
			try { renderTileUpdate.update(); }
			catch (Exception e) { renderTileUpdate = null; }
		}
		++tickCount;
		if (prevPower != newPower && powering <= 0) {
			EventHooks.onScriptBlockRedstonePower(this, prevPower, newPower);
			prevPower = newPower;
		}
		timers.update();
		if (tickCount >= 10) {
			if (isEnabled()) {
				ScriptController.Instance.tryAdd(0, this);
				EventHooks.onScriptBlockUpdate(this);
			}
			tickCount = 0;
		}
		if (needsClientUpdate) {
			markDirty();
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
			needsClientUpdate = false;
		}
	}

	@Override
	public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) { handleUpdateTag(pkt.getNbtCompound()); }

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		int light = lightValue;
		setDisplayNBT(tag);
		if (light != lightValue) { world.checkLight(pos); }
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }

	@Override
	public @Nonnull NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("x", pos.getX());
		compound.setInteger("y", pos.getY());
		compound.setInteger("z", pos.getZ());
		saveDisplayNBT(compound);
		return compound;
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
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
			powering = activePowering;
		}
	}

	public ILayerBlockModel getMainModel() {
		ILayerBlockModel lbm = layers.get(0);
		if (lbm == null) {
			lbm = new LayerBlockModel(0, new ItemStack(CustomBlocks.scripted), this);
			layers.add(0, lbm);
			int i = 0;
			for (ILayerBlockModel model : new ArrayList<>(layers)) {
				if (model instanceof LayerBlockModel) { ((LayerBlockModel) model).setId(i); }
				i++;
			}
			needsClientUpdate = true;
		}
		return lbm;
	}

	public ILayerBlockModel createLayerModel() {
		if (layers.size() >= 25) { return layers.get(24); }
		LayerBlockModel lbm = new LayerBlockModel(this);
		layers.add(lbm);
		int i = 0;
		for (ILayerBlockModel model : new ArrayList<>(layers)) {
			if (model instanceof LayerBlockModel) { ((LayerBlockModel) model).setId(i); }
			i++;
		}
		return lbm;
	}

	public boolean removeLayerModel(ILayerBlockModel layer) {
		if (!(layer instanceof LayerBlockModel)) { return false; }
		return layers.remove(layer);
	}

	@Override
	public void runScript(String type, Event event) {
		if (isEnabled()) {
			if (ScriptController.Instance.lastLoaded > lastInited) {
				lastInited = ScriptController.Instance.lastLoaded;
				if (!type.equalsIgnoreCase(EnumScriptType.INIT.function)) { EventHooks.onScriptBlockInit(this); }
			}
			for (ScriptContainer script : scripts) { script.run(type, event); }
		}
	}

	@Override
	public boolean isClient() { return world == null || world.isRemote; }

	@Override
	public boolean getEnabled() { return enabled; }

	@Override
	public void setEnabled(boolean bo) { enabled = bo; }

	@Override
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
				.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp").getParent()));
		message.append(Component.literal("in ").withStyle(TextFormatting.DARK_GRAY))
				.append(posClick);
		return message.append(Component.literal("; Side: " + (isClient() ? "Client" : "Server")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public String getLanguage() { return scriptLanguage; }

	@Override
	public void setLanguage(String lang) {
		if (lang == null || lang.isEmpty()) { lang = "ECMAScript"; }
		scriptLanguage = lang;
	}

	@Override
	public List<ScriptContainer> getScripts() { return scripts; }

	@Override
	public TreeMap<Long, String> getConsoleText() {
		TreeMap<Long, String> map = new TreeMap<>();
		int tab = 0;
		for (ScriptContainer script : getScripts()) {
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
	public void clearConsole() { for (ScriptContainer script : getScripts()) { script.console.clear(); } }

	@Override
	@SideOnly(Side.CLIENT)
	public @Nonnull AxisAlignedBB getRenderBoundingBox() {
		double minX = 0.0f;
		double minY = 0.0f;
		double minZ = 0.0f;
		double maxX = 0.0f;
		double maxY = 0.0f;
		double maxZ = 0.0f;
		for (ILayerBlockModel layer : new ArrayList<>(layers)) {
			AxisAlignedBB aabb = layer.getBoundingBox();
			if (minX > aabb.minX) { minX = aabb.minX; }
			if (minY > aabb.minY) { minY = aabb.minY; }
			if (minZ > aabb.minZ) { minZ = aabb.minZ; }
			if (maxX < aabb.maxX) { maxX = aabb.maxX; }
			if (maxY < aabb.maxY) { maxY = aabb.maxY; }
			if (maxZ < aabb.maxZ) { maxZ = aabb.maxZ; }
		}
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(pos);
	}

	@Override
	public void init() { lastInited = -1; }

	// New from Unofficial (BetaZavr)
	public @Nonnull List<ILayerBlockModel> getLayers() { return layers; }

	public @Nonnull List<ITextPlane> getTextPlanes() { return textPlanes; }

}
