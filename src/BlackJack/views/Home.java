package BlackJack.views;

import BlackJack.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Schermata Home.
 */
public class Home extends JPanel {

    // Variabili di istanza per tenere traccia del nickname, conteggio delle vittorie, sconfitte, pareggi e importo
    private String nickname;
    private int winCount;
    private int defeatCount;
    private int drawCount;
    private int importo;

    // Immagini per gli avatar dei bot
    private Image avatarBot1image;
    private Image avatarBot2image;

    // Variabili di istanza per conteggiare vittorie, sconfitte e pareggi dei bot
    private int winCountBot1;
    private int defeatCountBot1;
    private int drawCountBot1;

    private int winCountBot2;
    private int defeatCountBot2;
    private int drawCountBot2;

    // Immagine dell'avatar del giocatore
    private Image avatarImage;

    // Pannelli personalizzati usati nell'interfaccia
    private PanelOne panelOne;
    private PanelTwo panelTwo;

    /**
     * Costruttore della classe Home che inizializza i componenti della GUI.
     *
     * @param nickname         Il nickname del giocatore.
     * @param avatar           L'indice dell'avatar del giocatore.
     * @param winCount         Numero di vittorie del giocatore.
     * @param defeatCount      Numero di sconfitte del giocatore.
     * @param drawCount        Numero di pareggi del giocatore.
     * @param importo          Importo attuale del giocatore.
     * @param avatarBot1       L'indice dell'avatar del primo bot.
     * @param avatarBot2       L'indice dell'avatar del secondo bot.
     * @param winCountBot1     Numero di vittorie del primo bot.
     * @param defeatCountBot1  Numero di sconfitte del primo bot.
     * @param drawCountBot1    Numero di pareggi del primo bot.
     * @param winCountBot2     Numero di vittorie del secondo bot.
     * @param defeatCountBot2  Numero di sconfitte del secondo bot.
     * @param drawCountBot2    Numero di pareggi del secondo bot.
     */
    public Home(String nickname, int avatar, int winCount, int defeatCount, int drawCount, int importo,
                int avatarBot1, int avatarBot2, int winCountBot1, int defeatCountBot1, int drawCountBot1,
                int winCountBot2, int defeatCountBot2, int drawCountBot2) {

        // Inizializza le variabili di istanza con i parametri passati
        this.nickname = nickname;
        this.avatarImage = loadAvatarImage(avatar);
        this.winCount = winCount;
        this.defeatCount = defeatCount;
        this.drawCount = drawCount;
        this.importo = importo;

        this.avatarBot1image = loadAvatarImage(avatarBot1);
        this.avatarBot2image = loadAvatarImage(avatarBot2);
        this.winCountBot1 = winCountBot1;
        this.defeatCountBot1 = defeatCountBot1;
        this.drawCountBot1 = drawCountBot1;
        this.winCountBot2 = winCountBot2;
        this.defeatCountBot2 = defeatCountBot2;
        this.drawCountBot2 = drawCountBot2;

        // Configura il layout e aggiunge i pannelli personalizzati
        setLayout(new BorderLayout());
        panelOne = new PanelOne();
        panelTwo = new PanelTwo();

        add(panelOne, BorderLayout.NORTH);
        add(panelTwo, BorderLayout.SOUTH);
    }

    /**
     * Carica l'immagine dell'avatar in base all'indice passato.
     *
     * @param avatar L'indice dell'avatar.
     * @return L'immagine dell'avatar ridimensionata.
     */
    private Image loadAvatarImage(int avatar) {
        String imagePath = String.format("/BlackJack/resources/images/avatars/avatar%d.png", avatar);
        Image image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        return image.getScaledInstance(144, 210, Image.SCALE_SMOOTH);
    }

