package com.example.book.service;

import com.example.book.model.Review;
import com.example.book.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Vélemény nem található ID: " + id));
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    // Recenzió frissítése
    public Review updateReview(Long id, Review reviewDetails) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vélemény nem található ID: " + id));

        existingReview.setRating(reviewDetails.getRating());
        existingReview.setComment(reviewDetails.getComment());
        return reviewRepository.save(existingReview);
    }
}

