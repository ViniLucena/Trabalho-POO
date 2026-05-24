package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tabuleiro do Clue, representado por um grid de 24 colunas
 * x 25 linhas. Possui:
 *
 *  - Casas de CORREDOR  (caminháveis fora dos cômodos);
 *  - Casas de COMODO    (interior dos 9 cômodos jogáveis);
 *  - Casas de PORTA     (corredor, adjacentes a cômodos);
 *  - Casas BLOQUEADAs   (fora do tabuleiro útil).
 *
 * As regras de movimento implementadas seguem a 2ª iteração:
 *   - O jogador lança 2 dados e tenta se deslocar até uma casa
 *     ou cômodo escolhido por clique.
 *   - Em corredor, o deslocamento é de até N passos pelo caminho
 *     mais curto (BFS), sem atravessar cômodos.
 *   - Para entrar num cômodo, basta alcançar uma de suas PORTAs
 *     dentro do número de passos.
 *   - Estando num cômodo, o pino pode sair por qualquer PORTA
 *     desse cômodo e usar o restante dos passos no corredor.
 *   - Cômodos opostos têm passagem secreta: a opção é oferecida
 *     pela View, mas a decisão de mover é processada por Jogo.
 *
 * Visibilidade: package-private. A View só interage com o
 * tabuleiro através da fachada Jogo.
 */
class Tabuleiro {

    static final int LINHAS = 25;
    static final int COLUNAS = 24;

    private final Casa[][] grid = new Casa[LINHAS][COLUNAS];
    private final Map<Integer, Casa> casasPorId = new HashMap<>();
    private final Map<String, Comodo> comodosPorNome = new HashMap<>();
    private final List<Comodo> comodos = new ArrayList<>();

    Tabuleiro() {
        construirCorredores();
        construirComodos();
        construirPortas();
        construirPassagensSecretas();
    }

    // ------------------------------------------------------------
    // Construção do tabuleiro
    // ------------------------------------------------------------

    private void construirCorredores() {
        int id = 0;
        for (int l = 0; l < LINHAS; l++) {
            for (int c = 0; c < COLUNAS; c++) {
                grid[l][c] = new Casa(id, l, c, TipoCasa.CORREDOR);
                casasPorId.put(id, grid[l][c]);
                id++;
            }
        }
    }

    /**
     * Cria cada cômodo como um retângulo de Casas do tipo COMODO.
     * As coordenadas são uma aproximação da disposição clássica
     * do Clue (Cozinha, Sala de Música, Jardim de Inverno na parte
     * de cima; Sala de Jantar, Salão de Jogos e Biblioteca no
     * meio; Sala de Estar, Entrada e Escritório embaixo). O
     * centro "Detetive" do tabuleiro real (onde ficam as armas)
     * é marcado como BLOQUEADA, pois não é jogável.
     */
    private void construirComodos() {
        criarComodo("Cozinha",          1, 1, 6, 5);
        criarComodo("Sala de Música",   1, 9, 6, 14);
        criarComodo("Jardim de Inverno",1, 18, 5, 22);
        criarComodo("Sala de Jantar",   9, 0, 13, 6);
        criarComodo("Salão de Jogos",   9, 18, 12, 22);
        criarComodo("Biblioteca",       14, 17, 17, 22);
        criarComodo("Sala de Estar",    19, 1, 23, 5);
        criarComodo("Entrada",          19, 9, 24, 13);
        criarComodo("Escritório",       19, 18, 23, 22);

        // Centro não jogável (onde ficam as armas - "Detetive").
        for (int l = 10; l <= 14; l++) {
            for (int c = 10; c <= 14; c++) {
                Casa antiga = grid[l][c];
                Casa nova = new Casa(antiga.getId(), l, c, TipoCasa.BLOQUEADA);
                grid[l][c] = nova;
                casasPorId.put(nova.getId(), nova);
            }
        }
    }

    private void criarComodo(String nome, int linIni, int colIni,
                             int linFim, int colFim) {
        Comodo comodo = new Comodo(comodos.size(), nome);
        for (int l = linIni; l <= linFim; l++) {
            for (int c = colIni; c <= colFim; c++) {
                Casa antiga = grid[l][c];
                Casa nova = new Casa(antiga.getId(), l, c, TipoCasa.COMODO);
                nova.setComodo(comodo);
                grid[l][c] = nova;
                casasPorId.put(nova.getId(), nova);
                comodo.adicionarCasa(nova);
            }
        }
        comodos.add(comodo);
        comodosPorNome.put(nome, comodo);
    }

