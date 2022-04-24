package ru.bookcrossing.BookcrossingServer.books.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "t_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;

    private String author;

    private String genre;

    private String publishingHouse;

    private int year;

    @ManyToOne
    @JoinColumn(name = "ownerBook")
    @ToString.Exclude
    @JsonIgnore
    private User owner;

    @OneToOne
    @JoinColumn(name = "attachId")
    private Attachment attachment;
}
