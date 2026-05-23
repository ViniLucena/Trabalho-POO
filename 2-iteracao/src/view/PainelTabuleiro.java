package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import model.Jogo;

/**
 * Painel customizado que exibe o tabuleiro do Clue. Todo o
 * desenho é feito EXCLUSIVAMENTE pelo método drawImage() do
 * Graphics2D, conforme exigência do enunciado. As imagens são
 * fornecidas pelo ResourceLoader; quando elas não estão no
 * disco, o próprio ResourceLoader gera BufferedImages
 * placeholder em runtime.
 *
 * O painel não usa componentes Swing internos (JLabel, JButton,
 * etc.) para representar elementos de jogo.
 */
public class PainelTabuleiro extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int LARGURA  = 920;
    public static final int ALTURA   = 960;
    public static final int PINO_TAM = 30;

    private final Jogo jogo;
    private final ResourceLoader recursos;

    public PainelTabuleiro(Jogo jogo, ResourceLoader recursos) {
        this.jogo = jogo;
        this.recursos = recursos;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(60, 60, 60));
    }

    /** Largura em pixels de uma casa do grid. */
    public double getCellW() {
        return LARGURA / (double) jogo.getColunasTabuleiro();
    }

    /** Altura em pixels de uma casa do grid. */
    public double getCellH() {
        return ALTURA / (double) jogo.getLinhasTabuleiro();
    }

    /** Converte coordenadas de pixel para (linha, coluna). */
    public int[] pixelParaCelula(int px, int py) {
        int c = (int) (px / getCellW());
        int l = (int) (py / getCellH());
        if (l < 0) l = 0; else if (l >= jogo.getLinhasTabuleiro()) l = jogo.getLinhasTabuleiro() - 1;
        if (c < 0) c = 0; else if (c >= jogo.getColunasTabuleiro()) c = jogo.getColunasTabuleiro() - 1;
        return new int[] { l, c };
    }

    @Override
    protected void paintComponent(Graphics gOriginal) {
        super.paintComponent(gOriginal);
        Graphics2D g = (Graphics2D) gOriginal;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // 1) Desenhar o tabuleiro inteiro (UM drawImage).
        BufferedImage img = recursos.getTabuleiro();
        g.drawImage(img, 0, 0, LARGURA, ALTURA, null);

        // 2) Desenhar cada pino com drawImage().
        double cw = getCellW();
        double ch = getCellH();
        for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
            int linha  = jogo.getLinhaPino(i);
            int coluna = jogo.getColunaPino(i);

            int cx = (int) ((coluna + 0.5) * cw);
            int cy = (int) ((linha  + 0.5) * ch);

            // Pequeno deslocamento para que pinos no mesmo cômodo
            // não se sobreponham completamente.
            int dx = ((i % 3) - 1) * (PINO_TAM / 3);
            int dy = ((i / 3) - 0) * (PINO_TAM / 3);

            BufferedImage pino = recursos.getPino(i);
            g.drawImage(pino,
                cx - PINO_TAM / 2 + dx,
                cy - PINO_TAM / 2 + dy,
                PINO_TAM, PINO_TAM, null);
        }
    }
}
