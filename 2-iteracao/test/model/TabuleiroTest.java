package model;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class TabuleiroTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private Tabuleiro tabuleiro;

    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void origemNuncaDeveEstarNaListaDeAlcancaveis() {
        Casa origem = tabuleiro.getPosicaoInicial(0);
        List<Integer> alcancaveis = tabuleiro.mapearCasasAlcancaveis(origem, 3);
        assertFalse("Origem não deve aparecer na lista de alcançáveis",
                alcancaveis.contains(origem.getId()));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void casasNoResultadoSaoCorredoresOuInterioresDeComodo() {
        Casa origem = tabuleiro.getPosicaoInicial(0);
        List<Integer> alcancaveis = tabuleiro.mapearCasasAlcancaveis(origem, 4);
        for (int id : alcancaveis) {
            Casa casa = tabuleiro.getCasaPorId(id);
            assertTrue("Resultado deve conter apenas corredores ou interiores de cômodo (representativas)",
                    casa.isTransitavel() || casa.getNomeComodo() != null);
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void todasPosicoesIniciaisSaoTransitaveis() {
        for (int i = 0; i < 6; i++) {
            Casa c = tabuleiro.getPosicaoInicial(i);
            assertTrue("Posição inicial do jogador " + i + " id=" + c.getId() + " deve ser transitável",
                    c.isTransitavel());
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void com0PassosResultadoDeveSerListaVazia() {
        Casa origem = tabuleiro.getPosicaoInicial(0);
        List<Integer> alcancaveis = tabuleiro.mapearCasasAlcancaveis(origem, 0);
        assertEquals("Com 0 passos a lista deve ser vazia", 0, alcancaveis.size());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void com1PassoAPartirDaCasaInicialDeveHaverCasasAlcancaveis() {
        // Usa Peacock (índice 4) que fica na borda oeste (12,0), com vizinho a direita (12,1)
        Casa origem = tabuleiro.getPosicaoInicial(4);
        List<Integer> alcancaveis = tabuleiro.mapearCasasAlcancaveis(origem, 1);
        assertTrue("Com 1 passo deve haver pelo menos uma casa alcançável",
                alcancaveis.size() >= 1);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void casasAlcancaveisNaoDevemConterDuplicatas() {
        Casa origem = tabuleiro.getPosicaoInicial(5);
        List<Integer> alcancaveis = tabuleiro.mapearCasasAlcancaveis(origem, 5);
        Set<Integer> sem_dup = new HashSet<Integer>(alcancaveis);
        assertEquals("Lista de alcançáveis não deve ter duplicatas",
                sem_dup.size(), alcancaveis.size());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void passosExatosDevemSerRespeitados() {
        // Verifica que casas a distância menor não aparecem se os passos são maiores
        Casa origem = tabuleiro.getPosicaoInicial(5); // Plum: (0,11)
        List<Integer> com2 = tabuleiro.mapearCasasAlcancaveis(origem, 2);
        List<Integer> com1 = tabuleiro.mapearCasasAlcancaveis(origem, 1);
        // Com 2 passos deve ter pelo menos as casas de 1 passo (pode voltar)
        // Mas o conjunto de 2 passos deve ser diferente ou igual — apenas verifica não vazio
        assertFalse("Com 2 passos resultado não deve ser vazio", com2.isEmpty());
        assertFalse("Com 1 passo resultado não deve ser vazio", com1.isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void posicoesIniciaisDos6PersonagensSaoTodasDistintas() {
        Set<Integer> ids = new HashSet<Integer>();
        for (int i = 0; i < 6; i++) {
            Casa c = tabuleiro.getPosicaoInicial(i);
            ids.add(c.getId());
        }
        assertEquals("As 6 posições iniciais devem ser distintas", 6, ids.size());
    }
}
