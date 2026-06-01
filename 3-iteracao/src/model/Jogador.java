package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Jogador do Clue. Tem nome, cor (identifica o pino), mão de
 * cartas, pino no tabuleiro, bloco de notas próprio e estado
 * de eliminação (após uma acusação errada).
 */
class Jogador {

    private final String nome;
    private final String cor;
    private final List<Carta> mao = new ArrayList<Carta>();
    private final BlocoNotas blocoNotas = new BlocoNotas();
    private Pino pino;
    private boolean eliminado = false;

    Jogador(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
    }

    String getNome() { return nome; }
    String getCor()  { return cor; }

    void receberCarta(Carta c) {
        mao.add(c);
        blocoNotas.inicializarComCarta(c.getId(), true);
    }

    List<Carta> getMao() {
        return Collections.unmodifiableList(mao);
    }

    BlocoNotas getBlocoNotas() {
        return blocoNotas;
    }

    Pino getPino() {
        return pino;
    }

    void setPino(Pino pino) {
        this.pino = pino;
    }

    boolean isEliminado()       { return eliminado; }
    void setEliminado(boolean v) { this.eliminado = v; }

    /** Devolve a primeira carta da mão que pertence ao conjunto
     *  {personagem, arma, local}, ou null se não tiver nenhuma.
     *  Usada para refutar palpites. */
    Carta cartaParaRefutar(Carta personagem, Carta arma, Carta local) {
        for (Carta c : mao) {
            if (c.equals(personagem) || c.equals(arma) || c.equals(local)) {
                return c;
            }
        }
        return null;
    }
}
