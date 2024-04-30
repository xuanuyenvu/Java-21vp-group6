package com.group06.bsms.order;

import java.sql.Date;
import java.util.List;

import com.group06.bsms.accounts.Account;

public class OrderSheet {

    public int id;
    public int employeeInChargeId;
    public Account employee;
    public Date orderDate;
    public Double totalCost;
    public List<OrderedBook> orderedBooks;

    public OrderSheet() {

    }

    public OrderSheet(int id, int employeeInChargeId, Date orderDate, Double totalCost, List<OrderedBook> orderedBooks) {
        this.id = id;
        this.employeeInChargeId = employeeInChargeId;
        this.orderDate = orderDate;
        this.totalCost = totalCost;
        this.orderedBooks = orderedBooks;
    }

    public OrderSheet(int employeeInChargeId, Date orderDate, Double totalCost, List<OrderedBook> orderedBooks) {

        this.employeeInChargeId = employeeInChargeId;
        this.orderDate = orderDate;
        this.totalCost = totalCost;
        this.orderedBooks = orderedBooks;
    }

    @Override
    public String toString() {
        return "OrderSheet {id=" + id + ", employeeInChargeId=" + employeeInChargeId + ", orderDate=" + orderDate
                + ", totalCost=" + totalCost + ", orderedBooks=" + orderedBooks + "}";
    }

}