    /**
     * Aggiunge un listener al pulsante "1 Player".
     *
     * @param listener L'ActionListener da aggiungere.
     */
    public void addOnePlayerButtonListener(ActionListener listener) {
        panelTwo.addOnePlayerButtonListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "2 Players".
     *
     * @param listener L'ActionListener da aggiungere.
     */
    public void addTwoPlayersButtonListener(ActionListener listener) {
        panelTwo.addTwoPlayerButtonListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "3 Players".
     *
     * @param listener L'ActionListener da aggiungere.
     */
    public void addThreePlayersButtonListener(ActionListener listener) {
        panelTwo.addThreePlayerButtonListener(listener);
    }

    // Classe interna PanelOne, un pannello che visualizza gli avatar e le statistiche dei giocatori
    class PanelOne extends JPanel {

        /**
         * Costruttore della classe PanelOne. Configura il layout e aggiunge i componenti al pannello.
         */
        PanelOne() {
            setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
            setBackground(JBlackJack.BACKGROUND_COLOR);
            setPreferredSize(new Dimension(JBlackJack.BOARD_WIDTH, JBlackJack.BOARD_HEIGHT / 3));
            setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

            Font firstFont = new Font("Arial", Font.PLAIN, 24);
            Font smallerFont = new Font("Arial", Font.PLAIN, 14);

            // Crea e aggiunge i pannelli per l'avatar del giocatore e dei bot
            JPanel firstAvatarPanel = createAvatarPanel(nickname, avatarImage, winCount, defeatCount, drawCount, firstFont, 120, 170);
            add(firstAvatarPanel);

            add(Box.createRigidArea(new Dimension(30, 0)));

            JPanel secondAvatarPanel = createAvatarPanel("Player 2", avatarBot1image, winCountBot1, defeatCountBot1, drawCountBot1, smallerFont, 90, 130);
            add(secondAvatarPanel);

            JPanel thirdAvatarPanel = createAvatarPanel("Player 3", avatarBot2image, winCountBot2, defeatCountBot2, drawCountBot2, smallerFont, 90, 130);
            add(thirdAvatarPanel);
        }

        /**
         * Crea un pannello che contiene l'avatar di un giocatore o bot e le sue statistiche.
         *
         * @param nickname      Il nickname del giocatore o bot.
         * @param avatarImage   L'immagine dell'avatar.
         * @param winCount      Numero di vittorie.
         * @param defeatCount   Numero di sconfitte.
         * @param drawCount     Numero di pareggi.
         * @param font          Il font da utilizzare per i testi.
         * @param avatarWidth   Larghezza dell'avatar.
         * @param avatarHeight  Altezza dell'avatar.
         * @return Un JPanel contenente l'avatar e le statistiche.
         */
        private JPanel createAvatarPanel(String nickname, Image avatarImage, int winCount, int defeatCount, int drawCount, Font font, int avatarWidth, int avatarHeight) {
            JPanel avatarPanel = new JPanel();
            avatarPanel.setLayout(new BorderLayout());
            avatarPanel.setBackground(JBlackJack.BACKGROUND_COLOR);

            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
            imagePanel.setBackground(JBlackJack.BACKGROUND_COLOR);

            // Ridimensiona l'immagine dell'avatar e crea un'etichetta per essa
            Image scaledAvatarImage = avatarImage.getScaledInstance(avatarWidth, avatarHeight, Image.SCALE_SMOOTH);
            JLabel avatarLabel = new JLabel(new ImageIcon(scaledAvatarImage));
            avatarLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
            imagePanel.add(avatarLabel);

            // Etichetta per il nickname
            JLabel descriptionLabel = new JLabel(nickname);
            descriptionLabel.setForeground(Color.WHITE);
            descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
            descriptionLabel.setFont(font);
            imagePanel.add(descriptionLabel);

            avatarPanel.add(imagePanel, BorderLayout.WEST);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(JBlackJack.BACKGROUND_COLOR);

            // Crea etichette per le statistiche (vittorie, sconfitte, pareggi)
            String[] labelsText = {"Vittorie: " + winCount, "Sconfitte: " + defeatCount, "Pareggi: " + drawCount};
            for (String text : labelsText) {
                JLabel label = new JLabel(text);
                label.setForeground(Color.WHITE);
                label.setFont(font);
                textPanel.add(label);
                textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));
            avatarPanel.add(textPanel, BorderLayout.CENTER);

            return avatarPanel;
        }
    }

    // Classe interna PanelTwo, un pannello che gestisce i pulsanti per selezionare il numero di giocatori
    public class PanelTwo extends JPanel {

        private JButton onePlayerButton;
        private JButton twoPlayerButton;
        private JButton threePlayerButton;

        /**
         * Costruttore della classe PanelTwo. Configura il layout e aggiunge i componenti al pannello.
         */
        public PanelTwo() {
            setPreferredSize(new Dimension(JBlackJack.BOARD_WIDTH, (JBlackJack.BOARD_HEIGHT * 2) / 3));
            setBackground(JBlackJack.BACKGROUND_COLOR);
            setLayout(new BorderLayout());

            // Etichetta che visualizza l'importo
            JLabel titleLabel = new JLabel("$" + importo, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBorder(new EmptyBorder(30, 0, 0, 0));
            add(titleLabel, BorderLayout.NORTH);

            // Pannello che contiene i pulsanti con le carte
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            cardPanel.setOpaque(false);

            // Aggiunge i pulsanti delle carte al pannello
            onePlayerButton = addCard(cardPanel, "/BlackJack/resources/images/cards/A-C.png", "1 Player");
            twoPlayerButton = addCard(cardPanel, "/BlackJack/resources/images/cards/2-H.png", "2 Players");
            threePlayerButton = addCard(cardPanel, "/BlackJack/resources/images/cards/3-S.png", "3 Players");

            add(cardPanel, BorderLayout.CENTER);
        }

        /**
         * Aggiunge una carta (pulsante) al pannello specificato.
         *
         * @param panel     Il pannello a cui aggiungere la carta.
         * @param imagePath Il percorso dell'immagine della carta.
         * @param labelText Il testo dell'etichetta sotto la carta.
         * @return Il pulsante creato e aggiunto al pannello.
         */
        private JButton addCard(JPanel panel, String imagePath, String labelText) {
            Font font = new Font("Arial", Font.PLAIN, 20);

            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image image = icon.getImage().getScaledInstance(170, 238, Image.SCALE_SMOOTH);
            JButton button = new JButton(new ImageIcon(image));
            button.setPreferredSize(new Dimension(170, 238));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);

            JPanel cardWrapper = new JPanel();
            cardWrapper.setLayout(new BorderLayout());
            cardWrapper.setOpaque(false);

            JPanel spacer = new JPanel();
            spacer.setPreferredSize(new Dimension(170, 40));
            spacer.setOpaque(false);

            cardWrapper.add(spacer, BorderLayout.NORTH);
            cardWrapper.add(button, BorderLayout.CENTER);

            JLabel label = new JLabel(labelText, SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(15, 0, 0, 0));
            label.setFont(font);
            label.setForeground(Color.WHITE);
            cardWrapper.add(label, BorderLayout.SOUTH);

            panel.add(cardWrapper);

            return button;
        }

        /**
         * Aggiunge un listener al pulsante "1 Player".
         *
         * @param listener L'ActionListener da aggiungere.
         */
        public void addOnePlayerButtonListener(ActionListener listener) {
            onePlayerButton.addActionListener(listener);
        }

        /**
         * Aggiunge un listener al pulsante "2 Players".
         *
         * @param listener L'ActionListener da aggiungere.
         */
        public void addTwoPlayerButtonListener(ActionListener listener) {
            twoPlayerButton.addActionListener(listener);
        }

        /**
         * Aggiunge un listener al pulsante "3 Players".
         *
         * @param listener L'ActionListener da aggiungere.
         */
        public void addThreePlayerButtonListener(ActionListener listener) {
            threePlayerButton.addActionListener(listener);
        }
    }
}
