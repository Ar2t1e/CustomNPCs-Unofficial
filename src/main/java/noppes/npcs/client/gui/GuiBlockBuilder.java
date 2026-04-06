package noppes.npcs.client.gui;

import java.util.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.IPos;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.reflection.client.renderer.BlockModelRendererReflection;
import noppes.npcs.reflection.client.renderer.BlockRendererDispatcherReflection;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.util.LogWriter;
import org.lwjgl.opengl.GL11;

public class GuiBlockBuilder extends GuiNPCInterface
		implements IGuiData, ICustomScrollListener, IScrollData, GuiYesNoCallback {

	protected final BlockPos pos;
	protected final TileBuilder tile;
	protected GuiCustomScrollNop scroll;
	protected ISchematic selected = null;

	public GuiBlockBuilder(BlockPos posIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;

		pos = posIn;
		tile = (TileBuilder) player.world.getTileEntity(pos);
		Packets.sendServer(new SPacketSchematicsTileGet(pos));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (selected == null || minecraft.world == null) { return; }
		SchematicWrapper schem = new SchematicWrapper(selected);
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft + imageWidth, guiTop + 26.0f, 0.0f);
		// background
		minecraft.getTextureManager().bindTexture(background);
		drawTexturedModalRect(0, 0, 172, 0, 84, 80);
		drawTexturedModalRect(0, 80, 172, 213, 84, 4);
		// schem
		int w = selected.getWidth();
		int l = selected.getLength();
		int h = selected.getHeight();
		float sW = (float) (w + l) * (float) Math.cos(Math.toRadians(30));
		float sH = (float) (w + l) * (float) Math.sin(Math.toRadians(30)) + (float) h;
		float scale;
		if (sW > sH) { scale = 84.0f / sW; } else { scale = 84.0f / sH; }

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		ScaledResolution sw = new ScaledResolution(minecraft);
		double d4 = sw.getScaledWidth() < minecraft.displayWidth
				? (int) Math.round((double) minecraft.displayWidth / (double) sw.getScaledWidth())
				: 1;
		int left = guiLeft + imageWidth + 2;
		int top = guiTop + 30;
		int right = guiLeft + imageWidth + 78;
		int bottom = guiTop + 106;
		GL11.glScissor((int) ((double) left * d4),
				(int) ((double) mc.displayHeight - (double) bottom * d4),
				Math.max(0, (int) ((double) (right - left) * d4)),
				Math.max(0, (int) ((double) (bottom - top) * d4)));
		GlStateManager.translate(42.0f - (w / 2.0f) * scale, 41.0f + (h / 2.0f) * scale, 150.0f);
		GlStateManager.scale(scale, -scale, -scale);

		GlStateManager.pushMatrix();
		GlStateManager.translate(w / 2.0f, h / 2.0f, l / 2.0f);
		float f0 = (minecraft.world.getTotalWorldTime() % 360.0f) * 2.0f;
		GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
		GlStateManager.rotate(f0, 0.0f, 1.0f, 0.0f);
		GlStateManager.translate(-w / 2.0f, -h / 2.0f, -l / 2.0f);

		try {
			BlockRendererDispatcher dispatcher = minecraft.getBlockRendererDispatcher();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
			for(int i = 0; i < schem.size && i < 25000; ++i) {
				IBlockState state = schem.schema.getBlockState(i);
				if (state.getRenderType() == EnumBlockRenderType.MODEL) {
					int posX = i % w;
					int posZ = (i - posX) / w % l;
					int posY = ((i - posX) / w - posZ) / l;
					BlockPos pos = schem.rotatePos(posX, posY, posZ, 0);
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
					state = SchematicWrapper.rotationState(state, 0);
					switch (state.getRenderType()) {
						case MODEL:
							IBakedModel ibakedmodel = dispatcher.getModelForState(state);
							GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
							BlockModelRenderer bmr = BlockRendererDispatcherReflection.getBlockModelRenderer(dispatcher);
							BlockColors bc = BlockModelRendererReflection.getBlockColors(bmr);
							int color = bc.colorMultiplier(state, null, null, 0);
							if (EntityRenderer.anaglyphEnable) {
								color = TextureUtil.anaglyphColor(color);
							}
							float r = (float) (color >> 16 & 255) / 255.0F;
							float g = (float) (color >> 8 & 255) / 255.0F;
							float b = (float) (color & 255) / 255.0F;
							for (EnumFacing enumfacing : EnumFacing.values()) {
								ClientEventHandler.renderModelBlockQuads(ibakedmodel.getQuads(state, enumfacing, 0L), r, g, b);
							}
							ClientEventHandler.renderModelBlockQuads(ibakedmodel.getQuads(state, null, 0L), r, g, b);
							break;
						case ENTITYBLOCK_ANIMATED:
							ChestRenderer chestRenderer = BlockRendererDispatcherReflection.getChestRenderer(dispatcher);
							chestRenderer.renderChestBrightness(state.getBlock(), 1.0f);
						default:
							break;
					}
					GlStateManager.popMatrix();
				}
			}
		} catch (Exception e) { LogWriter.error("Error preview builder block", e); }

		GlStateManager.popMatrix();

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0).setSize(125, 208); }
		add(scroll.setPos(guiLeft + 4, guiTop + 4));
		if (selected == null) { return; }
		int x0 = guiLeft + 132;
		int x1 = x0 + 69;
		int y = guiTop + 4;
		addYesNo(3, x1, y, tile.getSchematic() != null && tile.getShow());
		addLabel(3, x0, y + 5, "schematic.preview").setSize(66, 12);

		addLabel(0, x0, y += 21, Component.translatable("schematic.width").append(": ").append("" + selected.getWidth())).setSize(120, 12);
		addLabel(1, x0, y += 11, Component.translatable("schematic.length").append(": ").append("" + selected.getLength())).setSize(120, 12);
		addLabel(2, x0, y += 11, Component.translatable("schematic.height").append(": ").append("" + selected.getHeight())).setSize(120, 12);

		addYesNo(4, x1, y += 14, tile.enabled);
		addLabel(4, x0, y + 5, "gui.enabled").setSize(66, 12);

		addYesNo(7, x1, y += 22, tile.finished);
		addLabel(7, x0, y + 5, "gui.finished").setSize(66, 12);

		addYesNo(8, x1, y += 22, tile.started);
		addLabel(8, x0, y + 5, "gui.started").setSize(66, 12);

		addTextField(9, x1, y += 22, 50, 20, "" + tile.yOffset);
		addLabel(9, x0, y + 5, "gui.yoffset").setSize(66, 12);
		getTextField(9).setMinMaxDefault(-10, 10, 0);

		addButton(5, x1, y += 22, false, tile.rotation, 0, 90, 180, 270)
				.setSize(50, 20);
		addLabel(5, x0, y + 5, "movement.rotation").setSize(66, 12);

		addButton(6, x0 - 1, y += 22, "availability.options")
				.setSize(120, 20)
				.setHoverTexts("builder.hover.availability");
		addButton(10, x0 - 1, y + 22, "schematic.instantBuild")
				.setSize(120, 20);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 3: tile.setDrawSchematic(new SchematicWrapper(selected), ((GuiButtonYesNo) button).getBoolean()); break;
			case 4: tile.enabled = ((GuiButtonYesNo) button).getBoolean(); break;
			case 5: tile.rotation = button.getValue();break;
			case 6: setSubGui(new SubGuiNpcAvailability(tile.availability, this)); break;
			case 7: {
				tile.finished = ((GuiButtonYesNo) button).getBoolean();
				Packets.sendServer(new SPacketSchematicsTileSet(pos, scroll.getSelected()));
				break;
			}
			case 8: tile.started = ((GuiButtonYesNo) button).getBoolean(); break;
			case 10: {
				save();
				ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
					if (agree) {
						Packets.sendServer(new SPacketSchematicsTileBuild(pos, 0, new NBTTagCompound()));
						onClose();
						selected = null;
						tile.setDrawSchematic(null, false);
					}
					else { NoppesUtil.openGUI(player, this); }
				},
						Component.empty(), Component.translatable("schematic.instantBuildText"));
				setScreen(guiYesNo);
				break;
			}
		}
	}

	@Override
	public void save() {
		if (tile != null) {
			if (getTextField(9) != null) { tile.yOffset = getTextField(9).getInteger(); }
			Packets.sendServer(new SPacketSchematicsTileSave(pos, tile.savePartNBT(new NBTTagCompound())));
		}
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		if (compound.hasKey("Width")) {
			final List<IBlockState> states = new ArrayList<>();
			NBTTagList list = compound.getTagList("Data", 10);
			for (int i = 0; i < list.tagCount(); ++i) { states.add(NBTUtil.readBlockState(list.getCompoundTagAt(i))); }
			selected = new ISchematic() {
				@Override
				public IBlockState getBlockState(int i) { return states.get(i); }
				@Override
				public IBlockState getBlockState(int x, int y, int z) { return getBlockState((y * getLength() + z) * getWidth() + x); }
				@Override
				public NBTTagList getEntitys() { return new NBTTagList(); }
				@Override
				public short getHeight() { return compound.getShort("Height"); }
				@Override
				public short getLength() { return compound.getShort("Length"); }
				@Override
				public String getName() { return compound.getString("SchematicName"); }
				@Override
				public NBTTagCompound getNBT() { return null; }
				@Override
				public IPos getOffset() { return BlockPosWrapper.ORIGIN; }
				@Override
				public NBTTagCompound getTileEntity(int i) { return null; }
				@Override
				public int getTileEntitySize() { return 0; }
				@Override
				public short getWidth() { return compound.getShort("Width"); }
				@Override
				public boolean hasEntitys() { return false; }
			};
			SchematicWrapper wrapper = new SchematicWrapper(selected);
			wrapper.rotation = tile.rotation;
			scroll.setSelected(selected.getName());
			if (getButton(3) != null) { tile.setDrawSchematic(wrapper, ((GuiButtonYesNo) getButton(3)).getBoolean()); }
			else { tile.setDrawSchematic(wrapper, tile.getShow()); }
		}
		else { tile.loadPartNBT(compound);}
		initGui();
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (scroll.hasSelected()) {
			Packets.sendServer(new SPacketSchematicsTileSet(pos, scroll.getSelected()));
		}
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

	@Override
	public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
		scroll.setList(dataList);
		if (selected != null) { scroll.setSelected(selected.getName()); }
		initGui();
	}

	@Override
	public void setSelected(String selected) { }

}
