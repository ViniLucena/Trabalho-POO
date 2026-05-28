# Prompt — 1ª Iteração: Clue (INF1636 – PUC-Rio)

Leia o arquivo @contexto_POO.md antes de escrever qualquer linha de código.
Siga TODAS as convenções da seção 18 desse arquivo durante toda a implementação.
Este é um trabalho acadêmico da disciplina INF1636 do professor Ivan Mathias Filho.

---

## O que você vai implementar

O componente **Model** do jogo Clue, organizado como um pacote Java chamado `model`.
Nesta iteração não há interface gráfica — apenas lógica de jogo pura e testes JUnit.

---

## Regras de visibilidade (OBRIGATÓRIO)

- Todas as classes que representem elementos do jogo (Tabuleiro, Casa, Comodo,
  Carta, Baralho, Envelope, Jogador, Pino, Dados) devem ser **package-private**
  (sem `public` na declaração da classe).
- O pacote `model` expõe **uma única classe pública: `Jogo`**. Ela é a Façade.
- A classe `app.Main` só pode importar `model.Jogo`. Qualquer tentativa de importar
  `model.Carta`, `model.Tabuleiro` etc. deve ser recusada pelo compilador.
- Isso prova que o princípio da ocultação da informação está sendo respeitado.

---

## Estrutura de pastas

```
src/
  model/
    Jogo.java          ← public  (única classe pública do pacote)
    Tabuleiro.java     ← package-private
    Casa.java          ← package-private
    Comodo.java        ← package-private
    Carta.java         ← package-private
    Baralho.java       ← package-private
    Envelope.java      ← package-private
    Jogador.java       ← package-private
    Pino.java          ← package-private
    Dados.java         ← package-private
  app/
    Main.java          ← public (demonstração; só usa model.Jogo)

test/
  model/
    BaralhoTest.java
    DadosTest.java
    EnvelopeTest.java
    TabuleiroTest.java
    JogoTest.java

lib/
  junit-4.13.2.jar
  hamcrest-core-1.3.jar
```

---

## As classes e o que cada uma deve fazer

### Carta.java
- Enum interno `Tipo`: `PERSONAGEM`, `ARMA`, `LOCAL`
- Atributos privados: `String nome`, `Tipo tipo`
- Construtor package-private: `Carta(String nome, Tipo tipo)`
- Métodos: `getNome()`, `getTipo()`, `toString()`, `equals()`, `hashCode()`
- `equals` compara nome e tipo.

### Baralho.java
Implementa os **itens 4, 5 e 6 da Preparação** do manual:

**Item 4** — `Envelope sortearSolucao()`:
- Separa as cartas em 3 grupos (Suspeitos, Armas, Cômodos).
- Embaralha cada grupo separadamente com `Collections.shuffle()`.
- Retira a primeira carta de cada grupo e monta o Envelope (solução do crime).
- As cartas do Envelope nunca aparecem nas mãos dos jogadores.

**Itens 5 e 6** — `void distribuir(List<Jogador> jogadores)`:
- Junta as 18 cartas restantes e embaralha tudo junto.
- Distribui em rodízio (`i % n`). Diferença máxima entre mãos: 1 carta.

Cartas do jogo (21 no total, nomes exatos do manual em português):
```
Suspeitos (6): Coronel Mustard, Srta. Scarlet, Professor Plum,
               Reverendo Green, Sra. White, Sra. Peacock

Armas (6):     Corda, Cano de Chumbo, Faca,
               Chave Inglesa, Castiçal, Revólver

Cômodos (9):   Cozinha, Sala de Música, Jardim de Inverno,
               Sala de Jantar, Salão de Jogos, Biblioteca,
               Sala de Estar, Entrada, Escritório
```

Use um `HashMap<Carta.Tipo, List<Carta>>` para organizar as pilhas de cartas
(o professor pede HashMap sempre que possível — Cap. 15).

### Envelope.java
- Atributos privados e finais: `Carta personagem`, `Carta arma`, `Carta local`
- Construtor valida os tipos: lança `IllegalArgumentException` se tipo errado.
- Método `boolean verificarAcusacao(String nomePersonagem, String nomeArma, String nomeLocal)`:
  compara pelos nomes (Strings), não por referência.
- Getters retornam `String` (nome da carta), não objetos `Carta` — o Envelope
  nunca expõe cartas diretamente para fora do pacote.

### Jogador.java
- Atributos privados: `String nome`, `String cor`, `List<Carta> mao`,
  `List<String> folhaDeNotas`, `Pino pino`
- `receberCarta(Carta c)`: adiciona à mão.
- `getMao()`: retorna `Collections.unmodifiableList(mao)`.
- `getFolhaDeNotas()`: retorna lista imutável com todos os 21 nomes de cartas.
- `riscarDaFolha(String item)`: remove o item da folha.
- Folha inicializada com os 21 nomes exatos (mesmos do Baralho).

