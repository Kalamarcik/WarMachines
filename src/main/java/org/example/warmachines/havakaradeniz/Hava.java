package org.example.warmachines.havakaradeniz;

import org.example.warmachines.SavasAraci.SavasAraci;

public abstract class Hava extends SavasAraci {
    private int karaVurusAvantaji;

    public Hava(int dayaniklilik, int seviyePuani, int karaVurusAvantaji) {
        super(dayaniklilik, seviyePuani);
        this.karaVurusAvantaji = karaVurusAvantaji;
    }

    public int getKaraVurusAvantaji() {
        return karaVurusAvantaji;
    }

    @Override
    public void durumGuncelle(int saldiriGucu, SavasAraci rakip) {
        setDayaniklilik(getDayaniklilik() - saldiriGucu);
        if (getDayaniklilik() <= 0) {
            rakip.setSeviyePuani(rakip.getSeviyePuani() + Math.max(getSeviyePuani(), 10));
        }
    }
}