package com.group06.bsms.books;

import com.group06.bsms.authors.Author;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.categories.Category;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherService;
import java.sql.Date;

public class BookService {

    private final BookDAO bookDAO;
    private final AuthorService authorService;
    private final PublisherService publisherService;

    public BookService(BookDAO bookDAO, AuthorService authorService, PublisherService publisherService) {
        this.bookDAO = bookDAO;
        this.authorService = authorService;
        this.publisherService = publisherService;
    }

    public void updateBook(Book book, Book updatedBook) throws Exception {
        try {
            if (updatedBook.title == null || updatedBook.title.equals("")) {
                throw new Exception("Title cannot be empty");
            }

            if (updatedBook.author == null) {
                throw new Exception("Author cannot be empty");
            }

            if (updatedBook.publisher == null) {
                throw new Exception("Publisher cannot be empty");
            }

            if (updatedBook.publishDate == null) {
                throw new Exception("Publish date cannot be empty");
            }

            if (updatedBook.categories.isEmpty()) {
                throw new Exception("Categories cannot be empty");
            }

            if (updatedBook.dimension == null || updatedBook.dimension.equals("")) {
                throw new Exception("Dimension cannot be empty");
            }

            if (updatedBook.pageCount == 0) {
                throw new Exception("Pages cannot be 0");
            }

            if (updatedBook.overview == null || updatedBook.overview.equals("")) {
                throw new Exception("Overview cannot be empty");
            }

            if (book.maxImportPrice != null && updatedBook.salePrice <= 1.1 * book.maxImportPrice) {
                throw new Exception("Sale price must be greater than 1.1 * import price");
            }

            int count = 0;
            if (updatedBook.author.id == 0) {
                updatedBook.authorId = authorService.insertAuthorIfNotExists(updatedBook.author.name);
                updatedBook.author.id = updatedBook.authorId;
            } else {
                updatedBook.authorId = updatedBook.author.id;
            }
            if (updatedBook.publisher.id == 0) {
                updatedBook.publisherId = publisherService.insertPublisherIfNotExists(updatedBook.publisher.name);
                updatedBook.publisher.id = updatedBook.publisherId;
            } else {
                updatedBook.publisherId = updatedBook.publisher.id;
            }

            if (updatedBook.author.isHidden) {
                count++;
            }
            if (updatedBook.publisher.isHidden) {
                count++;
            }
            for (Category c : updatedBook.categories) {
                if (c.isHidden) {
                    count++;
                }
            }
            updatedBook.hiddenParentCount = count;
            bookDAO.updateBook(book, updatedBook);
        } catch (Exception e) {
            throw e;
        }
    }

    public Book getBook(int id) throws Exception {
        try {
            Book book = bookDAO.selectBook(id);
            if (book == null) {
                throw new Exception("Cannot find book with id = " + id);
            }
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

    public void insertBook(
            String title, Author author, Publisher publisher, ArrayList<Category> categoriesList,
            Date publishDate, String dimension, Object pages, String translator,
            String overview, boolean hideChecked) throws Exception {
        if (title == null || title.equals("")) {
            throw new Exception("Title cannot be empty");
        }

        if (author == null) {
            throw new Exception("Author cannot be empty");
        }

        if (publisher == null) {
            throw new Exception("Publisher cannot be empty");
        }

        if (publishDate == null) {
            throw new Exception("Publish date cannot be empty");
        }

        if (categoriesList.isEmpty()) {
            throw new Exception("Categories cannot be empty");
        }

        if (dimension == null || dimension.equals("")) {
            throw new Exception("Dimension cannot be empty");
        }

        if (pages == null || pages.equals(0)) {
            throw new Exception("Pages cannot be 0");
        }

        if (overview == null || overview.equals("")) {
            throw new Exception("Overview cannot be empty");
        }

        Book book = new Book();
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
            book.author.id = book.authorId;
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

    public List<Book> searchBooksByTitle(String title) throws Exception {
        try {
            return bookDAO.getBooksByTitle(title);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Book> searchAvailableBooksByTitle(String title) throws Exception {
        try {

            return bookDAO.getAvailableBooksByTitle(title);


        } catch (Exception e) {
            throw e;
        }
    }

    List<Book> getTop10BooksWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Book> books = bookDAO.selectTop10BooksWithHighestRevenue(sortAttributeAndOrder, startDate, endDate);
        return books;
    }
}
