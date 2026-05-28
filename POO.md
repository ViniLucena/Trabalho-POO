# Contexto – INF1636 Programação Orientada a Objetos
---

## 1. Filosofia e Objetivos da Disciplina

O professor trabalha com foco em três objetivos centrais:
1. Apresentar os **principais conceitos de orientação a objetos** (classe, objeto, mensagem, encapsulamento, herança, polimorfismo etc.).
2. Conhecer os elementos básicos e as principais bibliotecas do Java.
3. Aplicar técnicas de POO na construção de uma **aplicação de média complexidade** (trabalho em grupo).

> O professor parte sempre do **problema concreto** para motivar o conceito. Exemplo: mostra um problema em C (acesso livre a membros de struct causando inconsistência) e introduz a solução via **classe** (encapsulamento).

---

## 2. Conceitos Fundamentais de OO (Cap. 1)

### 2.1 Classe
- **Definição 1 (Meyer):** Uma classe é um tipo abstrato de dados possivelmente parcialmente implementado.
- **Definição 2 (Booch):** Uma classe é a descrição de um conjunto de objetos que possuem a mesma semântica e compartilham as mesmas propriedades (atributos, operações e relacionamentos).
- Classe = molde; objetos = instâncias da classe.

### 2.2 Objeto
- Entidade com **fronteiras bem definidas**, que possui **identidade, estado e comportamento** próprios.
- **Estado** → valores das variáveis de instância (núcleo/centro do objeto).
- **Comportamento** → operações (métodos) que rodeiam e escondem o núcleo.
- O empacotamento da estrutura interna é chamado de **encapsulamento**.

### 2.3 Mensagem
- Objetos interagem por meio de **mensagens**.
- Quando o objeto A quer que o objeto B execute uma operação, A envia uma mensagem para B.
- As informações passadas pela mensagem são os **parâmetros** da operação.

---

## 3. Estrutura de Classe em Java (Cap. 1 e 2)

### 3.1 Estrutura básica
```java
<modificadores> class <nome> {
    // variaveis
    <lista_modificadores> <tipo> <nome> [= <valor_inicial>];

    // métodos
    <lista_modificadores> <tipo> <nome>([<lista_parametros>]) {
        <comandos>
    }
}
```

### 3.2 Exemplo canônico (classe Vetor)
```java
public class Vetor {
    // variaveis
    private double x = 3.0;
    private double y = 4.0;

    // métodos
    public double modulo() {
        double a;
        a = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
        return a;
    }
}
```

### 3.3 Modificadores de acesso

| Modificador | Efeito |
|---|---|
| `public` (classe) | Pode ser usada por qualquer outra classe |
| `public` (membro) | Pode ser acessado por qualquer classe |
| `private` (membro) | Só pode ser acessado pela própria classe |
| `protected` (membro) | Visível nas subclasses e classes do mesmo pacote |
| *(ausente)* (membro) | Visível apenas no mesmo pacote |

**Regra do professor:**
- Variáveis de instância → **sempre `private`**
- Métodos da interface da classe → **sempre `public`**
- Nunca acessar variáveis `private` de fora da classe (quebra de encapsulamento)

---

## 4. Tecnologia Java (Cap. 2)

### 4.1 Fluxo de desenvolvimento
```
Arquivo.java → [javac] → Arquivo.class (bytecodes) → [JVM] → execução
```

### 4.2 HelloWorldApp
```java
/**
 * The HelloWorldApp class implements an application that
 * simply displays "Hello World!" to the standard output.
 */
class HelloWorldApp {
    public static void main(String[] args) {
        //Display "Hello World!"
        System.out.println("Hello World!");
    }
}
```
- Compilar: `javac HelloWorldApp.java`
- Executar: `java HelloWorldApp`
- O arquivo `.java` deve ter o **mesmo nome da classe principal**.

### 4.3 O método `main`
```java
public static void main(String[] args) { ... }
```
- `public` → invocável por qualquer objeto
- `static` → método de classe
- `void` → não retorna valor
- Argumento padrão: `args`

