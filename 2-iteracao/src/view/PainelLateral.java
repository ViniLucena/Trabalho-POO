package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Jogo;

/**
 * Painel lateral à direita do tabuleiro. Reúne os componentes
 * Swing permitidos pelo enunciado: botões para "Lançar Dados",
 * "Passagem Secreta" e "Próximo Jogador", JComboBoxes para
 * definir manualmente os valores dos dados (recomendado pelo
 * enunciado para testes) e a área de exibição dos dados —
 * pintada com Java2D usando drawImage().
 *
 * A cor de fundo do painel de dados muda conforme o jogador
 * da vez, atendendo ao requisito de indicar visualmente quem
 * está jogando (Figura 4 do enunciado).
 */
public class PainelLateral extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int DADO_TAM = 80;

    private final Jogo jogo;
    private final ResourceLoader recursos;

    private final PainelDados painelDados;
    private final JLabel labelJogador;
    private final JButton btnLancar;
    private final JButton btnPassagem;
    private final JButton btnProximo;
    private final JComboBox<Integer> comboD1;
    private final JComboBox<Integer> comboD2;
    private final JButton btnAplicarDadosManuais;

    public PainelLateral(Jogo jogo, ResourceLoader recursos) {
        this.jogo = jogo;
        this.recursos = recursos;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(280, PainelTabuleiro.ALTURA));
        setBackground(new Color(70, 70, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.weightx = 1.0;
        int linha = 0;

        labelJogador = new JLabel("", SwingConstants.CENTER);
        labelJogador.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelJogador.setForeground(Color.WHITE);
        gbc.gridy = linha++;
        add(labelJogador, gbc);

        painelDados = new PainelDados();
        gbc.gridy = linha++;
        add(painelDados, gbc);

        btnLancar = new JButton("Lançar Dados");
        gbc.gridy = linha++;
        add(btnLancar, gbc);

        // JComboBoxes para definir manualmente o lançamento.
        JPanel painelManual = new JPanel();
        painelManual.setLayout(new BoxLayout(painelManual, BoxLayout.Y_AXIS));
        painelManual.setOpaque(false);

        JLabel lblTeste = new JLabel("Definir dados (teste)",
                                     SwingConstants.CENTER);
        lblTeste.setForeground(Color.WHITE);
        lblTeste.setAlignmentX(CENTER_ALIGNMENT);
        painelManual.add(lblTeste);
        painelManual.add(Box.createVerticalStrut(4));

        JPanel linhaCombos = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        linhaCombos.setOpaque(false);
        Integer[] valores = { 1, 2, 3, 4, 5, 6 };
        comboD1 = new JComboBox<Integer>(valores);
        comboD2 = new JComboBox<Integer>(valores);
        linhaCombos.add(comboD1);
        linhaCombos.add(comboD2);
        painelManual.add(linhaCombos);
        painelManual.add(Box.createVerticalStrut(4));

        btnAplicarDadosManuais = new JButton("Usar valores acima");
        btnAplicarDadosManuais.setAlignmentX(CENTER_ALIGNMENT);
        painelManual.add(btnAplicarDadosManuais);

        gbc.gridy = linha++;
        add(painelManual, gbc);

        btnPassagem = new JButton("Passagem Secreta");
        btnPassagem.setEnabled(false);
        gbc.gridy = linha++;
        add(btnPassagem, gbc);

        btnProximo = new JButton("Próximo Jogador");
        gbc.gridy = linha++;
        add(btnProximo, gbc);

        gbc.gridy = linha++;
        gbc.weighty = 1.0;
        add(Box.createGlue(), gbc);

        atualizar();
    }

    // ============================================================
    // API usada pelo Controller
    // ============================================================

    public JButton getBtnLancar()                  { return btnLancar; }
    public JButton getBtnPassagem()                { return btnPassagem; }
    public JButton getBtnProximo()                 { return btnProximo; }
    public JButton getBtnAplicarDadosManuais()     { return btnAplicarDadosManuais; }
    public int getComboD1Selecionado()             { return ((Integer) comboD1.getSelectedItem()).intValue(); }
    public int getComboD2Selecionado()             { return ((Integer) comboD2.getSelectedItem()).intValue(); }

    /** Refaz o label do jogador atual e o estado dos botões. */
    public void atualizar() {
        String nome = jogo.getNomeJogadorAtual();
        String cor  = jogo.getCorJogadorAtual();
        labelJogador.setText(nome);
        labelJogador.setForeground(ResourceLoader.corPino(cor));

        btnLancar.setEnabled(!jogo.jaLancouNestaVez());
        btnPassagem.setEnabled(jogo.podeUsarPassagemSecreta());
        painelDados.repaint();
    }

    // ============================================================
    // Sub-painel que desenha os dois dados (com drawImage)
    // ============================================================

    /**
     * Painel interno que pinta o resultado dos dois dados via
     * drawImage() e, como fundo, a cor do jogador da vez —
     * técnica sugerida na Figura 4 do enunciado para sinalizar
     * de quem é o turno.
     */
    private class PainelDados extends JPanel {

        private static final long serialVersionUID = 1L;

        PainelDados() {
            setPreferredSize(new Dimension(220, DADO_TAM + 24));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics gOriginal) {
            super.paintComponent(gOriginal);
            Graphics2D g = (Graphics2D) gOriginal;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

            // Fundo com a cor do jogador da vez.
            Color cor = ResourceLoader.corPino(jogo.getCorJogadorAtual());
            g.setColor(cor);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g.setColor(cor.darker());
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

            int[] lanc = jogo.getUltimoLancamento();
            int d1 = (lanc == null) ? 1 : lanc[0];
            int d2 = (lanc == null) ? 1 : lanc[1];

            int totalLarg = DADO_TAM * 2 + 24;
            int x = (getWidth() - totalLarg) / 2;
            int y = (getHeight() - DADO_TAM) / 2;

            BufferedImage img1 = recursos.getDado(d1);
            BufferedImage img2 = recursos.getDado(d2);
            g.drawImage(img1, x, y, DADO_TAM, DADO_TAM, null);
            g.drawImage(img2, x + DADO_TAM + 24, y, DADO_TAM, DADO_TAM, null);
        }
    }
}
