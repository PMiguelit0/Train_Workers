package com.example.teste;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane; // Importar AnchorPane
import javafx.util.Duration;
import java.util.concurrent.CountDownLatch;

public class TrainThread extends Thread {
    private final int trainCapacity;
    private final int travelTime;
    private final SimulationSceneController controller;

    // --- MUDANÇA 1: Variáveis para guardar os componentes da UI ---
    private final ImageView trainImageView;
    private final AnchorPane mainPane;

    // --- MUDANÇA 2: Construtor atualizado para receber os componentes ---
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
                // --- CARREGANDO O TREM (Lógica de semáforos - permanece igual) ---
                System.out.println("🚂 Trem aguardando para carregar...");
                Semaphores.itensDisponiveis.acquire(this.trainCapacity);
                for (int i = 0; i < this.trainCapacity; i++) {
                    Semaphores.mutexDeposito.acquire();
                    try {
                        System.out.println("📦 Trem carregou a caixa " + (i + 1) + "/" + this.trainCapacity);
                    } finally {
                        Semaphores.mutexDeposito.release();
                    }
                    Semaphores.espacosVazios.release();
                }
                System.out.println("✅ Trem cheio! Partindo para viagem...");


                // --- MUDANÇA 3: LÓGICA DE ANIMAÇÃO DE IDA (AGORA DENTRO DA THREAD) ---
                final CountDownLatch departureLatch = new CountDownLatch(1);

                // Envia a tarefa de animação para a thread da UI
                Platform.runLater(() -> {
                    TranslateTransition transition = new TranslateTransition(Duration.millis(travelTime), trainImageView);
                    // Move o trem até a borda direita da tela (considerando a largura do trem)
                    transition.setToX(mainPane.getWidth() - trainImageView.getFitWidth());

                    transition.setOnFinished(event -> {
                        departureLatch.countDown(); // Libera a TrainThread
                    });
                    transition.play();
                });

                // A thread do trem espera aqui, sem usar CPU, até a animação terminar
                departureLatch.await();
                System.out.println("✨ Trem chegou ao destino e descarregou.");


                // --- MUDANÇA 4: LÓGICA DE ANIMAÇÃO DE VOLTA (AGORA DENTRO DA THREAD) ---
                final CountDownLatch returnLatch = new CountDownLatch(1);

                // Envia a tarefa de animação de volta para a thread da UI
                Platform.runLater(() -> {
                    TranslateTransition transition = new TranslateTransition(Duration.millis(travelTime), trainImageView);
                    // Move o trem de volta para sua posição original
                    transition.setToX(0);

                    transition.setOnFinished(event -> {
                        returnLatch.countDown(); // Libera a TrainThread
                    });
                    transition.play();
                });

                // A thread do trem espera aqui até o retorno terminar
                returnLatch.await();
                System.out.println("🏠 Trem retornou à estação.");

            } catch (InterruptedException e) {
                System.out.println("❗ Thread do Trem foi interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}