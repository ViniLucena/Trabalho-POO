package model;

/**
 * Carta do Clue. Tem nome, tipo (PERSONAGEM, ARMA, LOCAL) e um
 * id único atribuído na criação. O id permite que a View
 * recupere a imagem correspondente em tempo constante (chave de
 * HashMap), conforme o requisito da 2ª iteração.
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

    int getId()    { return id; }
    String getNome() { return nome; }
    Tipo getTipo() { return tipo; }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carta)) return false;
        return id == ((Carta) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
