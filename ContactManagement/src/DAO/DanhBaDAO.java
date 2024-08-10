    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import POJO.DanhBa;

import java.sql.*;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author quochuy
 */
public class DanhBaDAO {
    public static ArrayList<DanhBa> showdsDB(int maTaiKhoan) {
    ArrayList<DanhBa> dsDB = new ArrayList<>();
    try {
        // SQL query to get contacts by user account
        String sql = "SELECT db.MaLienHe, db.Ten, db.SoDienThoai, db.Email, db.DiaChi, db.NgaySinh " +
                     "FROM DanhBa db " +
                     "JOIN TaiKhoanLienHe tklh ON db.MaLienHe = tklh.MaLienHe " +
                     "WHERE tklh.MaTaiKhoan = ?";
        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        PreparedStatement ps = provider.getConnection().prepareStatement(sql);
        ps.setInt(1, maTaiKhoan); // Thiết lập giá trị cho tham số ? ở vị trí đầu tiên
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            DanhBa db = new DanhBa();
            db.setMaLienHe(rs.getInt("MaLienHe"));
            db.setTen(rs.getString("Ten"));
            db.setSoDienThoai(rs.getInt("SoDienThoai")); // Changed to String
            db.setEmail(rs.getString("Email"));
            db.setDiaChi(rs.getString("DiaChi"));
            db.setNgaySinh(rs.getDate("NgaySinh"));
            dsDB.add(db);
        }
        rs.close();
        ps.close();
        provider.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return dsDB;
}

    public static boolean themDanhBa(DanhBa db, int maTaiKhoan) {
        boolean kq = false;
        String sql = String.format("INSERT INTO DanhBa(Ten, SoDienThoai, Email, DiaChi, NgaySinh) VALUES('%s', %d, '%s', '%s', '%s');",
                db.getTen(), db.getSoDienThoai(), db.getEmail(), db.getDiaChi(), new SimpleDateFormat("yyyy-MM-dd").format(db.getNgaySinh()));

        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        int n = provider.executeUpdate(sql);
        if (n == 1) {
            // Thêm mã liên hệ vào bảng TaiKhoanLienHe với mã tài khoản tương ứng
            String sqlTaiKhoanLienHe = String.format("INSERT INTO TaiKhoanLienHe(MaTaiKhoan, MaLienHe) VALUES(%d, @@IDENTITY);", maTaiKhoan);
            int result = provider.executeUpdate(sqlTaiKhoanLienHe);
            if (result == 1) {
                kq = true;
            }
        }
        provider.close();
        return kq;
    }


    public static int getMaxMaLienHeFromDatabase() {
        int maxMaLienHe = 0;
        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        Connection connection = provider.getConnection();
        try {
            String sql = "SELECT MAX(MaLienHe) AS MaxMaLienHe FROM DanhBa";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maxMaLienHe = rs.getInt("MaxMaLienHe");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            provider.close();
        }
        return maxMaLienHe;
    }

