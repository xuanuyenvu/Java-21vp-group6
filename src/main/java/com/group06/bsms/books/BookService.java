package com.group06.bsms.books;

import com.group06.bsms.authors.Author;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.categories.Category;
import com.group06.bsms.categories.CategoryService;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherService;
import java.sql.Date;

public class BookService {

    private final BookDAO bookDAO;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;

    public BookService(BookDAO bookDAO, AuthorService authorService, PublisherService publisherService,
            CategoryService categoryService) {
        this.bookDAO = bookDAO;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.categoryService = categoryService;
    }

    public void updateBook(Book book) throws Exception, IllegalArgumentException {
        try {
            if (book == null) {
                throw new IllegalArgumentException("Book object cannot be null");
            }
            if (Double.valueOf(book.maxImportPrice) == null && Double.valueOf(book.salePrice) != null) {
                throw new Exception("Cannot update sale price because the maximum import price is null");
            }
            if (book.salePrice >= 1.1 * book.maxImportPrice) {
                throw new Exception("Sale price must be bigger than 1.1 * maximum import price");
            }

            bookDAO.updateBook(book);
        } catch (Exception e) {
            throw e;
        }

    }

    public void hideBook(int id) throws Exception {
        bookDAO.hideBook(id);
    }

    public void showBook(int id) throws Exception {
        bookDAO.showBook(id);
    }

    public List<Book> searchSortFilterBook(int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice,
            Author author, Publisher publisher, Double minPrice, Double maxPrice,
            ArrayList<Category> categoriesList
    ) throws Exception {

        List<Integer> listBookCategoryId = new ArrayList<>();
        for (Category category : categoriesList) {
            listBookCategoryId.add(category.id);
        }

        var authorId = author == null ? -1 : author.id;
        var publisherId = publisher == null ? -1 : publisher.id;

        List<Book> books = bookDAO.selectSearchSortFilterBooks(offset, limit, sortValue, searchString, searchChoice,
                authorId, publisherId, minPrice, maxPrice, listBookCategoryId);

        return books;
    }

    public void updateBookAttributeById(int bookId, String attr, Object value) throws Exception {
        bookDAO.updateBookAttributeById(bookId, attr, value);
    }

    public void insertBook(String title, Author author, Publisher publisher, ArrayList<Category> categoriesList,
            Date publishDate, String dimension, Object pages, String translator,
            String overview, boolean hideChecked) throws Exception {
        Book book = new Book();
        book.title = title;
        book.authorId = author.id;
        book.publisherId = publisher.id;
        book.publishDate = publishDate;
        book.categories = new ArrayList<>(categoriesList);
        book.dimension = dimension;
        book.pageCount = (Integer) pages;
        book.translatorName = translator;
        book.overview = overview;
        book.isHidden = hideChecked;

        int count = 0;
        Author a = authorService.selectAuthor(book.authorId);
        Publisher p = publisherService.selectPublisher(book.publisherId);

        if (a != null && a.isHidden) {
            count++;
        }
        if (p != null && p.isHidden) {
            count++;
        }
        for (Category c : book.categories) {
            if (c.isHidden) {
                count++;
            }
        }
        book.hiddenParentCount = count;

        if (book == null) {
            throw new IllegalArgumentException("Book object cannot be null");
        }

        if (book.title == null || book.authorId == -1
                || book.publisherId == -1
                || book.publishDate == null
                || book.categories.isEmpty()
                || book.dimension == null
                || book.pageCount == 0
                || book.overview == null) {

            throw new IllegalArgumentException("Please fill in all required information.");
        }

        bookDAO.insertBook(book);
    }

    List<Book> getNewBooks() throws Exception {
        List<Book> books = bookDAO.getNewBooks();
        return books;
    }

    List<Book> getHotBooks() throws Exception {
        List<Book> books = bookDAO.getHotBooks();
        return books;
    }

    List<Book> getOutOfStockBooks() throws Exception {
        List<Book> books = bookDAO.getOutOfStockBooks();
        return books;
    }
}
