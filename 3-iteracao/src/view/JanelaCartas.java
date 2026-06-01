package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Jogo;
import model.Observador;

/**
 * Janela "Mostrar Cartas" — exibe as cartas que estão com o
 * jogador da vez, agrupadas por Suspeitos / Cômodos / Armas.
 *
 * Toda renderização é feita por drawImage() do Graphics2D.
 * O componente Swing (JFrame, JScrollPane) só é usado para
 * abertura/fechamento, conforme permitido pelo enunciado.
 */
public class JanelaCartas extends JFrame implements Observador {

    private static final long serialVersionUID = 1L;
    private static final int CARTA_LARG = 150;
    private static final int CARTA_ALT  = 210;
    private static final int MARGEM     = 16;

    private final Jogo jogo;
    private final ResourceLoader recursos;
    private final PainelCartas painel;

    public JanelaCartas(Jogo jogo, ResourceLoader recursos) {
        super("Clue - Cartas do Jogador");
        this.jogo = jogo;
        this.recursos = recursos;
        this.painel = new PainelCartas();

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(new JScrollPane(painel));
        setSize(580, 540);
        setLocationRelativeTo(null);

        jogo.registrarObservador(this);
    }

    /** Atualiza o título com o nome do jogador atual e refaz o painel. */
    public void atualizar() {
        setTitle("Clue - Cartas de " + jogo.getNomeJogadorAtual());
        painel.recalcular();
        painel.repaint();
    }

    @Override
    public void setVisible(boolean v) {
        if (v) atualizar();
        super.setVisible(v);
    }

    @Override
    public void notificar() {
        if (isVisible()) atualizar();
    }

    // ============================================================
    // Painel customizado — desenha cartas com drawImage.
    // ============================================================

    private class PainelCartas extends JPanel {

        private static final long serialVersionUID = 1L;

        PainelCartas() {
            setBackground(new Color(245, 240, 220));
            recalcular();
        }

        /** Recalcula tamanho preferido conforme número de cartas. */
        void recalcular() {
            List<int[]> cartas = jogo.getIdsCartasJogadorAtual();
            int[] contagem = new int[3]; // PERSONAGEM, ARMA, LOCAL
            for (int[] c : cartas) contagem[c[1]]++;
            int linhas = 3;
            int maxCol = Math.max(1,
                Math.max(contagem[0], Math.max(contagem[1], contagem[2])));
            int largura = MARGEM + 120 + maxCol * (CARTA_LARG + MARGEM);
            int altura  = MARGEM + linhas * (CARTA_ALT + MARGEM);
            setPreferredSize(new Dimension(largura, altura));
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics gOriginal) {
            super.paintComponent(gOriginal);
            Graphics2D g = (Graphics2D) gOriginal;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

            // Agrupa as cartas do jogador atual por tipo.
            List<Integer> personagens = new ArrayList<Integer>();
            List<Integer> armas       = new ArrayList<Integer>();
            List<Integer> locais      = new ArrayList<Integer>();
            for (int[] c : jogo.getIdsCartasJogadorAtual()) {
                if      (c[1] == 0) personagens.add(Integer.valueOf(c[0]));
                else if (c[1] == 1) armas.add(Integer.valueOf(c[0]));
                else                locais.add(Integer.valueOf(c[0]));
            }

            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(new Color(50, 50, 50));

            int y = MARGEM;
            desenharLinha(g, "Suspeito(s)", personagens, y);
            y += CARTA_ALT + MARGEM;
            desenharLinha(g, "Cômodo(s)",   locais,      y);
            y += CARTA_ALT + MARGEM;
            desenharLinha(g, "Arma(s)",     armas,       y);
        }

        private void desenharLinha(Graphics2D g, String titulo,
                                   List<Integer> ids, int y) {
            g.setColor(new Color(50, 50, 50));
            g.drawString(titulo, MARGEM, y + CARTA_ALT / 2);
            int x = MARGEM + 110;
            for (Integer id : ids) {
                BufferedImage img = recursos.getCarta(id.intValue());
                if (img != null) {
                    g.drawImage(img, x, y, CARTA_LARG, CARTA_ALT, null);
                }
                x += CARTA_LARG + MARGEM;
            }
        }
    }
}
