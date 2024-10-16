package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

class FilesParsingTest {

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();

    private byte[] extractFileFromZip(String fileName, String zipFilePath) throws Exception {
        InputStream zipStream = cl.getResourceAsStream(zipFilePath);
        Assertions.assertNotNull(zipStream, "ZIP файл не найден, проверьте путь");

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    return baos.toByteArray();  // Возвращаем содержимое файла в виде массива байт
                }
            }
        }
        throw new IllegalArgumentException("Файл " + fileName + " не найден в архиве");
    }

    // Метод для создания временного файла
    private File createTempFile(byte[] content, String extension) throws Exception {
        File tempFile = File.createTempFile("temp", extension);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content);
        }
        return tempFile;
    }

    @Test
    @DisplayName("Проверка PDF файла из архива")
    void pdfFileParsingTest() throws Exception {
        byte[] pdfContent = extractFileFromZip("files_archive/тестовый.pdf", "files_archive.zip");
        File tempPdf = createTempFile(pdfContent, ".pdf");

        // Чтение PDF файла
        PDF pdf = new PDF(new FileInputStream(tempPdf));

        // Используем SoftAssertions
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(pdf.text).contains("Пример pdf"); // Первая проверка
        softAssertions.assertThat(pdf.numberOfPages).isEqualTo(1); // Пример второй проверки
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Проверка CSV файла из архива")
    void csvFileParsingTest() throws Exception {
        byte[] csvContent = extractFileFromZip("files_archive/Samat Test csv.csv", "files_archive.zip");
        File tempCsv = createTempFile(csvContent, ".csv");
        InputStream zipStream = cl.getResourceAsStream("files_archive.zip");
        Assertions.assertNotNull(zipStream, "ZIP файл не найден, проверьте путь");
        // Чтение CSV файла
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(
                new FileInputStream(tempCsv), StandardCharsets.UTF_8))) {
        List<String[]> data = csvReader.readAll();

            // Используем SoftAssertions для проверок
            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(data).hasSize(2); // Проверка количества строк
            softAssertions.assertThat(data.get(1)[1]).isEqualTo("88"); // Проверка значения в ячейке
            softAssertions.assertAll();
            }
        }

    @Test
    @DisplayName("Проверка XLS/XLSX файла из архива")
    void xlsFileParsingTest() throws Exception {
        byte[] xlsxContent = extractFileFromZip("files_archive/Samat Test.xlsx", "files_archive.zip");
        File tempXlsx = createTempFile(xlsxContent, ".xlsx");
        InputStream zipStream = cl.getResourceAsStream("files_archive.zip");
        Assertions.assertNotNull(zipStream, "ZIP файл не найден, проверьте путь");
        // Чтение XLS/XLSX файла
        XLS xls = new XLS(new FileInputStream(tempXlsx));
        Cell cell = xls.excel.getSheetAt(0).getRow(1).getCell(1);
        // Используем SoftAssertions для проверок
        SoftAssertions softAssertions = new SoftAssertions();
        if (cell.getCellType() == CellType.STRING) {
            String actualValue = cell.getStringCellValue();
            softAssertions.assertThat(actualValue).contains("88"); // Проверка строкового значения
        } else if (cell.getCellType() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            softAssertions.assertThat(numericValue).isEqualTo(88.0); // Проверка числового значения
        }

        softAssertions.assertAll(); // Собираем все ошибки
    }
}

