package br.com.dioSudoku.service;

import br.com.dioSudoku.model.Board;
import br.com.dioSudoku.model.GameStatusEnum;
import br.com.dioSudoku.model.space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BoardService {

    private final static int BOARD_LIMIT = 9;

    private final Board board;

    public BoardService(final Map<String, String> gameConfig) {
        this.board = new Board(initBoard(gameConfig));
    }

    public List<List<space>> getSpaces(){
        return this.board.getSpaces();
    }

    public void reset(){
        this.board.reset();
    }

    public boolean hasError(){
        return !getErrorSpaces().isEmpty();
    }

    public List<space> getErrorSpaces() {
        return board.getSpacesWithErrors();
    }

    public GameStatusEnum getStatus(){
        return board.getStatus();
    }

    public boolean gameIsFinished(){
        return board.isFinished();
    }

    private List<List<space>> initBoard(Map<String, String> gameConfig) {
        List<List<space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = gameConfig.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }
        return spaces;
    }
}