### 4.4 Tipos de comentários
| Tipo | Sintaxe | Uso |
|---|---|---|
| Documentação | `/** ... */` | Usado pelo `javadoc` |
| Linha | `// texto` | Ignora até fim da linha |
| Bloco | `/* texto */` | Ignora entre os delimitadores |

---

## 5. Tipos Primitivos, Operadores e Controle de Fluxo (Cap. 2)

### 5.1 Tipos primitivos
| Tipo | Descrição | Formato |
|---|---|---|
| `byte` | Byte-length integer | 8-bit two's complement |
| `short` | Short integer | 16-bit two's complement |
| `int` | Integer | 32-bit two's complement |
| `long` | Long integer | 64-bit two's complement |
| `float` | Single-precision floating point | 32-bit IEEE 754 |
| `double` | Double-precision floating point | 64-bit IEEE 754 |
| `char` | A single character | 16-bit Unicode character |
| `boolean` | A boolean value | true or false |

### 5.2 Modificador `final` (constante)
```java
private final int x = 2; // constante inteira
```

### 5.3 Operadores aritméticos
`+`, `-`, `*`, `/`, `%`, `++` (pré/pós), `--` (pré/pós)

**Regra de promoção:** se há `double` → `double`; se há `float` → `float`; se há `long` → `long`; senão → `int`.

### 5.4 Operadores relacionais e lógicos
- Relacionais: `>`, `>=`, `<`, `<=`, `==`, `!=` (retornam `boolean`)
- Lógicos: `&&`, `||`, `!`
- Atribuição: `=`, `+=`, `-=`, `*=`, `/=`, `%=`, `&=`, `|=`, `^=`
- Outros: `?:`, `[]`, `new`, `(type)`, `instanceof`

### 5.5 Expressões booleanas (diferença do C)
```java
// CERTO em Java
int x = 10;
while (x > 0) { System.out.println(x); x--; }

// ERRADO em Java (válido em C)
while (x) { ... }
```

### 5.6 Comandos de controle de fluxo
| Tipo | Palavras-reservadas |
|---|---|
| Repetição | `while`, `do-while`, `for` |
| Decisão | `if-else`, `switch-case` |
| Tratamento de Exceção | `try-catch-finally`, `throw` |
| Desvio de Fluxo | `break`, `continue`, `label:`, `return` |

---

## 6. Entrada e Saída no Console (Cap. 2 e 8)

### 6.1 Saída com `System.out.println` e `System.out.printf`
```java
System.out.println("texto");            // imprime e pula linha
System.out.print("texto");             // imprime sem pular linha
System.out.printf("%d %.2f\n", x, y); // saída formatada
```

### 6.2 Entrada com `Scanner`
```java
import java.util.*;

Scanner e = new Scanner(System.in);
int x = e.nextInt();
float f = e.nextFloat();
double d = e.nextDouble();
String linha = e.nextLine();
String palavra = e.next();
byte b = e.nextByte();
```

---

## 7. Ponteiros, Objetos e Construtores (Cap. 3)

### 7.1 Variáveis de objeto e `new`
```java
Vetor v = null;       // v não referencia objeto algum
v = new Vetor();      // cria o objeto no heap e retorna referência
```
- `new` aloca o objeto no **heap** e retorna uma referência.
- Atribuição entre variáveis de objeto **não cria novo objeto** – ambas referenciam o mesmo.

### 7.2 Valores default de variáveis de instância
| Tipo | Valor default |
|---|---|
| Numérico | `0` |
| `char` | `\u0000` |
| `boolean` | `false` |
| Objeto | `null` |

### 7.3 Construtores
- Nome igual ao da classe, sem tipo de retorno (nem `void`).
- Chamado exclusivamente via `new`.
- Pode ser sobrecarregado (overloading).
- Se nenhum construtor for definido, o compilador fornece um **construtor default** (sem parâmetros).
- Se um construtor não-default for definido, o compilador **NÃO** fornece o default automaticamente.

```java
public class Contribuinte {
    private String nome;
    private String dtNasc;
    private long cpf;

    public Contribuinte() {      // construtor default
        nome = "João da Silva";
        dtNasc = "01/01/1900";
    }

    public Contribuinte(String n) { // construtor sobrecarregado
        nome = n;
    }
}
```

