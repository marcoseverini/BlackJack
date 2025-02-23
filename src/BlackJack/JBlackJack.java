package BlackJack;

import BlackJack.views.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JBlackJack {

    // Costanti per la larghezza e l'altezza del pannello principale del gioco
    public static final int BOARD_WIDTH = 800; 
    public static final int BOARD_HEIGHT = 800;

    // Colore di sfondo del gioco
    public static final Color BACKGROUND_COLOR = new Color(53, 101, 77);

    // Riferimenti ai vari pannelli del gioco
    private Start start;
    private Home home;
    private OnePlayer onePlayer;
    private TwoPlayers twoPlayers;
    private ThreePlayers threePlayers;
    private Bet betPanel;

    // Variabili di stato del gioco
    private int avatar = 1; // Avatar del giocatore
    private int importo = 5000; // Importo iniziale del giocatore
    private int winCount = 0, defeatCount = 0, drawCount = 0; // Contatori delle partite vinte, perse e pareggiate
    private int avatarBot1, winCountBot1 = 0, defeatCountBot1 = 0, drawCountBot1 = 0; // Contatori per il primo bot
    private int avatarBot2, winCountBot2 = 0, defeatCountBot2 = 0, drawCountBot2 = 0; // Contatori per il secondo bot

    // Finestra principale del gioco
    private JFrame frame;

    /**
     * Metodo principale per avviare l'applicazione BlackJack.
     * @param args argomenti della riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        JBlackJack main = new JBlackJack();
        main.setupGUI();
    }

    /**
     * Configura la GUI principale del gioco BlackJack.
     * Inizializza la finestra e imposta il pannello iniziale.
     */
    private void setupGUI() {
        frame = new JFrame("Black Jack");
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inizializza il pannello Start
        start = new Start();
        frame.setLayout(new BorderLayout());
        frame.add(start, BorderLayout.CENTER);
        frame.setVisible(true);

        // Aggiunge listener ai bottoni del pannello Start
        start.addEnterButtonListener(e -> startGame());
        start.addLeftButtonListener(e -> switchAvatarLeft());
        start.addRightButtonListener(e -> switchAvatarRight());
    }

    /**
     * Inizia il gioco dopo che l'utente ha inserito un nickname valido.
     * Controlla la validità del nickname e avvia il gioco se corretto.
     */
    private void startGame() {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click.wav");
        String nickname = start.getNickname();

        // Controlla che il nickname non sia vuoto e che non superi i 10 caratteri
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nickname non valido. Per favore, inserisci un nickname.");
        } else if (nickname.length() > 10) {
            JOptionPane.showMessageDialog(frame, "Nickname non valido. Lunghezza massima: 10 caratteri.");
        } else {
            generateBotAvatars(); // Genera gli avatar casuali per i bot
            // Inizializza il pannello Home con i dati del giocatore e dei bot
            home = new Home(nickname, avatar, winCount, defeatCount, drawCount, importo, avatarBot1, avatarBot2, winCountBot1, defeatCountBot1, drawCountBot1, winCountBot2, defeatCountBot2, drawCountBot2);
            switchToPanel(home); // Cambia il pannello visualizzato
            // Aggiunge listener ai bottoni del pannello Home per selezionare la modalità di gioco
            home.addOnePlayerButtonListener(e -> bet(nickname, avatar, 1));
            home.addTwoPlayersButtonListener(e -> bet(nickname, avatar, 2));
            home.addThreePlayersButtonListener(e -> bet(nickname, avatar, 3));
        }
    }

    /**
     * Genera gli avatar per i bot selezionandoli casualmente tra quelli disponibili.
     * Evita di assegnare ai bot lo stesso avatar del giocatore.
     */
    private void generateBotAvatars() {
        List<Integer> numbers = new ArrayList<>();
        // Aggiunge i numeri degli avatar disponibili (escluso quello del giocatore)
        for (int i = 1; i <= 4; i++) {
            if (i != avatar) {
                numbers.add(i);
            }
        }
        Collections.shuffle(numbers); // Mescola la lista degli avatar
        avatarBot1 = numbers.get(0); // Assegna l'avatar al primo bot
        avatarBot2 = numbers.get(1); // Assegna l'avatar al secondo bot
    }

    /**
     * Cambia l'avatar del giocatore spostandosi a sinistra nell'elenco degli avatar disponibili.
     */
    private void switchAvatarLeft() {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        avatar = (avatar == 1) ? 4 : avatar - 1; // Se l'avatar è il primo, passa all'ultimo, altrimenti decrementa
        start.setAvatarImage("avatar" + avatar + ".png"); // Imposta la nuova immagine dell'avatar
    }

    /**
     * Cambia l'avatar del giocatore spostandosi a destra nell'elenco degli avatar disponibili.
     */
    private void switchAvatarRight() {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        avatar = (avatar == 4) ? 1 : avatar + 1; // Se l'avatar è l'ultimo, passa al primo, altrimenti incrementa
        start.setAvatarImage("avatar" + avatar + ".png"); // Imposta la nuova immagine dell'avatar
    }

    /**
     * Apre il pannello per le scommesse e inizia il gioco in base al numero di giocatori selezionati.
     * @param nickname nickname del giocatore
     * @param avatar avatar del giocatore
     * @param players numero di giocatori (1, 2 o 3)
     */
    private void bet(String nickname, int avatar, int players) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click.wav");
        betPanel = new Bet(importo); // Crea il pannello delle scommesse
        switchToPanel(betPanel); // Cambia il pannello visualizzato
        betPanel.addBetButtonListener(e -> startGameWithPlayers(nickname, avatar, players)); // Aggiunge listener al pulsante della scommessa
    }

    /**
     * Inizia la partita in base al numero di giocatori selezionati.
     * @param nickname nickname del giocatore
     * @param avatar avatar del giocatore
     * @param players numero di giocatori (1, 2 o 3)
     */
    private void startGameWithPlayers(String nickname, int avatar, int players) {
        if (players == 1) {
            onePlayerSelected(nickname, avatar); // Inizia una partita a giocatore singolo
        } else if (players == 2) {
            twoPlayersSelected(nickname, avatar); // Inizia una partita a due giocatori
        } else if (players == 3) {
            threePlayersSelected(nickname, avatar); // Inizia una partita a tre giocatori
        }
    }

    /**
     * Configura e avvia una partita a giocatore singolo.
     * @param nickname nickname del giocatore
     * @param avatar avatar del giocatore
     */
    private void onePlayerSelected(String nickname, int avatar) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        onePlayer = new OnePlayer(); // Crea il pannello per un giocatore
        Model model = new Model(1); // Inizializza il modello del gioco per un giocatore
        model.addObserver(onePlayer); // Aggiunge il pannello come osservatore del modello
        model.startNewGame(); // Avvia una nuova partita
        new Controller(model, onePlayer); // Crea un controller per gestire il gioco
        switchToPanel(onePlayer); // Cambia il pannello visualizzato
        // Aggiunge listener per il ritorno alla schermata Home dopo la partita
        onePlayer.gamePanel.addHomeButtonListener(e -> backHome(nickname, avatar, onePlayer.getResult(), 0, 0));
    }

    /**
     * Configura e avvia una partita a due giocatori.
     * @param nickname nickname del giocatore
     * @param avatar avatar del giocatore
     */
    private void twoPlayersSelected(String nickname, int avatar) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        twoPlayers = new TwoPlayers(); // Crea il pannello per due giocatori
        Model model = new Model(2); // Inizializza il modello del gioco per due giocatori
        model.addObserver(twoPlayers); // Aggiunge il pannello come osservatore del modello
        model.startNewGame(); // Avvia una nuova partita
        new Controller(model, twoPlayers); // Crea un controller per gestire il gioco
        switchToPanel(twoPlayers); // Cambia il pannello visualizzato
        // Aggiunge listener per il ritorno alla schermata Home dopo la partita
        twoPlayers.gamePanel.addHomeButtonListener(e -> backHome(nickname, avatar, twoPlayers.getResult(), twoPlayers.getBot1Result(), 0));
    }

    /**
     * Configura e avvia una partita a tre giocatori.
     * @param nickname nickname del giocatore
     * @param avatar avatar del giocatore
     */
    private void threePlayersSelected(String nickname, int avatar) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        threePlayers = new ThreePlayers(); // Crea il pannello per tre giocatori
        Model model = new Model(3); // Inizializza il modello del gioco per tre giocatori
        model.addObserver(threePlayers); // Aggiunge il pannello come osservatore del modello
        model.startNewGame(); // Avvia una nuova partita
        new Controller(model, threePlayers); // Crea un controller per gestire il gioco
        switchToPanel(threePlayers); // Cambia il pannello visualizzato
        // Aggiunge listener per il ritorno alla schermata Home dopo la partita
        threePlayers.gamePanel.addHomeButtonListener(e -> backHome(nickname, avatar, threePlayers.getResult(), threePlayers.getBot1Result(), threePlayers.getBot2Result()));
    }

    /**
     * Ritorna al pannello Home dopo una partita e aggiorna i contatori delle vittorie, sconfitte e pareggi.
     * @param nickname nickname del giocatore
     * @param avatar avatar del giocatore
     * @param result risultato della partita per il giocatore (1=vittoria, 2=sconfitta, 3=pareggio)
     * @param bot1result risultato della partita per il primo bot
     * @param bot2result risultato della partita per il secondo bot
     */
    private void backHome(String nickname, int avatar, int result, int bot1result, int bot2result) {
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click.wav");

        // Aggiorna i contatori basati sui risultati della partita
        updateCounts(result, bot1result, bot2result);
        // Ritorna al pannello Home con i dati aggiornati
        home = new Home(nickname, avatar, winCount, defeatCount, drawCount, importo, avatarBot1, avatarBot2, winCountBot1, defeatCountBot1, drawCountBot1, winCountBot2, defeatCountBot2, drawCountBot2);
        switchToPanel(home); // Cambia il pannello visualizzato

        // Aggiunge listener per selezionare nuovamente la modalità di gioco
        home.addOnePlayerButtonListener(e -> bet(nickname, avatar, 1));
        home.addTwoPlayersButtonListener(e -> bet(nickname, avatar, 2));
        home.addThreePlayersButtonListener(e -> bet(nickname, avatar, 3));
    }

    /**
     * Aggiorna i contatori delle vittorie, sconfitte e pareggi per il giocatore e i bot.
     * @param result risultato della partita per il giocatore (1=vittoria, 2=sconfitta, 3=pareggio)
     * @param bot1result risultato della partita per il primo bot
     * @param bot2result risultato della partita per il secondo bot
     */
    private void updateCounts(int result, int bot1result, int bot2result) {
        // Aggiorna i contatori del giocatore in base al risultato
        if (result == 1) {
            winCount++;
            importo += betPanel.puntata;
        } else if (result == 2) {
            defeatCount++;
            importo -= betPanel.puntata;
        } else if (result == 3) {
            drawCount++;
        }
        // Aggiorna i contatori per i bot
        updateBotCounts(bot1result, 1);
        updateBotCounts(bot2result, 2);
    }

    /**
     * Aggiorna i contatori delle vittorie, sconfitte e pareggi per un bot specifico.
     * @param result risultato della partita per il bot (1=vittoria, 2=sconfitta, 3=pareggio)
     * @param botNumber numero del bot (1 o 2)
     */
    private void updateBotCounts(int result, int botNumber) {
        // Aggiorna i contatori del primo bot
        if (botNumber == 1) {
            if (result == 1) {
                winCountBot1++;
            } else if (result == 2) {
                defeatCountBot1++;
            } else if (result == 3) {
                drawCountBot1++;
            }
        // Aggiorna i contatori del secondo bot
        } else if (botNumber == 2) {
            if (result == 1) {
                winCountBot2++;
            } else if (result == 2) {
                defeatCountBot2++;
            } else if (result == 3) {
                drawCountBot2++;
            }
        }
    }

    /**
     * Cambia il pannello visualizzato nella finestra principale.
     * @param panel il nuovo pannello da visualizzare
     */
    private void switchToPanel(JPanel panel) {
        frame.getContentPane().removeAll(); // Rimuove il pannello corrente
        frame.add(panel); // Aggiunge il nuovo pannello
        frame.revalidate(); // Rende il layout del pannello visibile
        frame.repaint(); // Ridisegna la finestra
    }
}
