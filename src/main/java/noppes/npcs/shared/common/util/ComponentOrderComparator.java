package noppes.npcs.shared.common.util;

import net.minecraft.network.chat.Component;

import java.util.Comparator;

public class ComponentOrderComparator implements Comparator<Object> {

    int compareRight(String a, String b) {
        int bias = 0;
        int ia = 0;
        int ib = 0;
        while(true) {
            char ca = charAt(a, ia);
            char cb = charAt(b, ib);
            if (!Character.isDigit(ca) && !Character.isDigit(cb)) { return bias; }
            if (!Character.isDigit(ca)) { return -1; }
            if (!Character.isDigit(cb)) { return 1; }
            if (ca < cb) {
                if (bias == 0) { bias = -1; }
            } else if (ca > cb) {
                if (bias == 0) { bias = 1; }
            }
            else if (ca == 0) { return bias; }
            ++ia;
            ++ib;
        }
    }

    public int compare(Object o1, Object o2) {
        String a = o1.toString().toLowerCase();
        String b = o2.toString().toLowerCase();
        if (o1 instanceof Component c1) { a = c1.getString().toLowerCase(); }
        if (o2 instanceof Component c2) { b = c2.getString().toLowerCase(); }
        int ia = 0;
        int ib = 0;
        while(true) {
            int nzb = 0;
            int nza = 0;
            char ca = charAt(a, ia);
            char cb;
            for(cb = charAt(b, ib); Character.isSpaceChar(ca) || ca == '0'; ca = charAt(a, ia)) {
                if (ca == '0') { ++nza; }
                else { nza = 0; }
                ++ia;
            }
            while(Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') { ++nzb; }
                else { nzb = 0; }
                ++ib;
                cb = charAt(b, ib);
            }
            int result;
            if (Character.isDigit(ca) && Character.isDigit(cb) && (result = this.compareRight(a.substring(ia), b.substring(ib))) != 0) { return result; }
            if (ca == 0 && cb == 0) { return nza - nzb; }
            if (ca < cb) { return -1; }
            if (ca > cb) { return 1; }
            ++ia;
            ++ib;
        }
    }

    static char charAt(String s, int i) { return i >= s.length() ? '\u0000' : s.charAt(i); }

}
