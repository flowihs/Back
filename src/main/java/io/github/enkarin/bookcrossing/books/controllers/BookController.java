package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.service.BookService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Tag(
    name = "Раздел со всеми книгами в системе",
    description = "Позволяет пользователю получить все книги, доступные для обмена"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
@Validated
public class BookController {

    private final BookService bookService;

    @Operation(
        summary = "Все книги в системе",
        description = "Позволяет получить книги всех пользователей"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает все книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = BookModelDto.class)))})
    })
    @GetMapping("/all")
    public ResponseEntity<List<BookModelDto>> books(@RequestParam final int pageNumber, @RequestParam final int pageSize) {
        return ResponseEntity.ok(bookService.findAll(pageNumber, pageSize));
    }

    @Operation(
        summary = "Все книги пользователя",
        description = "Позволяет получить книги пользователя по его id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает все книги пользователя",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = BookModelDto.class)))})
    })
    @GetMapping("/by-user")
    public ResponseEntity<List<BookModelDto>> booksByUser(@RequestParam(name = "id") @NotBlank(message = "3013") final String userId,
                                                          @RequestParam final int pageNumber,
                                                          @RequestParam final int pageSize) {
        return ResponseEntity.ok(bookService.findBookByOwnerId(userId, PageRequest.of(pageNumber, pageSize)));
    }

    @Operation(
        summary = "Страница книги",
        description = "Позволяет получить данные выбранной книги"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Книга не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает данные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                schema = @Schema(implementation = BookModelDto.class))})
    })
    @GetMapping("/info")
    public ResponseEntity<BookModelDto> bookInfo(@RequestParam final int bookId) {
        final BookModelDto book = bookService.findById(bookId);
        return ResponseEntity.ok(book);
    }

    @Operation(
        summary = "Поиск книг по названию или автору",
        description = "Позволяет найти книги по названию или автору"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = BookModelDto.class)))}),
        @ApiResponse(responseCode = "400", description = "Поле name не должно быть пустым",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/ValidationErrorBody"))})
    })
    @GetMapping("/searchByTitle")
    public ResponseEntity<List<BookModelDto>> searchByTitleOrAuthor(
        @RequestParam @NotBlank(message = "3008") @Parameter(description = "Полное называние книги или имя автора") final String name,
        @RequestParam final int pageNumber,
        @RequestParam final int pageSize) {
        return ResponseEntity.ok(bookService.findByTitleOrAuthor(name, pageNumber, pageSize));
    }

    @Operation(
        summary = "Поиск книг с фильтрацией",
        description = "Позволяет найти книги с помощью фильтров"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = BookModelDto.class)))})
    })
    @PostMapping("/searchWithFilters")
    public ResponseEntity<List<BookModelDto>> searchWithFilters(@RequestBody final BookFiltersRequest filters) {
        return ResponseEntity.ok(bookService.filter(filters));
    }

    @Operation(summary = "Получение владельца книги", description = "Позволяет получить информацию о владельце книги по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = UserPublicProfileDto.class))}),
        @ApiResponse(responseCode = "404", description = "Книга с указанным bookId не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))})
    })
    @GetMapping("/owner")
    public ResponseEntity<UserPublicProfileDto> searchBookOwner(@RequestParam final int bookId, @RequestParam final int zoneId) {
        return ResponseEntity.ok(bookService.findBookOwner(bookId, zoneId));
    }
}
