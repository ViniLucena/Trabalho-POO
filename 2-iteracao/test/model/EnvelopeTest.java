package model;

import org.junit.*;
import static org.junit.Assert.*;

public class EnvelopeTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private Envelope envelope;

    @Before
    public void setUp() {
        Carta personagem = new Carta("Srta. Scarlet", Carta.Tipo.PERSONAGEM);
        Carta arma = new Carta("Revólver", Carta.Tipo.ARMA);
        Carta local = new Carta("Cozinha", Carta.Tipo.LOCAL);
        envelope = new Envelope(personagem, arma, local);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void acusacaoCorretaDeveRetornarVerdadeiro() {
        assertTrue("Acusação correta deve retornar true",
                envelope.verificarAcusacao("Srta. Scarlet", "Revólver", "Cozinha"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void acusacaoComPersonagemErradoDeveRetornarFalso() {
        assertFalse("Personagem errado deve retornar false",
                envelope.verificarAcusacao("Coronel Mustard", "Revólver", "Cozinha"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void acusacaoComArmaErradaDeveRetornarFalso() {
        assertFalse("Arma errada deve retornar false",
                envelope.verificarAcusacao("Srta. Scarlet", "Faca", "Cozinha"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void acusacaoComLocalErradoDeveRetornarFalso() {
        assertFalse("Local errado deve retornar false",
                envelope.verificarAcusacao("Srta. Scarlet", "Revólver", "Biblioteca"));
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
    public void construtorDeveRejeitarPersonagemComTipoErrado() {
        Carta errada = new Carta("Revólver", Carta.Tipo.ARMA);
        Carta arma = new Carta("Faca", Carta.Tipo.ARMA);
        Carta local = new Carta("Cozinha", Carta.Tipo.LOCAL);
        new Envelope(errada, arma, local);
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
    public void construtorDeveRejeitarArmaComTipoErrado() {
        Carta personagem = new Carta("Srta. Scarlet", Carta.Tipo.PERSONAGEM);
        Carta errada = new Carta("Cozinha", Carta.Tipo.LOCAL);
        Carta local = new Carta("Biblioteca", Carta.Tipo.LOCAL);
        new Envelope(personagem, errada, local);
    }

    @Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
    public void construtorDeveRejeitarLocalComTipoErrado() {
        Carta personagem = new Carta("Srta. Scarlet", Carta.Tipo.PERSONAGEM);
        Carta arma = new Carta("Revólver", Carta.Tipo.ARMA);
        Carta errada = new Carta("Coronel Mustard", Carta.Tipo.PERSONAGEM);
        new Envelope(personagem, arma, errada);
    }
}
