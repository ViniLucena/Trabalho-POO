# Prompt — 2ª Iteração: Clue (INF1636 – PUC-Rio)

Leia o @contexto_POO.md antes de escrever qualquer linha de código.
Siga TODAS as convenções da seção 18 desse arquivo durante toda a implementação.

## Contexto importante — continuidade entre iterações

Este projeto é desenvolvido em 4 iterações incrementais. A 1ª iteração já está
implementada e funcionando — o pacote `model/` com todas as classes e testes
JUnit está pronto. Isso significa que:

- O `model/` já está correto e testado. NÃO reescreva nenhuma classe dele.
- Se precisar adicionar um método à Façade `Jogo.java`, adicione sem quebrar
  os métodos e testes já existentes.
- Todo investimento feito agora em uma boa arquitetura de View economiza
  trabalho nas iterações 3 e 4. Por isso, implemente as classes de View já
  pensando no que vem a seguir:
  - 3ª iteração: Observer obrigatório, Singleton+Façade em Jogo, Controller,
    palpite e acusação, integração completa.
  - 4ª iteração: salvamento e recuperação de partida.
- Cada classe de View deve ser desenhada para receber mais funcionalidade
  sem precisar ser reescrita. Prefira métodos pequenos e coesos.

---

## O que você vai implementar nesta iteração

A interface gráfica do jogo Clue usando **Java Swing** e **Java 2D**.
Ao final desta iteração o jogo deve ser jogável: piões se movem no tabuleiro
de acordo com os dados, respeitando todas as regras de movimentação.

As telas iniciais (TelaInicio e TelaPersonagens) serão implementadas de forma
**mockada** nesta iteração — estrutura completa, visual funcional, botões
navegando entre janelas — mas sem polish final. O objetivo é ter o fluxo
completo funcionando do início ao fim, mesmo que simples.

---

## Regras absolutas da interface gráfica (penalidades no enunciado)

- O tabuleiro, os piões, os dados e as cartas devem ser exibidos
  **EXCLUSIVAMENTE** via `drawImage()` da classe `Graphics2D`.
  Usar `JPanel`, `JButton`, `JLabel` ou qualquer componente Swing para
  exibir essas imagens → **solução não aceita pelo professor**.
- **PROIBIDO** usar expressões lambda no tratamento de eventos Swing.
  Penalidade: **-2,0 na nota final**. Use classes anônimas ou implemente
  as interfaces de listener diretamente na classe com `implements`.
- **PROIBIDO** usar `JDialog` ou `JOptionPane` para mensagens durante o jogo.
  Mensagens devem aparecer no próprio painel do tabuleiro via `Graphics2D`.
- Dimensão máxima de qualquer janela: **1400 × 1050 pixels**.

---

## Estrutura de pastas (incremento sobre a 1ª iteração)

```
src/
  model/              ← já existe, NÃO modificar
  view/
    TelaInicio.java       ← mockada: fundo colorido + 2 botões funcionais
    TelaPersonagens.java  ← mockada: checkboxes + botão Jogar funcional
    TelaPrincipal.java    ← tela principal do jogo
    PainelTabuleiro.java  ← painel central com drawImage e MouseListener
    JanelaCartas.java     ← janela auxiliar de cartas
    JanelaNotas.java      ← janela auxiliar do bloco de notas
  app/
    Main.java             ← substitui o Main de demonstração da 1ª iteração

assets/
  tabuleiro.png
  piao_vermelho.png
  piao_amarelo.png
  piao_branco.png
  piao_verde.png
  piao_violeta.png
  piao_azul.png
  dado_1.png  dado_2.png  dado_3.png
  dado_4.png  dado_5.png  dado_6.png
```

**Sobre assets:** Carregue as imagens com `ImageIO.read()` uma única vez no
construtor. Nunca dentro de `paintComponent()`. Se o arquivo não existir,
desenhe um retângulo colorido como fallback — o jogo não pode travar por
imagem ausente.

---

## Janela 1 — Tela Inicial mockada (TelaInicio.java)

Classe: `public class TelaInicio extends JFrame`

