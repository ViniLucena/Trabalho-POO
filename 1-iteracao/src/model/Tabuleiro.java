package model;

import java.util.*;

/**
 * Representa o tabuleiro do jogo Clue (24 linhas x 25 colunas).
 */
class Tabuleiro {

    // variaveis
    private static final int LINHAS = 24;
    private static final int COLUNAS = 25;

    private static final String COZINHA          = "Cozinha";
    private static final String SALA_MUSICA      = "Sala de Música";
    private static final String JARDIM_INVERNO   = "Jardim de Inverno";
    private static final String SALA_JANTAR      = "Sala de Jantar";
    private static final String SALAO_JOGOS      = "Salão de Jogos";
    private static final String BIBLIOTECA       = "Biblioteca";
    private static final String SALA_ESTAR       = "Sala de Estar";
    private static final String ENTRADA          = "Entrada";
    private static final String ESCRITORIO       = "Escritório";

    private final Casa[][] grid;
    private final HashMap<Integer, Casa> casasPorId;
    private final HashMap<String, Comodo> comodos;

    // métodos
    Tabuleiro() {
        grid = new Casa[LINHAS][COLUNAS];
        casasPorId = new HashMap<Integer, Casa>();
        comodos = new HashMap<String, Comodo>();

        construirTabuleiro();
    }

    private void construirTabuleiro() {
        inicializarComodos();
        preencherGrid();
        definirPortas();
        definirPosicoesIniciais();
    }

    private void inicializarComodos() {
        // idCasaRepresentativa = célula interna central de cada cômodo (usada para teleporte)
        // Cozinha: linhas 1-6, colunas 1-6 → representativa: (3,3)
        comodos.put(COZINHA,        new Comodo(COZINHA,        id(3,3),  ESCRITORIO));
        // Sala de Música: linhas 1-6, colunas 9-14 → representativa: (3,11)
        comodos.put(SALA_MUSICA,    new Comodo(SALA_MUSICA,    id(3,11), SALA_ESTAR));
        // Jardim de Inverno: linhas 1-6, colunas 18-23 → representativa: (3,20)
        comodos.put(JARDIM_INVERNO, new Comodo(JARDIM_INVERNO, id(3,20), null));
        // Sala de Jantar: linhas 9-14, colunas 1-6 → representativa: (11,3)
        comodos.put(SALA_JANTAR,    new Comodo(SALA_JANTAR,    id(11,3), null));
        // Salão de Jogos: linhas 9-14, colunas 9-14 → inacessível → representativa: (11,11)
        comodos.put(SALAO_JOGOS,    new Comodo(SALAO_JOGOS,    id(11,11),null));
        // Biblioteca: linhas 9-14, colunas 18-23 → representativa: (11,20)
        comodos.put(BIBLIOTECA,     new Comodo(BIBLIOTECA,     id(11,20),null));
        // Sala de Estar: linhas 17-22, colunas 1-6 → representativa: (19,3)
        comodos.put(SALA_ESTAR,     new Comodo(SALA_ESTAR,     id(19,3), SALA_MUSICA));
        // Entrada: linhas 17-22, colunas 9-14 → representativa: (19,11)
        comodos.put(ENTRADA,        new Comodo(ENTRADA,        id(19,11),null));
        // Escritório: linhas 17-22, colunas 18-23 → representativa: (19,20)
        comodos.put(ESCRITORIO,     new Comodo(ESCRITORIO,     id(19,20),COZINHA));
    }

    private void preencherGrid() {
        int nextId = 0;
        for (int l = 0; l < LINHAS; l++) {
            for (int c = 0; c < COLUNAS; c++) {
                String comodo = detectarComodo(l, c);
                boolean transitavel;
                if (comodo != null) {
                    transitavel = false;
                } else if (ehCanto(l, c)) {
                    transitavel = false;
                } else {
                    transitavel = true;
                }
                Casa casa = new Casa(nextId, l, c, transitavel, comodo);
                grid[l][c] = casa;
                casasPorId.put(nextId, casa);
                nextId++;
            }
        }
    }

    private String detectarComodo(int l, int c) {
        if (l >= 1 && l <= 6  && c >= 1 && c <= 6)  return COZINHA;
        if (l >= 1 && l <= 6  && c >= 9 && c <= 14) return SALA_MUSICA;
        if (l >= 1 && l <= 6  && c >= 18 && c <= 23) return JARDIM_INVERNO;
        if (l >= 9 && l <= 14 && c >= 1 && c <= 6)  return SALA_JANTAR;
        if (l >= 9 && l <= 14 && c >= 9 && c <= 14) return SALAO_JOGOS;
        if (l >= 9 && l <= 14 && c >= 18 && c <= 23) return BIBLIOTECA;
        if (l >= 17 && l <= 22 && c >= 1 && c <= 6)  return SALA_ESTAR;
        if (l >= 17 && l <= 22 && c >= 9 && c <= 14) return ENTRADA;
        if (l >= 17 && l <= 22 && c >= 18 && c <= 23) return ESCRITORIO;
        return null;
    }

    private boolean ehCanto(int l, int c) {
        // Cantos arredondados nos 4 cantos do tabuleiro
        if (l == 0 && c == 0) return true;
        if (l == 0 && c == 24) return true;
        if (l == 23 && c == 0) return true;
        if (l == 23 && c == 24) return true;
        return false;
    }

