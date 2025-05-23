package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.dto.ChangeBookDto;
import io.github.enkarin.bookcrossing.books.enums.Status;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.books.repository.GenreRepository;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.GenreNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(final UserRepository usr, final BookRepository bkr, final ModelMapper mdm, final GenreRepository genreRepository) {
        userRepository = usr;
        bookRepository = bkr;
        modelMapper = mdm;
        modelMapper.createTypeMap(BookDto.class, Book.class).addMappings(ms -> ms.skip(Book::setOwner));
        this.genreRepository = genreRepository;
    }

    @Transactional
    public BookModelDto saveBook(final BookDto bookDTO, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        final Book book = modelMapper.map(bookDTO, Book.class);
        book.setOwner(user);
        book.setGenre(genreRepository.findById(bookDTO.getGenre()).orElseThrow(GenreNotFoundException::new));
        book.setStatus(bookDTO.getStatusId() == 0 ? Status.EXCHANGES : Status.getById(bookDTO.getStatusId()));
        return BookModelDto.fromBook(bookRepository.save(book));
    }

    public List<BookModelDto> findBookForOwner(final String login, final int pageNumber, final int pageSize) {
        return userRepository.findByLogin(login).orElseThrow().getBooks().stream()
            .skip((long) pageNumber * pageSize)
            .limit(pageSize)
            .map(BookModelDto::fromBook)
            .toList();
    }

    public List<BookModelDto> findBookByOwnerId(final String userId, final Pageable pageable) {
        return bookRepository.findBooksByOwnerUserId(Integer.parseInt(userId), pageable).stream()
            .map(BookModelDto::fromBook)
            .toList();
    }

    public BookModelDto findById(final int bookId) {
        final Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        return BookModelDto.fromBook(book);
    }

    public List<BookModelDto> filter(final BookFiltersRequest request) {
        List<Book> books = StringUtils.isEmpty(request.getAuthorOrTitle()) ?
            bookRepository.findAll() :
            bookRepository.findBooksByTitleOrAuthorIgnoreCase(request.getAuthorOrTitle());
        if (!CollectionUtils.isEmpty(request.getGenre())) {
            books = books.stream()
                .filter(book -> request.getGenre().contains(book.getGenre().getId()))
                .toList();
        }
        if (request.getAuthor() != null) {
            books = books.stream()
                .filter(book -> book.getAuthor() != null)
                .filter(book -> book.getAuthor().equalsIgnoreCase(request.getAuthor()))
                .toList();
        }
        if (request.getPublishingHouse() != null) {
            books = books.stream()
                .filter(book -> book.getPublishingHouse() != null)
                .filter(book -> book.getPublishingHouse().equalsIgnoreCase(request.getPublishingHouse()))
                .toList();
        }
        if (request.getYear() != 0) {
            books = books.stream()
                .filter(book -> book.getYear() == request.getYear())
                .toList();
        }
        if (request.getTitle() != null) {
            books = books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(request.getTitle()))
                .toList();
        }
        if (request.getCity() != null) {
            books = books.stream()
                .filter(book -> book.getOwner().getCity() != null)
                .filter(book -> book.getOwner().getCity().equalsIgnoreCase(request.getCity()))
                .toList();
        }
        return filterBookFromNonLockedOwner(books)
            .skip((long) request.getPageSize() * request.getPageNumber())
            .limit(request.getPageSize())
            .map(BookModelDto::fromBook)
            .toList();
    }

    public List<BookModelDto> findAll(final int pageNumber, final int pageSize) {
        return filterBookFromNonLockedOwner(bookRepository.findAll())
            .skip((long) pageNumber * pageSize)
            .limit(pageSize)
            .map(BookModelDto::fromBook)
            .toList();
    }

    @Transactional
    public void deleteBook(final String login, final int bookId) {
        bookRepository.findBooksByOwnerLoginAndBookId(login, bookId).ifPresentOrElse(bookRepository::delete, () -> {
            throw new BookNotFoundException();
        });
    }

    public List<BookModelDto> findByTitleOrAuthor(final String field, final int pageNumber, final int pageSize) {
        return filterBookFromNonLockedOwner(bookRepository.findBooksByTitleOrAuthorIgnoreCase(field))
            .skip((long) pageNumber * pageSize)
            .limit(pageSize)
            .map(BookModelDto::fromBook)
            .toList();
    }

    public UserPublicProfileDto findBookOwner(final int bookId, final int zoneId) {
        return UserPublicProfileDto.fromUser(bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new).getOwner(), zoneId);
    }

    @Transactional
    public BookModelDto changeBook(final String login, final ChangeBookDto bookDto) {
        final Book book = bookRepository.findBooksByOwnerLoginAndBookId(login, bookDto.getBookId()).orElseThrow(BookNotFoundException::new);
        if (nonNull(bookDto.getTitle()) && !bookDto.getTitle().isBlank()) {
            book.setTitle(bookDto.getTitle());
        }
        if (nonNull(bookDto.getAuthor()) && !bookDto.getAuthor().isBlank()) {
            book.setAuthor(bookDto.getAuthor());
        }
        if (nonNull(bookDto.getGenre())) {
            book.setGenre(genreRepository.findById(bookDto.getGenre())
                .orElseThrow(GenreNotFoundException::new));
        }
        if (nonNull(bookDto.getPublishingHouse())) {
            book.setPublishingHouse(bookDto.getPublishingHouse());
        }
        if (nonNull(bookDto.getYear())) {
            book.setYear(bookDto.getYear());
        }
        if (nonNull(bookDto.getStatusId())) {
            book.setStatus(Status.getById(bookDto.getStatusId()));
        }
        return BookModelDto.fromBook(book);
    }

    private Stream<Book> filterBookFromNonLockedOwner(final List<Book> books) {
        return books.stream().filter(b -> b.getOwner().isAccountNonLocked());
    }
}
