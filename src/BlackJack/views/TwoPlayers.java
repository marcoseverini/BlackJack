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
 * Classe che rappresenta la vista per una partita di Blackjack a due giocatori.
 */
public class TwoPlayers extends JPanel implements GameView, Observer {

    public GamePanel gamePanel;
    private JPanel buttonPanel;

    private JButton hitButton;
    private JButton stayButton;
    private JButton homeButton;

    public static final int CARD_WIDTH = 78;
    public static final int CARD_HEIGHT = 110;

    private int result;
    private int bot1Result;
    private boolean gameEnded = false;

    /**
     * Costruttore della classe. Imposta il layout e inizializza i componenti.
     */
    public TwoPlayers() {
        setupLayout();
        initializeComponents();
    }

    /**
     * Configura il layout del pannello.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(JBlackJack.BACKGROUND_COLOR);
    }

    /**
     * Inizializza i componenti grafici del pannello.
     */
    private void initializeComponents() {
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea il pannello con i pulsanti di azione.
     * 
     * @return Il pannello contenente i pulsanti.
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
     * Crea un pulsante con il testo specificato.
     * 
     * @param text Il testo del pulsante.
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
     * Aggiorna la vista con i dati del modello.
     * 
     * @param model Il modello che contiene i dati di gioco.
     */
    @Override
    public void updateView(Model model) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/card.wav");
        gamePanel.setModel(model);

        if (!gameEnded && !stayButton.isEnabled()) {
            gameEnded = true;
            gamePanel.startDealerRevealAnimation();
            gamePanel.startBotRevealAnimation();
        }

        gamePanel.repaint();
    }

    /**
     * Restituisce il risultato del giocatore.
     * 
     * @return Il risultato del giocatore.
     */
    public int getResult() {
        return result;
    }

    /**
     * Restituisce il risultato del bot.
     * 
     * @return Il risultato del bot.
     */
    public int getBot1Result() {
        return bot1Result;
    }

    /**
     * Classe interna che rappresenta il pannello di gioco.
     */
    public class GamePanel extends JPanel {
        private Model model;
        private List<AnimationCard.AnimatedCard2> playerCards = new ArrayList<>();
        private List<AnimationCard.AnimatedCard2> dealerCards = new ArrayList<>();
        private List<AnimationCard.AnimatedCard2> botCards = new ArrayList<>();
        private Timer animationTimer;
        private boolean dealerCardsInitialized = false;
        private boolean botCardsInitialized = false;

        /**
         * Costruttore della classe GamePanel. Configura il pannello.
         */
        public GamePanel() {
            setupPanel();
        }

        /**
         * Configura il pannello, inclusi i pulsanti e il timer per le animazioni.
         */
        private void setupPanel() {
            setLayout(null);
            setupHomeButton();
            setupAnimationTimer();
        }

        /**
         * Configura il pulsante "Home" e lo posiziona nel pannello.
         */
        private void setupHomeButton() {
            homeButton = new JButton("Home");
            int homeButtonWidth = 100;
            int homeButtonHeight = 30;
            int x = (JBlackJack.BOARD_WIDTH / 2) - (homeButtonWidth / 2);
            int y = (JBlackJack.BOARD_HEIGHT / 2) - (homeButtonHeight / 2) + 20;
            homeButton.setBounds(x, y, homeButtonWidth, homeButtonHeight);
            homeButton.setVisible(false);
            add(homeButton);
        }

        /**
         * Configura il timer per le animazioni delle carte.
         */
        private void setupAnimationTimer() {
            animationTimer = new Timer(16, e -> {
                updateCardPositions();
                repaint();
            });
        }

        /**
         * Imposta il modello di gioco e avvia le animazioni.
         * 
         * @param model Il modello che contiene i dati di gioco.
         */
        public void setModel(Model model) {
            this.model = model;
            updatePlayerCardList(model.getPlayerHand());

            if (!dealerCardsInitialized) {
                initializeDealerCards(model.getDealerHand(), model.getHiddenCard());
                dealerCardsInitialized = true;
            }

            if (!botCardsInitialized) {
                initializeBotCards(model.getBot1Hand());
                botCardsInitialized = true;
            }

            animationTimer.start();
        }

        /**
         * Inizializza le carte del dealer e le posiziona.
         * 
         * @param hand La mano del dealer.
         * @param hiddenCard La carta nascosta del dealer.
         */
        private void initializeDealerCards(List<Model.Card> hand, Model.Card hiddenCard) {
            dealerCards.clear();
            dealerCards.add(createAnimatedCard(hiddenCard, JBlackJack.BOARD_WIDTH, 35, false));
            dealerCards.add(createAnimatedCard(hand.get(0), JBlackJack.BOARD_WIDTH + CARD_WIDTH + 5, 35, false));
            updateDealerCardPositions();
        }

