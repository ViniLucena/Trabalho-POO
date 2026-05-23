package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fachada pública do pacote Model. Concentra TODA a API que
 * será consumida pelos pacotes view e controller. As demais
 * classes do Model permanecem package-private — a View não
 * conhece Carta, Casa, Comodo, Tabuleiro, etc.
 *
 * Em termos de design, a classe segue o espírito do padrão
 * Facade do Capítulo 17 (uma única porta para o subsistema).
 * O padrão Singleton formal será aplicado em uma iteração
 * futura; aqui o construtor permanece público para facilitar
 * a inicialização explícita pela camada de aplicação.
 */
public class Jogo {

    /** Cores fixas do Clue, na ordem clássica de jogo. */
    public static final String[] CORES_PADRAO = {
        "Vermelho", "Amarelo", "Branco", "Verde", "Azul", "Violeta"
    };

    /** Personagens correspondentes às cores (mesma ordem). */
    public static final String[] PERSONAGENS_PADRAO = {
        "Srta. Scarlet",
        "Coronel Mustard",
        "Sra. White",
        "Reverendo Green",
        "Sra. Peacock",
        "Professor Plum"
    };

    private final Tabuleiro tabuleiro;
    private final Baralho baralho;
    private final Dados dados;
    private final List<Jogador> jogadores;
    private final Envelope envelope;

    private int indiceJogadorAtual;
    private int[] ultimoLancamento; // null antes de lançar; resetado ao deslocar
    private boolean usouPassagemSecretaNaVez;

    /**
     * Cria uma nova partida. Recebe o número de jogadores
     * (3 a 6). Cada jogador recebe a cor e o personagem na
     * mesma ordem dos arrays CORES_PADRAO / PERSONAGENS_PADRAO.
     */
    public Jogo(int numJogadores) {
        if (numJogadores < 3 || numJogadores > 6) {
            throw new IllegalArgumentException(
                "O Clue exige entre 3 e 6 jogadores.");
        }

        this.tabuleiro = new Tabuleiro();
        this.baralho   = new Baralho();
        this.dados     = new Dados();
        this.jogadores = new ArrayList<Jogador>();

        for (int i = 0; i < numJogadores; i++) {
            Jogador j = new Jogador(PERSONAGENS_PADRAO[i], CORES_PADRAO[i]);
            Casa inicio = tabuleiro.getPosicaoInicial(i);
            j.setPino(new Pino(CORES_PADRAO[i], inicio));
            jogadores.add(j);
        }

        this.envelope = baralho.sortearSolucao();
        baralho.distribuir(jogadores);

        // Srta. Scarlet (vermelho) começa por convenção do Clue.
        this.indiceJogadorAtual = 0;
        this.ultimoLancamento = null;
        this.usouPassagemSecretaNaVez = false;
    }

    // ============================================================
    // CONSULTAS DE TABULEIRO (para o desenho da View)
    // ============================================================

    public int getLinhasTabuleiro()  { return tabuleiro.getLinhas(); }
    public int getColunasTabuleiro() { return tabuleiro.getColunas(); }

    /** Tipo da casa nas coordenadas (linha, coluna). */
    public String getTipoCasa(int linha, int coluna) {
        Casa c = tabuleiro.getCasa(linha, coluna);
        return (c == null) ? "BLOQUEADA" : c.getTipo().name();
    }

    /** Id da casa nas coordenadas (linha, coluna), ou -1. */
    public int getIdCasa(int linha, int coluna) {
        Casa c = tabuleiro.getCasa(linha, coluna);
        return (c == null) ? -1 : c.getId();
    }

    /** Nome do cômodo da casa em (linha, coluna), ou null. */
    public String getNomeComodoEm(int linha, int coluna) {
        Casa c = tabuleiro.getCasa(linha, coluna);
        if (c == null || c.getComodo() == null) return null;
        return c.getComodo().getNome();
    }

    /** Lista os nomes dos cômodos (para a View desenhar labels). */
    public List<String> getNomesComodos() {
        List<String> nomes = new ArrayList<String>();
        for (Comodo c : tabuleiro.getComodos()) {
            nomes.add(c.getNome());
        }
        return Collections.unmodifiableList(nomes);
    }

