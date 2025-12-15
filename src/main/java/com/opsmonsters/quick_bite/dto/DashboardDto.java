package com.opsmonsters.quick_bite.dto;

import java.util.List;


    public class DashboardDto {
        private List<AddressDto> addresses;
        private List<OrderDto> orders;
        private List<PromoCodeDto> promoCodes;
        private List<NotificationDto> notifications;
        private String logoutMessage;

        public DashboardDto(List<AddressDto> addresses, List<OrderDto> orders,
                            List<PromoCodeDto> promoCodes, List<NotificationDto> notifications,
                            String message) {
            this.addresses = addresses;
            this.orders = orders;
            this.promoCodes = promoCodes;
            this.notifications = notifications;
            this.logoutMessage = logoutMessage;
        }

    public List<AddressDto> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDto> addresses) { this.addresses = addresses; }

    public List<OrderDto> getOrders() { return orders; }
    public void setOrders(List<OrderDto> orders) { this.orders = orders; }

    public List<PromoCodeDto> getPromoCodes() { return promoCodes; }
    public void setPromoCodes(List<PromoCodeDto> promoCodes) { this.promoCodes = promoCodes; }

    public List<NotificationDto> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationDto> notifications) { this.notifications = notifications; }

    public String getLogoutMessage() { return logoutMessage; }
    public void setLogoutMessage(String logoutMessage) { this.logoutMessage = logoutMessage; }
}