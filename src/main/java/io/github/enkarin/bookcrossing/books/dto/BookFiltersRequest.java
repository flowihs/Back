package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Фильтры для книг")
@Getter
public class BookFiltersRequest {

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    private final String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    private final String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    private final String genre;

    @Schema(description = "Издательство", example = "АСТ")
    private final String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private final int year;

    private BookFiltersRequest(final String city, final String title, final String genre, final String author,
                              final String publishingHouse, final int year) {
        this.city = city;
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.publishingHouse = publishingHouse;
        this.year = year;
    }

    @JsonCreator
    public static BookFiltersRequest create(final String city, final String title, final String genre,
                                            final String author, final String publishingHouse, final int year) {
        return new BookFiltersRequest(city, title, genre, author, publishingHouse, year);
    }
}
