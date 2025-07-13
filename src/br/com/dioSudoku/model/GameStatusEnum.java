package br.com.dioSudoku.model;

public enum GameStatusEnum {

    NON_STARTED("Nao iniciado"),
    INCOMPLETE("Jogo Incompleto"),
    COMPLETE("Parabens! Voce finalizou o jogo :) ");

    private String label;

    GameStatusEnum(final String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
