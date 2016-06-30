package d.swan.mobileseller;

import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by daniel on 6/12/16.
 */
public class ExcelWorker {

    private String datetime;
    private String organization;
    private String address;
    private String comment;
    private float summary;
    private ArrayList<Point> priceArray = new ArrayList<>();

    private int rowNum = 0; // счетчик для строк

    private HSSFWorkbook workbook = new HSSFWorkbook(); // создание самого excel файла в памяти

    private HSSFSheet sheet; // создание листа с названием
    private Row row; // строка
    private Cell cell; //ячейка

    private HSSFFont font = workbook.createFont(); // создание шрифта
    private HSSFCellStyle styleNormal = workbook.createCellStyle(); // жирный стиль
    private HSSFCellStyle styleBold = workbook.createCellStyle(); // жирный стиль


    public void setDateTime(String datetime) {
        this.datetime = datetime;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPriceArray(ArrayList<Point> priceArray) {
        this.priceArray.clear();
        this.priceArray.addAll(priceArray);
    }

    public void setSummary(float summary) {
        this.summary = summary;
    }


    private void setStyleBold() {
        // жирный стиль
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleBold.setFont(font);
        styleBold.setBorderRight((short) 1);
        styleBold.setBorderTop((short) 1);
        styleBold.setBorderBottom((short) 1);
        styleBold.setBorderLeft((short) 1);
    }

    private void setStyleNormal() {
        // обычный стиль
        styleNormal.setBorderRight((short) 1);
        styleNormal.setBorderTop((short) 1);
        styleNormal.setBorderBottom((short) 1);
        styleNormal.setBorderLeft((short) 1);
    }

    private void setColumnsWidth() {
        // размеры колонок
        sheet.setColumnWidth(0, 12500);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 1500);
        sheet.setColumnWidth(3, 2500);
    }

    private void setOrganizationRow() {
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Организация: " + organization);
    }

    private void setAddressRow() {
        row = sheet.createRow(++rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Адрес: " + address);
    }


    private void priceFill() {
        for (Point point : priceArray)
            rowFill(styleNormal, point.name,
                    String.valueOf(new Rounding().round_up(point.priceUnit)),
                    String.valueOf(point.amount),
                    String.valueOf(new Rounding().round_up(point.priceTotal))
            );
    }

    private void setCommentRow() {
        row = sheet.createRow(++rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Комментарий: " + comment);
    }

    private void write() throws IOException {
        // проверка на наличие папки
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mobile Seller";
        File dir = new File(filename);
        if (!dir.exists()) dir.mkdir();

        // запись
        filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mobile Seller/" + datetime + ".xls";
        File file = new File(filename);
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
    }

    private void rowFill(CellStyle style, String... args) {
        row = sheet.createRow(++rowNum);
        int count = 0;
        for (String s : args) {
            cell = row.createCell(count++);
            try {
                cell.setCellValue(Float.parseFloat(s));
            } catch (IllegalArgumentException e) {
                cell.setCellValue(s);
            }
            cell.setCellStyle(style);
        }
    }

    public void writeIntoExcel() throws IOException {
        sheet = workbook.createSheet(datetime); // создание листа с названием

        setColumnsWidth();

        setStyleBold();
        setStyleNormal();

        setOrganizationRow();
        setAddressRow();

        // строка оглавления
        rowFill(styleBold, "Наименование", "Цена", "шт/кг", "Сумма");

        // заполнение листа данными
        priceFill();

        // пустая строка
        rowFill(styleNormal, "", "", "", "");

        // строка итога
        rowFill(styleBold, "ИТОГ:", "", "", String.valueOf(summary));

        // строка комментария
        setCommentRow();

        // запись созданного в памяти Excel документа в файл
        write();
    }

/*
    // метод чтения списка товара из файла и запись в БД
    public static void readFromExcel(Context context, String file) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = 0;
        HSSFRow row;

        DataBaseHelper dbHelper = new DataBaseHelper(context);
        dbHelper.deletePrice();

        do {
            ++rowNum;
            row = sheet.getRow(rowNum);
            String name = row.getCell(0).getStringCellValue();
            float wholescapePrice = (float) row.getCell(1).getNumericCellValue();
            float retailPrice = (float) row.getCell(2).getNumericCellValue();

            dbHelper.setPrice(name, wholescapePrice, retailPrice);

        } while (rowNum != sheet.getLastRowNum());

        workbook.close();
    }
*/
}
