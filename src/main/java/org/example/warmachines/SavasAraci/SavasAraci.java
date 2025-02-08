package org.example.warmachines.SavasAraci;

public abstract class SavasAraci {
    private int dayaniklilik;
    private int seviyePuani;

    public SavasAraci(int dayaniklilik, int seviyePuani) {
        this.dayaniklilik = dayaniklilik;
        this.seviyePuani = seviyePuani;
    }

    public abstract int getVurus();
    public abstract String getSinif();

    public int getDayaniklilik() {
        return dayaniklilik;
    }

    public void setDayaniklilik(int dayaniklilik) {
        this.dayaniklilik = dayaniklilik;
    }

    public int getSeviyePuani() {
        return seviyePuani;
    }

    public void setSeviyePuani(int seviyePuani) {
        this.seviyePuani = seviyePuani;
    }

    public void kartPuaniGoster() {
        System.out.println("Dayaniklilik: " + dayaniklilik);
        System.out.println("Seviye Puani: " + seviyePuani);
    }

    public abstract void durumGuncelle(int saldiriGucu, SavasAraci rakip);
}
