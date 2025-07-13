package br.com.dioSudoku.ui.custom.buttons;

import javax.swing.*;
import java.awt.event.ActionListener;

public class CheckGameStatusButton extends JButton {

    public CheckGameStatusButton(final ActionListener actionListener){
        this.setText("Verificar Status do Jogo");
        this.addActionListener(actionListener);
    }
}
