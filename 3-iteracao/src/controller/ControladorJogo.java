package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import model.Jogo;
import view.JanelaAcusacao;
import view.JanelaPalpite;
import view.JanelaPrincipal;
import view.PainelLateral;
import view.PainelTabuleiro;

/**
 * Controlador da arquitetura MVC. Liga os eventos disparados
 * pela View aos métodos correspondentes da fachada Jogo (Model).
 *
 * Conforme exigência do enunciado, NÃO usamos expressões lambda
 * no tratamento de eventos: cada listener é uma classe anônima
 * no estilo do Capítulo 12 do material da disciplina.
 *
 * A atualização da interface é feita por meio do padrão Observer
 * implementado no Model: ao chamar um método da fachada que
 * altere o estado (lançarDados, tentarMover, ...), o próprio
 * Jogo notifica os observadores. O Controller não precisa
 * chamar "refresh()" manualmente.
 */
public class ControladorJogo {

    private final Jogo jogo;
    private final JanelaPrincipal janela;
    private final PainelTabuleiro painelTabuleiro;
    private final PainelLateral painelLateral;

    public ControladorJogo(Jogo jogo, JanelaPrincipal janela) {
        this.jogo            = jogo;
        this.janela          = janela;
        this.painelTabuleiro = janela.getPainelTabuleiro();
        this.painelLateral   = janela.getPainelLateral();
        instalarListeners();
    }

    private void instalarListeners() {

        // ---------- Lançar Dados ----------
        painelLateral.getBtnLancar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jogo.podeLancarDados()) return;
                jogo.lancarDados();
                if (jogo.getCasasAlcancaveis().isEmpty()) {
                    painelLateral.setStatus("Sem destinos válidos — passe a vez.");
                } else {
                    painelLateral.setStatus("Clique no tabuleiro para mover.");
                }
            }
        });

        // ---------- Aplicar dados manuais ----------
        painelLateral.getBtnAplicarDadosManuais().addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!jogo.podeLancarDados()) return;
                    int d1 = painelLateral.getComboD1Selecionado();
                    int d2 = painelLateral.getComboD2Selecionado();
                    jogo.setDadosManuais(d1, d2);
                    jogo.lancarDados();
                    painelLateral.setStatus("Clique no tabuleiro para mover.");
                }
            });

        // ---------- Passagem Secreta ----------
        painelLateral.getBtnPassagem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jogo.usarPassagemSecreta()) {
                    painelLateral.setStatus("Pino moveu via passagem secreta.");
                }
            }
        });

        // ---------- Próximo Jogador ----------
        painelLateral.getBtnProximo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.proximoJogador();
                painelLateral.setStatus(" ");
            }
        });

        // ---------- Palpite ----------
        painelLateral.getBtnPalpite().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jogo.podeFazerPalpite()) return;
                JanelaPalpite dlg = new JanelaPalpite(janela, jogo);
                dlg.setVisible(true);
            }
        });

        // ---------- Acusar ----------
        painelLateral.getBtnAcusar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jogo.podeFazerAcusacao()) return;
                JanelaAcusacao dlg = new JanelaAcusacao(janela, jogo);
                dlg.setVisible(true);
                if (jogo.isPartidaTerminada()) anunciarFim();
            }
        });

        // ---------- Mostrar Cartas ----------
        painelLateral.getBtnMostrarCartas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                janela.getJanelaCartas().setVisible(true);
            }
        });

        // ---------- Bloco de Notas ----------
        painelLateral.getBtnBlocoNotas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                janela.getJanelaNotas().setVisible(true);
            }
        });

        // ---------- Salvar Jogo ----------
        painelLateral.getBtnSalvar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser ch = new JFileChooser();
                ch.setSelectedFile(new File("clue-save.txt"));
                int res = ch.showSaveDialog(janela);
                if (res != JFileChooser.APPROVE_OPTION) return;
                try {
                    jogo.salvar(ch.getSelectedFile());
                    painelLateral.setStatus("Partida salva.");
                } catch (IOException ex) {
                    painelLateral.setStatus("Erro ao salvar: " + ex.getMessage());
                }
            }
        });

        // ---------- Clique no tabuleiro → deslocamento ----------
        // Se o destino não for válido, NADA acontece (sem mensagem,
        // sem troca de jogador) — exigência do enunciado.
        painelTabuleiro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] lc = painelTabuleiro.pixelParaCelula(e.getX(), e.getY());
                boolean moveu = jogo.tentarMover(lc[0], lc[1]);
                if (moveu) {
                    if (jogo.podeFazerPalpite()) {
                        painelLateral.setStatus("Você está num cômodo — pode dar palpite.");
                    } else {
                        painelLateral.setStatus("Pode passar a vez.");
                    }
                }
            }
        });
    }

    private void anunciarFim() {
        int v = jogo.getIndiceVencedor();
        String msg;
        if (v >= 0) {
            msg = "Vencedor: " + jogo.getNomePersonagem(v);
        } else {
            msg = "Ninguém venceu — partida encerrada.";
        }
        JOptionPane.showMessageDialog(janela, msg, "Fim de partida",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}
