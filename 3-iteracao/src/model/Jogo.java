package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * Padrões de projeto aplicados (Capítulo 17):
 *
 *   - Singleton: o construtor é privado; a partida é obtida
 *     por meio de {@link #getInstancia()}, {@link #novaPartida(int)}
 *     ou {@link #carregarPartida(File)}.
 *   - Façade   : a classe é a única "porta" do subsistema
 *     Model — toda a View dialoga com ela.
 *   - Observer : implementa {@link Observado}. Os painéis da
 *     View se registram via {@link #registrarObservador(Observador)}
 *     e são avisados a cada mudança de estado.
 */
public class Jogo implements Observado {

    /** Cores fixas do Clue, na ordem clássica. */
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

    public static final String[] ARMAS_PADRAO = {
        "Castiçal", "Revólver", "Faca", "Cano", "Corda", "Chave Inglesa"
    };

    public static final String[] COMODOS_PADRAO = {
        "Cozinha", "Sala de Música", "Jardim de Inverno",
        "Sala de Jantar", "Salão de Jogos", "Biblioteca",
        "Sala de Estar", "Entrada", "Escritório"
    };

    // ============================================================
    // Singleton
    // ============================================================

    private static Jogo instancia;

    /** Devolve a partida em andamento. Pode ser null. */
    public static Jogo getInstancia() {
        return instancia;
    }

    /** Inicia uma partida com os primeiros N personagens da lista
     *  padrão. */
    public static Jogo novaPartida(int numJogadores) {
        boolean[] selecionados = new boolean[CORES_PADRAO.length];
        for (int i = 0; i < numJogadores; i++) selecionados[i] = true;
        return novaPartida(selecionados);
    }

    /** Inicia uma partida com os personagens marcados em
     *  "selecionados" (na ordem de PERSONAGENS_PADRAO). É preciso
     *  selecionar entre 3 e 6 personagens. */
    public static Jogo novaPartida(boolean[] selecionados) {
        instancia = new Jogo(selecionados);
        return instancia;
    }

    /** Restaura uma partida salva em arquivo. */
    public static Jogo carregarPartida(File arquivo) throws IOException {
        Jogo j = new Jogo(arquivo);
        instancia = j;
        return j;
    }

    // ============================================================
    // Estado da partida
    // ============================================================

    private final Tabuleiro tabuleiro;
    private final Baralho baralho;
    private final Dados dados;
    private final List<Jogador> jogadores;
    private final Envelope envelope;
    /** 6 pinos sempre — mesmo personagens sem jogador podem ser
     *  movidos por palpites (regra do manual). */
    private final Pino[] pinos = new Pino[CORES_PADRAO.length];

    private int indiceJogadorAtual;
    private int[] ultimoLancamento; // null antes de lançar
    private boolean usouPassagemSecretaNaVez;
    private boolean jaMoveouNestaVez;
    /** Comodo em que o jogador atual já fez palpite nesta vez (impede
     *  novos palpites neste mesmo cômodo até sair e voltar). */
    private boolean jaPalpitouNestaVez;
    private boolean partidaTerminada;
    private int indiceVencedor = -1;
    /** Resultado do último palpite, para que a View consulte. */
    private int ultimoRefutadorIndice = -1;
    private int ultimoCartaMostradaId = -1;

    private final List<Observador> observadores = new ArrayList<Observador>();

    // ============================================================
    // Construtores (privados)
    // ============================================================

    private Jogo(boolean[] selecionados) {
        validarSelecao(selecionados);

        this.tabuleiro = new Tabuleiro();
        this.baralho   = new Baralho();
        this.dados     = new Dados();
        this.jogadores = new ArrayList<Jogador>();

        // Cria os 6 pinos, um por cor. Os personagens sem jogador
        // ficam nas posições iniciais e podem ser deslocados por
        // palpites.
        for (int i = 0; i < CORES_PADRAO.length; i++) {
            pinos[i] = new Pino(CORES_PADRAO[i], tabuleiro.getPosicaoInicial(i));
        }

        // Cria um Jogador por personagem selecionado.
        for (int i = 0; i < CORES_PADRAO.length; i++) {
            if (!selecionados[i]) continue;
            Jogador j = new Jogador(PERSONAGENS_PADRAO[i], CORES_PADRAO[i]);
            j.setPino(pinos[i]);
            jogadores.add(j);
        }

        this.envelope = baralho.sortearSolucao();
        baralho.distribuir(jogadores);

        this.indiceJogadorAtual       = 0;
        this.ultimoLancamento         = null;
        this.usouPassagemSecretaNaVez = false;
        this.jaMoveouNestaVez         = false;
        this.jaPalpitouNestaVez       = false;
    }

    private Jogo(File arquivo) throws IOException {
        // Carrega a partida do arquivo. Reconstroi o tabuleiro
        // e o baralho do zero e depois sobrescreve o estado.
        this.tabuleiro = new Tabuleiro();
        this.baralho   = new Baralho();
        this.dados     = new Dados();
        this.jogadores = new ArrayList<Jogador>();
        for (int i = 0; i < CORES_PADRAO.length; i++) {
            pinos[i] = new Pino(CORES_PADRAO[i], tabuleiro.getPosicaoInicial(i));
        }
        this.envelope = carregarDeArquivo(arquivo);
    }

    private void validarSelecao(boolean[] selecionados) {
        if (selecionados == null
                || selecionados.length != CORES_PADRAO.length) {
            throw new IllegalArgumentException(
                "Seleção de personagens inválida.");
        }
        int n = 0;
        for (boolean b : selecionados) if (b) n++;
        if (n < 3 || n > 6) {
            throw new IllegalArgumentException(
                "É preciso escolher de 3 a 6 personagens (atual: " + n + ").");
        }
    }

    // ============================================================
    // Observer
    // ============================================================

    @Override
    public void registrarObservador(Observador o) {
        if (o != null && !observadores.contains(o)) observadores.add(o);
    }

    @Override
    public void removerObservador(Observador o) {
        observadores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        for (Observador o : observadores) o.notificar();
    }

    // ============================================================
    // CONSULTAS DE TABULEIRO (para o desenho da View)
    // ============================================================

    public int getLinhasTabuleiro()  { return tabuleiro.getLinhas(); }
    public int getColunasTabuleiro() { return tabuleiro.getColunas(); }

    public String getTipoCasa(int linha, int coluna) {
        Casa c = tabuleiro.getCasa(linha, coluna);
        return (c == null) ? "BLOQUEADA" : c.getTipo().name();
    }

    public String getNomeComodoEm(int linha, int coluna) {
        Casa c = tabuleiro.getCasa(linha, coluna);
        if (c == null || c.getComodo() == null) return null;
        return c.getComodo().getNome();
    }

    public List<String> getNomesComodos() {
        List<String> nomes = new ArrayList<String>();
        for (Comodo c : tabuleiro.getComodos()) nomes.add(c.getNome());
        return Collections.unmodifiableList(nomes);
    }

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

    /** Conjunto de ids de Casa destacados (alcançáveis) para a
     *  View pintar uma sobreposição. */
    public Set<Integer> getCasasAlcancaveis() {
        if (ultimoLancamento == null) return new HashSet<Integer>();
        Casa origem = pinoAtual().getCasa();
        int passos = ultimoLancamento[0] + ultimoLancamento[1];
        return tabuleiro.mapearAlcancaveis(origem, passos);
    }

    /** Linha / coluna de uma casa pelo id (usado pela View para
     *  pintar o destaque). */
    public int[] getCoordenadasPorId(int idCasa) {
        Casa c = tabuleiro.getCasaPorId(idCasa);
        return (c == null) ? null : new int[] { c.getLinha(), c.getColuna() };
    }

    // ============================================================
    // CONSULTAS DOS JOGADORES E PINOS
    // ============================================================

    public int getNumeroJogadores()    { return jogadores.size(); }
    public int getIndiceJogadorAtual() { return indiceJogadorAtual; }
    public String getNomeJogadorAtual() { return jogadores.get(indiceJogadorAtual).getNome(); }
    public String getCorJogadorAtual()  { return jogadores.get(indiceJogadorAtual).getCor(); }

    public String getNomePersonagem(int indiceJogador) {
        return jogadores.get(indiceJogador).getNome();
    }
    public String getCorPino(int indiceJogador) {
        return jogadores.get(indiceJogador).getCor();
    }
    public boolean isJogadorEliminado(int indiceJogador) {
        return jogadores.get(indiceJogador).isEliminado();
    }

    /** Total de pinos = 6 (cada personagem clássico). */
    public int getNumeroPinos() { return pinos.length; }

    public int getLinhaPinoPersonagem(int indicePersonagem) {
        return pinos[indicePersonagem].getCasa().getLinha();
    }
    public int getColunaPinoPersonagem(int indicePersonagem) {
        return pinos[indicePersonagem].getCasa().getColuna();
    }
    public String getCorPinoPersonagem(int indicePersonagem) {
        return CORES_PADRAO[indicePersonagem];
    }
    public boolean isPersonagemControladoPorJogador(int indicePersonagem) {
        for (Jogador j : jogadores) {
            if (j.getCor().equals(CORES_PADRAO[indicePersonagem])) return true;
        }
        return false;
    }

    /** Compat. com SmokeTest da 2ª iteração. */
    public int getLinhaPino(int indiceJogador) {
        return jogadores.get(indiceJogador).getPino().getCasa().getLinha();
    }
    public int getColunaPino(int indiceJogador) {
        return jogadores.get(indiceJogador).getPino().getCasa().getColuna();
    }

    // ============================================================
    // CARTAS
    // ============================================================

    /** Info de todas as 21 cartas (id, nome, tipo). Usado pela
     *  View para pré-carregar imagens. */
    public List<String[]> getTodasAsCartas() {
        List<String[]> lista = new ArrayList<String[]>();
        adicionarInfoCarta(lista, envelope.getPersonagem());
        adicionarInfoCarta(lista, envelope.getArma());
        adicionarInfoCarta(lista, envelope.getLocal());
        for (Jogador j : jogadores) {
            for (Carta c : j.getMao()) adicionarInfoCarta(lista, c);
        }
        return lista;
    }
    private void adicionarInfoCarta(List<String[]> lista, Carta c) {
        lista.add(new String[] {
            String.valueOf(c.getId()), c.getNome(), c.getTipo().name() });
    }

    /** Cartas (id, tipo.ordinal) na mão do jogador da vez. */
    public List<int[]> getIdsCartasJogadorAtual() {
        List<int[]> ids = new ArrayList<int[]>();
        for (Carta c : jogadores.get(indiceJogadorAtual).getMao()) {
            ids.add(new int[] { c.getId(), c.getTipo().ordinal() });
        }
        return ids;
    }

    /** Ids das cartas de cada tipo, em ordem fixa. Os ids são
     *  atribuídos pelo Baralho na ordem PERSONAGEM (0..5),
     *  ARMA (6..11) e LOCAL (12..20). */
    public List<int[]> getIdsCartasDoTipo(String tipo) {
        Carta.Tipo t = Carta.Tipo.valueOf(tipo);
        int inicio, fim;
        if (t == Carta.Tipo.PERSONAGEM) { inicio = 0;  fim = 5; }
        else if (t == Carta.Tipo.ARMA)  { inicio = 6;  fim = 11; }
        else                            { inicio = 12; fim = 20; }
        List<int[]> r = new ArrayList<int[]>();
        for (int i = inicio; i <= fim; i++) r.add(new int[] { i });
        return r;
    }

    public String getNomeCarta(int idCarta) {
        Carta c = buscarCarta(idCarta);
        return (c == null) ? null : c.getNome();
    }
    public String getTipoCarta(int idCarta) {
        Carta c = buscarCarta(idCarta);
        return (c == null) ? null : c.getTipo().name();
    }

    // ============================================================
    // BLOCO DE NOTAS
    // ============================================================

    public boolean isCartaMarcada(int idCarta) {
        return jogadores.get(indiceJogadorAtual)
                        .getBlocoNotas().estaMarcada(idCarta);
    }
    public void marcarCarta(int idCarta, boolean marcada) {
        BlocoNotas bn = jogadores.get(indiceJogadorAtual).getBlocoNotas();
        if (marcada) bn.marcar(idCarta);
        else         bn.desmarcar(idCarta);
        notificarObservadores();
    }

    // ============================================================
    // DADOS
    // ============================================================

    public boolean podeLancarDados() {
        if (partidaTerminada) return false;
        if (jogadores.get(indiceJogadorAtual).isEliminado()) return false;
        return !jaMoveouNestaVez && ultimoLancamento == null
            && !usouPassagemSecretaNaVez;
    }

    public boolean podePassarVez() {
        if (partidaTerminada) return false;
        if (jogadores.get(indiceJogadorAtual).isEliminado()) return true;
        if (jaMoveouNestaVez || usouPassagemSecretaNaVez) return true;
        // Dados lançados mas sem destinos válidos.
        return ultimoLancamento != null && getCasasAlcancaveis().isEmpty();
    }

    public int[] lancarDados() {
        ultimoLancamento = dados.lancar();
        notificarObservadores();
        return Arrays.copyOf(ultimoLancamento, 2);
    }

    public void setDadosManuais(int d1, int d2) {
        dados.definirProximoLancamento(d1, d2);
    }

    public int[] getUltimoLancamento() {
        return (ultimoLancamento == null) ? null
                                          : Arrays.copyOf(ultimoLancamento, 2);
    }

    public boolean jaLancouNestaVez() { return ultimoLancamento != null; }

    // ============================================================
    // MOVIMENTO
    // ============================================================

    /** Tenta deslocar o pino do jogador atual para (linha, coluna).
     *  Conforme exigência do enunciado, se o destino não for válido
     *  retorna false sem qualquer aviso e sem trocar o jogador da vez. */
    public boolean tentarMover(int linha, int coluna) {
        if (partidaTerminada) return false;
        if (jogadores.get(indiceJogadorAtual).isEliminado()) return false;
        if (ultimoLancamento == null) return false;

        Casa destino = tabuleiro.getCasa(linha, coluna);
        if (destino == null || destino.getTipo() == TipoCasa.BLOQUEADA) {
            return false;
        }
        Set<Integer> validas = getCasasAlcancaveis();

        int idAlvo;
        if (destino.isComodo() || destino.isPorta()) {
            idAlvo = destino.getComodo().getCasaPrincipal().getId();
        } else {
            idAlvo = destino.getId();
        }
        if (!validas.contains(Integer.valueOf(idAlvo))) return false;

        Casa casaFinal = tabuleiro.getCasaPorId(idAlvo);
        pinoAtual().setCasa(casaFinal);
        ultimoLancamento = null;
        jaMoveouNestaVez = true;
        jaPalpitouNestaVez = false; // novo cômodo permite novo palpite
        notificarObservadores();
        return true;
    }

    // ============================================================
    // PASSAGEM SECRETA
    // ============================================================

    public boolean podeUsarPassagemSecreta() {
        if (partidaTerminada) return false;
        if (jogadores.get(indiceJogadorAtual).isEliminado()) return false;
        if (usouPassagemSecretaNaVez || jaMoveouNestaVez
                || ultimoLancamento != null) return false;
        Casa c = pinoAtual().getCasa();
        return c.isComodo() && c.getComodo().temPassagemSecreta();
    }

    public boolean usarPassagemSecreta() {
        if (!podeUsarPassagemSecreta()) return false;
        Casa c = pinoAtual().getCasa();
        Comodo destino = c.getComodo().getConexaoSecreta();
        pinoAtual().setCasa(destino.getCasaPrincipal());
        usouPassagemSecretaNaVez = true;
        ultimoLancamento = null;
        jaMoveouNestaVez = true;
        jaPalpitouNestaVez = false;
        notificarObservadores();
        return true;
    }

    // ============================================================
    // PALPITE
    // ============================================================

    /** True se o jogador da vez pode dar palpite (está num cômodo
     *  e ainda não palpitou nesta vez). */
    public boolean podeFazerPalpite() {
        if (partidaTerminada) return false;
        if (jogadores.get(indiceJogadorAtual).isEliminado()) return false;
        if (jaPalpitouNestaVez) return false;
        return pinoAtual().getCasa().isComodo();
    }

    /** Nome do cômodo em que o jogador atual está, ou null. */
    public String getComodoDoJogadorAtual() {
        Casa c = pinoAtual().getCasa();
        return c.isComodo() ? c.getComodo().getNome() : null;
    }

    /**
     * Faz um palpite com (idPersonagem, idArma) — o cômodo é o
     * mesmo em que o jogador está. Move o pino do suspeito até o
     * cômodo (regra: o suspeito é "chamado" à cena do crime) e
     * busca o primeiro adversário (à esquerda) que tenha uma das
     * três cartas. O índice do refutador e o id da carta mostrada
     * ficam disponíveis em {@link #getUltimoRefutadorIndice()} e
     * {@link #getUltimoCartaMostradaId()}.
     *
     * Retorna true se conseguiu registrar o palpite.
     */
    public boolean fazerPalpite(int idPersonagem, int idArma) {
        if (!podeFazerPalpite()) return false;
        String comodoNome = getComodoDoJogadorAtual();
        Carta personagem = buscarCarta(idPersonagem);
        Carta arma       = buscarCarta(idArma);
        Carta local      = buscarCartaPorNome(comodoNome, Carta.Tipo.LOCAL);
        if (personagem == null || arma == null || local == null) return false;

        // Move o pino do suspeito (independente de ter jogador) para
        // o cômodo atual.
        int idxPersonagem = indicePorNomePersonagem(personagem.getNome());
        if (idxPersonagem >= 0) {
            pinos[idxPersonagem].setCasa(pinoAtual().getCasa());
        }

        // Procura refutador na ordem horária a partir do próximo.
        ultimoRefutadorIndice = -1;
        ultimoCartaMostradaId = -1;
        int n = jogadores.size();
        for (int i = 1; i < n; i++) {
            int idx = (indiceJogadorAtual + i) % n;
            Carta refuta = jogadores.get(idx)
                            .cartaParaRefutar(personagem, arma, local);
            if (refuta != null) {
                ultimoRefutadorIndice = idx;
                ultimoCartaMostradaId = refuta.getId();
                // Marca a carta mostrada no bloco de notas do jogador atual.
                jogadores.get(indiceJogadorAtual)
                         .getBlocoNotas().marcar(refuta.getId());
                break;
            }
        }
        jaPalpitouNestaVez = true;
        notificarObservadores();
        return true;
    }

    public int getUltimoRefutadorIndice() { return ultimoRefutadorIndice; }
    public int getUltimoCartaMostradaId() { return ultimoCartaMostradaId; }

    // ============================================================
    // ACUSAÇÃO
    // ============================================================

    public boolean podeFazerAcusacao() {
        if (partidaTerminada) return false;
        return !jogadores.get(indiceJogadorAtual).isEliminado();
    }

    /** Faz a acusação final. Se acertar, o jogador vence; se errar,
     *  é eliminado (só continua mostrando cartas em palpites). */
    public boolean fazerAcusacao(int idPersonagem, int idArma, int idLocal) {
        if (!podeFazerAcusacao()) return false;
        Carta p = buscarCarta(idPersonagem);
        Carta a = buscarCarta(idArma);
        Carta l = buscarCarta(idLocal);
        if (p == null || a == null || l == null) return false;

        boolean acertou = envelope.verificarSolucao(p, a, l);
        if (acertou) {
            partidaTerminada = true;
            indiceVencedor   = indiceJogadorAtual;
        } else {
            jogadores.get(indiceJogadorAtual).setEliminado(true);
            // Verifica se sobrou só um jogador ativo (ele ganha por W.O.).
            int ativos = 0, ultimoAtivo = -1;
            for (int i = 0; i < jogadores.size(); i++) {
                if (!jogadores.get(i).isEliminado()) {
                    ativos++;
                    ultimoAtivo = i;
                }
            }
            if (ativos == 1) {
                partidaTerminada = true;
                indiceVencedor   = ultimoAtivo;
            } else if (ativos == 0) {
                partidaTerminada = true;
                indiceVencedor   = -1; // ninguém venceu
            }
        }
        notificarObservadores();
        return acertou;
    }

    public boolean isPartidaTerminada() { return partidaTerminada; }
    public int getIndiceVencedor()      { return indiceVencedor; }

    /** Cartas do envelope (apenas para mostrar ao final do jogo). */
    public int[] getEnvelopeIds() {
        return new int[] {
            envelope.getPersonagem().getId(),
            envelope.getArma().getId(),
            envelope.getLocal().getId()
        };
    }

    // ============================================================
    // TURNO
    // ============================================================

    public void proximoJogador() {
        if (partidaTerminada) return;
        // Pula jogadores eliminados.
        int n = jogadores.size();
        int prox = indiceJogadorAtual;
        for (int k = 0; k < n; k++) {
            prox = (prox + 1) % n;
            if (!jogadores.get(prox).isEliminado()) break;
        }
        indiceJogadorAtual       = prox;
        ultimoLancamento         = null;
        usouPassagemSecretaNaVez = false;
        jaMoveouNestaVez         = false;
        jaPalpitouNestaVez       = false;
        ultimoRefutadorIndice    = -1;
        ultimoCartaMostradaId    = -1;
        notificarObservadores();
    }

    // ============================================================
    // SAVE / LOAD
    // ============================================================

    /** Salva a partida num arquivo de texto simples. */
    public void salvar(File arquivo) throws IOException {
        BufferedWriter w = new BufferedWriter(new FileWriter(arquivo));
        try {
            w.write("clue-save-v1"); w.newLine();
            w.write("envelope " + envelope.getPersonagem().getId()
                    + " " + envelope.getArma().getId()
                    + " " + envelope.getLocal().getId());
            w.newLine();
            w.write("jogadorAtual " + indiceJogadorAtual);
            w.newLine();
            w.write("terminada " + partidaTerminada + " " + indiceVencedor);
            w.newLine();
            for (int i = 0; i < pinos.length; i++) {
                Casa c = pinos[i].getCasa();
                w.write("pino " + i + " " + c.getLinha() + " " + c.getColuna());
                w.newLine();
            }
            w.write("jogadores " + jogadores.size()); w.newLine();
            for (int i = 0; i < jogadores.size(); i++) {
                Jogador j = jogadores.get(i);
                int idxPersonagem = indicePorNomePersonagem(j.getNome());
                StringBuilder mao = new StringBuilder();
                for (Carta c : j.getMao()) {
                    if (mao.length() > 0) mao.append(",");
                    mao.append(c.getId());
                }
                w.write("jogador " + i + " " + idxPersonagem
                        + " " + (j.isEliminado() ? 1 : 0)
                        + " " + mao);
                w.newLine();
                // Notas marcadas
                StringBuilder notas = new StringBuilder();
                for (String[] info : getTodasAsCartas()) {
                    int id = Integer.parseInt(info[0]);
                    if (j.getBlocoNotas().estaMarcada(id)) {
                        if (notas.length() > 0) notas.append(",");
                        notas.append(id);
                    }
                }
                w.write("notas " + i + " " + notas); w.newLine();
            }
        } finally {
            w.close();
        }
    }

    /** Lê um save e devolve o envelope reconstruído. O resto do
     *  estado é aplicado diretamente em this. */
    private Envelope carregarDeArquivo(File arquivo) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(arquivo));
        Envelope env = null;
        try {
            String linha = r.readLine();
            if (linha == null || !linha.equals("clue-save-v1")) {
                throw new IOException("Arquivo de save inválido.");
            }
            while ((linha = r.readLine()) != null) {
                String[] partes = linha.split(" ");
                if (partes.length == 0) continue;
                String tag = partes[0];
                if ("envelope".equals(tag)) {
                    env = montarEnvelope(Integer.parseInt(partes[1]),
                                         Integer.parseInt(partes[2]),
                                         Integer.parseInt(partes[3]));
                } else if ("jogadorAtual".equals(tag)) {
                    indiceJogadorAtual = Integer.parseInt(partes[1]);
                } else if ("terminada".equals(tag)) {
                    partidaTerminada = Boolean.parseBoolean(partes[1]);
                    indiceVencedor   = Integer.parseInt(partes[2]);
                } else if ("pino".equals(tag)) {
                    int idx = Integer.parseInt(partes[1]);
                    int l   = Integer.parseInt(partes[2]);
                    int col = Integer.parseInt(partes[3]);
                    pinos[idx].setCasa(tabuleiro.getCasa(l, col));
                } else if ("jogador".equals(tag)) {
                    int idx          = Integer.parseInt(partes[1]);
                    int idxPersonagem = Integer.parseInt(partes[2]);
                    boolean elim     = "1".equals(partes[3]);
                    Jogador j = new Jogador(PERSONAGENS_PADRAO[idxPersonagem],
                                            CORES_PADRAO[idxPersonagem]);
                    j.setPino(pinos[idxPersonagem]);
                    j.setEliminado(elim);
                    if (partes.length >= 5 && partes[4].length() > 0) {
                        for (String s : partes[4].split(",")) {
                            int id = Integer.parseInt(s);
                            // Recria a carta a partir do baralho (já criado).
                            Carta c = buscarCartaInicial(id);
                            if (c != null) j.receberCarta(c);
                        }
                    }
                    while (jogadores.size() <= idx) jogadores.add(null);
                    jogadores.set(idx, j);
                } else if ("notas".equals(tag)) {
                    int idx = Integer.parseInt(partes[1]);
                    if (partes.length >= 3 && partes[2].length() > 0) {
                        Jogador j = jogadores.get(idx);
                        for (String s : partes[2].split(",")) {
                            j.getBlocoNotas().marcar(Integer.parseInt(s));
                        }
                    }
                }
            }
        } finally {
            r.close();
        }
        if (env == null) throw new IOException("Envelope ausente no save.");
        return env;
    }

    /** Constroi um Envelope com base nos ids salvos. */
    private Envelope montarEnvelope(int idP, int idA, int idL) {
        return new Envelope(buscarCartaInicial(idP),
                            buscarCartaInicial(idA),
                            buscarCartaInicial(idL));
    }

    /** Recupera uma carta pelo id usando o baralho completo (recém
     *  criado). Útil para o LOAD. */
    private Carta buscarCartaInicial(int id) {
        // O baralho criado por novaPartida tem todas as 21 cartas no
        // mesmo conjunto antes da distribuição. Recriar um baralho
        // novo nos dá acesso aos mesmos ids, pois a ordem de criação
        // é determinística.
        Baralho ref = new Baralho();
        // ref aqui não é usado para nada além de gerar os ids; o que
        // precisamos é apenas (nome, tipo) para o id. Reaproveitamos
        // o mapeamento estático.
        int n = 0;
        // 6 personagens
        for (String s : PERSONAGENS_PADRAO) {
            if (n == id) return new Carta(id, s, Carta.Tipo.PERSONAGEM);
            n++;
        }
        for (String s : ARMAS_PADRAO) {
            if (n == id) return new Carta(id, s, Carta.Tipo.ARMA);
            n++;
        }
        for (String s : COMODOS_PADRAO) {
            if (n == id) return new Carta(id, s, Carta.Tipo.LOCAL);
            n++;
        }
        return null;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private Pino pinoAtual() {
        return jogadores.get(indiceJogadorAtual).getPino();
    }

    private Carta buscarCarta(int idCarta) {
        // Procura entre envelope + mãos.
        if (envelope.getPersonagem().getId() == idCarta) return envelope.getPersonagem();
        if (envelope.getArma().getId() == idCarta)       return envelope.getArma();
        if (envelope.getLocal().getId() == idCarta)      return envelope.getLocal();
        for (Jogador j : jogadores) {
            for (Carta c : j.getMao()) {
                if (c.getId() == idCarta) return c;
            }
        }
        return null;
    }

    private Carta buscarCartaPorNome(String nome, Carta.Tipo tipo) {
        if (envelope.getPersonagem().getNome().equals(nome)
                && envelope.getPersonagem().getTipo() == tipo)
            return envelope.getPersonagem();
        if (envelope.getArma().getNome().equals(nome)
                && envelope.getArma().getTipo() == tipo)
            return envelope.getArma();
        if (envelope.getLocal().getNome().equals(nome)
                && envelope.getLocal().getTipo() == tipo)
            return envelope.getLocal();
        for (Jogador j : jogadores) {
            for (Carta c : j.getMao()) {
                if (c.getTipo() == tipo && c.getNome().equals(nome)) return c;
            }
        }
        return null;
    }

    private int indicePorNomePersonagem(String nome) {
        for (int i = 0; i < PERSONAGENS_PADRAO.length; i++) {
            if (PERSONAGENS_PADRAO[i].equals(nome)) return i;
        }
        return -1;
    }
}
