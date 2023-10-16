package com.huy.appnoithat.Controller.LuaChonNoiThat;

import com.huy.appnoithat.Common.KeyboardUtils;
import com.huy.appnoithat.Common.PopupUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Common.TableCalculationUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Common.TableUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangNoiThat;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangThanhToan;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Operation.ExportOperation;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Operation.ImportOperation;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Setup.SetupBangNoiThat;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Setup.SetupBangThanhToan;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Setup.SetupTruongThongTin;
import com.huy.appnoithat.Enums.Action;
import com.huy.appnoithat.Enums.FileType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class LuaChonNoiThatController implements Initializable {
    final static Logger LOGGER = LogManager.getLogger(LuaChonNoiThatController.class);
    // Thong Tin ve cong ty
    @FXML
    private TextField TenCongTy, VanPhong, DiaChiXuong, DienThoaiCongTy, Email;
    // Thong tin ve khach hang
    @FXML
    private TextField TenKhachHang, DienThoaiKhachHang, DiaChiKhachHang, NgayLapBaoGia, SanPham;
    // Bang noi that
    @FXML
    private TreeTableView<BangNoiThat> TableNoiThat;
    @FXML
    private TreeTableColumn<BangNoiThat, Double> Cao, Dai, Rong, KhoiLuong;
    @FXML
    private TreeTableColumn<BangNoiThat, Long> DonGia, ThanhTien;
    @FXML
    private TreeTableColumn<BangNoiThat, String> DonVi, HangMuc, VatLieu, STT;
    // Button
    @FXML
    private Button addContinuousButton, addNewButton, ExportButton, SaveButton;
    @FXML
    private ImageView ImageView;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private TableColumn<BangThanhToan, Long> DatCocThiCong30, DatCocThietKe10, HangDenChanCongTrinh50, NghiemThuQuyet, TongTien;
    @FXML
    private TableView<BangThanhToan> bangThanhToan;
    @FXML
    private MenuItem MenuItemExportPDF, MenuItemExportXLS, MenuItemSave, MenuItemSaveAs;
    private ByteArrayOutputStream imageStream;
    private State currentState;
    @Setter
    private String currentDirectory;
    @FXML
    private CheckMenuItem AutoSave;
    private Timeline autoSaveTimer;
    public LuaChonNoiThatController() {
        imageStream = new ByteArrayOutputStream();
    }
    @FXML
    void OnMouseClickedHandler(MouseEvent event) {
        Object source = event.getSource();
        if (source == ImageView) {
            imageViewHandler();
        }
    }
    /**
     * @param event
     * @see javafx.scene.input.KeyEvent
     * This function will handle some key pressed event that will trigger clear selection and delete selection
     */
    @FXML
    void onKeyPressed(KeyEvent event) {
        if (KeyboardUtils.isRightKeyCombo(Action.DELETE, event)) {
            handleDeleteAction();
        }
        if (KeyboardUtils.isRightKeyCombo(Action.CLEAR_SELECTION, event)) {
            TableNoiThat.getSelectionModel().clearSelection();
        }
    }
    @FXML
    void onClickMenuItem(ActionEvent event) {
        Object source = event.getSource();
        if (source == MenuItemExportPDF) {
//            exportFile(FileType.PDF);
        }
        if (source == MenuItemExportXLS) {
            exportFile(FileType.EXCEL);
        }
        if (source == MenuItemSave) {
            save();
        }
        if (source == MenuItemSaveAs) {
            saveAs();
        }
        if (source == AutoSave) {
            if (AutoSave.isSelected()) {
                LOGGER.info("Auto save is enabled");
                startAutoSaveAction();
            } else {
                LOGGER.info("Auto save is disabled");
                stopAutoSaveAction();
            }
        }
    }
    private void handleDeleteAction() {
        if (TableNoiThat.getSelectionModel().getSelectedItems().isEmpty()) {
            return;
        }
        ObservableList<TreeItem<BangNoiThat>> listItem = TableNoiThat.getSelectionModel().getSelectedItems();
        for (int i = listItem.size() - 1; i >= 0; i--) {
            if (listItem.get(i) == null) continue;
            if (listItem.get(i).getParent() == null) continue;

            listItem.get(i).getParent().getChildren().remove(listItem.get(i));
        }
        TableNoiThat.getSelectionModel().clearSelection();
        TableUtils.reArrangeList(TableNoiThat);
        TableCalculationUtils.recalculateAllTongTien(TableNoiThat.getRoot());
    }
    /**
     * @param url
     * @param resourceBundle
     * @see Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public final void initialize(URL url, ResourceBundle resourceBundle) {
        currentState = State.NEW_FILE;
        AutoSave.setSelected(true);
        autoSaveTimer = new Timeline(new KeyFrame(Duration.seconds(20), event -> {
            if (currentState == State.OPEN_FROM_EXISTING_FILE && currentDirectory != null) {
                save();
            }
        }));
        startAutoSaveAction();

        setUpBangThanhToan();
        setUpBangNoiThat();
        setUpButton();
        setUpTruongThongTin();
    }
    /**
     * @param fileType This function will export the table to a file
     */
    public String exportFile(FileType fileType) {
        ExportOperation exportOperation = new ExportOperation(this);
        return exportOperation.exportFile(fileType);
    }
    /**
     * @param directory This function will import the table from a file
     */
    public void importFile(String directory) {
        ImportOperation importOperation = new ImportOperation(this);
        importOperation.importFile(directory);
        currentState = State.OPEN_FROM_EXISTING_FILE;
        currentDirectory = directory;
    }
    /**
     * This function will handle the event when user want to choose an image
     */
    private void imageViewHandler() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) return;
        String fileExtension = FilenameUtils.getExtension(file.getName());
        if (!(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))) {
            PopupUtils.throwErrorSignal("File không hợp lệ");
            return;
        }
        try {
            InputStream imageInputStream = new FileInputStream(file);
            imageStream = new ByteArrayOutputStream();
            IOUtils.copy(imageInputStream, imageStream);
            Image image = new Image(new ByteArrayInputStream(imageStream.toByteArray()));
            ImageView.setImage(image);
        } catch (IOException e) {
            LOGGER.error("Error while reading image file", e);
            throw new RuntimeException(e);
        }
    }
    /**
     * This function will set up the bang noi that
     */
    private void setUpBangNoiThat() {
        SetupBangNoiThat setupBangNoiThat = new SetupBangNoiThat(this);
        setupBangNoiThat.setUpBangNoiThat();
    }
    /**
     * This function will set up the collum for KichThuoc
     */
    private void setUpBangThanhToan() {
        SetupBangThanhToan setupBangThanhToan = new SetupBangThanhToan(this);
        setupBangThanhToan.setUpBangThanhToan();
    }

    private void setUpButton() {
        ButtonHandler buttonHandler = new ButtonHandler(this);
        addContinuousButton.setOnAction(buttonHandler::continuousLineAdd);
        ExportButton.setOnAction(buttonHandler::exportButtonHandler);
        SaveButton.setOnAction(buttonHandler::onSaveAction);
    }

    private void setUpTruongThongTin() {
        NgayLapBaoGia.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        SetupTruongThongTin setupTruongThongTin = new SetupTruongThongTin(this);
        setupTruongThongTin.setup();
    }

    public String saveAs() {
        String directory = exportFile(FileType.NT);
        if (directory == null) {
            return null;
        }
        LOGGER.info("Save as Operation: Save file to: " + directory);
        return directory;
    }

    public void save() {
        ExportOperation exportOperation = new ExportOperation(this);
        if (currentState == State.NEW_FILE) {
            String directory = saveAs();
            if (directory == null) {
                return;
            }
            currentState = State.OPEN_FROM_EXISTING_FILE;
            currentDirectory = directory;
            startAutoSaveAction();
        } else if (currentState == State.OPEN_FROM_EXISTING_FILE) {
            currentDirectory = exportOperation.exportFile(FileType.NT, new File(currentDirectory), false);
        }
        LOGGER.info("Normal save: Save file to: " + currentDirectory);
    }

    private void startAutoSaveAction() {
        autoSaveTimer.setCycleCount(Timeline.INDEFINITE);
        autoSaveTimer.play();
    }
    private void stopAutoSaveAction() {
        autoSaveTimer.stop();
    }
    public void init(Stage stage) {
        stage.setOnHidden(event -> {
            stopAutoSaveAction();
            LOGGER.info("Close window");
        });
    }
}
