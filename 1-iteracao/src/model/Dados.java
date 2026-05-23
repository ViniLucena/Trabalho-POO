package model;

import java.util.Random;

/**
 * Abstrai o lançamento de dois dados de seis faces.
 * O método lancar() devolve um array de dois inteiros, cada
 * um entre 1 e 6.
 *
 * Isolar essa classe facilita os testes: em iterações futuras
 * podemos injetar um Random com seed fixa, ou substituir por
 * um mock, sem mudar o restante do Model.
 */
class Dados {

	private final Random random;

	Dados() {
		this.random = new Random();
	}

	/** Construtor para testes — permite seed determinístico. */
	Dados(long seed) {
		this.random = new Random(seed);
	}

	int[] lancar() {
		int d1 = random.nextInt(6) + 1;
		int d2 = random.nextInt(6) + 1;
		return new int[] { d1, d2 };
	}
}
