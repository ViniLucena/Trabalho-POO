package app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.Jogo;

/**
 * Aplicação demonstrativa. Está num pacote SEPARADO de propósito,
 * para provar que toda a interação acontece pela classe pública
 * Jogo. Se tentássemos importar Carta, Tabuleiro ou Envelope
 * aqui, o compilador recusaria — exatamente o que o trabalho
 * pede.
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("=== Clue — 1ª Iteração ===\n");

		Jogo jogo = new Jogo(Arrays.asList("Lucas", "Ana", "Pedro", "Marina"));

		System.out.println("Jogadores: " + jogo.getNumeroJogadores());
		System.out.println("Tabuleiro: " + jogo.getLinhasTabuleiro()
			+ "x" + jogo.getColunasTabuleiro()
			+ " (" + jogo.getTotalCasas() + " casas)\n");

		// Mostra a mão de cada jogador (só pra demonstração;
		// na partida real só o próprio jogador veria a sua).
		for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
			System.out.println("Jogador da vez: " + jogo.getNomeJogadorAtual()
				+ " (" + jogo.getCorJogadorAtual() + ")");
			System.out.println("  Casa atual: id=" + jogo.getIdCasaJogadorAtual());
			System.out.println("  Cartas na mão:");
			for (String c : jogo.getCartasJogadorAtual()) {
				System.out.println("    - " + c);
			}
			jogo.proximoJogador();
			System.out.println();
		}

		// Simula uma jogada do primeiro jogador (já voltou para
		// ele depois das N iterações acima).
		System.out.println("--- Simulando uma jogada de "
			+ jogo.getNomeJogadorAtual() + " ---");

		int[] dados = jogo.lancarDados();
		System.out.println("Dados: " + dados[0] + " + " + dados[1]
			+ " = " + (dados[0] + dados[1]));

		List<Integer> destinos = jogo.mapearCasas(dados);
		Collections.sort(destinos);
		System.out.println("Casas alcançáveis (ids): " + destinos);

		int escolhida = destinos.get(0);
		jogo.deslocarPiao(escolhida);
		System.out.println("Pião deslocado para a casa " + escolhida);
		System.out.println("Casa atual: id=" + jogo.getIdCasaJogadorAtual());
	}
}