    /**
     * Marca como PORTA as casas de corredor adjacentes a cada
     * cômodo. Cada porta é, na prática, a "entrada" do cômodo:
     * estar sobre a porta equivale a poder entrar no cômodo na
     * próxima jogada (e vice-versa).
     */
    private void construirPortas() {
        adicionarPorta("Cozinha",          7, 4);
        adicionarPorta("Sala de Música",   7, 9);
        adicionarPorta("Sala de Música",   7, 14);
        adicionarPorta("Jardim de Inverno",6, 19);
        adicionarPorta("Sala de Jantar",   8, 3);
        adicionarPorta("Sala de Jantar",   13, 7);
        adicionarPorta("Salão de Jogos",   8, 20);
        adicionarPorta("Salão de Jogos",   13, 19);
        adicionarPorta("Biblioteca",       14, 16);
        adicionarPorta("Biblioteca",       18, 20);
        adicionarPorta("Sala de Estar",    18, 3);
        adicionarPorta("Entrada",          18, 9);
        adicionarPorta("Entrada",          18, 13);
        adicionarPorta("Escritório",       18, 21);
    }

    private void adicionarPorta(String nomeComodo, int linha, int coluna) {
        Casa antiga = grid[linha][coluna];
        if (antiga.getTipo() != TipoCasa.CORREDOR) return;
        Comodo comodo = comodosPorNome.get(nomeComodo);
        Casa nova = new Casa(antiga.getId(), linha, coluna, TipoCasa.PORTA);
        nova.setComodo(comodo);
        grid[linha][coluna] = nova;
        casasPorId.put(nova.getId(), nova);
        comodo.adicionarPorta(nova);
    }

    /**
     * Cria as duas passagens secretas clássicas do Clue:
     *  - Cozinha           <-> Escritório
     *  - Jardim de Inverno <-> Sala de Estar
     */
    private void construirPassagensSecretas() {
        ligarPassagem("Cozinha", "Escritório");
        ligarPassagem("Jardim de Inverno", "Sala de Estar");
    }

    private void ligarPassagem(String a, String b) {
        Comodo ca = comodosPorNome.get(a);
        Comodo cb = comodosPorNome.get(b);
        if (ca != null && cb != null) {
            ca.setConexaoSecreta(cb);
            cb.setConexaoSecreta(ca);
        }
    }

    // ------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------

    Casa getCasa(int linha, int coluna) {
        if (linha < 0 || linha >= LINHAS) return null;
        if (coluna < 0 || coluna >= COLUNAS) return null;
        return grid[linha][coluna];
    }

    Casa getCasaPorId(int id) {
        return casasPorId.get(id);
    }

    List<Comodo> getComodos() {
        return comodos;
    }

    Comodo getComodoPorNome(String nome) {
        return comodosPorNome.get(nome);
    }

    int getLinhas()  { return LINHAS; }
    int getColunas() { return COLUNAS; }

    /**
     * Posições iniciais (clássicas do Clue) para os 6 personagens,
     * nas bordas do tabuleiro. A ordem segue Jogo.CORES_PADRAO.
     */
    Casa getPosicaoInicial(int indicePersonagem) {
        switch (indicePersonagem) {
            case 0: return grid[24][7];   // Srta. Scarlet (vermelho)
            case 1: return grid[17][23];  // Cel. Mustard  (amarelo)
            case 2: return grid[0][9];    // Sra. White    (branco)
            case 3: return grid[0][14];   // Rev. Green    (verde)
            case 4: return grid[6][23];   // Sra. Peacock  (azul)
            case 5: return grid[19][0];   // Prof. Plum    (violeta)
            default: return grid[0][0];
        }
    }

    // ------------------------------------------------------------
    // Mapeamento de casas/cômodos alcançáveis
    // ------------------------------------------------------------

