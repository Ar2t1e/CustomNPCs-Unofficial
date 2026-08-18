package noppes.npcs.client.gui.elements;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.blocks.custom.CustomBlock;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.gui.animation.GuiNpcAnimation;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.ModData;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Sub-GUI for editing simple custom blocks.
 */
public class SubGuiElementBlockSimple extends GuiBasic implements ITextfieldListener {

    protected static boolean showAxis = false;
    protected final boolean isNew;
    protected CustomBlock block;
    public final @Nonnull NBTTagCompound nbtData;

    // Dropdown option arrays
    public static final Object[] SOUND_TYPES = {
            "WOOD", "GROUND", "PLANT", "METAL", "GLASS", "CLOTH",
            "SAND", "SNOW", "LADDER", "ANVIL", "SLIME", "STONE"
    };
    public static final Object[] MATERIALS = {
            "AIR", "GRASS", "GROUND", "WOOD", "IRON", "ANVIL", "WATER", "LAVA",
            "LEAVES", "PLANTS", "VINE", "SPONGE", "CLOTH", "FIRE", "SAND", "CIRCUITS",
            "CARPET", "GLASS", "REDSTONE_LIGHT", "TNT", "CORAL", "ICE", "PACKED_ICE",
            "SNOW", "CRAFTED_SNOW", "CACTUS", "CLAY", "GOURD", "DRAGON_EGG", "PORTAL",
            "CAKE", "WEB", "PISTON", "BARRIER", "STRUCTURE_VOID", "ROCK"
    };
    public static final Object[] RENDER_TYPES = {
            "MODEL", "INVISIBLE", "ENTITYBLOCK_ANIMATED"
    };
    public static final Object[] BLOCK_LAYERS = {
            "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT"
    };

    public SubGuiElementBlockSimple(ICustomElement element) {
        super();
        isNew = element == null;
        if (element instanceof CustomBlock) {
            block = (CustomBlock) element;
            nbtData = block.nbtData != null ? block.nbtData.copy() : ModData.getExampleBlock();
        } else {
            nbtData = ModData.getExampleBlock();
            nbtData.removeTag("-Description");
            block = null;
        }
        setBackground("standardbg.png");
        imageWidth = 420;
        imageHeight = 237;
    }

