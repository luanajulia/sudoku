package br.com.dioSudoku.ui.custom.screen;

import br.com.dioSudoku.model.space;
import br.com.dioSudoku.service.BoardService;
import br.com.dioSudoku.service.EventEnum;
import br.com.dioSudoku.service.NotifierService;
import br.com.dioSudoku.ui.custom.buttons.CheckGameStatusButton;
import br.com.dioSudoku.ui.custom.buttons.FinishGameButton;
import br.com.dioSudoku.ui.custom.buttons.ResetButton;
import br.com.dioSudoku.ui.custom.frame.MainFrame;
import br.com.dioSudoku.ui.custom.input.NumberText;
import br.com.dioSudoku.ui.custom.panel.MainPanel;
import br.com.dioSudoku.ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static br.com.dioSudoku.service.EventEnum.CLEAR_SPACE;
import static javax.swing.JOptionPane.*;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600,  600);

    private final BoardService boardService;
    private final NotifierService notifierService;
    private final List<NumberText> allNumberFields = new ArrayList<>(); // Armazenar todos os NumberText

    private JButton checkGameStatusButoon;
    private JButton finishGameButoon;
    private JButton resetButoon;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);

        for (int r = 0; r < 9; r+=3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c+=3) {
                var endCol = c + 2;
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, r, endRow);
                JPanel sector = generateSection(spaces);
                mainPanel.add(sector);
            }
        }

        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);

        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<space> getSpacesFromSector(final List<List<space>> spaces,
                                            final int initCol, final int endCol,
                                            final int initRow, final int endRow){
        List<space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r++) {
            for (int c = initCol; c <= endCol; c++) {
                spaceSector.add(spaces.get(r).get(c));
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        // Adiciona todos os NumberText criados à lista global
        this.allNumberFields.addAll(fields);
        // Assina os NumberText ao serviço de notificação para eventos como CLEAR_SPACE
        fields.forEach(t -> notifierService.subscriber(CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void addFinishGameButton(JPanel mainPanel) {
        finishGameButoon = new FinishGameButton(e -> {
            clearAllErrorHighlights();

            if (boardService.gameIsFinished()){
                showMessageDialog(null, "Parabéns você concluiu o jogo");
                resetButoon.setEnabled(false);
                checkGameStatusButoon.setEnabled(false);
                finishGameButoon.setEnabled(false);
            } else {
                List<space> errorSpaces = boardService.getErrorSpaces();
                if (!errorSpaces.isEmpty()) {
                    highlightErrors(errorSpaces);
                    showMessageDialog(null, "Seu Jogo contém erros nas posições destacadas. Ajuste novamente!");
                } else {
                    showMessageDialog(null, "O Jogo está incompleto. Continue preenchendo!");
                }
            }
        });
        mainPanel.add(finishGameButoon);
    }

    private void addCheckGameStatusButton(JPanel mainPanel) {
        checkGameStatusButoon = new CheckGameStatusButton(e -> {
            // Sempre limpa os destaques antigos antes de uma nova verificação
            clearAllErrorHighlights();

            List<space> errorSpaces = boardService.getErrorSpaces();

            if (!errorSpaces.isEmpty()) {
                highlightErrors(errorSpaces);
                var message = "Seu Jogo contém erros nas posições destacadas!";
                showMessageDialog(null, message);
            } else {
                var gameStatus = boardService.getStatus();
                var message = switch (gameStatus) {
                    case NON_STARTED -> "O jogo ainda não foi iniciado ";
                    case INCOMPLETE -> "O jogo está incompleto";
                    case COMPLETE -> "O Jogo está completo";
                };
                message += " e não contém erros.";
                showMessageDialog(null, message);
            }
        });
        mainPanel.add(checkGameStatusButoon);
    }

    private void addResetButton(JPanel mainPanel) {
        resetButoon = new ResetButton(e -> {
            var dialogResult = showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o Jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0){
                boardService.reset();
                notifierService.notify(CLEAR_SPACE);
                clearAllErrorHighlights();
                resetButoon.setEnabled(true);
                checkGameStatusButoon.setEnabled(true);
                finishGameButoon.setEnabled(true);
            }
        });
        mainPanel.add(resetButoon);
    }

    private void clearAllErrorHighlights() {
        for (NumberText field : allNumberFields) {
            field.clearHighlight();
        }
    }


    private void highlightErrors(List<space> errorSpaces) {
        for (NumberText field : allNumberFields) {
            if (errorSpaces.contains(field.getAssociatedSpace())) {
                field.highlightError();
            }
        }
    }
}