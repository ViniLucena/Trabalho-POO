package model;

/**
 * Casa (célula) do tabuleiro. Cada casa tem id único, posição
 * (linha, coluna), um TipoCasa e — quando o tipo for COMODO ou
 * PORTA — uma referência ao Comodo correspondente.
 *
 * A separação entre TipoCasa e referência a Comodo permite que
 * o tabuleiro represente, sem ambiguidade, tanto as células
 * internas de uma sala (COMODO) quanto as portas no corredor
 * (PORTA) que dão acesso a ela.
 */
class Casa {

    private final int id;
    private final int linha;
    private final int coluna;
    private final TipoCasa tipo;
    private Comodo comodo; // só preenchido se tipo == COMODO ou PORTA

    Casa(int id, int linha, int coluna, TipoCasa tipo) {
        this.id = id;
        this.linha = linha;
        this.coluna = coluna;
        this.tipo = tipo;
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

    TipoCasa getTipo() {
        return tipo;
    }

    Comodo getComodo() {
        return comodo;
    }

    void setComodo(Comodo c) {
        this.comodo = c;
    }

    boolean isTransitavel() {
        return tipo != TipoCasa.BLOQUEADA;
    }

    boolean isComodo() {
        return tipo == TipoCasa.COMODO;
    }

    boolean isPorta() {
        return tipo == TipoCasa.PORTA;
    }

    boolean isCorredor() {
        return tipo == TipoCasa.CORREDOR || tipo == TipoCasa.PORTA;
    }

    @Override
    public String toString() {
        return "Casa#" + id + "(" + linha + "," + coluna + "," + tipo + ")";
    }
}
