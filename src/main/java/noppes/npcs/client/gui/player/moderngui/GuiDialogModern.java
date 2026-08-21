package noppes.npcs.client.gui.player.moderngui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDialogSelected;
import noppes.npcs.packets.server.SPacketQuestCompletionCheckAll;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class GuiDialogModern
        extends GuiNPCInterface
        implements IGuiClose {

    public static final ResourceLocation DECOMPOSED = new ResourceLocation(CustomNpcs.MODID, "textures/gui/dialog_menu_decomposed.png");

    private List<Integer> options = new ArrayList<>();
    private boolean isGrabbed = false;
    private int selected = -1;
    private Dialog dialog;
    // Display
    protected final EntityNPCInterface dialogNpc;
    protected float wScale = 1.0F;
    protected float hScale = 1.0F;

    public GuiDialogModern(EntityNPCInterface npc, Dialog dialogIn) {
        super(npc);
        imageHeight = 238;

        dialog = dialogIn;
        appendDialog(dialog);

        if (npc instanceof EntityDialogNpc) { dialogNpc = null; }
        else { dialogNpc = Util.instance.copyToGUI(npc, minecraft.world, false); }
    }

    @Override
    public void initGui() {
        super.initGui();
        isGrabbed = false;
        grabMouse(dialog.showWheel);
        guiTop = height - imageHeight;

        wScale = (float) width / 960.0F;
        hScale = (float) height / 509.0F;
    }

    @Override
    public void drawDefaultBackground() {
        drawGradientRect(0, 0, width, height, 0x66000000, 0x66000000);
        if (!dialog.hideNPC && dialogNpc != null) {
            drawNpc(dialogNpc,
                    -210 + (int) (300.0F * (1.0F - wScale)),
                    350 - (int)(100.0F * (1.0F - hScale)),
                    9.5F * hScale,
                    -10, 0,
                    0);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int textBlockWidth = (int)(700.0 * wScale);
        int lineCount = getLineCount(dialog.text, textBlockWidth);
        int gap = Math.max(16, Math.min((int)(2.6f * (float)lineCount), 32));
        int textPartHeight = 26 + lineCount * ClientProxy.Font.height(null) + 2 * gap;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.5, 2000.0D);
        drawGradientRect(0, height - textPartHeight, width, height, 0x99000000, 0x99000000);
        drawLine(23, height - textPartHeight + 23, width - 23);

        GlStateManager.scale(1.5f, 1.5f, 1.0f);
        font.drawString(npc.getDisplayName().getFormattedText(), 31, (int)((double)(height - textPartHeight + 5) / 1.5), -1);
        GlStateManager.scale(0.6666667f, 0.6666667f, 1.0f);
        drawTextBlock(dialog.text, (width - textBlockWidth) / 2, height - textPartHeight + 23 + 3 + gap, textBlockWidth);
        selected = -1;
        GlStateManager.scale(wScale, wScale, wScale);

        int accumulatedHeight = 0;
        for (int i = 0; i < options.size(); ++i) {
            int optionNum = options.get(i);
            DialogOption option = dialog.options.get(optionNum);
            int optionHeight = 220 + accumulatedHeight;
            String[] titleLines = option.title.split("\\\\n");
            int optionBackHeight = titleLines.length * 9 + (titleLines.length + 1) * 2;
            if ((double)mouseX >= 723.0 * wScale && (double)mouseX <= 946.0 * wScale && (double)mouseY >= (double)optionHeight * wScale && (double)mouseY <= (double)(optionHeight + 13) * wScale) {
                selected = i;
            }
            GlStateManager.enableBlend();
            minecraft.getTextureManager().bindTexture(DECOMPOSED);
            drawTexturedModalRect(723, optionHeight,0, i == selected ? optionBackHeight : 0, 223, optionBackHeight);
            GlStateManager.disableBlend();
            if (getQuestByOptionId(optionNum) != null) {
                font.drawString("!", 727, optionHeight + 3, 7792731);
            } else {
                font.drawString(">", 727, optionHeight + 3, -1);
            }
            int lineOffset = 0;
            for (String line : titleLines) {
                if (line.isEmpty()) continue;
                font.drawString(Component.translatable(line).getFormattedText(), 735, optionHeight + 3 + lineOffset, option.optionColor);
                lineOffset += 12;
            }
            accumulatedHeight += 19 + lineOffset;
        }
        GlStateManager.popMatrix();
    }

    public Quest getQuestByOptionId(int id) {
        DialogOption option = dialog.options.get(id);
        if (option != null) {
            Dialog d = option.getDialog(player);
            if (d != null && d.hasQuest()) { return d.getQuest(); }
        }
        return null;
    }

    public void drawLine(int x, int y, int width) {
        if (npc.display.getLineColors() == null || npc.display.getLineColors().length != 3) {
            npc.display.setLineColors(0xFF8D3800, 0xFFFEA53B, 0xFFAE5301);
        }
        drawRect(x, y, width, y + 1, npc.display.getLineColors()[0]);
        drawRect(x, y + 1, width, y + 2, npc.display.getLineColors()[1]);
        drawRect(x, y + 2, width, y + 3, npc.display.getLineColors()[2]);
    }

    @Override
    public boolean keyPressed(char typedChar, int keyCode) {
        if (isEnterKey(keyCode) && (selected == -1 && options.isEmpty() || selected >= 0)) {
            handleDialogSelection();
        }
        if (closeOnEsc && (isEscKey(keyCode) || isInventoryKey(keyCode))) {
            Packets.sendServer(new SPacketDialogSelected(dialog.id, -1));
            onClose();
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if ((selected == -1 && options.isEmpty() || selected >= 0) && mouseButton == 0) {
            handleDialogSelection();
        }
        return super.mouseClicked(mouseX / wScale, mouseY / wScale, mouseButton);
    }

    @Override
    public void setClose(NBTTagCompound data) { grabMouse(false); }

    private void handleDialogSelection() {
        int optionId = -1;
        if (dialog.showWheel) { optionId = selected; }
        else if (!options.isEmpty()) { optionId = options.get(selected); }
        if (getQuestByOptionId(optionId) == null) { Packets.sendServer(new SPacketDialogSelected(dialog.id, optionId)); }
        else { minecraft.displayGuiScreen(new GuiQuestModern(npc, getQuestByOptionId(optionId), dialog, optionId)); }
        if (dialog != null && !dialog.notHasOtherOptions() && !options.isEmpty()) {
            DialogOption option = dialog.options.get(optionId);
            if (option != null && option.optionType == OptionType.DIALOG_OPTION) { NoppesUtil.clickSound(); }
            else if (closeOnEsc) { onClose(); }
        }
        else if (closeOnEsc) { onClose(); }
    }

    @Override
    public void save() {
        grabMouse(false);
        Packets.sendServer(new SPacketQuestCompletionCheckAll());
    }

    public void drawTextBlock(String text, int x, int y, int width) {
        TextBlockClient block = new TextBlockClient((ICommandSender) null, text, width, -1, null, player, npc);
        int count = 0;
        for (Component line : block.lines) {
            int height = y + count * ClientProxy.Font.height(null);
            font.drawString(line.getFormattedText(), x + (width - font.getStringWidth(line.getFormattedText())) / 2, height, -1);
            ++count;
        }
    }

    public int getLineCount(String text, int width) {
        TextBlockClient block = new TextBlockClient((ICommandSender) null, text, width, -1, null, player, npc);
        return block.lines.size();
    }

    public void appendDialog(Dialog dialogIn) {
        closeOnEsc = !dialog.disableEsc;
        dialog = dialogIn;
        options = new ArrayList<>();
        MusicController.Instance.stopSound(null, SoundCategory.VOICE);
        if (dialog.sound != null) {
            CustomNPCsScheduler.runTack(() ->
                            MusicController.Instance.playSoundDialog(SoundCategory.VOICE, dialog.sound, npc.getPosition(), 1.0F, 1.0F),
                    50);
        }
        for (int slot : dialog.options.keySet()) {
            DialogOption option = dialog.options.get(slot);
            if (option == null || !option.isAvailable(player)) continue;
            options.add(slot);
        }
        grabMouse(dialog.showWheel);
    }

    public void grabMouse(boolean grab) {
        if (grab && !isGrabbed) {
            Mouse.setCursorPosition(0, 0);
            Mouse.setGrabbed(false);
            mc.mouseHelper.mouseXYChange();
            isGrabbed = true;
        }
        else if (!grab && isGrabbed) {
            mc.mouseHelper.ungrabMouseCursor();
            mc.mouseHelper.mouseXYChange();
            isGrabbed = false;
        }
    }

}