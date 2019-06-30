package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
@Component
@Scope("prototype")
public class ReportErrorDialogController extends StageController {

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private File appDataDir;

    @Autowired
    private Executor executor;

    @Value("${mail.server.url}")
    private String mailServer;

    @FXML
    private Button buttonSend;
    @FXML
    private Button buttonCancel;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField emailTextField;

    @FXML
    private void initialize() {

    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    @FXML
    private void handleSend() {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
//                Resource resource = new FileSystemResource(new File(appDataDir, "logs" + File.separator + "log.log"));
//                if (resource.exists()) {
                    // https://script.google.com/macros/s/AKfycbwz-6GzlvTiqhpmKT-0JXpsJXT2BhV-2kltZPfseQ/exec
                    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
                    parts.add("text", textArea.getText());
                    parts.add("from", emailTextField.getText());
//                    parts.add("Content-Type", "text/plain");
                    // todo: send logs as well
//                    parts.add("file", resource);

                    ResponseEntity<String> exchange = restTemplate.exchange(mailServer, HttpMethod.POST,
                            new HttpEntity<MultiValueMap<String, Object>>(parts),
                            String.class);
                    return exchange.getStatusCode().equals(HttpStatus.FOUND);
//                } else {
//                    return false;
//                }
            }
        };

        Dialog progressDialog = FXMLUtils.getProgressDialog(stage, task);
        EventHandler<WorkerStateEvent> onFail = event -> {
            FXMLUtils.alert("Fail!", "Failed to send report.", "Please try again later or contact me on email", Alert.AlertType.ERROR).showAndWait();
            progressDialog.close();
        };
        task.setOnSucceeded(event -> {
            if (task.getValue()) {
                FXMLUtils.alert("Success!", "Report sent.", "Thanks for reporting error!", Alert.AlertType.INFORMATION).showAndWait();
                progressDialog.close();
                stage.close();
            } else {
                onFail.handle(event);
            }
        });
        task.setOnFailed(onFail);
        executor.execute(task);
        progressDialog.showAndWait();
    }
}
