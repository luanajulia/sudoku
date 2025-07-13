package br.com.dioSudoku.ui.custom.buttons;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ResetButton extends JButton {

    public ResetButton(final ActionListener actionListener){
        this.setText("Reiniciar o Jogo");
        this.addActionListener(actionListener);
    }
}
