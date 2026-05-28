package model;

import java.util.*;

/**
 * Representa os dois dados do jogo.
 */
class Dados {

    // variaveis
    private final Random random;

    // métodos
    Dados() {
        this.random = new Random();
    }

    Dados(long seed) {
        this.random = new Random(seed);
    }

    int[] lancar() {
        int d1 = random.nextInt(6) + 1;
        int d2 = random.nextInt(6) + 1;
        return new int[]{d1, d2};
    }
}
