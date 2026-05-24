package app;

import java.awt.GraphicsEnvironment;
import java.util.Set;

import model.Jogo;
import view.ResourceLoader;

/**
 * Teste de fumaça da 2ª iteração. Cobre os comportamentos novos:
 *   - Controle de lançamento de dados (podeLancarDados)
 *   - Controle de passagem de vez (podePassarVez)
 *   - Movimento via clique (tentarMover)
 *   - Passagem secreta
 *   - Placeholder do ResourceLoader
 *
 * Pode ser executado sem display gráfico (headless).
 */
public class SmokeTest {

    private static int passados = 0;
    private static int falhos   = 0;

    public static void main(String[] args) {
        System.out.println("=== SmokeTest — 2ª Iteração ===\n");

        testarInicializacao();
        testarControleDados();
        testarMovimento();
        testarControleDeVez();
        testarPassagemSecreta();
        testarResourceLoader();

        System.out.println("\n=== Resultado: "
            + passados + " OK  |  " + falhos + " FALHOU ===");
        if (falhos > 0) {
            System.exit(1);
        }
    }

    // ----------------------------------------------------------------
    // Testes
    // ----------------------------------------------------------------

    private static void testarInicializacao() {
        titulo("1. Inicialização");
        Jogo jogo = new Jogo(6);
        verificar(jogo.getNumeroJogadores() == 6,
            "6 jogadores criados");
        verificar(jogo.getLinhasTabuleiro() == 25,
            "tabuleiro tem 25 linhas");
        verificar(jogo.getColunasTabuleiro() == 24,
            "tabuleiro tem 24 colunas");
        verificar(jogo.getNomesComodos().size() == 9,
            "9 cômodos no tabuleiro");
        verificar(jogo.getIndiceJogadorAtual() == 0,
            "Srta. Scarlet começa (índice 0)");
        verificar("Vermelho".equals(jogo.getCorJogadorAtual()),
            "cor inicial é Vermelho (Scarlet)");
        // Scarlet começa na linha 24, coluna 7
        verificar(jogo.getLinhaPino(0) == 24, "Scarlet: linha inicial = 24");
        verificar(jogo.getColunaPino(0) == 7,  "Scarlet: coluna inicial = 7");
    }

    private static void testarControleDados() {
        titulo("2. Controle de dados");
        Jogo jogo = new Jogo(6);

        verificar(jogo.podeLancarDados(),
            "pode lançar dados antes de rolar");
        verificar(!jogo.podePassarVez(),
            "NÃO pode passar vez antes de mover");

        jogo.setDadosManuais(3, 4);
        int[] d = jogo.lancarDados();
        verificar(d[0] == 3 && d[1] == 4,
            "dados manuais retornam os valores definidos (3, 4)");
        verificar(!jogo.podeLancarDados(),
            "NÃO pode lançar dados novamente após rolar");
        verificar(jogo.jaLancouNestaVez(),
            "jaLancouNestaVez = true após rolar");

        // A soma dos dados é 7; Scarlet está em (24,7).
        // Tenta mover para (22,7) — alcançável em 2 passos para cima.
        // (Com dados = 7, 2 passos é menos que o máximo.)
        boolean moveu = jogo.tentarMover(22, 7);
        verificar(moveu, "tentarMover(22,7) retorna true (destino alcançável)");
        verificar(!jogo.podeLancarDados(),
            "NÃO pode lançar dados após mover (Bug 2 corrigido)");
    }

    private static void testarMovimento() {
        titulo("3. Movimento");
        Jogo jogo = new Jogo(6);

        // Scarlet em (24,7), dados 1+1=2.
        jogo.setDadosManuais(1, 1);
        jogo.lancarDados();

        Set<Integer> alcanc = jogo.getCasasAlcancaveis();
        verificar(alcanc.size() > 0,
            "getCasasAlcancaveis retorna conjunto não vazio");

        // (22,7) = 2 passos para cima → alcançável com dados 2.
        boolean moveuValido = jogo.tentarMover(22, 7);
        verificar(moveuValido,
            "tentarMover para destino válido retorna true");
        verificar(jogo.getLinhaPino(0) == 22 && jogo.getColunaPino(0) == 7,
            "pino de Scarlet atualizado para (22,7)");

        // Nova rodada: Scarlet agora em (22,7), dados 1+1=2.
        jogo.proximoJogador(); // passa para Mustard
        // Volta para Scarlet na próxima rodada (simplificação do teste)
        // — testa destino inválido com outro jogador.
        // Mustard começa em (17,23). Dados 1+1=2.
        jogo.setDadosManuais(1, 1);
        jogo.lancarDados();

        // (10,10) é o centro bloqueado — nunca alcançável.
        boolean moveuInvalido = jogo.tentarMover(10, 10);
        verificar(!moveuInvalido,
            "tentarMover para destino inválido retorna false");
        // Posição de Mustard não mudou (linha 17, coluna 23).
        verificar(jogo.getLinhaPino(1) == 17 && jogo.getColunaPino(1) == 23,
            "posição de Mustard não muda após tentativa inválida");
    }