    @Override
    public void initGui() {
        super.initGui();
        int lId = 0;
        int x0 = guiLeft + 5;
        int x1 = x0 + 125;
        int x2 = x0 + 101;
        int x3 = x0 + 202;
        int y = guiTop + 4;
        addLabel(lId++, x0, y, isNew ? "element.create.block.simple" : "element.edit.block.simple")
                .setCenter(200)
                .setSize(200, 10);
        // RegistryName
        addLabel(lId++, x0, y += 11, "element.registry.name")
                .setSize(200, 10);
        addTextField(1, x0 + 1, y += 12, 198, 12, nbtData.getString("RegistryName"))
                .setMaxStringLength(16)
                .setResourceLocationType(2)
                .setAllowUppercase(false)
                .setHoverTexts("element.hover.registry.name");
        // Hardness
        addLabel(lId++, x0, (y += 16) + 2, "element.hardness")
                .setSize(123, 10);
        addTextField(2, x1 + 1, y, 73, 12, String.valueOf(nbtData.getFloat("Hardness")))
                .setMinMaxDefault(0.0f, Float.MAX_VALUE, nbtData.getFloat("Hardness"))
                .setHoverTexts("element.hover.hardness");
        // Resistance
        addLabel(lId++, x0, (y += 16) + 2, "element.resistance")
                .setSize(123, 10);
        addTextField(3, x1 + 1, y, 73, 12, String.valueOf(nbtData.getFloat("Resistance")))
                .setMinMaxDefault(0.0f, Float.MAX_VALUE, nbtData.getFloat("Resistance"))
                .setHoverTexts("element.hover.resistance");
        // LightLevel
        addLabel(lId++, x0, (y += 16) + 2, "element.light.level")
                .setSize(123, 10);
        int f0 = nbtData.hasKey("LightLevel", 5) ? (int) (nbtData.getFloat("LightLevel") * 15.0f) : 0;
        addTextField(4, x1 + 1, y, 73, 12, f0)
                .setMinMaxDefault(0.0f, 15.0f, f0)
                .setHoverTexts("element.hover.light.level");
        // SoundType
        addLabel(lId++, x0, (y += 15) + 2, "element.sound.type")
                .setSize(123, 10);
        addButton(5, x1, y, true,
                indexOf(SOUND_TYPES, nbtData.hasKey("SoundType", 8) ? nbtData.getString("SoundType") : "STONE"), SOUND_TYPES)
                .setSize(75, 14)
                .setHoverTexts("element.hover.sound.type");
        // Material
        addLabel(lId++, x0, (y += 16) + 2, "element.material")
                .setSize(123, 10);
        addButton(6, x1, y, true,
                indexOf(MATERIALS, nbtData.hasKey("Material", 8) ? nbtData.getString("Material") : "STONE"), MATERIALS)
                .setSize(75, 14)
                .setHoverTexts("element.hover.material");
        // BlockRenderType
        addLabel(lId++, x0, (y += 16) + 2, "element.render.type")
                .setSize(123, 10);
        addButton(7, x1, y, true,
                indexOf(RENDER_TYPES, nbtData.hasKey("BlockRenderType", 8) ? nbtData.getString("BlockRenderType") : "MODEL"), RENDER_TYPES)
                .setSize(75, 14)
                .setHoverTexts("element.hover.render.type");
        // BlockLayer
        addLabel(lId++, x0, (y += 16) + 2, "element.block.layer")
                .setSize(123, 10);
        addButton(8, x1, y, true,
                indexOf(BLOCK_LAYERS, nbtData.hasKey("BlockLayer", 8) ? nbtData.getString("BlockLayer") : "SOLID"), BLOCK_LAYERS)
                .setSize(75, 14)
                .setHoverTexts("element.hover.block.layer");
        // IsLadder
        addLabel(lId++, x0, (y += 16) + 2, "element.is.ladder")
                .setSize(67, 10);
        addYesNo(9, x2 - 32, y, nbtData.getBoolean("IsLadder"))
                .setSize(30, 14)
                .setHoverTexts("element.hover.is.ladder");
        // IsPassable
        addLabel(lId++, x2, y + 2, "element.is.passable")
                .setSize(67, 10);
        addYesNo(10, x3 - 32, y, nbtData.getBoolean("IsPassable"))
                .setSize(30, 14)
                .setHoverTexts("element.hover.is.passable");
        // IsOpaqueCube
        addLabel(lId++, x0, (y += 16) + 2, "element.is.opaque.cube")
                .setSize(67, 10);
        addYesNo(11, x2 - 32, y, nbtData.hasKey("IsOpaqueCube") && nbtData.getBoolean("IsOpaqueCube"))
                .setSize(30, 14)
                .setHoverTexts("element.hover.is.opaque.cube");
        // IsFullCube
        addLabel(lId++, x2, y + 2, "element.is.full.cube")
                .setSize(67, 10);
        addYesNo(12, x3 - 32, y, nbtData.hasKey("IsFullCube") && nbtData.getBoolean("IsFullCube"))
                .setSize(30, 14)
                .setHoverTexts("element.hover.is.full.cube");
        // ShowInCreative
        addLabel(lId++, x0, (y += 16) + 2, "element.show.in.creative")
                .setSize(67, 10);
        addYesNo(13, x2 - 32, y, !nbtData.hasKey("ShowInCreative") || nbtData.getBoolean("ShowInCreative"))
                .setSize(30, 14)
                .setHoverTexts("element.hover.show.in.creative");
        // Edit AABB
        addButton(14, x0, y += 16, "element.edit.aabb")
                .setSize(99, 14);
        // Edit property
        addButton(15, x2, y, "element.edit.property")
                .setSize(99, 14);
        // Cancel
        addButton(66, x0, y += 16, "gui.cancel")
                .setSize(99, 14);
        // Save
        addButton(16, guiLeft + imageWidth - 104, y, "gui.save")
                .setSize(99, 14);
        // preview world
        y = guiTop + 4;
        addLabel(100, guiLeft + imageWidth - 84, y, "element.preview.world")
                .setSize(60, 10);
        addCheckBox(17, guiLeft + imageWidth - 27, y, "", "", GuiNpcAnimation.backColor == 0xFF000000)
                .setSize(10, 10)
                .setHoverTexts("animation.hover.color");
        addCheckBox(18, guiLeft + imageWidth - 15, y, "", "", showAxis)
                .setSize(10, 10)
                .setHoverTexts("element.hover.show.axis");
        // preview slot
        addLabel(lId, guiLeft + imageWidth - 146, y, "element.preview.slot")
                .setSize(58, 10);
    }

