package com.huy.appnoithat.Controller.LuaChonNoiThat.Setup;

import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.CommandManager;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.implementation.BangThanhToan.*;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Utils.TableCalculationUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Converter.DecimalLongStringConverter;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangThanhToan;
import com.huy.appnoithat.Controller.LuaChonNoiThat.LuaChonNoiThatController;
import javafx.collections.FXCollections;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetupBangThanhToan {
    private final Logger LOGGER = LogManager.getLogger(this);

    private final TableView<BangThanhToan> bangThanhToan;
    private final TableColumn<BangThanhToan, Long> DatCocThiCong30, DatCocThietKe10, HangDenChanCongTrinh50, NghiemThuQuyet, TongTien;
    private final BangThanhToan.Percentage percentage = BangThanhToan.Percentage.createDefault();
    private final CommandManager commandManager;

    public SetupBangThanhToan(LuaChonNoiThatController luaChonNoiThatController) {
        bangThanhToan = luaChonNoiThatController.getBangThanhToan();
        DatCocThiCong30 = luaChonNoiThatController.getDatCocThiCong30();
        DatCocThietKe10 = luaChonNoiThatController.getDatCocThietKe10();
        HangDenChanCongTrinh50 = luaChonNoiThatController.getHangDenChanCongTrinh50();
        NghiemThuQuyet = luaChonNoiThatController.getNghiemThuQuyet();
        TongTien = luaChonNoiThatController.getTongTien();
        commandManager = luaChonNoiThatController.getCommandManager();
    }

    /**
     * Set up the BangThanhToan table with custom cell factories and cell value factories.
     * Also sets up the row factory to make the rows bold.
     */
    public void setUpBangThanhToan() {
        bangThanhToan.setEditable(true);
        MenuItem DatCocThietKePercent = new MenuItem("Thay đổi phần trăm đặt cọc thiết kế");
        DatCocThietKePercent.setOnAction((event) -> {
            commandManager.execute(new EditDatCocThietKePercentCommand(percentage));
        });
        MenuItem DatCocThiCongPercent = new MenuItem("Thay đổi phần trăm đặt cọc thi công");
        DatCocThiCongPercent.setOnAction((event) -> {
            commandManager.execute(new EditDatCocThiCongPercentCommand(percentage));
        });
        MenuItem HangDenChanCongTrinhPercent = new MenuItem("Thay đổi phần trăm hàng đến chân công trình");
        HangDenChanCongTrinhPercent.setOnAction((event) -> {
            commandManager.execute(new EditHangDenChanCongTrinhPercentCommand(percentage));
        });
        MenuItem NghiemThuQuyetPercent = new MenuItem("Thay đổi phần trăm nghiệm thu quyết");
        NghiemThuQuyetPercent.setOnAction((event) -> {
            commandManager.execute(new EditNghiemThuQuyetCommand(percentage));
        });
        MenuItem recalculate = new MenuItem("Tính toán lại");
        recalculate.setOnAction((event) -> {
            commandManager.execute(new RecalculateCommand(bangThanhToan.getItems().get(0), percentage));
        });
        bangThanhToan.getContextMenu().getItems().addAll(DatCocThietKePercent, DatCocThiCongPercent, HangDenChanCongTrinhPercent, NghiemThuQuyetPercent, recalculate);

        DatCocThietKe10.setCellValueFactory(param -> param.getValue().getDatCocThietKe10().asObject());
        DatCocThietKe10.setCellFactory(param -> new TextFieldTableCell<>(new DecimalLongStringConverter()));
        DatCocThietKe10.setOnEditCommit(event -> {
            if (event.getRowValue() == null) {
                LOGGER.error("Row value of DatCocThietKe10 is null");
                return;
            }
            commandManager.execute(new EditBangThanhToan<>(event, event.getRowValue().getDatCocThietKe10()));
        });

        DatCocThiCong30.setCellValueFactory(param -> param.getValue().getDatCocThiCong30().asObject());
        DatCocThiCong30.setCellFactory(param -> new TextFieldTableCell<>(new DecimalLongStringConverter()));
        DatCocThiCong30.setOnEditCommit(event -> {
            if (event.getRowValue() == null) {
                LOGGER.error("Row value of DatCocThiCong30 is null");
                return;
            }
            commandManager.execute(new EditBangThanhToan<>(event, event.getRowValue().getDatCocThiCong30()));
        });

        HangDenChanCongTrinh50.setCellValueFactory(param -> param.getValue().getHangDenChanCongTrinh50().asObject());
        HangDenChanCongTrinh50.setCellFactory(param -> new TextFieldTableCell<>(new DecimalLongStringConverter()));
        HangDenChanCongTrinh50.setOnEditCommit(event -> {
            if (event.getRowValue() == null) {
                LOGGER.error("Row value of HangDenChanCongTrinh50 is null");
                return;
            }
            commandManager.execute(new EditBangThanhToan<>(event, event.getRowValue().getHangDenChanCongTrinh50()));
        });

        NghiemThuQuyet.setCellValueFactory(param -> param.getValue().getNghiemThuQuyet().asObject());
        NghiemThuQuyet.setCellFactory(param -> new TextFieldTableCell<>(new DecimalLongStringConverter()));
        NghiemThuQuyet.setOnEditCommit(event -> {
            if (event.getRowValue() == null) {
                LOGGER.error("Row value of NghiemThuQuyet is null");
                return;
            }
            commandManager.execute(new EditBangThanhToan<>(event, event.getRowValue().getNghiemThuQuyet()));
        });

        TongTien.setCellFactory(param -> new TextFieldTableCell<>(new DecimalLongStringConverter()));
        TongTien.setCellValueFactory(param -> param.getValue().getTongTien().asObject());
        TongTien.setEditable(false);
        bangThanhToan.setRowFactory(new Callback<>() {
            @Override
            public TableRow<BangThanhToan> call(TableView<BangThanhToan> param) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(BangThanhToan row1, boolean empty) {
                        super.updateItem(row1, empty);
                        setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
                    }
                };
            }
        });
        if (bangThanhToan.getItems().isEmpty()) {
            bangThanhToan.setItems(FXCollections.observableArrayList(BangThanhToan.empty()));
        }

        percentage.addListener((ignored, oldValue, newValue) -> updateBangThanhToan());
    }

    private void updateBangThanhToan() {
        DatCocThietKe10.setText(String.format("Đặt cọc thiết kế: %s", percentage.getDatCocThietKePercentage().get() + "%"));
        DatCocThiCong30.setText(String.format("Đặt cọc thi công: %s", percentage.getDatCocThiCongPercentage().get() + "%"));
        HangDenChanCongTrinh50.setText(String.format("Hàng đến chân công trình: %s", percentage.getHangDenChanCongTrinhPercentage().get() + "%"));
        TableCalculationUtils.calculateBangThanhToan(bangThanhToan, TongTien.getCellData(0));
    }
}
