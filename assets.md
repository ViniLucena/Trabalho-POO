# Prompt — Integração dos Assets no Código

A pasta `assets/` já existe na raiz do projeto com a seguinte estrutura:

```
assets/
  Tabuleiros/
    Tabuleiro-Clue-A.jpg
    Tabuleiro-Clue-B.jpg
    Tabuleiro-Clue-C.jpg
    Tabuleiro-Original.JPG
    dado1.jpg
    dado2.jpg
    dado3.jpg
    dado4.jpg
    dado5.jpg
    dado6.jpg
  Suspeitos/
    Scarlet.jpg
    Mustard.jpg
    White.jpg
    Green.jpg
    Peacock.jpg
    Plum.jpg
  Comodos/
    Cozinha.jpg
    SalaDeMusica.jpg
    JardimInverno.jpg
    SalaDeJantar.jpg
    SalaoDeJogos.jpg
    Biblioteca.jpg
    SalaDeEstar.jpg
    Entrada.jpg
    Escritorio.jpg
  Armas/
    Corda.jpg
    Cano.jpg
    Faca.jpg
    ChaveInglesa.jpg
    Castical.jpg
    Revolver.jpg
```

NÃO mova, renomeie ou copie nenhum arquivo. Apenas atualize os caminhos
no código Java para bater com essa estrutura exata.

---

## O que atualizar em cada classe

### PainelTabuleiro.java

Tabuleiro — usar `Tabuleiro-Clue-A.jpg`:
```java
imgTabuleiro = ImageIO.read(new File("assets/Tabuleiros/Tabuleiro-Clue-A.jpg"));
```

Dados — índice 0 = dado1, índice 1 = dado2 etc.:
```java
for (int i = 0; i < 6; i++) {
    imgDados[i] = ImageIO.read(
        new File("assets/Tabuleiros/dado" + (i + 1) + ".jpg"));
}
```

Piões — cada cor mapeia para o arquivo do suspeito correspondente:
```java
imgPiaos.put("Vermelho", ImageIO.read(new File("assets/Suspeitos/Scarlet.jpg")));
imgPiaos.put("Amarelo",  ImageIO.read(new File("assets/Suspeitos/Mustard.jpg")));
imgPiaos.put("Branco",   ImageIO.read(new File("assets/Suspeitos/White.jpg")));
imgPiaos.put("Verde",    ImageIO.read(new File("assets/Suspeitos/Green.jpg")));
imgPiaos.put("Violeta",  ImageIO.read(new File("assets/Suspeitos/Peacock.jpg")));
imgPiaos.put("Azul",     ImageIO.read(new File("assets/Suspeitos/Plum.jpg")));
```

Todo `ImageIO.read()` deve estar dentro de `try/catch(IOException e)`.
Se falhar, atribua `null` — o fallback de círculo colorido já está implementado.

### JanelaCartas.java

Mapeie o nome exato da carta (como retornado por `jogo.getCartasJogadorAtual()`)
para o caminho do arquivo:

```java
// Suspeitos
caminhosCarta.put("Srta. Scarlet",   "assets/Suspeitos/Scarlet.jpg");
caminhosCarta.put("Coronel Mustard", "assets/Suspeitos/Mustard.jpg");
caminhosCarta.put("Sra. White",      "assets/Suspeitos/White.jpg");
caminhosCarta.put("Reverendo Green", "assets/Suspeitos/Green.jpg");
caminhosCarta.put("Sra. Peacock",    "assets/Suspeitos/Peacock.jpg");
caminhosCarta.put("Professor Plum",  "assets/Suspeitos/Plum.jpg");

// Armas
caminhosCarta.put("Corda",          "assets/Armas/Corda.jpg");
caminhosCarta.put("Cano de Chumbo", "assets/Armas/Cano.jpg");
caminhosCarta.put("Faca",           "assets/Armas/Faca.jpg");
caminhosCarta.put("Chave Inglesa",  "assets/Armas/ChaveInglesa.jpg");
caminhosCarta.put("Castiçal",       "assets/Armas/Castical.jpg");
caminhosCarta.put("Revólver",       "assets/Armas/Revolver.jpg");

// Cômodos
caminhosCarta.put("Cozinha",           "assets/Comodos/Cozinha.jpg");
caminhosCarta.put("Sala de Música",    "assets/Comodos/SalaDeMusica.jpg");
caminhosCarta.put("Jardim de Inverno", "assets/Comodos/JardimInverno.jpg");
caminhosCarta.put("Sala de Jantar",    "assets/Comodos/SalaDeJantar.jpg");
caminhosCarta.put("Salão de Jogos",    "assets/Comodos/SalaoDeJogos.jpg");
caminhosCarta.put("Biblioteca",        "assets/Comodos/Biblioteca.jpg");
caminhosCarta.put("Sala de Estar",     "assets/Comodos/SalaDeEstar.jpg");
caminhosCarta.put("Entrada",           "assets/Comodos/Entrada.jpg");
caminhosCarta.put("Escritório",        "assets/Comodos/Escritorio.jpg");
```

---

## Verificação final

Após as alterações, compile e execute. Confirme visualmente:
- Tabuleiro aparece no `PainelTabuleiro`.
- Piões aparecem sobre o tabuleiro nas posições iniciais.
- Dados aparecem ao clicar "Jogar Dados".
- Janela de cartas exibe as imagens ao clicar "Mostrar Cartas".

Se alguma imagem não carregar, o fallback deve entrar em ação
silenciosamente — sem exceção estourar na tela.