### 7.4 Blocos de inicialização
```java
public class Contribuinte {
    // variaveis
    private long cpf;

    { cpf = 0; }  // bloco de inicialização – executado antes do construtor

    public Contribuinte() { ... }
}
```

**Sequência de criação de objeto:**
1. Variáveis de instância inicializadas (na ordem de declaração)
2. Blocos de inicialização (na ordem de definição)
3. Construtor invocado pelo `new`

### 7.5 Variáveis e métodos estáticos
```java
public class Empregado {
    private int id;
    private static int numEmp = 0; // variável estática (pertence à classe)

    public Empregado() {
        numEmp++;
    }

    public int getNumEmp() {
        return numEmp;
    }
}
```
- Variável estática: **existe apenas uma** independentemente do número de objetos.
- Método estático: pertence à classe, não manipula variáveis de instância.
- Método estático deve ser invocado pelo **nome da classe**: `Math.sqrt(...)`.

### 7.6 Referência `this`
```java
// Passando o próprio objeto como argumento
public void alocaGerente(Projeto p) {
    p.registraGerente(this);
}

// Chamando outro construtor da mesma classe
public Vetor() {
    this(0.0, 0.0);
}
```

### 7.7 Blocos estáticos
```java
static {
    System.out.println("Bloco estático – executado quando a classe é carregada");
}
```
- Executado **uma única vez** quando a classe é carregada pela JVM.
- Executado **antes** do construtor default.
- Não pode acessar membros não-estáticos.

### 7.8 Garbage Collector
- Em Java, o programador **não libera memória** manualmente.
- O **Garbage Collector** (thread em background) reclama áreas de memória sem referências.

---

## 8. Herança e Visibilidade (Cap. 4)

### 8.1 Tipos enumerados
```java
public enum Cor {
    amarelo, vermelho, azul, verde, preto
}

Cor vareta = Cor.amarelo;

switch (vareta) {
    case amarelo: pontos = 5; break;
    case azul: pontos = 15; break;
    // ...
}
```

### 8.2 Herança com `extends`
```java
public class Poligono { }
public class Triangulo extends Poligono { }
public class Retangulo extends Poligono { }
public class Quadrado extends Retangulo { }
```
- Regra **é-um (is-a):** todo quadrado é um retângulo, todo retângulo é um polígono.
- A herança permite que propriedades não-privadas da superclasse façam parte do espaço de nomes das subclasses.

### 8.3 Visibilidade em UML
| Símbolo | Modificador | Visibilidade |
|---|---|---|
| `+` | `public` | Todos |
| `#` | `protected` | Subclasses + mesmo pacote |
| `-` | `private` | Apenas a própria classe |
| `~` | *(default)* | Apenas o mesmo pacote |

### 8.4 Chamada de construtores com `super`
```java
public class Retangulo extends Poligono {
    public Retangulo() {
        super(0); // invoca construtor de Poligono
    }
}
```
- Quando um objeto é criado, **todos os construtores ancestrais** são invocados (implícita ou explicitamente).

### 8.5 A classe `Object`
- Toda classe herda implicitamente de `java.lang.Object`.
- O operador `==` entre objetos verifica se **referenciam o mesmo objeto**.
- `equals()` pode ser **sobrescrito** para definir um novo conceito de igualdade.

---

## 9. Polimorfismo e Modificadores (Cap. 5)

### 9.1 Conversão de tipos
```java
Poligono p1 = new Quadrado(); // OK: descendente → ancestral (implícito)
Quadrado q1 = (Quadrado) p1;  // Cast explícito: ancestral → descendente
```
- Conversão para subclasse é verificada em **tempo de execução**; pode lançar exceção.

### 9.2 Operador `instanceof`
```java
if (p instanceof Retangulo)
    x = (Retangulo) p;
```

### 9.3 Polimorfismo
- **Overloading (sobrecarga):** mesmo nome, assinaturas diferentes → amarração em **tempo de compilação**.
- **Overriding (sobrescrita):** mesmo nome e assinatura em subclasse → amarração em **tempo de execução** (late binding / amarração dinâmica).

