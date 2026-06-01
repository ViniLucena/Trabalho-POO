package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import model.Jogo;

/**
 * Janela principal do jogo, com o tabuleiro (centro) e o painel
 * lateral de controles (leste). Mantém também referências às
 * janelas auxiliares (Cartas, Bloco de Notas) para que o
 * Controller possa alterná-las.
 *
 * Respeita o limite máximo de 1400x1050 pixels (enunciado).
 */
public class JanelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final PainelTabuleiro painelTabuleiro;
    private final PainelLateral painelLateral;
    private final JanelaCartas janelaCartas;
    private final JanelaBlocoNotas janelaNotas;

    public JanelaPrincipal(Jogo jogo, ResourceLoader recursos) {
        super("Clue - Jogo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        painelTabuleiro = new PainelTabuleiro(jogo, recursos);
        painelLateral   = new PainelLateral(jogo, recursos);
        janelaCartas    = new JanelaCartas(jogo, recursos);
        janelaNotas     = new JanelaBlocoNotas(jogo);

        add(painelTabuleiro, BorderLayout.CENTER);
        add(painelLateral,   BorderLayout.EAST);

        pack();

        Dimension d = getSize();
        int largura = Math.min(d.width,  1400);
        int altura  = Math.min(d.height, 1050);
        setSize(largura, altura);

        setLocationRelativeTo(null);
        setResizable(false);
    }

    public PainelTabuleiro getPainelTabuleiro() { return painelTabuleiro; }
    public PainelLateral   getPainelLateral()   { return painelLateral; }
    public JanelaCartas    getJanelaCartas()    { return janelaCartas; }
    public JanelaBlocoNotas getJanelaNotas()    { return janelaNotas; }
}
