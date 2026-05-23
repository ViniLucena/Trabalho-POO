package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testes da classe Dados.
 *
 * Estratégia: como o lançamento é aleatório, repetimos o
 * sorteio muitas vezes e verificamos que TODOS os resultados
 * caem no domínio esperado [1..6]. Não testamos distribuição
 * (uniformidade) nesta iteração — basta o intervalo.
 */
public class DadosTest {

	private static final int REPETICOES = 1_000;

	@Test
	public void dadoDeveRetornarSempreDoisValores() {
		Dados d = new Dados();
		int[] r = d.lancar();
		assertEquals(2, r.length);
	}

	@Test
	public void cadaValorDoDadoDeveEstarEntre1e6() {
		Dados d = new Dados();
		for (int i = 0; i < REPETICOES; i++) {
			int[] r = d.lancar();
			assertTrue("Valor inválido: " + r[0], r[0] >= 1 && r[0] <= 6);
			assertTrue("Valor inválido: " + r[1], r[1] >= 1 && r[1] <= 6);
		}
	}

	@Test
	public void dadosComMesmoSeedDevemProduzirMesmaSequencia() {
		// Garante que o construtor com seed funciona — útil
		// para escrever testes determinísticos noutros pontos.
		Dados a = new Dados(42L);
		Dados b = new Dados(42L);
		for (int i = 0; i < 10; i++) {
			int[] ra = a.lancar();
			int[] rb = b.lancar();
			assertEquals(ra[0], rb[0]);
			assertEquals(ra[1], rb[1]);
		}
	}
}
