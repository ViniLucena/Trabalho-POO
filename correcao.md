# Prompt — Correção de Bugs (2ª Iteração)

Leia o @contexto_POO.md antes de qualquer alteração.
Corrija APENAS os bugs listados abaixo. Não refatore nem reescreva código
que não está relacionado aos bugs. Após cada correção, compile e confirme
que não há erros antes de passar para o próximo.

---

## Bug 1 — Dados jogados infinitamente (CRÍTICO)
**Arquivo:** `src/model/Tabuleiro.java` — método `dfs()`
**Causa:** o DFS não trata a entrada em cômodos pela porta. Quando o jogador
está num corredor e tenta explorar casas alcançáveis, ao encontrar uma célula
de porta (transitável mas adjacente ao interior de um cômodo que é
intransitável), o DFS entra na porta, não consegue avançar para dentro do
cômodo, faz backtrack, tenta de novo pela mesma porta, e fica em loop de
exploração excessiva. Isso gera um `resultado` com milhares de entradas e
trava a UI quando `painelTabuleiro.repaint()` tenta desenhar todos esses
destaques.

**Correção:** adicione um método auxiliar em `Tabuleiro` que identifica
se uma casa é porta de algum cômodo e qual cômodo:
```java
private String getComodoDaPorta(int idCasa) {
    for (Comodo comodo : comodos.values()) {
        if (comodo.getIdPortas().contains(idCasa)) {
            return comodo.getNome();
        }
    }
    return null;
}
```

Certifique-se que `Comodo` tem o método `getIdPortas()` retornando
`List<Integer>` e `getNome()` retornando `String`. Adicione-os se não
existirem.

Modifique o `dfs()` para tratar portas como destino final (entrar no cômodo
consome todos os passos restantes, independente de quantos sobravam):
```java
private void dfs(Casa atual, int passosRestantes,
                 HashSet<Integer> visitados, HashSet<Integer> resultado) {
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

        // Verifica se a vizinha é uma porta de cômodo
        String nomeComodo = getComodoDaPorta(vizinha.getId());
        if (nomeComodo != null) {
            // Entrar no cômodo termina o movimento — adiciona casaRepresentativa
            Comodo comodo = comodos.get(nomeComodo);
            resultado.add(comodo.getIdCasaRepresentativa());
        } else {
            // Corredor normal — continua DFS
            visitados.add(vizinha.getId());
            dfs(vizinha, passosRestantes - 1, visitados, resultado);
            visitados.remove(vizinha.getId());
        }
    }
}
```

---

## Bug 2 — Caminho das imagens errado (tabuleiro não aparece)
**Arquivo:** `src/view/PainelTabuleiro.java` e `src/view/JanelaCartas.java`
**Causa:** os caminhos `"assets/Tabuleiros/..."` são relativos ao working
directory da JVM. A pasta `assets/` está na raiz do repositório, um nível
acima da pasta `2-iteracao/`. Quando o Eclipse executa com working directory
`2-iteracao/`, o caminho correto é `"../assets/Tabuleiros/..."`.

**Correção:** substitua o prefixo `"assets/"` por `"../assets/"` em TODOS
os `carregarImagem(...)` de `PainelTabuleiro.java`:
```java
imgTabuleiro = carregarImagem("../assets/Tabuleiros/Tabuleiro-Clue-A.jpg");

imgPiaos.put("Vermelho", carregarImagem("../assets/Suspeitos/Scarlet.jpg"));
imgPiaos.put("Amarelo",  carregarImagem("../assets/Suspeitos/Mustard.jpg"));
imgPiaos.put("Branco",   carregarImagem("../assets/Suspeitos/White.jpg"));
imgPiaos.put("Verde",    carregarImagem("../assets/Suspeitos/Green.jpg"));
imgPiaos.put("Violeta",  carregarImagem("../assets/Suspeitos/Peacock.jpg"));
imgPiaos.put("Azul",     carregarImagem("../assets/Suspeitos/Plum.jpg"));

for (int i = 0; i < 6; i++) {
    imgDados[i] = carregarImagem("../assets/Tabuleiros/dado" + (i + 1) + ".jpg");
}
```

