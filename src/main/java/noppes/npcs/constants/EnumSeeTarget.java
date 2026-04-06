package noppes.npcs.constants;

public enum EnumSeeTarget {
    NORMAL, // vanilla
    DEAF, // a direct view is necessary
    WARY, // a direct view is necessary + senses if the target is close
    CALM, // a direct view is necessary + feels the noise around
    REALISTIC, // a direct view is necessary + feels the noise around + senses if the target is close
    BLIND, // only sound
    NONE // there are no obstacles
}
