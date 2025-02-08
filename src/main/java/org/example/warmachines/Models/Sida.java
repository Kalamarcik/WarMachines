package org.example.warmachines.Models;

import org.example.warmachines.havakaradeniz.Deniz;

public class Sida extends Deniz {
    private static final int BASLANGIC_DAYANIKLILIK = 15;
    private static final int VURUS_GUCU = 8;
    private int karaVurusAvantaji;

    public Sida(int seviyePuani) {
        super(BASLANGIC_DAYANIKLILIK, seviyePuani, 10);
        this.karaVurusAvantaji = 10;
    }

    @Override
    public int getVurus() {
        return VURUS_GUCU;
    }

    @Override
    public String getSinif() {
        return "Deniz";
    }

    public int getKaraVurusAvantaji() {
        return karaVurusAvantaji;
    }
}