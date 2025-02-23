package BlackJack.views;

import javax.swing.*;
import BlackJack.Model;
import java.awt.event.ActionListener;

/**
 * Interfaccia che rappresenta una vista del gioco del BlackJack.
 * Fornisce metodi per ottenere i pulsanti "Hit" e "Stay", 
 * aggiornare la vista con il modello del gioco e gestire gli eventi associati ai pulsanti.
 */
public interface GameView {

    /**
     * Restituisce il pulsante "Hit" (Carta).
     * @return JButton associato all'azione "Hit".
     */
    JButton getHitButton();

    /**
     * Restituisce il pulsante "Stay" (Stare).
     * @return JButton associato all'azione "Stay".
     */
    JButton getStayButton();

    /**
     * Aggiorna la vista con i dati forniti dal modello del gioco.
     * @param model Il modello del gioco da visualizzare.
     */
    void updateView(Model model);

    /**
     * Aggiunge un listener al pulsante "Hit" per gestire l'evento di pressione del pulsante.
     * @param listener ActionListener da associare al pulsante "Hit".
     */
    default public void addHitButtonListener(ActionListener listener) {
        getHitButton().addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Stay" per gestire l'evento di pressione del pulsante.
     * @param listener ActionListener da associare al pulsante "Stay".
     */
    default public void addStayButtonListener(ActionListener listener) {
        getStayButton().addActionListener(listener);
    }

    /**
     * Abilita o disabilita il pulsante "Hit".
     * @param enabled true per abilitare il pulsante, false per disabilitarlo.
     */
    default public void setHitButtonEnabled(boolean enabled) {
        getHitButton().setEnabled(enabled);
    }

    /**
     * Abilita o disabilita il pulsante "Stay".
     * @param enabled true per abilitare il pulsante, false per disabilitarlo.
     */
    default public void setStayButtonEnabled(boolean enabled) {
        getStayButton().setEnabled(enabled);
    }

    /**
     * Crea un nuovo pulsante con il testo specificato.
     * @param text Il testo da visualizzare sul pulsante.
     * @return JButton creato con il testo fornito.
     */
    default public JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        return button;
    }
}