    private int indexOf(Object[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 5: nbtData.setString("SoundType", SOUND_TYPES[button.getValue()].toString()); break;
            case 6: nbtData.setString("Material", MATERIALS[button.getValue()].toString()); break;
            case 7: nbtData.setString("BlockRenderType", RENDER_TYPES[button.getValue()].toString()); break;
            case 8: nbtData.setString("BlockLayer", BLOCK_LAYERS[button.getValue()].toString()); break;
            case 9: nbtData.setBoolean("IsLadder", button.getValue() == 1); break;
            case 10: nbtData.setBoolean("IsPassable", button.getValue() == 1); break;
            case 11: nbtData.setBoolean("IsOpaqueCube", button.getValue() == 1); break;
            case 12: nbtData.setBoolean("IsFullCube", button.getValue() == 1); break;
            case 13: nbtData.setBoolean("ShowInCreative", button.getValue() == 1); break;
            case 14: setSubGui(new SubGuiEditAABB(nbtData)); break;
            case 15: setSubGui(new SubGuiEditProperty(nbtData)); break;
            case 16: saveData(); onClose(); break;
            case 17: {
                GuiNpcAnimation.backColor = (GuiNpcAnimation.backColor == 0xFF000000 ? 0xFFFFFFFF : 0xFF000000);
                break;
            } // back color
            case 18: showAxis = !showAxis; break;
            case 66: onClose(); break;
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 1: nbtData.setString("RegistryName", textField.getValue()); break;
            case 2: nbtData.setFloat("Hardness", textField.getFloat()); break;
            case 3: nbtData.setFloat("Resistance", textField.getFloat()); break;
            case 4: nbtData.setFloat("LightLevel", ValueUtil.correctFloat(textField.getFloat(), 0.0f, 15.0f) / 15.0f); break;
            default: break;
        }
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();
        drawVerticalLine(guiLeft + 207, guiTop + 4, guiTop + imageHeight - 5, 0xFF373737);
        if (!hasSubGui()) {
            drawBlockPreview(minecraft.getRenderPartialTicks());
            drawSlotPreview();
        }
    }

    /**
     * Renders the block as it appears in the world.
     */
    private void drawBlockPreview(float partialTicks) {
        int slotS = 80;
        int slotX = guiLeft + imageWidth - 85;
        int slotY = guiTop + 14;
        float centerX = slotX + slotS / 2.0f + 1.0f;
        float centerY = slotY + slotS / 2.0f + 1.0f;
        RenderHelper.enableGUIStandardItemLighting();
        drawRect(slotX, slotY, slotX + slotS, slotY + slotS, GuiNpcAnimation.backColor == 0xFF000000 ?
                new Color(0xFFF080F0).getRGB() :
                new Color(0xFFF020F0).getRGB());
        drawRect(slotX + 1, slotY + 1, slotX + slotS - 1, slotY + slotS - 1, GuiNpcAnimation.backColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        if (block == null) { return; }
        IBlockState state = block.getDefaultState();
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sw = new ScaledResolution(mc);
        int c = sw.getScaledWidth() < mc.displayWidth ? (int) Math.round((double) mc.displayWidth / sw.getScaledWidth()) : 1;
        GL11.glScissor((slotX + 1) * c, mc.displayHeight - (slotY + slotS - 1) * c, (slotS - 2) * c, (slotS - 2) * c);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.depthMask(false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX - 1.25f, centerY - 1.0f, 50.0f);
        GlStateManager.rotate(-30.0f, 1.0f, 0.0f, 0.0f);
        long time = 10000L;
        GlStateManager.rotate((System.currentTimeMillis() % time) * 360.0f / time, 0.0f, 1.0f, 0.0f);
        float scale = slotS / 1.75f;
        GlStateManager.scale(-scale, -scale, -scale);
        GlStateManager.translate(-0.5f, -0.5f, 0.5f);
        ClientEventHandler.renderBlock(minecraft.world, state, player.getPosition(), partialTicks);
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.depthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.disableStandardItemLighting();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    /**
     * Renders the block as it appears in an inventory / GUI slot.
     */
    private void drawSlotPreview() {
        int slotS = 18;
        int slotX = guiLeft + imageWidth - 148;
        int slotY = guiTop + 14;
        int centerX = slotX + slotS / 2;
        int centerY = slotY + slotS / 2;

        // Draw slot background
        mc.getTextureManager().bindTexture(RESOURCE_SLOT);
        drawTexturedModalRect(slotX, slotY, 0, 0, slotS, slotS);

        if (block == null) { return; }
        RenderHelper.enableGUIStandardItemLighting();
        ItemStack stack = new ItemStack(block);
        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX, centerY, 50.0f);
        GlStateManager.rotate(-30.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(10.0f, -10.0f, 10.0f);
        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }

    private void saveData() {

    }

}