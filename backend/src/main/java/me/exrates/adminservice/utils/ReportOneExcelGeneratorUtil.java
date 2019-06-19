package me.exrates.adminservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.domain.enums.UserStatus;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportOneExcelGeneratorUtil {

    private static final DateTimeFormatter FORMATTER_FOR_REPORT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH-mm");

    private static final String SHEET1_NAME = "Sheet1 - User information";

    public static byte[] generate(List<UserInfoDto> userInfoList) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);

        XSSFSheet sheet1 = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet1.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Id");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Nickname");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("IP регистрации");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("E-mail");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Страна");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Сумма на балансе в USD");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Дата регистрации");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Последний вход");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Номер телефона");
        cell.setCellStyle(header1Style);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Статус верификации");
        cell.setCellStyle(header1Style);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Тип юзера");
        cell.setCellStyle(header1Style);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Статус активности");
        cell.setCellStyle(header1Style);

        sheet1.autoSizeColumn(0, true);
        sheet1.setColumnWidth(0, sheet1.getColumnWidth(0) + 256);
        sheet1.autoSizeColumn(1, true);
        sheet1.setColumnWidth(1, sheet1.getColumnWidth(1) + 256);
        sheet1.autoSizeColumn(2, true);
        sheet1.setColumnWidth(2, sheet1.getColumnWidth(2) + 256);
        sheet1.autoSizeColumn(3, true);
        sheet1.setColumnWidth(3, sheet1.getColumnWidth(3) + 256);
        sheet1.autoSizeColumn(4, true);
        sheet1.setColumnWidth(4, sheet1.getColumnWidth(4) + 256);
        sheet1.autoSizeColumn(5, true);
        sheet1.setColumnWidth(5, sheet1.getColumnWidth(5) + 256);
        sheet1.autoSizeColumn(6, true);
        sheet1.setColumnWidth(6, sheet1.getColumnWidth(6) + 256);
        sheet1.autoSizeColumn(7, true);
        sheet1.setColumnWidth(7, sheet1.getColumnWidth(7) + 256);
        sheet1.autoSizeColumn(8, true);
        sheet1.setColumnWidth(8, sheet1.getColumnWidth(8) + 256);
        sheet1.autoSizeColumn(9, true);
        sheet1.setColumnWidth(9, sheet1.getColumnWidth(9) + 256);
        sheet1.autoSizeColumn(10, true);
        sheet1.setColumnWidth(10, sheet1.getColumnWidth(10) + 256);
        sheet1.autoSizeColumn(11, true);
        sheet1.setColumnWidth(11, sheet1.getColumnWidth(11) + 256);

        //body
        int i = 0;
        for (UserInfoDto userInfo : userInfoList) {
            final int userId = userInfo.getUserId();
            final String userNickname = userInfo.getUserNickname();
            final String registerIp = userInfo.getRegisterIp();
            final String email = userInfo.getEmail();
            final String country = userInfo.getCountry();
            final BigDecimal balanceSumUsd = userInfo.getBalanceSumUsd();
            final LocalDateTime registrationDate = userInfo.getRegistrationDate();
            final LocalDateTime lastEntryDate = userInfo.getLastEntryDate();
            final String phone = userInfo.getPhone();
            final String verificationStatus = userInfo.getVerificationStatus();
            final UserRole userRole = userInfo.getRole();
            final UserStatus userStatus = userInfo.getStatus();

            row = sheet1.createRow(i + 1);

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(userId);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(userNickname);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(registerIp);
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(email);
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(country);
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(balanceSumUsd.doubleValue());
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(registrationDate.format(FORMATTER_FOR_REPORT));
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(lastEntryDate.format(FORMATTER_FOR_REPORT));
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue(phone);
            cell.setCellStyle(body1Style);

            cell = row.createCell(9, CellType.STRING);
            cell.setCellValue(verificationStatus);
            cell.setCellStyle(body1Style);

            cell = row.createCell(10, CellType.STRING);
            cell.setCellValue(userRole.name());
            cell.setCellStyle(body1Style);

            cell = row.createCell(11, CellType.STRING);
            cell.setCellValue(userStatus.name());
            cell.setCellStyle(body1Style);

            i++;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            bos.close();
        } catch (IOException ex) {
            throw new Exception("Problem with convert workbook to byte array", ex);
        }
        return bos.toByteArray();
    }

    private static CellStyle getHeader1Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        font.setBold(true);
        headerStyle.setFont(font);

        headerStyle.setWrapText(true);

        return headerStyle;
    }

    private static CellStyle getBode1Style(XSSFWorkbook workbook) {
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        bodyStyle.setFont(font);

        return bodyStyle;
    }
}