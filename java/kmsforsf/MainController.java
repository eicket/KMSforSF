// Erik Icket, ON4PB - 2024
package kmsforsf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import common.Constants;
import crypto.Crypto;
import static crypto.Crypto.bytesToHex;
import static crypto.Crypto.computeSHA256;
import crypto.StrongPasswordGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import static org.miracl.core.AES.CBC_IV0_ENCRYPT;

public class MainController
{

    static final Logger logger = Logger.getLogger(MainController.class.getName());

    File keystore = new File(Constants.KEYSTORE);
    File publicKeystoreFile = new File(Constants.PUBLIC_KEYSTORE);

    ObservableList<ExpeditionInstance> expeditionObservableList = FXCollections.observableArrayList();
    ObjectMapper objectMapper = new ObjectMapper();

    private Timeline timeline;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<ExpeditionInstance> expeditionTable;

    @FXML
    private TableColumn<?, ?> indexCol;

    @FXML
    private TableColumn<?, ?> callsignCol;

    @FXML
    private TableColumn<?, ?> dxExpeditionCol;

    @FXML
    private TableColumn<?, ?> startDateCol;

    @FXML
    private TableColumn<?, ?> endDateCol;

    @FXML
    private TableColumn<?, ?> passwordCol;

    @FXML
    private TableColumn<?, ?> selectCol;

    @FXML
    private TextField indexText;

    @FXML
    private TextField callsignText;

    @FXML
    private TextField dxExpeditionText;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    // export the first selected key
    @FXML
    void clickedExportPrivateKeysButton(ActionEvent event)
    {
        logger.fine("clicked export private keys button");

        // check that no more than one key is selected
        // Convert ObservableList to a regular List (ArrayList) because json cannot operate with FXCollections
        List<ExpeditionInstance> expeditionList = FXCollections.observableArrayList(expeditionObservableList);
        List<ExpeditionInstance> privateKeyList = new ArrayList();
        ExpeditionInstance expeditionInstance = null;

        for (int i = 0; i < expeditionList.size(); i++)
        {
            expeditionInstance = expeditionList.get(i);
            if (expeditionInstance.getSelect())
            {
                privateKeyList.add(expeditionInstance);
                break;
            }
        }

        try
        {
            // Define filter to exclude "select"
            SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", SimpleBeanPropertyFilter.serializeAllExcept("select", "password"));

            File privateKeystoreFile = new File("privateKeyFile" + "_" + expeditionInstance.getIndex() + ".json");

            if (expeditionInstance.getPassword().isEmpty())
            {
                // no encryption for the private key file
                objectMapper.writer(filters).writeValue(new File("privateKeyFile" + "_" + expeditionInstance.getIndex() + ".json"), privateKeyList);
                logger.info("Private key store file exported : " + privateKeystoreFile);
            }
            else
            {
                // with encryption
                String privateKeyInJson = objectMapper.writer(filters).writeValueAsString(privateKeyList);
                logger.info("Private key : " + privateKeyInJson);

                // make a 256 bit hash of the strong password, and use this as the encryption key
                logger.info("Password : " + expeditionInstance.getPassword());
                byte[] hashResult = computeSHA256(expeditionInstance.getPassword().getBytes());
                logger.info("Hash : " + bytesToHex(hashResult));

                byte[] encrypted = CBC_IV0_ENCRYPT(hashResult, privateKeyInJson.getBytes());
                logger.info("Encrypted (unencoded) : " + bytesToHex(encrypted));

                String encryptedPrivateKey = Base64.getEncoder().encodeToString(encrypted);
                logger.info("Encrypted (encoded) : " + encryptedPrivateKey);
                logger.info("Encrypted (encoded in hex) : " + bytesToHex(encryptedPrivateKey.getBytes()));

                try (FileWriter fileWriter = new FileWriter("privateKeyFile" + "_" + expeditionInstance.getIndex() + ".enc"))
                {
                    fileWriter.write(encryptedPrivateKey);
                }
                catch (IOException e)
                {
                    logger.severe("exception in writing privateKeyFile.enc" + e.getMessage());
                }
            }
        }
        catch (IOException e)
        {
            logger.severe("IOException writing : " + e.getMessage());
        }
    }