        /**
         * Inizializza le carte del bot e le posiziona.
         * 
         * @param hand La mano del bot.
         */
        private void initializeBotCards(List<Model.Card> hand) {
            botCards.clear();
            hand.forEach(card -> botCards.add(createAnimatedCard(card, 45, JBlackJack.BOARD_HEIGHT, true)));
            updateBotCardPositions();
        }

        /**
         * Crea una carta animata.
         * 
         * @param card La carta da animare.
         * @param x La posizione X della carta.
         * @param y La posizione Y della carta.
         * @param isBot Indica se la carta è del bot.
         * @return La carta animata creata.
         */
        private AnimationCard.AnimatedCard2 createAnimatedCard(Model.Card card, float x, float y, boolean isBot) {
            return new AnimationCard.AnimatedCard2(card, x, y, isBot);
        }

        /**
         * Aggiorna la lista delle carte del giocatore.
         * 
         * @param hand La mano del giocatore.
         */
        private void updatePlayerCardList(List<Model.Card> hand) {
            IntStream.range(playerCards.size(), hand.size()).forEach(i -> 
                playerCards.add(createAnimatedCard(hand.get(i), JBlackJack.BOARD_WIDTH, 620, false))
            );
            updatePlayerCardPositions();
        }

        /**
         * Aggiorna le posizioni di tutte le carte.
         */
        private void updateCardPositions() {
            updatePlayerCardPositions();
            updateDealerCardPositions();
            updateBotCardPositions();
        }

