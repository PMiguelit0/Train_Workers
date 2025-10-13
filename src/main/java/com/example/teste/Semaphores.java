package com.example.teste;

import java.util.concurrent.Semaphore;

/**
 * Classe utilitária para armazenar e gerenciar os semáforos globais da simulação.
 * Segue o padrão clássico do problema Produtor-Consumidor.
 */
public class Semaphores {

    // --- Semáforos para o Depósito ---

    /**
     * Representa os espaços vazios no depósito.
     * Os trabalhadores (produtores) devem dar um .acquire() neste semáforo antes de adicionar uma caixa.
     * Inicia com o tamanho total do depósito.
     */
    public static Semaphore espacosVazios;

    /**
     * Representa os itens (caixas) que estão prontos no depósito.
     * O trem (consumidor) deve dar um .acquire() neste semáforo antes de remover uma caixa.
     * Inicia com 0.
     */
    public static Semaphore itensDisponiveis;

    /**
     * Mutex (trava) para garantir acesso exclusivo à estrutura de dados do depósito.
     * Apenas uma thread (trabalhador ou trem) pode modificar o depósito por vez.
     * Inicia com 1.
     */
    public static Semaphore mutexDeposito;


    /**
     * Método estático para inicializar todos os semáforos com os valores
     * definidos pelo usuário na tela inicial.
     *
     * @param limiteDeposito    O tamanho máximo do depósito.
     */
    public static void initializeSemaphores(int limiteDeposito) {
        // Inicializa os semáforos do depósito
        espacosVazios = new Semaphore(limiteDeposito, true); // Começa cheio de espaços vazios
        itensDisponiveis = new Semaphore(0, true);          // Começa sem nenhum item
        mutexDeposito = new Semaphore(1, true);             // Trava de acesso, começa liberada

        System.out.println("--- Semáforos Inicializados ---");
        System.out.println("Limite do Depósito: " + limiteDeposito + " espaços.");
        System.out.println("-----------------------------");
    }
}