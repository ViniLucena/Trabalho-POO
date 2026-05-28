package model;

/**
 * Representa o pino (peão) de um jogador no tabuleiro.
 */
class Pino {

    // variaveis
    private final String cor;
    private Casa casaAtual;

    // métodos
    Pino(String cor) {
        this.cor = cor;
    }

    String getCor() {
        return cor;
    }

    Casa getCasaAtual() {
        return casaAtual;
    }

    void setCasaAtual(Casa c) {
        this.casaAtual = c;
    }
}
