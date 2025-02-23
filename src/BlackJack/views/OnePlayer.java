package BlackJack.views;

import BlackJack.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.stream.IntStream;

/**
 * Classe che rappresenta la vista per un gioco di BlackJack con un solo giocatore.
 */
public class OnePlayer extends JPanel implements GameView, Observer {

    private static final int CARD_WIDTH = 110; // Larghezza della carta

    public GamePanel gamePanel;

    private JPanel buttonPanel;
    private JButton hitButton;
    private JButton stayButton;

    private boolean gameEnded = false; // Flag per verificare se il gioco è terminato
    private int result; // Risultato del gioco

    /**
     * Costruttore della classe OnePlayer.
     * Inizializza il pannello della vista del gioco.
     */
    public OnePlayer() {
        initializePanel();
    }

    /**
     * Inizializza il pannello principale.
     */
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(JBlackJack.BACKGROUND_COLOR);

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea e restituisce il pannello dei bottoni.
     *
     * @return Il pannello contenente i bottoni "Hit" e "Stay".
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(53, 101, 77));

        hitButton = createButton("Hit");
        stayButton = createButton("Stay");

        panel.add(hitButton);
        panel.add(stayButton);

        return panel;
    }

    /**
     * Crea un bottone con il testo specificato e una dimensione preferita.
     *
     * @param text Il testo da visualizzare sul bottone.
     * @return Il bottone creato.
     */
    public JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    @Override
    public JButton getHitButton() {
        return hitButton;
    }

    @Override
    public JButton getStayButton() {
        return stayButton;
    }

    /**
     * Restituisce il risultato del gioco.
     *
     * @return Il risultato del gioco.
     */
    public int getResult() {
        return result;
    }

    /**
     * Classe interna che rappresenta il pannello di gioco.
     */
    public class GamePanel extends JPanel {

        private Model model;
        private JButton homeButton;
        private List<AnimationCard.AnimatedCard1> playerCards;
        private List<AnimationCard.AnimatedCard1> dealerCards;
        private Timer animationTimer;
        private boolean dealerCardsInitialized = false;

        /**
         * Costruttore della classe GamePanel.
         * Inizializza il pannello di gioco e le liste di carte.
         */
        public GamePanel() {
            playerCards = new ArrayList<>();
            dealerCards = new ArrayList<>();
            initializePanel();
        }

        /**
         * Inizializza il pannello di gioco.
         */
        private void initializePanel() {
            setLayout(null);

            homeButton = new JButton("Home");
            homeButton.setBounds(getHomeButtonBounds());
            homeButton.setVisible(false);
            add(homeButton);

            animationTimer = new Timer(16, e -> updateAnimations());
        }

        /**
         * Restituisce le coordinate e le dimensioni del bottone "Home".
         *
         * @return Un rettangolo che rappresenta le coordinate e la dimensione del bottone "Home".
         */
        private Rectangle getHomeButtonBounds() {
            int width = 100;
            int height = 30;
            int x = (JBlackJack.BOARD_WIDTH - width) / 2;
            int y = (JBlackJack.BOARD_HEIGHT - height) / 2 + 20;
            return new Rectangle(x, y, width, height);
        }

        /**
         * Aggiorna le animazioni delle carte.
         */
        private void updateAnimations() {
            updatePlayerCardPositions();
            if (dealerCardsInitialized) {
                updateDealerCardPositions();
            }
            repaint();
        }

        /**
         * Imposta il modello del gioco e inizializza le carte.
         *
         * @param model Il modello del gioco da utilizzare.
         */
        public void setModel(Model model) {
            this.model = model;
            updatePlayerCardList(model.getPlayerHand());
            if (!dealerCardsInitialized) {
                initializeDealerCards(model.getDealerHand(), model.getHiddenCard());
                dealerCardsInitialized = true;
            }
            animationTimer.start();
        }

        /**
         * Inizializza le carte del dealer.
         *
         * @param hand La mano del dealer.
         * @param hiddenCard La carta nascosta del dealer.
         */
        private void initializeDealerCards(List<Model.Card> hand, Model.Card hiddenCard) {
            dealerCards.clear();
            dealerCards.add(new AnimationCard.AnimatedCard1(hiddenCard, JBlackJack.BOARD_WIDTH, 35));
            dealerCards.add(new AnimationCard.AnimatedCard1(hand.get(0), JBlackJack.BOARD_WIDTH + CARD_WIDTH + 5, 35));
            updateDealerCardPositions();
        }

        /**
         * Aggiorna le posizioni delle carte del dealer.
         */
        private void updateDealerCardPositions() {
            updateCardPositions(dealerCards, 35);
        }

        /**
         * Aggiorna la lista delle carte del giocatore.
         *
         * @param hand La mano del giocatore.
         */
        private void updatePlayerCardList(List<Model.Card> hand) {
            IntStream.range(playerCards.size(), hand.size()).forEach(i -> {
                Model.Card card = hand.get(i);
                playerCards.add(new AnimationCard.AnimatedCard1(card, JBlackJack.BOARD_WIDTH, 570));
            });
            updatePlayerCardPositions();
        }

        /**
         * Aggiorna le posizioni delle carte del giocatore.
         */
        private void updatePlayerCardPositions() {
            updateCardPositions(playerCards, 570);
        }

        /**
         * Aggiorna le posizioni delle carte in base alla lista e alla coordinata y specificata.
         *
         * @param cards La lista delle carte da aggiornare.
         * @param y La coordinata y delle carte.
         */
        private void updateCardPositions(List<AnimationCard.AnimatedCard1> cards, int y) {
            int totalWidth = cards.size() * CARD_WIDTH + (cards.size() - 1) * 5;
            int startX = (JBlackJack.BOARD_WIDTH - totalWidth) / 2;

            IntStream.range(0, cards.size()).forEach(i -> {
                AnimationCard.AnimatedCard1 card = cards.get(i);
                float targetX = startX + (CARD_WIDTH + 5) * i;
                card.setTarget(targetX, y);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Avvia l'animazione di rivelazione delle carte del dealer.
         */
        public void startDealerRevealAnimation() {
            List<Model.Card> dealerHand = model.getDealerHand();
            int existingCards = dealerCards.size();
            int newCards = dealerHand.size() + 1 - existingCards;

            IntStream.range(0, existingCards).forEach(i -> dealerCards.get(i).setVisible(true));

            IntStream.range(existingCards, dealerHand.size() + 1).forEach(i -> {
                Model.Card card = (i == 0) ? model.getHiddenCard() : dealerHand.get(i - 1);
                dealerCards.add(new AnimationCard.AnimatedCard1(card, JBlackJack.BOARD_WIDTH, 35));
            });

            updateDealerCardPositions(newCards);
            animationTimer.start();
        }

        /**
         * Aggiorna le posizioni delle carte del dealer in base al numero di nuove carte.
         *
         * @param newCards Il numero di nuove carte.
         */
        private void updateDealerCardPositions(int newCards) {
            int totalCards = dealerCards.size();
            int totalWidth = totalCards * CARD_WIDTH + (totalCards - 1) * 5;
            int startX = (JBlackJack.BOARD_WIDTH - totalWidth) / 2;

            IntStream.range(0, totalCards).forEach(i -> {
                AnimationCard.AnimatedCard1 card = dealerCards.get(i);
                float targetX = startX + (CARD_WIDTH + 5) * i;

                if (i < totalCards - newCards) {
                    card.setTarget(targetX, 35);
                } else {
                    card.x = JBlackJack.BOARD_WIDTH;
                    card.y = 35;
                    card.setTarget(targetX, 35);
                }

                card.setVisible(true);
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (model == null) return;

            setBackground(new Color(53, 101, 77));

            drawDealerInfo(g);
            drawDealerCards(g);
            drawPlayerCards(g);

            if (gameEnded) {
                showGameResult(g);
                homeButton.setVisible(true);
            } else {
                homeButton.setVisible(false);
            }

            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        }

        /**
         * Disegna le informazioni del dealer.
         *
         * @param g L'oggetto Graphics utilizzato per disegnare.
         */
        private void drawDealerInfo(Graphics g) {
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            String message = "Dealer";
            int x = (JBlackJack.BOARD_WIDTH - fm.stringWidth(message)) / 2;
            g.drawString(message, x, 20);
        }

        /**
         * Disegna le carte del dealer.
         *
         * @param g L'oggetto Graphics utilizzato per disegnare.
         */
        private void drawDealerCards(Graphics g) {
            IntStream.range(0, dealerCards.size()).forEach(i -> {
                AnimationCard.AnimatedCard1 card = dealerCards.get(i);
                boolean faceUp = gameEnded || i > 0;
                card.draw(g, faceUp);
            });
        }

        /**
         * Disegna le carte del giocatore.
         *
         * @param g L'oggetto Graphics utilizzato per disegnare.
         */
        private void drawPlayerCards(Graphics g) {
            playerCards.forEach(card -> card.draw(g, true));
        }

        /**
         * Mostra il risultato del gioco sul pannello.
         *
         * @param g L'oggetto Graphics utilizzato per disegnare.
         */
        private void showGameResult(Graphics g) {
            int dealerFinalSum = model.reduceAce(model.getDealerSum(), model.getDealerAceCount());
            int playerFinalSum = model.reduceAce(model.getPlayerSum(), model.getPlayerAceCount());
            gameEnded = true;

            String message = getGameResultMessage(playerFinalSum, dealerFinalSum);
            result = getResultFromMessage(message);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            int messageHeight = fm.getHeight();
            int x = (JBlackJack.BOARD_WIDTH - messageWidth) / 2;
            int y = (JBlackJack.BOARD_HEIGHT - messageHeight) / 2 + fm.getAscent() - 20;

            g.drawString(message, x, y);
        }

        /**
         * Restituisce il messaggio del risultato del gioco basato sui punteggi del giocatore e del dealer.
         *
         * @param playerSum Il punteggio finale del giocatore.
         * @param dealerSum Il punteggio finale del dealer.
         * @return Il messaggio del risultato del gioco.
         */
        private String getGameResultMessage(int playerSum, int dealerSum) {
            if (playerSum > 21) return "Hai perso";
            if (dealerSum > 21) return "Hai vinto";
            if (playerSum == dealerSum) return "Pareggio";
            return (playerSum > dealerSum) ? "Hai vinto" : "Hai perso";
        }

        /**
         * Restituisce il codice del risultato basato sul messaggio del risultato del gioco.
         *
         * @param message Il messaggio del risultato del gioco.
         * @return Il codice del risultato.
         */
        private int getResultFromMessage(String message) {
            switch (message) {
                case "Hai vinto": return 1;
                case "Hai perso": return 2;
                case "Pareggio": return 3;
                default: return 0;
            }
        }

        /**
         * Aggiunge un listener al bottone "Home".
         *
         * @param listener Il listener da aggiungere.
         */
        public void addHomeButtonListener(ActionListener listener) {
            homeButton.addActionListener(listener);
        }
    }

    @Override
    public void updateView(Model model) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/card.wav");
        gamePanel.setModel(model);
        if (!gameEnded && !stayButton.isEnabled()) {
            gameEnded = true;
            gamePanel.startDealerRevealAnimation();
        }
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof Model) {
            Model model = (Model) o;
            updateView(model);
        }
    }
}
