package model;

/**
 * Observador, conforme o padrão Observer apresentado no Capítulo 17.
 *
 * As classes da View que precisam reagir a mudanças no estado do
 * jogo (movimentos, dados, etc.) implementam esta interface e
 * se registram em um Observado (no nosso caso, o Jogo).
 */
public interface Observador {

    /** Chamado pelo Observado quando o estado muda. */
    void notificar();
}
