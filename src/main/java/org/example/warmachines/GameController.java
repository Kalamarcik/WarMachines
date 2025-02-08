package org.example.warmachines;


import org.example.warmachines.SavasAraci.SavasAraci;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.warmachines.havakaradeniz.Deniz;
import org.example.warmachines.havakaradeniz.Kara;
import org.example.warmachines.havakaradeniz.Hava;
import org.example.warmachines.Models.*;


import java.util.*;

public class GameController {
    private List<SavasAraci> playerCards;
    private List<SavasAraci> computerCards;
    private List<SavasAraci> cardPool;
    private List<SavasAraci> selectedCards;
    private int playerScore;
    private int computerScore;
    private int currentMove;
    private static final int TOTAL_MOVES = 5;
    private static final int LEVEL_SCORE_LIMIT = 20;
    private static final int MINIMUM_SCORE = 10;
    private boolean isLastRound = false;
    private List<SavasAraci> computerCurrentMoveCards;

    @FXML private FlowPane playerCardsPane;
    @FXML private FlowPane computerCardsPane;
    @FXML private Text playerScoreText;
    @FXML private Text computerScoreText;
    @FXML private Text currentMoveText;
    @FXML private Button startGameButton;
    @FXML private Button playMoveButton;
    @FXML private ListView<String> gameLog;
    @FXML private Label selectionInfoLabel;

    private ObservableList<String> logEntries = FXCollections.observableArrayList();
    private Map<String, Image> cardImages = new HashMap<>();
    private Set<SavasAraci> previouslySelectedCards = new HashSet<>();
    private boolean computerCardsRevealed = false;
    private Set<SavasAraci> previouslyPlayedCards = new HashSet<>();
    private BattleLogger battleLogger = new BattleLogger();


    @FXML
    public void initialize() {
        gameLog.setItems(logEntries);
        playerCards = new ArrayList<>();
        computerCards = new ArrayList<>();
        cardPool = new ArrayList<>();
        selectedCards = new ArrayList<>();
        playerScore = 0;
        computerScore = 0;
        currentMove = 0;
        loadCardImages();
        updateScores();
        playMoveButton.setDisable(true);
        selectionInfoLabel.setText("Lütfen 3 kart seçin");
    }

    private void loadCardImages() {
        String[] cardTypes = {"Ucak", "Obus", "Firkateyn", "Siha", "KFS", "Sida", "Hidden"};
        for (String type : cardTypes) {
            try {
                Image img;
                if (type.equals("Hidden")) {
                    img = new Image(getClass().getResource("hidden_card.png").toExternalForm());
                } else {
                    img = new Image(getClass().getResource(type + ".png").toExternalForm());
                }
                cardImages.put(type, img);
            } catch (Exception e) {
                System.err.println("Resim yüklenemedi: " + type);
                cardImages.put(type, createPlaceholderImage(type));
            }
        }
    }

    private Image createPlaceholderImage(String type) {
        return null;
    }

    private void createBasicCardPool() {
        cardPool.clear();
        // Her tipten 4'er kart oluşturduk
        for (int i = 0; i < 4; i++) {
            cardPool.add(new Ucak(0));
            cardPool.add(new Obus(0));
            cardPool.add(new Firkateyn(0));
        }
    }

    @FXML
    private void handleStartGame() {
        startGameButton.setDisable(true);
        playMoveButton.setDisable(true);
        selectedCards.clear();
        previouslySelectedCards.clear();
        computerCardsRevealed = false;  // Oyun başlangıcında kartları gizle

        playerCards.clear();
        computerCards.clear();
        playerScore = 0;
        computerScore = 0;
        currentMove = 0;

        createBasicCardPool();
        dealInitialCards();

        logEntries.clear();
        logEntries.add("Oyun başladı! Her oyuncuya 6 kart dağıtıldı.");
        battleLogger.logGameStart();
        updateGameState();
        selectionInfoLabel.setText("Lütfen 3 kart seçin (0/3)");
    }


    @FXML
    private void handlePlayMove() {
        if (selectedCards.size() != 3) {
            showAlert("Uyarı", "Lütfen tam olarak 3 kart seçin!");
            return;
        }

        playMoveWithSelectedCards();


        showBattleDialog();

        selectedCards.clear();
    }

    private void dealInitialCards() {
        Collections.shuffle(cardPool);

        // Her oyuncuya 6'şar kart
        for (int i = 0; i < 6; i++) {
            if (!cardPool.isEmpty()) {
                playerCards.add(cardPool.remove(0));
            }
            if (!cardPool.isEmpty()) {
                computerCards.add(cardPool.remove(0));
            }
        }
    }