E em `JanelaCartas.java`, substitua também o prefixo de todos os caminhos
de cartas para `"../assets/..."`.

---

## Bug 3 — Botão "Jogar Dados" pode ser clicado múltiplas vezes
**Arquivo:** `src/view/TelaPrincipal.java`
**Causa:** não há bloqueio do botão após o primeiro clique, permitindo
sobrescrever `ultimoLancamento` várias vezes sem mover o pião.

**Correção:** transforme `btnJogarDados` em variável de instância (igual
a `btnPassagem`) e desabilite-o após cada lançamento. Reabilite apenas
no listener de `btnProximo`:

Adicione a variável de instância:
```java
private JButton btnJogarDados;
```

No listener de `btnJogarDados`:
```java
// última linha do actionPerformed:
btnJogarDados.setEnabled(false);
```

No listener de `btnProximo`:
```java
// após jogo.proximoJogador():
btnJogarDados.setEnabled(true);
```

Também desabilite após mover pelo clique no tabuleiro: passe `btnJogarDados`
como referência ao `PainelTabuleiro`, ou adicione um método em
`TelaPrincipal` chamado `onMovimentoConcluido()` que o `PainelTabuleiro`
chama após `jogo.deslocarPiao()` bem-sucedido. Opção mais simples: o botão
já estará desabilitado desde o lançamento, então basta que o "Próximo" o
reabilite. Confirme que esse fluxo funciona.

---

## Bug 4 — `JanelaNotas` não marca itens da mão corretamente
**Arquivo:** `src/view/JanelaNotas.java`
**Causa:** `jogo.getCartasJogadorAtual()` retorna Strings no formato
`"Corda (ARMA)"`, mas a comparação com os itens da folha usa o nome
simples `"Corda"`. A comparação nunca bate e nenhum item fica marcado.

**Correção:** extraia apenas o nome antes do `" ("` ao comparar:
```java
List<String> cartasFormatadas = jogo.getCartasJogadorAtual();
List<String> nomesDaMao = new ArrayList<String>();
for (String s : cartasFormatadas) {
    int idx = s.lastIndexOf(" (");
    if (idx >= 0) {
        nomesDaMao.add(s.substring(0, idx));
    } else {
        nomesDaMao.add(s);
    }
}
```

Use `nomesDaMao.contains(nomeItem)` para verificar se o item está na mão
do jogador ao criar cada `JCheckBox`.

---

## Bug 5 — Posições iniciais: verificar se são transitáveis
**Arquivo:** `src/model/Tabuleiro.java` — método `getPosicaoInicial()`
**Causa provável:** as células retornadas podem cair dentro de áreas de
cômodo ou em cantos não transitáveis dependendo do grid.

**Correção:** adicione temporariamente no `app/Main.java` (ou num teste)
a seguinte verificação e confirme que todas retornam `true`:
```java
for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
    int id = jogo.getIdCasaJogador(i);
    boolean ok = jogo.isCasaTransitavel(id);
    System.out.println("Jogador " + i + " id=" + id + " transitavel=" + ok);
}
```

Se alguma retornar `false`, ajuste a célula para a mais próxima que seja
transitável e esteja na borda correta do tabuleiro.

---

## Verificação final após todas as correções

Compile sem erros e confirme manualmente:
- [ ] Tabuleiro `Tabuleiro-Clue-A.jpg` aparece no painel.
- [ ] Piões aparecem nas posições iniciais corretas sobre o tabuleiro.
- [ ] "Jogar Dados" mostra destaque verde e desabilita o botão.
- [ ] Segundo clique em "Jogar Dados" não é possível (botão desabilitado).
- [ ] Clicar numa casa verde move o pião corretamente.
- [ ] Clicar numa casa não alcançável não faz nada (sem mensagem).
- [ ] "Próximo" muda o jogador, limpa destaque e reabilita "Jogar Dados".
- [ ] "Mostrar Cartas" exibe as imagens das cartas do jogador atual.
- [ ] "Bloco de Notas" abre com itens da mão marcados e desabilitados.
- [ ] "Passagem Secreta" só habilitado em cômodo com passagem.
- [ ] Todos os testes JUnit continuam passando.
