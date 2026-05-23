package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Jogador do Clue. Tem nome, cor (identifica o pino), mão de
 * cartas e uma referência ao seu pino no tabuleiro.
 */
class Jogador {

    private final String nome;
    private final String cor;
    private final List<Carta> mao = new ArrayList<Carta>();
    private Pino pino;

    Jogador(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
    }

    String getNome() { return nome; }
    String getCor()  { return cor; }

    void receberCarta(Carta c) {
        mao.add(c);
    }

    List<Carta> getMao() {
        return Collections.unmodifiableList(mao);
    }

    Pino getPino() {
        return pino;
    }

    void setPino(Pino pino) {
        this.pino = pino;
    }
}
