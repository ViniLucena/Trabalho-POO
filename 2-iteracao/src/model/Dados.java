package model;

import java.util.Random;

/**
 * Dois dados de seis faces. Aceita seed para testes
 * determinísticos. Permite também que o testador defina,
 * manualmente, os valores dos próximos dados — útil para
 * inspecionar situações específicas do jogo, conforme
 * sugerido no enunciado da 2ª iteração.
 */
class Dados {

    private final Random random;
    private int proximoD1 = 0; // 0 = aleatório
    private int proximoD2 = 0;

    Dados() {
        this.random = new Random();
    }

    Dados(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Define os valores que serão devolvidos no próximo
     * lançamento (1..6). Passar 0 (ou valores fora do intervalo)
     * retoma o comportamento aleatório.
     */
    void definirProximoLancamento(int d1, int d2) {
        this.proximoD1 = (d1 >= 1 && d1 <= 6) ? d1 : 0;
        this.proximoD2 = (d2 >= 1 && d2 <= 6) ? d2 : 0;
    }

    int[] lancar() {
        int d1 = (proximoD1 != 0) ? proximoD1 : random.nextInt(6) + 1;
        int d2 = (proximoD2 != 0) ? proximoD2 : random.nextInt(6) + 1;
        proximoD1 = 0;
        proximoD2 = 0;
        return new int[] { d1, d2 };
    }
}
