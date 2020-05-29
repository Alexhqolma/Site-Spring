package com.SiteSpring.controllers;

import com.SiteSpring.models.Review;
import com.SiteSpring.models.Role;
import com.SiteSpring.models.User;
import com.SiteSpring.repository.ReviewRepository;
import com.SiteSpring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("name", "Hello World");

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {

        model.addAttribute("title", "Страница про нас");

        return "about";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {

        Iterable<Review> reviews = reviewRepository.findAll();
        model.addAttribute("title", "Страница с отзывами");
        model.addAttribute("reviews", reviews);

        return "reviews";
    }

    @PostMapping("reviews-add")
    public String reviewsAdd(@AuthenticationPrincipal User user, @RequestParam String title, @RequestParam String text, Model model) {

        Review review = new Review(title, text, user);

        reviewRepository.save(review);

        return "redirect:reviews";

    }

    @GetMapping("/reviews/{id}")
    public String reviewInfo(@PathVariable(value = "id") long reviewId, Model model) {

        Optional<Review> review = reviewRepository.findById(reviewId);

        ArrayList<Review> result = new ArrayList<>();

        review.ifPresent(result::add);

        model.addAttribute("review", result);

        return "review-info";
    }

    @GetMapping("/reviews/{id}/update")
    public String reviewUpdate(@PathVariable(value = "id") long reviewId, Model model) {

        Optional<Review> review = reviewRepository.findById(reviewId);

        ArrayList<Review> result = new ArrayList<>();

        review.ifPresent(result::add);

        model.addAttribute("review", result);

        return "review-update";
    }

    @PostMapping("/reviews/{id}/update")
    public String reviewsUpdateForm(@PathVariable(value = "id") long reviewId, @RequestParam String title, @RequestParam String text, Model model) throws ClassNotFoundException {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ClassNotFoundException());

        review.setTitle(title);
        review.setText(text);

        reviewRepository.save(review);

        return "redirect:/reviews/" + reviewId;

    }

    @PostMapping("/reviews/{id}/delete")
    public String reviewsDelete(@PathVariable(value = "id") long reviewId, Model model) throws ClassNotFoundException {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ClassNotFoundException());

        reviewRepository.delete(review);

        return "redirect:/reviews";

    }

    @GetMapping("/reg")
    public String reg() {

        return "reg";
    }

    @PostMapping("/reg")
    public String addUser(User user, Model model) throws ClassNotFoundException {

        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));

        userRepository.save(user);

        return "redirect:/login";

    }

    @GetMapping("/user")
    public String userPage(Principal principal, Model model) {

        String passwordError = "";

        User user = userRepository.findByUsername(principal.getName());

        model.addAttribute("useremail", user.getEmail());
        model.addAttribute("passwordError", passwordError);

        return "user";
    }

    @PostMapping("/userupdate")
    public String userUpdate(@RequestParam String email, @RequestParam String password, @RequestParam String role, Model model, Principal principal) throws ClassNotFoundException {

        User user = userRepository.findByUsername(principal.getName());

        String passwordError = "";

        if(password.equals("") || password.length() < 3) {
            passwordError = "Вы ввели слишко короткий пароль";
        } else {
            user.setPassword(password);
        }

        switch (role) {
            case "USER":
                user.setRoles(new HashSet<Role>() {{
                    add(Role.USER);
                }});
                break;
            case "ADMIN":
                user.setRoles(new HashSet<Role>() {{
                    add(Role.ADMIN);
                }});
                break;
            case "OWNER":
                user.setRoles(new HashSet<Role>() {{
                    add(Role.OWNER);
                }});
                break;
            case "VIP":
                user.setRoles(new HashSet<Role>() {{
                    add(Role.VIP);
                }});
                break;
        }


        user.setEmail(email);

        userRepository.save(user);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("useremail", user.getEmail());
        model.addAttribute("userpassword", user.getPassword());
        model.addAttribute("passwordError", passwordError);

        if (passwordError.length() >0) {
            return "user";
        } else {
            return "redirect:/user";
        }

    }


}