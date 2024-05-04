package com.group06.bsms.accounts;

import com.group06.bsms.auth.AuthService;

import java.sql.Date;
import java.util.ArrayList;
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

    public List<Account> selectAllAccounts() {
        try {
            List<Account> accounts = accountDAO.selectAllAccounts();
            return accounts;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Account selectAccount(int id) throws Exception {
        try {
            return accountDAO.selectAccount(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Account selectAccountByName(String accountName) throws Exception {
        try {
            return accountDAO.selectAccountByName(accountName);
        } catch (Exception e) {
            throw e;
        }
    }

    public Account getAccount(int id) throws Exception {
        try {
            Account account = accountDAO.selectAccount(id);
            if (account == null) {
                throw new Exception("Cannot find account with id = " + id);
            }
            return account;
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateAccountAttributeById(int accountId, String attr, Object value) throws Exception {
        accountDAO.updateAccountAttributeById(accountId, attr, value);
    }

    public void updateAccount(Account account, AccountWithPassword updatedAccount) throws Exception {
        try {
            if (updatedAccount.phone == null || updatedAccount.phone.equals("")) {
                throw new Exception("Phone cannot be empty");
            }
            if ("".equals(updatedAccount.name)) {
                updatedAccount.name = null;
            }
            if ("".equals(updatedAccount.gender)) {
                updatedAccount.gender = "Other";
            }
            if ("".equals(updatedAccount.email)) {
                updatedAccount.email = null;
            }
            if ("".equals(updatedAccount.address)) {
                updatedAccount.address = null;
            }

            accountDAO.updateAccount(account, updatedAccount);
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertAccount(AccountWithPassword account) throws Exception {
        if (account.phone == null || account.phone.equals("")) {
            throw new Exception("Phone cannot be empty");
        }
        if (account.password == null || account.password.equals("")) {
            throw new Exception("Password cannot be empty");
        }
        if (account.password.length() <= 4) {
            throw new Exception("Insufficient password length");
        }
        if ("".equals(account.name)) {
            account.name = null;
        }
        if ("".equals(account.gender)) {
            account.gender = "Other";
        }
        if ("".equals(account.email)) {
            account.email = null;
        }
        if ("".equals(account.address)) {
            account.address = null;
        }

        accountDAO.insertAccount(account);
    }

    public void lockAccount(int id) throws Exception {
        accountDAO.lockAccount(id);
    }

    public void unlockAccount(int id) throws Exception {
        accountDAO.unlockAccount(id);
    }

    public List<Account> searchSortFilterAccounts(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice
    ) throws Exception {

        List<Account> accounts = accountDAO.selectSearchSortFilterAccounts(
                offset, limit, sortValue, searchString, searchChoice
        );

        return accounts;
    }

    public void updatePasswordById(int id, String currentPassword, String newPassword, String confirmPassword) throws Exception {
        try {
            if (newPassword == null || newPassword.equals("")
                    || confirmPassword == null || confirmPassword.equals("")) {
                throw new Exception("Password and confirm password cannot be empty");
            }

            if (!newPassword.equals(confirmPassword)) {
                throw new Exception("Passwords do not match");
            }

            if (!accountDAO.checkPasswordById(id, currentPassword)) {
                throw new Exception("Incorrect current password");
            }

            if (newPassword.length() <= 4) {
                throw new Exception("Insufficient password length");
            }

            accountDAO.updatePasswordById(id, newPassword);
        } catch (Exception e) {
            throw e;
        }
    }

}
