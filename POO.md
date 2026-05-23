# Contexto da Disciplina: Programação Orientada a Objetos (POO) em Java

## Objetivo deste arquivo
Este arquivo serve como contexto completo do conteúdo programático da disciplina de POO.
Use-o como referência para garantir que as implementações estejam alinhadas com o que foi ensinado.

---

## Capítulo 1 — Conceitos Básicos de OO
- **Classes** e **Objetos** (instâncias com identidade, estado e comportamento)
- **Mensagens** como mecanismo de interação entre objetos
- **Encapsulamento**: proteção do estado interno do objeto

## Capítulo 2 — Sintaxe Java e Eclipse
- Estrutura de uma aplicação Java (método `main`)
- **Tipos primitivos**: `int`, `double`, `boolean`, `char`, etc.
- Entrada de dados com a classe **`Scanner`**

## Capítulo 3 — Referências, Construtores e Static
- Variáveis de objetos são **referências** (ponteiros sem aritmética)
- **Construtores**: métodos especiais executados na criação do objeto
- Blocos de inicialização e a referência **`this`**
- Modificador **`static`**: variáveis, métodos e blocos de classe (independem de instância)

## Capítulo 4 — Herança e Generalização
- Regra **"É-UM"** e herança com a palavra-chave **`extends`**
- **Modificadores de acesso**: `public`, `protected`, `private`, package-private
- Palavra-chave **`super`** para chamar construtores da superclasse
- A classe **`Object`** como ancestral de todas as classes Java

## Capítulo 5 — Polimorfismo e Classes Abstratas
- **Cast explícito** de tipos e operador **`instanceof`**
- **Polimorfismo**: sobrescrita de métodos (`@Override`)
- Modificador **`final`**: impede sobrescrita ou herança
- Modificador **`abstract`**: classes que não podem ser instanciadas

## Capítulo 6 — Arrays e Wrappers
- Criação e manipulação de **arrays** (incluindo arrays de arrays / matrizes)
- Métodos **`get()`** e **`set()`** para acesso a variáveis privadas
- **Object Wrappers**: `Integer`, `Double`, `Boolean`, etc.
- **Autoboxing** e **Unboxing** automáticos

## Capítulo 7 — Composição vs. Herança e Interfaces
- Quando usar **herança** × **composição** (objeto contendo outro objeto)
- Criação e implementação de **Interfaces** (`interface` / `implements`)

## Capítulo 8 — Strings e Pacotes
- Manipulação e comparação de `String`: `equals()`, `compareTo()`, etc.
- Sistema de **Pacotes (Packages)** do Java
- **CLASSPATH** e funcionamento do class loader

## Capítulo 9 — Exceções e Assertivas
- Tratamento de exceções em tempo de execução (`try`, `catch`, `finally`, `throw`, `throws`)
- **Assertivas** (`assert`) para validar premissas do desenvolvedor

## Capítulo 14 — Generics
- Uso de **`<T>`** para coleções com tipo específico
- Verificação de tipo em **tempo de compilação**
- Eliminação de castings perigosos em tempo de execução

## Capítulo 15 — Coleções (Collections Framework)
- **Interfaces**: `List`, `Set` (sem duplicatas), `Map`
- **Implementações**: `LinkedList`, `HashSet`, `TreeSet`, `HashMap`

## Capítulo 17 — Padrões de Projeto (Design Patterns)
- **Singleton**: garante uma única instância da classe
- **Facade**: interface simplificada para um subsistema complexo
- **Factory Method**: delega a criação de objetos a subclasses
- **Observer**: notificação automática de dependentes
- **Strategy**: encapsula algoritmos intercambiáveis
- **Adapter**: compatibiliza interfaces incompatíveis

## Capítulo 18 — Testes Unitários (JUnit)
- Anotações: **`@Test`**, **`@Before`**, **`@After`**
- **Testes de regressão** e tratamento de exceções nos testes
- **TDD** (Test-Driven Development): escrever os testes antes do código

---

## Instruções para o Claude Code

Ao implementar qualquer tarefa deste projeto, siga estas diretrizes:

1. **Use apenas os conceitos listados acima** — não introduza bibliotecas ou padrões fora do escopo da disciplina sem justificativa.
2. **Respeite as boas práticas ensinadas**: encapsulamento, modificadores de acesso corretos, uso de interfaces quando aplicável.
3. **Prefira composição à herança** quando a relação não for claramente "É-UM".
4. **Use Generics** em coleções para garantir segurança de tipos.
5. **Aplique Design Patterns** do Capítulo 17 quando o problema se encaixar.
6. **Escreva testes JUnit** seguindo o padrão do Capítulo 18 quando solicitado.
7. **Documente** construtores, métodos públicos e interfaces com comentários claros.