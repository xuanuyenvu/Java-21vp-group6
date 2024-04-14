package com.group06.bsms.accounts;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class AccountService {

    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    List<Account> getTop10EmployeesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Account> employees = accountDAO.selectTop10EmployeesWithHighestRevenue(sortAttributeAndOrder, startDate, endDate);
        return employees;
    }
}
