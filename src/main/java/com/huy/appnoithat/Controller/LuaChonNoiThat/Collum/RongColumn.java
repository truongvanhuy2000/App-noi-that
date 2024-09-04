package com.huy.appnoithat.Controller.LuaChonNoiThat.Collum;

import com.huy.appnoithat.Controller.LuaChonNoiThat.Cell.CustomNumberCell;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.CommandManager;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Command.implementation.EditCommitRongCommand;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangNoiThat;
import javafx.scene.control.TreeTableColumn;
import javafx.util.converter.DoubleStringConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RongColumn implements CustomColumn{
    private final TreeTableColumn<BangNoiThat, Double> Rong;
    private final CommandManager commandManager;

    @Override
    public void setup() {
        Rong.setCellValueFactory(param -> {
            if (param.getValue() == null) return null;
            return param.getValue().getValue().getRong().asObject();
        });
        Rong.setCellFactory(param -> new CustomNumberCell<>(new DoubleStringConverter(), Rong.getTreeTableView(), true));
        Rong.setOnEditCommit(event -> {
            commandManager.execute(new EditCommitRongCommand(event));
        });
    }
}
