package noppes.npcs.api.gui;

import java.util.List;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;

public interface IComponentsWrapper {

    IButton addButton(@ParamName("id") int id, @ParamName("y") String label, @ParamName("x") int x, @ParamName("y") int y);

    IButton addButton(@ParamName("id") int id, @ParamName("y") String label, @ParamName("x") int x, @ParamName("y") int y,
                      @ParamName("width") int width, @ParamName("height") int height);

    IButtonList addButtonList(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y,
                              @ParamName("width") int width, @ParamName("height") int height);

    IButton addTexturedButton(@ParamName("id") int id, @ParamName("y") String label,@ParamName("x")  int x, @ParamName("y") int y,
                              @ParamName("width") int width, @ParamName("height") int height, @ParamName("texture") String texture);

    IButton addTexturedButton(@ParamName("id") int id, @ParamName("y") String label, @ParamName("x") int x, @ParamName("y") int y,
                              @ParamName("width") int width, @ParamName("height") int height,
                              @ParamName("texture") String texture, @ParamName("textureX") int textureX, @ParamName("textureY") int textureY);

    ILabel addLabel(@ParamName("id") int id, @ParamName("label") String label, @ParamName("x") int x, @ParamName("y") int y,
                    @ParamName("width") int width, @ParamName("height") int height);

    ILabel addLabel(@ParamName("id") int id, @ParamName("label") String label, @ParamName("x") int x, @ParamName("y") int y,
                    @ParamName("width") int width, @ParamName("height") int height, @ParamName("y") int color);

    ITextField addTextField(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y,
                            @ParamName("width") int width, @ParamName("height") int height);

    ITextArea addTextArea(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y,
                          @ParamName("width") int width, @ParamName("height") int height);

    IScroll addScroll(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y,
                      @ParamName("width") int width, @ParamName("height") int height, @ParamName("list") String... list);

    ISlider addSlider(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y,
                      @ParamName("width") int width, @ParamName("height") int height, @ParamName("format") String format);

    IEntityDisplay addEntityDisplay(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y, @ParamName("entity") IEntity<?> entity);

    IColoredLine addColoredLine(@ParamName("id") int id, @ParamName("xStart") int xStart, @ParamName("yStart") int yStart,
                                @ParamName("xEnd") int xEnd, @ParamName("yEnd") int yEnd,
                                @ParamName("color") int color, @ParamName("thickness") float thickness);

    IItemRenderer addItemRenderer(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y,
                                  @ParamName("width") int width, @ParamName("height") int height, @ParamName("stack") IItemStack stack);

    IAssetsSelector addAssetsSelector(@ParamName("id") int id, @ParamName("y") int x, @ParamName("y") int y,
                                      @ParamName("width") int width, @ParamName("height") int height);

    ITexturedRect addTexturedRect(@ParamName("id") int id, @ParamName("texture") String texture,
                                  @ParamName("x") int x, @ParamName("y") int y, @ParamName("width") int width, @ParamName("height") int height);

    ITexturedRect addTexturedRect(@ParamName("id") int id, @ParamName("texture") String texture,
                                  @ParamName("x") int x, @ParamName("y") int y,
                                  @ParamName("width") int width, @ParamName("height") int height,
                                  @ParamName("textureX") int textureX, @ParamName("textureY") int textureY);

    List<ICustomGuiComponent> getComponents();

    ICustomGuiComponent getComponent(@ParamName("componentID") int componentID);

    void addComponent(@ParamName("component") ICustomGuiComponent component);

    void removeComponent(@ParamName("componentID") int componentID);

    List<IItemSlot> getSlots();

    List<IItemSlot> getPlayerSlots();

    IItemSlot addItemSlot(@ParamName("x") int x, @ParamName("y") int y);

    IItemSlot addItemSlot(@ParamName("x") int x, @ParamName("y") int y, @ParamName("stack") IItemStack stack);

    void removeItemSlot(@ParamName("slot") IItemSlot slot);

    /** @deprecated */
    @Deprecated
    void showPlayerInventory(@ParamName("x") int x, @ParamName("y") int y);

    IItemSlot[] showPlayerInventory(@ParamName("x") int x, @ParamName("y") int y, @ParamName("full") boolean full);

    // New from Unofficial (BetaZavr)
    CustomGuiTextFieldWrapper getTextField(int id);

    CustomGuiSliderWrapper getSlider(int id);

}
