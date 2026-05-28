package model;

import java.util.*;

/**
 * Façade pública do jogo Clue. Única classe pública do pacote model.
 *
 * Nota: o professor pedirá Singleton+Façade apenas na 3ª iteração.
 * Nesta 1ª iteração, Jogo é apenas uma Façade com construtor público normal.
 */
public class Jogo {

    // variaveis
    private static final List<String> CORES = Arrays.asList(
            "Vermelho", "Amarelo", "Branco", "Verde", "Violeta", "Azul"
    );

    private final Tabuleiro tabuleiro;
    private final Baralho baralho;
    private final Dados dados;
    private final List<Jogador> jogadores;
    private final Envelope envelope;
    private int indiceJogadorAtual;
    private int[] ultimoLancamento;

    // métodos

    /**
     * Cria um novo jogo com os nomes dos jogadores fornecidos (3 a 6).
     */
    public Jogo(List<String> nomesJogadores) {
        if (nomesJogadores.size() < 3 || nomesJogadores.size() > 6) {
            throw new IllegalArgumentException(
                    "Número de jogadores deve ser entre 3 e 6, recebeu: " + nomesJogadores.size());
        }

        tabuleiro = new Tabuleiro();
        baralho = new Baralho();
        dados = new Dados();

        jogadores = new ArrayList<Jogador>();
        for (int i = 0; i < nomesJogadores.size(); i++) {
            Jogador j = new Jogador(nomesJogadores.get(i), CORES.get(i));
            j.getPino().setCasaAtual(tabuleiro.getPosicaoInicial(i));
            jogadores.add(j);
        }

        envelope = baralho.sortearSolucao();
        baralho.distribuir(jogadores);

        indiceJogadorAtual = 0;
        ultimoLancamento = null;
    }

    /** Lança os dados e armazena o resultado para a jogada atual. */
    public int[] lancarDados() {
        ultimoLancamento = dados.lancar();
        return Arrays.copyOf(ultimoLancamento, ultimoLancamento.length);
    }

    /** Define manualmente os valores dos dados (útil para testes). */
    public void definirDados(int d1, int d2) {
        ultimoLancamento = new int[]{d1, d2};
    }

    /** Retorna os ids das casas alcançáveis com o lançamento atual. */
    public List<Integer> mapearCasas(int[] valoresDados) {
        int passos = valoresDados[0] + valoresDados[1];
        Casa origem = jogadores.get(indiceJogadorAtual).getPino().getCasaAtual();
        return tabuleiro.mapearCasasAlcancaveis(origem, passos);
    }

    /**
     * Move o pino do jogador atual para a casa indicada.
     * Lança IllegalStateException se os dados não foram lançados.
     * Lança IllegalArgumentException se a casa não é alcançável.
     */
    public void deslocarPiao(int idCasa) {
        if (ultimoLancamento == null) {
            throw new IllegalStateException("Os dados precisam ser lançados antes de mover o pino.");
        }
        List<Integer> alcancaveis = mapearCasas(ultimoLancamento);
        if (!alcancaveis.contains(idCasa)) {
            throw new IllegalArgumentException("Casa " + idCasa + " não está na lista de alcançáveis.");
        }
        Casa destino = tabuleiro.getCasaPorId(idCasa);
        jogadores.get(indiceJogadorAtual).getPino().setCasaAtual(destino);
        ultimoLancamento = null;
    }

    /** Avança para o próximo jogador (sentido anti-horário = próximo na lista). */
    public void proximoJogador() {
        indiceJogadorAtual = (indiceJogadorAtual + 1) % jogadores.size();
        ultimoLancamento = null;
    }

    /** Retorna o número de jogadores. */
    public int getNumeroJogadores() {
        return jogadores.size();
    }

    /** Retorna o nome do jogador atual. */
    public String getNomeJogadorAtual() {
        return jogadores.get(indiceJogadorAtual).getNome();
    }

    /** Retorna a cor do jogador atual. */
    public String getCorJogadorAtual() {
        return jogadores.get(indiceJogadorAtual).getCor();
    }

    /** Retorna o id da casa onde o pino do jogador atual está. */
    public int getIdCasaJogadorAtual() {
        return jogadores.get(indiceJogadorAtual).getPino().getCasaAtual().getId();
    }

    /** Retorna true se o jogador atual está dentro de algum cômodo. */
    public boolean jogadorEstaEmComodo() {
        return jogadores.get(indiceJogadorAtual).getPino().getCasaAtual().getNomeComodo() != null;
    }

    /** Retorna o nome do cômodo onde o jogador atual está, ou null se estiver no corredor. */
    public String getNomeComodoAtual() {
        return jogadores.get(indiceJogadorAtual).getPino().getCasaAtual().getNomeComodo();
    }

    /** Retorna as cartas do jogador atual como Strings (nunca expõe objetos Carta). */
    public List<String> getCartasJogadorAtual() {
        List<String> resultado = new ArrayList<String>();
        for (Carta c : jogadores.get(indiceJogadorAtual).getMao()) {
            resultado.add(c.getNome() + " (" + c.getTipo() + ")");
        }
        return resultado;
    }

    /** Retorna a folha de notas do jogador atual. */
    public List<String> getFolhaDeNotasJogadorAtual() {
        return jogadores.get(indiceJogadorAtual).getFolhaDeNotas();
    }

    /** Remove um item da folha de notas do jogador atual. */
    public void riscarDaFolha(String item) {
        jogadores.get(indiceJogadorAtual).riscarDaFolha(item);
    }

    /** Retorna true se a casa com o id fornecido é transitável. */
    public boolean isCasaTransitavel(int id) {
        Casa casa = tabuleiro.getCasaPorId(id);
        if (casa == null) return false;
        return casa.isTransitavel();
    }

    /** Retorna o número de linhas do tabuleiro. */
    public int getLinhasTabuleiro() {
        return tabuleiro.getLinhas();
    }

    /** Retorna o número de colunas do tabuleiro. */
    public int getColunasTabuleiro() {
        return tabuleiro.getColunas();
    }

    /** Retorna true se o jogador atual está em um cômodo com passagem secreta. */
    public boolean temPassagemSecreta() {
        String nomeComodo = getNomeComodoAtual();
        if (nomeComodo == null) return false;
        Comodo comodo = tabuleiro.getComodoPorNome(nomeComodo);
        return comodo != null && comodo.getPassagemSecreta() != null;
    }

    /**
     * Teleporta o jogador pela passagem secreta para o cômodo de destino.
     * Lança IllegalStateException se não houver passagem disponível.
     */
    public void usarPassagemSecreta() {
        if (!temPassagemSecreta()) {
            throw new IllegalStateException("O jogador atual não está em cômodo com passagem secreta.");
        }
        String nomeComodo = getNomeComodoAtual();
        Comodo comodoAtual = tabuleiro.getComodoPorNome(nomeComodo);
        String nomeDestino = comodoAtual.getPassagemSecreta();
        Comodo comodoDestino = tabuleiro.getComodoPorNome(nomeDestino);
        Casa casaDestino = tabuleiro.getCasaPorId(comodoDestino.getIdCasaRepresentativa());
        jogadores.get(indiceJogadorAtual).getPino().setCasaAtual(casaDestino);
    }

    /**
     * Verifica se a acusação final corresponde ao envelope.
     */
    public boolean verificarAcusacao(String personagem, String arma, String local) {
        return envelope.verificarAcusacao(personagem, arma, local);
    }
}
