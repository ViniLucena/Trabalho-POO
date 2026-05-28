package view;

import model.Jogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Janela auxiliar com o bloco de notas do jogador atual em três colunas.
 */
public class JanelaNotas extends JFrame {

    // variaveis
    private static final String[] PERSONAGENS = {
        "Coronel Mustard", "Srta. Scarlet", "Professor Plum",
        "Reverendo Green", "Sra. White",    "Sra. Peacock"
    };

    private static final String[] ARMAS = {
        "Corda", "Cano de Chumbo", "Faca",
        "Chave Inglesa", "Castiçal", "Revólver"
    };

    private static final String[] LOCAIS = {
        "Cozinha", "Sala de Música", "Jardim de Inverno",
        "Sala de Jantar", "Salão de Jogos", "Biblioteca",
        "Sala de Estar", "Entrada", "Escritório"
    };

    private final Jogo jogo;

    // métodos

    /**
     * Cria o bloco de notas para o jogador atual.
     */
    public JanelaNotas(Jogo jogo) {
        this.jogo = jogo;
        setTitle("Clue - Bloco de Notas de " + jogo.getNomeJogadorAtual());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterface();
        pack();
        setLocationRelativeTo(null);
    }

    private void construirInterface() {
        List<String> cartasStr = jogo.getCartasJogadorAtual();
        List<String> folha     = jogo.getFolhaDeNotasJogadorAtual();

        Set<String> nomesNaMao = new HashSet<String>();
        for (String s : cartasStr) {
            int idx = s.lastIndexOf(" (");
            nomesNaMao.add(s.substring(0, idx));
        }

        JPanel painelGeral = new JPanel(new GridLayout(1, 3, 10, 0));
        painelGeral.setBackground(new Color(35, 35, 35));
        painelGeral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelGeral.add(construirColuna("Suspeitos",  PERSONAGENS, nomesNaMao, folha));
        painelGeral.add(construirColuna("Armas",      ARMAS,       nomesNaMao, folha));
        painelGeral.add(construirColuna("Cômodos",    LOCAIS,      nomesNaMao, folha));

        JScrollPane scroll = new JScrollPane(painelGeral);
        scroll.setPreferredSize(new Dimension(540, 350));
        add(scroll);
    }

    private JPanel construirColuna(String titulo, String[] itens,
                                   Set<String> nomesNaMao, List<String> folha) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.YELLOW);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        col.add(lblTitulo);
        col.add(Box.createVerticalStrut(5));

        for (String item : itens) {
            final String nomeItem = item;
            boolean naMao     = nomesNaMao.contains(item);
            boolean naFolha   = folha.contains(item);

            final JCheckBox chk = new JCheckBox(item);
            chk.setForeground(Color.WHITE);
            chk.setOpaque(false);
            chk.setFont(new Font("Arial", Font.PLAIN, 12));
            chk.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (naMao) {
                // Carta na mão: marcada e desabilitada
                chk.setSelected(true);
                chk.setEnabled(false);
            } else if (!naFolha) {
                // Já foi riscada manualmente: marcada e desabilitada
                chk.setSelected(true);
                chk.setEnabled(false);
            } else {
                // Ainda não riscada: desmarcada, habilitada
                chk.setSelected(false);
                chk.setEnabled(true);
                chk.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            jogo.riscarDaFolha(nomeItem);
                            chk.setEnabled(false);
                        }
                    }
                });
            }

            col.add(chk);
        }

        return col;
    }
}