    private void definirPortas() {
        // Portas: célula de corredor adjacente à borda do cômodo
        // Cozinha (lin 1-6, col 1-6)
        //   Porta sul: (7, 2)
        //   Porta leste: (4, 7)
        adicionarPortaSe(COZINHA, 7, 2);
        adicionarPortaSe(COZINHA, 4, 7);

        // Sala de Música (lin 1-6, col 9-14)
        //   Porta sul: (7, 11)
        //   Porta leste: (3, 15)
        adicionarPortaSe(SALA_MUSICA, 7, 11);
        adicionarPortaSe(SALA_MUSICA, 3, 15);

        // Jardim de Inverno (lin 1-6, col 18-23)
        //   Porta sul: (7, 20)
        //   Porta oeste: (3, 17)
        adicionarPortaSe(JARDIM_INVERNO, 7, 20);
        adicionarPortaSe(JARDIM_INVERNO, 3, 17);

        // Sala de Jantar (lin 9-14, col 1-6)
        //   Porta norte: (8, 3)
        //   Porta leste: (12, 7)
        adicionarPortaSe(SALA_JANTAR, 8, 3);
        adicionarPortaSe(SALA_JANTAR, 12, 7);

        // Salão de Jogos – inacessível, sem portas

        // Biblioteca (lin 9-14, col 18-23)
        //   Porta norte: (8, 20)
        //   Porta oeste: (11, 17)
        adicionarPortaSe(BIBLIOTECA, 8, 20);
        adicionarPortaSe(BIBLIOTECA, 11, 17);

        // Sala de Estar (lin 17-22, col 1-6)
        //   Porta norte: (16, 3)
        //   Porta leste: (19, 7)
        adicionarPortaSe(SALA_ESTAR, 16, 3);
        adicionarPortaSe(SALA_ESTAR, 19, 7);

        // Entrada (lin 17-22, col 9-14)
        //   Porta norte: (16, 11)
        //   Porta leste: (19, 15)
        adicionarPortaSe(ENTRADA, 16, 11);
        adicionarPortaSe(ENTRADA, 19, 15);

        // Escritório (lin 17-22, col 18-23)
        //   Porta norte: (16, 20)
        //   Porta oeste: (19, 17)
        adicionarPortaSe(ESCRITORIO, 16, 20);
        adicionarPortaSe(ESCRITORIO, 19, 17);
    }

    private void adicionarPortaSe(String nomeComodo, int l, int c) {
        Casa casa = grid[l][c];
        if (casa != null && casa.isTransitavel()) {
            comodos.get(nomeComodo).adicionarPorta(casa.getId());
        }
    }

    private void definirPosicoesIniciais() {
        // As posições iniciais são células do corredor próximas à borda.
        // Índice 0 = Scarlet, 1 = Mustard, 2 = White, 3 = Green, 4 = Peacock, 5 = Plum
        // Srta. Scarlet: borda leste, entre Sala de Jantar e Biblioteca → (12, 24)
        // Coronel Mustard: borda sul, abaixo da Sala de Jantar → (23, 3)
        // Sra. White: borda sul, abaixo do Escritório → (23, 20)
        // Reverendo Green: borda norte, acima do Jardim de Inverno → (0, 20)
        // Sra. Peacock: borda oeste, entre Cozinha e Sala de Estar → (12, 0) -- mas (12,0) é transitável
        // Professor Plum: borda norte, acima da Sala de Música → (0, 11)
        // Verifica-se que essas células são corredores (transitável = true) ou borda.
    }

    private int id(int l, int c) {
        return l * COLUNAS + c;
    }

    Casa getCasaPorId(int id) {
        return casasPorId.get(id);
    }

    Comodo getComodoPorNome(String nome) {
        return comodos.get(nome);
    }

    /**
     * Retorna a casa inicial do jogador pelo índice:
     * 0=Scarlet, 1=Mustard, 2=White, 3=Green, 4=Peacock, 5=Plum
     */
    Casa getPosicaoInicial(int indiceJogador) {
        switch (indiceJogador) {
            case 0: return grid[12][24]; // Scarlet – borda leste
            case 1: return grid[23][3];  // Mustard – borda sul
            case 2: return grid[23][20]; // White – borda sul
            case 3: return grid[0][20];  // Green – borda norte
            case 4: return grid[12][0];  // Peacock – borda oeste
            case 5: return grid[0][11];  // Plum – borda norte
            default: throw new IllegalArgumentException("Índice de jogador inválido: " + indiceJogador);
        }
    }

    /**
     * Retorna os ids das casas alcançáveis a partir de origem em exatamente 'passos' passos.
     * Usa DFS com backtracking (conjunto de visitados por caminho).
     */
    List<Integer> mapearCasasAlcancaveis(Casa origem, int passos) {
        HashSet<Integer> resultado = new HashSet<Integer>();
        if (passos == 0) {
            return new ArrayList<Integer>(resultado);
        }
        HashSet<Integer> visitados = new HashSet<Integer>();
        visitados.add(origem.getId());
        dfs(origem, passos, visitados, resultado);
        return new ArrayList<Integer>(resultado);
    }

    private void dfs(Casa atual, int passosRestantes, HashSet<Integer> visitados, HashSet<Integer> resultado) {
        if (passosRestantes == 0) {
            resultado.add(atual.getId());
            return;
        }
        int l = atual.getLinha();
        int c = atual.getColuna();
        int[][] direcoes = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] dir : direcoes) {
            int nl = l + dir[0];
            int nc = c + dir[1];
            if (nl < 0 || nl >= LINHAS || nc < 0 || nc >= COLUNAS) continue;
            Casa vizinha = grid[nl][nc];
            if (!vizinha.isTransitavel()) continue;
            if (visitados.contains(vizinha.getId())) continue;
            visitados.add(vizinha.getId());
            dfs(vizinha, passosRestantes - 1, visitados, resultado);
            visitados.remove(vizinha.getId());
        }
    }

    int getLinhas() {
        return LINHAS;
    }

    int getColunas() {
        return COLUNAS;
    }
}