    private static void testarControleDeVez() {
        titulo("4. Controle de vez");
        Jogo jogo = new Jogo(6);

        verificar(!jogo.podePassarVez(),
            "podePassarVez = false antes de qualquer ação");

        jogo.setDadosManuais(1, 1);
        jogo.lancarDados();
        verificar(!jogo.podePassarVez(),
            "podePassarVez = false após rolar (ainda não moveu)");

        jogo.tentarMover(22, 7); // move Scarlet para cima
        verificar(jogo.podePassarVez(),
            "podePassarVez = true após mover com sucesso");

        jogo.proximoJogador();
        verificar(jogo.podeLancarDados(),
            "proximoJogador() reseta podeLancarDados para true");
        verificar(!jogo.podePassarVez(),
            "proximoJogador() reseta podePassarVez para false");
        verificar(jogo.getIndiceJogadorAtual() == 1,
            "próximo jogador é índice 1 (Mustard)");
    }

    private static void testarPassagemSecreta() {
        titulo("5. Passagem secreta");
        Jogo jogo = new Jogo(6);

        // No início, todos estão no corredor → nenhum pode usar passagem.
        verificar(!jogo.podeUsarPassagemSecreta(),
            "podeUsarPassagemSecreta = false (Scarlet no corredor)");

        // Verifica que os 4 pares de passagem secreta existem nos cômodos.
        verificar(
            jogo.getNomesComodos().contains("Cozinha") &&
            jogo.getNomesComodos().contains("Escritório"),
            "cômodos Cozinha e Escritório existem (passagem secreta 1)");
        verificar(
            jogo.getNomesComodos().contains("Jardim de Inverno") &&
            jogo.getNomesComodos().contains("Sala de Estar"),
            "cômodos Jardim de Inverno e Sala de Estar existem (passagem secreta 2)");
    }

    private static void testarResourceLoader() {
        titulo("6. ResourceLoader (placeholders)");
        System.setProperty("java.awt.headless", "true");

        Jogo jogo = new Jogo(6);
        ResourceLoader rl = new ResourceLoader(jogo, 920, 960, 30, 80, 180, 240);

        verificar(rl.getTabuleiro() != null,
            "imagem do tabuleiro gerada");
        verificar(rl.getTabuleiro().getWidth() == 920,
            "largura do tabuleiro = 920");

        boolean dadosOk = true;
        for (int v = 1; v <= 6; v++) {
            if (rl.getDado(v) == null) { dadosOk = false; break; }
        }
        verificar(dadosOk, "imagens dos 6 dados geradas");

        boolean pinosOk = true;
        for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
            if (rl.getPino(i) == null) { pinosOk = false; break; }
        }
        verificar(pinosOk, "imagens dos 6 pinos geradas");

        boolean cartasOk = true;
        for (int[] info : jogo.getIdsCartasJogadorAtual()) {
            if (rl.getCarta(info[0]) == null) { cartasOk = false; break; }
        }
        verificar(cartasOk, "imagens das cartas do jogador atual indexadas por id");
    }

    // ----------------------------------------------------------------
    // Utilitários
    // ----------------------------------------------------------------

    private static void titulo(String s) {
        System.out.println("--- " + s + " ---");
    }

    private static void verificar(boolean condicao, String descricao) {
        if (condicao) {
            System.out.println("[OK]     " + descricao);
            passados++;
        } else {
            System.out.println("[FALHOU] " + descricao);
            falhos++;
        }
    }
}
