package model;

/**
 * Pino (peão) de um personagem. Sabe sua cor e a casa em que
 * está no momento.
 */
class Pino {

    private final String cor;
    private Casa casa;

    Pino(String cor, Casa casaInicial) {
        this.cor = cor;
        this.casa = casaInicial;
    }

    String getCor()  { return cor; }
    Casa getCasa()   { return casa; }

    void setCasa(Casa novaCasa) {
        this.casa = novaCasa;
    }
}
