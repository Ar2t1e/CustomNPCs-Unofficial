package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;

public class ConfirmScreen extends GuiScreen {

    protected static final ITextComponent GUI_YES = Component.translatable("gui.yes");
    protected static final ITextComponent GUI_NO = Component.translatable("gui.no");
    @FunctionalInterface
    public interface BooleanConsumer {
        void accept(boolean value);

        default BooleanConsumer andThen(BooleanConsumer after) {
            java.util.Objects.requireNonNull(after);
            return (t) -> {
                accept(t);
                after.accept(t);
            };
        }
    }

    protected final ITextComponent message;
    protected final ITextComponent title;
    protected final ITextComponent yesButton;
    protected final ITextComponent noButton;
    protected String confirmButtonText;
    protected String cancelButtonText;
    protected int parentButtonClickedId;
    protected final BooleanConsumer callback;

    public ConfirmScreen(BooleanConsumer callbackIn, ITextComponent messageLine1In, ITextComponent messageLine2In) {
        this(callbackIn, messageLine1In, messageLine2In, GUI_YES, GUI_NO);
    }

    public ConfirmScreen(BooleanConsumer callbackIn, ITextComponent messageLine1In, ITextComponent messageLine2In, ITextComponent yesButtonIn, ITextComponent noButtonIn) {
        callback = callbackIn;
        title = messageLine1In;
        message = messageLine2In;
        yesButton = yesButtonIn;
        noButton = noButtonIn;
    }

}
