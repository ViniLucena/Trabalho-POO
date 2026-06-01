package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Baralho do Clue. Mantém as três pilhas (PERSONAGEM, ARMA,
 * LOCAL) num HashMap indexado pelo enum Tipo. Os nomes dos
 * cômodos coincidem com os criados pelo Tabuleiro, garantindo
 * que palpites/acusações possam ser cruzados com posições reais
 * dos pinos.
 */
class Baralho {

    private final Map<Carta.Tipo, List<Carta>> pilhas;
    private int proximoId = 0;

    Baralho() {
        this.pilhas = new HashMap<Carta.Tipo, List<Carta>>();
        this.pilhas.put(Carta.Tipo.PERSONAGEM, new ArrayList<Carta>());
        this.pilhas.put(Carta.Tipo.ARMA,       new ArrayList<Carta>());
        this.pilhas.put(Carta.Tipo.LOCAL,      new ArrayList<Carta>());
        inicializarCartas();
    }

    private void inicializarCartas() {
        // 6 personagens (mesma ordem das cores: vermelho, amarelo,
        // branco, verde, azul, violeta).
        adicionar("Srta. Scarlet",      Carta.Tipo.PERSONAGEM);
        adicionar("Coronel Mustard",    Carta.Tipo.PERSONAGEM);
        adicionar("Sra. White",         Carta.Tipo.PERSONAGEM);
        adicionar("Reverendo Green",    Carta.Tipo.PERSONAGEM);
        adicionar("Sra. Peacock",       Carta.Tipo.PERSONAGEM);
        adicionar("Professor Plum",     Carta.Tipo.PERSONAGEM);

        // 6 armas
        adicionar("Castiçal",      Carta.Tipo.ARMA);
        adicionar("Revólver",      Carta.Tipo.ARMA);
        adicionar("Faca",          Carta.Tipo.ARMA);
        adicionar("Cano",          Carta.Tipo.ARMA);
        adicionar("Corda",         Carta.Tipo.ARMA);
        adicionar("Chave Inglesa", Carta.Tipo.ARMA);

        // 9 cômodos (mesmos nomes usados no Tabuleiro)
        adicionar("Cozinha",           Carta.Tipo.LOCAL);
        adicionar("Sala de Música",    Carta.Tipo.LOCAL);
        adicionar("Jardim de Inverno", Carta.Tipo.LOCAL);
        adicionar("Sala de Jantar",    Carta.Tipo.LOCAL);
        adicionar("Salão de Jogos",    Carta.Tipo.LOCAL);
        adicionar("Biblioteca",        Carta.Tipo.LOCAL);
        adicionar("Sala de Estar",     Carta.Tipo.LOCAL);
        adicionar("Entrada",           Carta.Tipo.LOCAL);
        adicionar("Escritório",        Carta.Tipo.LOCAL);
    }

    private void adicionar(String nome, Carta.Tipo tipo) {
        pilhas.get(tipo).add(new Carta(proximoId++, nome, tipo));
    }

    /** Sorteia uma carta de cada tipo e monta o envelope-solução. */
    Envelope sortearSolucao() {
        Collections.shuffle(pilhas.get(Carta.Tipo.PERSONAGEM));
        Collections.shuffle(pilhas.get(Carta.Tipo.ARMA));
        Collections.shuffle(pilhas.get(Carta.Tipo.LOCAL));

        Carta p = pilhas.get(Carta.Tipo.PERSONAGEM).remove(0);
        Carta a = pilhas.get(Carta.Tipo.ARMA).remove(0);
        Carta l = pilhas.get(Carta.Tipo.LOCAL).remove(0);

        return new Envelope(p, a, l);
    }

    /** Distribui as cartas restantes entre os jogadores. */
    void distribuir(List<Jogador> jogadores) {
        List<Carta> restantes = new ArrayList<Carta>();
        restantes.addAll(pilhas.get(Carta.Tipo.PERSONAGEM));
        restantes.addAll(pilhas.get(Carta.Tipo.ARMA));
        restantes.addAll(pilhas.get(Carta.Tipo.LOCAL));
        Collections.shuffle(restantes);

        pilhas.get(Carta.Tipo.PERSONAGEM).clear();
        pilhas.get(Carta.Tipo.ARMA).clear();
        pilhas.get(Carta.Tipo.LOCAL).clear();

        int n = jogadores.size();
        for (int i = 0; i < restantes.size(); i++) {
            jogadores.get(i % n).receberCarta(restantes.get(i));
        }
    }
}
