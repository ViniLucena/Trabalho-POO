package view;

import model.Jogo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Janela auxiliar que exibe as cartas da mão do jogador atual agrupadas por tipo.
 */
public class JanelaCartas extends JFrame {

    // variaveis
    private final Jogo jogo;

    // métodos

    /**
     * Cria a janela de cartas para o jogador atual.
     */
    public JanelaCartas(Jogo jogo) {
        this.jogo = jogo;
        setTitle("Clue - Cartas de " + jogo.getNomeJogadorAtual());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterface();
        pack();
        setLocationRelativeTo(null);
    }

    private void construirInterface() {
        List<String> cartas = jogo.getCartasJogadorAtual();

        List<String> personagens = new ArrayList<String>();
        List<String> armas       = new ArrayList<String>();
        List<String> locais      = new ArrayList<String>();

        for (String carta : cartas) {
            int parIdx = carta.lastIndexOf(" (");
            String nome = carta.substring(0, parIdx);
            String tipo = carta.substring(parIdx + 2, carta.length() - 1);
            if (tipo.equals("PERSONAGEM")) {
                personagens.add(nome);
            } else if (tipo.equals("ARMA")) {
                armas.add(nome);
            } else {
                locais.add(nome);
            }
        }

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(30, 30, 30));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        adicionarSecao(painel, "Suspeitos", personagens);
        adicionarSecao(painel, "Armas", armas);
        adicionarSecao(painel, "Cômodos", locais);

        JScrollPane scroll = new JScrollPane(painel);
        scroll.setPreferredSize(new Dimension(280, 400));
        add(scroll);
    }

    private void adicionarSecao(JPanel painel, String titulo, List<String> nomes) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.YELLOW);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);

        if (nomes.isEmpty()) {
            JLabel lblVazio = new JLabel("  (nenhuma)");
            lblVazio.setForeground(Color.GRAY);
            lblVazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(lblVazio);
        } else {
            for (String nome : nomes) {
                painel.add(criarComponenteCarta(nome));
            }
        }
        painel.add(Box.createVerticalStrut(8));
    }

    private JComponent criarComponenteCarta(String nome) {
        String nomeArq = nome.toLowerCase()
                .replace(" ", "_").replace(".", "").replace("ã", "a")
                .replace("é", "e").replace("ê", "e").replace("ú", "u")
                .replace("í", "i").replace("ó", "o") + ".png";
        File arquivo = new File("assets/" + nomeArq);

        if (arquivo.exists()) {
            try {
                final BufferedImage img = ImageIO.read(arquivo);
                JPanel painelCarta = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                    }
                };
                painelCarta.setPreferredSize(new Dimension(80, 110));
                painelCarta.setMaximumSize(new Dimension(80, 110));
                painelCarta.setAlignmentX(Component.LEFT_ALIGNMENT);
                return painelCarta;
            } catch (IOException e) {
                // fallback para JLabel
            }
        }

        JLabel lbl = new JLabel("  " + nome);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
