package com.example.book.controller;

import com.example.book.model.Book;
import com.example.book.model.Review;
import com.example.book.model.User;
import com.example.book.service.BookService;
import com.example.book.service.ReviewService;
import com.example.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/book/{bookId}")
    public String getReviewsByBookId(@PathVariable Long bookId, Model model) {
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Könyv nem található"));

        model.addAttribute("reviews", reviews);
        model.addAttribute("book", book);
        return "reviews";
    }

    @GetMapping("/book/{bookId}/add")
    public String showAddReviewForm(@PathVariable Long bookId, Model model) {
        model.addAttribute("review", new Review());
        model.addAttribute("bookId", bookId);
        return "add-review";
    }

    @PostMapping("/book/{bookId}/add")
    public String addReview(@PathVariable Long bookId, @ModelAttribute Review review, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Könyv nem található"));

        review.setBook(book);
        review.setUser(currentUser);
        reviewService.createReview(review);

        return "redirect:/reviews/book/" + bookId;
    }

    @GetMapping("/edit/{id}")
    public String showEditReviewForm(@PathVariable Long id, Model model, Authentication authentication) {
        Review review = reviewService.getReviewById(id);
        User currentUser = userService.findByUsername(authentication.getName());

        if (!canAccessReview(review, currentUser)) {
            throw new RuntimeException("Nincs jogosultsága módosítani ezt a véleményt");
        }

        model.addAttribute("review", review);
        return "edit-review";
    }

    @PostMapping("/edit/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute Review reviewDetails, Authentication authentication) {
        Review review = reviewService.getReviewById(id);
        User currentUser = userService.findByUsername(authentication.getName());

        if (!canAccessReview(review, currentUser)) {
            throw new RuntimeException("Nincs jogosultsága módosítani ezt a véleményt");
        }

        review.setRating(reviewDetails.getRating());
        review.setComment(reviewDetails.getComment());
        reviewService.updateReview(id, review);
        return "redirect:/reviews/book/" + review.getBook().getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Authentication authentication) {
        Review review = reviewService.getReviewById(id);
        User currentUser = userService.findByUsername(authentication.getName());

        if (!canAccessReview(review, currentUser)) {
            throw new RuntimeException("Nincs jogosultsága törölni ezt a véleményt");
        }

        Long bookId = review.getBook().getId();
        reviewService.deleteReview(id);
        return "redirect:/reviews/book/" + bookId;
    }

    private boolean canAccessReview(Review review, User currentUser) {
        return review.getUser().getId().equals(currentUser.getId()) || currentUser.getRoles().contains("ADMIN");
    }
}



