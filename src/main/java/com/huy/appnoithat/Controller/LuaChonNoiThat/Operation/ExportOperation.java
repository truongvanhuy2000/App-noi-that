package com.huy.appnoithat.Controller.LuaChonNoiThat.Operation;

import com.huy.appnoithat.Common.PopupUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.Common.TableUtils;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangNoiThat;
import com.huy.appnoithat.Controller.LuaChonNoiThat.DataModel.BangThanhToan;
import com.huy.appnoithat.Controller.LuaChonNoiThat.LuaChonNoiThatController;
import com.huy.appnoithat.DataModel.*;
import com.huy.appnoithat.Enums.FileType;
import com.huy.appnoithat.Service.LuaChonNoiThat.LuaChonNoiThatService;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class ExportOperation {
    @NonNull
    private TextField TenCongTy, VanPhong, DiaChiXuong, DienThoaiCongTy, Email;
    @NonNull
    private ImageView ImageView;
    @NonNull
    private TextField TenKhachHang, DienThoaiKhachHang, DiaChiKhachHang, NgayLapBaoGia, SanPham;
    @NonNull
    private TreeTableView<BangNoiThat> TableNoiThat;
    @NonNull
    private TableView<BangThanhToan> bangThanhToan;
    @NonNull
    private TextArea noteTextArea;
    LuaChonNoiThatController luaChonNoiThatController;
    public ExportOperation(LuaChonNoiThatController luaChonNoiThatController) {
        TenCongTy = luaChonNoiThatController.getTenCongTy();
        VanPhong = luaChonNoiThatController.getVanPhong();
        DiaChiXuong = luaChonNoiThatController.getDiaChiXuong();
        DienThoaiCongTy = luaChonNoiThatController.getDienThoaiCongTy();
        Email = luaChonNoiThatController.getEmail();
        ImageView = luaChonNoiThatController.getImageView();
        TenKhachHang = luaChonNoiThatController.getTenKhachHang();
        DienThoaiKhachHang = luaChonNoiThatController.getDienThoaiKhachHang();
        DiaChiKhachHang = luaChonNoiThatController.getDiaChiKhachHang();
        NgayLapBaoGia = luaChonNoiThatController.getNgayLapBaoGia();
        SanPham = luaChonNoiThatController.getSanPham();
        TableNoiThat = luaChonNoiThatController.getTableNoiThat();
        bangThanhToan = luaChonNoiThatController.getBangThanhToan();
        noteTextArea = luaChonNoiThatController.getNoteTextArea();

        this.luaChonNoiThatController = luaChonNoiThatController;
    }

    public String exportFile(FileType fileType) {
        File selectedFile = PopupUtils.fileSaver();
        if (selectedFile == null) {
            return null;
        }
        return exportFile(fileType, selectedFile, true);
    }
    public String exportFile(FileType fileType, File selectedFile, boolean showPopup) {
        DataPackage dataPackage = new DataPackage(
                getThongTinCongTy(),
                getThongTinKhachHang(),
                noteTextArea.getText(),
                getThongTinNoiThatList(),
                getThongTinThanhToan()
        );
        boolean result = new LuaChonNoiThatService().exportFile(selectedFile, fileType, dataPackage);
        if (!result) {
            if (showPopup) {
                if (fileType == FileType.NT) {
                    PopupUtils.throwErrorSignal("Lưu thất bại");
                }
                else {
                    PopupUtils.throwErrorSignal("Xuất file thất bại");
                }
            }
            return null;
        }
        else {
            if (showPopup) {
                if (fileType == FileType.NT) {
                    PopupUtils.throwSuccessSignal("Lưu file thành công");
                }
                else {
                    PopupUtils.throwSuccessSignal("Xuất file thành công");
                }
            }
        }
        return selectedFile.getAbsolutePath();
    }
    /**
     * THis function will return a list of ThongTinNoiThat from item root from the table
     *
     * @return
     */
    private List<ThongTinNoiThat> getThongTinNoiThatList() {
        // This function will return a list of ThongTinNoiThat
        List<ThongTinNoiThat> listNoiThat = new ArrayList<>();
        // Get a list of phong cach item
        TableNoiThat.getRoot().getChildren().forEach(
                item -> {
                    listNoiThat.add(TableUtils.convertFromTreeItem(item));
                    item.getChildren().forEach(
                            item1 -> {
                                listNoiThat.add(TableUtils.convertFromTreeItem(item1));
                                item1.getChildren().forEach(
                                        item2 -> {
                                            listNoiThat.add(TableUtils.convertFromTreeItem(item2));
                                        }
                                );
                            }
                    );
                }
        );
        return listNoiThat;
    }

    /**
     * This function will return ThongTinThanhToan from bang thanh toan
     *
     * @return
     */
    private ThongTinThanhToan getThongTinThanhToan() {
        return new ThongTinThanhToan(
                bangThanhToan.getItems().get(0));
    }

    /**
     * This function will return ThongTinCongTy from thong tin cong ty text field
     *
     * @return
     */
    private ThongTinCongTy getThongTinCongTy() {

        return new ThongTinCongTy(
                new ByteArrayInputStream(luaChonNoiThatController.getImageStream().toByteArray()),
                TenCongTy.getText(),
                VanPhong.getText(),
                DiaChiXuong.getText(),
                DienThoaiCongTy.getText(),
                Email.getText()
        );
    }
    /**
     * This function will return ThongTinKhachHang from thong tin khach hang text field
     *
     * @return
     */
    private ThongTinKhachHang getThongTinKhachHang() {
        return new ThongTinKhachHang(
                TenKhachHang.getText(),
                DiaChiKhachHang.getText(),
                DienThoaiKhachHang.getText(),
                NgayLapBaoGia.getText(),
                SanPham.getText()
        );
    }
}
