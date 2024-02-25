package com.example.vazifa.controller;

import com.example.vazifa.book.Book;
import com.example.vazifa.book.BookCreateDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    private final JdbcTemplate jdbcTemplate;

    public BookController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("")
    public String homePage(Model model) {
        String sql = "select * from book;";
        List<Book> books = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Book.class));
        model.addAttribute("books", books);
        return "home";
    }

    @GetMapping("/create")
    public String createBookPage(Model model) {
        model.addAttribute("bookCreateDTO", new BookCreateDTO());
        return "book_create";
    }

    @PostMapping("/create")
    public String createBook(@ModelAttribute BookCreateDTO dto) {
        String sql = "insert into book(title, description, price, author) values(?, ?, ?, ?);";
        jdbcTemplate.update(sql, dto.getTitle(), dto.getDescription(), dto.getPrice(), dto.getAuthor());
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBookPage(@PathVariable Long id, Model model) {
        String sql = "select * from book where id = ?;";
        Book book = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Book.class), id);
        model.addAttribute("book", book);
        return "book_edit";
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute Book book) {
        String sql = "update book set title = ?, description = ?, price = ?, author = ? where id = ?;";
        jdbcTemplate.update(sql, book.getTitle(), book.getDescription(), book.getPrice(), book.getAuthor(), book.getId());
        return "redirect:/books";
    }


    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        String sql = "delete from book where id = ?;";
        jdbcTemplate.update(sql, id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    @PostMapping("/search")
    public String searchBooks(@RequestParam String query, Model model) {
        String sql = "select * from book where title like ? or description like ? or author like ?;";
        List<Book> books = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Book.class), "%" + query + "%", "%" + query + "%", "%" + query + "%");
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        return "search_books";
    }
}