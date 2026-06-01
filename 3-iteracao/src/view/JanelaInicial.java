package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Primeira janela do jogo (Figura 1 do enunciado). Oferece os
 * dois botões "Novo Jogo" e "Continuar".
 *
 * O fechamento e a transição para a próxima janela são feitos
 * pelo Controller, que registra ActionListeners nos botões.
 */
public class JanelaInicial extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JButton btnNovoJogo;
    private final JButton btnContinuar;

    public JanelaInicial() {
        super("Clue - Início");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        FundoInicial fundo = new FundoInicial();
        fundo.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        btnNovoJogo  = new JButton("Novo Jogo");
        btnContinuar = new JButton("Continuar");
        btnNovoJogo.setPreferredSize(new Dimension(160, 36));
        btnContinuar.setPreferredSize(new Dimension(160, 36));

        gbc.gridy = 0;
        fundo.add(btnNovoJogo, gbc);
        gbc.gridy = 1;
        fundo.add(btnContinuar, gbc);

        setContentPane(fundo);
        setSize(640, 480);
        setLocationRelativeTo(null);
    }

    public JButton getBtnNovoJogo()  { return btnNovoJogo; }
    public JButton getBtnContinuar() { return btnContinuar; }

    /**
     * Painel de fundo com um gradiente discreto e o título
     * "CLUE" desenhado. Não usamos imagem porque o enunciado
     * não disponibilizou capa.
     */
    private static class FundoInicial extends JPanel {

        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics gOriginal) {
            super.paintComponent(gOriginal);
            Graphics2D g = (Graphics2D) gOriginal;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            // Gradiente vertical simples (degradê)
            for (int y = 0; y < getHeight(); y++) {
                float t = y / (float) getHeight();
                int r = (int) (80  + 30 * t);
                int v = (int) (20  + 30 * t);
                int b = (int) (20  + 80 * t);
                g.setColor(new Color(r, v, b));
                g.drawLine(0, y, getWidth(), y);
            }
            g.setColor(new Color(255, 240, 220));
            g.setFont(new Font("Serif", Font.BOLD, 96));
            String titulo = "CLUE";
            int sw = g.getFontMetrics().stringWidth(titulo);
            g.drawString(titulo, (getWidth() - sw) / 2, 130);
            g.setColor(new Color(220, 220, 220));
            g.setFont(new Font("SansSerif", Font.ITALIC, 16));
            String sub = "O Clássico Jogo dos Investigadores";
            int sw2 = g.getFontMetrics().stringWidth(sub);
            g.drawString(sub, (getWidth() - sw2) / 2, 160);
        }
    }
}
