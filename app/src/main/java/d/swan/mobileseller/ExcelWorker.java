package d.swan.mobileseller;

import android.text.TextUtils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.BaseNumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IllegalFormatException;

/**
 * Created by daniel on 6/12/16.
 */
public final class ExcelWorker {

    private static String datetime = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss").format(System.currentTimeMillis());

    private static int rowNum = 0; // счетчик для строк

    private static Cell cell; //ячейка
    private static Row row; // строка

    private static HSSFWorkbook workbook = new HSSFWorkbook(); // создание самого excel файла в памяти
    private static HSSFSheet sheet = workbook.createSheet(datetime); // создание листа с названием

    private static HSSFFont font = workbook.createFont(); // создание шрифта
    private static HSSFCellStyle styleNormal = workbook.createCellStyle(); // обычный стиль
    private static HSSFCellStyle styleBold = workbook.createCellStyle(); // жирный стиль

    public static String writeIntoExcel(String organization, String address, ArrayList<Point> priceArray, float summary) throws IOException {

        // жирный стиль
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleBold.setFont(font);
        styleBold.setBorderRight((short) 1);
        styleBold.setBorderTop((short) 1);
        styleBold.setBorderBottom((short) 1);
        styleBold.setBorderLeft((short) 1);

        // обычный стиль
        styleNormal.setBorderRight((short) 1);
        styleNormal.setBorderTop((short) 1);
        styleNormal.setBorderBottom((short) 1);
        styleNormal.setBorderLeft((short) 1);

        // размеры колонок
        sheet.setColumnWidth(0, 12500);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 1500);
        sheet.setColumnWidth(3, 2500);


        // строки организации и адреса
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Организация: " + organization);
        row = sheet.createRow(++rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Адрес: " + address);


        // строка оглавления
        rowFill(styleBold, "Наименование", "Цена", "шт", "Сумма");

        // заполнение листа данными
        for (Point point : priceArray)
            rowFill(styleNormal, point.name,
                    String.valueOf(new Rounding().round_up(point.priceUnit)),
                    String.valueOf(point.amount),
                    String.valueOf(new Rounding().round_up(point.priceTotal))
            );

        // пустая строка
        rowFill(styleNormal, "", "", "", "");
        // строка итога
        rowFill(styleBold, "ИТОГ:", "", "", String.valueOf(summary));

        // запись созданного в памяти Excel документа в файл

        // проверка на наличие папки
        String filename = "/sdcard/Mobile Seller";
        File dir = new File(filename);
        if (!dir.exists()) dir.mkdir();


        filename = "/sdcard/Mobile Seller/" + datetime + ".xls";
        File file = new File(filename);
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);

        // возвращает полный путь сохраненного документа
        return filename;
    }

    private static void rowFill(CellStyle style, String... args) {
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
