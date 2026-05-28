package model;

import java.util.*;

/**
 * Gerencia as cartas do jogo: sorteia a solução e distribui as restantes.
 */
class Baralho {

    // variaveis
    private static final List<String> NOMES_PERSONAGENS = Arrays.asList(
            "Coronel Mustard", "Srta. Scarlet", "Professor Plum",
            "Reverendo Green", "Sra. White", "Sra. Peacock"
    );

    private static final List<String> NOMES_ARMAS = Arrays.asList(
            "Corda", "Cano de Chumbo", "Faca",
            "Chave Inglesa", "Castiçal", "Revólver"
    );

    private static final List<String> NOMES_LOCAIS = Arrays.asList(
            "Cozinha", "Sala de Música", "Jardim de Inverno",
            "Sala de Jantar", "Salão de Jogos", "Biblioteca",
            "Sala de Estar", "Entrada", "Escritório"
    );

    private final HashMap<Carta.Tipo, List<Carta>> pilhas;

    // métodos
    Baralho() {
        pilhas = new HashMap<Carta.Tipo, List<Carta>>();

        List<Carta> personagens = new ArrayList<Carta>();
        for (String nome : NOMES_PERSONAGENS) {
            personagens.add(new Carta(nome, Carta.Tipo.PERSONAGEM));
        }

        List<Carta> armas = new ArrayList<Carta>();
        for (String nome : NOMES_ARMAS) {
            armas.add(new Carta(nome, Carta.Tipo.ARMA));
        }

        List<Carta> locais = new ArrayList<Carta>();
        for (String nome : NOMES_LOCAIS) {
            locais.add(new Carta(nome, Carta.Tipo.LOCAL));
        }

        pilhas.put(Carta.Tipo.PERSONAGEM, personagens);
        pilhas.put(Carta.Tipo.ARMA, armas);
        pilhas.put(Carta.Tipo.LOCAL, locais);
    }

    Envelope sortearSolucao() {
        List<Carta> personagens = new ArrayList<Carta>(pilhas.get(Carta.Tipo.PERSONAGEM));
        List<Carta> armas = new ArrayList<Carta>(pilhas.get(Carta.Tipo.ARMA));
        List<Carta> locais = new ArrayList<Carta>(pilhas.get(Carta.Tipo.LOCAL));

        Collections.shuffle(personagens);
        Collections.shuffle(armas);
        Collections.shuffle(locais);

        Carta cartaPersonagem = personagens.remove(0);
        Carta cartaArma = armas.remove(0);
        Carta cartaLocal = locais.remove(0);

        pilhas.put(Carta.Tipo.PERSONAGEM, personagens);
        pilhas.put(Carta.Tipo.ARMA, armas);
        pilhas.put(Carta.Tipo.LOCAL, locais);

        return new Envelope(cartaPersonagem, cartaArma, cartaLocal);
    }

    void distribuir(List<Jogador> jogadores) {
        List<Carta> restantes = new ArrayList<Carta>();
        restantes.addAll(pilhas.get(Carta.Tipo.PERSONAGEM));
        restantes.addAll(pilhas.get(Carta.Tipo.ARMA));
        restantes.addAll(pilhas.get(Carta.Tipo.LOCAL));
        Collections.shuffle(restantes);

        int n = jogadores.size();
        for (int i = 0; i < restantes.size(); i++) {
            jogadores.get(i % n).receberCarta(restantes.get(i));
        }
    }

    static List<String> getTodosOsNomes() {
        List<String> todos = new ArrayList<String>();
        todos.addAll(NOMES_PERSONAGENS);
        todos.addAll(NOMES_ARMAS);
        todos.addAll(NOMES_LOCAIS);
        return todos;
    }
}
