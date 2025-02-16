package com.huy.appnoithat.Controller.NewTab;

import com.huy.appnoithat.Common.KeyboardUtils;
import com.huy.appnoithat.Common.PopupUtils;
import com.huy.appnoithat.Controller.Common.SaveBeforeCloseAlert;
import com.huy.appnoithat.Controller.Common.StageUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Constant.State;
import com.huy.appnoithat.Controller.NewTab.Operation.ExportOperation;
import com.huy.appnoithat.Controller.NewTab.Operation.SaveOperation;
import com.huy.appnoithat.Controller.NewTab.Operation.TabOperation;
import com.huy.appnoithat.DataModel.Enums.Action;
import com.huy.appnoithat.DataModel.Enums.FileType;
import com.huy.appnoithat.Service.LuaChonNoiThat.LuaChonNoiThatService;
import com.huy.appnoithat.Service.LuaChonNoiThat.NoiThatFileService;
import com.huy.appnoithat.Service.PersistenceStorage.StorageService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

@Data
public class NewTabController implements Initializable {
    final static Logger LOGGER = LogManager.getLogger(NewTabController.class);
    private final StorageService persistenceStorageService;
    private final NoiThatFileService noiThatFileService;
    private final LuaChonNoiThatService luaChonNoiThatService;
    private Stage currentStage;
    private final List<TabContent> currentlyOpenTab = new ArrayList<>();
    @FXML
    private MenuItem MenuItemExportPDF, MenuItemExportXLS, MenuItemSave, MenuItemSaveAs,
            MenuItemSaveCompanyInfo, MenuItemSaveNoteArea, MenuItemExportMultipleXLS;
    @FXML
    private CheckMenuItem AutoSave;
    @FXML
    private TabPane tabPane;
    @FXML
    private StackPane loadingPane;
    private Timeline autoSaveTimer;
    private State currentState = State.NEW_FILE;
    private String currentDirectory;
    private TabOperation tabOperation;
    private SaveOperation saveOperation;
    private ExportOperation exportOperation;

    public NewTabController(
            StorageService persistenceStorageService,
            NoiThatFileService noiThatFileService,
            LuaChonNoiThatService luaChonNoiThatService
    ) {
        this.persistenceStorageService = persistenceStorageService;
        this.noiThatFileService = noiThatFileService;
        this.luaChonNoiThatService = luaChonNoiThatService;
    }

    @FXML
    void onClickMenuItem(ActionEvent event) {
        Object source = event.getSource();
        if (source == MenuItemExportPDF) {
            exportOperation.exportFile(FileType.PDF);
        } else if (source == MenuItemExportXLS) {
            exportOperation.exportFile(FileType.EXCEL);
        } else if (source == MenuItemExportMultipleXLS) {
            exportOperation.exportMultipleExcel();
        } else if (source == MenuItemSave) {
            saveOperation.save();
        } else if (source == MenuItemSaveAs) {
            saveOperation.saveAs();
        } else if (source == AutoSave) {
            if (AutoSave.isSelected()) {
                startAutoSaveAction();
            } else {
                stopAutoSaveAction();
            }
        } else if (source == MenuItemSaveCompanyInfo) {
            saveOperation.saveThongTinCongTy();
        } else if (source == MenuItemSaveNoteArea) {
            saveOperation.saveNoteArea();
        }
    }

    @FXML
    private void sceneSwitcher(ActionEvent actionEvent) {
    }

    @FXML
    void onKeyPressed(KeyEvent event) {
        if (KeyboardUtils.isRightKeyCombo(Action.SAVE, event)) {
            LOGGER.info("Save key combo pressed");
            saveOperation.save();
        } else if (KeyboardUtils.isRightKeyCombo(Action.UNDO, event)) {
            TabContent selectedTabContent = getSelectedTabContent();
            if (selectedTabContent == null) {
                LOGGER.error("No tab was selected");
                return;
            }
            selectedTabContent.getLuaChonNoiThatController().undo();
        }
    }

