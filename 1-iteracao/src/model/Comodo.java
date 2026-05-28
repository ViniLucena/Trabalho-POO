package model;

import java.util.*;

/**
 * Representa um cômodo da mansão no jogo Clue.
 */
class Comodo {

    // variaveis
    private final String nome;
    private final List<Integer> idPortas;
    private final int idCasaRepresentativa;
    private final String passagemSecreta;

    // métodos
    Comodo(String nome, int idCasaRepresentativa, String passagemSecreta) {
        this.nome = nome;
        this.idCasaRepresentativa = idCasaRepresentativa;
        this.passagemSecreta = passagemSecreta;
        this.idPortas = new ArrayList<Integer>();
    }

    String getNome() {
        return nome;
    }

    List<Integer> getIdPortas() {
        return Collections.unmodifiableList(idPortas);
    }

    int getIdCasaRepresentativa() {
        return idCasaRepresentativa;
    }

    String getPassagemSecreta() {
        return passagemSecreta;
    }

    void adicionarPorta(int idCasa) {
        idPortas.add(idCasa);
    }
}
