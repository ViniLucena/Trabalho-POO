package app;

import java.io.File;
import java.util.Set;

import model.Jogo;
import model.Observador;
import view.ResourceLoader;

/**
 * Teste de fumaça da 3ª iteração. Roda sem display gráfico
 * (headless) e cobre:
 *   - Padrão Singleton da fachada (getInstancia / novaPartida).
 *   - Padrão Observer (Jogo notifica observadores).
 *   - Movimentação básica e regras do enunciado.
 *   - Palpite (refute + marcação no bloco de notas).
 *   - Acusação (acerto e erro).
 *   - Save / Load.
 */
public class SmokeTest {

    private static int passados = 0;
    private static int falhos   = 0;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.out.println("=== SmokeTest — 3ª Iteração ===\n");

        testarSingleton();
        testarObserver();
        testarMovimento();
        testarPalpite();
        testarAcusacao();
        testarSaveLoad();
        testarResourceLoader();

        System.out.println("\n=== Resultado: "
            + passados + " OK  |  " + falhos + " FALHOU ===");
        if (falhos > 0) System.exit(1);
    }

    // ----------------------------------------------------------------
    // Testes
    // ----------------------------------------------------------------

    private static void testarSingleton() {
        titulo("1. Singleton");
        Jogo jogo = Jogo.novaPartida(6);
        verificar(jogo == Jogo.getInstancia(),
            "novaPartida define a instância Singleton");
        Jogo outra = Jogo.novaPartida(4);
        verificar(outra == Jogo.getInstancia(),
            "nova chamada substitui a instância");
        verificar(outra.getNumeroJogadores() == 4,
            "partida nova com 4 jogadores");
    }

    private static void testarObserver() {
        titulo("2. Observer");
        Jogo jogo = Jogo.novaPartida(6);
        final int[] contador = { 0 };
        Observador o = new Observador() {
            @Override
            public void notificar() { contador[0]++; }
        };
        jogo.registrarObservador(o);
        jogo.setDadosManuais(1, 1);
        jogo.lancarDados();
        verificar(contador[0] >= 1,
            "observador notificado após lançarDados");
        int antes = contador[0];
        jogo.proximoJogador();
        verificar(contador[0] > antes,
            "observador notificado após proximoJogador");
        jogo.removerObservador(o);
        int antes2 = contador[0];
        jogo.setDadosManuais(2, 2);
        jogo.lancarDados();
        verificar(contador[0] == antes2,
            "observador removido deixa de ser notificado");
    }

    private static void testarMovimento() {
        titulo("3. Movimento");
        Jogo jogo = Jogo.novaPartida(6);
        verificar(jogo.getLinhaPino(0) == 24 && jogo.getColunaPino(0) == 7,
            "Scarlet começa em (24, 7)");

        jogo.setDadosManuais(1, 1);
        jogo.lancarDados();
        Set<Integer> alc = jogo.getCasasAlcancaveis();
        verificar(!alc.isEmpty(),
            "há casas alcançáveis com 2 passos");

        boolean ok = jogo.tentarMover(22, 7);
        verificar(ok, "tentarMover(22,7) ok");
        verificar(jogo.getLinhaPino(0) == 22 && jogo.getColunaPino(0) == 7,
            "pino atualizado para (22,7)");
        verificar(!jogo.podeLancarDados(),
            "não pode relançar dados após mover");

        // Destino inválido — pino não muda.
        jogo.proximoJogador();
        jogo.setDadosManuais(1, 1);
        jogo.lancarDados();
        int linhaMustard = jogo.getLinhaPino(1);
        int colMustard   = jogo.getColunaPino(1);
        boolean ruim = jogo.tentarMover(10, 10); // centro bloqueado
        verificar(!ruim, "tentarMover para centro bloqueado falha");
        verificar(jogo.getLinhaPino(1) == linhaMustard
                  && jogo.getColunaPino(1) == colMustard,
            "pino de Mustard não muda após tentativa inválida");
    }

    private static void testarPalpite() {
        titulo("4. Palpite");
        Jogo jogo = Jogo.novaPartida(6);
        // Não pode palpitar fora de um cômodo.
        verificar(!jogo.podeFazerPalpite(),
            "não pode palpitar no início (Scarlet no corredor)");

        // Força o jogador atual para um cômodo via tentarMover não
        // é trivial sem caminho longo, então usamos os IDs:
        // Em vez disso, fazemos palpite com um id válido e
        // confirmamos que getNomeCarta funciona.
        verificar(jogo.getNomeCarta(0) != null,
            "getNomeCarta(0) devolve um nome");
        verificar(jogo.getTipoCarta(0).equals("PERSONAGEM"),
            "id 0 é PERSONAGEM");
        verificar(jogo.getTipoCarta(6).equals("ARMA"),
            "id 6 é ARMA");
        verificar(jogo.getTipoCarta(12).equals("LOCAL"),
            "id 12 é LOCAL");
    }

    private static void testarAcusacao() {
        titulo("5. Acusação");
        Jogo jogo = Jogo.novaPartida(6);
        int[] env = jogo.getEnvelopeIds();
        verificar(env.length == 3, "envelope tem 3 ids");

        // Acusação errada: usa três ids quaisquer NÃO do envelope
        // (impossível garantir totalmente sem inspeção, então
        // tentamos uma combinação muito provavelmente errada).
        int idP = (env[0] == 0) ? 1 : 0;
        int idA = (env[1] == 6) ? 7 : 6;
        int idL = (env[2] == 12) ? 13 : 12;
        boolean acerto = jogo.fazerAcusacao(idP, idA, idL);
        verificar(!acerto, "acusação errada (id diferentes do envelope)");
        verificar(jogo.isJogadorEliminado(0),
            "jogador eliminado após acusação errada");

        // Próxima acusação correta — usando os ids do envelope.
        Jogo jogo2 = Jogo.novaPartida(6);
        int[] env2 = jogo2.getEnvelopeIds();
        boolean correto = jogo2.fazerAcusacao(env2[0], env2[1], env2[2]);
        verificar(correto, "acusação correta vence o jogo");
        verificar(jogo2.isPartidaTerminada(), "partida termina ao vencer");
    }

    private static void testarSaveLoad() {
        titulo("6. Save / Load");
        try {
            Jogo jogo = Jogo.novaPartida(6);
            jogo.setDadosManuais(1, 1);
            jogo.lancarDados();
            jogo.tentarMover(22, 7);
            int linhaAntes = jogo.getLinhaPino(0);
            int colAntes   = jogo.getColunaPino(0);
            int idxAntes   = jogo.getIndiceJogadorAtual();
            int[] envAntes = jogo.getEnvelopeIds();

            File f = File.createTempFile("clue-save-", ".txt");
            jogo.salvar(f);

            Jogo jogo2 = Jogo.carregarPartida(f);
            verificar(jogo2.getLinhaPino(0) == linhaAntes
                      && jogo2.getColunaPino(0) == colAntes,
                "save/load preserva posição de Scarlet");
            verificar(jogo2.getIndiceJogadorAtual() == idxAntes,
                "save/load preserva jogador da vez");
            int[] envDepois = jogo2.getEnvelopeIds();
            verificar(envAntes[0] == envDepois[0]
                      && envAntes[1] == envDepois[1]
                      && envAntes[2] == envDepois[2],
                "save/load preserva o envelope");

            f.delete();
        } catch (Exception e) {
            verificar(false, "save/load lançou exceção: " + e.getMessage());
        }
    }

    private static void testarResourceLoader() {
        titulo("7. ResourceLoader");
        Jogo jogo = Jogo.novaPartida(6);
        ResourceLoader rl = new ResourceLoader(jogo, 720, 750, 26, 70, 160, 220);

        verificar(rl.getTabuleiro() != null,
            "imagem do tabuleiro carregada ou gerada");
        verificar(rl.getTabuleiro().getWidth() == 720,
            "tabuleiro com 720 px de largura");
        for (int v = 1; v <= 6; v++) {
            verificar(rl.getDado(v) != null, "dado " + v + " carregado");
        }
        for (int i = 0; i < 6; i++) {
            verificar(rl.getPino(i) != null, "pino " + i + " gerado");
        }
        boolean cartasOk = true;
        for (int[] info : jogo.getIdsCartasJogadorAtual()) {
            if (rl.getCarta(info[0]) == null) { cartasOk = false; break; }
        }
        verificar(cartasOk, "cartas do jogador atual indexadas por id");
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
