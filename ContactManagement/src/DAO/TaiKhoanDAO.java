/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TaiKhoanDAO extends SQLServerDataProvider{
    
   public static boolean kiemTraDangNhap(String tenTaiKhoan, String matKhau) {
        boolean isValid = false;
        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        Connection con = provider.getConnection();
        try {
            String sql = "SELECT * FROM TaiKhoan WHERE TenTaiKhoan = ? AND MatKhau = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenTaiKhoan);
            ps.setString(2, matKhau);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            provider.close();
        }
        return isValid;
    }
    public static int getMaTaiKhoan(String tenTaiKhoan) {
        int maTaiKhoan = -1; // Giá trị mặc định khi không tìm thấy
        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        Connection con = provider.getConnection();
        try {
            String sql = "SELECT MaTaiKhoan FROM TaiKhoan WHERE TenTaiKhoan = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenTaiKhoan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maTaiKhoan = rs.getInt("MaTaiKhoan");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            provider.close();
        }
        return maTaiKhoan;
    }
    public static boolean themTaiKhoan(String tenTaiKhoan, String matKhau, String email) {
        boolean success = false;
        SQLServerDataProvider provider = new SQLServerDataProvider();
        provider.open();
        Connection con = provider.getConnection();
        try {
            String sql = "INSERT INTO TaiKhoan (TenTaiKhoan, MatKhau, Email) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenTaiKhoan);
            ps.setString(2, matKhau);
            ps.setString(3, email);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                success = true; // Đã thêm thành công
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            provider.close();
        }
        return success;
    }

}

    