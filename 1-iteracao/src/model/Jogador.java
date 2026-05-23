package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

/**
 * Um jogador do Clue. Tem nome, cor (que identifica seu pino),
 * uma mão de cartas e uma referência ao seu pino no tabuleiro.
 */
class Jogador {

	private final String nome;
	private final String cor;
	private final List<String> folhaDeNotas = new ArrayList<>();
	private final List<Carta> mao;
	private Pino pino;
		
	public Jogador(String nome, String cor) {
	    this.nome = nome;
	    this.cor = cor;
	    this.mao = new ArrayList<>();

	    // Inicializa a folha com todos os suspeitos, armas e cômodos (nomes exatos do manual)
	    this.folhaDeNotas.addAll(Arrays.asList(
	        // Suspeitos
	        "Coronel Mustard", "Srta. Scarlet", "Professor Plum", "Reverend Green", "Sra. White", "Sra. Peacock",
	        // Armas
	        "Corda", "Cano de Chumbo", "Faca", "Chave Inglesa", "Castiçal", "Revólver",
	        // Cômodos
	        "Sala de Estar", "Cozinha", "Biblioteca", "Escritório", "Salão de Baile",
	        "Sala de Jantar", "Varanda", "Salão", "Estudo"
	    ));
	}

	String getNome() {
		return nome;
	}

	String getCor() {
		return cor;
	}

	void receberCarta(Carta c) {
		mao.add(c);
	}

	/** Lista imutável para impedir alteração externa da mão. */
	List<Carta> getMao() {
		return Collections.unmodifiableList(mao);
	}

	Pino getPino() {
		return pino;
	}

	void setPino(Pino pino) {
		this.pino = pino;
	}
	public void riscarDaFolha(String item) {
	    folhaDeNotas.remove(item);
	}
	public List<String> getFolhaDeNotas() {
	    return Collections.unmodifiableList(folhaDeNotas);
	}
}
