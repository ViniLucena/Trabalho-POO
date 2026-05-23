package model;

/**
 * Representa uma carta do jogo Clue. Uma carta tem um nome
 * (ex.: "Castiçal") e um tipo (PERSONAGEM, ARMA ou LOCAL).
 *
 * Visibilidade: package-private. As classes dos pacotes View e
 * Controller não devem manipular cartas diretamente; toda
 * interação passa pela classe pública Jogo.
 */
class Carta {

	enum Tipo {
		PERSONAGEM,
		ARMA,
		LOCAL
	}

	private final String nome;
	private final Tipo tipo;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Carta)) return false;
		Carta outra = (Carta) o;
		return nome.equals(outra.nome) && tipo == outra.tipo;
	}

	@Override
	public int hashCode() {
		return nome.hashCode() * 31 + tipo.hashCode();
	}
}
