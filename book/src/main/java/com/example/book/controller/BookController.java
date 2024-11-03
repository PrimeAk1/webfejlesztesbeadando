package com.example.book.controller;

import com.example.book.model.Book;
import com.example.book.model.Review;
import com.example.book.service.BookService;
import com.example.book.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public String showBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        try {
            bookService.createBook(book);
            return "redirect:/books";
        } catch (Exception e) {
            System.out.println("Hiba történt a könyv hozzáadása során: " + e.getMessage());
            return "add-book";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "edit-book";
        } else {
            return "redirect:/books";
        }
    }


    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book bookDetails) {
        bookService.updateBook(id, bookDetails);
        return "redirect:/books";
    }

    @GetMapping("/details/{id}")
    public String getBookDetails(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Könyv nem található"));
        List<Review> reviews = reviewService.getReviewsByBookId(id);
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        return "book-details";
    }


}

