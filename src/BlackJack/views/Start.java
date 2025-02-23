package BlackJack.views;
import BlackJack.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Interfaccia utente all'avvio del gioco Blackjack.
 */
public class Start extends JPanel{
    private final PanelOne panelOne;
    private final PanelTwo panelTwo;
    private final PanelThree panelThree;

    /**
     * Costruisce un nuovo pannello {@code Start} e inizializza i tre pannelli interni.
     * Imposta il layout del pannello principale e aggiunge i pannelli interni nelle posizioni appropriate.
     */
    public Start(){
        setLayout(new BorderLayout());

        panelOne = new PanelOne();
        panelTwo = new PanelTwo();
        panelThree = new PanelThree();

        add(panelOne, BorderLayout.NORTH);
        add(panelTwo, BorderLayout.CENTER);
        add(panelThree, BorderLayout.SOUTH);
    }

    /**
     * Aggiunge un {@code ActionListener} al pulsante di sinistra nel {@code PanelTwo}.
     * 
     * @param listener l'oggetto {@code ActionListener} da aggiungere
     */
    public void addLeftButtonListener(ActionListener listener) {
        panelTwo.addLeftButtonListener(listener);
    }

    /**
     * Aggiunge un {@code ActionListener} al pulsante di destra nel {@code PanelTwo}.
     * 
     * @param listener l'oggetto {@code ActionListener} da aggiungere
     */
    public void addRightButtonListener(ActionListener listener) {
        panelTwo.addRightButtonListener(listener);
    }

    /**
     * Imposta l'immagine dell'avatar nel {@code PanelTwo}.
     * 
     * @param imageName il nome del file dell'immagine dell'avatar
     */
    public void setAvatarImage(String imageName) {
        panelTwo.setAvatarImage(imageName);
    }

    /**
     * Aggiunge un {@code ActionListener} al pulsante "Enter" nel {@code PanelThree}.
     * 
     * @param listener l'oggetto {@code ActionListener} da aggiungere
     */
    public void addEnterButtonListener(ActionListener listener) {
        panelThree.addEnterButtonListener(listener);
    }

    /**
     * Restituisce il nickname inserito nel {@code PanelThree}.
     * 
     * @return il nickname inserito dall'utente
     */
    public String getNickname() {
        return panelThree.getNickname();
    }

    /**
     * La classe {@code PanelOne} rappresenta il pannello superiore contenente il logo del gioco.
     * Estende {@code JPanel} e gestisce la visualizzazione dell'immagine del logo.
     */
    class PanelOne extends JPanel {
        private final Image logoImage;

        /**
         * Costruisce un nuovo {@code PanelOne}, imposta il colore di sfondo e carica l'immagine del logo.
         */
        PanelOne() {
            setBackground(JBlackJack.BACKGROUND_COLOR);
            setPreferredSize(new Dimension(JBlackJack.BOARD_WIDTH, JBlackJack.BOARD_HEIGHT / 3));
            logoImage = new ImageIcon(Start.class.getResource("/BlackJack/resources/images/logo.png")).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int logoWidth = 561;
            int logoHeight = 215;
            g.drawImage(logoImage, (JBlackJack.BOARD_WIDTH / 2) - (logoWidth / 2), (JBlackJack.BOARD_HEIGHT / 6) - (logoHeight / 2), logoWidth, logoHeight, null);
        }
    }

    /**
     * La classe {@code PanelTwo} rappresenta il pannello centrale con i pulsanti di selezione dell'avatar e l'immagine dell'avatar.
     * Estende {@code JPanel} e gestisce la disposizione e le azioni relative all'avatar.
     */
    class PanelTwo extends JPanel {
        private final JButton leftButton;
        private final JButton rightButton;
        private final JLabel avatarLabel;

