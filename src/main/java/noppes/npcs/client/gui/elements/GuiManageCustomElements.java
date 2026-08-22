package noppes.npcs.client.gui.elements;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.*;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.blocks.custom.CustomBlockLiquid;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.blocks.custom.CustomBlockSlab;
import noppes.npcs.blocks.custom.CustomCauldron;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.elements.data.GameElementData;
import noppes.npcs.client.particles.CustomParticleSettings;
import noppes.npcs.client.renderer.obj.ModelBuffer;
import noppes.npcs.client.renderer.obj.ParameterizedModel;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main manager GUI for creating and editing custom elements (Blocks, Items, Particles).
 * Left scroll shows available subtypes for the selected main type.
 * Right scroll shows already existing elements of that main type.
 */
public class GuiManageCustomElements extends GuiBasic implements ICustomScrollListener {

    protected GuiCustomScrollNop scrollSubtypes;
    protected GuiCustomScrollNop scrollElements;

    // 0 = Blocks, 1 = Items, 2 = Particles
    protected int currentType = 0;
    protected final Object[] typeNames = { Component.translatable("soundCategory.block"),
            Component.translatable("stat.itemsButton"),
            Component.translatable("part.particles") };

    // Subtype names per type
    protected final Map<Integer, List<Component>> subtypes = new HashMap<>();
    // Existing element names per type
    protected final Map<Component, GameElementData> existingElements = new HashMap<>();
    // obj particle
    protected ParameterizedModel objParticle = null;

    public GuiManageCustomElements() {
        super();
        setBackground("menubg.png");
        imageWidth = 252;
        imageHeight = 240;
        title = Component.translatable("gui.manage.custom.elements");

        // Initialize subtype lists for each main type
        Component simple = Component.translatable("elements.type.simple");
        subtypes.put(0, Arrays.asList(simple,
                Component.translatable("elements.type.block.liquid"),
                Component.translatable("elements.type.block.chest"),
                Component.translatable("elements.type.block.stairs"),
                Component.translatable("elements.type.block.slab"),
                Component.translatable("elements.type.block.portal"),
                Component.translatable("elements.type.block.door")));
        subtypes.put(1, Arrays.asList(simple,
                Component.translatable("elements.type.item.weapon"),
                Component.translatable("elements.type.item.tool"),
                Component.translatable("elements.type.item.armor"),
                Component.translatable("elements.type.item.shield"),
                Component.translatable("elements.type.item.bow"),
                Component.translatable("elements.type.item.food"),
                Component.translatable("elements.type.item.potion"),
                Component.translatable("elements.type.item.fishing.rod")));
        subtypes.put(2, Arrays.asList(simple,
                Component.translatable("elements.type.particle.obj")));
    }

