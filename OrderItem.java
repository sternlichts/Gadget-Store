package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderItem {
    private String        name;
    private String        price;
    private String        variant;
    private String        imagePath;
    private int           quantity;
    private LocalDate     orderDate;
    private LocalDateTime orderedAt;

    public OrderItem(String name, String price, String variant, String imagePath, int quantity) {
        this.name      = name;
        this.price     = price;
        this.variant   = variant;
        this.imagePath = imagePath;
        this.quantity  = quantity;
        this.orderDate = LocalDate.now();
    }

    public String        getName()      { return name; }
    public String        getPrice()     { return price; }
    public String        getVariant()   { return variant; }
    public String        getImagePath() { return imagePath; }
    public int           getQuantity()  { return quantity; }
    public LocalDate     getOrderDate() { return orderDate; }
    public LocalDateTime getOrderedAt() { return orderedAt; }

    public void setQuantity(int quantity)          { this.quantity  = quantity; }
    public void setOrderDate(LocalDate date)       { this.orderDate = date; }
    public void setOrderedAt(LocalDateTime ts)     { this.orderedAt = ts; }
}
