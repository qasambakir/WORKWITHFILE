package guru.qa;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.utils.UtilsBook;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

class  JsonParsingTest {

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
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(utilsBook.getTitle()).isEqualTo("Effective Java");
        softAssertions.assertThat(utilsBook.getIsbn()).isEqualTo("978-0134685991");
        softAssertions.assertThat(utilsBook.getPublishedYear()).isEqualTo(2018);
        softAssertions.assertThat(utilsBook.getAuthors().get(0)).isEqualTo("Joshua Bloch");

        softAssertions.assertAll();
    }
}