    @Override
    public void initGui() {
        super.initGui();
        int x0 = guiLeft + 5;
        int x1 = x0 + 122;
        int y = guiTop + 17;
        // Main type selector button (top center)
        addLabel(0, x0, y, "gui.manage.types")
                .setSize(120, 10);
        addLabel(1, x1, y + 18, "gui.manage.elements")
                .setSize(92, 10);
        addButton(0, x0, y += 12, true, currentType, typeNames)
                .setSize(120, 16)
                .layerColor = currentType == 1 ? 0xFFBEE72E : currentType == 2 ? 0xFFE72E97 : 0xFF2EA8E7;
        y += 18;
        // Left scroll: subtypes (creation variants)
        if (scrollSubtypes == null) {
            scrollSubtypes = addScroll(0, false)
                    .disabledSearch();
        }
        add(scrollSubtypes.setSize(120, 166)
                .setUnsortedList(new ArrayList<>(subtypes.get(currentType)))
                .setPos(x0, y));
        if (!scrollSubtypes.hasSelected()) { scrollSubtypes.setSelectedIndex(0); }
        // Right scroll: existing elements
        if (scrollElements == null) {
            scrollElements = addScroll(1, false)
                    .disabledSearch();
            refreshElementsList();
            scrollElements.type = currentType;
        }
        if (scrollElements.type != currentType) {
            refreshElementsList();
            scrollElements.type = currentType;
        }
        add(scrollElements.setSize(120, 166)
                .setPos(x1, y));
        if (!scrollElements.hasSelected()) { scrollElements.setSelectedIndex(0); }
        y += scrollSubtypes.height + 2;
        // Bottom buttons
        addButton(1, x1, y, "gui.add").setSize(59, 20);
        addButton(2, x1 + 61, y, "gui.edit").setSize(59, 20);
        addButton(3, x0, y, "display.hover.X").setSize(59, 20);
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                currentType = button.getValue();
                initGui();
                break;
            } // Cycle main type (Blocks -> Items -> Particles)
            case 1: openEditor(true); break; // create new element
            case 2: openEditor(false); break;  // edit existing element
            case 3: onClose(); break;
            default: break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!hasSubGui()) { drawPreviewSlot(partialTicks); }
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        // Selection changes are handled directly by the scroll component
        refreshElementsList();
        initGui();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
        // Double-clicking an existing element triggers edit
        if (scroll.id == 1 && scroll.hasSelected()) {
            buttonEvent(getButton(2));
        }
    }

    @Override
    public void onClose() {
        NoppesUtil.requestOpenGUI(EnumGuiType.MainMenuGlobal);
    }

    /**
     * Draws the slot background and the preview of the hovered/selected element inside it.
     */
    private void drawPreviewSlot(float partialTicks) {
        int slotS = 28;
        int slotX = scrollElements.getX() + scrollElements.getWidth() - slotS;
        int slotY = scrollElements.getY() - slotS - 2;
        // Draw slot background
        drawRect(slotX, slotY, slotX + slotS, slotY + slotS, 0xFF373737);
        drawRect(slotX + 1, slotY + 1, slotX + slotS - 1, slotY + slotS - 1, 0xFF8B8B8B);
        if (scrollElements != null) {
            ICustomElement select = null;
            int hover = scrollElements.getHover();
            Component display = scrollElements.getNormalSelected();
            if (scrollElements.getHover() >= 0) { display = scrollElements.getNormalList().get(hover); }
            if (existingElements.containsKey(display)) { select = existingElements.get(display).element; }
            if (select != null) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GlStateManager.depthMask(false);
                if (select instanceof Block) {
                    RenderHelper.enableGUIStandardItemLighting();
                    renderBlockInSlot((Block) select, slotX + slotS / 2, slotY + slotS / 2, partialTicks);
                }
                else if (select instanceof CustomParticleSettings) {
                    renderParticleInSlot((CustomParticleSettings) select, slotX, slotY);
                }
                else if (select instanceof Item) {
                    RenderHelper.enableGUIStandardItemLighting();
                    ItemStack stack = new ItemStack((Item) select);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(slotX + slotS / 2.0f, slotY + slotS / 2.0f, 50.0f);
                    GlStateManager.rotate(-30.0f, 1.0f, 0.0f, 0.0f);
                    long time = 5000L;
                    GlStateManager.rotate((System.currentTimeMillis() % time) * 360.0f / time, 0.0f, 1.0f, 0.0f);
                    GlStateManager.scale(16.0f, -16.0f, 16.0f);
                    mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                    GlStateManager.popMatrix();
                }
                RenderHelper.disableStandardItemLighting();
                GlStateManager.depthMask(true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    /**
     * Renders a block inside the preview field using the same matrix setup as SubGuiEditAnimation.
     */
    private void renderBlockInSlot(Block block, int centerX, int centerY, float partialTicks) {
        IBlockState state = block.getDefaultState();

        // GL setup — exactly like in SubGuiEditAnimation.drawWork()
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX, centerY, 0.0f);
        GlStateManager.rotate(210.0f, 1.0f, 0.0f, 0.0f);
        long time = 5000L;
        GlStateManager.rotate((System.currentTimeMillis() % time) * 360.0f / time, 0.0f, 1.0f, 0.0f);
        float scale = 16.0f;
        GlStateManager.scale(scale, scale, scale);
        // Center the block on its origin
        GlStateManager.translate(-0.5f, -0.5f, 0.5f);
        if (block instanceof BlockDoor) { GlStateManager.translate(-0.4f, -0.5f, 0.0f); }
        if (block instanceof CustomBlockPortal || block instanceof CustomBlockLiquid) { GlStateManager.translate(0.0f, 0.0f, -1.0f); }
        ClientEventHandler.renderBlock(minecraft.world, state, player.getPosition(), partialTicks);
        GlStateManager.popMatrix();

        // Restore GL state
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
    }

    /**
     * Renders a particle preview inside the field.
     * For textured particles: draws the texture scaled to fit.
     * For OBJ particles: renders the model rotated.
     */
    private void renderParticleInSlot(CustomParticleSettings particle, int x, int y) {
        String name = NoppesUtilServer.validPath(particle.nbtData.hasKey("Texture", 8) ?
                particle.nbtData.getString("Texture") :
                particle.nbtData.getString("RegistryName"));
        ResourceLocation texture = new ResourceLocation(CustomNpcs.MODID, "textures/particle/" + name + ".png");
        if (particle.nbtData.hasKey("OBJModel", 8)) {
            ResourceLocation obj = new ResourceLocation(CustomNpcs.MODID, "models/particle/" + particle.nbtData.getString("OBJModel") + ".obj");
            if (objParticle == null || !objParticle.modelLocation.equals(obj)) {
                objParticle = ModelBuffer.getParameterizedModel(obj, null, null, false, 0, false);
            }
            if (objParticle != null) {
                float particleScale = particle.nbtData.getFloat("Scale") * 26.0f;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 13.0f, y + 13.0f, 50.0f);
                GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
                long time = 5000L;
                GlStateManager.rotate((System.currentTimeMillis() % time) * 360.0f / time, 0.0f, 1.0f, 0.0f);
                GlStateManager.scale(particleScale, particleScale, particleScale);
                ModelBuffer.render(objParticle);
                GlStateManager.popMatrix();
                texture = null;
            }
        }
        if (texture != null) {
            mc.getTextureManager().bindTexture(texture);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0.0f);
            GlStateManager.scale(0.1015325f, 0.1015325f, 1.0f);
            drawTexturedModalRect(0, 0, 0, 0, 256, 256);
            GlStateManager.popMatrix();
        }
    }

    /**
     * Refills the right scroll with currently loaded custom elements based on currentType.
     */
    private void refreshElementsList() {
        existingElements.clear();
        switch (currentType) {
            case 0: {
                for (ICustomElement element : CustomBlocks.customblocks.keySet()) {
                    if (element instanceof Block &&
                            !(element instanceof CustomCauldron) &&
                            !(element instanceof CustomBlockSlab.CustomBlockSlabDouble) &&
                            element.getElementType() == scrollSubtypes.getSelectedIndex() ) {
                        addElement(element);
                    }
                }
                break;
            } // Blocks
            case 1: {
                for (ICustomElement element : CustomItems.customitems) {
                    if (element instanceof Item && element.getElementType() == scrollSubtypes.getSelectedIndex()) { addElement(element); }
                }
                break;
            } // Items
            case 2: {
                for (CustomParticleSettings particle : CustomParticles.customparticles.values()) {
                    if (particle.getElementType() == scrollSubtypes.getSelectedIndex()) { addElement(particle); }
                }
                break;
            } // Particles
            default: break;
        }
        if (scrollElements != null) { scrollElements.setNormalList(new ArrayList<>(existingElements.keySet())); }
    }

    private void addElement(ICustomElement element) {
        boolean has = false;
        String name = element.getCustomName();
        for (Component c : existingElements.keySet()) {
            if (c.getString().equals(name)) {
                has = true;
                break;
            }
        }
        if (!has) { existingElements.put(Component.literal(name), new GameElementData(element)); }
    }

    private void openEditor(boolean isNew) {
        if (scrollSubtypes.hasSelected() && scrollElements != null) {
            int elementType = scrollSubtypes.getSelectedIndex();
            GameElementData gameData = isNew ? null : existingElements.get(scrollElements.getNormalSelected());
            // TODO: open creation GUI for selectedSubtype under currentType
            switch (currentType) {
                case 1: {
                    switch (elementType) {
                        //case 1: setSubGui(new SubGuiElementItemWeapon(element)); break;
                        //case 2: setSubGui(new SubGuiElementItemTool(element)); break;
                        //case 3: setSubGui(new SubGuiElementItemArmor(element)); break;
                        //case 4: setSubGui(new SubGuiElementItemShield(element)); break;
                        //case 5: setSubGui(new SubGuiElementItemBow(element)); break;
                        //case 6: setSubGui(new SubGuiElementItemFood(element)); break;
                        //case 7: setSubGui(new SubGuiElementItemFood(element)); break;
                        //case 8: setSubGui(new SubGuiElementItemFishingRod(element)); break;
                        //default: setSubGui(new SubGuiElementItemSimple(element)); break;
                    }
                    break;
                } // item
                case 2: {
                    switch (elementType) {
                        //case 1: setSubGui(new SubGuiElementParticleObj(element)); break;
                        //default: setSubGui(new SubGuiElementParticleSimple(element)); break;
                    }
                    break;
                } // particle
                default:  {
                    switch (elementType) {
                        case 0: setSubGui(new SubGuiElementBlockSimple(gameData.element)); break;
                        //case 1: setSubGui(new SubGuiElementBlockLiquid(element)); break;
                        //case 2: setSubGui(new SubGuiElementBlockChest(element)); break;
                        //case 3: setSubGui(new SubGuiElementBlockStairs(element)); break;
                        //case 4: setSubGui(new SubGuiElementBlockSlab(element)); break;
                        //case 5: setSubGui(new SubGuiElementBlockPortal(element)); break;
                        //case 6: setSubGui(new SubGuiElementBlockDoor(element)); break;
                    }
                    break;
                } // block
            }
        }
    }

}