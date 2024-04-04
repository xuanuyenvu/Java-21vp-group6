package com.group06.bsms.books;

import com.group06.bsms.authors.Author;
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

    public void updateBook(Book book, Book updatedBook) throws Exception, IllegalArgumentException {
        try {
            if (updatedBook.title == null
                    || updatedBook.publishDate == null
                    || updatedBook.categories.isEmpty()
                    || updatedBook.dimension == null
                    || updatedBook.pageCount == 0
                    || updatedBook.overview == null) {
                throw new IllegalArgumentException("Please fill in all required information.");
            }
            if (updatedBook.authorId == 0)
                updatedBook.authorId = authorService.insertAuthorIfNotExists(updatedBook.author.name);

            if (updatedBook.publisherId == 0)
                updatedBook.publisherId = publisherService.insertPublisherIfNotExists(updatedBook.publisher.name);

            if (book.maxImportPrice != null &&updatedBook.salePrice <= 1.1 * book.maxImportPrice) {
                throw new Exception("Sale price must be bigger than 1.1 * import price");
            }

            bookDAO.updateBook(book, updatedBook);
        } catch (Exception e) {
            
            throw e;
        }

    }

    public Book getBook(int id) throws Exception {
        try {
            Book book = bookDAO.selectBook(id);
            if (book == null)
                throw new Exception("Cannot find book: " + id);
            return book;
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
            ArrayList<Category> categoriesList) throws Exception {

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
        if (title == null
                || publishDate == null
                || categoriesList.isEmpty()
                || dimension == null
                || (Integer)pages == 0
                || overview == null) {
           
            throw new IllegalArgumentException("Please fill in all required information.");
        }
        book.title = title;
        book.publishDate = publishDate;
        book.categories = new ArrayList<>(categoriesList);
        book.dimension = dimension;
        book.pageCount = (Integer) pages;
        book.translatorName = translator;
        book.overview = overview;
        book.isHidden = hideChecked;
        book.author = author;
        book.publisher = publisher;
        int count = 0;
        if (author.id == 0) {
            book.authorId = authorService.insertAuthorIfNotExists(author.name);
            book.author.id =book.authorId;
        } else {
            book.authorId = author.id;
        }
        if (publisher.id == 0) {
            book.publisherId = publisherService.insertPublisherIfNotExists(publisher.name);
            book.publisher.id = book.publisherId;
        } else {
            book.publisherId = publisher.id;
        }

        if (book.author.isHidden) {
            count++;
        }
        if (book.publisher.isHidden) {
            count++;
        }
        for (Category c : book.categories) {
            if (c.isHidden) {
                count++;
            }
        }
        book.hiddenParentCount = count;
        bookDAO.insertBook(book);
    }

}