    /**
     * Devolve a "caixa" (linhaInicio, colunaInicio, linhaFim,
     * colunaFim) ocupada por um cômodo. Útil para a View
     * desenhar o nome centralizado.
     */
    public int[] getBoundingBoxComodo(String nomeComodo) {
        Comodo c = tabuleiro.getComodoPorNome(nomeComodo);
        if (c == null) return null;
        int lMin = Integer.MAX_VALUE, cMin = Integer.MAX_VALUE;
        int lMax = Integer.MIN_VALUE, cMax = Integer.MIN_VALUE;
        for (Casa casa : c.getCasas()) {
            lMin = Math.min(lMin, casa.getLinha());
            cMin = Math.min(cMin, casa.getColuna());
            lMax = Math.max(lMax, casa.getLinha());
            cMax = Math.max(cMax, casa.getColuna());
        }
        return new int[] { lMin, cMin, lMax, cMax };
    }

    // ============================================================
    // CONSULTAS DOS JOGADORES
    // ============================================================

    public int getNumeroJogadores() {
        return jogadores.size();
    }

    public int getIndiceJogadorAtual() {
        return indiceJogadorAtual;
    }

    public String getNomeJogadorAtual() {
        return jogadores.get(indiceJogadorAtual).getNome();
    }

    public String getCorJogadorAtual() {
        return jogadores.get(indiceJogadorAtual).getCor();
    }

    /** Linha do pino de um jogador (por índice). */
    public int getLinhaPino(int indiceJogador) {
        return jogadores.get(indiceJogador).getPino().getCasa().getLinha();
    }

    /** Coluna do pino de um jogador (por índice). */
    public int getColunaPino(int indiceJogador) {
        return jogadores.get(indiceJogador).getPino().getCasa().getColuna();
    }

    /** Cor do pino (índice → cor). */
    public String getCorPino(int indiceJogador) {
        return jogadores.get(indiceJogador).getCor();
    }

    /** Nome do personagem de um jogador (índice → nome). */
    public String getNomePersonagem(int indiceJogador) {
        return jogadores.get(indiceJogador).getNome();
    }

    // ============================================================
    // CARTAS DA MÃO
    // ============================================================

    /**
     * Retorna info de TODAS as 21 cartas do jogo (do envelope +
     * mãos de todos os jogadores). Cada elemento é String[3]:
     * [0]=id, [1]=nome, [2]=tipo (PERSONAGEM/ARMA/LOCAL).
     * Usado pelo ResourceLoader para pré-carregar as imagens
     * de todas as cartas em O(1) por id.
     */
    public List<String[]> getTodasAsCartas() {
        List<String[]> lista = new ArrayList<String[]>();
        adicionarInfoCarta(lista, envelope.getPersonagem());
        adicionarInfoCarta(lista, envelope.getArma());
        adicionarInfoCarta(lista, envelope.getLocal());
        for (Jogador j : jogadores) {
            for (Carta c : j.getMao()) {
                adicionarInfoCarta(lista, c);
            }
        }
        return lista;
    }

    private void adicionarInfoCarta(List<String[]> lista, Carta c) {
        lista.add(new String[] {
            String.valueOf(c.getId()),
            c.getNome(),
            c.getTipo().name()
        });
    }

    /**
     * Cartas que o jogador atual tem na mão, na forma de pares
     * (id, nome) — formato amigável para a View construir um
     * HashMap id -> imagem.
     */
    public List<int[]> getIdsCartasJogadorAtual() {
        List<int[]> ids = new ArrayList<int[]>();
        for (Carta c : jogadores.get(indiceJogadorAtual).getMao()) {
            ids.add(new int[] { c.getId(), c.getTipo().ordinal() });
        }
        return ids;
    }

    public List<String> getNomesCartasJogadorAtual() {
        List<String> nomes = new ArrayList<String>();
        for (Carta c : jogadores.get(indiceJogadorAtual).getMao()) {
            nomes.add(c.getNome());
        }
        return nomes;
    }

    // ============================================================
    // DADOS
    // ============================================================

    /** Lança os dois dados (ou usa valores definidos via setDadosManuais). */
    public int[] lancarDados() {
        this.ultimoLancamento = dados.lancar();
        return Arrays.copyOf(ultimoLancamento, ultimoLancamento.length);
    }

