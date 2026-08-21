package noppes.npcs.api.wrapper.gui;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.gui.ITexturedRect;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiComponentUpdate;
import noppes.npcs.packets.client.PacketGuiData;

@SideOnly(Side.CLIENT)
public class CustomGuiWrapper
		extends GuiComponentsWrapper
		implements ICustomGui {

	private int id;
	private int width;
	private int height;
	private boolean pauseGame;
	private boolean closesOnEsc = true;
	private final CustomGuiTexturedRectWrapper background = new CustomGuiTexturedRectWrapper();
	private final GuiComponentsScrollableWrapper scrollingPanel;
	private ScriptContainer scriptHandler;
	private CustomGuiWrapper parent;
	private CustomGuiWrapper subgui;
	public EntityCustomNpc npc;

	// New from Unofficial (BetaZavr)
	public CustomNpcsPermissions.Permission permission = null;

	public CustomGuiWrapper(IPlayer<?> player) {
		super(player);
		scrollingPanel = new GuiComponentsScrollableWrapper(this, player);
	}

	public CustomGuiWrapper(IPlayer<?> player, int idIn, int width, int height, boolean pauseGameIn) {
		this(player);
		id = idIn;
		setSize(width, height);
		pauseGame = pauseGameIn;
		scriptHandler = ScriptContainer.Current;
		background.setId(-1);
	}

	@Override
	public int getId() { return id; }

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public ScriptContainer getScriptHandler() {
		return scriptHandler;
	}

	@Override
	public void setSize(int widthIn, int heightIn) {
		width = widthIn;
		height = heightIn;
		if (background.getWidth() <= 0 || background.getHeight() <= 0) { background.setSize(widthIn, heightIn); }
	}

	@Override
	public void setDoesPauseGame(boolean pauseGameIn) { pauseGame = pauseGameIn; }

	@Override
	public void setClosesOnEsc(boolean closesOnEscIn) { closesOnEsc = closesOnEscIn; }

	public boolean getClosesOnEsc() { return closesOnEsc; }

	public boolean getDoesPauseGame() { return pauseGame; }

	@Override
	public void setBackgroundTexture(String resourceLocation) {
		background.texture = resourceLocation;
	}

	@SuppressWarnings("all")
	public String getBackgroundTexture() { return background.texture; }

	public ITexturedRect getBackgroundRect() {
		return background;
	}

	@Override
	public GuiComponentsScrollableWrapper getScrollingPanel() {
		return scrollingPanel;
	}

	@Override
	public void openSubGui(ICustomGui gui) {
		subgui = (CustomGuiWrapper) gui;
		subgui.parent = this;
		subgui.npc = npc;
		getRootGui().update();
	}

	@Override
	public CustomGuiWrapper getSubGuiWrapper() { return subgui; }

	@Override
	public CustomGuiWrapper closeSubGui() {
		if (subgui == null) {
			throw new CustomNPCsException("Current gui has no subgui");
		}
		CustomGuiWrapper gui = subgui;
		subgui = null;
		player.showCustomGui(getRootGui());
		return gui;
	}

	@Override
	public void close() {
		if (parent == null) {
			player.closeGui();
		}
		else {
			parent.subgui = null;
			getRootGui().update();
		}
	}

	@Override
	public CustomGuiWrapper getParentGui() {
		return parent;
	}

	@Override
	public CustomGuiWrapper getRootGui() {
		return parent == null ? this : parent.getRootGui();
	}

	@Override
	public CustomGuiWrapper getActiveGui() {
		return subgui == null ? this : subgui.getActiveGui();
	}

	@Override
	public IPlayer<?> getPlayer() { return player; }

	@Override
	public void update() {
		if (player instanceof EntityPlayerMP) {
			if (player.getMCEntity().openContainer instanceof ContainerCustomGui) { Packets.send((EntityPlayerMP) player.getMCEntity(), new PacketGuiData(getRootGui().toNBT())); }
			((ContainerCustomGui) player.getMCEntity().openContainer).setGui(getActiveGui(), player.getMCEntity());
		}
	}

	@Override
	public void update(ICustomGuiComponent component) {
		if (player instanceof EntityPlayerMP && player.getMCEntity().openContainer instanceof ContainerCustomGui) {
			Packets.send((EntityPlayerMP) player.getMCEntity(), new PacketGuiComponentUpdate(component.getUniqueID(), ((CustomGuiComponentWrapper) component).toNBT(new NBTTagCompound())));
		}
	}

	public ICustomGui of(NBTTagCompound tag) {
		id = tag.getInteger("id");
		width = tag.getIntArray("size")[0];
		height = tag.getIntArray("size")[1];
		pauseGame = tag.getBoolean("pause");
		closesOnEsc = tag.getBoolean("closesOnEsc");
		background.fromNBT(tag.getCompoundTag("backgroundRect"));
		setComponentNbt(tag.getCompoundTag("components"));
		scrollingPanel.setComponentNbt(tag.getCompoundTag("scrolling_components"));
		if (tag.hasKey("subgui")) {
			if (subgui == null) {
				subgui = new CustomGuiWrapper(player);
				subgui.of(tag.getCompoundTag("subgui"));
			}
		} else {
			subgui = null;
		}
		return this;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setIntArray("size", new int[]{width, height});
		tag.setBoolean("pause", pauseGame);
		tag.setBoolean("closesOnEsc", closesOnEsc);
		tag.setTag("backgroundRect", background.toNBT(new NBTTagCompound()));
		tag.setTag("components", getComponentNbt());
		tag.setTag("scrolling_components", scrollingPanel.getComponentNbt());
		if (parent == null) { tag.setInteger("slotSize", getActiveGui().getSlots().size()); }
		if (subgui != null) { tag.setTag("subgui", subgui.toNBT()); }
		return tag;
	}

	@Override
	public ICustomGuiComponent getComponentUuid(UUID id) {
		ICustomGuiComponent comp;
		if (subgui != null) {
			comp = subgui.getComponentUuid(id);
			if (comp != null) { return comp; }
		}
		comp = super.getComponentUuid(id);
		return comp != null ? comp : scrollingPanel.getComponentUuid(id);
	}

	public boolean hasSubGui() { return this.subgui != null; }

	// New from Unofficial (BetaZavr)
	public CustomNpcsPermissions.Permission getPermission() { return permission; }
}
