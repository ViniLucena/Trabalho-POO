# Clue — INF1636, 3ª Iteração

Conclusão do trabalho: integração completa da View com o Model
para que TODAS as regras possam ser acionadas a partir da
interface gráfica. A 3ª iteração foca em três pontos:

1. **Padrões de projeto** (Capítulo 17):
   - **Singleton** — `Jogo` tem construtor privado; a partida é
     obtida via `Jogo.novaPartida(...)` / `Jogo.getInstancia()`.
   - **Façade** — `Jogo` continua sendo a única classe pública do
     pacote `model`.
   - **Observer** — interfaces `Observador` e `Observado` no
     pacote `model`. `Jogo` implementa `Observado` e notifica os
     painéis (que implementam `Observador`) a cada mudança de
     estado: lançamento de dados, movimento, palpite, acusação,
     etc.
2. **Funcionalidades novas** da iteração 3:
   - **Janelas iniciais** (Figuras 1 e 2 do enunciado):
     `JanelaInicial` ("Novo Jogo" / "Continuar") e
     `JanelaPersonagens` (escolha dos 3 a 6 personagens).
   - **Janela de cartas** do jogador (`JanelaCartas`,
     `drawImage` para cada carta).
   - **Bloco de notas** com `JCheckBox` por categoria
     (`JanelaBlocoNotas`).
   - **Palpite** e **Acusação** (`JanelaPalpite`,
     `JanelaAcusacao`).
   - **Salvar / Continuar** uma partida em arquivo de texto.
3. **Consolidação do Controller** — `ControladorJogo` reúne
   todos os listeners (sem expressões lambda).

## Estrutura

```
3-iteracao/
├── src/
│   ├── model/             ← Apenas Jogo, Observado e Observador são pública.
│   │   ├── Jogo.java          (Façade + Singleton + Observado)
│   │   ├── Observador.java    (interface)
│   │   ├── Observado.java     (interface)
│   │   ├── Tabuleiro.java
│   │   ├── Casa.java
│   │   ├── Comodo.java
│   │   ├── TipoCasa.java
│   │   ├── Carta.java
│   │   ├── Baralho.java
│   │   ├── Envelope.java
│   │   ├── Jogador.java
│   │   ├── BlocoNotas.java
│   │   ├── Pino.java
│   │   └── Dados.java
│   ├── view/              ← Tudo via drawImage(); só JFrame/JButton/...
│   │   ├── JanelaInicial.java       (Novo Jogo / Continuar)
│   │   ├── JanelaPersonagens.java   (Seleção de 3–6 personagens)
│   │   ├── JanelaPrincipal.java     (Tabuleiro + Lateral)
│   │   ├── JanelaCartas.java        (cartas do jogador, drawImage)
│   │   ├── JanelaBlocoNotas.java    (checkboxes por categoria)
│   │   ├── JanelaPalpite.java       (JDialog modal)
│   │   ├── JanelaAcusacao.java      (JDialog modal)
│   │   ├── PainelTabuleiro.java     (drawImage do tabuleiro/pinos)
│   │   ├── PainelLateral.java       (dados + botões)
│   │   └── ResourceLoader.java
│   ├── controller/
│   │   └── ControladorJogo.java     (listeners em classes anônimas)
│   └── app/
│       ├── Main.java                (encadeia as três janelas)
│       └── SmokeTest.java           (teste headless)
├── resources/images/      ← imagens oficiais do EAD
└── bin/                   ← saída de compilação
```

## Como compilar e rodar

A partir da pasta `3-iteracao/`:

```bash
mkdir -p bin
find src -name "*.java" -print0 | xargs -0 javac -d bin -encoding UTF-8
java -cp bin app.Main
```

Para o teste sem janela:

```bash
java -Djava.awt.headless=true -cp bin app.SmokeTest
```

## Padrões de projeto, em detalhe

### Singleton

`Jogo` tem três métodos estáticos de fábrica:

```java
public static Jogo novaPartida(int numJogadores);     // 3..6
public static Jogo novaPartida(boolean[] selecionados);
public static Jogo carregarPartida(File arquivo);
public static Jogo getInstancia();                    // pode ser null
```

O construtor é `private`. A View nunca instancia `Jogo`
diretamente — `Main` chama `novaPartida(...)` e `getInstancia()`
fica disponível para qualquer parte do código.

### Façade

Apenas três tipos do pacote `model` são públicos:
`Jogo`, `Observado` e `Observador`. Casa, Carta, Comodo,
Tabuleiro etc. são package-private e a View não os enxerga.

### Observer

```
Observado  (interface)
Observador (interface)
                 ↑ implementa
PainelTabuleiro, PainelLateral, JanelaCartas, JanelaBlocoNotas
```

Toda mudança de estado em `Jogo` chama
`notificarObservadores()`. Os painéis chamam `repaint()` /
re-renderizam combos e checkboxes. O Controller, portanto,
não precisa orquestrar "refresh" manualmente.

## Mapeamento das exigências do enunciado

| Exigência | Onde |
|---|---|
| MVC em pacotes distintos | `model/`, `view/`, `controller/` |
| Singleton + Façade + Observer | `model/Jogo.java`, `model/Observado.java`, `model/Observador.java` |
| Modelos antigos do tabuleiro / cartas / dados continuam funcionando | classes do `model/` |
| Janelas iniciais (Figs. 1 e 2) | `view/JanelaInicial.java`, `view/JanelaPersonagens.java` |
| Tabuleiro com `drawImage()` apenas | `view/PainelTabuleiro.java` |
| Cor do jogador da vez na área dos dados (Fig. 4) | `view/PainelLateral.PainelDados.paintComponent` |
| Dados por imagens (1..6) | `resources/images/Tabuleiros/dado1.jpg`..`dado6.jpg` |
| Definição manual dos dados | dois `JComboBox` + botão "Usar valores acima" |
| Movimento por clique | `MouseAdapter` em `PainelTabuleiro` → `Jogo.tentarMover(...)` |
| Passagens secretas | `Comodo.setConexaoSecreta` + botão "Passagem Secreta" |
| Cartas indexadas por id em `HashMap<Integer, BufferedImage>` | `view/ResourceLoader.cartas` |
| Janela de cartas (Fig. 5) com `drawImage()` | `view/JanelaCartas.java` |
| Janela de bloco de notas (Fig. 6) com `JCheckBox` | `view/JanelaBlocoNotas.java` |
| Palpite | `Jogo.fazerPalpite(...)` + `view/JanelaPalpite.java` |
| Acusação | `Jogo.fazerAcusacao(...)` + `view/JanelaAcusacao.java` |
| Save / Continuar | `Jogo.salvar(File)` + `Jogo.carregarPartida(File)` |
| Sem expressões lambda em listeners | classes anônimas em `controller/ControladorJogo` e nas janelas |
| Janela ≤ 1400 × 1050 px | `JanelaPrincipal` faz `Math.min(...)` |

## Sobre as imagens

As imagens oficiais do EAD ficam em `resources/images/`. Se um
arquivo não estiver presente, o `ResourceLoader` gera um
placeholder em runtime — assim o jogo continua executável.
Os nomes esperados são exatamente os do `.zip` distribuído.
