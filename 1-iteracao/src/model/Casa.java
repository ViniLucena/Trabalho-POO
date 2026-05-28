package model;

/**
 * Representa uma célula do tabuleiro do jogo Clue.
 */
class Casa {

    // variaveis
    private final int id;
    private final int linha;
    private final int coluna;
    private final boolean transitavel;
    private final String nomeComodo;

    // métodos
    Casa(int id, int linha, int coluna, boolean transitavel, String nomeComodo) {
        this.id = id;
        this.linha = linha;
        this.coluna = coluna;
        this.transitavel = transitavel;
        this.nomeComodo = nomeComodo;
    }

    int getId() {
        return id;
    }

    int getLinha() {
        return linha;
    }

    int getColuna() {
        return coluna;
    }

    boolean isTransitavel() {
        return transitavel;
    }

    String getNomeComodo() {
        return nomeComodo;
    }

    @Override
    public String toString() {
        return "Casa#" + id + "(" + linha + "," + coluna + ")";
    }
}
