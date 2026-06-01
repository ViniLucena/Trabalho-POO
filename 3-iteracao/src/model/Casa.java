package model;

/**
 * Casa (célula) do tabuleiro. Cada casa tem id único, posição
 * (linha, coluna), um TipoCasa e — quando o tipo for COMODO ou
 * PORTA — uma referência ao Comodo correspondente.
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

    int getId()         { return id; }
    int getLinha()      { return linha; }
    int getColuna()     { return coluna; }
    TipoCasa getTipo()  { return tipo; }
    Comodo getComodo()  { return comodo; }

    void setComodo(Comodo c) {
        this.comodo = c;
    }

    boolean isComodo()    { return tipo == TipoCasa.COMODO; }
    boolean isPorta()     { return tipo == TipoCasa.PORTA; }
    boolean isCorredor()  { return tipo == TipoCasa.CORREDOR || tipo == TipoCasa.PORTA; }
}
