package model;

/**
 * Representa o envelope lacrado com a solução do crime.
 */
class Envelope {

    // variaveis
    private final Carta personagem;
    private final Carta arma;
    private final Carta local;

    // métodos
    Envelope(Carta personagem, Carta arma, Carta local) {
        if (personagem.getTipo() != Carta.Tipo.PERSONAGEM) {
            throw new IllegalArgumentException("Carta de personagem esperada, recebeu: " + personagem.getTipo());
        }
        if (arma.getTipo() != Carta.Tipo.ARMA) {
            throw new IllegalArgumentException("Carta de arma esperada, recebeu: " + arma.getTipo());
        }
        if (local.getTipo() != Carta.Tipo.LOCAL) {
            throw new IllegalArgumentException("Carta de local esperada, recebeu: " + local.getTipo());
        }
        this.personagem = personagem;
        this.arma = arma;
        this.local = local;
    }

    boolean verificarAcusacao(String nomePersonagem, String nomeArma, String nomeLocal) {
        return personagem.getNome().equals(nomePersonagem)
                && arma.getNome().equals(nomeArma)
                && local.getNome().equals(nomeLocal);
    }

    String getNomePersonagem() {
        return personagem.getNome();
    }

    String getNomeArma() {
        return arma.getNome();
    }

    String getNomeLocal() {
        return local.getNome();
    }
}
