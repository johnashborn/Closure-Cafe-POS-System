/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

/**
 *
 * @author arant
 */
public class TopProduct {
    private String name;
    private String category;
    private int totalSold;
    private String revenue;

    public TopProduct(String name, String category, int totalSold, String revenue) {
        this.name = name;
        this.category = category;
        this.totalSold = totalSold;
        this.revenue = revenue;
    }

    public String getName()     { return name; }
    public String getCategory() { return category; }
    public int getTotalSold()   { return totalSold; }
    public String getRevenue()  { return revenue; }
}
