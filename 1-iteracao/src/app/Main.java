package app;

import model.Jogo;
import java.util.*;

/**
 * Demonstração básica do jogo Clue — 1ª iteração.
 * Importa apenas model.Jogo (demais classes do pacote são package-private).
 */
public class Main {

    public static void main(String[] args) {
        List<String> nomes = Arrays.asList("Alice", "Bruno", "Carla", "Diego");
        Jogo jogo = new Jogo(nomes);

        for (int rodada = 0; rodada < 2; rodada++) {
            System.out.println("=== Vez de: " + jogo.getNomeJogadorAtual()
                    + " (cor " + jogo.getCorJogadorAtual() + ") ===");
            System.out.println("Cartas na mão: " + jogo.getCartasJogadorAtual());
            System.out.println("Casa atual id: " + jogo.getIdCasaJogadorAtual());

            int[] lancamento = jogo.lancarDados();
            System.out.println("Dados: " + lancamento[0] + " + " + lancamento[1]
                    + " = " + (lancamento[0] + lancamento[1]));

            List<Integer> alcancaveis = jogo.mapearCasas(lancamento);
            System.out.println("Casas alcançáveis (" + alcancaveis.size() + "): " + alcancaveis);

            if (!alcancaveis.isEmpty()) {
                int destino = alcancaveis.get(0);
                jogo.deslocarPiao(destino);
                System.out.println("Pião movido para casa id: " + destino);
            } else {
                System.out.println("Sem casas alcançáveis nesta jogada.");
                jogo.definirDados(0, 0);
            }

            System.out.println();
            jogo.proximoJogador();
        }
    }
}
