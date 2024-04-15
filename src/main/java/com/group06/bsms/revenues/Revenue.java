package com.group06.bsms.revenues;

public class Revenue {

    public Double revenue;
    public int saleQuantity;

    public Revenue(Double revenue, int saleQuantity) {
        this.revenue = revenue;
        this.saleQuantity = saleQuantity;
    }

    @Override
    public String toString() {
        return "Revenue{" + "revenue=" + revenue + ", saleQuantity=" + saleQuantity + '}';
    }
}
