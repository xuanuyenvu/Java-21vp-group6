package com.group06.bsms.ordersheet;

import java.sql.Date;
import java.util.List;

import com.group06.bsms.accounts.Account;
import com.group06.bsms.members.Member;

public class OrderSheet {

    public int id;
    public int employeeInChargeId;
    public int memberId;
    public Account employee;
    public Member member;
    public Date orderDate;
    public Double discountedTotalCost;
    public List<OrderedBook> orderedBooks;

    public OrderSheet() {

    }

    public OrderSheet(int id, int employeeInChargeId, int memberId, Date orderDate, Double discountedTotalCost,
            List<OrderedBook> orderedBooks) {
        this.id = id;
        this.memberId = memberId;
        this.employeeInChargeId = employeeInChargeId;
        this.orderDate = orderDate;
        this.discountedTotalCost = discountedTotalCost;
        this.orderedBooks = orderedBooks;
        
    }

    public OrderSheet(int employeeInChargeId, int memberId, Date orderDate, Double discountedTotalCost,
            List<OrderedBook> orderedBooks) {
        this.employeeInChargeId = employeeInChargeId;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.discountedTotalCost = discountedTotalCost;
        this.orderedBooks = orderedBooks;
      
    }

    @Override
    public String toString() {
        return "OrderSheet {id=" + id + ", employeeInChargeId=" + employeeInChargeId + ", memberId=" + memberId
                + ", employee=" + employee + ", member=" + member + ", orderDate=" + orderDate
                + ", discountedTotalCost=" + discountedTotalCost + ", orderedBooks=" + orderedBooks + "}";
    }

    

}
