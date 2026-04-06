package noppes.npcs.api.constants;

public enum GuiComponentType {

   COLORED_LINE(11),
   TEXTURED_RECT(2),
   ITEM_SLOT(5),
   LABEL(1),
   TIMER(13), // New from Unofficial (BetaZavr)
   BUTTON(0),
   TOP_BUTTON(14), // New from Unofficial (BetaZavr)
   SIDE_BUTTON(15),
   TEXT_FIELD(3),
   TEXT_AREA(6),
   SCROLL(4),
   BUTTON_LIST(7),
   SLIDER(8),
   ENTITY_DISPLAY(9),
   ITEM_RENDERER(12),
   ASSETS_SELECTOR(10),
   EXTRA(16);

   final int type;

   GuiComponentType(int t) { type = t; }

   public int get() { return type; }

}