    /**
     * Calcula todas as posições alcançáveis (ids de Casa) a
     * partir da origem usando EXATAMENTE "passos" movimentos,
     * respeitando as regras do manual oficial do Clue:
     *
     *  - O jogador deve usar todos os passos do dado em casas
     *    de corredor (não pode parar antes), salvo se entrar
     *    num cômodo (entrar encerra o movimento, mesmo que
     *    sobrem passos — manual pg. 4).
     *  - Não pode passar pelo mesmo espaço duas vezes na mesma
     *    jogada (manual pg. 4).
     *  - Não pode entrar, passar nem cair em casa ocupada por
     *    outro Suspeito (manual pg. 4). Cômodos não bloqueiam:
     *    vários pinos podem coexistir num cômodo.
     *  - Não pode retornar ao mesmo cômodo em que iniciou a
     *    jogada.
     *
     * Casas internas de um cômodo são representadas pelo id da
     * casa principal: ao alcançar uma porta do cômodo, o id
     * desse cômodo é adicionado ao conjunto alcançável.
     *
     * @param origem   casa onde o pino está
     * @param passos   total dos dois dados
     * @param ocupadas ids de casas (corredor/porta) ocupadas por
     *                 outros pinos — bloqueiam tanto destino
     *                 quanto travessia
     */
    Set<Integer> mapearAlcancaveis(Casa origem, int passos,
                                   Set<Integer> ocupadas) {
        Set<Integer> alcancaveis = new HashSet<>();
        if (passos <= 0) return alcancaveis;

        if (origem.isComodo()) {
            // Sair de um cômodo: o pino atravessa uma porta (de
            // graça, segundo o manual: "Uma porta é a abertura
            // presente na parede, não é o espaço em frente à
            // entrada"). A casa em frente à porta — representada
            // no código como uma casa do tipo PORTA — é o
            // primeiro espaço gasto.
            for (Casa porta : origem.getComodo().getPortas()) {
                if (ocupadas.contains(porta.getId())) continue;
                Set<Integer> visitadas = new HashSet<>();
                visitadas.add(porta.getId());
                explorar(porta, passos - 1, visitadas, origem,
                         ocupadas, alcancaveis);
            }
        } else {
            Set<Integer> visitadas = new HashSet<>();
            visitadas.add(origem.getId());
            explorar(origem, passos, visitadas, origem,
                     ocupadas, alcancaveis);
        }
        return alcancaveis;
    }

    /**
     * Busca em profundidade limitada a "restantes" passos,
     * marcando as casas visitadas no caminho atual para impedir
     * revisita (regra do manual). Em cada nó:
     *  - Se for porta de outro cômodo, o cômodo entra em
     *    alcancaveis (entrar encerra a jogada).
     *  - Se restantes == 0 e for corredor, a casa entra em
     *    alcancaveis (parada válida em corredor).
     *  - Se restantes > 0, continua para os vizinhos que não
     *    estejam visitados nem ocupados por outro pino.
     */
    private void explorar(Casa atual, int restantes,
                          Set<Integer> visitadas, Casa origem,
                          Set<Integer> ocupadas,
                          Set<Integer> alcancaveis) {
        // Entrar em cômodo (de outro cômodo que não o de origem)
        // é destino válido em qualquer profundidade — entrar pára
        // o movimento mesmo que sobrem passos.
        if (atual.isPorta()
                && !(origem.isComodo()
                     && atual.getComodo() == origem.getComodo())) {
            Casa principal = atual.getComodo().getCasaPrincipal();
            alcancaveis.add(principal.getId());
        }

        if (restantes == 0) {
            // Parada em corredor só é destino se gastou todos os
            // passos do dado (regra do manual).
            if (atual != origem && atual.isCorredor()) {
                alcancaveis.add(atual.getId());
            }
            return;
        }

        for (Casa viz : vizinhasCorredor(atual)) {
            if (visitadas.contains(viz.getId())) continue;
            if (ocupadas.contains(viz.getId())) continue;
            visitadas.add(viz.getId());
            explorar(viz, restantes - 1, visitadas, origem,
                     ocupadas, alcancaveis);
            visitadas.remove(viz.getId());
        }
    }

    /**
     * Vizinhança de corredor: 4-conectada, sem entrar em casas
     * BLOQUEADAs nem em cômodos (entrada se dá só via PORTA).
     */
    private List<Casa> vizinhasCorredor(Casa c) {
        List<Casa> v = new ArrayList<>();
        int l = c.getLinha();
        int col = c.getColuna();
        adicionarSeCorredor(v, l - 1, col);
        adicionarSeCorredor(v, l + 1, col);
        adicionarSeCorredor(v, l, col - 1);
        adicionarSeCorredor(v, l, col + 1);
        return v;
    }

    private void adicionarSeCorredor(List<Casa> v, int l, int c) {
        if (l < 0 || l >= LINHAS) return;
        if (c < 0 || c >= COLUNAS) return;
        Casa casa = grid[l][c];
        if (casa.getTipo() == TipoCasa.CORREDOR
                || casa.getTipo() == TipoCasa.PORTA) {
            v.add(casa);
        }
    }
}