```java
public class C1 {
    public void m1() { System.out.println("PUC-Rio"); }
}

public class C2 extends C1 {
    public void m1() { System.out.println("Departamento de Informática"); }
}

// Uso
C1 p = new C2();
p.m1(); // imprime "Departamento de Informática" (late binding)
```

### 9.4 Modificador `final`
- Em **classe:** impede a criação de subclasses.
- Em **método:** impede a sobrescrita.
- Em **variável:** constante (valor não pode ser alterado).

### 9.5 Modificador `abstract`
```java
public abstract class Component {
    public void setBackground(java.awt.Color c) { /* código */ }
    public abstract void draw(); // método abstrato – sem implementação
}

public class Button extends Component {
    public void draw() { /* implementação obrigatória */ }
}
```
- Classe abstrata **não pode ser instanciada**.
- Método abstrato **obriga** as subclasses a fornecerem implementação.
- Use quando: várias subclasses serão definidas; todos os objetos serão tratados como instâncias da raiz; a raiz não deve ser instanciada sozinha.

---

## 10. Arrays e Object Wrappers (Cap. 6)

### 10.1 Arrays
```java
int qtd[] = new int[]{1, 2, 3, 4, 5};  // criação com inicialização
int num[] = new int[100];               // criação com tamanho

// Cópia
System.arraycopy(qtd, 0, num, 0, 5);
int num2[] = (int[]) qtd.clone();

// Tamanho
qtd.length
```

### 10.2 For-each (nova versão do for – JDK 1.5)
```java
for (int[] v : mat) {
    for (int i : v)
        System.out.print(i + " ");
    System.out.println();
}
```

### 10.3 Arrays de arrays (matrizes jagged)
```java
int mat[][] = new int[][]{{0}, {0,1}, {0,1,2}, {0,1,2,3}};
```

### 10.4 Object Wrappers
| Tipo Primitivo | Classe Wrapper |
|---|---|
| `boolean` | `java.lang.Boolean` |
| `char` | `java.lang.Character` |
| `int` | `java.lang.Integer` |
| `long` | `java.lang.Long` |
| `byte` | `java.lang.Byte` |
| `short` | `java.lang.Short` |
| `double` | `java.lang.Double` |
| `float` | `java.lang.Float` |

```java
int i = Integer.parseInt("2047");
String s = Integer.toString(1234);
Double d = Double.valueOf("25.54");
```

**Autoboxing/Unboxing (JDK 1.5):**
```java
Double dObj1 = 20.0;           // boxing
double result = dObj1 + 10.0;  // unboxing automático
```

### 10.5 Métodos get() e set()
```java
public class umaClasse {
    private int matricula;
    private String nome;

    public int getMatric() { return matricula; }
    public String getNome() { return nome; }
    public void setMatric(int m) { matricula = m; }
    public void setNome(String n) { nome = n; }
}
```

---

## 11. Interface e Composição (Cap. 7)

### 11.1 Herança vs. Composição
- **Herança:** mecanismo caixa-branca, estático, aumenta acoplamento.
- **Composição:** mecanismo caixa-preta, permite reconfiguração dinâmica, mais reutilizável. **Preferir composição sobre herança.**

### 11.2 Interface
```java
public interface IFila {
    public boolean vazio();
    public boolean insFin(Object x);
    public Object retIni();
}

public class Fila implements IFila {
    // deve implementar todos os métodos da interface
}
```
- Não pode ser instanciada.
- Todas as operações são `public`.
- Variáveis são constantes estáticas (`public static final`).
- Uma classe pode implementar **múltiplas interfaces**.

### 11.3 Delegação e Composição
```java
public class Fila {
    Lista ls = new Lista(); // composição

    public boolean enqueue(Object x) {
        return ls.insFin(x); // delegação
    }

    public Object dequeue() {
        return ls.retIni();
    }

    public boolean vazio() {
        return ls.vazio();
    }
}
```

### 11.4 Métodos default e estáticos em interface (Java 8+)
```java
public interface Veiculo {
    default void print() {
        System.out.println("Sou um veículo!");
    }

    static void acionaBuzina() {
        System.out.println("Tocando a buzina!!!");
    }
}
```
- Conflito de métodos default: sobrescrever na classe ou usar `Veiculo.super.print()`.