    private void playMoveWithSelectedCards() {
        logEntries.add("Hamle " + (currentMove + 1));

        // Bilgisayar kartlarını seç
        computerCurrentMoveCards = selectCards(computerCards, true);

        // Kartları göster
        computerCardsRevealed = true;
        updateCardDisplay(playerCards, computerCards);

        // Karşılaşmaları gerçekleştir
        for (int i = 0; i < 3; i++) {
            battle(selectedCards.get(i), computerCurrentMoveCards.get(i));
            previouslyPlayedCards.add(selectedCards.get(i)); // Oynanan kartları takip et
        }

        removeDestroyedCards(playerCards);
        removeDestroyedCards(computerCards);

        // Her hamle sonunda bir kart ekle (son round değilse)
        if (!isLastRound) {
            addExtraCardToEachSide();
        }

        currentMove++;

        // Eğer bir tarafın kartı 1'den azsa, son round kuralını uygula
        if (playerCards.size() <= 1 || computerCards.size() <= 1) {
            applyLastStandRule();
        }

        // Son round ise ve bu son hamle ise oyunu bitir
        if (isLastRound && currentMove >= TOTAL_MOVES) {
            playMoveButton.setDisable(true);
            startGameButton.setDisable(false);
            showGameResult();
        }

        // Burayı ekledik: Her hamle sonunda oyun durumunu güncelle
        updateGameState();

        if (!isGameOver()) {
            dealNewCards();
            computerCardsRevealed = false;
            updateCardDisplay(playerCards, computerCards);
        }
        battleLogger.logBattleMove(currentMove, playerCards, computerCards, playerScore, computerScore);
    }

    private void addExtraCardToEachSide() {
        if (cardPool.isEmpty()) {
            if (playerScore >= LEVEL_SCORE_LIMIT ) {
                addSpecialCardsToPlayer();
            } else {
                createBasicCardPool();
            }

            if (computerScore >= LEVEL_SCORE_LIMIT) {
                addSpecialCardsToComputer();
            } else {
                createBasicCardPool();
            }
        }

        if (!cardPool.isEmpty()) {
            playerCards.add(cardPool.remove(0));
        }
        if (!cardPool.isEmpty()) {
            computerCards.add(cardPool.remove(0));
        }
    }


    private void applyLastStandRule() {
        if (playerCards.size() <= 1 || computerCards.size() <= 1) {
            isLastRound = true;
            logEntries.add("Son round başladı!");
        }

        // Eğer oyuncunun 1'den az kartı varsa
        while (playerCards.size() < 3 && !cardPool.isEmpty()) {
            playerCards.add(cardPool.remove(0));
        }

        // Eğer bilgisayarın 1'den az kartı varsa
        while (computerCards.size() < 3 && !cardPool.isEmpty()) {
            computerCards.add(cardPool.remove(0));
        }
    }


    private List<SavasAraci> selectCards(List<SavasAraci> cards, boolean isComputer) {
        List<SavasAraci> selectedCards = new ArrayList<>();
        if (isComputer) {
            // Bilgisayar için rastgele 3 kart
            Collections.shuffle(cards);
            for (int i = 0; i < 3 && i < cards.size(); i++) {
                selectedCards.add(cards.get(i));
            }
        } else {
            // Oyuncu için ilk 3 kartı seç (gerçek uygulamada oyuncu seçicem)
            for (int i = 0; i < 3 && i < cards.size(); i++) {
                selectedCards.add(cards.get(i));
            }
        }
        return selectedCards;
    }

    private void battle(SavasAraci playerCard, SavasAraci computerCard) {
        // attack değerleri hespalama atama
        int playerAttackValue = calculateAttackValue(playerCard, computerCard);
        int computerAttackValue = calculateAttackValue(computerCard, playerCard);

        // Apply damage kısmı
        computerCard.setDayaniklilik(computerCard.getDayaniklilik() - playerAttackValue);
        playerCard.setDayaniklilik(playerCard.getDayaniklilik() - computerAttackValue);

        String battleLog = String.format("%s vs %s",
                playerCard.getClass().getSimpleName(),
                computerCard.getClass().getSimpleName());
        logEntries.add(battleLog);

        // sskor updatei
        if (computerCard.getDayaniklilik() <= 0) {
            int points = Math.max(computerCard.getSeviyePuani(), MINIMUM_SCORE);
            playerCard.setSeviyePuani(playerCard.getSeviyePuani() + points);
            playerScore += points;
            logEntries.add(String.format("Oyuncu kartı %d seviye puanı kazandı!", points));
        }
        if (playerCard.getDayaniklilik() <= 0) {
            int points = Math.max(playerCard.getSeviyePuani(), MINIMUM_SCORE);
            computerCard.setSeviyePuani(computerCard.getSeviyePuani() + points);
            computerScore += points;
            logEntries.add(String.format("Bilgisayar kartı %d seviye puanı kazandı!", points));
        }
    }

