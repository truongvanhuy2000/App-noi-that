package com.huy.appnoithat.Controller.LuaChonNoiThat.Cell;

import com.huy.appnoithat.Common.KeyboardUtils;
import com.huy.appnoithat.Common.Utils;
import com.huy.appnoithat.Enums.Action;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;

public class CustomEditingCell<BangNoiThat> extends TreeTableCell<BangNoiThat, String> {
    private TextField textField;
    boolean isSttCell = false;
    public CustomEditingCell(boolean isSttCell) {
        this.isSttCell = isSttCell;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(item);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                    setGraphic(null);
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
        TreeTableRow<BangNoiThat> currentRow = getTableRow();
        if (!isEmpty()) {
            if (!isSttCell) {
                return;
            }
            if (Utils.isNumeric(getItem())) {
                currentRow.setStyle("-fx-font-weight: normal");
                return;
            }
            if (Utils.RomanNumber.isRoman(getItem())) {
                currentRow.setStyle("-fx-font-weight: bold");
                return;
            }
            if (Utils.isAlpha(getItem())) {
                currentRow.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
                return;
            }
        }
    }

    private void createTextField() {
        if (textField != null) {
            return;
        }
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnAction((e) -> commitEdit(textField.getText()));
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                commitEdit(textField.getText());
            }
        });
        textField.setOnKeyPressed((key) -> {
            if (KeyboardUtils.isRightKeyCombo(Action.SAVE, key)) {
                commitEdit(textField.getText());
                updateItem(textField.getText(), false);
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
