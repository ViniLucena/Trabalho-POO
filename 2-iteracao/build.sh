#!/bin/bash
# Compilar e executar o projeto Clue — 2ª iteração
set -e

LIBS="lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar"

echo "=== Compilando ==="
javac -d out -cp "$LIBS" $(find src test -name "*.java" | sort)

echo "=== Testes JUnit ==="
java -cp "out:$LIBS" org.junit.runner.JUnitCore \
    model.BaralhoTest model.DadosTest model.EnvelopeTest model.JogoTest model.TabuleiroTest

echo "=== Iniciando jogo ==="
java -cp "out" app.Main
