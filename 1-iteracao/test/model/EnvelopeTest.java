package model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testes do Envelope: garantia de que apenas as três cartas
 * exatas resultam em verificação positiva, e que o construtor
 * recusa cartas de tipo errado.
 */
public class EnvelopeTest {

	private final Carta personagem = new Carta("Sra. Branca",  Carta.Tipo.PERSONAGEM);
	private final Carta arma       = new Carta("Castiçal",     Carta.Tipo.ARMA);
	private final Carta local      = new Carta("Biblioteca",   Carta.Tipo.LOCAL);

	@Test
	public void acusacaoCorretaDeveRetornarVerdadeiro() {
		Envelope e = new Envelope(personagem, arma, local);
		assertTrue(e.verificarSolucao(personagem, arma, local));
	}

	@Test
	public void acusacaoErradaDeveRetornarFalso() {
		Envelope e = new Envelope(personagem, arma, local);
		Carta outroPersonagem = new Carta("Coronel Mostarda", Carta.Tipo.PERSONAGEM);
		assertFalse(e.verificarSolucao(outroPersonagem, arma, local));
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDeveAceitarPersonagemComTipoErrado() {
		Carta falso = new Carta("X", Carta.Tipo.ARMA);
		new Envelope(falso, arma, local);
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDeveAceitarArmaComTipoErrado() {
		Carta falso = new Carta("X", Carta.Tipo.LOCAL);
		new Envelope(personagem, falso, local);
	}

	@Test(expected = IllegalArgumentException.class)
	public void naoDeveAceitarLocalComTipoErrado() {
		Carta falso = new Carta("X", Carta.Tipo.PERSONAGEM);
		new Envelope(personagem, arma, falso);
	}
}
