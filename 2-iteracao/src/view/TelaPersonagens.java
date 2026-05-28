package view;

import model.Jogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de seleção de personagens (mockada para a 2ª iteração).
 * Permite escolher entre 3 e 6 personagens antes de iniciar o jogo.
 */
public class TelaPersonagens extends JFrame {

    // variaveis
    private static final int JANELA_LARGURA = 600;
    private static final int JANELA_ALTURA  = 400;

    private final List<JCheckBox> checkboxes;
    private JButton btnJogar;

    // métodos

    /** Cria a tela de seleção de personagens. */
    public TelaPersonagens() {
        this.checkboxes = new ArrayList<JCheckBox>();
        setTitle("Clue - Personagens");
        setSize(JANELA_LARGURA, JANELA_ALTURA);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelFundo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(50, 50, 60));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JLabel titulo = new JLabel("Escolha os Personagens (3 a 6)", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        painelFundo.add(titulo, BorderLayout.NORTH);

        JPanel painelCheckboxes = new JPanel(new GridLayout(3, 2, 10, 10));
        painelCheckboxes.setOpaque(false);
        painelCheckboxes.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        String[] personagens = {
            "Srta. Scarlet",    "Coronel Mustard",
            "Sra. White",       "Reverendo Green",
            "Sra. Peacock",     "Professor Plum"
        };

        for (String personagem : personagens) {
            JCheckBox chk = new JCheckBox(personagem);
            chk.setForeground(Color.WHITE);
            chk.setOpaque(false);
            chk.setFont(new Font("Arial", Font.PLAIN, 14));
            checkboxes.add(chk);
            chk.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    atualizarBotaoJogar();
                }
            });
            painelCheckboxes.add(chk);
        }
        painelFundo.add(painelCheckboxes, BorderLayout.CENTER);

        btnJogar = new JButton("Jogar");
        btnJogar.setEnabled(false);
        btnJogar.setFont(new Font("Arial", Font.BOLD, 14));

        btnJogar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<String> nomesSelecionados = new ArrayList<String>();
                for (JCheckBox chk : checkboxes) {
                    if (chk.isSelected()) {
                        nomesSelecionados.add(chk.getText());
                    }
                }
                Jogo jogoNovo = new Jogo(nomesSelecionados);
                dispose();
                new TelaPrincipal(jogoNovo).setVisible(true);
            }
        });

        JPanel painelBotao = new JPanel();
        painelBotao.setOpaque(false);
        painelBotao.add(btnJogar);
        painelFundo.add(painelBotao, BorderLayout.SOUTH);

        setContentPane(painelFundo);
    }

    private void atualizarBotaoJogar() {
        int count = 0;
        for (JCheckBox chk : checkboxes) {
            if (chk.isSelected()) count++;
        }
        btnJogar.setEnabled(count >= 3 && count <= 6);
    }
}
