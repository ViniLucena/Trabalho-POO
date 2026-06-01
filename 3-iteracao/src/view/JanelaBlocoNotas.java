package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Jogo;
import model.Observador;

/**
 * Janela "Bloco de Notas" do jogador da vez. Apresenta três
 * grupos de checkboxes (Suspeitos, Cômodos, Armas) e mantém o
 * estado das marcações no Model.
 *
 * As cartas que estão na mão do jogador já entram marcadas
 * (e desabilitadas, para indicar que não devem ser mudadas).
 *
 * Implementa Observador para se reconfigurar quando muda o
 * jogador da vez.
 */
public class JanelaBlocoNotas extends JFrame implements Observador {

    private static final long serialVersionUID = 1L;

    private final Jogo jogo;
    private JPanel painelGrupos;

    public JanelaBlocoNotas(Jogo jogo) {
        super("Clue - Bloco de Notas");
        this.jogo = jogo;
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(560, 360);
        setLocationRelativeTo(null);

        painelGrupos = new JPanel(new GridLayout(1, 3, 8, 0));
        painelGrupos.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setContentPane(painelGrupos);

        jogo.registrarObservador(this);
    }

    public void atualizar() {
        setTitle("Clue - Bloco de Notas de " + jogo.getNomeJogadorAtual());
        painelGrupos.removeAll();
        painelGrupos.add(grupo("Suspeitos", "PERSONAGEM"));
        painelGrupos.add(grupo("Cômodos",   "LOCAL"));
        painelGrupos.add(grupo("Armas",     "ARMA"));
        painelGrupos.revalidate();
        painelGrupos.repaint();
    }

    @Override
    public void setVisible(boolean v) {
        if (v) atualizar();
        super.setVisible(v);
    }

    @Override
    public void notificar() {
        if (isVisible()) atualizar();
    }

    private JPanel grupo(String titulo, String tipo) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.setLayout(new GridLayout(0, 1, 0, 2));
        p.setBackground(new Color(245, 245, 240));
        for (int[] info : jogo.getIdsCartasDoTipo(tipo)) {
            final int id = info[0];
            String nome  = jogo.getNomeCarta(id);
            final JCheckBox cb = new JCheckBox(nome, jogo.isCartaMarcada(id));
            cb.setBackground(p.getBackground());

            // Cartas na mão do jogador atual ficam fixas (são fato).
            if (estaNaMaoDoAtual(id)) cb.setEnabled(false);

            cb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    jogo.marcarCarta(id, cb.isSelected());
                }
            });
            p.add(cb);
        }
        return p;
    }

    private boolean estaNaMaoDoAtual(int idCarta) {
        for (int[] c : jogo.getIdsCartasJogadorAtual()) {
            if (c[0] == idCarta) return true;
        }
        return false;
    }
}