    private void startAutoSaveAction() {
        autoSaveTimer.setCycleCount(Timeline.INDEFINITE);
        autoSaveTimer.play();
    }

    private void stopAutoSaveAction() {
        autoSaveTimer.stop();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadingPane.setDisable(true);
        loadingPane.setVisible(false);

        tabOperation = new TabOperation(this);
        saveOperation = new SaveOperation(this);
        exportOperation = new ExportOperation(this);

        tabPane.getTabs().clear();
        Tab newTabButton = newTabButton();
        tabPane.getTabs().add(newTabButton);
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> change) ->
                handleMovingNewTabButton(change, newTabButton));
        startAutoSave();
    }

    private void startAutoSave() {
        AutoSave.setSelected(true);
        autoSaveTimer = new Timeline(new KeyFrame(Duration.minutes(10), event -> {
            if (currentState == State.OPEN_FROM_EXISTING_FILE && currentDirectory != null) {
                saveOperation.save();
            } else if (currentState == State.NEW_FILE) {
                exportOperation.backup();
            }
        }));
        startAutoSaveAction();
    }

    /**
     * @param change
     * @param newTabButton This method will handle the situation when user trying to move the add new tab button because it a tab :))
     */
    private void handleMovingNewTabButton(ListChangeListener.Change<? extends Tab> change, Tab newTabButton) {
        {
            int indexOfNewTab = tabPane.getTabs().indexOf(newTabButton);
            if (indexOfNewTab > 0 && tabPane.getTabs().size() >= 2) {
                ObservableList<Tab> observableList = FXCollections.observableArrayList(tabPane.getTabs());
                Collections.swap(observableList, indexOfNewTab, 0);
                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(observableList);
                tabPane.getSelectionModel().select(1);
            }
        }
    }

    public void init(TabState tabState, String importDirectory, Stage currentStage) {
        this.currentStage = currentStage;
        AutoSave.setSelected(true);
        tabOperation.createNewTab(tabState, importDirectory);
        currentStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            onCloseRequest(currentStage);
        });
    }

    private void onCloseRequest(Stage currentStage) {
        if (isReadyToExit()) {
            StageUtils.closeStage(currentStage);
            return;
        }

        Alert alert = new SaveBeforeCloseAlert();
        ButtonType result = alert.showAndWait().orElse(SaveBeforeCloseAlert.buttonTypeCancel);
        if (result == SaveBeforeCloseAlert.buttonTypeSave) {
            boolean saveResult = saveOperation.save();
            if (saveResult) {
                StageUtils.closeStage(currentStage);;
            }
        } else if (result == SaveBeforeCloseAlert.buttonTypeNotSave) {
            String backedUpFile = exportOperation.backup();
            PopupUtils.throwSuccessNotification(
                    MessageFormat.format("Tệp đã được sao lưu lại với tên {0} và sẽ bị xóa sau 10 ngày", backedUpFile));
            StageUtils.closeStage(currentStage);
        }
    }

    private boolean isReadyToExit() {
        return getCurrentlyOpenTab().stream().allMatch(tab -> tab.getLuaChonNoiThatController().isReadyToClosed());
    }

    private Tab newTabButton() {
        Tab addTab = new Tab();
        Button newTabButton = new Button("+");
        newTabButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        newTabButton.setOnAction(event -> {
            if (tabPane.getSelectionModel().getSelectedItem() != null) {
                tabOperation.createNewTab(TabState.BLANK_TAB, null);
            }
        });
        addTab.setClosable(false);
        addTab.setGraphic(newTabButton);
        return addTab;
    }

    public TabContent getSelectedTabContent() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            return null;
        }
        return currentlyOpenTab.stream().filter(tabContent -> tabContent.getTab().equals(selectedTab)).findFirst().orElse(null);
    }
}
