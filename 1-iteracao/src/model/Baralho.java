package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Baralho de cartas do Clue. Conhece as três categorias
 * (PERSONAGEM, ARMA, LOCAL) e implementa as regras 4–7 do
 * capítulo "Preparação" do manual:
 *
 *  4. Sortear, sem mostrar, uma carta de cada categoria e
 *     guardar no envelope (a solução do crime);
 *  5. Embaralhar as cartas restantes;
 *  6. Distribuir as cartas restantes entre os jogadores
 *     (em quantidades o mais iguais possível);
 *  7. Cada jogador olha as suas cartas — modelado pelo
 *     método getMao() de Jogador.
 *
 * Atende ao enunciado do trabalho que pede o uso de HashMap
 * "sempre que possível": as três pilhas de cartas ficam
 * organizadas num HashMap indexado pelo enum Carta.Tipo.
 */
class Baralho {

	private final Map<Carta.Tipo, List<Carta>> pilhas;

	Baralho() {
		this.pilhas = new HashMap<>();
		this.pilhas.put(Carta.Tipo.PERSONAGEM, new ArrayList<>());
		this.pilhas.put(Carta.Tipo.ARMA,       new ArrayList<>());
		this.pilhas.put(Carta.Tipo.LOCAL,      new ArrayList<>());
		inicializarCartas();
	}

	private void inicializarCartas() {
		// 6 personagens
		adicionar("Coronel Mustard", Carta.Tipo.PERSONAGEM);
	    adicionar("Srta. Scarlet",    Carta.Tipo.PERSONAGEM);
	    adicionar("Professor Plum",   Carta.Tipo.PERSONAGEM);
	    adicionar("Reverend Green",   Carta.Tipo.PERSONAGEM);
	    adicionar("Sra. White",       Carta.Tipo.PERSONAGEM);
	    adicionar("Sra. Peacock",     Carta.Tipo.PERSONAGEM);

	    // 6 armas
	    adicionar("Corda",          Carta.Tipo.ARMA);
	    adicionar("Cano de Chumbo",  Carta.Tipo.ARMA);
	    adicionar("Faca",           Carta.Tipo.ARMA);
	    adicionar("Chave Inglesa",   Carta.Tipo.ARMA);
	    adicionar("Castiçal",       Carta.Tipo.ARMA);
	    adicionar("Revólver",       Carta.Tipo.ARMA);

	    // 9 locais
	    adicionar("Sala de Estar",  Carta.Tipo.LOCAL);
	    adicionar("Cozinha",        Carta.Tipo.LOCAL);
	    adicionar("Biblioteca",     Carta.Tipo.LOCAL);
	    adicionar("Escritório",     Carta.Tipo.LOCAL);
	    adicionar("Salão de Baile", Carta.Tipo.LOCAL);
	    adicionar("Sala de Jantar", Carta.Tipo.LOCAL);
	    adicionar("Varanda",        Carta.Tipo.LOCAL);
	    adicionar("Salão",          Carta.Tipo.LOCAL);
	    adicionar("Estudo",         Carta.Tipo.LOCAL);
	}

	private void adicionar(String nome, Carta.Tipo tipo) {
		pilhas.get(tipo).add(new Carta(nome, tipo));
	}

	/**
	 * Item 4 da Preparação: embaralha cada pilha, retira a
	 * primeira carta de cada uma e monta o envelope-solução.
	 */
	Envelope sortearSolucao() {
		Collections.shuffle(pilhas.get(Carta.Tipo.PERSONAGEM));
		Collections.shuffle(pilhas.get(Carta.Tipo.ARMA));
		Collections.shuffle(pilhas.get(Carta.Tipo.LOCAL));

		Carta p = pilhas.get(Carta.Tipo.PERSONAGEM).remove(0);
		Carta a = pilhas.get(Carta.Tipo.ARMA).remove(0);
		Carta l = pilhas.get(Carta.Tipo.LOCAL).remove(0);

		return new Envelope(p, a, l);
	}

	/**
	 * Itens 5 e 6 da Preparação: junta as cartas restantes,
	 * embaralha e distribui entre os jogadores em rodízio.
	 *
	 * Quando o número de cartas não é múltiplo do número de
	 * jogadores, alguns recebem uma carta a mais — exatamente
	 * como manda o manual.
	 */
	void distribuir(List<Jogador> jogadores) {
		List<Carta> restantes = new ArrayList<>();
		restantes.addAll(pilhas.get(Carta.Tipo.PERSONAGEM));
		restantes.addAll(pilhas.get(Carta.Tipo.ARMA));
		restantes.addAll(pilhas.get(Carta.Tipo.LOCAL));

		Collections.shuffle(restantes);

		// Esvazia as pilhas locais — as cartas agora pertencem
		// aos jogadores. Evita inconsistência se alguém chamar
		// distribuir() de novo por engano.
		pilhas.get(Carta.Tipo.PERSONAGEM).clear();
		pilhas.get(Carta.Tipo.ARMA).clear();
		pilhas.get(Carta.Tipo.LOCAL).clear();

		int n = jogadores.size();
		for (int i = 0; i < restantes.size(); i++) {
			jogadores.get(i % n).receberCarta(restantes.get(i));
		}
	}

	int totalCartasInicial() {
		// 6 + 6 + 9 = 21
		return 21;
	}
}
