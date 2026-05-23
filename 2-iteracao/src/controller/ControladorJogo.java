package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.Jogo;
import view.JanelaPrincipal;
import view.PainelLateral;
import view.PainelTabuleiro;

/**
 * Controlador único da arquitetura MVC. Liga os eventos
 * disparados pela View aos métodos correspondentes da fachada
 * Jogo (Model).
 *
 * IMPORTANTE: o enunciado da 2ª iteração proíbe o uso de
 * expressões lambda no tratamento de eventos de componentes
 * gráficos (penalidade de 2,0 pontos). Por isso, TODOS os
 * listeners aqui usam classes anônimas, no estilo apresentado
 * no Capítulo 12 do material da disciplina.
 */
public class ControladorJogo {

    private final Jogo jogo;
    private final JanelaPrincipal janela;
    private final PainelTabuleiro painelTabuleiro;
    private final PainelLateral painelLateral;

    public ControladorJogo(Jogo jogo, JanelaPrincipal janela) {
        this.jogo = jogo;
        this.janela = janela;
        this.painelTabuleiro = janela.getPainelTabuleiro();
        this.painelLateral   = janela.getPainelLateral();

        instalarListeners();
    }

    private void instalarListeners() {

        // Botão "Lançar Dados"
        painelLateral.getBtnLancar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jogo.jaLancouNestaVez()) return;
                jogo.lancarDados();
                refresh();
            }
        });

        // Botão "Usar valores acima" (define dados manuais e lança)
        painelLateral.getBtnAplicarDadosManuais().addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (jogo.jaLancouNestaVez()) return;
                    int d1 = painelLateral.getComboD1Selecionado();
                    int d2 = painelLateral.getComboD2Selecionado();
                    jogo.setDadosManuais(d1, d2);
                    jogo.lancarDados();
                    refresh();
                }
            });

        // Botão "Passagem Secreta"
        painelLateral.getBtnPassagem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jogo.podeUsarPassagemSecreta()) return;
                jogo.usarPassagemSecreta();
                refresh();
            }
        });

        // Botão "Próximo Jogador"
        painelLateral.getBtnProximo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.proximoJogador();
                refresh();
            }
        });

        // Clique sobre o tabuleiro → tentativa de deslocamento.
        // Conforme o enunciado, se o destino não for válido
        // NADA deve acontecer (sem mensagem, sem troca de
        // jogador). O método tentarMover retorna boolean apenas
        // para informar a View se houve atualização.
        painelTabuleiro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] lc = painelTabuleiro.pixelParaCelula(e.getX(), e.getY());
                boolean moveu = jogo.tentarMover(lc[0], lc[1]);
                if (moveu) {
                    refresh();
                }
            }
        });
    }

    /** Atualiza painel lateral e força repintura do tabuleiro. */
    private void refresh() {
        painelLateral.atualizar();
        painelTabuleiro.repaint();
        janela.repaint();
    }
}
