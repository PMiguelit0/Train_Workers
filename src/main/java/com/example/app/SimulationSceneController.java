package com.example.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Arrays;
import java.util.List;

import javafx.scene.layout.AnchorPane;


public class SimulationSceneController {

    @FXML private TextField workerIdField;
    @FXML private TextField packingTimeField;
    @FXML private Label statusLabel;


    @FXML private ImageView imageView0;
    @FXML private ImageView imageView1;
    @FXML private ImageView imageView2;
    @FXML private ImageView imageView3;
    @FXML private ImageView imageView4;
    @FXML private ImageView imageView5;
    @FXML private ImageView imageView6;
    @FXML private ImageView imageView7;
    @FXML private ImageView imageView8;
    @FXML private ImageView imageView9;
    @FXML private ImageView imageView10;
    @FXML private ImageView imageView11;
    @FXML private ImageView imageView12;

    @FXML private AnchorPane mainPane; 
    @FXML private ImageView train;
    @FXML private javafx.scene.control.Label boxCountLabel;

    private List<ImageView> imageViews;

    // contador de caixas empacotadas
    private int boxCount = 0;

    @FXML
    public void initialize() {
        imageViews = Arrays.asList(
                imageView0, imageView1, imageView2, imageView3, imageView4,
                imageView5, imageView6, imageView7, imageView8,
                imageView9, imageView10, imageView11, imageView12
        );
        statusLabel.setText("Sistema pronto. Escolha um trabalhador (ID 1-13).");
        TrainThread trainThread = new TrainThread(
                InitialSceneController.trainCapacityValue,
                InitialSceneController.travellingTimeValue,
                this, // Passa a referência do controller (se a thread precisar chamar outros métodos dele)
                train, // Passa a referência do ImageView do trem
                mainPane        // Passa a referência do painel principal
        );
        trainThread.start();
    }

    public void setWorkerImage(int workerId, Image image) {
        if (workerId < 1 || workerId > imageViews.size() || image == null) return;
        ImageView workerImageView = imageViews.get(workerId - 1);
        Platform.runLater(() -> workerImageView.setImage(image));
    }

    /**
     * Incrementa o contador de caixas e atualiza o rótulo na UI.
     * Pode ser chamado de threads de background.
     */
    public void incrementBoxCount() {
        // Atualiza a variável em background e atualiza o UI thread-safely
        synchronized (this) {
            boxCount++;
        }
        final int current = boxCount;
        Platform.runLater(() -> boxCountLabel.setText(String.valueOf(current)));
    }

    /**
     * Decrementa o contador de caixas em uma unidade e atualiza o rótulo na UI.
     * Não permite que o contador fique negativo.
     * Pode ser chamado de threads de background.
     */
    public void decreaseBoxCount() {
        synchronized (this) {
            if (boxCount > 0) {
                boxCount--;
            }
        }
        final int current = boxCount;
        Platform.runLater(() -> boxCountLabel.setText(String.valueOf(current)));
    }

    public void setWorkerVisible(int workerId, boolean isVisible) {
        if (workerId < 1 || workerId > imageViews.size()) return;
        ImageView workerImageView = imageViews.get(workerId - 1);
        Platform.runLater(() -> workerImageView.setVisible(isVisible));
    }

    @FXML
    private void createWorkerButton() {
        statusLabel.setText("");
        int workerId, packingTime;

        try {
            workerId = Integer.parseInt(workerIdField.getText());
            packingTime = Integer.parseInt(packingTimeField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Erro: ID e Tempo devem ser números válidos.");
            return;
        }

        // Esta validação agora está correta para IDs de 1 a 13.
        if (workerId < 1 || workerId > imageViews.size()) {
            statusLabel.setText("Erro: ID deve estar entre 1 e " + imageViews.size() + ".");
            return;
        }
        ImageView targetWorkerImageView = imageViews.get(workerId - 1);
        if (targetWorkerImageView.isVisible()) {
            statusLabel.setText("Aviso: O trabalhador " + workerId + " já está ativo.");
            return; // Para a execução do método
        }
        WorkerThread workerThread = new WorkerThread(workerId, packingTime, this);
        workerThread.start();

        statusLabel.setText("Trabalhador " + workerId + " ativado!");
    }
}