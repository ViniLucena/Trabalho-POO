package model;

/**
 * Pião (peão) de um jogador no tabuleiro. Sabe em qual casa
 * está atualmente e qual é a sua cor.
 *
 * Separar Pino de Jogador deixa o modelo mais limpo: o jogador
 * cuida da mão de cartas e identidade; o pino cuida da posição
 * física no tabuleiro. Isso facilita evoluções futuras (ex.:
 * mover cartas/pinos sem afetar a outra responsabilidade).
 */
class Pino {

	private final String cor;
	private Casa casa;

	Pino(String cor, Casa casaInicial) {
		this.cor = cor;
		this.casa = casaInicial;
	}

	String getCor() {
		return cor;
	}

	Casa getCasa() {
		return casa;
	}

	void setCasa(Casa novaCasa) {
		this.casa = novaCasa;
	}
}
