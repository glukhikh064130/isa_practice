package entities;

import java.util.Objects;

public final class Product {
    private final int id;
    private final String good;
    private final double price;
    private final String categoryName;

    public Product(int id, String good, double price, String categoryName) {
        this.id = id;
        this.good = good;
        this.price = price;
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Good: %s | Price: %s | Category: %s",
                this.id, this.good, this.price, this.categoryName);
    }

    public int getId() {
        return id;
    }

    public String getGood() {
        return good;
    }

    public double getPrice() {
        return price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Product) obj;
        return this.id == that.id &&
                Objects.equals(this.good, that.good) &&
                Double.doubleToLongBits(this.price) == Double.doubleToLongBits(that.price) &&
                Objects.equals(this.categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, good, price, categoryName);
    }

}