    public static boolean xoaDanhBa(int id) {
        boolean kq = false;
        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        Connection con = provider.getConnection();
        try {
            // Xóa liên kết từ bảng TaiKhoanLienHe
            String sqlDeleteLienKet = "DELETE FROM TaiKhoanLienHe WHERE MaLienHe = ?";
            PreparedStatement psDeleteLienKet = con.prepareStatement(sqlDeleteLienKet);
            psDeleteLienKet.setInt(1, id);
            psDeleteLienKet.executeUpdate();
            psDeleteLienKet.close();

            // Sau khi xóa liên kết, tiến hành xóa dữ liệu từ bảng DanhBa
            String sqlDeleteDanhBa = "DELETE FROM DanhBa WHERE MaLienHe = ?";
            PreparedStatement psDeleteDanhBa = con.prepareStatement(sqlDeleteDanhBa);
            psDeleteDanhBa.setInt(1, id);
            int n = psDeleteDanhBa.executeUpdate();

            if (n == 1) {
                kq = true;
            }
            psDeleteDanhBa.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            provider.close();
        }
        return kq;  
    }
   public static boolean xoaTatCaDanhBaTheoMaTaiKhoan(int maTaiKhoan) {
    boolean kq = false;
    SQLServerDataProvider provider = new SQLServerDataProvider();
    provider.open();
    Connection con = provider.getConnection();
    try {
        // Xóa liên kết từ bảng TaiKhoanLienHe dựa trên mã tài khoản
        String sqlDeleteLienKet = "DELETE FROM TaiKhoanLienHe WHERE MaTaiKhoan = ?";
        PreparedStatement psDeleteLienKet = con.prepareStatement(sqlDeleteLienKet);
        psDeleteLienKet.setInt(1, maTaiKhoan);
        psDeleteLienKet.executeUpdate();
        psDeleteLienKet.close();

        // Sau khi xóa liên kết, tiến hành xóa dữ liệu từ bảng DanhBa dựa trên mã tài khoản
        String sqlDeleteDanhBa = "DELETE FROM DanhBa WHERE MaLienHe IN (SELECT MaLienHe FROM TaiKhoanLienHe WHERE MaTaiKhoan = ?)";
        PreparedStatement psDeleteDanhBa = con.prepareStatement(sqlDeleteDanhBa);
        psDeleteDanhBa.setInt(1, maTaiKhoan);
        int n = psDeleteDanhBa.executeUpdate();

        if (n > 0) {
            kq = true;
        }
        psDeleteDanhBa.close();
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        provider.close();
    }
    return kq;  
}


    public static boolean capNhatDanhBa(DanhBa db) {
        boolean kq = false;
        String sql = String.format(
            "UPDATE DanhBa SET Ten = '%s', SoDienThoai = %d, Email = '%s', DiaChi = '%s', NgaySinh = '%s' WHERE MaLienHe = %d;",
            db.getTen(), db.getSoDienThoai(), db.getEmail(), db.getDiaChi(),
            new SimpleDateFormat("yyyy-MM-dd").format(db.getNgaySinh()), db.getMaLienHe()
        );

        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        int n = provider.executeUpdate(sql);
        if (n == 1) {
            kq = true;
        }
        provider.close();
        return kq;
    }
    public static ArrayList<DanhBa> timKiemDanhBa(String columnName, String keyword, int maTaiKhoan) {
        ArrayList<DanhBa> dsDB = new ArrayList<>();
        try {
            // Tạo câu lệnh SQL tương ứng với tên cột và từ khóa tìm kiếm
            String sql = "SELECT db.MaLienHe, db.Ten, db.SoDienThoai, db.Email, db.DiaChi, db.NgaySinh " +
                        "FROM DanhBa db " +
                        "JOIN TaiKhoanLienHe tklh ON db.MaLienHe = tklh.MaLienHe " +
                        "WHERE tklh.MaTaiKhoan = ? AND " + columnName + " LIKE ?";
            
                        

            
            // Mở kết nối đến cơ sở dữ liệu
            SQLServerDataProvider provider = new SQLServerDataProvider();
            provider.open();
            
            // Tạo và thiết lập giá trị cho câu lệnh PreparedStatement
            PreparedStatement ps = provider.getConnection().prepareStatement(sql);
            ps.setInt(1, maTaiKhoan);
            ps.setString(2, "%" + keyword + "%");
            
            // Thực thi truy vấn
            ResultSet rs = ps.executeQuery();
            
            // Duyệt qua các kết quả trả về và thêm vào danh sách danh bạ
            while (rs.next()) {
                DanhBa db = new DanhBa();
                db.setMaLienHe(rs.getInt("MaLienHe"));
                db.setTen(rs.getString("Ten"));
                db.setSoDienThoai(rs.getInt("SoDienThoai"));
                db.setEmail(rs.getString("Email"));
                db.setDiaChi(rs.getString("DiaChi"));
                db.setNgaySinh(rs.getDate("NgaySinh"));
                dsDB.add(db);
            }
            
            // Đóng kết nối và các tài nguyên liên quan
            rs.close();
            ps.close();
            provider.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsDB;
    }
    public static void exportToExcel(List<DanhBa> danhBaList, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DanhBa");

        // Tạo hàng đầu tiên (header)
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã Liên Hệ");
        headerRow.createCell(1).setCellValue("Họ Tên");
        headerRow.createCell(2).setCellValue("Số Điện Thoại");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Địa Chỉ");
        headerRow.createCell(5).setCellValue("Ngày Sinh");

        // Điền dữ liệu vào các hàng tiếp theo
        int rowNum = 1;
        for (DanhBa danhBa : danhBaList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(danhBa.getMaLienHe());
            row.createCell(1).setCellValue(danhBa.getTen());
            row.createCell(2).setCellValue(danhBa.getSoDienThoai());
            row.createCell(3).setCellValue(danhBa.getEmail());
            row.createCell(4).setCellValue(danhBa.getDiaChi());
            Cell ngaySinhCell = row.createCell(5);
            ngaySinhCell.setCellValue(danhBa.getNgaySinh());
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));
            ngaySinhCell.setCellStyle(cellStyle);
        }

