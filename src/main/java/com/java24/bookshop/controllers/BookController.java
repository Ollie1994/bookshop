package com.java24.bookshop.controllers;

import com.java24.bookshop.models.Book;
import com.java24.bookshop.repositories.AuthorRepository;
import com.java24.bookshop.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        // om author fylls i - kolla att den finns i databasen
        if(book.getAuthor() != null && !authorRepository.existsById(book.getAuthor().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author not found");
        }
        if(book.getCoAuthor() != null && !authorRepository.existsById(book.getCoAuthor().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CoAuthor not found");
        }
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);

    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    return ResponseEntity.ok(book);
    }


    //Testa göra put och delete själv


    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book bookDetails) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        // uppdatera egenskaper
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setPages(bookDetails.getPages());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setIsbn(bookDetails.getIsbn());
        existingBook.setPriceExVat(bookDetails.getPriceExVat());
        existingBook.setBookCoverUrl(bookDetails.getBookCoverUrl());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setCoAuthor(bookDetails.getCoAuthor());

        return ResponseEntity.ok(bookRepository.save(existingBook));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable String id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
