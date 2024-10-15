package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesParsingTest {

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();

    @Test
    @DisplayName("Проверка PDF файла из архива")
    void pdfFileParsingTest() throws Exception {
        InputStream zipStream = cl.getResourceAsStream("files_archive.zip");
        Assertions.assertNotNull(zipStream, "ZIP файл не найден, проверьте путь");

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("Обрабатывается файл: " + fileName);

                if (fileName.endsWith(".pdf")) {
                    // Чтение содержимого файла в массив байт
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }

                    // Создаем временный файл PDF для анализа
                    File tempPdf = File.createTempFile("temp", ".pdf");
                    try (FileOutputStream fos = new FileOutputStream(tempPdf)) {
                        fos.write(baos.toByteArray());
                    }

                    // Чтение PDF файла
                    PDF pdf = new PDF(new FileInputStream(tempPdf));
                    Assertions.assertTrue(pdf.text.contains("Пример pdf"),
                            "PDF файл не содержит ожидаемый текст");
                }
            }
        }
    }

    @Test
    @DisplayName("Проверка CSV файла из архива")
    void csvFileParsingTest() throws Exception {
        InputStream zipStream = cl.getResourceAsStream("files_archive.zip");
        Assertions.assertNotNull(zipStream, "ZIP файл не найден, проверьте путь");

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("Обрабатывается файл: " + fileName);
                if (fileName.endsWith(".csv")) {
                    // Чтение содержимого файла в массив байт
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    // Преобразуем байты в поток и обрабатываем CSV
                    try (CSVReader csvReader = new CSVReader(
                            new InputStreamReader(new ByteArrayInputStream(
                                    baos.toByteArray()), StandardCharsets.UTF_8))) {
                        List<String[]> data = csvReader.readAll();
                        Assertions.assertEquals(2, data.size(),
                                "CSV файл не содержит ожидаемое количество строк");
                        Assertions.assertEquals("88", data.get(1)[1],
                                "CSV файл не содержит ожидаемое значение в столбце ИИН");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Проверка XLS/XLSX файла из архива")
    void xlsFileParsingTest() throws Exception {
        InputStream zipStream = cl.getResourceAsStream("files_archive.zip");
        Assertions.assertNotNull(zipStream, "ZIP файл не найден, проверьте путь");

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("Обрабатывается файл: " + fileName);

                if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    // Чтение содержимого файла в массив байт
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }

                    // Создаем временный файл XLS для анализа
                    File tempXls = File.createTempFile("temp", ".xls");
                    try (FileOutputStream fos = new FileOutputStream(tempXls)) {
                        fos.write(baos.toByteArray());
                    }

                    // Чтение XLS файла
                    XLS xls = new XLS(new FileInputStream(tempXls));
                    Cell cell = xls.excel.getSheetAt(0).getRow(1).getCell(1);

                    // Проверка типа данных в ячейке
                    if (cell.getCellType() == CellType.STRING) {
                        String actualValue = cell.getStringCellValue();
                        Assertions.assertTrue(actualValue.contains("88"),
                                "XLS файл не содержит ожидаемое строковое значение");
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        double numericValue = cell.getNumericCellValue();
                        Assertions.assertEquals(88.0, numericValue,
                                "XLS файл не содержит ожидаемое числовое значение");
                    } else {
                        Assertions.fail("Неподдерживаемый тип данных в ячейке");
                    }
                }
            }
        }
    }
}
