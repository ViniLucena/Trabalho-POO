package model;

/**
 * Envelope com a solução do crime: uma carta de personagem,
 * uma de arma e uma de local, sorteadas no início da partida.
 */
class Envelope {

    private final Carta personagem;
    private final Carta arma;
    private final Carta local;

    Envelope(Carta personagem, Carta arma, Carta local) {
        this.personagem = personagem;
        this.arma = arma;
        this.local = local;
    }

    Carta getPersonagem() { return personagem; }
    Carta getArma()       { return arma; }
    Carta getLocal()      { return local; }

    /** True se as três cartas batem exatamente com a solução. */
    boolean verificarSolucao(Carta p, Carta a, Carta l) {
        return personagem.equals(p) && arma.equals(a) && local.equals(l);
    }
}
