package model;

import java.util.*;

/**
 * Representa um jogador no jogo Clue.
 */
class Jogador {

    // variaveis
    private final String nome;
    private final String cor;
    private final List<Carta> mao;
    private final List<String> folhaDeNotas;
    private final Pino pino;

    // métodos
    Jogador(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
        this.mao = new ArrayList<Carta>();
        this.folhaDeNotas = new ArrayList<String>(Baralho.getTodosOsNomes());
        this.pino = new Pino(cor);
    }

    String getNome() {
        return nome;
    }

    String getCor() {
        return cor;
    }

    Pino getPino() {
        return pino;
    }

    void receberCarta(Carta c) {
        mao.add(c);
    }

    List<Carta> getMao() {
        return Collections.unmodifiableList(mao);
    }

    List<String> getFolhaDeNotas() {
        return Collections.unmodifiableList(folhaDeNotas);
    }

    void riscarDaFolha(String item) {
        folhaDeNotas.remove(item);
    }
}
