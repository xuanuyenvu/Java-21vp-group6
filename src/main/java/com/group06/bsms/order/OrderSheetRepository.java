package com.group06.bsms.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import com.group06.bsms.Repository;
import com.group06.bsms.accounts.AccountRepository;
import com.group06.bsms.members.MemberRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.SortOrder;

public class OrderSheetRepository extends Repository<OrderSheet> implements OrderSheetDAO {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    public OrderSheetRepository(Connection db,AccountRepository accountRepository,
            MemberRepository memberRepository) {
        super(db, OrderSheet.class);
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;

    }

    @Override
    public void insertOrderSheet(OrderSheet orderSheet) throws Exception {
        try {
            db.setAutoCommit(false);
            if (orderSheet == null) {
                throw new NullPointerException("The parameer cannot be null");
            }
            if (orderSheet.orderedBooks.isEmpty()) {
                throw new Exception("The imported books is empty");
            }

            try (PreparedStatement insertOrderSheetQuery = db.prepareStatement(
                    "INSERT INTO OrderSheet (memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                insertOrderSheetQuery.setInt(1, orderSheet.member.id);
                insertOrderSheetQuery.setInt(2, orderSheet.employeeInChargeId);
                insertOrderSheetQuery.setDate(3, orderSheet.orderDate);
                insertOrderSheetQuery.setDouble(4, orderSheet.discountedTotalCost);

                int rowsAffected = insertOrderSheetQuery.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insertion failed, no rows affected.");
                }

                ResultSet generatedKeys = insertOrderSheetQuery.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderSheet.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }

                insertOrderedBooksList(orderSheet.id, orderSheet.orderedBooks);

            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }

    }

    private void insertOrderedBooksList(int orderSheetId, List<OrderedBook> OrderedBooks) throws Exception {
        try {
            db.setAutoCommit(false);
            if (OrderedBooks == null || OrderedBooks.isEmpty()) {
                throw new NullPointerException("The parameter cannot be null or empty");
            }
            int orderedBookResults[] = null;

            String insertQuery = "INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (?, ?, ?, ?)";
            try (var orderedBookStatement = db.prepareStatement(insertQuery)) {
                for (OrderedBook orderedBook : OrderedBooks) {

                    orderedBookStatement.setInt(1, orderSheetId);
                    orderedBookStatement.setInt(2, orderedBook.bookId);
                    orderedBookStatement.setInt(3, orderedBook.quantity);
                    orderedBookStatement.setDouble(4, orderedBook.pricePerBook);
                    orderedBookStatement.addBatch();
                }
                orderedBookResults = orderedBookStatement.executeBatch();

                db.commit();

                for (int orderedBookResult : orderedBookResults) {
                    if (orderedBookResult == PreparedStatement.EXECUTE_FAILED) {
                        throw new Exception("Cannot insert ordered books");
                    }
                }

            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }

    }

    @Override
    public OrderSheet selectOrderSheet(int id) throws Exception {
        try {
            OrderSheet orderSheet = selectById(id);
            if (orderSheet == null) {
                throw new Exception("Entity not found");
            }
            db.setAutoCommit(false);
            try (var selectOrderedBooksQuery = db.prepareStatement(
                    "SELECT ib.bookId, b.title, ib.quantity, ib.salePrice FROM OrderedBook ib JOIN Book b ON ib.bookId = b.id WHERE ib.orderSheetId = ?")) {
                selectOrderedBooksQuery.setInt(1, id);
                var result = selectOrderedBooksQuery.executeQuery();

                if (orderSheet.orderedBooks == null) {
                    orderSheet.orderedBooks = new ArrayList<>();
                }

                while (result.next()) {
                    orderSheet.orderedBooks.add(new OrderedBook(id,
                            result.getInt("bookId"),
                            result.getString("title"),
                            result.getInt("quantity"),
                            result.getDouble("salePrice")));
                }

                db.commit();
            }
            orderSheet.employee = accountRepository.selectAccount(orderSheet.employeeInChargeId);
            orderSheet.member = memberRepository.selectById(orderSheet.memberId);

            return orderSheet;

        } catch (Exception e) {
            db.rollback();
            throw e;

        }
    }

    @Override
    public List<OrderSheet> selectSearchSortFilterOrderSheets(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice) throws Exception {
        List<OrderSheet> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT OrderSheet.id, OrderSheet.employeeInChargeId, OrderSheet.memberId, OrderSheet.discountedTotalCost, OrderSheet.orderDate, Account.phone, Member.phone FROM OrderSheet JOIN Account ON Account.id = OrderSheet.employeeInChargeId JOIN Meber ON Member.id = OrderSheet.memberId";

            stringQuery += " WHERE " + searchChoice
                    + ((searchChoice.trim().equals("Account.phone") || searchChoice.trim().equals("Member.phone"))
                            ? " LIKE ?"
                            : " = ? ");

            if (!sortValue.isEmpty()) {
                stringQuery += " ORDER BY ";
                for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                    Integer key = entry.getKey();
                    SortOrder value = entry.getValue();

                    String sortKey;
                    switch (key) {
                        case 0 ->
                            sortKey = "Account.phone";
                        case 1 ->
                            sortKey = "Member.phone";
                        case 2 ->
                            sortKey = "OrderSheet.orderDate";
                        case 3 ->
                            sortKey = "OrderSheet.discountedTotalCost";
                        default ->
                            throw new IllegalArgumentException("Invalid sort key: " + key);
                    }

                    stringQuery += sortKey + (value == SortOrder.ASCENDING ? " ASC" : " DESC") + ", ";
                }

                stringQuery = stringQuery.substring(0, stringQuery.length() - 2);
            }

            stringQuery += " OFFSET ? LIMIT ? ";

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                int parameterIndex = 1;

                if (searchChoice.trim().equals("OrderSheet.orderDate")) {
                    if (searchString == null || searchString.isEmpty()) {
                        return result;
                    } else {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date searchDate = dateFormat.parse(searchString);
                            preparedStatement.setDate(parameterIndex++, new java.sql.Date(searchDate.getTime()));
                        } catch (ParseException e) {
                            return result;
                        }

                    }
                } else if (searchChoice.trim().equals("OrderSheet.discountedTotalCost")) {
                    if (searchString == null || searchString.isEmpty()) {
                        return result;
                    } else {
                        try {
                            double searchValue = Double.parseDouble(searchString);
                            preparedStatement.setDouble(parameterIndex++, searchValue);
                        } catch (NumberFormatException e) {
                            return result;
                        }
                    }
                } else {
                    preparedStatement.setString(parameterIndex++, "%" + searchString + "%");
                }

