package d.swan.mobileseller;

import android.content.Context;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by daniel on 6/12/16.
 */
public class ExcelWorker {

    public static void writeIntoExcel(String organization, String address, ArrayList<Point> price, float summary) throws IOException {


        String datetime = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss").format(System.currentTimeMillis());

        // создание самого excel файла в памяти
        HSSFWorkbook workbook = new HSSFWorkbook();
        // создание листа с названием "Просто лист"
        HSSFSheet sheet = workbook.createSheet(datetime);

        // заполняем список какими-то данными


        // счетчик для строк
        int rowNum = 0;

        // создаем подписи к столбцам (это будет первая строчка в листе Excel файла)
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("№");
        row.createCell(1).setCellValue("Наименование");
        row.createCell(2).setCellValue("Цена за ед.");
        row.createCell(3).setCellValue("Кол-во");
        row.createCell(4).setCellValue("Сумма");

        // заполняем лист данными
        for (Point point : price) {
            createSheetHeader(sheet, ++rowNum, point);
        }
        row = sheet.createRow(++rowNum);
        row.createCell(0).setCellValue("ИТОГ:");
        row.createCell(4).setCellValue(summary);

        row = sheet.createRow(++rowNum);
        row.createCell(0).setCellValue("Организация");
        row.createCell(1).setCellValue(organization);

        row = sheet.createRow(++rowNum);
        row.createCell(0).setCellValue("Адрес");
        row.createCell(1).setCellValue(address);

        // записываем созданный в памяти Excel документ в файл
        File dir = new File("/sdcard/Mobile Seller");
        if (!dir.exists())
            dir.mkdir();

        FileOutputStream out = new FileOutputStream(new File("/sdcard/Mobile Seller/" + datetime + ".xls"));
        workbook.write(out);
    }

    private static void createSheetHeader(HSSFSheet sheet, int rowNum, Point point) {
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(rowNum);
        row.createCell(1).setCellValue(point.name);
        row.createCell(2).setCellValue(new Rounding().round_up(point.priceUnit));
        row.createCell(3).setCellValue(point.amount);
        row.createCell(4).setCellValue(new Rounding().round_up(point.priceTotal));
    }

    public static void readFromExcel(Context context, String file) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = 0;
        HSSFRow row;

        DataBaseHelper dbHelper = new DataBaseHelper(context);

        do {
            ++rowNum;
            row = sheet.getRow(rowNum);
            String name = row.getCell(0).getStringCellValue();
            float wholescapePrice = (float) row.getCell(1).getNumericCellValue();
            float retailPrice = (float) row.getCell(2).getNumericCellValue();

            dbHelper.setPrice(name, wholescapePrice, retailPrice);

        } while (rowNum != sheet.getLastRowNum());

        workbook.close();

        ///ArrayList<Point> price = new ArrayList<>();
        new DataBaseHelper(context).getPrice(true);

    }
}
