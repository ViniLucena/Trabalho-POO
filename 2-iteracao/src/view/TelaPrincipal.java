package view;

import model.Jogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Janela principal do jogo: tabuleiro à esquerda e painel lateral à direita.
 */
public class TelaPrincipal extends JFrame {

    // variaveis
    private static final int JANELA_LARGURA = 1200;
    private static final int JANELA_ALTURA  = 800;

    private final Jogo jogo;
    private PainelTabuleiro painelTabuleiro;
    private JLabel lblJogadorAtual;
    private JButton btnJogarDados;
    private JButton btnPassagem;
    private JComboBox<Integer> cbDado1;
    private JComboBox<Integer> cbDado2;

    // métodos

    /**
     * Cria a tela principal associada ao jogo fornecido.
     */
    public TelaPrincipal(Jogo jogo) {
        this.jogo = jogo;
        setTitle("Clue - Jogo");
        setSize(JANELA_LARGURA, JANELA_ALTURA);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        construirInterface();
    }

    private void construirInterface() {
        setLayout(new BorderLayout());

        painelTabuleiro = new PainelTabuleiro(jogo);
        add(painelTabuleiro, BorderLayout.CENTER);

        add(construirSidebar(), BorderLayout.EAST);
    }

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(190, JANELA_ALTURA));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        sidebar.setBackground(new Color(40, 40, 40));

        lblJogadorAtual = new JLabel("Vez de: " + jogo.getNomeJogadorAtual()
                + " (" + jogo.getCorJogadorAtual() + ")");
        lblJogadorAtual.setForeground(Color.WHITE);
        lblJogadorAtual.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblJogadorAtual);
        sidebar.add(Box.createVerticalStrut(10));

        btnJogarDados = criarBotao("Jogar Dados");
        sidebar.add(btnJogarDados);

        btnPassagem = criarBotao("Passagem Secreta");
        btnPassagem.setEnabled(jogo.temPassagemSecreta());
        sidebar.add(btnPassagem);

        JButton btnMostrarCartas = criarBotao("Mostrar Cartas");
        sidebar.add(btnMostrarCartas);

        JButton btnBlocoNotas = criarBotao("Bloco de Notas");
        sidebar.add(btnBlocoNotas);

        JButton btnPalpite = criarBotao("Palpite");
        btnPalpite.setEnabled(false);
        sidebar.add(btnPalpite);

        JButton btnAcusar = criarBotao("Acusar");
        btnAcusar.setEnabled(false);
        sidebar.add(btnAcusar);

        JButton btnSalvar = criarBotao("Salvar Jogo");
        btnSalvar.setEnabled(false);
        sidebar.add(btnSalvar);

        JButton btnProximo = criarBotao("Próximo");
        sidebar.add(btnProximo);

        sidebar.add(Box.createVerticalStrut(10));

        JLabel lblDefinirDados = new JLabel("Definir Dados:");
        lblDefinirDados.setForeground(Color.LIGHT_GRAY);
        lblDefinirDados.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblDefinirDados);

        cbDado1 = new JComboBox<Integer>();
        cbDado1.addItem(null);
        for (int i = 1; i <= 6; i++) {
            cbDado1.addItem(Integer.valueOf(i));
        }
        cbDado1.setMaximumSize(new Dimension(160, 25));
        sidebar.add(cbDado1);

        cbDado2 = new JComboBox<Integer>();
        cbDado2.addItem(null);
        for (int i = 1; i <= 6; i++) {
            cbDado2.addItem(Integer.valueOf(i));
        }
        cbDado2.setMaximumSize(new Dimension(160, 25));
        sidebar.add(cbDado2);

        configurarListeners(btnJogarDados, btnMostrarCartas, btnBlocoNotas, btnProximo);

        return sidebar;
    }

    private JButton criarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(170, 30));
        return btn;
    }

    private void configurarListeners(JButton btnJogarDados, JButton btnMostrarCartas,
                                     JButton btnBlocoNotas, JButton btnProximo) {
        btnJogarDados.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer d1 = (Integer) cbDado1.getSelectedItem();
                Integer d2 = (Integer) cbDado2.getSelectedItem();
                int[] resultado;
                if (d1 != null && d2 != null) {
                    jogo.definirDados(d1, d2);
                    resultado = new int[]{d1, d2};
                } else {
                    resultado = jogo.lancarDados();
                }
                painelTabuleiro.setUltimoLancamento(resultado);
                painelTabuleiro.setCasasAlcancaveis(jogo.mapearCasas(resultado));
                painelTabuleiro.repaint();
                btnJogarDados.setEnabled(false);
            }
        });

        btnPassagem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    jogo.usarPassagemSecreta();
                    painelTabuleiro.setCasasAlcancaveis(null);
                    painelTabuleiro.setUltimoLancamento(null);
                    painelTabuleiro.repaint();
                    lblJogadorAtual.setText("Vez de: " + jogo.getNomeJogadorAtual()
                            + " (" + jogo.getCorJogadorAtual() + ")");
                    btnPassagem.setEnabled(jogo.temPassagemSecreta());
                } catch (IllegalStateException ex) {
                    // Sem passagem — não ocorre pois botão só ativo quando há passagem
                }
            }
        });

        btnMostrarCartas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new JanelaCartas(jogo).setVisible(true);
            }
        });

        btnBlocoNotas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new JanelaNotas(jogo).setVisible(true);
            }
        });

        btnProximo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jogo.proximoJogador();
                lblJogadorAtual.setText("Vez de: " + jogo.getNomeJogadorAtual()
                        + " (" + jogo.getCorJogadorAtual() + ")");
                btnPassagem.setEnabled(jogo.temPassagemSecreta());
                btnJogarDados.setEnabled(true);
                painelTabuleiro.setCasasAlcancaveis(null);
                painelTabuleiro.setUltimoLancamento(null);
                painelTabuleiro.repaint();
            }
        });
    }
}