        /**
         * Aggiorna le posizioni delle carte del giocatore.
         */
        private void updatePlayerCardPositions() {
            int totalWidth = playerCards.size() * CARD_WIDTH + (playerCards.size() - 1) * 5;
            int startX = (JBlackJack.BOARD_WIDTH - totalWidth) / 2;

            IntStream.range(0, playerCards.size()).forEach(i -> {
                AnimationCard.AnimatedCard2 card = playerCards.get(i);
                float targetX = startX + (CARD_WIDTH + 5) * i;
                card.setTarget(targetX, 620);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Aggiorna le posizioni delle carte del dealer.
         */
        private void updateDealerCardPositions() {
            int totalWidth = dealerCards.size() * CARD_WIDTH + (dealerCards.size() - 1) * 5;
            int startX = (JBlackJack.BOARD_WIDTH - totalWidth) / 2;

            IntStream.range(0, dealerCards.size()).forEach(i -> {
                AnimationCard.AnimatedCard2 card = dealerCards.get(i);
                float targetX = startX + (CARD_WIDTH + 5) * i;
                card.setTarget(targetX, 35);
                card.update();
                card.setVisible(true);
            });
        }

        /**
         * Aggiorna le posizioni delle carte del bot.
         */
        private void updateBotCardPositions() {
            int totalHeight = botCards.size() * CARD_WIDTH + (botCards.size() - 1) * 5;
            int startY = (JBlackJack.BOARD_HEIGHT - totalHeight) / 2;

            IntStream.range(0, botCards.size()).forEach(i -> {
                AnimationCard.AnimatedCard2 card = botCards.get(i);
                float targetY = startY + (CARD_WIDTH + 5) * i;
                card.setTarget(45, targetY);
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

            IntStream.range(existingCards, dealerHand.size() + 1).forEach(i -> {
                Model.Card card = (i == 0) ? model.getHiddenCard() : dealerHand.get(i - 1);
                dealerCards.add(createAnimatedCard(card, JBlackJack.BOARD_WIDTH, 35, false));
            });

            updateDealerCardPositions();
        }

        /**
         * Avvia l'animazione di rivelazione delle carte del bot.
         */
        public void startBotRevealAnimation() {
            List<Model.Card> botHand = model.getBot1Hand();
            int existingCards = botCards.size();

            IntStream.range(existingCards, botHand.size()).forEach(i -> 
                botCards.add(createAnimatedCard(botHand.get(i), 45, JBlackJack.BOARD_HEIGHT, true))
            );

            updateBotCardPositions();
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
         * Disegna le carte sul pannello.
         * 
         * @param g L'oggetto Graphics utilizzato per disegnare le carte.
         */
        private void drawCards(Graphics g) {
            dealerCards.forEach(card -> card.draw(g, gameEnded || dealerCards.indexOf(card) > 0));
            playerCards.forEach(card -> card.draw(g, true));
            botCards.forEach(card -> card.draw(g, gameEnded || botCards.indexOf(card) < 2));
        }

        /**
         * Disegna le etichette sul pannello.
         * 
         * @param g L'oggetto Graphics utilizzato per disegnare le etichette.
         */
        private void drawLabels(Graphics g) {
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();

            drawDealerLabel(g, fm);
            drawPlayer2Label(g, fm);
        }

        /**
         * Disegna l'etichetta "Dealer".
         * 
         * @param g L'oggetto Graphics utilizzato per disegnare l'etichetta.
         * @param fm L'oggetto FontMetrics utilizzato per calcolare la larghezza del testo.
         */
        private void drawDealerLabel(Graphics g, FontMetrics fm) {
            String dealerMessage = "Dealer";
            int dealerMessageWidth = fm.stringWidth(dealerMessage);
            int xDealer = (JBlackJack.BOARD_WIDTH - dealerMessageWidth) / 2;
            g.drawString(dealerMessage, xDealer, 20);
        }

        /**
         * Disegna l'etichetta "Player 2" ruotata di 90 gradi.
         * 
         * @param g L'oggetto Graphics utilizzato per disegnare l'etichetta.
         * @param fm L'oggetto FontMetrics utilizzato per calcolare la larghezza del testo.
         */
        private void drawPlayer2Label(Graphics g, FontMetrics fm) {
            String player2Message = "Player 2";
            g.setColor(new Color(185, 185, 185));
            int player2Width = fm.stringWidth(player2Message);
            int xPlayer2 = (JBlackJack.BOARD_WIDTH - player2Width) / 2 - 16;

            Graphics2D g2d = (Graphics2D) g;
            AffineTransform originalTransform = g2d.getTransform();
            AffineTransform rotatedTransform = new AffineTransform();
            rotatedTransform.rotate(Math.toRadians(90), CARD_HEIGHT + 50, xPlayer2);
            g2d.setTransform(rotatedTransform);

            g2d.drawString(player2Message, CARD_HEIGHT + 50, xPlayer2);
            g2d.setTransform(originalTransform);
        }

        /**
         * Mostra il risultato della partita.
         * 
         * @param g L'oggetto Graphics utilizzato per disegnare il risultato.
         */
        private void showGameResult(Graphics g) {
            int dealerFinalSum = model.reduceAce(model.getDealerSum(), model.getDealerAceCount());
            int playerFinalSum = model.reduceAce(model.getPlayerSum(), model.getPlayerAceCount());
            int bot1FinalSum = model.reduceAce(model.getBot1Sum(), model.getBot1AceCount());

            determineResults(playerFinalSum, dealerFinalSum, bot1FinalSum);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.WHITE);

            FontMetrics fm = g.getFontMetrics();
            String message = getResultMessage(playerFinalSum, dealerFinalSum);
            drawResultMessage(g, fm, message);
        }

        /**
         * Determina i risultati della partita.
         * 
         * @param playerFinalSum La somma finale delle carte del giocatore.
         * @param dealerFinalSum La somma finale delle carte del dealer.
         * @param bot1FinalSum La somma finale delle carte del bot.
         */
        private void determineResults(int playerFinalSum, int dealerFinalSum, int bot1FinalSum) {
            result = calculateResult(playerFinalSum, dealerFinalSum);
            bot1Result = calculateResult(bot1FinalSum, dealerFinalSum);
        }

        /**
         * Calcola il risultato della partita.
         * 
         * @param playerSum La somma delle carte del giocatore.
         * @param dealerSum La somma delle carte del dealer.
         * @return Il risultato della partita (1 = vittoria, 2 = sconfitta, 3 = pareggio).
         */
        private int calculateResult(int playerSum, int dealerSum) {
            if (playerSum > 21) return 2; // Perdita
            if (dealerSum > 21) return 1; // Vittoria
            if (playerSum == dealerSum) return 3; // Pareggio
            return playerSum > dealerSum ? 1 : 2; // Vittoria o Perdita
        }

        /**
         * Restituisce il messaggio di risultato della partita.
         * 
         * @param playerFinalSum La somma finale delle carte del giocatore.
         * @param dealerFinalSum La somma finale delle carte del dealer.
         * @return Il messaggio di risultato.
         */
        private String getResultMessage(int playerFinalSum, int dealerFinalSum) {
            if (playerFinalSum > 21) return "Hai perso";
            if (dealerFinalSum > 21) return "Hai vinto";
            if (playerFinalSum == dealerFinalSum) return "Pareggio";
            return playerFinalSum > dealerFinalSum ? "Hai vinto" : "Hai perso";
        }

        /**
         * Disegna il messaggio di risultato sul pannello.
         * 
         * @param g L'oggetto Graphics utilizzato per disegnare il messaggio.
         * @param fm L'oggetto FontMetrics utilizzato per calcolare le dimensioni del messaggio.
         * @param message Il messaggio di risultato da disegnare.
         */
        private void drawResultMessage(Graphics g, FontMetrics fm, String message) {
            int messageWidth = fm.stringWidth(message);
            int messageHeight = fm.getHeight();
            int x = (JBlackJack.BOARD_WIDTH - messageWidth) / 2;
            int y = (JBlackJack.BOARD_HEIGHT - messageHeight) / 2 + fm.getAscent() - 20;

            g.drawString(message, x, y);
        }

        /**
         * Aggiunge un listener al pulsante "Home".
         * 
         * @param listener L'ascoltatore da aggiungere al pulsante.
         */
        public void addHomeButtonListener(ActionListener listener) {
            homeButton.addActionListener(listener);
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
