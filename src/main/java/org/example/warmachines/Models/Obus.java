package org.example.warmachines.Models;

import org.example.warmachines.havakaradeniz.Kara;

public class Obus extends Kara {
    private static final int BASLANGIC_DAYANIKLILIK = 20;
    private static final int VURUS_GUCU = 10;

    public Obus(int seviyePuani) {
        super(BASLANGIC_DAYANIKLILIK, seviyePuani, 5);
    }

    @Override
    public int getVurus() {
        return VURUS_GUCU;
    }

    @Override
    public String getSinif() {
        return "Kara";
    }
}