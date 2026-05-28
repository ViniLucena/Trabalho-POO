package model;

import org.junit.*;
import static org.junit.Assert.*;

public class DadosTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    @Test(timeout = DEFAULT_TIMEOUT)
    public void lancarDadosDeveRetornarArrayDeDoisElementos() {
        Dados dados = new Dados();
        int[] resultado = dados.lancar();
        assertEquals("Lançamento deve retornar array de 2 elementos", 2, resultado.length);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void cadaValorDeveEstarEntre1e6() {
        Dados dados = new Dados();
        for (int i = 0; i < 1000; i++) {
            int[] resultado = dados.lancar();
            assertTrue("d1 deve ser >= 1", resultado[0] >= 1);
            assertTrue("d1 deve ser <= 6", resultado[0] <= 6);
            assertTrue("d2 deve ser >= 1", resultado[1] >= 1);
            assertTrue("d2 deve ser <= 6", resultado[1] <= 6);
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void dadosComMesmoSeedDevemProduzirMesmaSequencia() {
        Dados d1 = new Dados(42L);
        Dados d2 = new Dados(42L);
        int[] r1 = d1.lancar();
        int[] r2 = d2.lancar();
        assertEquals("Valores com mesmo seed devem ser iguais (d1)", r1[0], r2[0]);
        assertEquals("Valores com mesmo seed devem ser iguais (d2)", r1[1], r2[1]);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void somaDosDadosDeveEstarEntre2e12() {
        Dados dados = new Dados();
        for (int i = 0; i < 1000; i++) {
            int[] resultado = dados.lancar();
            int soma = resultado[0] + resultado[1];
            assertTrue("Soma deve ser >= 2", soma >= 2);
            assertTrue("Soma deve ser <= 12", soma <= 12);
        }
    }
}
