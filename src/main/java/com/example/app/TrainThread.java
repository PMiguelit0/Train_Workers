package com.example.app;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane; 
 

public class TrainThread extends Thread {
    private final int trainCapacity;
    private final int travelTime;
    private final SimulationSceneController controller;

    private final ImageView trainImageView;
    private final AnchorPane mainPane;

    public TrainThread(int trainMaximumLoad, int travellingTime, SimulationSceneController controller,
                       ImageView trainImageView, AnchorPane mainPane) {
        this.trainCapacity = trainMaximumLoad;
        this.travelTime = travellingTime * 1000;
        this.controller = controller;
        this.trainImageView = trainImageView; // Armazena a referência da imagem
        this.mainPane = mainPane;             // Armazena a referência do painel
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Trem aguardando para carregar...");
                Semaphores.itensDisponiveis.acquire(this.trainCapacity);
                for (int i = 0; i < this.trainCapacity; i++) {
                    Semaphores.mutexDeposito.acquire();
                    controller.resetBoxCount();
                    try {
                        System.out.println("Trem carregou a caixa " + (i + 1) + "/" + this.trainCapacity);
                    } finally {
                        Semaphores.mutexDeposito.release();
                    }
                    Semaphores.espacosVazios.release();
                }
                System.out.println("Trem cheio! Partindo para viagem...");


                // --- Animação CPU-bound do trem (ida) ---
                System.out.println("Trem iniciando animaçao (CPU-bound) ida...");
                double startX = trainImageView.getTranslateX();
                double endX = Math.max(0, mainPane.getWidth() - trainImageView.getFitWidth());
                final long startMs = System.currentTimeMillis();
                final long durationMs = travelTime;
                final int frameMs = 1000 / 60; // ~16ms por frame

                // Loop que atualiza a posição do trem e consome CPU
                while (true) {
                    long elapsed = System.currentTimeMillis() - startMs;
                    if (elapsed >= durationMs) break;
                    double progress = (double) elapsed / (double) durationMs;
                    double currentX = startX + (endX - startX) * progress;
                    Platform.runLater(() -> trainImageView.setTranslateX(currentX));

                    // busy-wait para tornar a animação CPU-bound por frameMs
                    long frameStart = System.nanoTime();
                    while ((System.nanoTime() - frameStart) < frameMs * 1_000_000L) {
                        double acc = Math.sqrt(98765.4321) * Math.PI; // trabalho leve
                        if (acc == Double.MIN_VALUE) System.out.println(acc);
                    }
        
                }
                // garante posição final
                Platform.runLater(() -> trainImageView.setTranslateX(endX));
                System.out.println("Trem chegou ao destino e descarregou.");

                // --- Animação CPU-bound do trem (volta) ---
                System.out.println("Trem iniciando animação (CPU-bound) volta...");
                final long startReturnMs = System.currentTimeMillis();
                final double returnStartX = endX;
                final double returnEndX = 0;
                while (true) {
                    long elapsed = System.currentTimeMillis() - startReturnMs;
                    if (elapsed >= durationMs) break;
                    double progress = (double) elapsed / (double) durationMs;
                    double currentX = returnStartX + (returnEndX - returnStartX) * progress;
                    Platform.runLater(() -> trainImageView.setTranslateX(currentX));

                    long frameStart = System.nanoTime();
                    while ((System.nanoTime() - frameStart) < frameMs * 1_000_000L) {
                        double acc = Math.sqrt(56789.1234) * Math.PI;
                        if (acc == Double.MIN_VALUE) System.out.println(acc);
                    }
                }
                Platform.runLater(() -> trainImageView.setTranslateX(returnEndX));
                System.out.println("Trem retornou à estação.");

            } catch (InterruptedException e) {
                System.out.println("Thread do Trem foi interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}