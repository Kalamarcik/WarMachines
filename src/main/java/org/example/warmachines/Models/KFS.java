package org.example.warmachines.Models;

import org.example.warmachines.havakaradeniz.Kara;

public class KFS extends Kara {
    private static final int BASLANGIC_DAYANIKLILIK = 10;
    private static final int VURUS_GUCU = 10;
    private int havaVurusAvantaji;

    public KFS(int seviyePuani) {
        super(BASLANGIC_DAYANIKLILIK, seviyePuani, 10);
        this.havaVurusAvantaji = 20;
    }

    @Override
    public int getVurus() {
        return VURUS_GUCU;
    }

    @Override
    public String getSinif() {
        return "Kara";
    }

    public int getHavaVurusAvantaji() {
        return havaVurusAvantaji;
    }
}