package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Jogo;

/**
 * Diálogo modal para o jogador escolher um suspeito e uma arma
 * ao fazer um palpite. O cômodo é fixo (o cômodo em que ele
 * está). Após confirmar, dispara {@code Jogo.fazerPalpite} e
 * mostra o resultado (quem refutou, qual carta foi mostrada).
 */
public class JanelaPalpite extends JDialog {

    private static final long serialVersionUID = 1L;

    private final Jogo jogo;

    public JanelaPalpite(Frame dono, Jogo jogo) {
        super(dono, "Palpite", true);
        this.jogo = jogo;

        String comodo = jogo.getComodoDoJogadorAtual();
        if (comodo == null) {
            // Não deveria ser aberto fora de um cômodo, mas por
            // segurança fechamos sem fazer nada.
            dispose();
            return;
        }

        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titulo = new JLabel("Palpite no cômodo: " + comodo);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        principal.add(titulo);
        principal.add(Box.createVerticalStrut(10));

        final JComboBox<NomeId> comboSuspeito = montarCombo("PERSONAGEM");
        final JComboBox<NomeId> comboArma     = montarCombo("ARMA");

        principal.add(linhaComLabel("Suspeito:", comboSuspeito));
        principal.add(Box.createVerticalStrut(6));
        principal.add(linhaComLabel("Arma:    ", comboArma));
        principal.add(Box.createVerticalStrut(10));

        final JLabel resultado = new JLabel(" ", JLabel.CENTER);
        resultado.setAlignmentX(CENTER_ALIGNMENT);
        principal.add(resultado);
        principal.add(Box.createVerticalStrut(10));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        final JButton btnConfirmar = new JButton("Confirmar");
        final JButton btnFechar    = new JButton("Fechar");
        botoes.add(btnConfirmar);
        botoes.add(btnFechar);
        principal.add(botoes);

        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NomeId s = (NomeId) comboSuspeito.getSelectedItem();
                NomeId a = (NomeId) comboArma.getSelectedItem();
                boolean ok = jogo.fazerPalpite(s.id, a.id);
                if (!ok) {
                    resultado.setText("Palpite não permitido agora.");
                    return;
                }
                int idx = jogo.getUltimoRefutadorIndice();
                int idCarta = jogo.getUltimoCartaMostradaId();
                if (idx >= 0 && idCarta >= 0) {
                    resultado.setText("<html>" + jogo.getNomePersonagem(idx)
                            + " mostrou a carta <b>"
                            + jogo.getNomeCarta(idCarta)
                            + "</b>.</html>");
                } else {
                    resultado.setText("Ninguém pôde refutar o palpite.");
                }
                btnConfirmar.setEnabled(false);
            }
        });

        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setContentPane(principal);
        pack();
        setLocationRelativeTo(dono);
    }

    private JComboBox<NomeId> montarCombo(String tipo) {
        List<int[]> ids = jogo.getIdsCartasDoTipo(tipo);
        NomeId[] items = new NomeId[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i)[0];
            items[i] = new NomeId(id, jogo.getNomeCarta(id));
        }
        return new JComboBox<NomeId>(items);
    }

    private JPanel linhaComLabel(String label, JComboBox<NomeId> combo) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.add(new JLabel(label));
        p.add(combo);
        return p;
    }

    /** Wrapper para guardar id + nome no JComboBox. */
    private static class NomeId {
        final int id;
        final String nome;
        NomeId(int id, String nome) { this.id = id; this.nome = nome; }
        @Override public String toString() { return nome; }
    }
}