**Item 7 do manual** — cada jogador vê apenas a própria mão:
a API pública de `Jogo` só expõe as cartas do jogador atual, nunca dos outros.

**Item 7 do manual** — ordem de jogo:
Srta. Scarlet é sempre a primeira a jogar. O jogo segue ao próximo jogador
sempre à esquerda (sentido anti-horário = ordem da lista de jogadores).

### Dados.java
- Atributo privado: `Random random`
- Construtor padrão: `Dados()` — usa `new Random()`
- Construtor para testes: `Dados(long seed)` — usa `new Random(seed)`
- `int[] lancar()`: retorna array de 2 inteiros, cada um entre 1 e 6.

### Casa.java
- Atributos privados e finais: `int id`, `int linha`, `int coluna`,
  `boolean transitavel`, `String nomeComodo` (null se for corredor)
- Todos os atributos definidos no construtor.
- Getters para todos os atributos.
- `toString()`: `"Casa#id(linha,coluna)"`

### Comodo.java
- Atributos privados: `String nome`, `List<Integer> idPortas`,
  `int idCasaRepresentativa`, `String passagemSecreta` (null se não tiver)
- `passagemSecreta` é o nome do cômodo destino da passagem (ou null).
- Getters para todos os atributos.
- `void adicionarPorta(int idCasa)`

### Tabuleiro.java

**Dimensões reais do tabuleiro do Clue:** 24 linhas × 25 colunas.

O tabuleiro deve ser construído hardcoded com as posições reais do jogo.
Use a convenção: linha 0 = topo, coluna 0 = esquerda.

**Cômodos e suas posições aproximadas no grid (áreas de células):**
```
Cozinha:          linhas 1–6,   colunas 1–6
Sala de Música:   linhas 1–6,   colunas 9–14
Jardim de Inverno:linhas 1–6,   colunas 18–23
Sala de Jantar:   linhas 9–14,  colunas 1–6
Salão de Jogos:   linhas 9–14,  colunas 9–14  ← INACESSÍVEL (centro)
Biblioteca:       linhas 9–14,  colunas 18–23
Sala de Estar:    linhas 17–22, colunas 1–6
Entrada:          linhas 17–22, colunas 9–14
Escritório:       linhas 17–22, colunas 18–23
```

Células dentro de um cômodo: `transitavel = false`, `nomeComodo = nome do cômodo`.
Células de corredor: `transitavel = true`, `nomeComodo = null`.
Células fora do tabuleiro (cantos arredondados): `transitavel = false`, `nomeComodo = null`.

**Portas** (abertura na parede que liga corredor ao cômodo):
Cada cômodo tem 1 ou 2 portas. Uma porta é uma célula do corredor adjacente
à parede do cômodo — é por ela que o pião entra/sai.
Defina pelo menos 1 porta por cômodo para esta iteração.

**Passagens Secretas** (cantos opostos da mansão — item do manual):
```
Cozinha       ↔  Escritório
Sala de Música ↔  Sala de Estar
```

**Posições iniciais dos piões** (células do corredor próximas à borda):
```
Srta. Scarlet:    borda leste,  entre Sala de Jantar e Biblioteca
Coronel Mustard:  borda sul,    abaixo da Sala de Jantar
Sra. White:       borda sul,    abaixo do Escritório
Reverendo Green:  borda norte,  acima do Jardim de Inverno
Sra. Peacock:     borda oeste,  entre Cozinha e Sala de Estar
Professor Plum:   borda norte,  acima da Sala de Música
```

**Coleções obrigatórias no Tabuleiro:**
- `Casa[][] grid` — acesso por linha/coluna em O(1).
- `HashMap<Integer, Casa> casasPorId` — busca por id em O(1).
- `HashMap<String, Comodo> comodos` — busca por nome em O(1).

**Métodos obrigatórios:**
```java
Casa getCasaPorId(int id)
Comodo getComodoPorNome(String nome)
Casa getPosicaoInicial(int indiceJogador)  // índice 0=Scarlet, 1=Mustard, ...
List<Integer> mapearCasasAlcancaveis(Casa origem, int passos)
int getLinhas()
int getColunas()
```

**Algoritmo de mapearCasasAlcancaveis — REGRAS EXATAS DO MANUAL:**

O manual diz:
- O pião se move exatamente N casas (soma dos 2 dados).
- Pode mudar de direção a cada passo (cima, baixo, esquerda, direita — nunca diagonal).
- **Não pode passar pelo mesmo espaço mais de uma vez na mesma jogada.**
- Não pode entrar, passar ou parar em célula ocupada por outro pião.
- Ao entrar num cômodo pela porta, para imediatamente (não conta o passo da porta).

