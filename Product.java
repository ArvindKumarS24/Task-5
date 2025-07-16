package com.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a product in the eCommerce system.
 * Created by Arvind Kumar S.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String category;
    private double price;
    private int quantity;
    private String description;

    // Default constructor
    public Product() {
        this.name = "";
        this.category = "";
        this.price = 0.0;
        this.quantity = 0;
        this.description = "";
    }

    // Full constructor
    public Product(int id, String name, String category, double price, int quantity, String description) {
        this.id = id;
        this.setName(name);
        this.setCategory(category);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setDescription(description);
    }

    // Constructor without ID (for creation use-case)
    public Product(String name, String category, double price, int quantity, String description) {
        this.setName(name);
        this.setCategory(category);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setDescription(description);
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty");
        this.name = name;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty())
            throw new IllegalArgumentException("Category cannot be empty");
        this.category = category;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = quantity;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    @Override
    public String toString() {
        return String.format("Product [ID=%d, Name='%s', Category='%s', Price=%.2f, Quantity=%d, Description='%s']",
                id, name, category, price, quantity, description);
    }
}