Esta tela é mockada nesta iteração: não precisa ter a imagem oficial do Clue,
mas deve ter a estrutura correta para receber a imagem real nas próximas
iterações. Implemente assim:

- Um painel interno que sobrescreve `paintComponent(Graphics g)`.
  - Se `assets/tabuleiro_inicio.png` existir, exibe via `drawImage()`.
  - Se não existir, pinta o fundo de verde escuro e escreve "CLUE" no centro
    com `g2.drawString()` em fonte grande.
- Dois botões Swing sobrepostos: `"Novo Jogo"` e `"Continuar"`.
- `"Continuar"` começa **desabilitado** (`setEnabled(false)`) — 4ª iteração.
- Ao clicar `"Novo Jogo"`: fechar esta janela e abrir `TelaPersonagens`.
- Título: `"Clue - Início"`. Tamanho: 800 × 600.

```java
// Tratamento de evento — sem lambda:
btnNovoJogo.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        dispose();
        new TelaPersonagens().setVisible(true);
    }
});
```

---

## Janela 2 — Escolha de Personagens mockada (TelaPersonagens.java)

Classe: `public class TelaPersonagens extends JFrame`

Esta tela também é mockada nesta iteração: sem as imagens oficiais dos
personagens por enquanto, mas com toda a lógica de seleção funcionando.

- Um painel com fundo cinza escuro ou imagem se disponível.
- Para cada personagem, um `JCheckBox` com o nome. Os 6 personagens
  (nomes exatos do manual):
  `Srta. Scarlet`, `Coronel Mustard`, `Sra. White`,
  `Reverendo Green`, `Sra. Peacock`, `Professor Plum`
- Botão `"Jogar"`:
  - **Desabilitado** por padrão.
  - Habilitado somente quando entre 3 e 6 checkboxes estão marcados.
  - Ao clicar: fechar esta janela, criar `Jogo` com os nomes marcados,
    e abrir `TelaPrincipal` passando o `Jogo` como parâmetro.
- Título: `"Clue - Personagens"`. Tamanho: 600 × 400.
- Implementar `ItemListener` nos checkboxes sem lambda:

```java
chk.addItemListener(new ItemListener() {
    public void itemStateChanged(ItemEvent e) {
        atualizarBotaoJogar();
    }
});
```

---

## Janela 3 — Tabuleiro (TelaPrincipal.java + PainelTabuleiro.java)

### TelaPrincipal.java
Classe: `public class TelaPrincipal extends JFrame`

Recebe o objeto `Jogo` no construtor: `public TelaPrincipal(Jogo jogo)`

Layout: `BorderLayout` — `PainelTabuleiro` no `CENTER`, painel lateral no `EAST`.

Painel lateral (`JPanel` com `BoxLayout` ou `GridLayout`):
```
JLabel    lblJogadorAtual  → "Vez de: [nome] ([cor])"
JButton   btnJogarDados    → "Jogar Dados"
JButton   btnPassagem      → "Passagem Secreta"  (desabilitado por padrão)
JButton   btnMostrarCartas → "Mostrar Cartas"
JButton   btnBlocoNotas    → "Bloco de Notas"
JButton   btnPalpite       → "Palpite"   (desabilitado — 3ª iteração)
JButton   btnAcusar        → "Acusar"    (desabilitado — 3ª iteração)
JButton   btnSalvar        → "Salvar Jogo" (desabilitado — 4ª iteração)
JButton   btnProximo       → "Próximo"
JLabel                     → "Definir Dados:"
JComboBox<Integer> cbDado1 → valores 1 a 6
JComboBox<Integer> cbDado2 → valores 1 a 6
```

Lógica do botão "Jogar Dados":
```java
// Verificar se testador definiu os dados pelos comboboxes
Integer d1 = (Integer) cbDado1.getSelectedItem();
Integer d2 = (Integer) cbDado2.getSelectedItem();
int[] resultado;
if (d1 != null && d2 != null) {
    jogo.definirDados(d1, d2);
    resultado = new int[]{d1, d2};
} else {
    resultado = jogo.lancarDados();
}
painelTabuleiro.setUltimoLancamento(resultado);
painelTabuleiro.setCasasAlcancaveis(jogo.mapearCasas(resultado));
painelTabuleiro.repaint();
```