    @FXML
    void clickedExportPublicKeysButton(ActionEvent event)
    {
        logger.fine("clicked export public keys button");

        // Convert ObservableList to a regular List (ArrayList)
        List<ExpeditionInstance> expeditionList = FXCollections.observableArrayList(expeditionObservableList);

        try
        {
            // Define filter to exclude "select"
            SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", SimpleBeanPropertyFilter.serializeAllExcept("select", "privateKey", "password"));
            objectMapper.writer(filters).writeValue(publicKeystoreFile, expeditionList);           
        }
        catch (IOException e)
        {
            logger.severe("IOException writing : " + e.getMessage());
            return;
        }
    }

    @FXML
    void clickedAddButton(ActionEvent event)
    {
        logger.fine("clicked add button");

        // reset all field borders 
        indexText.setStyle("");
        callsignText.setStyle("");
        dxExpeditionText.setStyle("");
        fromDatePicker.setStyle("");
        toDatePicker.setStyle("");

        // index : 1 to 1024 only
        int index;
        try
        {
            index = Integer.parseInt(indexText.getText());
        }
        catch (NumberFormatException e)
        {
            indexText.setStyle("-fx-border-color: red;");
            return;
        }

        if ((index < 0) || (index > 1023))
        {
            indexText.setStyle("-fx-border-color: red;");
            return;
        }

        // check if index is free
        for (int i = 0; i < expeditionObservableList.size(); i++)
        {
            ExpeditionInstance expeditionInstance = expeditionObservableList.get(i);
            if (expeditionInstance.getIndex() == index)
            {
                indexText.setStyle("-fx-border-color: red;");
                return;
            }
        }

        // callsign : uppercase, numbers, and '/'
        String callsign = callsignText.getText();
        if (!callsign.matches("^[A-Z0-9/]+$"))
        {
            callsignText.setStyle("-fx-border-color: red;");
            return;
        }

        // expedition < 80 chars
        String expedition = dxExpeditionText.getText();
        if (!expedition.matches("^[a-zA-Z0-9 ]+$"))
        {
            dxExpeditionText.setStyle("-fx-border-color: red;");
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        logger.fine("from date : " + fromDate);
        if (fromDate == null)
        {
            fromDatePicker.setStyle("-fx-border-color: red;");
            return;
        }

        LocalDate toDate = toDatePicker.getValue();
        logger.fine("to date : " + toDate);
        if (toDate == null)
        {
            toDatePicker.setStyle("-fx-border-color: red;");
            return;
        }

        if (fromDate.isAfter(toDate))
        {
            toDatePicker.setStyle("-fx-border-color: red;");
            return;
        }

        // make key pair
        Crypto crypto = new Crypto();
        crypto.createKeyPair();

        // enter a password for the private key ?
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Create a strong password for the private keyfile ?");
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();

        String password = "";
        if (result.isPresent() && result.get() == yesButton)
        {
            password = StrongPasswordGenerator.generatePassword(10);
            logger.info("password : " + password);
        }

        ExpeditionInstance expeditionInstance = new ExpeditionInstance(index, callsign, expedition, fromDate.toString(), toDate.toString(), crypto.encodedPrivateKey, crypto.encodedPublicKey, password, false);
        expeditionObservableList.add(expeditionInstance);

        try
        {
            // Convert ObservableList to a regular List (ArrayList)
            List<ExpeditionInstance> expeditionList = FXCollections.observableArrayList(expeditionObservableList);

            // Define filter to exclude "password"
            SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", SimpleBeanPropertyFilter.serializeAllExcept("select"));

            objectMapper.writer(filters).writeValue(keystore, expeditionList);
        }
        catch (IOException e)
        {
            logger.severe("IOException writing : " + e.getMessage());
            return;
        }
    }

    @FXML
    void clickedDeleteButton(ActionEvent event
    )
    {
        logger.fine("clicked delete button");

        for (int i = 0; i < expeditionObservableList.size(); i++)
        {
            ExpeditionInstance expeditionInstance = expeditionObservableList.get(i);
            if (expeditionInstance.getSelect())
            {
                expeditionObservableList.remove(i);
            }
        }

        try
        {
            // Convert ObservableList to a regular List (ArrayList)
            List<ExpeditionInstance> expeditionList = FXCollections.observableArrayList(expeditionObservableList);
            objectMapper.writeValue(keystore, expeditionList);
        }
        catch (IOException e)
        {
            logger.severe("IOException writing : " + e.getMessage());
            return;
        }
    }

    @FXML
    private Button verifyAllButton;

    @FXML
    void clickedVerifyAllButton(ActionEvent event
    )
    {
        logger.fine("clicked verify all button");

        for (int i = 0; i < expeditionObservableList.size(); i++)
        {
            logger.info("Verifying index : " + i);
            ExpeditionInstance expeditionInstance = expeditionObservableList.get(i);

            Crypto crypto = new Crypto();
            byte[] signature = new byte[org.miracl.core.BN158.BLS.BFS + 1];
            crypto.sign(signature, Constants.CLEAR_MESSAGE, expeditionInstance.getPrivateKey());

            int result = crypto.verify(signature, Constants.CLEAR_MESSAGE, expeditionInstance.getPublicKey());
            if (result == 0)
            {
                logger.info("Signature is OK");
            }
            else
            {
                logger.info("Signature is *NOT* OK");
            }
        }
    }

    @FXML
    void initialize()
    {
        // read the private keystore file

        // Enable indentation for pretty-printing
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Check if the file exists and is not empty
        try
        {
            if (keystore.exists() && keystore.length() > 0)
            {
                // Read the existing JSON array from the file
                List<ExpeditionInstance> expeditionList = objectMapper.readValue(keystore, new TypeReference<List<ExpeditionInstance>>()
                {
                });
                logger.info("list loaded");
                expeditionObservableList = FXCollections.observableArrayList(expeditionList);
            }
            else
            {
                // If the file doesn't exist or is empty, create a new list
                // expeditionList = new ArrayList<>();
            }
        }
        catch (IOException e)
        {
            logger.severe("IOException reading : " + e.getMessage());
            return;
        }

        expeditionTable.setItems(expeditionObservableList);
        expeditionTable.setEditable(true);

        // "index" will call "getIndex"
        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        callsignCol.setCellValueFactory(new PropertyValueFactory<>("callsign"));
        dxExpeditionCol.setCellValueFactory(new PropertyValueFactory<>("expedition"));

        String pattern = "yyyy-MM-dd";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        fromDatePicker.setConverter(new StringConverter<LocalDate>()
        {
            {
                fromDatePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date)
            {
                if (date != null)
                {
                    return dateFormatter.format(date);
                }
                else
                {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string)
            {
                if (string != null && !string.isEmpty())
                {
                    // when directly entering into the field
                    try
                    {
                        return LocalDate.parse(string, dateFormatter);
                    }
                    catch (Exception e)
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
        );
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        toDatePicker.setConverter(new StringConverter<LocalDate>()
        {
            {
                toDatePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date
            )
            {
                if (date != null)
                {
                    return dateFormatter.format(date);
                }
                else
                {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string)
            {
                if (string != null && !string.isEmpty())
                {
                    // when directly entering into the field
                    try
                    {
                        return LocalDate.parse(string, dateFormatter);
                    }
                    catch (Exception e)
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
        );
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        selectCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>()
        {
            @Override
            public ObservableValue<Boolean> call(Integer param)
            {
                logger.fine("Checkbox Select " + expeditionObservableList.get(param).getSelect());
                return expeditionObservableList.get(param).selectProperty();
            }
        }));
        selectCol.setEditable(true);

        timeline = new Timeline();

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent
            )
            {
                logger.fine("Animation event received");
            }
        }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
    }
}