Isso significa que o BFS simples de distância mínima está ERRADO para o Clue.
Use **DFS recursivo com conjunto de visitados por caminho** (não global):

```
função alcancaveis(casaAtual, passosRestantes, visitadosNesteCaminho):
    se passosRestantes == 0:
        adicionar casaAtual ao resultado
        retornar
    para cada vizinha ortogonal de casaAtual:
        se vizinha é transitável
        e vizinha não está em visitadosNesteCaminho:
            visitadosNesteCaminho.add(vizinha)
            alcancaveis(vizinha, passosRestantes - 1, visitadosNesteCaminho)
            visitadosNesteCaminho.remove(vizinha)
```

A origem **nunca** aparece no resultado (jogador é obrigado a se mover).
Casas não-transitáveis nunca aparecem no resultado.
Use `HashSet<Integer>` para o conjunto de visitados.
Use `HashSet<Integer>` para o conjunto de resultados (sem duplicatas).

### Pino.java
- Atributos privados: `String cor`, `Casa casaAtual`
- `getCasaAtual()`, `setCasaAtual(Casa c)`

### Jogo.java — a Façade pública

**Atenção:** o professor pede Singleton+Façade **apenas na 3ª iteração**.
Nesta 1ª iteração, `Jogo` é apenas uma Façade (construtor público normal).
Deixe um comentário explicando isso para não confundir o corretor.

**Atributos privados:**
```java
private final Tabuleiro tabuleiro;
private final Baralho baralho;
private final Dados dados;
private final List<Jogador> jogadores;
private final Envelope envelope;
private int indiceJogadorAtual;
private int[] ultimoLancamento;   // null antes do lançamento da rodada
```

**Construtor:** `public Jogo(List<String> nomesJogadores)`
- Valida: entre 3 e 6 jogadores. Lança `IllegalArgumentException` se inválido.
- Cria os jogadores com as cores na ordem:
  `Vermelho (Scarlet), Amarelo (Mustard), Branco (White),
   Verde (Green), Violeta (Peacock), Azul (Plum)`
- Busca no Tabuleiro a posição inicial de cada personagem.
- Executa preparação: `sortearSolucao()` → `distribuir()`.
- `indiceJogadorAtual = 0` (Scarlet é sempre a primeira).

**API pública obrigatória:**
```java
// Etapas de uma jogada
public int[]         lancarDados()
public void          definirDados(int d1, int d2)    // para testes manuais
public List<Integer> mapearCasas(int[] valoresDados)
public void          deslocarPiao(int idCasa)
public void          proximoJogador()

// Consultas para futuro Controller/View
public int           getNumeroJogadores()
public String        getNomeJogadorAtual()
public String        getCorJogadorAtual()
public int           getIdCasaJogadorAtual()
public boolean       jogadorEstaEmComodo()
public String        getNomeComodoAtual()           // null se estiver no corredor
public List<String>  getCartasJogadorAtual()        // Strings, nunca objetos Carta
public List<String>  getFolhaDeNotasJogadorAtual()
public void          riscarDaFolha(String item)
public boolean       isCasaTransitavel(int id)
public int           getLinhasTabuleiro()
public int           getColunasTabuleiro()
public boolean       temPassagemSecreta()
public void          usarPassagemSecreta()

// Acusação
public boolean       verificarAcusacao(String personagem, String arma, String local)
```

**Comportamento de `deslocarPiao`:**
- Lança `IllegalStateException` se `ultimoLancamento == null`.
- Lança `IllegalArgumentException` se `idCasa` não está na lista de alcançáveis.
- Move o pino. Zera `ultimoLancamento`.

**Comportamento de `usarPassagemSecreta`:**
- Lança `IllegalStateException` se o jogador não está em cômodo com passagem.
- Teleporta o pino para a `casaRepresentativa` do cômodo destino.

**Retorno de `getCartasJogadorAtual()`:**
- Retorna `List<String>` com `carta.getNome() + " (" + carta.getTipo() + ")"`.
- Nunca expõe objetos `Carta` fora do pacote.

### app/Main.java — demonstração
Crie um `main` simples que:
1. Cria um `Jogo` com 4 jogadores.
2. Imprime quem é o jogador atual e suas cartas.
3. Lança os dados, imprime o resultado e as casas alcançáveis.
4. Desloca o pião para a primeira casa da lista.
5. Chama `proximoJogador()` e repete para o próximo.

O arquivo **não pode importar** nada de `model` além de `model.Jogo`.
Se importar outra classe do pacote, o compilador rejeita — e isso é o correto.

---

## Testes JUnit 4 (OBRIGATÓRIO — Cap. 18)

Crie os 5 arquivos abaixo em `test/model/`. Todos os testes ficam no
**package model** para poder acessar as classes package-private.

