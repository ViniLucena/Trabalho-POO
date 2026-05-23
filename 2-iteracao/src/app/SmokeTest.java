package app;

import java.awt.GraphicsEnvironment;
import java.util.Set;

import model.Jogo;
import view.ResourceLoader;

/**
 * Pequeno teste de fumaça (sem GUI) que valida que o Model
 * inicializa, o ResourceLoader gera as imagens placeholder
 * e o cálculo de casas alcançáveis devolve resultados
 * coerentes. Útil em ambientes sem display gráfico.
 */
public class SmokeTest {
    public static void main(String[] args) {
        boolean headless = GraphicsEnvironment.isHeadless();
        System.out.println("Headless: " + headless);

        Jogo jogo = new Jogo(6);
        System.out.println("Jogadores : " + jogo.getNumeroJogadores());
        System.out.println("Tabuleiro : "
            + jogo.getLinhasTabuleiro() + "x" + jogo.getColunasTabuleiro());
        System.out.println("Cômodos   : " + jogo.getNomesComodos());

        jogo.setDadosManuais(3, 4);
        int[] d = jogo.lancarDados();
        System.out.println("Dados     : " + d[0] + " + " + d[1] + " = "
                           + (d[0] + d[1]));

        Set<Integer> alcanc = jogo.getCasasAlcancaveis();
        System.out.println("Alcancáveis: " + alcanc.size() + " casas");

        // Testa o ResourceLoader mesmo em headless (não precisa de display).
        System.setProperty("java.awt.headless", "true");
        ResourceLoader rl = new ResourceLoader(jogo, 920, 960, 30, 80, 180, 240);
        System.out.println("Tabuleiro: "
            + rl.getTabuleiro().getWidth() + "x"
            + rl.getTabuleiro().getHeight());
        for (int v = 1; v <= 6; v++) {
            System.out.println("  Dado " + v + ": "
                + rl.getDado(v).getWidth() + "x"
                + rl.getDado(v).getHeight());
        }
        for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
            System.out.println("  Pino " + jogo.getCorPino(i) + ": "
                + rl.getPino(i).getWidth() + "x" + rl.getPino(i).getHeight());
        }
        System.out.println("Cartas do jogador atual:");
        for (int[] info : jogo.getIdsCartasJogadorAtual()) {
            int id = info[0];
            java.awt.image.BufferedImage img = rl.getCarta(id);
            System.out.println("  id=" + id + " -> "
                + (img != null ? img.getWidth() + "x" + img.getHeight() : "NULL"));
        }

        System.out.println("OK");
    }
}
