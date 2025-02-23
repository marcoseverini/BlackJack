package BlackJack.views;

import BlackJack.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Interfaccia utente durante la fase di puntata prima di ogni partita.
 */
public class Bet extends JPanel {

    // Variabile che rappresenta l'importo totale del giocatore
    private int importo;
    
    // Variabile che rappresenta la puntata corrente
    public int puntata = 0;

    // Bottone per piazzare la puntata
    private JButton betButton;

    // Mappa per associare il valore della chip ai pulsanti circolari
    private final Map<Integer, CircularButton> chipButtons = new LinkedHashMap<>();
    
    // Mappa per associare il valore della chip alle immagini delle chip
    private final Map<Integer, Image> chipImages = new LinkedHashMap<>();
    
    // Mappa per associare l'immagine della chip al valore della chip
    private final Map<Image, Integer> chipValues = new LinkedHashMap<>();

    // Costanti per dimensioni e posizionamento delle chip
    private static final int CHIP_WIDTH = 70;
    private static final int CHIP_HEIGHT = 70;
    private static final int SPACE_BETWEEN_CHIPS = 20;
    private static final int TOTAL_WIDTH = (CHIP_WIDTH * 6) + SPACE_BETWEEN_CHIPS * 5;
    private static final int START_X = (JBlackJack.BOARD_WIDTH - TOTAL_WIDTH) / 2;
    private static final int START_Y = JBlackJack.BOARD_HEIGHT * 11 / 16;

    // Costanti per il diametro del cerchio e l'animazione delle chip
    private static final int CIRCLE_DIAMETER = 50;
    private static final int ANIMATION_DURATION = 300; // Durata in millisecondi
    private static final int FRAMES_PER_SECOND = 150;
    private static final int FRAME_DELAY = 1000 / FRAMES_PER_SECOND;

    // Variabili per gestire l'animazione delle chip
    private int targetX;
    private int targetY = JBlackJack.BOARD_HEIGHT * 2 / 5 - CIRCLE_DIAMETER / 2;
    private boolean isAnimating = false;

    // Timer per l'animazione delle chip
    private Timer animationTimer;
    
    // Lista di cerchi in movimento durante l'animazione delle chip
    private java.util.List<MovingCircle> circles = new ArrayList<>();

    /**
     * Costruttore della classe Bet.
     * Inizializza il pannello, il bottone per la puntata, carica le immagini delle chip e crea i pulsanti delle chip.
     * 
     * @param importo L'importo iniziale del giocatore.
     */
    public Bet(int importo) {
        this.importo = importo;
        setBackground(JBlackJack.BACKGROUND_COLOR);
        setLayout(null);

        initBetButton();
        loadChipImages();
        initChipButtons();
        createAndAddChipButtons();
    }

    /**
     * Inizializza il bottone per piazzare la puntata e lo posiziona nel pannello.
     */
    private void initBetButton() {
        betButton = new JButton("Bet");
        betButton.setBounds(JBlackJack.BOARD_WIDTH / 2 - 50, JBlackJack.BOARD_HEIGHT * 6 / 7 - 25, 100, 50);
        add(betButton);
    }

    /**
     * Carica le immagini delle chip dai file di risorse e le scala alle dimensioni appropriate.
     */
    private void loadChipImages() {
        int[] values = {1, 5, 25, 100, 500, 1000};
        Arrays.stream(values).forEach(value -> {
            URL chipImageURL = getClass().getResource("/BlackJack/resources/images/chips/" + value + ".png");
            ImageIcon chipIcon = new ImageIcon(chipImageURL);
            Image chipImage = chipIcon.getImage().getScaledInstance(CHIP_WIDTH, CHIP_HEIGHT, Image.SCALE_SMOOTH);
            chipImages.put(value, chipImage);
        });
    }

    /**
     * Inizializza i pulsanti circolari per ogni chip e associa i valori delle chip.
     */
    private void initChipButtons() {
        chipImages.forEach((value, image) -> {
            CircularButton button = createChipButton(image);
            chipButtons.put(value, button);
            chipValues.put(image, value);
        });
    }

