package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes de integração da classe Jogo (a API pública do
 * pacote Model). Cobrem o fluxo completo das três etapas de
 * uma jogada: lançar dados, mapear casas, deslocar pião.
 */
public class JogoTest {

	private Jogo jogo;

	@Before
	public void setUp() {
		jogo = new Jogo(Arrays.asList("Lucas", "Ana", "Pedro", "Marina"));
	}

	@Test
	public void jogoNovoTem4JogadoresECartasDistribuidas() {
		assertEquals(4, jogo.getNumeroJogadores());

		int totalDistribuido = 0;
		for (int i = 0; i < 4; i++) {
			totalDistribuido += jogo.getCartasJogadorAtual().size();
			jogo.proximoJogador();
		}
		assertEquals(18, totalDistribuido);
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDevePermitirMenosDe3Jogadores() {
		new Jogo(Arrays.asList("A", "B"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDevePermitirMaisDe6Jogadores() {
		new Jogo(Arrays.asList("A", "B", "C", "D", "E", "F", "G"));
	}

	@Test
	public void lancarDadosSempreRetornaDoisValoresValidos() {
		int[] r = jogo.lancarDados();
		assertEquals(2, r.length);
		assertTrue(r[0] >= 1 && r[0] <= 6);
		assertTrue(r[1] >= 1 && r[1] <= 6);
	}

	@Test
	public void mapearCasasSempreProduzListaNaoVazia() {
		// Mesmo no pior caso (1+1=2), a partir da posição
		// inicial (canto) sempre deve haver pelo menos 1 casa
		// alcançável.
		int[] dados = jogo.lancarDados();
		List<Integer> ids = jogo.mapearCasas(dados);
		assertFalse(ids.isEmpty());
	}

	@Test
	public void deslocarPiaoMoveOJogadorAtual() {
		int origem = jogo.getIdCasaJogadorAtual();
		int[] dados = jogo.lancarDados();
		List<Integer> destinos = jogo.mapearCasas(dados);
		int alvo = destinos.get(0);

		jogo.deslocarPiao(alvo);
		assertEquals(alvo, jogo.getIdCasaJogadorAtual());
		assertNotEquals(origem, jogo.getIdCasaJogadorAtual());
	}

	@Test(expected = IllegalStateException.class)
	public void naoPodeDeslocarSemLancarDadosAntes() {
		jogo.deslocarPiao(5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoPodeDeslocarParaCasaForaDoAlcance() {
		// Lançamos dados mínimos (vão dar entre 2 e 12) e
		// pedimos deslocamento para a casa diametralmente
		// oposta — impossível em 1 jogada.
		jogo.lancarDados();
		jogo.deslocarPiao(99); // canto oposto
	}

	@Test
	public void proximoJogadorAvancaCircular() {
		String j0 = jogo.getNomeJogadorAtual();
		jogo.proximoJogador();
		assertNotEquals(j0, jogo.getNomeJogadorAtual());
		// Volta ao início após 4 trocas (4 jogadores).
		jogo.proximoJogador();
		jogo.proximoJogador();
		jogo.proximoJogador();
		assertEquals(j0, jogo.getNomeJogadorAtual());
	}

	@Test
	public void envelopeMaisMaosSomam21Cartas() {
		// Visibilidade package-private permite verificar a
		// integridade do estado interno do jogo recém-criado.
		Envelope e = jogo.getEnvelopeInterno();
		java.util.Set<String> nomes = new java.util.HashSet<>();
		nomes.add(e.getPersonagem().getNome());
		nomes.add(e.getArma().getNome());
		nomes.add(e.getLocal().getNome());

		int contagemMaos = 0;
		for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
			contagemMaos += jogo.getCartasJogadorAtual().size();
			jogo.proximoJogador();
		}
		assertEquals(3, nomes.size());
		assertEquals(18, contagemMaos);
	}
	@Test
	public void testScarletSempreComeca() {
	    // A cor do primeiro jogador deve ser "Vermelho" (Srta. Scarlet)
	    assertEquals("Vermelho", jogo.getCorJogadorAtual());
	}
}
