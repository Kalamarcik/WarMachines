package org.example.warmachines.havakaradeniz;


import org.example.warmachines.SavasAraci.SavasAraci;

public abstract class Deniz extends SavasAraci {
    private int havaVurusAvantaji;

    public Deniz(int dayaniklilik, int seviyePuani, int havaVurusAvantaji) {
        super(dayaniklilik, seviyePuani);
        this.havaVurusAvantaji = havaVurusAvantaji;
    }

    public int getHavaVurusAvantaji() {
        return havaVurusAvantaji;
    }

    @Override
    public void durumGuncelle(int saldiriGucu, SavasAraci rakip) {
        setDayaniklilik(getDayaniklilik() - saldiriGucu);
        if (getDayaniklilik() <= 0) {
            rakip.setSeviyePuani(rakip.getSeviyePuani() + Math.max(getSeviyePuani(), 10));
        }
    }
}