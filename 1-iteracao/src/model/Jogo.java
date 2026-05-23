package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe pública do pacote Model. Concentra a API que será
 * consumida pelos pacotes View e Controller, escondendo todas
 * as classes internas (Tabuleiro, Casa, Carta, Baralho, etc.).
 *
 * Esta classe ainda NÃO é Singleton+Façade no sentido formal
 * exigido pelo trabalho — segundo o enunciado, esses padrões
 * só serão aplicados na 3ª iteração. No entanto, a estrutura
 * atual já segue o espírito da Façade (uma única porta de
 * entrada para o subsistema), o que torna a transformação
 * futura uma questão de tornar o construtor privado e expor
 * um método estático getInstance().
 *
 * Princípios aplicados:
 *  - Ocultação de informação: tudo que não é necessário ao
 *    Controller/View permanece package-private.
 *  - Aberto/Fechado: novos serviços podem ser adicionados
 *    como métodos públicos sem alterar os existentes; novas
 *    propriedades das casas/cartas não afetam a API.
 */
public class Jogo {

	// Cores fixas no Clue, na ordem clássica de jogo.
	private static final String[] CORES_PADRAO = {
		"Vermelho", "Amarelo", "Branco", "Verde", "Violeta", "Azul"
	};

	private final Tabuleiro tabuleiro;
	private final Baralho baralho;
	private final Dados dados;
	private final List<Jogador> jogadores;
	private final Envelope envelope;

	private int indiceJogadorAtual;
	private int[] ultimoLancamento; // null antes do primeiro lançamento da rodada

	/**
	 * Cria uma nova partida e executa imediatamente os itens
	 * 4–7 da Preparação: sorteia a solução (envelope), embaralha
	 * e distribui as cartas restantes entre os jogadores.
	 *
	 * @param nomesJogadores lista entre 3 e 6 nomes — limites
	 *                       padrão do Clue.
	 */
	public Jogo(List<String> nomesJogadores) {
		if (nomesJogadores == null || nomesJogadores.size() < 3 || nomesJogadores.size() > 6) {
			throw new IllegalArgumentException(
				"O Clue exige entre 3 e 6 jogadores.");
		}

		this.tabuleiro = new Tabuleiro();
		this.baralho   = new Baralho();
		this.dados     = new Dados();
		this.jogadores = new ArrayList<>();

		// Cria os jogadores e seus pinos nas posições iniciais.
		for (int i = 0; i < nomesJogadores.size(); i++) {
			Jogador j = new Jogador(nomesJogadores.get(i), CORES_PADRAO[i]);
			Casa inicio = tabuleiro.getPosicaoInicial(i);
			j.setPino(new Pino(CORES_PADRAO[i], inicio));
			jogadores.add(j);
		}

		// Itens 4 e 5 da Preparação.
		this.envelope = baralho.sortearSolucao();
		// Itens 6 e 7 da Preparação.
		baralho.distribuir(jogadores);

		for (int i = 0; i < jogadores.size(); i++) {
		    if (jogadores.get(i).getCor().equals("Vermelho")) {
		        this.indiceJogadorAtual = i;
		        break;
		    }
		}
		this.ultimoLancamento = null;
	}

	// ---------- Etapas de uma jogada ----------

	/**
	 * Etapa A: lança os dois dados virtualmente.
	 * @return array de 2 inteiros, cada um entre 1 e 6.
	 */
	public int[] lancarDados() {
		this.ultimoLancamento = dados.lancar();
		return Arrays.copyOf(ultimoLancamento, ultimoLancamento.length);
	}

	/**
	 * Etapa B: dado o resultado dos dados, devolve os ids das
	 * casas que o pião do jogador atual pode alcançar.
	 *
	 * @param valoresDados array com os dois valores obtidos no
	 *                     lançamento; o número de passos é a
	 *                     soma dos dois.
	 */
	public List<Integer> mapearCasas(int[] valoresDados) {
		if (valoresDados == null || valoresDados.length != 2) {
			throw new IllegalArgumentException(
				"São esperados exatamente dois valores de dados.");
		}
		int passos = valoresDados[0] + valoresDados[1];
		Casa origem = jogadores.get(indiceJogadorAtual).getPino().getCasa();
		return tabuleiro.mapearCasasAlcancaveis(origem, passos);
	}

	/**
	 * Etapa C: move o pino do jogador da vez para a casa
	 * indicada. Lança IllegalArgumentException se a casa não
	 * estiver na lista de alcançáveis do último lançamento.
	 */
	public void deslocarPiao(int idCasa) {
		if (ultimoLancamento == null) {
			throw new IllegalStateException(
				"É preciso lançar os dados antes de deslocar o pião.");
		}
		List<Integer> validas = mapearCasas(ultimoLancamento);
		if (!validas.contains(idCasa)) {
			throw new IllegalArgumentException(
				"Casa " + idCasa + " não é alcançável neste lançamento.");
		}
		Casa destino = tabuleiro.getCasaPorId(idCasa);
		jogadores.get(indiceJogadorAtual).getPino().setCasa(destino);
		this.ultimoLancamento = null; // jogada consumida
	}

	/** Avança para o próximo jogador, em ordem circular. */
	public void proximoJogador() {
		indiceJogadorAtual = (indiceJogadorAtual + 1) % jogadores.size();
	}

	// ---------- Consultas usadas pelo Controller/View ----------

	public int getNumeroJogadores() {
		return jogadores.size();
	}

	public String getNomeJogadorAtual() {
		return jogadores.get(indiceJogadorAtual).getNome();
	}

	public String getCorJogadorAtual() {
		return jogadores.get(indiceJogadorAtual).getCor();
	}

	public int getIdCasaJogadorAtual() {
		return jogadores.get(indiceJogadorAtual).getPino().getCasa().getId();
	}

	/**
	 * Devolve, em forma de Strings, as cartas que o jogador
	 * atual tem na mão. Strings ao invés de Carta porque a
	 * classe Carta é package-private.
	 */
	public List<String> getCartasJogadorAtual() {
		List<String> nomes = new ArrayList<>();
		for (Carta c : jogadores.get(indiceJogadorAtual).getMao()) {
			nomes.add(c.toString());
		}
		return Collections.unmodifiableList(nomes);
	}

	/** Total de casas no tabuleiro (útil para a View desenhar). */
	public int getTotalCasas() {
		return tabuleiro.getLinhas() * tabuleiro.getColunas();
	}

	public int getLinhasTabuleiro()  { return tabuleiro.getLinhas(); }
	public int getColunasTabuleiro() { return tabuleiro.getColunas(); }

	/** Permite à View saber se uma casa é parede ao desenhar. */
	public boolean isCasaTransitavel(int idCasa) {
		Casa c = tabuleiro.getCasaPorId(idCasa);
		return c != null && c.isTransitavel();
	}

	// ---------- Para fins de teste ----------
	// Métodos package-private propositalmente: só os testes
	// dentro do pacote model conseguem inspecionar a solução.

	Envelope getEnvelopeInterno() {
		return envelope;
	}
}