    /**
     * Crea un pulsante circolare con l'immagine della chip.
     * 
     * @param chipImage L'immagine della chip da utilizzare nel pulsante.
     * @return Un pulsante circolare con l'immagine della chip.
     */
    private CircularButton createChipButton(Image chipImage) {
        return new CircularButton(new ImageIcon(chipImage));
    }

    /**
     * Posiziona i pulsanti delle chip nel pannello e assegna i relativi action listener.
     */
    private void createAndAddChipButtons() {
        List<Map.Entry<Integer, CircularButton>> entries = new ArrayList<>(chipButtons.entrySet());
        IntStream.range(0, entries.size()).forEach(index -> {
            CircularButton button = entries.get(index).getValue();
            int chipValue = entries.get(index).getKey();
            button.setBounds(START_X + (CHIP_WIDTH + SPACE_BETWEEN_CHIPS) * index, START_Y, CHIP_WIDTH, CHIP_HEIGHT);
            button.addActionListener(e -> startAnimationForChip(chipValue));
            add(button);
        });
    }

    /**
     * Avvia l'animazione per la chip selezionata, se non è già in corso un'animazione e se l'importo è sufficiente.
     * 
     * @param chipValue Il valore della chip selezionata.
     */
    private void startAnimationForChip(int chipValue) {
        if (isAnimating || importo < chipValue) {
            if (importo < chipValue) {
                JOptionPane.showMessageDialog(this, "Importo insufficiente per la chip selezionata.");
            }
            return;
        }

        // Suona l'audio della chip e avvia l'animazione
        AudioManager.getInstance().play("src/BlackJack/resources/audio/chip.wav");
        isAnimating = true;

        updatePuntata(chipValue);
        updateImporto(chipValue);

        targetX = JBlackJack.BOARD_WIDTH / 2 - CIRCLE_DIAMETER / 2;
        targetY -= 5;

        Image chipImage = chipImages.get(chipValue);
        int startX = START_X + (CHIP_WIDTH + SPACE_BETWEEN_CHIPS) * getChipIndex(chipValue);

        MovingCircle newCircle = new MovingCircle(startX, START_Y, targetX, targetY, ANIMATION_DURATION / FRAME_DELAY + 1, chipImage);
        circles.add(newCircle);

        disableButtonForChipValue(chipValue);

        // Timer per l'animazione della chip
        animationTimer = new Timer(FRAME_DELAY, evt -> {
            boolean allCirclesFinished = circles.stream().allMatch(MovingCircle::isFinished);
            circles.forEach(MovingCircle::update);
            repaint();
            if (allCirclesFinished) {
                animationTimer.stop();
                isAnimating = false;
                enableButtonForChipValue(chipValue);
            }
        });
        animationTimer.start();
    }

    /**
     * Aggiorna la puntata corrente aggiungendo il valore della chip selezionata.
     * 
     * @param chipValue Il valore della chip selezionata.
     */
    private void updatePuntata(int chipValue) {
        puntata += chipValue;
        repaint();
    }

    /**
     * Aggiorna l'importo totale sottraendo il valore della chip selezionata.
     * 
     * @param chipValue Il valore della chip selezionata.
     */
    private void updateImporto(int chipValue) {
        importo -= chipValue;
        repaint();
    }

