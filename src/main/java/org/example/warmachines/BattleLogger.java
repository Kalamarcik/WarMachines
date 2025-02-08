package org.example.warmachines;
import org.example.warmachines.SavasAraci.SavasAraci;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class BattleLogger {
    private static final String LOG_FILE_PATH = "battle_results.txt";

    public void logGameStart() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, false))) {
            writer.println("\n==================== Yeni Oyun Başladı ====================");
            writer.println("Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            writer.println("=========================================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logBattleMove(int moveNumber,
                              List<SavasAraci> playerCards,
                              List<SavasAraci> computerCards,
                              int playerScore,
                              int computerScore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.println("\n==================== Hamle " + moveNumber + " ====================");

            // Oyuncu Kartları Detayları
            writer.printf("%-20s %-10s %-10s %-10s\n", "Kart İsmi", "Vuruş", "Dayanıklılık", "Seviye Puanı");
            writer.println("-------------------------------------------------------------");
            for (SavasAraci card : playerCards) {
                writer.printf("%-20s %-10d %-10d %-10d\n",
                        card.getClass().getSimpleName(),
                        card.getVurus(),
                        card.getDayaniklilik(),
                        card.getSeviyePuani());
            }

            // Bilgisayar Kartları Detayları
            writer.println("\nBilgisayar Kartları:");
            writer.printf("%-20s %-10s %-10s %-10s\n", "Kart İsmi", "Vuruş", "Dayanıklılık", "Seviye Puanı");
            writer.println("-------------------------------------------------------------");
            for (SavasAraci card : computerCards) {
                writer.printf("%-20s %-10d %-10d %-10d\n",
                        card.getClass().getSimpleName(),
                        card.getVurus(),
                        card.getDayaniklilik(),
                        card.getSeviyePuani());
            }

            // Güncel Skorlar
            writer.println("\nMevcut Skorlar:");
            writer.println("Oyuncu Skoru: " + playerScore);
            writer.println("Bilgisayar Skoru: " + computerScore);

            // Kart Performans Analizi
            SavasAraci bestPlayerCard = playerCards.stream().max(Comparator.comparingInt(SavasAraci::getVurus)).orElse(null);
            SavasAraci bestComputerCard = computerCards.stream().max(Comparator.comparingInt(SavasAraci::getVurus)).orElse(null);

            if (bestPlayerCard != null) {
                writer.println("Oyuncunun en güçlü kartı: " + bestPlayerCard.getClass().getSimpleName() +
                        " (Vuruş: " + bestPlayerCard.getVurus() + ")");
            }
            if (bestComputerCard != null) {
                writer.println("Bilgisayarın en güçlü kartı: " + bestComputerCard.getClass().getSimpleName() +
                        " (Vuruş: " + bestComputerCard.getVurus() + ")");
            }

            // Hasar Karşılaştırması
            int playerTotalDamage = playerCards.stream().mapToInt(SavasAraci::getVurus).sum();
            int computerTotalDamage = computerCards.stream().mapToInt(SavasAraci::getVurus).sum();

            writer.println("\nTur Özeti:");
            writer.println("Oyuncunun toplam verdiği hasar: " + playerTotalDamage);
            writer.println("Bilgisayarın toplam verdiği hasar: " + computerTotalDamage);

            if (playerTotalDamage > computerTotalDamage) {
                writer.println("Bu turu oyuncu domine etti!");
            } else if (computerTotalDamage > playerTotalDamage) {
                writer.println("Bu turda bilgisayar daha etkiliydi.");
            } else {
                writer.println("Bu tur dengeli geçti.");
            }

            writer.println("=========================================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logBattleResult(String winner, int playerFinalScore, int computerFinalScore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.println("\n==================== Oyun Sonucu ====================");
            writer.println("Kazanan: " + winner);
            writer.println("Oyuncu Final Skoru: " + playerFinalScore);
            writer.println("Bilgisayar Final Skoru: " + computerFinalScore);

            if (playerFinalScore == computerFinalScore) {
                writer.println("Sonuç: Berabere!");
            } else {
                writer.println("Farklı bir performansla " + winner + " kazandı!");
            }

            writer.println("=========================================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}