package model;

/**
 * Representa uma casa (célula) do tabuleiro. Cada casa tem um
 * identificador único e uma posição (linha, coluna).
 *
 * O atributo "transitavel" permite marcar paredes/obstáculos.
 * Numa próxima iteração esse modelo pode ser estendido para
 * distinguir corredores, salas e portas (princípio aberto/fechado).
 */
class Casa {

	private final int id;
	private final int linha;
	private final int coluna;
	private final boolean transitavel;

	Casa(int id, int linha, int coluna, boolean transitavel) {
		this.id = id;
		this.linha = linha;
		this.coluna = coluna;
		this.transitavel = transitavel;
	}

	int getId() {
		return id;
	}

	int getLinha() {
		return linha;
	}

	int getColuna() {
		return coluna;
	}

	boolean isTransitavel() {
		return transitavel;
	}

	@Override
	public String toString() {
		return "Casa#" + id + "(" + linha + "," + coluna + ")";
	}
}