---

## 12. A Classe String e Pacotes (Cap. 8)

### 12.1 String
```java
String s = "Departamento de Informática"; // literal
String s2 = new String(new char[]{'P','U','C'});

s.length()                            // tamanho
s.concat(s2)  ou  s + s2             // concatenação
s.charAt(0)                           // primeiro caractere
s.substring(0, 12)                    // substring
s.indexOf("for")                      // busca
s.startsWith("Info")                  // comparação
s.equals(outra)                       // igualdade (conteúdo)
s.equalsIgnoreCase(outra)             // igualdade sem case
s.compareTo(outra)                    // ordem lexicográfica
String.format("N: %d\n", 25)          // formata string
String.valueOf(34.87)                 // número para string
```
- Strings são **imutáveis** – métodos que "alteram" retornam nova string.
- Para comparar conteúdo: use **`.equals()`**, nunca `==`.

### 12.2 Pacotes
```java
package a.b.c;  // declaração no topo do arquivo

import poligonos.*;  // importação
```
- Nome do pacote casa com a estrutura de diretórios.
- Nome completo de classe: `a.b.c.NomeDaClasse`.
- Compilação: `javac a/b/c/Classe.java`; Execução: `java a.b.c.Classe`.
- Variável de ambiente **CLASSPATH** informa ao class loader onde buscar pacotes.
- Arquivos **JAR** distribuem aplicações empacotadas (similar a `.zip`).

---

## 13. Tratamento de Exceções e Assertivas (Cap. 9)

### 13.1 Hierarquia de exceções
- `Throwable` → `Error` (erros graves, sem recuperação) | `Exception` → `RuntimeException` (unchecked) | demais (checked)

### 13.2 try-catch-finally
```java
try {
    // código que pode lançar exceção
}
catch (TipoExcecao1 ex) {
    // tratamento específico
}
catch (Exception ex) {
    // captura qualquer exceção (usar por último)
}
finally {
    // executado sempre, com ou sem exceção
}
```

### 13.3 Criando e lançando exceções (checked)
```java
public class NovaExcecao extends Exception {
    public NovaExcecao() {}
    public NovaExcecao(String mensagem) {
        super(mensagem);
    }
}

// Lançar
throw new NovaExcecao("Ocorreu uma excecao");

// Declarar no método
public void m1() throws NovaExcecao { ... }
```

### 13.4 Relançamento
```java
catch (ExcecaoSimples ex) {
    // trata parcialmente
    throw ex; // relança
}
```

### 13.5 Assertivas
```java
assert y != 0 : "Denominador igual a zero";
```
- Habilitadas com `java -ea NomeDaClasse`.
- Usadas apenas para detectar **erros fatais** (debugging).

---

## 14. Generics (Cap. 14)

```java
// Declaração de coleção genérica
Vector<String> ls = new Vector<String>();

// Classe genérica própria
public class No<X> {
    private X elem;
    private No<X> prox;

    public No(X e, No<X> p) { elem = e; prox = p; }
    public X getElem() { return elem; }
    public No<X> getProx() { return prox; }
    public void setProx(No<X> o) { prox = o; }
}
```
- Verificação de tipos em **tempo de compilação**.
- Elimina a necessidade de cast ao recuperar elementos.

---

## 15. Coleções (Cap. 15)

### 15.1 Interfaces principais
- `Collection` → raiz
- `List` → coleção ordenada, aceita duplicatas, acesso por índice
- `Set` → sem duplicatas
- `SortedSet` → Set ordenado ascendentemente
- `Queue` → fila, geralmente FIFO
- `Map<K,V>` → pares chave-valor, sem chaves duplicadas

### 15.2 LinkedList
```java
import java.util.*;

List<String> cs = new LinkedList<String>();
cs.add("Brasil");
cs.add("Argentina");

ListIterator<String> li = cs.listIterator(cs.size());
while (li.hasPrevious())
    System.out.println(li.previous());
```

### 15.3 HashSet
```java
Set<Ponto> ss = new HashSet<Ponto>();
ss.add(new Ponto(0, 0));
// Duplicatas pelo mesmo objeto não são adicionadas
```

