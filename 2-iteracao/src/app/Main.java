package app;

import view.TelaInicio;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da 2ª iteração do jogo Clue.
 * Inicia a interface gráfica na Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TelaInicio().setVisible(true);
            }
        });
    }
}
