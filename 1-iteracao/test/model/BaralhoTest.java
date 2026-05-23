package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Testes do Baralho. Cobrem as regras 4–7 da Preparação:
 * sorteio do envelope-solução e distribuição entre jogadores.
 */
public class BaralhoTest {

	private List<Jogador> criarJogadores(int n) {
		List<Jogador> js = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			js.add(new Jogador("J" + i, "cor" + i));
		}
		return js;
	}

	@Test
	public void envelopeDeveTerUmaCartaDeCadaTipo() {
		Baralho b = new Baralho();
		Envelope e = b.sortearSolucao();

		assertNotNull(e);
		assertEquals(Carta.Tipo.PERSONAGEM, e.getPersonagem().getTipo());
		assertEquals(Carta.Tipo.ARMA,       e.getArma().getTipo());
		assertEquals(Carta.Tipo.LOCAL,      e.getLocal().getTipo());
	}

	@Test
	public void distribuirDeveEntregar18CartasParaJogadores() {
		// 21 cartas no total - 3 do envelope = 18 a distribuir.
		Baralho b = new Baralho();
		b.sortearSolucao();
		List<Jogador> js = criarJogadores(4);
		b.distribuir(js);

		int total = 0;
		for (Jogador j : js) total += j.getMao().size();
		assertEquals(18, total);
	}

	@Test
	public void distribuicaoDeveSerEquilibrada() {
		// Com 4 jogadores e 18 cartas, a diferença máxima
		// entre maior e menor mão deve ser 1.
		Baralho b = new Baralho();
		b.sortearSolucao();
		List<Jogador> js = criarJogadores(4);
		b.distribuir(js);

		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Jogador j : js) {
			min = Math.min(min, j.getMao().size());
			max = Math.max(max, j.getMao().size());
		}
		assertTrue("Mãos desbalanceadas: " + min + " vs " + max, max - min <= 1);
	}

	@Test
	public void naoDevemHaverCartasDuplicadasEntreEnvelopeEMaos() {
		// Conferir que uma mesma carta não aparece em dois
		// lugares — bug clássico de embaralhamento por engano.
		Baralho b = new Baralho();
		Envelope e = b.sortearSolucao();
		List<Jogador> js = criarJogadores(3);
		b.distribuir(js);

		Set<Carta> todas = new HashSet<>();
		todas.add(e.getPersonagem());
		todas.add(e.getArma());
		todas.add(e.getLocal());

		int contagemMaos = 0;
		for (Jogador j : js) {
			for (Carta c : j.getMao()) {
				assertTrue("Carta no envelope também na mão: " + c,
					todas.add(c));
				contagemMaos++;
			}
		}
		// 3 do envelope + 18 das mãos = 21 cartas distintas.
		assertEquals(21, todas.size());
		assertEquals(18, contagemMaos);
	}

	@Test
	public void distribuicaoComTresJogadoresDistribuiTodas18Cartas() {
		// Caso de carga: 18 / 3 = 6 cartas por jogador, exato.
		Baralho b = new Baralho();
		b.sortearSolucao();
		List<Jogador> js = criarJogadores(3);
		b.distribuir(js);
		for (Jogador j : js) {
			assertEquals(6, j.getMao().size());
		}
	}
}