    /**
     * Restituisce l'indice della chip corrispondente al valore specificato.
     * 
     * @param chipValue Il valore della chip.
     * @return L'indice della chip.
     */
    private int getChipIndex(int chipValue) {
        return IntStream.range(0, chipButtons.size())
                .filter(i -> new ArrayList<>(chipButtons.keySet()).get(i) == chipValue)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Disabilita il pulsante corrispondente al valore della chip.
     * 
     * @param chipValue Il valore della chip.
     */
    private void disableButtonForChipValue(int chipValue) {
        chipButtons.get(chipValue).setEnabled(false);
    }

    /**
     * Abilita il pulsante corrispondente al valore della chip.
     * 
     * @param chipValue Il valore della chip.
     */
    private void enableButtonForChipValue(int chipValue) {
        chipButtons.get(chipValue).setEnabled(true);
    }

    /**
     * Override del metodo paintComponent per disegnare le informazioni dell'importo e della puntata, 
     * e per visualizzare le chip in movimento.
     * 
     * @param g L'oggetto Graphics per disegnare nel pannello.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Importo = " + importo, 60, 90);
        g.drawString("Puntata = " + puntata, 60, 135);

        circles.forEach(circle -> g.drawImage(circle.getImage(), circle.getX(), circle.getY(), this));
    }

    /**
     * Aggiunge un listener al bottone della puntata.
     * 
     * @param listener L'ActionListener da aggiungere.
     */
    public void addBetButtonListener(ActionListener listener) {
        removeBetButtonListeners();
        betButton.addActionListener(listener);
    }

    /**
     * Rimuove tutti i listener associati al bottone della puntata.
     */
    public void removeBetButtonListeners() {
        for (ActionListener al : betButton.getActionListeners()) {
            betButton.removeActionListener(al);
        }
    }

    /**
     * Aggiunge un listener al pulsante della chip corrispondente al valore specificato.
     * 
     * @param chipValue Il valore della chip.
     * @param listener L'ActionListener da aggiungere.
     */
    public void addChipButtonListener(int chipValue, ActionListener listener) {
        chipButtons.get(chipValue).addActionListener(listener);
    }

    /**
     * Classe interna CircularButton che rappresenta un pulsante circolare.
     */
    public class CircularButton extends JButton {
        public CircularButton(Icon icon) {
            super(icon);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        /**
         * Override del metodo paintComponent per disegnare il pulsante circolare.
         * 
         * @param g L'oggetto Graphics per disegnare il pulsante.
         */
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getModel().isArmed() ? Color.LIGHT_GRAY : getBackground());
            g.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    /**
     * Classe interna MovingCircle che rappresenta una chip in movimento durante l'animazione.
     */
    private class MovingCircle {
        private final int startX, startY;
        private final int targetX, targetY;
        private int x, y;
        private final int totalFrames;
        private int currentFrame;
        private final Image chipImage;

        /**
         * Costruttore della classe MovingCircle.
         * Inizializza la posizione iniziale, la posizione finale, il numero di frame e l'immagine della chip.
         * 
         * @param startX La posizione iniziale sull'asse X.
         * @param startY La posizione iniziale sull'asse Y.
         * @param targetX La posizione finale sull'asse X.
         * @param targetY La posizione finale sull'asse Y.
         * @param totalFrames Il numero totale di frame per l'animazione.
         * @param chipImage L'immagine della chip da animare.
         */
        public MovingCircle(int startX, int startY, int targetX, int targetY, int totalFrames, Image chipImage) {
            this.startX = startX;
            this.startY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.totalFrames = totalFrames;
            this.currentFrame = 0;
            this.x = startX;
            this.y = startY;
            this.chipImage = chipImage;
        }

        /**
         * Aggiorna la posizione del cerchio in base al frame corrente.
         */
        public void update() {
            if (currentFrame < totalFrames) {
                double t = (double) currentFrame / (totalFrames - 1);
                x = (int) (startX + t * (targetX - startX));
                y = (int) (startY + t * (targetY - startY));
                currentFrame++;
            }
        }

        /**
         * Verifica se l'animazione è terminata.
         * 
         * @return true se l'animazione è terminata, false altrimenti.
         */
        public boolean isFinished() {
            return currentFrame >= totalFrames;
        }

        /**
         * Restituisce la posizione X corrente del cerchio.
         * 
         * @return La posizione X.
         */
        public int getX() {
            return x;
        }

        /**
         * Restituisce la posizione Y corrente del cerchio.
         * 
         * @return La posizione Y.
         */
        public int getY() {
            return y;
        }

        /**
         * Restituisce l'immagine della chip associata al cerchio.
         * 
         * @return L'immagine della chip.
         */
        public Image getImage() {
            return chipImage;
        }
    }
}
