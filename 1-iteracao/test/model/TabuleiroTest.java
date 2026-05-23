package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Testes do Tabuleiro. Foco nas funcionalidades 3 e 4 da
 * especificação: mapear casas alcançáveis e movimentar o pino.
 */
public class TabuleiroTest {

	@Test
	public void com1PassoDeveDevolverNoMaximo4Vizinhas() {
		Tabuleiro t = new Tabuleiro();
		Casa origem = t.getCasaPorId(55); // (5,5), centro
		List<Integer> alcanca = t.mapearCasasAlcancaveis(origem, 1);
		// (5,4) está bloqueada por parede, então só 3 vizinhas.
		// (5,1) e (5,2) são paredes, (5,4) também deveria ser
		// — vou validar pelo número específico do meu grid.
		// Aqui apenas garantimos no máximo 4.
		assertTrue("Excedeu vizinhança 4-conectada: " + alcanca.size(),
			alcanca.size() <= 4);
	}

	@Test
	public void origemNuncaPodeFazerParteDoResultado() {
		// O jogador é obrigado a se mover; ficar parado não é
		// uma jogada válida.
		Tabuleiro t = new Tabuleiro();
		Casa origem = t.getCasaPorId(0);
		List<Integer> alcanca = t.mapearCasasAlcancaveis(origem, 5);
		assertFalse("Origem não pode estar em alcançáveis",
			alcanca.contains(origem.getId()));
	}

	@Test
	public void casasNaoTransitaveisNuncaSaoListadas() {
		Tabuleiro t = new Tabuleiro();
		Casa origem = t.getCasaPorId(0);
		List<Integer> alcanca = t.mapearCasasAlcancaveis(origem, 20);
		for (int id : alcanca) {
			assertTrue("Parede listada como alcançável: " + id,
				t.getCasaPorId(id).isTransitavel());
		}
	}

	@Test
	public void com0PassosNaoAlcancaNenhumaCasa() {
		// Caso degenerado: ainda assim a função deve responder
		// sem explodir.
		Tabuleiro t = new Tabuleiro();
		Casa origem = t.getCasaPorId(0);
		List<Integer> alcanca = t.mapearCasasAlcancaveis(origem, 0);
		assertTrue(alcanca.isEmpty());
	}

	@Test
	public void com2PassosACasaDeOrigemTemAteOitoVizinhasAlcancaveis() {
		// Num grid sem paredes a (1+2+1)x(1+2+1)/2... bom, só
		// garantimos um limite superior razoável e que o número
		// faz sentido. Para origem (0,0) num canto, com 2 passos:
		// alcançaveis: (0,2), (1,1), (2,0) = 3.
		Tabuleiro t = new Tabuleiro();
		Casa origem = t.getCasaPorId(0); // (0,0)
		List<Integer> alcanca = t.mapearCasasAlcancaveis(origem, 2);
		assertEquals(3, alcanca.size());
	}


	@Test
	public void posicoesIniciaisSaoTodasDistintas() {
		Tabuleiro t = new Tabuleiro();
		java.util.Set<Integer> ids = new java.util.HashSet<>();
		for (int i = 0; i < 6; i++) {
			ids.add(t.getPosicaoInicial(i).getId());
		}
		assertEquals(6, ids.size());
	}
}