    private int calculateAttackValue(SavasAraci attacker, SavasAraci defender) {
        int attackValue = attacker.getVurus();

        // Avantaj kontrolleri (üstünlük
        if (attacker instanceof Hava && defender instanceof Kara) {
            attackValue += ((Hava) attacker).getKaraVurusAvantaji();
        } else if (attacker instanceof Kara && defender instanceof Deniz) {
            attackValue += ((Kara) attacker).getDenizVurusAvantaji();
        } else if (attacker instanceof Deniz && defender instanceof Hava) {
            attackValue += ((Deniz) attacker).getHavaVurusAvantaji();
        }

        return attackValue;
    }

    private void removeDestroyedCards(List<SavasAraci> cards) {
        cards.removeIf(card -> card.getDayaniklilik() <= 0);
    }

    private void dealNewCards() {
        // Oyuncu tarafı için special kart kontrolü
        if (playerScore >= LEVEL_SCORE_LIMIT) {
            addSpecialCardsToPlayer();
        }

        // Bilgisayar tarafı için special kart kontrolü
        if (computerScore >= LEVEL_SCORE_LIMIT) {
            addSpecialCardsToComputer();
        }

        // Kartları 3'e tamamlamaca
        completeCards(playerCards);
        completeCards(computerCards);
    }

    private void addSpecialCardsToPlayer() {
        cardPool.add(new Siha(0));
        cardPool.add(new KFS(0));
        cardPool.add(new Sida(0));
        Collections.shuffle(cardPool);
    }

    private void addSpecialCardsToComputer() {
        cardPool.add(new Siha(0));
        cardPool.add(new KFS(0));
        cardPool.add(new Sida(0));
        Collections.shuffle(cardPool);
    }

    private void addSpecialCards() {
        cardPool.add(new Siha(0));
        cardPool.add(new KFS(0));
        cardPool.add(new Sida(0));
        Collections.shuffle(cardPool);
    }

    private void completeCards(List<SavasAraci> cards) {
        int missingCards = 3 - cards.size();
        if (missingCards > 0) {
            if (cardPool.isEmpty()) {
                createBasicCardPool();
            }

            for (int i = 0; i < missingCards && !cardPool.isEmpty(); i++) {
                // Special kart kontrolü
                SavasAraci nextCard = cardPool.get(0);
                boolean isSpecialCard = nextCard instanceof Siha ||
                        nextCard instanceof KFS ||
                        nextCard instanceof Sida;

                // Hangi oyuncunun kartları dolduruluyorsa ona göre skoru kontrol ediyorum
                int currentScore = (cards == playerCards) ? playerScore : computerScore;

                // Eğer special kart ise ve skor 20'nin altındaysa
                if (isSpecialCard && currentScore < LEVEL_SCORE_LIMIT) {
                    // Eğer basic kart yoksa yeni bir havuz oluştur
                    if (!cardPool.stream().anyMatch(c -> !(c instanceof Siha || c instanceof KFS || c instanceof Sida))) {
                        createBasicCardPool();
                    }

                    // Special kartı çıkar ve basic bir kart bul
                    cardPool.remove(0);
                    SavasAraci basicCard = cardPool.stream()
                            .filter(c -> !(c instanceof Siha || c instanceof KFS || c instanceof Sida))
                            .findFirst()
                            .orElse(null);

                    if (basicCard != null) {
                        cards.add(basicCard);
                        cardPool.remove(basicCard);
                    }
                } else {
                    // Kartı normal şekilde ekle
                    cards.add(cardPool.remove(0));
                }
            }
        }
    }

    private void updateGameState() {
        updateScores();
        updateCardDisplay(playerCards, computerCards);
        currentMoveText.setText("Hamle: " + (currentMove + 1) + "/" + TOTAL_MOVES);
    }

    private void updateScores() {
        playerScoreText.setText("Oyuncu Puanı: " + playerScore);
        computerScoreText.setText("Bilgisayar Puanı: " + computerScore);
    }

