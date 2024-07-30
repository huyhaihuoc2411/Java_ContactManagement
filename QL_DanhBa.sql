-- Tạo cơ sở dữ liệu
CREATE DATABASE QL_DanhBa;
GO

-- Sử dụng cơ sở dữ liệu vừa tạo
USE QL_DanhBa;
GO

-- Tạo bảng DanhBa
CREATE drop TABLE DanhBa (
    MaLienHe INT IDENTITY(1,1) PRIMARY KEY,
    Ten NVARCHAR(50),
    SoDienThoai NVARCHAR(15),
    Email NVARCHAR(100),
    DiaChi NVARCHAR(255),
    NgaySinh DATE
);
GO




-- Tạo bảng TaiKhoan
CREATE TABLE TaiKhoan (
    MaTaiKhoan INT IDENTITY(1,1) PRIMARY KEY,
    TenTaiKhoan NVARCHAR(50) NOT NULL,
    MatKhau NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL
);
GO

-- Tạo bảng liên kết TaiKhoanLienHe
CREATE TABLE TaiKhoanLienHe (
    MaTaiKhoan INT,
    MaLienHe INT,
    PRIMARY KEY (MaTaiKhoan, MaLienHe),
    FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan),
    FOREIGN KEY (MaLienHe) REFERENCES DanhBa(MaLienHe)
);
GO

-- Chèn dữ liệu vào bảng TaiKhoan
INSERT INTO TaiKhoan (TenTaiKhoan, MatKhau, Email)
VALUES
(N'admin', N'admin123', N'admin@gmail.com');
(N'user1', N'123', N'user1@gmail.com'),
(N'user2', N'123', N'user2@gmail.com'),
(N'user3', N'123', N'user3@gmail.com');
GO
select * from TaiKhoan


-- Chèn dữ liệu vào bảng TaiKhoanLienHe
-- Đảm bảo rằng các giá trị MaTaiKhoan và MaLienHe tồn tại trong các bảng TaiKhoan và DanhBa
INSERT INTO TaiKhoanLienHe (MaTaiKhoan, MaLienHe)
VALUES
(1, 171),
(1, 172),
(2, 175),
(2, 176),
(3, 177),
(3, 178);
GO

select * from danhba
delete from DanhBa

-- Thiết lập định dạng ngày tháng
SET DATEFORMAT dmy;

-- Chèn dữ liệu vào bảng DanhBa
INSERT INTO DanhBa (Ten, SoDienThoai, Email, DiaChi, NgaySinh)
VALUES
(N'Thanh D', N'0987654321', N'thanh.d@gmail.com', N'TP. Hồ Chí Minh', '04-04-1996'),
(N'Nam E', N'0123456789', N'nam.e@gmail.com', N'Hà Nội', '05-05-1998'),
(N'Hoa F', N'0912345678', N'hoa.f@gmail.com', N'Đà Nẵng', '06-06-2000'),
(N'Tuan G', N'0987654321', N'tuan.g@gmail.com', N'Cần Thơ', '07-07-2002'),
(N'Huyen H', N'0123456789', N'huyen.h@gmail.com', N'Hải Phòng', '08-08-2004'),
(N'Quang I', N'0912345678', N'quang.i@gmail.com', N'Bình Dương', '09-09-2006'),
(N'Linh J', N'0987654321', N'linh.j@gmail.com', N'Đồng Nai', '10-10-2008'),
(N'Tam K', N'0123456789', N'tam.k@gmail.com', N'Tiền Giang', '11-11-2010'),
(N'Khanh L', N'0912345678', N'khanh.l@gmail.com', N'Lâm Đồng', '12-12-2012'),
(N'Thu M', N'0987654321', N'thu.m@gmail.com', N'Thừa Thiên Huế', '01-01-2014');



GO

-- Kiểm tra dữ liệu đã chèn vào bảng DanhBa
SELECT * FROM DanhBa;
GO



-- Tạo stored procedure để lấy thông tin liên hệ của mỗi tài khoản
CREATE PROCEDURE sp_LayThongTinLienHeCuaTaiKhoan
AS
BEGIN
    SELECT 
        TK.TenTaiKhoan,
        TK.Email AS EmailTaiKhoan,
        DB.Ten AS TenLienHe,
        DB.SoDienThoai,
        DB.Email AS EmailLienHe,
        DB.DiaChi,
        DB.NgaySinh
    FROM 
        TaiKhoan TK
    JOIN 
        TaiKhoanLienHe TKLH ON TK.MaTaiKhoan = TKLH.MaTaiKhoan
    JOIN 
        DanhBa DB ON TKLH.MaLienHe = DB.MaLienHe;
END;
GO

-- Gọi stored procedure để lấy thông tin liên hệ của mỗi tài khoản
EXEC sp_LayThongTinLienHeCuaTaiKhoan;
GO