### 15.4 TreeSet (conjunto ordenado)
```java
Set<Ponto> ss = new TreeSet<Ponto>(new CompPonto());

// Implementar Comparator
public class CompPonto implements Comparator<Ponto> {
    public int compare(Ponto p1, Ponto p2) {
        if ((p1.x + p1.y) > (p2.x + p2.y)) return 1;
        else if ((p1.x + p1.y) < (p2.x + p2.y)) return -1;
        else return 0;
    }
}
```

### 15.5 HashMap
```java
Map<Integer, Aluno> lc = new HashMap<Integer, Aluno>();
lc.put(12345, new Aluno("João"));
Aluno a = lc.get(12345);

// Iterar
for (Map.Entry<Integer, Aluno> me : lc.entrySet()) {
    System.out.println(me.getKey() + " " + me.getValue().getNome());
}
```

### 15.6 Outros
- `Stack<E>` → pilha LIFO (extends Vector)
- `Vector<E>` → array dinâmico, sincronizado (preferir `ArrayList` se não precisar de sync)
- `Queue<E>` → fila; métodos: `add()`/`offer()`, `remove()`/`poll()`, `element()`/`peek()`

---

## 16. Padrões de Design (Cap. 17)

### 16.1 Singleton
Garante **uma única instância** de uma classe e um ponto global de acesso.
```java
public class CtrlVenda {
    private static CtrlVenda ctrl = null;

    private CtrlVenda() {}

    public static CtrlVenda getCtrlVenda() {
        if (ctrl == null)
            ctrl = new CtrlVenda();
        return ctrl;
    }

    public void encerraVenda() { }
}

// Uso
CtrlVenda.getCtrlVenda().encerraVenda();
```

### 16.2 Façade
Fornece uma **interface única** para os recursos mais gerais de um subsistema, reduzindo dependências entre subsistemas.

### 16.3 Factory Method
Cria objetos sem que o solicitante dependa diretamente das classes instanciadas. O solicitante usa uma interface/classe abstrata.
```java
// Cliente não sabe qual implementação concreta está usando
Criptografia c = FabricaCriptografia.getInstance().getCriptografia("config.cfg");
c.criptografa("Texto");
```

### 16.4 Observer
Permite que um objeto (**observado**) notifique dinamicamente outros (**observadores**) quando seu estado mudar.
```java
// Observador se registra
public pnRelogio() {
    Ctrl.getInstance().registra(this);
}

// Observado notifica
private void atualiza() {
    for (ObservadorIF o : lst)
        o.notify(this);
}

// Observador reage (callback)
public void notify(ObservadoIF o) {
    mm = o.get(1);
    ss = o.get(2);
    repaint();
}
```

### 16.5 Strategy
Define uma **família de algoritmos** encapsulados em objetos; permite trocar o algoritmo em tempo de execução.
- Usa composição em vez de herança.
- Novos comportamentos podem ser adicionados sem alterar as classes clientes.

### 16.6 Adapter
Converte a interface de uma classe em outra esperada pelos clientes.
- **Class Adapter:** herança + implementação de interface.
- **Object Adapter:** herança + composição (mais flexível, permite adaptar vários componentes).

---

## 17. Testes Unitários com JUnit (Cap. 18)

### 17.1 Estrutura básica
```java
import org.junit.*;
import static org.junit.Assert.*;

public class DateTest {

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testaSomaDias() {
        Date actual = Date.getDate(2050, 2, 15);
        actual.addDays(4);
        Date expected = Date.getDate(2050, 2, 19);
        assertEquals("Data incorreta após somar dias", expected, actual);
    }

    private static final int DEFAULT_TIMEOUT = 2000;
}
```

### 17.2 Principais anotações
| Anotação | Uso |
|---|---|
| `@Test` | Marca um método como caso de teste |
| `@Test(timeout=N)` | Falha se demorar mais de N ms |
| `@Test(expected=Exc.class)` | Passa se lançar a exceção especificada |
| `@Before` | Executa antes de cada `@Test` |
| `@After` | Executa após cada `@Test` |
| `@BeforeClass` | Executa uma vez antes de todos os testes |
| `@AfterClass` | Executa uma vez após todos os testes |