                preparedStatement.setInt(parameterIndex++, offset);

                preparedStatement.setInt(parameterIndex++, limit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        OrderSheet OrderSheet = new OrderSheet(resultSet.getInt("id"),
                                resultSet.getInt("employeeInChargeId"),
                                resultSet.getInt("MemberId"),
                                resultSet.getDate("orderDate"),
                                resultSet.getDouble("discountedTotalCost"), null);
                        OrderSheet.employee = accountRepository.selectAccount(OrderSheet.employeeInChargeId);
                        OrderSheet.member = memberRepository.selectById(OrderSheet.memberId);

                        result.add(OrderSheet);
                    }
                }
            }

            db.commit();

            return result;
        } catch (Exception e) {

            db.rollback();
            throw e;
        }
    }

    @Override
    public List<OrderSheet> selectOrderSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            java.sql.Date startDate, java.sql.Date endDate) throws Exception {

        List<OrderSheet> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = """
                    SELECT * FROM
                    (SELECT OrderSheet.id, OrderSheet.employeeInChargeId, OrderSheet.memberId, OrderSheet.discountedTotalCost, OrderSheet.orderDate, Account.phone AS employeePhone, Member.phone AS memberPhone
                         FROM OrderSheet
                         JOIN Account ON Account.id = OrderSheet.employeeInChargeId
                         JOIN Memvber ON Member.id = OrderSheet.memberId
                         WHERE OrderSheet.orderDate BETWEEN ? AND ?
                         ORDER BY OrderSheet.discountedTotalCost DESC
                        )
                    AS order_sheets
                    """;

            if (!sortAttributeAndOrder.isEmpty()) {
                stringQuery += " ORDER BY ";
                for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                    Integer key = entry.getKey();
                    SortOrder value = entry.getValue();

                    String sortKey;
                    switch (key) {
                        case 0 ->
                            sortKey = "order_sheets.employeePhone";
                        case 1 ->
                            sortKey = "order_sheets.memberPhone";
                        case 2 ->
                            sortKey = "order_sheets.orderDate";
                        case 3 ->
                            sortKey = "order_sheets.discountedTotalCost";
                        default ->
                            throw new IllegalArgumentException("Invalid sort key: " + key);
                    }

                    stringQuery += sortKey + (value == SortOrder.ASCENDING ? " ASC" : " DESC") + ", ";
                }

                stringQuery = stringQuery.substring(0, stringQuery.length() - 2);
            }

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {

                preparedStatement.setDate(1, startDate);
                preparedStatement.setDate(2, endDate);
                System.out.println(preparedStatement.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        OrderSheet orderSheet = new OrderSheet(resultSet.getInt("id"),
                                resultSet.getInt("employeeInChargeId"),
                                resultSet.getInt("memberId"),
                                resultSet.getDate("orderDate"),
                                resultSet.getDouble("discountedTotalCost"), null);
                        orderSheet.employee = accountRepository.selectAccount(orderSheet.employeeInChargeId);
                        orderSheet.employee = accountRepository.selectById(orderSheet.memberId);

                        result.add(orderSheet);
                    }
                }
                db.commit();

            }
            return result;
        } catch (Exception e) {
            db.rollback();
            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Order sheet not found");
            }
            throw e;
        }

    }

}
