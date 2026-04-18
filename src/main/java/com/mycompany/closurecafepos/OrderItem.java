/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

/**
 *
 * @author arant
 */
public class OrderItem {
    private Products product;
    private int quantity;

    public OrderItem(Products product) {
        this.product = product;
        this.quantity = 1;
    }

    public Products getProduct() { return product; }
    public int getQuantity()     { return quantity; }

    public void increment() { quantity++; }
    public void decrement() { if (quantity > 1) quantity--; }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}
