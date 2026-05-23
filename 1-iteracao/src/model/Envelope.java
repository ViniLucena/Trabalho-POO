package model;

/**
 * Envelope contendo a "solução" do crime: uma carta de personagem,
 * uma de arma e uma de local sorteadas no início da partida.
 *
 * As três cartas ficam imutáveis até o fim do jogo.
 * O método verificarSolucao() será usado, em iterações futuras,
 * quando um jogador fizer uma acusação.
 */
class Envelope {

	private final Carta personagem;
	private final Carta arma;
	private final Carta local;

	Envelope(Carta personagem, Carta arma, Carta local) {
		if (personagem.getTipo() != Carta.Tipo.PERSONAGEM)
			throw new IllegalArgumentException("Carta de personagem inválida.");
		if (arma.getTipo() != Carta.Tipo.ARMA)
			throw new IllegalArgumentException("Carta de arma inválida.");
		if (local.getTipo() != Carta.Tipo.LOCAL)
			throw new IllegalArgumentException("Carta de local inválida.");

		this.personagem = personagem;
		this.arma = arma;
		this.local = local;
	}

	Carta getPersonagem() {
		return personagem;
	}

	Carta getArma() {
		return arma;
	}

	Carta getLocal() {
		return local;
	}

	/**
	 * Compara as três cartas de uma acusação com a solução real.
	 */
	boolean verificarSolucao(Carta personagem, Carta arma, Carta local) {
		return this.personagem.equals(personagem)
			&& this.arma.equals(arma)
			&& this.local.equals(local);
	}
}
