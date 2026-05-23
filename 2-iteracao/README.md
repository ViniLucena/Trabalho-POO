# Clue — INF1636, 2ª Iteração

Implementação da interface gráfica do jogo Clue em Java Swing + Java2D, sobre
o Model evoluído da 1ª iteração. A arquitetura MVC é mantida e respeita as
restrições do enunciado da 2ª iteração.

## Estrutura

```
2-iteracao/
├── src/
│   ├── model/         ← Model. Apenas Jogo é pública (Fachada).
│   │   ├── Jogo.java           ← fachada do subsistema
│   │   ├── Tabuleiro.java      ← grid 25x24, cômodos, portas, passagens
│   │   ├── Casa.java
│   │   ├── Comodo.java
│   │   ├── TipoCasa.java       (enum CORREDOR/COMODO/PORTA/BLOQUEADA)
│   │   ├── Carta.java          (com id para busca O(1) de imagens)
│   │   ├── Baralho.java
│   │   ├── Envelope.java
│   │   ├── Jogador.java
│   │   ├── Pino.java
│   │   └── Dados.java
│   ├── view/          ← View. Tudo é desenhado por drawImage().
│   │   ├── JanelaPrincipal.java
│   │   ├── PainelTabuleiro.java
│   │   ├── PainelLateral.java
│   │   └── ResourceLoader.java
│   ├── controller/    ← Controller. Listeners em classes anônimas.
│   │   └── ControladorJogo.java
│   └── app/
│       ├── Main.java          ← ponto de entrada (Swing EDT)
│       └── SmokeTest.java     ← teste sem GUI
├── resources/
│   └── images/        ← imagens oficiais do EAD entram aqui
└── bin/               ← saída de compilação
```

## Como compilar e rodar

A partir da pasta `2-iteracao/`, no Windows (PowerShell ou cmd) ou em Bash:

```bash
mkdir -p bin
javac -d bin -encoding UTF-8 $(find src -name "*.java")
java -cp bin app.Main
```

Para o teste sem janela:

```bash
java -Djava.awt.headless=true -cp bin app.SmokeTest
```

## Como o trabalho atende ao enunciado

| Exigência do PDF | Onde está |
|---|---|
| Tabuleiro/peões/cartas desenhados **só com `drawImage()`** | `PainelTabuleiro.paintComponent` (linha do `g.drawImage(...)` para o tabuleiro e para cada pino); `PainelLateral.PainelDados.paintComponent` (dados). |
| Componentes Swing permitidos para botões/menus/dados | `JButton` em `PainelLateral` (Lançar Dados, Passagem Secreta, Próximo Jogador) e dois `JComboBox` para definir dados manualmente. |
| Janela máxima de **1400×1050 px** | `JanelaPrincipal` faz `Math.min(d.width, 1400)` e `Math.min(d.height, 1050)`. |
| Cor do jogador da vez sinalizada na área dos dados (Figura 4) | `PainelLateral.PainelDados.paintComponent` pinta o fundo com `ResourceLoader.corPino(...)`. |
| Dados exibidos por imagens (1–6) | `ResourceLoader.getDado(valor)` retorna o `BufferedImage`; placeholders são gerados se as imagens oficiais ainda não estiverem em `resources/images/`. |
| Definição manual dos dados (sugestão do enunciado) | Dois `JComboBox` (1–6) + botão "Usar valores acima" em `PainelLateral`. |
| Movimento por clique no tabuleiro | `ControladorJogo` instala um `MouseAdapter` em `PainelTabuleiro`; a tentativa passa por `Jogo.tentarMover(...)`. Em caso de movimento inválido, nada acontece — sem mensagem, sem troca de jogador (linha "Conforme exigência do enunciado..."). |
| Passagens secretas | `Comodo.setConexaoSecreta`, ligadas no `construirPassagensSecretas` de `Tabuleiro`. Botão "Passagem Secreta" só fica habilitado quando o pino está num cômodo conectado. |
| Cartas com identificador para acesso O(1) à imagem | `Carta.id` + `ResourceLoader.cartas` (`HashMap<Integer, BufferedImage>`). |
| **Proibição de expressões lambda em eventos** | Todos os listeners em `ControladorJogo` são `ActionListener`/`MouseAdapter` **anônimos** (estilo do Cap. 12). A compilação gera os arquivos `ControladorJogo$1.class` … `$5.class`, evidenciando classes anônimas. |
| Itens dispensados na 2ª iteração (janelas iniciais, cartas, bloco de notas) | Não foram implementados — apenas a janela do tabuleiro. |

## Sobre as imagens

O enunciado diz que as imagens (tabuleiro, dados, cartas, pinos) serão
disponibilizadas no EAD. Para que o jogo já seja executável sem depender
do download, o `ResourceLoader`:

1. tenta carregar `resources/images/<nome>.png`;
2. quando o arquivo não existe, gera um `BufferedImage` placeholder em runtime
   com aparência razoável (cores por cômodo, pontos do dado, pino circular
   colorido).

A renderização final passa sempre por `Graphics2D.drawImage(...)`, então
substituir os placeholders pelos arquivos oficiais é apenas copiar os PNGs
para `resources/images/` com os nomes esperados:

```
tabuleiro.png
dado1.png ... dado6.png
pino_vermelho.png, pino_amarelo.png, pino_branco.png,
pino_verde.png, pino_azul.png, pino_violeta.png
```

## Conceitos da disciplina aplicados

- **Encapsulamento / pacotes** (Cap. 8): a View só conhece `Jogo`. Tudo o mais
  é package-private.
- **Herança e polimorfismo** (Cap. 4–5): `PainelTabuleiro` e `PainelLateral`
  estendem `JPanel` e sobrescrevem `paintComponent`; `MouseAdapter` (classe
  abstrata adapter) é estendida via classe anônima.
- **Modificadores de acesso**: `public` reservado para a fachada e a API
  consumida pela View/Controller.
- **Coleções e Generics** (Caps. 14–15): `Map<Carta.Tipo, List<Carta>>`,
  `HashMap<Integer, BufferedImage>`, `Set<Integer>`, etc.
- **Composição vs. Herança** (Cap. 7): `Jogo` *contém* `Tabuleiro`, `Baralho`,
  `Dados` e a lista de `Jogador`; herança só onde "É-UM" se aplica (frames).
- **Design Patterns** (Cap. 17): a fachada `Jogo` já segue o espírito do
  padrão **Facade**. O padrão Singleton formal foi mantido para a 3ª
  iteração, conforme cronograma do enunciado.
- **Tratamento de eventos** (Cap. 12): listeners em classes anônimas, sem
  uso de expressões lambda.

## O que vem na 3ª iteração

- Janelas iniciais (escolha "Novo jogo / Continuar" + escolha de personagens).
- Janelas de cartas do jogador e de bloco de notas.
- Funcionalidade de palpite e acusação.
- Persistência (salvar / continuar partida).
- Aplicação formal dos padrões Singleton e Façade no Model.
