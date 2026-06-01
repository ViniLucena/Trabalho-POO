package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Jogo;

/**
 * Segunda janela (Figura 2): escolha dos personagens. Mostra
 * uma "carta" para cada um dos 6 suspeitos clássicos, com um
 * checkbox abaixo. O botão "Jogar" só fica ativo quando 3 a 6
 * personagens estão marcados.
 *
 * Toda a imagem da carta é desenhada por drawImage(), conforme
 * exigência do enunciado.
 */
public class JanelaPersonagens extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int CARTA_LARG = 160;
    private static final int CARTA_ALT  = 220;

    private final JCheckBox[] checks = new JCheckBox[Jogo.CORES_PADRAO.length];
    private final JButton btnJogar;

    public JanelaPersonagens(ResourceLoader recursos) {
        super("Clue - Personagens");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel grid = new JPanel(new GridLayout(2, 4, 8, 8));
        grid.setBackground(new Color(70, 50, 30));
        grid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < Jogo.CORES_PADRAO.length; i++) {
            // Recupera o id da carta de personagem (0..5 por construção).
            BufferedImage img = recursos.getCarta(i);
            JCheckBox cb = new JCheckBox(Jogo.PERSONAGENS_PADRAO[i],
                                         i < 3); // marca os 3 primeiros
            cb.setForeground(Color.WHITE);
            cb.setBackground(grid.getBackground());
            checks[i] = cb;
            grid.add(montarCelula(img, Jogo.PERSONAGENS_PADRAO[i], cb));
        }

        JPanel celulaJogar = new JPanel(new BorderLayout());
        celulaJogar.setBackground(grid.getBackground());
        JLabel titulo = new JLabel("Marque de 3 a 6 personagens",
                                   SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnJogar = new JButton("Jogar");
        celulaJogar.add(titulo, BorderLayout.NORTH);
        celulaJogar.add(btnJogar, BorderLayout.SOUTH);
        grid.add(celulaJogar);

        setContentPane(grid);
        pack();
        setLocationRelativeTo(null);
    }

    public JButton getBtnJogar() { return btnJogar; }

    /** Vetor booleano (tamanho 6) com os personagens marcados. */
    public boolean[] getSelecionados() {
        boolean[] sel = new boolean[checks.length];
        for (int i = 0; i < checks.length; i++) sel[i] = checks[i].isSelected();
        return sel;
    }

    private JPanel montarCelula(final BufferedImage img, final String nome,
                                final JCheckBox cb) {
        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setBackground(new Color(70, 50, 30));

        JPanel imgPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics gOriginal) {
                super.paintComponent(gOriginal);
                Graphics2D g = (Graphics2D) gOriginal;
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (img != null) {
                    g.drawImage(img, 0, 0, CARTA_LARG, CARTA_ALT, null);
                }
            }
        };
        imgPanel.setPreferredSize(new Dimension(CARTA_LARG, CARTA_ALT));
        imgPanel.setBackground(cell.getBackground());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rodape.setBackground(cell.getBackground());
        rodape.add(cb);

        cell.add(imgPanel, BorderLayout.CENTER);
        cell.add(rodape,   BorderLayout.SOUTH);
        return cell;
    }
}
