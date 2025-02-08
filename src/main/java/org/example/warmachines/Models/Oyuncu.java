package org.example.warmachines.Models;

import org.example.warmachines.SavasAraci.SavasAraci;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Oyuncu {
    private int oyuncuID;
    private String oyuncuAdi;
    private int skor;
    private List<SavasAraci> kartListesi;
    private List<SavasAraci> secilenKartlar;

    public Oyuncu(int oyuncuID, String oyuncuAdi, int skor) {
        this.oyuncuID = oyuncuID;
        this.oyuncuAdi = oyuncuAdi;
        this.skor = skor;
        this.kartListesi = new ArrayList<>();
        this.secilenKartlar = new ArrayList<>();
    }

    public List<SavasAraci> kartSec(boolean otomatik) {
        secilenKartlar.clear();
        List<SavasAraci> secilebilirKartlar = kartListesi.stream()
                .filter(kart -> kart.getDayaniklilik() > 0)
                .toList();

        if (secilebilirKartlar.size() < 3) {
            return secilenKartlar;
        }

        if (otomatik) {
            return otomatikKartSec(secilebilirKartlar);
        } else {
            return manuelKartSec(secilebilirKartlar);
        }
    }

    private List<SavasAraci> otomatikKartSec(List<SavasAraci> secilebilirKartlar) {
        List<SavasAraci> temp = new ArrayList<>(secilebilirKartlar);
        Collections.shuffle(temp);
        return temp.subList(0, 3);
    }

    private List<SavasAraci> manuelKartSec(List<SavasAraci> secilebilirKartlar) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nElinizdeki kartlar:");

        for (int i = 0; i < secilebilirKartlar.size(); i++) {
            SavasAraci kart = secilebilirKartlar.get(i);
            System.out.printf("%d. %s (Dayanıklılık: %d, Vuruş: %d)%n",
                    i + 1,
                    kart.getClass().getSimpleName(),
                    kart.getDayaniklilik(),
                    kart.getVurus());
        }

        while (secilenKartlar.size() < 3) {
            try {
                System.out.printf("Lütfen %d. kartı seçin (1-%d): ",
                        secilenKartlar.size() + 1,
                        secilebilirKartlar.size());

                int secim = Integer.parseInt(scanner.nextLine()) - 1;

                if (secim >= 0 && secim < secilebilirKartlar.size() &&
                        !secilenKartlar.contains(secilebilirKartlar.get(secim))) {
                    secilenKartlar.add(secilebilirKartlar.get(secim));
                } else {
                    System.out.println("Geçersiz seçim veya kart zaten seçilmiş!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lütfen geçerli bir sayı girin!");
            }
        }

        return secilenKartlar;
    }

    // Getter ve Setter metodları
    public int getOyuncuID() { return oyuncuID; }
    public String getOyuncuAdi() { return oyuncuAdi; }
    public int getSkor() { return skor; }
    public void setSkor(int skor) { this.skor = skor; }
    public List<SavasAraci> getKartListesi() { return kartListesi; }
}