CREATE TRIGGER trg_AfterInsertDanhBa
ON DanhBa
AFTER INSERT
AS
BEGIN
    -- Khai báo biến để lưu trữ mã liên hệ và mã tài khoản
    DECLARE @MaLienHe INT;
    DECLARE @MaTaiKhoan INT;

    -- Lấy mã liên hệ từ bản ghi mới được chèn vào bảng DanhBa
    SELECT @MaLienHe = inserted.MaLienHe
    FROM inserted;

    -- Lấy mã tài khoản từ bảng TaiKhoanLienHe dựa trên mã liên hệ
    SELECT @MaTaiKhoan = MaTaiKhoan
    FROM TaiKhoanLienHe
    WHERE MaLienHe = @MaLienHe;

    -- Kiểm tra xem mã tài khoản đã tồn tại hay không
    IF (@MaTaiKhoan IS NOT NULL)
    BEGIN
        -- Nếu tồn tại, không cần làm gì
        RETURN;
    END
    ELSE
    BEGIN
        -- Nếu không tồn tại, lấy mã tài khoản từ bảng TaiKhoanLienHe với mã tài khoản là số lượng bản ghi + 1
        SELECT @MaTaiKhoan = COALESCE(MAX(MaTaiKhoan), 0) + 1
        FROM TaiKhoanLienHe;

        -- Thêm bản ghi mới vào bảng TaiKhoanLienHe
        INSERT INTO TaiKhoanLienHe (MaTaiKhoan, MaLienHe)
        VALUES (@MaTaiKhoan, @MaLienHe);
    END
END;

CREATE TRIGGER trg_XoaTaiKhoanLienHe
ON DanhBa
AFTER DELETE
AS
BEGIN
    DELETE FROM TaiKhoanLienHe WHERE MaLienHe IN (SELECT deleted.MaLienHe FROM deleted);
END;

CREATE PROCEDURE sp_Admin
AS
BEGIN
    -- Khai báo biến để lưu trữ MaTaiKhoan của admin
    DECLARE @AdminTaiKhoan INT;
    
    -- Kiểm tra nếu tài khoản admin đã tồn tại
    IF EXISTS (SELECT 1 FROM TaiKhoan WHERE TenTaiKhoan = 'admin')
    BEGIN
        -- Nếu tồn tại, lấy MaTaiKhoan của admin
        SELECT @AdminTaiKhoan = MaTaiKhoan FROM TaiKhoan WHERE TenTaiKhoan = 'admin';
    END
    ELSE
    BEGIN
        -- Nếu không tồn tại, tạo tài khoản admin mới
        INSERT INTO TaiKhoan (TenTaiKhoan, MatKhau, Email)
        VALUES (N'admin', N'admin123', N'admin@gmail.com');
        
        -- Lấy MaTaiKhoan của tài khoản admin vừa tạo
        SELECT @AdminTaiKhoan = SCOPE_IDENTITY();
    END
    
    -- Liên kết tất cả các liên hệ hiện có với tài khoản admin
    INSERT INTO TaiKhoanLienHe (MaTaiKhoan, MaLienHe)
    SELECT @AdminTaiKhoan, MaLienHe 
    FROM DanhBa
    WHERE MaLienHe NOT IN (SELECT MaLienHe FROM TaiKhoanLienHe WHERE MaTaiKhoan = @AdminTaiKhoan);
END;
GO

EXEC sp_Admin;
GO

-- Xóa trigger cũ nếu có
DROP TRIGGER IF EXISTS trg_AfterInsertDanhBa;
GO

-- Tạo trigger mới để tự động thêm liên hệ mới vào tài khoản admin
CREATE TRIGGER trg_AfterInsertDanhBa
ON DanhBa
AFTER INSERT
AS
BEGIN
    DECLARE @AdminTaiKhoan INT;
    
    -- Lấy MaTaiKhoan của tài khoản admin
    SELECT @AdminTaiKhoan = MaTaiKhoan FROM TaiKhoan WHERE TenTaiKhoan = 'admin';
    
    -- Chèn tất cả các liên hệ mới vào bảng TaiKhoanLienHe với MaTaiKhoan là admin
    INSERT INTO TaiKhoanLienHe (MaTaiKhoan, MaLienHe)
    SELECT @AdminTaiKhoan, MaLienHe FROM inserted;
END;
GO

CREATE PROCEDURE TimKiemDanhBa
    @ColumnName NVARCHAR(50),
    @Keyword NVARCHAR(100),
    @MaTaiKhoan INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @sql NVARCHAR(MAX);
    SET @sql = '
        SELECT db.MaLienHe, db.Ten, db.SoDienThoai, db.Email, db.DiaChi, db.NgaySinh
        FROM DanhBa db
        JOIN TaiKhoanLienHe tklh ON db.MaLienHe = tklh.MaLienHe
        WHERE tklh.MaTaiKhoan = @MaTaiKhoan AND ' + @ColumnName + ' LIKE ''%'' + @Keyword + ''%'';
    ';
    
    EXEC sp_executesql @sql, N'@MaTaiKhoan INT, @Keyword NVARCHAR(100)', @MaTaiKhoan, @Keyword;
END;

