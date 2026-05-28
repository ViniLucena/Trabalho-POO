package view;

import model.Jogo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Janela auxiliar que exibe as cartas da mão do jogador atual agrupadas por tipo.
 */
public class JanelaCartas extends JFrame {

    // variaveis
    private final Jogo jogo;
    private final HashMap<String, String> caminhosCarta;

    // métodos

    /**
     * Cria a janela de cartas para o jogador atual.
     */
    public JanelaCartas(Jogo jogo) {
        this.jogo = jogo;
        this.caminhosCarta = inicializarCaminhosCarta();
        setTitle("Clue - Cartas de " + jogo.getNomeJogadorAtual());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterface();
        pack();
        setLocationRelativeTo(null);
    }

    private HashMap<String, String> inicializarCaminhosCarta() {
        HashMap<String, String> mapa = new HashMap<String, String>();
        mapa.put("Srta. Scarlet",   "assets/Suspeitos/Scarlet.jpg");
        mapa.put("Coronel Mustard", "assets/Suspeitos/Mustard.jpg");
        mapa.put("Sra. White",      "assets/Suspeitos/White.jpg");
        mapa.put("Reverendo Green", "assets/Suspeitos/Green.jpg");
        mapa.put("Sra. Peacock",    "assets/Suspeitos/Peacock.jpg");
        mapa.put("Professor Plum",  "assets/Suspeitos/Plum.jpg");
        mapa.put("Corda",           "assets/Armas/Corda.jpg");
        mapa.put("Cano de Chumbo",  "assets/Armas/Cano.jpg");
        mapa.put("Faca",            "assets/Armas/Faca.jpg");
        mapa.put("Chave Inglesa",   "assets/Armas/ChaveInglesa.jpg");
        mapa.put("Castiçal",        "assets/Armas/Castical.jpg");
        mapa.put("Revólver",        "assets/Armas/Revolver.jpg");
        mapa.put("Cozinha",           "assets/Comodos/Cozinha.jpg");
        mapa.put("Sala de Música",    "assets/Comodos/SalaDeMusica.jpg");
        mapa.put("Jardim de Inverno", "assets/Comodos/JardimInverno.jpg");
        mapa.put("Sala de Jantar",    "assets/Comodos/SalaDeJantar.jpg");
        mapa.put("Salão de Jogos",    "assets/Comodos/SalaoDeJogos.jpg");
        mapa.put("Biblioteca",        "assets/Comodos/Biblioteca.jpg");
        mapa.put("Sala de Estar",     "assets/Comodos/SalaDeEstar.jpg");
        mapa.put("Entrada",           "assets/Comodos/Entrada.jpg");
        mapa.put("Escritório",        "assets/Comodos/Escritorio.jpg");
        return mapa;
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
        String caminho = caminhosCarta.get(nome);
        if (caminho != null) {
            File arquivo = new File(caminho);
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
        }

        JLabel lbl = new JLabel("  " + nome);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