        /**
         * Costruisce un nuovo {@code PanelTwo}, imposta il layout, aggiunge i componenti e imposta l'immagine predefinita dell'avatar.
         */
        PanelTwo() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel avatarLabelText = new JLabel("Avatar:");
            avatarLabelText.setForeground(Color.WHITE);
            avatarLabelText.setFont(avatarLabelText.getFont().deriveFont(Font.PLAIN, 18f));
            gbc.gridx = 1;
            gbc.gridy = 0;
            add(avatarLabelText, gbc);

            leftButton = new JButton("<");
            leftButton.setPreferredSize(new Dimension(20, 20));
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(leftButton, gbc);

            avatarLabel = new JLabel();
            gbc.gridx = 1;
            gbc.gridy = 1;
            add(avatarLabel, gbc);

            rightButton = new JButton(">");
            rightButton.setPreferredSize(new Dimension(20, 20));
            gbc.gridx = 2;
            gbc.gridy = 1;
            add(rightButton, gbc);

            setPreferredSize(new Dimension(JBlackJack.BOARD_WIDTH, JBlackJack.BOARD_HEIGHT / 2));
            setBackground(JBlackJack.BACKGROUND_COLOR);

            setAvatarImage("avatar1.png");
        }

        /**
         * Aggiunge un {@code ActionListener} al pulsante di sinistra.
         * 
         * @param listener l'oggetto {@code ActionListener} da aggiungere
         */
        public void addLeftButtonListener(ActionListener listener) {
            leftButton.addActionListener(listener);
        }

        /**
         * Aggiunge un {@code ActionListener} al pulsante di destra.
         * 
         * @param listener l'oggetto {@code ActionListener} da aggiungere
         */
        public void addRightButtonListener(ActionListener listener) {
            rightButton.addActionListener(listener);
        }

        /**
         * Imposta l'immagine dell'avatar mostrata nel {@code PanelTwo}.
         * 
         * @param imageName il nome del file dell'immagine dell'avatar
         */
        public void setAvatarImage(String imageName) {
            Image avatarImage = new ImageIcon(getClass().getResource("/BlackJack/resources/images/avatars/" + imageName)).getImage();
            avatarLabel.setIcon(new ImageIcon(avatarImage.getScaledInstance(170, 247, Image.SCALE_SMOOTH)));
        }
    }

    /**
     * La classe {@code PanelThree} rappresenta il pannello inferiore con il campo di testo per il nickname e il pulsante "Enter".
     * Estende {@code JPanel} e gestisce la raccolta del nickname e l'azione di conferma.
     */
    class PanelThree extends JPanel {
        private final JButton enterButton;
        private final JTextField nicknameField;

        /**
         * Costruisce un nuovo {@code PanelThree}, imposta il colore di sfondo e il layout, e aggiunge i componenti per il nickname e il pulsante "Enter".
         */
        PanelThree() {
            setBackground(JBlackJack.BACKGROUND_COLOR);
            setPreferredSize(new Dimension(JBlackJack.BOARD_WIDTH, JBlackJack.BOARD_HEIGHT / 4));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel nicknameLabel = new JLabel("Nickname:");
            nicknameLabel.setForeground(Color.WHITE);
            nicknameLabel.setFont(nicknameLabel.getFont().deriveFont(Font.PLAIN, 18f));
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(nicknameLabel, gbc);

            nicknameField = new JTextField(15);
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(nicknameField, gbc);

            enterButton = new JButton("Enter");
            enterButton.setPreferredSize(new Dimension(100, 30));
            gbc.gridx = 0;
            gbc.gridy = 2;
            add(enterButton, gbc);
        }

        /**
         * Aggiunge un {@code ActionListener} al pulsante "Enter".
         * 
         * @param listener l'oggetto {@code ActionListener} da aggiungere
         */
        public void addEnterButtonListener(ActionListener listener) {
            enterButton.addActionListener(listener);
        }

        /**
         * Restituisce il nickname inserito nel campo di testo.
         * 
         * @return il nickname inserito dall'utente
         */
        public String getNickname() {
            return nicknameField.getText();
        }
    }
}
