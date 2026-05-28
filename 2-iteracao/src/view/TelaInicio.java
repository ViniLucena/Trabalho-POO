package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Tela inicial do jogo Clue (mockada para a 2ª iteração).
 * Exibe fundo com imagem ou cor sólida e dois botões de navegação.
 */
public class TelaInicio extends JFrame {

    // variaveis
    private static final int JANELA_LARGURA = 800;
    private static final int JANELA_ALTURA  = 600;

    // métodos

    /** Cria a tela inicial. */
    public TelaInicio() {
        setTitle("Clue - Início");
        setSize(JANELA_LARGURA, JANELA_ALTURA);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        construirInterface();
    }

    private void construirInterface() {
        final BufferedImage imgFundo = carregarImagem("assets/tabuleiro_inicio.png");

        JPanel painelFundo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (imgFundo != null) {
                    g2.drawImage(imgFundo, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(new Color(0, 80, 0));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Serif", Font.BOLD, 100));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth()  - fm.stringWidth("CLUE")) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2 - 40;
                    g2.drawString("CLUE", x, y);
                }
            }
        };

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelBotoes.setOpaque(false);

        JButton btnNovoJogo   = new JButton("Novo Jogo");
        JButton btnContinuar  = new JButton("Continuar");
        btnContinuar.setEnabled(false);

        btnNovoJogo.setFont(new Font("Arial", Font.BOLD, 16));
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 16));

        btnNovoJogo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TelaPersonagens().setVisible(true);
            }
        });

        painelBotoes.add(btnNovoJogo);
        painelBotoes.add(btnContinuar);
        painelFundo.add(painelBotoes, BorderLayout.SOUTH);

        setContentPane(painelFundo);
    }

    private BufferedImage carregarImagem(String caminho) {
        try {
            File f = new File(caminho);
            if (f.exists()) {
                return ImageIO.read(f);
            }
        } catch (IOException e) {
            // fallback: null
        }
        return null;
    }
}
