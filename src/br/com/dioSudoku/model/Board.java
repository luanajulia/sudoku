package br.com.dioSudoku.model;

import java.util.*;

import static br.com.dioSudoku.model.GameStatusEnum.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<space>> spaces;

    public Board(List<List<space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }
        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasError(){
        if (getStatus() == NON_STARTED) {
            return false;
        }

        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> nonNull(s.getActual()) &&  !s.getActual().equals(s.getExpected()));

    }

    public boolean changeValue(final int col, final int row, final int value){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }
        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }
        space.clearSpace();
        return true;
    }

    public void reset(){
        spaces.forEach(c -> c.forEach(space::clearSpace));
    }

    public boolean isFinished(){
        return !hasError() && getStatus() == COMPLETE;
    }

    public List<space> getSpacesWithErrors() {
        Set<space> uniqueErrorSpaces = new HashSet<>();
        for (int r = 0; r < 9; r++) {
            Set<Integer> seenValuesInRow = new HashSet<>();
            for (int c = 0; c < 9; c++) {
                space currentSpace = spaces.get(r).get(c);
                Integer value = currentSpace.getActual();
                if (value != null && value != 0) {
                    if (seenValuesInRow.contains(value)) {
                        uniqueErrorSpaces.add(currentSpace);
                        for (int prevC = 0; prevC < c; prevC++) {
                            if (spaces.get(r).get(prevC).getActual() != null && spaces.get(r).get(prevC).getActual().equals(value)) {
                                uniqueErrorSpaces.add(spaces.get(r).get(prevC));
                            }
                        }
                    }
                    seenValuesInRow.add(value);
                }
            }
        }

        for (int c = 0; c < 9; c++) {
            Set<Integer> seenValuesInCol = new HashSet<>();
            for (int r = 0; r < 9; r++) {
                space currentSpace = spaces.get(r).get(c);
                Integer value = currentSpace.getActual();

                if (value != null && value != 0) {
                    if (seenValuesInCol.contains(value)) {
                        uniqueErrorSpaces.add(currentSpace);
                        for (int prevR = 0; prevR < r; prevR++) {
                            if (spaces.get(prevR).get(c).getActual() != null && spaces.get(prevR).get(c).getActual().equals(value)) {
                                uniqueErrorSpaces.add(spaces.get(prevR).get(c));
                            }
                        }
                    }
                    seenValuesInCol.add(value);
                }
            }
        }

        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                Set<Integer> seenValuesInBlock = new HashSet<>();
                for (int r = blockRow * 3; r < (blockRow * 3) + 3; r++) {
                    for (int c = blockCol * 3; c < (blockCol * 3) + 3; c++) {
                        space currentSpace = spaces.get(r).get(c);
                        Integer value = currentSpace.getActual();

                        if (value != null && value != 0) {
                            if (seenValuesInBlock.contains(value)) {
                                uniqueErrorSpaces.add(currentSpace);
                                for (int prevR = blockRow * 3; prevR <= r; prevR++) {
                                    for (int prevC = blockCol * 3; prevC < ((prevR == r) ? c : (blockCol * 3) + 3); prevC++) { 
                                        if (spaces.get(prevR).get(prevC).getActual() != null && spaces.get(prevR).get(prevC).getActual().equals(value)) {
                                            uniqueErrorSpaces.add(spaces.get(prevR).get(prevC));
                                        }
                                    }
                                }
                            }
                            seenValuesInBlock.add(value);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(uniqueErrorSpaces);
    }
}