    private VBox createCardNode(SavasAraci card, boolean isComputer) {
        VBox cardBox = new VBox(5);
        cardBox.getStyleClass().add("card");

        if (isComputer && !computerCardsRevealed) {
            // Gizli kart
            ImageView hiddenCardImage = new ImageView(cardImages.get("Hidden"));
            hiddenCardImage.setFitWidth(120);
            hiddenCardImage.setFitHeight(180);
            hiddenCardImage.setPreserveRatio(true);

            Label hiddenLabel = new Label("?????");
            cardBox.getChildren().addAll(hiddenCardImage, hiddenLabel);
        } else {
            // Normal kart
            ImageView cardImage = new ImageView(cardImages.get(card.getClass().getSimpleName()));
            cardImage.setFitWidth(120);
            cardImage.setFitHeight(180);
            cardImage.setPreserveRatio(true);

            Label statsLabel = new Label(String.format(
                    "Vuruş: %d\nDayanıklılık: %d\nSeviye Puanı: %d",
                    card.getVurus(),
                    card.getDayaniklilik(),
                    card.getSeviyePuani()
            ));

            cardBox.getChildren().addAll(cardImage, statsLabel);
        }

        if (!isComputer) {
            cardBox.getStyleClass().add("selectable-card");
            cardBox.setOnMouseClicked(event -> handleCardSelection(cardBox, card));
        }

        return cardBox;
    }



    private void handleCardSelection(VBox cardBox, SavasAraci card) {
        // Eğer kart zaten seçilmişse, seçimi kaldır
        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
            cardBox.getStyleClass().remove("selected-card");
        } else if (selectedCards.size() < 3) {
            // Daha önce oynanmamış kartları öncelikli olarak seç
            if (!previouslyPlayedCards.contains(card)) {
                selectedCards.add(card);
                cardBox.getStyleClass().add("selected-card");
            } else {
                // Eğer daha önce oynanmamış tüm kartlar zaten seçildiyse, oynanmışlardan seçmeye izin ver
                long unplayedSelectedCount = selectedCards.stream()
                        .filter(c -> !previouslyPlayedCards.contains(c))
                        .count();

                if (unplayedSelectedCount >= getUnplayedCardCount()) {
                    selectedCards.add(card);
                    cardBox.getStyleClass().add("selected-card");
                }
            }
        }