### Regras obrigatórias para todos os testes:
- `@Test(timeout = DEFAULT_TIMEOUT)` em **todos** os métodos.
- `private static final int DEFAULT_TIMEOUT = 2000;` em cada classe.
- `assertEquals("mensagem explicativa", esperado, real)` — esperado à **esquerda**.
- Nome do método de teste **longo e descritivo** (o que ele verifica).
- **Uma assertiva por método** sempre que possível.
- Usar métodos `helper` privados para evitar repetição de código.

### BaralhoTest.java — testa as regras 4, 5 e 6 da Preparação:
```
- envelopeDeveTerUmaCartaDeCadaTipo
- envelopeNaoDeveConterCartasRepetidas
- distribuirDeveEntregarExatamente18CartasNoTotal
- distribuicaoDeveSerEquilibrada (diferença máx de 1 carta entre jogadores)
- naoDevemHaverCartasDuplicadasEntreEnvelopeEMaos
- distribuicaoComTresJogadoresEntrega6CartasPorJogador
- distribuicaoComQuatroJogadoresEntregaCartasCorretas
```

### DadosTest.java:
```
- lancarDadosDeveRetornarArrayDeDoisElementos
- cadaValorDeveEstarEntre1e6 (repetir 1000 vezes)
- dadosComMesmoSeedDevemProduzirMesmaSequencia
- somaDosDadosDeveEstarEntre2e12
```

### EnvelopeTest.java:
```
- acusacaoCorretaDeveRetornarVerdadeiro
- acusacaoComPersonagemErradoDeveRetornarFalso
- acusacaoComArmaErradaDeveRetornarFalso
- acusacaoComLocalErradoDeveRetornarFalso
- construtorDeveRejeitarPersonagemComTipoErrado (expected = IllegalArgumentException)
- construtorDeveRejeitarArmaComTipoErrado (expected = IllegalArgumentException)
- construtorDeveRejeitarLocalComTipoErrado (expected = IllegalArgumentException)
```

### TabuleiroTest.java — testa mapearCasasAlcancaveis:
```
- origemNuncaDeveEstarNaListaDeAlcancaveis
- casasNaoTransitaveisNuncaDevemAparecerNoResultado
- com0PassosResultadoDeveSerListaVazia
- com1PassoAPartirDoCantoDiretoDeveHaverCasasAlcancaveis
- casasAlcancaveisNaoDevemConterDuplicatas
- passosExatosDevemSerRespeitados (verificar que não retorna casas a menos passos)
- posicoesIniciaisDos6PersonagensSaoTodasDistintas
```

### JogoTest.java — testa o fluxo completo:
```
- jogoComMenosDe3JogadoresDeveLancarIllegalArgumentException
- jogoComMaisDe6JogadoresDeveLancarIllegalArgumentException
- jogoNovoDeveTer4JogadoresQuandoCriadoCom4Nomes
- scarlettDeveSerSempreAPrimeiraAJogar
- totalDeCartasDistribuidasDeveSer18
- lancarDadosDeveRetornarDoisValoresEntre1e6
- mapearCasasDeveRetornarListaNaoVazia
- deslocarPiaoDeveMoverJogadorParaCasaValida
- deslocarPiaoSemLancarDadosDeveLancarIllegalStateException
- deslocarPiaoParaCasaInvalidaDeveLancarIllegalArgumentException
- proximoJogadorDeveAvancarEmOrdemCircular
- verificarAcusacaoCorretaDeveRetornarVerdadeiro
- verificarAcusacaoErradaDeveRetornarFalso
- envelopeMaisTodasAsMaosSomam21CartasDistintas
```

---

## Convenções obrigatórias do professor (seção 18 do contexto)

- Variáveis de instância sempre `private`.
- Separar seções com `// variaveis` e `// métodos` dentro de cada classe.
- Coleções sempre tipadas: `List<Carta>`, `Map<Integer, Casa>`.
- `equals()` para comparar Strings, **nunca** `==`.
- `instanceof` antes de qualquer cast.
- Constantes com `final` (nada de literais mágicos soltos).
- `import java.util.*` quando usar coleções do Cap. 15.
- Getters de coleções retornam `Collections.unmodifiableList(...)`.
- Comentário Javadoc `/** ... */` na classe e nos métodos públicos.

---

## Ordem de implementação sugerida

1. `Carta.java` e `Carta.Tipo`
2. `Envelope.java`
3. `Baralho.java`
4. `Jogador.java` e `Pino.java`
5. `Dados.java`
6. `Casa.java` e `Comodo.java`
7. `Tabuleiro.java` (o mais trabalhoso — comece pelo grid, depois portas e passagens)
8. `Jogo.java`
9. `app/Main.java`
10. Testes JUnit (escreva cada teste logo após a classe correspondente)

Confirme que o compilador não aponta erros ao terminar cada classe antes
de passar para a próxima.
