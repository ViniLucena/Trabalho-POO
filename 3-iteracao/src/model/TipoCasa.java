package model;

/**
 * Tipos possíveis de uma casa do tabuleiro.
 *
 *  CORREDOR  - casa transitável fora de qualquer cômodo;
 *  COMODO    - casa que faz parte de um cômodo;
 *  PORTA     - casa de corredor adjacente a um cômodo (entrada);
 *  BLOQUEADA - casa não transitável (fora do tabuleiro útil).
 */
enum TipoCasa {
    CORREDOR,
    COMODO,
    PORTA,
    BLOQUEADA
}