Lógica do botão "Próximo":
```java
jogo.proximoJogador();
lblJogadorAtual.setText("Vez de: " + jogo.getNomeJogadorAtual()
    + " (" + jogo.getCorJogadorAtual() + ")");
btnPassagem.setEnabled(jogo.temPassagemSecreta());
painelTabuleiro.setCasasAlcancaveis(null);
painelTabuleiro.setUltimoLancamento(null);
painelTabuleiro.repaint();
```

Título: `"Clue - Jogo"`. Tamanho: 1200 × 800 (dentro do limite de 1400×1050).

### PainelTabuleiro.java
Classe: `public class PainelTabuleiro extends JPanel implements MouseListener`

**Atributos privados:**
```java
private final Jogo jogo;
private BufferedImage imgTabuleiro;
private final HashMap<String, BufferedImage> imgPiaos;
private final BufferedImage[] imgDados;          // índice 0=dado1, 1=dado2
private List<Integer> casasAlcancaveis;          // null quando sem lançamento
private int[] ultimoLancamento;                  // null antes de jogar
private final int larguraCelula;
private final int alturaCelula;
```

**`paintComponent(Graphics g)`:**
1. `super.paintComponent(g)`
2. `Graphics2D g2 = (Graphics2D) g`
3. Desenha o tabuleiro: `g2.drawImage(imgTabuleiro, 0, 0, getWidth(), getHeight(), null)`
4. Desenha destaque semitransparente nas casas alcançáveis:
```java
if (casasAlcancaveis != null) {
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
    g2.setColor(Color.GREEN);
    for (int id : casasAlcancaveis) {
        int col   = id % jogo.getColunasTabuleiro();
        int linha = id / jogo.getColunasTabuleiro();
        g2.fillRect(col * larguraCelula, linha * alturaCelula,
                    larguraCelula, alturaCelula);
    }
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
}
```
5. Desenha os piões de todos os jogadores.
6. Desenha os dados no canto inferior direito.
7. Desenha borda colorida (10px) com a cor do jogador atual.

**`mouseClicked(MouseEvent e)`:**
```java
public void mouseClicked(MouseEvent e) {
    int col   = e.getX() / larguraCelula;
    int linha = e.getY() / alturaCelula;
    int id    = linha * jogo.getColunasTabuleiro() + col;
    try {
        jogo.deslocarPiao(id);
        casasAlcancaveis = null;
        ultimoLancamento = null;
        repaint();
    } catch (IllegalArgumentException ex) {
        // Casa inválida — ignora silenciosamente, sem mensagem
    } catch (IllegalStateException ex) {
        // Dados não lançados — ignora silenciosamente
    }
}
// Métodos vazios obrigatórios do MouseListener:
public void mousePressed(MouseEvent e)  { }
public void mouseReleased(MouseEvent e) { }
public void mouseEntered(MouseEvent e)  { }
public void mouseExited(MouseEvent e)   { }
```

**Centralização do pião na célula:**
```java
private void desenharPiao(Graphics2D g2, int idCasa, BufferedImage img) {
    int col   = idCasa % jogo.getColunasTabuleiro();
    int linha = idCasa / jogo.getColunasTabuleiro();
    int piaoW = larguraCelula  - 4;
    int piaoH = alturaCelula - 4;
    int px    = col   * larguraCelula  + 2;
    int py    = linha * alturaCelula + 2;
    g2.drawImage(img, px, py, piaoW, piaoH, null);
}
```

**Mapeamento cor → imagem (usar `HashMap<String, BufferedImage>`):**
```
"Vermelho" → assets/piao_vermelho.png
"Amarelo"  → assets/piao_amarelo.png
"Branco"   → assets/piao_branco.png
"Verde"    → assets/piao_verde.png
"Violeta"  → assets/piao_violeta.png
"Azul"     → assets/piao_azul.png
```

