package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cômodo (sala) do tabuleiro do Clue. Agrupa Casas do tipo
 * COMODO e mantém referências para suas portas e para um
 * eventual cômodo conectado por passagem secreta.
 */
class Comodo {

    private final int id;
    private final String nome;
    private final List<Casa> casas  = new ArrayList<Casa>();
    private final List<Casa> portas = new ArrayList<Casa>();

    private Comodo conexaoSecreta;

    Comodo(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    int getId()      { return id; }
    String getNome() { return nome; }

    void adicionarCasa(Casa c)   { casas.add(c); }
    void adicionarPorta(Casa p)  { portas.add(p); }

    List<Casa> getCasas()  { return casas; }
    List<Casa> getPortas() { return portas; }

    void setConexaoSecreta(Comodo outro) { this.conexaoSecreta = outro; }
    Comodo getConexaoSecreta()           { return conexaoSecreta; }
    boolean temPassagemSecreta()         { return conexaoSecreta != null; }

    /**
     * Retorna uma casa "âncora" para posicionar pinos quando eles
     * entram no cômodo (a primeira casa do cômodo serve).
     */
    Casa getCasaPrincipal() {
        return casas.isEmpty() ? null : casas.get(0);
    }
}
