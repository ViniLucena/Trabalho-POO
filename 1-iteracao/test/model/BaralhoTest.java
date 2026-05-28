package model;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class BaralhoTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private Baralho baralho;
    private Envelope envelope;

    @Before
    public void setUp() {
        baralho = new Baralho();
        envelope = baralho.sortearSolucao();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void envelopeDeveTerUmaCartaDeCadaTipo() {
        assertNotNull("Personagem do envelope não pode ser nulo", envelope.getNomePersonagem());
        assertNotNull("Arma do envelope não pode ser nula", envelope.getNomeArma());
        assertNotNull("Local do envelope não pode ser nulo", envelope.getNomeLocal());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void envelopeNaoDeveConterCartasRepetidas() {
        String p = envelope.getNomePersonagem();
        String a = envelope.getNomeArma();
        String l = envelope.getNomeLocal();
        assertFalse("Personagem e arma não devem ser iguais", p.equals(a));
        assertFalse("Personagem e local não devem ser iguais", p.equals(l));
        assertFalse("Arma e local não devem ser iguais", a.equals(l));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void distribuirDeveEntregarExatamente18CartasNoTotal() {
        List<Jogador> jogadores = criarJogadores(3);
        baralho.distribuir(jogadores);
        int total = contarCartasNasMaos(jogadores);
        assertEquals("Total de cartas distribuídas deve ser 18", 18, total);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void distribuicaoDeveSerEquilibrada() {
        List<Jogador> jogadores = criarJogadores(4);
        baralho.distribuir(jogadores);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Jogador j : jogadores) {
            int qtd = j.getMao().size();
            if (qtd < min) min = qtd;
            if (qtd > max) max = qtd;
        }
        assertTrue("Diferença entre mãos deve ser no máximo 1", (max - min) <= 1);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void naoDevemHaverCartasDuplicadasEntreEnvelopeEMaos() {
        List<Jogador> jogadores = criarJogadores(3);
        baralho.distribuir(jogadores);
        Set<String> cartasNasMaos = new HashSet<String>();
        for (Jogador j : jogadores) {
            for (Carta c : j.getMao()) {
                cartasNasMaos.add(c.getNome());
            }
        }
        assertFalse("Personagem do envelope não deve estar nas mãos",
                cartasNasMaos.contains(envelope.getNomePersonagem()));
        assertFalse("Arma do envelope não deve estar nas mãos",
                cartasNasMaos.contains(envelope.getNomeArma()));
        assertFalse("Local do envelope não deve estar nas mãos",
                cartasNasMaos.contains(envelope.getNomeLocal()));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void distribuicaoComTresJogadoresEntrega6CartasPorJogador() {
        List<Jogador> jogadores = criarJogadores(3);
        baralho.distribuir(jogadores);
        for (Jogador j : jogadores) {
            assertEquals("Com 3 jogadores cada um deve ter 6 cartas", 6, j.getMao().size());
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void distribuicaoComQuatroJogadoresEntregaCartasCorretas() {
        List<Jogador> jogadores = criarJogadores(4);
        baralho.distribuir(jogadores);
        int total = contarCartasNasMaos(jogadores);
        assertEquals("Com 4 jogadores o total deve ser 18", 18, total);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Jogador j : jogadores) {
            int qtd = j.getMao().size();
            if (qtd < min) min = qtd;
            if (qtd > max) max = qtd;
        }
        assertTrue("Diferença de cartas entre jogadores deve ser no máximo 1", (max - min) <= 1);
    }

    // helpers
    private List<Jogador> criarJogadores(int n) {
        List<Jogador> lista = new ArrayList<Jogador>();
        for (int i = 0; i < n; i++) {
            lista.add(new Jogador("Jogador" + i, "Cor" + i));
        }
        return lista;
    }

    private int contarCartasNasMaos(List<Jogador> jogadores) {
        int total = 0;
        for (Jogador j : jogadores) {
            total += j.getMao().size();
        }
        return total;
    }
}