        // Ghi ra file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Xuất file thành công: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xuất file: " + e.getMessage());
        }
    }
    public static List<DanhBa> importFromExcel(String filePath) {
        List<DanhBa> danhBaList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua hàng đầu tiên (header)
            if (rows.hasNext()) {
                rows.next();
            }

            // Đọc dữ liệu từ các hàng tiếp theo
            while (rows.hasNext()) {
                Row row = rows.next();
                int maLienHe = (int) row.getCell(0).getNumericCellValue();
                String hoTen = row.getCell(1).getStringCellValue();
                int soDienThoai = (int) row.getCell(2).getNumericCellValue();
                String email = row.getCell(3).getStringCellValue();
                String diaChi = row.getCell(4).getStringCellValue();
                Date ngaySinh = row.getCell(5).getDateCellValue();

                DanhBa danhBa = new DanhBa(maLienHe, hoTen, soDienThoai, email, diaChi, ngaySinh);
                danhBaList.add(danhBa);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return danhBaList;
    }
     public static ArrayList<String> layDanhSachTenLienHe() {
        ArrayList<String> danhSachTen = new ArrayList<>();
        // Gọi phương thức để lấy danh sách liên hệ từ bảng DanhBa
        ArrayList<DanhBa> danhSachDB = showdsDB(); // Giả sử showdsDB() là phương thức lấy danh sách liên hệ từ bảng DanhBa
        // Lặp qua danh sách liên hệ và thêm tên của mỗi liên hệ vào danh sách kết quả
        for (DanhBa db : danhSachDB) {
            danhSachTen.add(db.getTen());
        }
        return danhSachTen;
    }
     public static ArrayList<DanhBa> showdsDB() {
        ArrayList<DanhBa> dsDB = new ArrayList<>();
        try {
            String sql = "SELECT Ten, SoDienThoai, Email, DiaChi, NgaySinh FROM DanhBa";
            SQLServerDataProvider provider = new SQLServerDataProvider();
            provider.open();
            Statement statement = provider.getConnection().createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                DanhBa db = new DanhBa();
                db.setTen(rs.getString("Ten"));
                db.setSoDienThoai(rs.getInt("SoDienThoai")); // Đảm bảo là phương thức getSoDienThoai trả về String
                db.setEmail(rs.getString("Email"));
                db.setDiaChi(rs.getString("DiaChi"));
                db.setNgaySinh(rs.getDate("NgaySinh"));
                dsDB.add(db);
            }
            rs.close();
            statement.close();
            provider.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsDB;
    }
}
