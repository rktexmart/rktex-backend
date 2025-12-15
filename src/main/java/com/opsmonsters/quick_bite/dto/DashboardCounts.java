package com.opsmonsters.quick_bite.dto;

public class DashboardCounts {
    private long orderCount;
    private long cartCount;

    public DashboardCounts(long orderCount, long cartCount) {
        this.orderCount = orderCount;
        this.cartCount = cartCount;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public long getCartCount() {
        return cartCount;
    }

    public void setCartCount(long cartCount) {
        this.cartCount = cartCount;
    }
}
