package model;

import java.util.*;

/**
 * Representa uma carta do jogo Clue.
 */
class Carta {

    // variaveis
    enum Tipo {
        PERSONAGEM, ARMA, LOCAL
    }

    private final String nome;
    private final Tipo tipo;

    // métodos
    Carta(String nome, Tipo tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    String getNome() {
        return nome;
    }

    Tipo getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nome + " (" + tipo + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Carta)) return false;
        Carta outra = (Carta) obj;
        return nome.equals(outra.nome) && tipo == outra.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, tipo);
    }
}
