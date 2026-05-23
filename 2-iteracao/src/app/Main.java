package app;

import java.awt.EventQueue;

import controller.ControladorJogo;
import model.Jogo;
import view.JanelaPrincipal;

/**
 * Ponto de entrada da 2ª iteração. Cria o Model (Jogo),
 * a View (JanelaPrincipal) e o Controller, montando os três
 * lados da arquitetura MVC.
 *
 * O startup gráfico é feito dentro de EventQueue.invokeLater
 * — boa prática Swing — usando classe anônima (sem lambda).
 */
public class Main {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 6 jogadores na ordem clássica do Clue.
                Jogo jogo = new Jogo(6);

                JanelaPrincipal janela = new JanelaPrincipal(jogo);
                new ControladorJogo(jogo, janela);

                janela.setVisible(true);
            }
        });
    }
}
