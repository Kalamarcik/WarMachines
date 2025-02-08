package org.example.warmachines.Models;

import org.example.warmachines.havakaradeniz.Hava;

public class Ucak extends Hava {
    private static final int BASLANGIC_DAYANIKLILIK = 20;
    private static final int VURUS_GUCU = 10;

    public Ucak(int seviyePuani) {
        super(BASLANGIC_DAYANIKLILIK, seviyePuani, 10);
    }

    @Override
    public int getVurus() {
        return VURUS_GUCU;
    }

    @Override
    public String getSinif() {
        return "Hava";
    }
}