package model;

/**
 * Carta do Clue. Tem nome, tipo e um id único atribuído na
 * criação. O id permite que a View recupere a imagem
 * correspondente em tempo constante (chave de HashMap ou
 * índice de array — requisito do enunciado da 2ª iteração).
 *
 * Conforme o enunciado, o Model representa apenas o significado
 * da carta; a imagem em si fica na View.
 */
class Carta {

    enum Tipo {
        PERSONAGEM,
        ARMA,
        LOCAL
    }

    private final int id;
    private final String nome;
    private final Tipo tipo;

    Carta(int id, String nome, Tipo tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    int getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    Tipo getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carta)) return false;
        Carta outra = (Carta) o;
        return id == outra.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
