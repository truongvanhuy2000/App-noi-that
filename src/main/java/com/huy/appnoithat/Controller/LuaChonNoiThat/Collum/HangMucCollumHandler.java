package com.huy.appnoithat.Controller.LuaChonNoiThat.Collum;

import com.huy.appnoithat.Common.PopupUtils;
import com.huy.appnoithat.Common.Utils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Cell.CustomHangMucCell;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.Command;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.CommandManager;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.implementation.EditCommitHangMucCommand;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Common.ItemTypeUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Common.TableCalculationUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Common.TableUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Constant.ItemType;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangNoiThat;
import com.huy.appnoithat.DataModel.Entity.HangMuc;
import com.huy.appnoithat.DataModel.Entity.ThongSo;
import com.huy.appnoithat.DataModel.Entity.VatLieu;
import com.huy.appnoithat.Service.LuaChonNoiThat.LuaChonNoiThatService;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HangMucCollumHandler {
    private final ObservableList<String> hangMucList;
    private final LuaChonNoiThatService luaChonNoiThatService;
    private final CommandManager commandManager;
    final static Logger LOGGER = LogManager.getLogger(HangMucCollumHandler.class);

    /**
     * Constructs a HangMucCollumHandler with the given ObservableList of hangMucList.
     *
     * @param hangMucList The ObservableList of String representing hangMucList data.
     */

    @Builder
    public HangMucCollumHandler(
            ObservableList<String> hangMucList,
            LuaChonNoiThatService luaChonNoiThatService,
            CommandManager commandManager

    ) {
        this.hangMucList = hangMucList;
        this.luaChonNoiThatService = luaChonNoiThatService;
        this.commandManager = commandManager;
    }

    /**
     * Handles cell edit commit events for the HangMuc column in the TreeTableView.
     * Updates the hangMuc property of the corresponding BangNoiThat object with the new value.
     *
     * @param event The CellEditEvent containing information about the edit event.
     */
    public void onEditCommitHangMuc(TreeTableColumn.CellEditEvent<BangNoiThat, String> event) {
        Command command = new EditCommitHangMucCommand(luaChonNoiThatService, event);
        commandManager.push(command);
        command.execute();
    }

    /**
     * Handles the event when a cell in the 'HangMuc' TreeTableColumn is being edited.
     * Dynamically populates 'hangMucList' based on the type of the edited item (noi that, phong cach, or hang muc).
     *
     * @param event The CellEditEvent containing information about the editing event.
     */
    public void onStartEditHangMuc(TreeTableColumn.CellEditEvent<BangNoiThat, String> event) {
        TreeItem<BangNoiThat> currentItem = event.getRowValue();
        List<String> items;
        hangMucList.clear();
        // Roman mean it's a noi that, mean that its parent is phong cach
        switch (ItemTypeUtils.determineItemType(currentItem)) {
            case ROMAN -> {
                String phongCach = currentItem.getParent().getValue().getHangMuc().getValue();
                items = Utils.getObjectNameList(luaChonNoiThatService.findNoiThatListBy(phongCach));
                hangMucList.addAll(items);
            }
            case AlPHA -> {
                items = Utils.getObjectNameList(luaChonNoiThatService.findAllPhongCachNoiThat());
                hangMucList.clear();
                hangMucList.addAll(items);
            }
            case NUMERIC -> {
                String noiThat = currentItem.getParent().getValue().getHangMuc().getValue();
                String phongCach = currentItem.getParent().getParent().getValue().getHangMuc().getValue();
                items = Utils.getObjectNameList(luaChonNoiThatService.findHangMucListBy(phongCach, noiThat));
                hangMucList.clear();
                hangMucList.addAll(items);
            }
            default -> {
            }
        }
    }

    /**
     * Provides a custom cell factory for the HangMuc column in the TreeTableView.
     * Initializes and returns a new instance of CustomHangMucCell with the specified 'hangMucList'.
     *
     * @param param The TreeTableColumn instance for which the custom cell factory is provided.
     * @return A new CustomHangMucCell instance with the given 'hangMucList'.
     */
    public TreeTableCell<BangNoiThat, String> getCustomCellFactory(TreeTableColumn<BangNoiThat, String> param) {
        return new CustomHangMucCell(hangMucList);
    }


    /**
     * Provides a custom cell value factory for the HangMuc column in the TreeTableView.
     * Retrieves the 'hangMuc' property value from the BangNoiThat object associated with the current row.
     * Returns an ObservableValue<String> representing the 'hangMuc' property of the cell data features.
     *
     * @param param The CellDataFeatures instance representing the data for the current cell.
     * @return An ObservableValue<String> representing the 'hangMuc' property of the current cell's data.
     * Returns null if the current row's data is null.
     */
    public ObservableValue<String> getCustomCellValueFactory(TreeTableColumn.CellDataFeatures<BangNoiThat, String> param) {
        if (param.getValue() == null) {
            return null;
        }
        return param.getValue().getValue().getHangMuc();
    }
}
