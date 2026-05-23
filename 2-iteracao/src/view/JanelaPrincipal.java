package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import model.Jogo;

/**
 * Janela principal do jogo. Para a 2ª iteração, conforme o
 * enunciado, é a única janela exibida: as janelas iniciais,
 * de cartas e de bloco de notas não são obrigatórias nesta
 * etapa.
 *
 * Respeita o limite máximo de 1400x1050 pixels para a janela
 * (constante no enunciado).
 */
public class JanelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final PainelTabuleiro painelTabuleiro;
    private final PainelLateral painelLateral;

    public JanelaPrincipal(Jogo jogo) {
        super("Clue - Jogo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tamanho razoável de imagens internas usadas pelos placeholders.
        ResourceLoader recursos = new ResourceLoader(
            jogo,
            PainelTabuleiro.LARGURA, PainelTabuleiro.ALTURA,
            PainelTabuleiro.PINO_TAM,
            80,           // tamanho do dado
            180, 240      // dimensões das cartas
        );

        painelTabuleiro = new PainelTabuleiro(jogo, recursos);
        painelLateral   = new PainelLateral(jogo, recursos);

        add(painelTabuleiro, BorderLayout.CENTER);
        add(painelLateral,   BorderLayout.EAST);

        pack();

        // Garante que a janela jamais exceda 1400x1050.
        Dimension d = getSize();
        int largura = Math.min(d.width, 1400);
        int altura  = Math.min(d.height, 1050);
        setSize(largura, altura);

        setLocationRelativeTo(null);
        setResizable(false);
    }

    public PainelTabuleiro getPainelTabuleiro() {
        return painelTabuleiro;
    }

    public PainelLateral getPainelLateral() {
        return painelLateral;
    }
}
