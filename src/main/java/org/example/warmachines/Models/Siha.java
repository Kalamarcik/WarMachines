package org.example.warmachines.Models;

import org.example.warmachines.havakaradeniz.Hava;

public class Siha extends Hava {
    private static final int BASLANGIC_DAYANIKLILIK = 15;
    private static final int VURUS_GUCU = 10;
    private int denizVurusAvantaji;

    public Siha(int seviyePuani) {
        super(BASLANGIC_DAYANIKLILIK, seviyePuani, 10);
        this.denizVurusAvantaji = 10;
    }

    @Override
    public int getVurus() {
        return VURUS_GUCU;
    }

    @Override
    public String getSinif() {
        return "Hava";
    }

    public int getDenizVurusAvantaji() {
        return denizVurusAvantaji;
    }
}