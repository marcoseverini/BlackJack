package BlackJack.views;

import BlackJack.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.stream.IntStream;

/**
 * Classe che rappresenta la vista per una partita di Blackjack a tre giocatori.
 */
public class ThreePlayers extends JPanel implements GameView, Observer {

    private static final int CARD_WIDTH = 78;
    private static final int CARD_HEIGHT = 110;

    public GamePanel gamePanel;
    private JPanel buttonPanel;

    private JButton hitButton;
    private JButton stayButton;
    private JButton homeButton;

    private int result;
    private int bot1result;
    private int bot2result;

    private boolean gameEnded = false;

    /**
     * Costruttore della classe ThreePlayers.
     * Inizializza il pannello del gioco e il pannello dei pulsanti.
     */
    public ThreePlayers() {
        setLayout(new BorderLayout());
        setBackground(JBlackJack.BACKGROUND_COLOR);

        initializeGamePanel();
        initializeButtonPanel();
    }

    /**
     * Inizializza il pannello del gioco e lo aggiunge al pannello principale.
     */
    private void initializeGamePanel() {
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);
    }

    /**
     * Inizializza il pannello dei pulsanti e lo aggiunge al pannello principale.
     */
    private void initializeButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(53, 101, 77));

        hitButton = createButton("Hit");
        stayButton = createButton("Stay");
        homeButton = createButton("Home");
        homeButton.setVisible(false);

        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        buttonPanel.add(homeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea un pulsante con il testo specificato e una dimensione predefinita.
     * 
     * @param text Il testo da visualizzare sul pulsante.
     * @return Il pulsante creato.
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
     * Aggiorna la vista del gioco con il modello fornito.
     * 
     * @param model Il modello che rappresenta lo stato attuale del gioco.
     */
    @Override
    public void updateView(Model model) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/card.wav");
        gamePanel.setModel(model);

        if (!gameEnded && !stayButton.isEnabled()) {
            gameEnded = true;
            gamePanel.startDealerRevealAnimation();
            gamePanel.startBot1RevealAnimation();
            gamePanel.startBot2RevealAnimation();
        }
        gamePanel.repaint();
    }

    /**
     * Restituisce il risultato della partita per il giocatore principale.
     * 
     * @return Il risultato della partita.
     */
    public int getResult() {
        return result;
    }

    /**
     * Restituisce il risultato della partita per il primo bot.
     * 
     * @return Il risultato della partita per il primo bot.
     */
    public int getBot1Result() {
        return bot1result;
    }

    /**
     * Restituisce il risultato della partita per il secondo bot.
     * 
     * @return Il risultato della partita per il secondo bot.
     */
    public int getBot2Result() {
        return bot2result;
    }

    public class GamePanel extends JPanel {
        private static final int TIMER_DELAY = 16;

        private Model model;
        private JButton homeButton;
        private List<AnimationCard.AnimatedCard3> playerCards = new ArrayList<>();
        private List<AnimationCard.AnimatedCard3> dealerCards = new ArrayList<>();
        private List<AnimationCard.AnimatedCard3> bot1Cards = new ArrayList<>();
        private List<AnimationCard.AnimatedCard3> bot2Cards = new ArrayList<>();
        private Timer animationTimer;

        private boolean dealerCardsInitialized = false;
        private boolean bot1CardsInitialized = false;
        private boolean bot2CardsInitialized = false;

        /**
         * Costruttore della classe GamePanel.
         * Inizializza il pannello e il timer per le animazioni.
         */
        public GamePanel() {
            setLayout(null);
            initializeHomeButton();
            initializeAnimationTimer();
        }

        /**
         * Inizializza il pulsante "Home" e lo aggiunge al pannello.
         */
        private void initializeHomeButton() {
            homeButton = new JButton("Home");
            int homeButtonWidth = 100;
            int homeButtonHeight = 30;
            int x = (JBlackJack.BOARD_WIDTH - homeButtonWidth) / 2;
            int y = (JBlackJack.BOARD_HEIGHT - homeButtonHeight) / 2 + 20;
            homeButton.setBounds(x, y, homeButtonWidth, homeButtonHeight);
            homeButton.setVisible(false);
            add(homeButton);
        }

        /**
         * Inizializza il timer per le animazioni delle carte.
         */
        private void initializeAnimationTimer() {
            animationTimer = new Timer(TIMER_DELAY, e -> {
                updateCardPositions();
                repaint();
            });
        }

        /**
         * Imposta il modello di gioco e aggiorna le liste delle carte.
         * 
         * @param model Il modello di gioco da visualizzare.
         */
        public void setModel(Model model) {
            this.model = model;
            updatePlayerCardList(model.getPlayerHand());
            if (!dealerCardsInitialized) {
                initializeDealerCards(model.getDealerHand(), model.getHiddenCard());
                dealerCardsInitialized = true;
            }
            if (!bot1CardsInitialized) {
                initializeBot1Cards(model.getBot1Hand());
                bot1CardsInitialized = true;
            }
            if (!bot2CardsInitialized) {
                initializeBot2Cards(model.getBot2Hand());
                bot2CardsInitialized = true;
            }
            animationTimer.start();
        }

        /**
         * Inizializza le carte del mazziere e le aggiunge alla lista delle carte del mazziere.
         * 
         * @param hand La mano del mazziere.
         * @param hiddenCard La carta nascosta del mazziere.
         */
        private void initializeDealerCards(List<Model.Card> hand, Model.Card hiddenCard) {
            dealerCards.clear();
            dealerCards.add(new AnimationCard.AnimatedCard3(hiddenCard, JBlackJack.BOARD_WIDTH, 35, false, 0));
            dealerCards.add(new AnimationCard.AnimatedCard3(hand.get(0), JBlackJack.BOARD_WIDTH + CARD_WIDTH + 5, 35, false, 0));
            updateDealerCardPositions();
        }

        /**
         * Inizializza le carte del primo bot e le aggiunge alla lista delle carte del primo bot.
         * 
         * @param hand La mano del primo bot.
         */
        private void initializeBot1Cards(List<Model.Card> hand) {
            bot1Cards.clear();
            IntStream.range(0, hand.size()).forEach(i -> 
                bot1Cards.add(new AnimationCard.AnimatedCard3(hand.get(i), 45, JBlackJack.BOARD_HEIGHT, true, 90))
            );
            updateBot1CardPositions();
        }

        /**
         * Inizializza le carte del secondo bot e le aggiunge alla lista delle carte del secondo bot.
         * 
         * @param hand La mano del secondo bot.
         */
        private void initializeBot2Cards(List<Model.Card> hand) {
            bot2Cards.clear();
            IntStream.range(0, hand.size()).forEach(i -> 
                bot2Cards.add(new AnimationCard.AnimatedCard3(hand.get(i), JBlackJack.BOARD_WIDTH - CARD_HEIGHT - 45, JBlackJack.BOARD_HEIGHT, true, -90))
            );
            updateBot2CardPositions();
        }

        /**
         * Aggiorna la lista delle carte del giocatore e la loro posizione.
         * 
         * @param hand La mano del giocatore.
         */
        private void updatePlayerCardList(List<Model.Card> hand) {
            while (playerCards.size() < hand.size()) {
                playerCards.add(new AnimationCard.AnimatedCard3(hand.get(playerCards.size()), JBlackJack.BOARD_WIDTH, 620, false, 0));
            }
            updatePlayerCardPositions();
        }

        /**
         * Aggiorna la posizione di tutte le carte.
         */
        private void updateCardPositions() {
            updatePlayerCardPositions();
            updateDealerCardPositions();
            updateBot1CardPositions();
            updateBot2CardPositions();
        }

        /**
         * Aggiorna la posizione delle carte del giocatore.
         */
        private void updatePlayerCardPositions() {
            int totalWidth = playerCards.size() * CARD_WIDTH + (playerCards.size() - 1) * 5;
            int startX = (JBlackJack.BOARD_WIDTH - totalWidth) / 2;

            IntStream.range(0, playerCards.size()).forEach(i -> {
                AnimationCard.AnimatedCard3 card = playerCards.get(i);
                float targetX = startX + (CARD_WIDTH + 5) * i;
                card.setTarget(targetX, 620);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Aggiorna la posizione delle carte del mazziere.
         */
        private void updateDealerCardPositions() {
            int totalWidth = dealerCards.size() * CARD_WIDTH + (dealerCards.size() - 1) * 5;
            int startX = (JBlackJack.BOARD_WIDTH - totalWidth) / 2;

            IntStream.range(0, dealerCards.size()).forEach(i -> {
                AnimationCard.AnimatedCard3 card = dealerCards.get(i);
                float targetX = startX + (CARD_WIDTH + 5) * i;
                card.setTarget(targetX, 35);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Aggiorna la posizione delle carte del primo bot.
         */
        private void updateBot1CardPositions() {
            int totalHeight = bot1Cards.size() * CARD_WIDTH + (bot1Cards.size() - 1) * 5;
            int startY = (JBlackJack.BOARD_HEIGHT - totalHeight) / 2;

            IntStream.range(0, bot1Cards.size()).forEach(i -> {
                AnimationCard.AnimatedCard3 card = bot1Cards.get(i);
                float targetY = startY + (CARD_WIDTH + 5) * i;
                card.setTarget(45, targetY);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Aggiorna la posizione delle carte del secondo bot.
         */
        private void updateBot2CardPositions() {
            int totalHeight = bot2Cards.size() * CARD_WIDTH + (bot2Cards.size() - 1) * 5;
            int startY = (JBlackJack.BOARD_HEIGHT - totalHeight) / 2 - 32;

            IntStream.range(0, bot2Cards.size()).forEach(i -> {
                AnimationCard.AnimatedCard3 card = bot2Cards.get(i);
                float targetY = startY + (CARD_WIDTH + 5) * i;
                card.setTarget(JBlackJack.BOARD_WIDTH - CARD_HEIGHT - 45, targetY);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Avvia l'animazione di rivelazione delle carte del mazziere.
         */
        public void startDealerRevealAnimation() {
            List<Model.Card> dealerHand = model.getDealerHand();
            int existingCards = dealerCards.size();
    
            for (int i = existingCards; i < dealerHand.size() + 1; i++) {
                Model.Card card = (i == 0) ? model.getHiddenCard() : dealerHand.get(i - 1);
                dealerCards.add(new AnimationCard.AnimatedCard3(card, JBlackJack.BOARD_WIDTH, 35, false, 0));
            }
    
            updateDealerCardPositions();
        }

        /**
         * Avvia l'animazione di rivelazione delle carte del primo bot.
         */
        public void startBot1RevealAnimation() {
            List<Model.Card> botHand = model.getBot1Hand();
            int existingCards = bot1Cards.size();

            IntStream.range(existingCards, botHand.size()).forEach(i -> 
                bot1Cards.add(new AnimationCard.AnimatedCard3(botHand.get(i), 45, JBlackJack.BOARD_HEIGHT, true, 90))
            );

            updateBot1CardPositions();
        }

        /**
         * Avvia l'animazione di rivelazione delle carte del secondo bot.
         */
        public void startBot2RevealAnimation() {
            List<Model.Card> botHand = model.getBot2Hand();
            int existingCards = bot2Cards.size();

            IntStream.range(existingCards, botHand.size()).forEach(i -> 
                bot2Cards.add(new AnimationCard.AnimatedCard3(botHand.get(i), JBlackJack.BOARD_WIDTH - CARD_HEIGHT - 45, JBlackJack.BOARD_HEIGHT, true, -90))
            );

            updateBot2CardPositions();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (model == null) return;
            setBackground(new Color(53, 101, 77));

            drawLabels(g);
            drawCards(g);

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
         * Disegna le etichette per il mazziere e i bot.
         * 
         * @param g Il contesto grafico su cui disegnare le etichette.
         */
        private void drawLabels(Graphics g) {
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            FontMetrics fm = g.getFontMetrics();
        
            g.setColor(Color.WHITE);
            drawLabel(g, "Dealer", (JBlackJack.BOARD_WIDTH - fm.stringWidth("Dealer")) / 2, 20);
        
            g.setColor(Color.LIGHT_GRAY);
            drawRotatedLabel(g, "Player 2", CARD_HEIGHT + 50, (JBlackJack.BOARD_WIDTH - fm.stringWidth("Player 2")) / 2 - 16, 90);
        
            g.setColor(Color.LIGHT_GRAY);
            drawRotatedLabel(g, "Player 3", JBlackJack.BOARD_WIDTH - CARD_HEIGHT - 50, (JBlackJack.BOARD_WIDTH + fm.stringWidth("Player 3")) / 2 - 16, -90);
        }

        /**
         * Disegna una etichetta normale.
         * 
         * @param g Il contesto grafico su cui disegnare l'etichetta.
         * @param text Il testo dell'etichetta.
         * @param x La coordinata x dell'etichetta.
         * @param y La coordinata y dell'etichetta.
         */
        private void drawLabel(Graphics g, String text, int x, int y) {
            g.drawString(text, x, y);
        }

        /**
         * Disegna una etichetta ruotata.
         * 
         * @param g Il contesto grafico su cui disegnare l'etichetta.
         * @param text Il testo dell'etichetta.
         * @param x La coordinata x dell'etichetta.
         * @param y La coordinata y dell'etichetta.
         * @param angle L'angolo di rotazione dell'etichetta.
         */
        private void drawRotatedLabel(Graphics g, String text, int x, int y, double angle) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform originalTransform = g2d.getTransform();
            g2d.rotate(Math.toRadians(angle), x, y);
            drawLabel(g2d, text, x, y);
            g2d.setTransform(originalTransform);
        }

        /**
         * Disegna le carte nel pannello di gioco.
         * 
         * @param g Il contesto grafico su cui disegnare le carte.
         */
        private void drawCards(Graphics g) {
            dealerCards.forEach(card -> card.draw(g, gameEnded || dealerCards.indexOf(card) > 0));
            playerCards.forEach(card -> card.draw(g, true));
            bot1Cards.forEach(card -> card.draw(g, gameEnded || bot1Cards.indexOf(card) < 2));
            bot2Cards.forEach(card -> card.draw(g, gameEnded || bot2Cards.indexOf(card) < 2));
        }

        /**
         * Mostra il risultato finale del gioco.
         * 
         * @param g Il contesto grafico su cui disegnare il risultato.
         */
        private void showGameResult(Graphics g) {
            int dealerFinalSum = model.reduceAce(model.getDealerSum(), model.getDealerAceCount());
            int playerFinalSum = model.reduceAce(model.getPlayerSum(), model.getPlayerAceCount());
            int bot1FinalSum = model.reduceAce(model.getBot1Sum(), model.getBot1AceCount());
            int bot2FinalSum = model.reduceAce(model.getBot2Sum(), model.getBot2AceCount());

            result = determineResult(playerFinalSum, dealerFinalSum);
            bot1result = determineResult(bot1FinalSum, dealerFinalSum);
            bot2result = determineResult(bot2FinalSum, dealerFinalSum);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.WHITE);

            String message = getResultMessage(result);
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            int messageHeight = fm.getHeight();
            int x = (JBlackJack.BOARD_WIDTH - messageWidth) / 2;
            int y = (JBlackJack.BOARD_HEIGHT - messageHeight) / 2 + fm.getAscent() - 20;

            g.drawString(message, x, y);
        }

        /**
         * Determina il risultato del gioco confrontando la somma finale del giocatore con quella del mazziere.
         * 
         * @param finalSum La somma finale del giocatore.
         * @param dealerFinalSum La somma finale del mazziere.
         * @return Il codice del risultato (1: vinto, 2: perso, 3: pareggio).
         */
        private int determineResult(int finalSum, int dealerFinalSum) {
            if (finalSum > 21) {
                return 2;
            } else if (dealerFinalSum > 21) {
                return 1; 
            } else if (finalSum == dealerFinalSum) {
                return 3;
            } else if (finalSum > dealerFinalSum) {
                return 1; 
            } else {
                return 2; 
            }
        }

        /**
         * Ottiene il messaggio del risultato basato sul codice del risultato.
         * 
         * @param resultCode Il codice del risultato.
         * @return Il messaggio del risultato.
         */
        private String getResultMessage(int resultCode) {
            switch (resultCode) {
                case 1:
                    return "Hai vinto";
                case 2:
                    return "Hai perso";
                case 3:
                    return "Pareggio";
                default:
                    return "";
            }
        }

        /**
         * Aggiunge un listener per il pulsante "Home".
         * 
         * @param listener Il listener da aggiungere al pulsante.
         */
        public void addHomeButtonListener(ActionListener listener) {
            homeButton.addActionListener(listener);
        }
    }

    /**
     * Aggiorna la vista quando il modello cambia.
     * 
     * @param o Il modello osservato.
     * @param arg L'argomento dell'aggiornamento.
     */
    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof Model) {
            Model model = (Model) o;
            updateView(model);
        }
    }
}
