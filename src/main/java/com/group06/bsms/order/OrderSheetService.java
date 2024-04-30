package com.group06.bsms.order;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class OrderSheetService {

    private final OrderSheetDAO orderSheetDAO;

    public OrderSheetService(OrderSheetDAO orderSheetDAO) {
        this.orderSheetDAO = orderSheetDAO;
    }

    public void insertOrderSheet(OrderSheet orderSheet) throws Exception {
        try {
            orderSheetDAO.insertOrderSheet(orderSheet);
        } catch (Exception e) {
            throw e;
        }
    }

    public OrderSheet selectOrderSheet(int id) throws Exception {
        try {
            return orderSheetDAO.selectOrderSheet(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<OrderSheet> searchSortFilterOrderSheets(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice) throws Exception {

        List<OrderSheet> orderSheets = orderSheetDAO.selectSearchSortFilterOrderSheets(
                offset, limit, sortValue, searchString, searchChoice);

        return orderSheets;
    }

    List<OrderSheet> getOrderSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        return orderSheetDAO.selectOrderSheetsWithHighestRevenue(sortAttributeAndOrder, startDate, endDate);

    }

}
