/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package POJO;

import java.util.Date;

/**
 *
 * @author quochuy
 */
public class DanhBa {
    public int MaLienHe;
    public String Ten;
    public int SoDienThoai;
    public String Email;
    public String DiaChi;
    public Date NgaySinh;

    public DanhBa() {
    }

    public DanhBa(int MaLienHe, String Ten, int SoDienThoai, String Email, String DiaChi, Date NgaySinh) {
        this.MaLienHe = MaLienHe;
        this.Ten = Ten;
        this.SoDienThoai = SoDienThoai;
        this.Email = Email;
        this.DiaChi = DiaChi;
        this.NgaySinh = NgaySinh;
    }
    

    public int getMaLienHe() {
        return MaLienHe;
    }

    public void setMaLienHe(int MaLienHe) {
        this.MaLienHe = MaLienHe;
    }

    public String getTen() {
        return Ten;
    }

    public void setTen(String Ten) {
        this.Ten = Ten;
    }

    public int getSoDienThoai() {
        return SoDienThoai;
    }

    public void setSoDienThoai(int SoDienThoai) {
        this.SoDienThoai = SoDienThoai;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String DiaChi) {
        this.DiaChi = DiaChi;
    }

    public Date getNgaySinh() {
        return NgaySinh;
    }

    public void setNgaySinh(Date NgaySinh) {
        this.NgaySinh = NgaySinh;
    }
    
    
}
