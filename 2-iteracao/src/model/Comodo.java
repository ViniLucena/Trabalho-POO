package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um cômodo (sala) do tabuleiro do Clue. Um cômodo
 * agrupa várias Casas do tipo COMODO e mantém referências para
 * suas portas (Casas do tipo PORTA, no corredor, adjacentes ao
 * cômodo) e para um eventual cômodo conectado por passagem
 * secreta.
 *
 * Visibilidade package-private — só Tabuleiro e Jogo manipulam
 * Comodos. A View enxerga cômodos apenas por nome/id, através
 * da fachada Jogo.
 */
class Comodo {

    private final int id;
    private final String nome;
    private final List<Casa> casas = new ArrayList<>();
    private final List<Casa> portas = new ArrayList<>();

    // Passagem secreta opcional para outro cômodo.
    private Comodo conexaoSecreta;

    Comodo(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    int getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    void adicionarCasa(Casa c) {
        casas.add(c);
    }

    void adicionarPorta(Casa porta) {
        portas.add(porta);
    }

    List<Casa> getCasas() {
        return casas;
    }

    List<Casa> getPortas() {
        return portas;
    }

    void setConexaoSecreta(Comodo outro) {
        this.conexaoSecreta = outro;
    }

    Comodo getConexaoSecreta() {
        return conexaoSecreta;
    }

    boolean temPassagemSecreta() {
        return conexaoSecreta != null;
    }

    /**
     * Retorna uma casa "âncora" para posicionar pinos quando
     * eles entram no cômodo (a primeira casa do cômodo serve).
     */
    Casa getCasaPrincipal() {
        return casas.isEmpty() ? null : casas.get(0);
    }
}
