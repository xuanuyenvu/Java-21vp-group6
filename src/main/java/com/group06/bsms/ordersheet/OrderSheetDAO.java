package com.group06.bsms.ordersheet;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface OrderSheetDAO {

        void insertOrderSheet(OrderSheet orderSheet) throws Exception;

        OrderSheet selectOrderSheet(int id) throws Exception;

        public List<OrderSheet> selectSearchSortFilterOrderSheets(
                        int offset, int limit, Map<Integer, SortOrder> sortValue,
                        String searchString, String searchChoice) throws Exception;

        List<OrderSheet> selectOrderSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
                        Date startDate, Date endDate) throws Exception;
}
