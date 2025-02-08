package org.example.warmachines.havakaradeniz;

import org.example.warmachines.SavasAraci.SavasAraci;

public abstract class Kara extends SavasAraci {
    private int denizVurusAvantaji;

    public Kara(int dayaniklilik, int seviyePuani, int denizVurusAvantaji) {
        super(dayaniklilik, seviyePuani);
        this.denizVurusAvantaji = denizVurusAvantaji;
    }

    public int getDenizVurusAvantaji() {
        return denizVurusAvantaji;
    }

    @Override
    public void durumGuncelle(int saldiriGucu, SavasAraci rakip) {
        setDayaniklilik(getDayaniklilik() - saldiriGucu);
        if (getDayaniklilik() <= 0) {
            rakip.setSeviyePuani(rakip.getSeviyePuani() + Math.max(getSeviyePuani(), 10));
        }
    }
}