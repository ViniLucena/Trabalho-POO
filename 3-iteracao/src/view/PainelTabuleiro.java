package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;

import model.Jogo;
import model.Observador;

/**
 * Painel que exibe o tabuleiro do Clue. Todo o desenho é feito
 * EXCLUSIVAMENTE pelo método drawImage() do Graphics2D, conforme
 * exigência do enunciado.
 *
 * Implementa Observador: ao ser notificado pelo Jogo (Observado),
 * apenas dispara repaint() — o estado é lido na próxima pintura.
 */
public class PainelTabuleiro extends JPanel implements Observador {

    private static final long serialVersionUID = 1L;

    public static final int LARGURA  = 720;
    public static final int ALTURA   = 750;
    public static final int PINO_TAM = 26;

    private final Jogo jogo;
    private final ResourceLoader recursos;

    public PainelTabuleiro(Jogo jogo, ResourceLoader recursos) {
        this.jogo = jogo;
        this.recursos = recursos;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(60, 60, 60));
        jogo.registrarObservador(this);
    }

    public double getCellW() {
        return LARGURA / (double) jogo.getColunasTabuleiro();
    }
    public double getCellH() {
        return ALTURA / (double) jogo.getLinhasTabuleiro();
    }

    /** Converte coordenadas de pixel para (linha, coluna). */
    public int[] pixelParaCelula(int px, int py) {
        int c = (int) (px / getCellW());
        int l = (int) (py / getCellH());
        if (l < 0) l = 0;
        if (l >= jogo.getLinhasTabuleiro()) l = jogo.getLinhasTabuleiro() - 1;
        if (c < 0) c = 0;
        if (c >= jogo.getColunasTabuleiro()) c = jogo.getColunasTabuleiro() - 1;
        return new int[] { l, c };
    }

    @Override
    public void notificar() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics gOriginal) {
        super.paintComponent(gOriginal);
        Graphics2D g = (Graphics2D) gOriginal;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // 1) Tabuleiro inteiro (UM drawImage).
        BufferedImage img = recursos.getTabuleiro();
        g.drawImage(img, 0, 0, LARGURA, ALTURA, null);

        // 2) Destaque das casas alcançáveis (se houver lançamento).
        desenharDestaque(g);

        // 3) Pinos — todos os 6, mesmo os sem jogador.
        desenharPinos(g);
    }

    private void desenharDestaque(Graphics2D g) {
        if (!jogo.jaLancouNestaVez()) return;
        Set<Integer> alc = jogo.getCasasAlcancaveis();
        double cw = getCellW();
        double ch = getCellH();
        g.setColor(new Color(255, 255, 0, 80)); // amarelo translúcido
        for (Integer id : alc) {
            int[] lc = jogo.getCoordenadasPorId(id.intValue());
            if (lc == null) continue;
            int x = (int) Math.round(lc[1] * cw);
            int y = (int) Math.round(lc[0] * ch);
            int w = (int) Math.round((lc[1] + 1) * cw) - x;
            int h = (int) Math.round((lc[0] + 1) * ch) - y;
            g.fillRect(x, y, w, h);
        }
    }

    private void desenharPinos(Graphics2D g) {
        double cw = getCellW();
        double ch = getCellH();
        for (int i = 0; i < jogo.getNumeroPinos(); i++) {
            int linha  = jogo.getLinhaPinoPersonagem(i);
            int coluna = jogo.getColunaPinoPersonagem(i);
            int cx = (int) ((coluna + 0.5) * cw);
            int cy = (int) ((linha  + 0.5) * ch);
            // pequeno offset para evitar sobreposição total no mesmo cômodo
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
