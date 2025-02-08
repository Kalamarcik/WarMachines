package org.example.warmachines.Models;

import org.example.warmachines.havakaradeniz.Deniz;

public class Firkateyn extends Deniz {
    private static final int BASLANGIC_DAYANIKLILIK = 25;
    private static final int VURUS_GUCU = 10;

    public Firkateyn(int seviyePuani) {
        super(BASLANGIC_DAYANIKLILIK, seviyePuani, 5);
    }

    @Override
    public int getVurus() {
        return VURUS_GUCU;
    }

    @Override
    public String getSinif() {
        return "Deniz";
    }
}
