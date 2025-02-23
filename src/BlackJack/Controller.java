package BlackJack;

import BlackJack.views.GameView;

public class Controller {
    // Riferimento al modello che contiene la logica di gioco
    private Model model;
    
    // Riferimento alla vista che gestisce l'interfaccia utente
    private GameView view;

    /**
     * Costruttore della classe Controller.
     * Inizializza il modello e la vista, aggiunge i listener per i pulsanti Hit e Stay,
     * e notifica gli osservatori del modello.
     *
     * @param model Il modello che gestisce lo stato del gioco.
     * @param view  La vista che gestisce l'interfaccia utente del gioco.
     */
    public Controller(Model model, GameView view) {
        this.model = model;
        this.view = view;
        
        // Aggiunge un listener per il pulsante Hit che esegue l'azione hitAction()
        view.addHitButtonListener(e -> hitAction());
        
        // Aggiunge un listener per il pulsante Stay che esegue l'azione stayAction()
        view.addStayButtonListener(e -> stayAction());
        
        // Notifica gli osservatori del modello per aggiornare la vista iniziale
        model.notifyObservers();
    }

    /**
     * Metodo privato che gestisce l'azione del pulsante Hit.
     * Viene giocata una carta, si aggiornano i valori del giocatore,
     * e si verifica se il giocatore ha superato 21 punti.
     * In tal caso, il turno passa al dealer e si disabilita il pulsante Hit.
     */
    private void hitAction() {
        // Riproduce il suono del click per il pulsante Hit
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        
        // Pesca una carta dal mazzo e aggiorna la somma dei punti del giocatore
        Model.Card card = model.drawCard();
        model.setPlayerSum(model.getPlayerSum() + card.getValue());
        
        // Aggiorna il conteggio degli assi del giocatore
        model.setPlayerAceCount(model.getPlayerAceCount() + (card.isAce() ? 1 : 0));
        
        // Aggiunge la carta pescata alla mano del giocatore
        model.getPlayerHand().add(card);

        // Se la somma dei punti del giocatore riducendo gli assi supera 21, disabilita il pulsante Hit e passa al turno del dealer
        if (model.reduceAce(model.getPlayerSum(), model.getPlayerAceCount()) > 21) {
            view.setHitButtonEnabled(false);
            stayAction();
        }
        
        // Notifica gli osservatori del modello per aggiornare la vista
        model.notifyObservers();
    }

    /**
     * Metodo privato che gestisce l'azione del pulsante Stay.
     * Disabilita i pulsanti Hit e Stay, e gestisce il turno del dealer e dei bot,
     * continuando a pescare carte finché la somma dei loro punti è inferiore a 17.
     */
    private void stayAction() {
        // Riproduce il suono del click per il pulsante Stay
        AudioManager.getInstance().play("src/BlackJack/resources/audio/click2.wav");
        
        // Disabilita i pulsanti Hit e Stay
        view.setHitButtonEnabled(false);
        view.setStayButtonEnabled(false);

        // Il dealer continua a pescare carte finché la sua somma è inferiore a 17
        while (model.getDealerSum() < 17) {
            Model.Card card = model.drawCard();
            model.setDealerSum(model.getDealerSum() + card.getValue());
            model.setDealerAceCount(model.getDealerAceCount() + (card.isAce() ? 1 : 0));
            model.getDealerHand().add(card);
        }

        // Se ci sono più di un giocatore (incluso il bot1), anche il bot1 pesca carte finché la sua somma è inferiore a 17
        if (model.getPlayers() > 1) {
            while (model.getBot1Sum() < 17) {
                Model.Card card = model.drawCard();
                model.setBot1Sum(model.getBot1Sum() + card.getValue());
                model.setBot1AceCount(model.getBot1AceCount() + (card.isAce() ? 1 : 0));
                model.getBot1Hand().add(card);
            }
        }
        
        // Se ci sono più di due giocatori (incluso il bot2), anche il bot2 pesca carte finché la sua somma è inferiore a 17
        if (model.getPlayers() > 2) {
            while (model.getBot2Sum() < 17) {
                Model.Card card = model.drawCard();
                model.setBot2Sum(model.getBot2Sum() + card.getValue());
                model.setBot2AceCount(model.getBot2AceCount() + (card.isAce() ? 1 : 0));
                model.getBot2Hand().add(card);
            }
        }
        
        // Notifica gli osservatori del modello per aggiornare la vista
        model.notifyObservers();
    }
}
