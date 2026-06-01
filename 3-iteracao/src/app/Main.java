package app;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import controller.ControladorJogo;
import model.Jogo;
import view.JanelaInicial;
import view.JanelaPersonagens;
import view.JanelaPrincipal;
import view.PainelTabuleiro;
import view.ResourceLoader;

/**
 * Ponto de entrada da 3ª iteração. Encadeia as três janelas:
 *
 *   JanelaInicial → JanelaPersonagens → JanelaPrincipal
 *
 * O startup é feito dentro de EventQueue.invokeLater — boa
 * prática Swing — usando classe anônima (sem expressão lambda).
 */
public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                abrirJanelaInicial();
            }
        });
    }

    // ============================================================
    // 1) Janela Inicial
    // ============================================================

    private static void abrirJanelaInicial() {
        final JanelaInicial inicial = new JanelaInicial();

        inicial.getBtnNovoJogo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inicial.dispose();
                abrirJanelaPersonagens();
            }
        });

        inicial.getBtnContinuar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser ch = new JFileChooser();
                int res = ch.showOpenDialog(inicial);
                if (res != JFileChooser.APPROVE_OPTION) return;
                try {
                    Jogo jogo = Jogo.carregarPartida(ch.getSelectedFile());
                    inicial.dispose();
                    abrirJanelaPrincipal(jogo);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(inicial,
                        "Erro ao carregar: " + ex.getMessage(),
                        "Falha", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        inicial.setVisible(true);
    }

    // ============================================================
    // 2) Janela de Personagens
    // ============================================================

    private static void abrirJanelaPersonagens() {
        // ResourceLoader leve só para as cartas dos suspeitos.
        // Usamos um Jogo temporário só para gerar a lista de
        // cartas (ids 0..5 são os personagens). Após a escolha,
        // descartamos esse jogo e criamos o definitivo.
        final Jogo temporario = Jogo.novaPartida(6);
        final ResourceLoader recursos = new ResourceLoader(
            temporario,
            PainelTabuleiro.LARGURA, PainelTabuleiro.ALTURA,
            PainelTabuleiro.PINO_TAM,
            70,
            160, 220);

        final JanelaPersonagens janela = new JanelaPersonagens(recursos);

        janela.getBtnJogar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean[] sel = janela.getSelecionados();
                int n = 0;
                for (boolean b : sel) if (b) n++;
                if (n < 3 || n > 6) {
                    JOptionPane.showMessageDialog(janela,
                        "Marque entre 3 e 6 personagens.",
                        "Seleção inválida",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Jogo jogo = Jogo.novaPartida(sel);
                janela.dispose();
                abrirJanelaPrincipal(jogo);
            }
        });

        janela.setVisible(true);
    }

    // ============================================================
    // 3) Janela Principal (tabuleiro + lateral)
    // ============================================================

    private static void abrirJanelaPrincipal(Jogo jogo) {
        ResourceLoader recursos = new ResourceLoader(
            jogo,
            PainelTabuleiro.LARGURA, PainelTabuleiro.ALTURA,
            PainelTabuleiro.PINO_TAM,
            70,
            160, 220);
        JanelaPrincipal janela = new JanelaPrincipal(jogo, recursos);
        new ControladorJogo(jogo, janela);
        janela.setVisible(true);
    }
}
