package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Bloco de notas do jogador. Para cada carta do jogo, guarda se
 * ela já foi marcada (vista) por este jogador. As cartas da mão
 * do próprio jogador entram marcadas desde o início; as demais
 * são marcadas pelo jogador conforme adversários as mostrem em
 * resposta a palpites.
 */
class BlocoNotas {

    private final Map<Integer, Boolean> marcadas = new HashMap<Integer, Boolean>();

    void inicializarComCarta(int idCarta, boolean marcada) {
        marcadas.put(Integer.valueOf(idCarta), Boolean.valueOf(marcada));
    }

    void marcar(int idCarta) {
        marcadas.put(Integer.valueOf(idCarta), Boolean.TRUE);
    }

    void desmarcar(int idCarta) {
        marcadas.put(Integer.valueOf(idCarta), Boolean.FALSE);
    }

    boolean estaMarcada(int idCarta) {
        Boolean b = marcadas.get(Integer.valueOf(idCarta));
        return b != null && b.booleanValue();
    }
}
