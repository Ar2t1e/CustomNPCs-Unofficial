package noppes.npcs.api;

public interface ICustomElement {

    String getCustomName();

    INbt getCustomNbt();

    /**
     * @return 0: Simple
     * 1: Liquid
     * 2: Chest
     * 3: Stairs
     * 4: Slab
     * 5: Portal
     * 6: Door
     */
    int getElementType();

    boolean showInCreative();

}
