package com.group06.bsms.accounts;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface AccountDAO {

    List<Account> selectTop10EmployeesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception;
}
