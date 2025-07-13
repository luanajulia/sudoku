package br.com.dioSudoku.ui.custom.input;

import br.com.dioSudoku.model.space;
import br.com.dioSudoku.service.EventEnum;
import br.com.dioSudoku.service.EventListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import static java.awt.Font.PLAIN;
import static br.com.dioSudoku.service.EventEnum.CLEAR_SPACE;

public class NumberText extends JTextField implements EventListener {

    private final space space;
    private final Color originalBackground;
    private final Color fixedValueBackground = new Color(240, 240, 240);
    private final Color errorBackground = new Color(255, 200, 200);

    public NumberText(final space space) {
        this.space = space;
        var dimension = new Dimension(50, 50);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setVisible(true);
        this.setFont(new Font("Arial", PLAIN, 20));
        this.setHorizontalAlignment(CENTER);
        this.setDocument(new NumberTextLimit());


        this.originalBackground = getBackground();

        this.setEnabled(!space.isFixed());
        if (space.isFixed()) {
            this.setText(String.valueOf(space.getActual()));
            this.setBackground(fixedValueBackground);
            this.setEditable(false);
        } else if (space.getActual() != null && space.getActual() != 0) {
            this.setText(String.valueOf(space.getActual()));
        } else {
            this.setText("");
        }

        this.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(final DocumentEvent e) {
                changeSpace();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                changeSpace();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                changeSpace();
            }

            private void changeSpace(){
                clearHighlight();

                String text = getText();
                if (text.isEmpty()){
                    space.clearSpace(); // Limpa o valor no objeto 'space'
                    return;
                }
                try {
                    int value = Integer.parseInt(text);
                    space.setActual(value);
                } catch (NumberFormatException ex) {
                    space.clearSpace();
                }
            }
        });
    }

    public space getAssociatedSpace() {
        return this.space;
    }


    public void highlightError() {

        if (!space.isFixed()) {
            setBackground(errorBackground);
            setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }
    }

    public void clearHighlight() {
        if (space.isFixed()) {
            setBackground(fixedValueBackground);
        } else {
            setBackground(originalBackground);
        }
        setBorder(UIManager.getBorder("TextField.border"));
    }

    @Override
    public void update(EventEnum eventType) {
        if (eventType.equals(CLEAR_SPACE)) {
            this.setText("");
            clearHighlight();
        }
    }
}