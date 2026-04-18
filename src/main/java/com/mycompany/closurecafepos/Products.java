/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

/**
 *
 * @author arant
 */
public class Products {
    private int id;
    private String name;
    private String description;
    private String category;
    private double price;
    private byte[] picture;
    private boolean isAvailable;

    // Constructor
    public Products(int id, String name, String category, double price, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public int getId()            { return id; }
    public String getName()       { return name; }
    public String getDescription(){ return description; }
    public String getCategory()   { return category; }
    public double getPrice()      { return price; }
    public byte[] getPicture()    { return picture; }
    public boolean getIsAvailable(){ return isAvailable; }

    public void setId(int id)                  { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setDescription(String d)       { this.description = d; }
    public void setCategory(String category)   { this.category = category; }
    public void setPrice(double price)         { this.price = price; }
    public void setPicture(byte[] picture)     { this.picture = picture; }
    public void setAvailable(boolean a)        { this.isAvailable = a; }
}