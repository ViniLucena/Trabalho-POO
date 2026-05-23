# Clue — INF1636, 1ª Iteração

Implementação inicial do componente Model do jogo Clue, em Java,
seguindo a arquitetura MVC.

## Estrutura

```
inf1636-clue/
├── src/
│   ├── model/        ← pacote Model (apenas Jogo é pública)
│   │   ├── Jogo.java
│   │   ├── Tabuleiro.java
│   │   ├── Casa.java
│   │   ├── Carta.java
│   │   ├── Envelope.java
│   │   ├── Baralho.java
│   │   ├── Jogador.java
│   │   ├── Pino.java
│   │   └── Dados.java
│   └── app/
│       └── Main.java     ← demonstração de uso da API pública
├── test/
│   └── model/            ← testes JUnit 4 (mesmo pacote)
│       ├── DadosTest.java
│       ├── BaralhoTest.java
│       ├── EnvelopeTest.java
│       ├── TabuleiroTest.java
│       └── JogoTest.java
└── lib/
    ├── junit-4.13.2.jar
    └── hamcrest-core-1.3.jar
```

## Como compilar e rodar

A partir da pasta `inf1636-clue/`:

```bash
# 1. Compilar o código de produção
mkdir -p bin
find src -name "*.java" | xargs javac -d bin -encoding UTF-8

# 2. Executar o demo
java -cp bin app.Main

# 3. Compilar os testes
mkdir -p bin-test
find test -name "*.java" | xargs javac -d bin-test \
    -cp "bin:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" -encoding UTF-8

# 4. Rodar os testes
for cls in DadosTest BaralhoTest EnvelopeTest TabuleiroTest JogoTest; do
    java -cp "bin:bin-test:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" \
        org.junit.runner.JUnitCore model.$cls
done
```

Ou, no Eclipse: importar como projeto Java existente, marcar
`src/` e `test/` como source folders, adicionar os JARs de
`lib/` ao build path e rodar os testes pelo botão direito.

## Status

- 30 testes JUnit 4, todos passando.
- Funcionalidades cobertas: itens 4–7 da Preparação,
  lançamento de dados, mapeamento de casas alcançáveis e
  deslocamento do pião.
