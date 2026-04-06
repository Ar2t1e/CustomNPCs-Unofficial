package noppes.npcs.api.constants;

public enum GuiComponentType {

	BUTTON(0, true),
	LABEL(1, false),
	TEXTURED_RECT(2, false),
	TEXT_FIELD(3, true),
	SCROLL(4, true),
	ITEM_SLOT(5, false),
	TEXT_AREA(6, false),
	BUTTON_LIST(7, true),
	SLIDER(8, true),
	ENTITY_DISPLAY(9, false),
	ASSETS_SELECTOR(10, false),
	COLORED_LINE(11, false),
	ITEM_RENDERER(12, false),
	TIMER(13, false), // New from Unofficial (BetaZavr)
	TOP_BUTTON(14, false), // New from Unofficial (BetaZavr)
	SIDE_BUTTON(15, false),
	EXTRA(16, false);

	final int type;

	final boolean isSelectable;

	GuiComponentType(int t, boolean isSelectableIn) {
		type = t;
		isSelectable = isSelectableIn;
	}

	public int get() { return type; }

	public boolean isSelectable() { return isSelectable; }

}
