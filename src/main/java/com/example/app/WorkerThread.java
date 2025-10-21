package com.example.app;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class WorkerThread extends Thread {

    private final int workerId;
    private final int packingTime;
    private final SimulationSceneController controller; // Referência ao Controller

    private final Image sleepingImage;
    private final Image workingImage;
    private final Image packedImage;

    // CONSTRUTOR ATUALIZADO
    public WorkerThread(int id, int time, SimulationSceneController controller) {
        this.workerId = id;
        this.packingTime = time*1000;
        this.controller = controller; // Armazena a referência

        this.sleepingImage = new Image(getClass().getResourceAsStream("/images/empacotadorDormindo.png"));
        this.workingImage = new Image(getClass().getResourceAsStream("/images/empacotadorEmpacotando.png"));
        this.packedImage = new Image(getClass().getResourceAsStream("/images/empacotadorEmpacotado.png"));
    }

    @Override
    public void run() {
        while (true) {
            try {
                Platform.runLater(() -> {
                    controller.setWorkerVisible(workerId, true);
                    controller.setWorkerImage(workerId, sleepingImage);
                });
                Semaphores.espacosVazios.acquire();
                // --- Animação CPU-bound do trabalhador ---
                System.out.println("Trabalhador " + workerId + " iniciando animaçao de empacotamento (CPU-bound)...");
                final int totalMs = packingTime; // já em ms
                final long startTime = System.currentTimeMillis();
                // Ativa a imagem e coloca imagem de working imediatamente
                Platform.runLater(() -> {
                    controller.setWorkerVisible(workerId, true);
                    controller.setWorkerImage(workerId, workingImage);
                });

                // Loop ativo que simula trabalho e atualiza UI em aproximadamente 60 FPS
                final int frameMs = 1000 / 60; // ~16ms por frame
                long elapsed;
                while (true) {
                    elapsed = System.currentTimeMillis() - startTime;
                    if (elapsed >= totalMs) break;

                    final long localElapsed = elapsed;
                    // metade do tempo mostra a imagem "packed"
                    if (localElapsed >= totalMs / 2) {
                        Platform.runLater(() -> controller.setWorkerImage(workerId, packedImage));
                    }

                    // faz trabalho CPU-bound leve (busy-wait por frameMs)
                    long frameStart = System.nanoTime();
                    while ((System.nanoTime() - frameStart) < frameMs * 1_000_000L) {
                        // busy work intencional para tornar CPU-bound; mínimo custo por iteração
                        double x = Math.sqrt(12345.6789);
                        x = x * Math.PI;
                    }
                }
                
                    System.out.println("Trabalhador " + workerId + " terminou a animaçao com a caixa pronta.");

                // --- PARTE 3: LÓGICA DE SINCRONIZAÇÃO ---

                // 1. Espera por um espaço vazio no depósito.
                //    Se bloquear aqui, a imagem 'packedImage' ficará visível, mostrando que ele está esperando.
                System.out.println("Trabalhador " + workerId + " aguardando espaço no depósito com a caixa na mão...");
                // 2. Trava o depósito para acesso exclusivo.
                Semaphores.mutexDeposito.acquire();
                try {
                    // --- REGIÃO CRÍTICA ---
                    // MUDANÇA 2: A imagem volta para "dormindo" APÓS colocar a caixa.
                    // Isso precisa ser feito via Platform.runLater.
                    Platform.runLater(() -> {
                        controller.setWorkerImage(workerId, sleepingImage);
                    });
                    System.out.println("Trabalhador " + workerId + " Colocou a caixa no depósito.");
                    // Notifica o controller para incrementar o contador de caixas empacotadas.
                    controller.incrementBoxCount();
                    // --- FIM DA REGIÃO CRÍTICA ---
                } finally {
                    // 3. Libera a trava do depósito.
                    Semaphores.mutexDeposito.release();
                }

                // 4. Sinaliza que um novo item está disponível para o trem.
                Semaphores.itensDisponiveis.release();

                System.out.println("Trabalhador " + workerId + " sinalizou que há "+Semaphores.itensDisponiveis.availablePermits()+" caixas");


            } catch (InterruptedException e) {
                System.out.println("Thread do Trabalhador " + workerId + " foi interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}