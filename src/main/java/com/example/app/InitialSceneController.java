package com.example.app;

import com.example.app.Semaphores;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InitialSceneController implements Initializable {
    @FXML private TextField travellingTime;
    @FXML private TextField depositLimit;
    @FXML private TextField trainCapacity; // Renomeado para clareza
    @FXML private Button btnConfirm;
    @FXML private Label errorLabel;

    // MUDANÇA 1: Declare as variáveis como public static
    // Elas servirão como um "container global" para os dados da simulação.
    public static int trainCapacityValue;
    public static int travellingTimeValue;
    public static int depositLimitValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addNumericListener(trainCapacity);
        addNumericListener(travellingTime);
        addNumericListener(depositLimit);
    }

    private void addNumericListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }

    @FXML
    private void buttonConfirmClicked() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        String cargaTremStr = trainCapacity.getText();
        String tempoViagemStr = travellingTime.getText();
        String tamanhoDepositoStr = depositLimit.getText();

        if (cargaTremStr.isEmpty() || tempoViagemStr.isEmpty() || tamanhoDepositoStr.isEmpty()) {
            errorLabel.setText("Todos os campos devem ser preenchidos.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        } else {
            // MUDANÇA 2: Atribua os valores às variáveis estáticas
            trainCapacityValue = Integer.parseInt(cargaTremStr);
            travellingTimeValue = Integer.parseInt(tempoViagemStr);
            depositLimitValue = Integer.parseInt(tamanhoDepositoStr);

            // MUDANÇA 3: Inicialize os semáforos com TODOS os valores necessários
            Semaphores.initializeSemaphores(depositLimitValue);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("simulationScene.fxml"));
                Parent root = loader.load();

                Stage simulation = (Stage) btnConfirm.getScene().getWindow();
                Scene scene = new Scene(root, 1024, 585);
                simulation.setScene(scene);
                simulation.show();
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Erro ao carregar a próxima tela.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        }
    }
}