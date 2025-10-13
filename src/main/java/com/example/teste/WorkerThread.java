package com.example.teste;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.concurrent.CountDownLatch;

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
                Semaphores.espacosVazios.acquire();
                // --- PARTE 1: ANIMAÇÃO VISUAL ---
                final CountDownLatch animationLatch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO, event -> {
                                controller.setWorkerVisible(workerId, true);
                                controller.setWorkerImage(workerId, workingImage);
                            }),
                            new KeyFrame(Duration.millis(packingTime / 2.0), event -> {
                                controller.setWorkerImage(workerId, packedImage);
                            }),
                            new KeyFrame(Duration.millis(packingTime), event -> {
                                controller.setWorkerImage(workerId, sleepingImage);
                            })
                    );
                    // Quando a animação terminar, avisa a thread de background.
                    timeline.setOnFinished(event -> animationLatch.countDown());
                    timeline.play();
                });

                // --- PARTE 2: ESPERA PELA ANIMAÇÃO ---
                System.out.println("👷 Trabalhador " + workerId + " iniciando animação de empacotamento...");
                animationLatch.await(); // Espera a animação de empacotar terminar.
                System.out.println("✅ Trabalhador " + workerId + " terminou a animação com a caixa pronta.");

                // --- PARTE 3: LÓGICA DE SINCRONIZAÇÃO ---

                // 1. Espera por um espaço vazio no depósito.
                //    Se bloquear aqui, a imagem 'packedImage' ficará visível, mostrando que ele está esperando.
                System.out.println("👷 Trabalhador " + workerId + " aguardando espaço no depósito com a caixa na mão...");
                // 2. Trava o depósito para acesso exclusivo.
                Semaphores.mutexDeposito.acquire();
                try {
                    // --- REGIÃO CRÍTICA ---
                    // MUDANÇA 2: A imagem volta para "dormindo" APÓS colocar a caixa.
                    // Isso precisa ser feito via Platform.runLater.
                    Platform.runLater(() -> {
                        controller.setWorkerImage(workerId, sleepingImage);
                    });
                    System.out.println("📦 Trabalhador " + workerId + " Colocou a caixa no depósito.");
                    // --- FIM DA REGIÃO CRÍTICA ---
                } finally {
                    // 3. Libera a trava do depósito.
                    Semaphores.mutexDeposito.release();
                }

                // 4. Sinaliza que um novo item está disponível para o trem.
                Semaphores.itensDisponiveis.release();
                System.out.println("📬 Trabalhador " + workerId + " sinalizou que há "+Semaphores.itensDisponiveis.availablePermits()+" caixas");


            } catch (InterruptedException e) {
                System.out.println("❗ Thread do Trabalhador " + workerId + " foi interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}