package model;

import java.util.Random;

/**
 * Dois dados de seis faces. O testador pode definir manualmente
 * os valores do próximo lançamento (conforme sugestão dos
 * enunciados das iterações 2 e 3) — útil para inspecionar
 * situações específicas do jogo sem depender da aleatoriedade.
 */
class Dados {

    private final Random random = new Random();
    private int proximoD1 = 0; // 0 = aleatório
    private int proximoD2 = 0;

    /**
     * Define os valores que serão devolvidos no próximo
     * lançamento (1..6). Passar 0 retoma o comportamento
     * aleatório.
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