    /**
     * Define manualmente os valores dos próximos dados (1..6).
     * Conforme sugestão do enunciado da 2ª iteração, isto permite
     * testar situações específicas sem depender de randomização.
     */
    public void setDadosManuais(int d1, int d2) {
        dados.definirProximoLancamento(d1, d2);
    }

    /** Último valor lançado, ou null se ainda não lançou (ou já moveu). */
    public int[] getUltimoLancamento() {
        return (ultimoLancamento == null) ? null
                                          : Arrays.copyOf(ultimoLancamento, 2);
    }

    public boolean jaLancouNestaVez() {
        return ultimoLancamento != null;
    }

    // ============================================================
    // MOVIMENTO
    // ============================================================

    /**
     * Lista os ids das Casas alcançáveis pelo jogador da vez,
     * dado o último lançamento. Inclui ids de cômodos: a View
     * apenas verifica se a casa clicada pertence a esse conjunto.
     */
    public Set<Integer> getCasasAlcancaveis() {
        if (ultimoLancamento == null) {
            return new HashSet<Integer>();
        }
        Casa origem = jogadores.get(indiceJogadorAtual).getPino().getCasa();
        int passos = ultimoLancamento[0] + ultimoLancamento[1];
        return tabuleiro.mapearAlcancaveis(origem, passos);
    }

    /**
     * Tenta deslocar o pino do jogador atual para a casa clicada
     * (informada por linha/coluna). Retorna true se o
     * deslocamento foi executado, false caso contrário.
     *
     * Conforme exigência do enunciado: nada deve ser feito e
     * nenhuma mensagem deve aparecer quando o destino não é
     * alcançável — o controlador apenas chama este método e
     * ignora o retorno false (nem troca o jogador da vez).
     */
    public boolean tentarMover(int linha, int coluna) {
        if (ultimoLancamento == null && !usouPassagemSecretaNaVez) {
            return false;
        }
        Casa destino = tabuleiro.getCasa(linha, coluna);
        if (destino == null || destino.getTipo() == TipoCasa.BLOQUEADA) {
            return false;
        }
        if (ultimoLancamento == null) return false;

        Set<Integer> validas = getCasasAlcancaveis();

        int idAlvo;
        if (destino.isComodo()) {
            // clique dentro do cômodo equivale a entrar pela porta
            idAlvo = destino.getComodo().getCasaPrincipal().getId();
        } else {
            idAlvo = destino.getId();
        }
        if (!validas.contains(idAlvo)) {
            return false;
        }

        Casa casaFinal = tabuleiro.getCasaPorId(idAlvo);
        jogadores.get(indiceJogadorAtual).getPino().setCasa(casaFinal);
        this.ultimoLancamento = null;
        return true;
    }

    // ============================================================
    // PASSAGEM SECRETA
    // ============================================================

    /** True se o jogador da vez está num cômodo com passagem secreta. */
    public boolean podeUsarPassagemSecreta() {
        if (usouPassagemSecretaNaVez) return false;
        Casa c = jogadores.get(indiceJogadorAtual).getPino().getCasa();
        return c.isComodo() && c.getComodo().temPassagemSecreta();
    }

    /**
     * Move o pino do jogador atual pelo cômodo conectado por
     * passagem secreta, se houver. Retorna true em caso de
     * sucesso. A passagem secreta substitui o uso dos dados na
     * vez — depois dela basta passar a vez.
     */
    public boolean usarPassagemSecreta() {
        if (!podeUsarPassagemSecreta()) return false;
        Casa c = jogadores.get(indiceJogadorAtual).getPino().getCasa();
        Comodo destino = c.getComodo().getConexaoSecreta();
        jogadores.get(indiceJogadorAtual).getPino().setCasa(
            destino.getCasaPrincipal());
        this.usouPassagemSecretaNaVez = true;
        this.ultimoLancamento = null;
        return true;
    }

    // ============================================================
    // TURNO
    // ============================================================

    /** Passa a vez para o próximo jogador (ordem circular). */
    public void proximoJogador() {
        indiceJogadorAtual = (indiceJogadorAtual + 1) % jogadores.size();
        ultimoLancamento = null;
        usouPassagemSecretaNaVez = false;
    }

    // ============================================================
    // ACESSO INTERNO (apenas testes / fachada)
    // ============================================================

    Envelope getEnvelopeInterno() {
        return envelope;
    }
}
