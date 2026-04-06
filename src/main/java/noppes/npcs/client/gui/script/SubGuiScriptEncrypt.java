package noppes.npcs.client.gui.script;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;

import javax.annotation.Nonnull;

public class SubGuiScriptEncrypt extends GuiNPCInterface {

    public String path;
    public String ext;
    public boolean onlyTab;
    public boolean send;

    public SubGuiScriptEncrypt(String pathStr, String extStr) {
        super();
        setBackground("smallbg.png");
        closeOnEsc = true;
        imageWidth = 176;
        imageHeight = 80;

        onlyTab = true;
        pathStr = pathStr.replaceAll("\\\\", "/");
        if (pathStr.contains("./")) { pathStr = pathStr.substring(pathStr.indexOf("./")); }
        path = pathStr + "/";
        ext = extStr.replace(".", ".p");
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                if (button instanceof GuiCheckBoxNop checkBox) {
                    onlyTab = checkBox.selected();
                }
                break;
            }
            case 1: send = true; onClose(); break;
            case 66: onClose(); break;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.pose().translate(0.0f, 0.0f, 1.0f);
        renderBackground(graphics);
        if (getButton(1) != null && getTextField(0) != null) {
            getButton(1).setIsEnabled(!getTextField(0).getValue().isEmpty());
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        super.init();
        int x = guiLeft + 5;
        int y = guiTop + 14;
        addLabel(0, x + 2, y - 10, Component.translatable("gui.path", ":"));
        addTextField(0, x, y, 166, 20, "default")
                .setHoverTexts(Component.translatable("encrypt.hover.path", path + "default" + ext));
        getTextField(0).prohibitedSpecialChars = GuiTextFieldNop.filePath;
        addCheckBox(0, x + 1, y += 22, "encrypt.only.tab", "encrypt.all.scripts", onlyTab)
                .setSize(164, 16)
                .setHoverTexts("encrypt.hover.type." + onlyTab);
        addButton(66, x, y += 20, "gui.back")
                .setSize(82, 20)
                .setHoverTexts("hover.back");
        addButton(1, x + 84, y, "gui.encrypt")
                .setSize(82, 20)
                .setHoverTexts("encrypt.hover.encrypt");
    }
}
