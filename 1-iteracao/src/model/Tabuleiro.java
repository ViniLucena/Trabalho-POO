package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Tabuleiro do jogo, modelado como um grid retangular de Casas.
 *
 * Para esta 1ª iteração, o tabuleiro é simplificado: um grid
 * 10x10 com algumas casas bloqueadas que simulam paredes. O
 * tabuleiro real do Clue (com salas, portas e passagens
 * secretas) será implementado em iterações futuras — o desenho
 * atual já está preparado para esse crescimento, pois Casa
 * pode ganhar novos atributos sem quebrar a API pública (princípio
 * aberto/fechado).
 *
 * Uso de HashMap: o trabalho pede HashMap "sempre que possível".
 * Aqui ele é usado como índice id -> Casa, permitindo busca
 * por identificador em O(1), que é exatamente o que o Controller
 * vai precisar quando o jogador clicar numa casa.
 */
class Tabuleiro {

	private static final int LINHAS = 10;
	private static final int COLUNAS = 10;

	private final Casa[][] grid;
	private final Map<Integer, Casa> casasPorId;

	Tabuleiro() {
		this.grid = new Casa[LINHAS][COLUNAS];
		this.casasPorId = new HashMap<>();
		construirGrid();
		marcarParedes();
	}

	private void construirGrid() {
		int id = 0;
		for (int l = 0; l < LINHAS; l++) {
			for (int c = 0; c < COLUNAS; c++) {
				Casa casa = new Casa(id, l, c, true);
				grid[l][c] = casa;
				casasPorId.put(id, casa);
				id++;
			}
		}
	}

	/**
	 * Marca algumas casas como não-transitáveis para representar
	 * obstáculos. Os ids são recriados (mesma posição, novo
	 * "transitavel") para manter consistência no HashMap.
	 */
	private void marcarParedes() {
		int[][] paredes = {
			{2, 3}, {2, 4}, {2, 5},
			{5, 1}, {5, 2},
			{7, 6}, {7, 7}, {7, 8}
		};
		for (int[] p : paredes) {
			Casa antiga = grid[p[0]][p[1]];
			Casa nova = new Casa(antiga.getId(), p[0], p[1], false);
			grid[p[0]][p[1]] = nova;
			casasPorId.put(nova.getId(), nova);
		}
	}

	Casa getCasaPorId(int id) {
		return casasPorId.get(id);
	}

	/**
	 * Posição inicial de cada jogador. No Clue real cada
	 * personagem tem uma casa de partida fixa; aqui usamos os
	 * cantos do tabuleiro só para simplificar.
	 */
	Casa getPosicaoInicial(int indiceJogador) {
		switch (indiceJogador) {
			case 0: return grid[0][0];
			case 1: return grid[0][COLUNAS - 1];
			case 2: return grid[LINHAS - 1][0];
			case 3: return grid[LINHAS - 1][COLUNAS - 1];
			case 4: return grid[0][COLUNAS / 2];
			case 5: return grid[LINHAS - 1][COLUNAS / 2];
			default: return grid[0][0];
		}
	}

	/**
	 * Mapeia todas as casas alcançáveis a partir de uma origem
	 * usando até "passos" movimentos ortogonais (cima, baixo,
	 * esquerda, direita), ignorando casas não-transitáveis.
	 *
	 * Implementação por BFS: garante que cada casa seja
	 * visitada com o menor número de passos, e o BFS para de
	 * expandir quando a profundidade ultrapassa o limite. A
	 * casa de origem NÃO é incluída no resultado — o jogador
	 * tem de se mover.
	 *
	 * Limitação conhecida: o Clue oficial exige movimento de
	 * exatamente N casas sem revisitar; aqui consideramos "até
	 * N passos pelo caminho mais curto". É uma simplificação
	 * intencional desta iteração e está documentada no
	 * relatório.
	 */
	List<Integer> mapearCasasAlcancaveis(Casa origem, int passos) {
		Set<Integer> alcancaveis = new HashSet<>();
		Map<Integer, Integer> distancia = new HashMap<>();
		Queue<Casa> fila = new LinkedList<>();

		fila.offer(origem);
		distancia.put(origem.getId(), 0);

		while (!fila.isEmpty()) {
			Casa atual = fila.poll();
			int d = distancia.get(atual.getId());

			if (d == passos && d > 0) {
				alcancaveis.add(atual.getId());
			}

			for (Casa viz : vizinhas(atual)) {
				if (!viz.isTransitavel()) continue;
				if (distancia.containsKey(viz.getId())) continue;
				distancia.put(viz.getId(), d + 1);
				fila.offer(viz);
			}
		}
		return new ArrayList<>(alcancaveis);
	}

	private List<Casa> vizinhas(Casa c) {
		List<Casa> v = new ArrayList<>();
		int l = c.getLinha();
		int col = c.getColuna();
		if (l > 0)             v.add(grid[l - 1][col]);
		if (l < LINHAS - 1)    v.add(grid[l + 1][col]);
		if (col > 0)            v.add(grid[l][col - 1]);
		if (col < COLUNAS - 1)  v.add(grid[l][col + 1]);
		return v;
	}

	int getLinhas()  { return LINHAS; }
	int getColunas() { return COLUNAS; }
}
