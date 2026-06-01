package model;

/**
 * Observado, conforme o padrão Observer do Capítulo 17.
 *
 * Mantém uma lista de Observadores e os notifica quando ocorre
 * alguma mudança de estado. No nosso projeto, a fachada Jogo é
 * o Observado e as janelas/painéis da View são os Observadores.
 */
public interface Observado {

    void registrarObservador(Observador o);

    void removerObservador(Observador o);

    void notificarObservadores();
}
