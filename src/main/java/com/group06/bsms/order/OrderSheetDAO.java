package com.group06.bsms.order;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface OrderSheetDAO {

        void insertOrderSheet(OrderSheet orderSheet) throws Exception;

        OrderSheet selectOrderSheet(int id) throws Exception;

        public List<OrderSheet> selectSearchSortFilterImportSheets(
                        int offset, int limit, Map<Integer, SortOrder> sortValue,
                        String searchString, String searchChoice) throws Exception;

        List<OrderSheet> selectTop10ImportSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
                        Date startDate, Date endDate) throws Exception;
}
