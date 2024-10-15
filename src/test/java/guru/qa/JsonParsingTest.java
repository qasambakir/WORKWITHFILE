package guru.qa;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.utils.UtilsBook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class  JsonParsingTest {

    private ClassLoader cl = JsonParsingTest.class.getClassLoader();

    @Test
    @DisplayName("Разбор JSON файла с объектом Book")
    void parserJsonFileTest() throws Exception {
        // Загрузка JSON файла
        InputStream jsonStream = cl.getResourceAsStream("book.json");
        Assertions.assertNotNull(jsonStream, "JSON файл не найден, проверьте путь");

        // Создание объекта ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Чтение и разбор JSON файла в объект Book
        UtilsBook utilsBook = objectMapper.readValue(jsonStream, UtilsBook.class);

        // Проверка данных из JSON
        Assertions.assertEquals("Effective Java", utilsBook.getTitle(), "Название книги не совпадает");
        Assertions.assertEquals("978-0134685991", utilsBook.getIsbn(), "ISBN не совпадает");
        Assertions.assertEquals(2018, utilsBook.getPublishedYear());
        Assertions.assertEquals("Joshua Bloch", utilsBook.getAuthors().get(0), "Автор не совпадает");
    }
}