        // Seçim bilgilerini ve buton durumunu güncelle
        selectionInfoLabel.setText(String.format("Lütfen 3 kart seçin (%d/3)", selectedCards.size()));
        playMoveButton.setDisable(selectedCards.size() != 3);
    }

    private boolean canSelectCard(SavasAraci card) {
        // Eğer tüm kartlar oynandıysa, yeniden seçim yapılabilir
        if (previouslyPlayedCards.size() >= playerCards.size()) {
            previouslyPlayedCards.clear();
        }

        // Daha önce oynanmamış bir kart seçilebilir
        return !previouslyPlayedCards.contains(card);
    }

    private long getUnplayedCardCount() {
        // Daha önce oynanmamış kartların sayısını döndür
        return playerCards.stream()
                .filter(card -> !previouslyPlayedCards.contains(card))
                .count();
    }

    private void updateCardDisplay(List<SavasAraci> playerCards, List<SavasAraci> computerCards) {
        playerCardsPane.getChildren().clear();
        computerCardsPane.getChildren().clear();

        for (SavasAraci card : playerCards) {
            playerCardsPane.getChildren().add(createCardNode(card, false));
        }

        for (SavasAraci card : computerCards) {
            computerCardsPane.getChildren().add(createCardNode(card, true));
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isGameOver() {
        // Eğer belirlenen hamle sayısına ulaşıldıysa veya bir tarafın kartı kalmadıysa
        return isLastRound || currentMove >= TOTAL_MOVES ||
                playerCards.isEmpty() || computerCards.isEmpty();
    }

    private void showGameResult() {
        String winner;

        // Kartların kalmama durumu öncelikli
        if (playerCards.isEmpty()) {
            winner = "Bilgisayar";
        } else if (computerCards.isEmpty()) {
            winner = "Oyuncu";
        } else {
            // Son round ise veya hamle sınırına ulaşıldıysa puan karşılaştırması
            winner = playerScore > computerScore ? "Oyuncu" : "Bilgisayar";
        }

        // Eğer skorlar eşitse, dayanıklılık toplamlarına bak
        if (playerScore == computerScore) {
            int playerDurability = playerCards.stream()
                    .mapToInt(SavasAraci::getDayaniklilik)
                    .sum();
            int computerDurability = computerCards.stream()
                    .mapToInt(SavasAraci::getDayaniklilik)
                    .sum();

            if (playerDurability > computerDurability) {
                playerScore += (playerDurability - computerDurability);
                winner = "Oyuncu";
                logEntries.add("Oyuncu dayanıklılık farkıyla kazandı!");
            } else if (computerDurability > playerDurability) {
                computerScore += (computerDurability - playerDurability);
                winner = "Bilgisayar";
                logEntries.add("Bilgisayar dayanıklılık farkıyla kazandı!");
            }
        }

        logEntries.add("Oyun bitti! Kazanan: " + winner);
        battleLogger.logBattleResult(winner, playerScore, computerScore);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oyun Bitti");
        alert.setHeaderText(null);
        alert.setContentText("Kazanan: " + winner + "\n" +
                "Oyuncu Puanı: " + playerScore + "\n" +
                "Bilgisayar Puanı: " + computerScore);
        alert.showAndWait();
    }

    private void showBattleDialog() {
        Dialog<Void> battleDialog = new Dialog<>();
        battleDialog.setTitle("Hamle Sonucu");
        battleDialog.setHeaderText("Kartlar Açıldı!");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);

        VBox playerCardBox = new VBox(10);
        playerCardBox.setAlignment(Pos.CENTER);
        Label playerLabel = new Label("Oyuncu Kartları");
        playerLabel.setStyle("-fx-font-weight: bold;");
        playerCardBox.getChildren().add(playerLabel);

        VBox computerCardBox = new VBox(10);
        computerCardBox.setAlignment(Pos.CENTER);
        Label computerLabel = new Label("Bilgisayar Kartları");
        computerLabel.setStyle("-fx-font-weight: bold;");
        computerCardBox.getChildren().add(computerLabel);

        // Sadece seçilen kartları göster
        for (SavasAraci playerCard : selectedCards) {
            ImageView cardImage = new ImageView(cardImages.get(playerCard.getClass().getSimpleName()));
            cardImage.setFitWidth(120);
            cardImage.setFitHeight(180);
            cardImage.setPreserveRatio(true);

            Label statsLabel = new Label(String.format(
                    "Vuruş: %d\nDayanıklılık: %d\nSeviye Puanı: %d",
                    playerCard.getVurus(),
                    playerCard.getDayaniklilik(),
                    playerCard.getSeviyePuani()
            ));

            VBox cardBox = new VBox(5, cardImage, statsLabel);
            cardBox.setAlignment(Pos.CENTER);
            playerCardBox.getChildren().add(cardBox);
        }

        // Bilgisayarın seçilen kartlarını göster
        for (SavasAraci computerCard : computerCurrentMoveCards) {
            ImageView cardImage = new ImageView(cardImages.get(computerCard.getClass().getSimpleName()));
            cardImage.setFitWidth(120);
            cardImage.setFitHeight(180);
            cardImage.setPreserveRatio(true);

            Label statsLabel = new Label(String.format(
                    "Vuruş: %d\nDayanıklılık: %d\nSeviye Puanı: %d",
                    computerCard.getVurus(),
                    computerCard.getDayaniklilik(),
                    computerCard.getSeviyePuani()
            ));

            VBox cardBox = new VBox(5, cardImage, statsLabel);
            cardBox.setAlignment(Pos.CENTER);
            computerCardBox.getChildren().add(cardBox);
        }

        grid.add(playerCardBox, 0, 0);
        grid.add(computerCardBox, 1, 0);

        battleDialog.getDialogPane().setContent(grid);

        ButtonType okButton = new ButtonType("Devam Et", ButtonBar.ButtonData.OK_DONE);
        battleDialog.getDialogPane().getButtonTypes().add(okButton);

        battleDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                // Dialog kapandığında kartları tekrar gizle
                computerCardsRevealed = false;

                Platform.runLater(() -> {
                    updateCardDisplay(playerCards, computerCards);

                    if (isGameOver()) {
                        playMoveButton.setDisable(true);
                        startGameButton.setDisable(false);
                        showGameResult();
                    } else {
                        selectionInfoLabel.setText("Lütfen 3 kart seçin (0/3)");
                        playMoveButton.setDisable(true);
                    }
                });
            }
            return null;
        });

        battleDialog.showAndWait();
    }
}