### 17.3 Métodos de asserção
```java
assertTrue("msg", test)
assertFalse("msg", test)
assertEquals("msg", expected, actual)  // expected sempre à ESQUERDA
assertSame("msg", expected, actual)    // usa ==
assertNotSame("msg", expected, actual)
assertNull("msg", value)
assertNotNull("msg", value)
fail()                                 // força falha imediata
```

### 17.4 Boas práticas de testes (segundo o professor)
- Nomes de métodos de teste **longos e descritivos**.
- **Uma assertiva por método** de teste (ou poucas).
- Sempre incluir **timeout** para evitar loops infinitos.
- Testar casos limite (zero, negativo, vazio, null).
- Usar métodos **helper** para reduzir redundância.
- Testes devem ser **independentes** uns dos outros.
- Evitar `if/else`, loops e `try/catch` dentro dos testes.
- Mensagens explicativas em todas as assertivas.
- `assertEquals`: valor **esperado à esquerda**, valor **real à direita**.

### 17.5 Suite de testes
```java
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DateTest.class,
    PoligonoTest.class
})
public class SuiteTestes {}
```

---

## 18. Padrões e Convenções Obrigatórias do Professor

> Estas regras devem ser seguidas em **todo código gerado** para esta disciplina.

1. **Encapsulamento obrigatório:** variáveis de instância sempre `private`.
2. **Métodos públicos:** métodos da interface da classe sempre `public`.
3. **Comentários de seção:** usar `// variaveis` e `// métodos` para separar seções dentro da classe.
4. **Um arquivo por classe pública:** cada classe `public` em seu próprio arquivo `.java`.
5. **Nome do arquivo = nome da classe principal** (com o método `main`).
6. **Expressões booleanas:** usar sempre expressões `boolean` em `if` e `while` (nunca inteiros como em C).
7. **Constantes com `final`:** usar `final` em vez de valores literais mágicos.
8. **`import java.util.*`** quando usar `Scanner`, `ArrayList`, coleções etc.
9. **Entrada pelo teclado:** usar a classe `Scanner` com `System.in`.
10. **Saída formatada:** usar `System.out.printf` para formatação numérica e `System.out.println` para texto simples.
11. **Generics:** sempre especificar o tipo em coleções: `List<String>`, `Map<Integer, Aluno>` etc.
12. **Comparação de Strings:** sempre usar `.equals()`, nunca `==`.
13. **`super()`** para invocar construtores ancestrais explicitamente quando necessário.
14. **`this()`** para invocar outro construtor da mesma classe.
15. **Preferir composição a herança** quando não houver relação é-um genuína.
16. **`instanceof`** antes de cast para evitar `ClassCastException`.
17. **JUnit:** `assertEquals` com valor esperado à esquerda e mensagem explicativa.

---

## 19. Roadmap dos Capítulos

| Capítulo | Tópico Principal |
|---|---|
| 00 | Apresentação da disciplina (objetivos, avaliação, regras) |
| 01 | Conceitos de OO: Classe, Objeto, Mensagem; OO em Java |
| 02 | Visão geral do Java; HelloWorld; Operadores; Eclipse |
| 03 | Ponteiros, variáveis de objeto, construtores, `this`, estáticos, blocos |
| 04 | Enums, herança (`extends`), visibilidade, `super`, classe `Object` |
| 05 | Conversão de tipos, polimorfismo, `final`, `abstract` |
| 06 | Arrays, arrays de arrays, Object Wrappers, autoboxing, get/set |
| 07 | Herança vs. Composição, interfaces, delegação, métodos default |
| 08 | I/O console, classe String, pacotes, CLASSPATH, JAR |
| 09 | Exceções (try-catch-finally, checked, throws), assertivas |
| 14 | Generics |
| 15 | Coleções (List, Set, Map, LinkedList, HashSet, TreeSet, HashMap) |
| 17 | Padrões de design (Singleton, Façade, Factory Method, Observer, Strategy, Adapter) |
| 18 | Testes unitários com JUnit |

---