**Mapeamento cor → `java.awt.Color` (para borda e fallback):**
```java
private Color corParaColor(String cor) {
    switch (cor) {
        case "Vermelho": return Color.RED;
        case "Amarelo":  return Color.YELLOW;
        case "Branco":   return Color.WHITE;
        case "Verde":    return Color.GREEN;
        case "Violeta":  return new Color(128, 0, 128);
        case "Azul":     return Color.BLUE;
        default:         return Color.GRAY;
    }
}
```

---

## Janela das Cartas (JanelaCartas.java)

Classe: `public class JanelaCartas extends JFrame`

Recebe `Jogo` no construtor: `public JanelaCartas(Jogo jogo)`

- Chama `jogo.getCartasJogadorAtual()` para obter a lista de Strings.
- Exibe cada carta: se a imagem existir em `assets/`, usa `drawImage()`;
  se não existir, exibe o nome como texto num `JLabel`.
- Agrupa por tipo com `JLabel` de título: "Suspeitos", "Armas", "Cômodos".
- `setDefaultCloseOperation(DISPOSE_ON_CLOSE)`.
- Título: `"Clue - Cartas de " + jogo.getNomeJogadorAtual()`.

---

## Janela do Bloco de Notas (JanelaNotas.java)

Classe: `public class JanelaNotas extends JFrame`

Recebe `Jogo` no construtor: `public JanelaNotas(Jogo jogo)`

- Três colunas com `JCheckBox`: Suspeitos, Armas, Cômodos.
- Obtém a folha com `jogo.getFolhaDeNotasJogadorAtual()`.
- Obtém as cartas da mão com `jogo.getCartasJogadorAtual()`.
- Itens que estão na mão: marcados e **desabilitados**.
- Demais itens: desmarcados, habilitados (jogador pode marcar livremente).
- Ao marcar: `jogo.riscarDaFolha(nomeItem)`.
- `setDefaultCloseOperation(DISPOSE_ON_CLOSE)`.
- Título: `"Clue - Bloco de Notas de " + jogo.getNomeJogadorAtual()`.

---

## app/Main.java

```java
package app;

import view.TelaInicio;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TelaInicio().setVisible(true);
            }
        });
    }
}
```

`SwingUtilities.invokeLater` com classe anônima — sem lambda.

---

## Testes JUnit

O enunciado da 2ª iteração diz explicitamente:
**"Não será necessário escrever novos testes unitários."**

Mantenha todos os testes da 1ª iteração passando. Se algum quebrar por
mudança no Model, corrija o Model — não o teste.

---

## Convenções obrigatórias do professor (seção 18 do contexto)

- Variáveis de instância sempre `private`.
- Separar seções com `// variaveis` e `// métodos` dentro de cada classe.
- Coleções sempre tipadas: `HashMap<String, BufferedImage>`, `List<Integer>`.
- `equals()` para comparar Strings, **nunca** `==`.
- Constantes com `final`: `larguraCelula`, `alturaCelula`, tamanhos de janela.
- Comentário Javadoc `/** ... */` nas classes e métodos públicos.
- **Sem lambda em eventos Swing** — use classes anônimas ou `implements`.
- **Sem JDialog/JOptionPane** durante o jogo.
- Imagens carregadas no construtor, nunca no `paintComponent`.

---

## Ordem de implementação sugerida

1. `PainelTabuleiro.java` — só o desenho primeiro (tabuleiro + piões), sem clique.
2. `TelaPrincipal.java` — layout com sidebar. Teste visual básico.
3. Adicione `MouseListener` ao `PainelTabuleiro` e teste clique no terminal.
4. Conecte "Jogar Dados" → destaque casas → clique → mover pião.
5. Conecte "Próximo", "Passagem Secreta".
6. `JanelaCartas.java` e `JanelaNotas.java`.
7. `TelaInicio.java` mockada — fundo simples + 2 botões.
8. `TelaPersonagens.java` mockada — checkboxes + botão Jogar.
9. `app/Main.java` — conecte tudo pelo fluxo completo.

Confirme que o compilador não aponta erros ao terminar cada classe antes
de passar para a próxima.
