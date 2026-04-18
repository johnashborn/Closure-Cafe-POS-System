/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author arant
 */
// ProductDAO.java
public class ProductDAO {

    public static List<Products> getAllProducts() {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT ProductID, Name, Category, Price, IsAvailable FROM products";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Products(
                    rs.getInt("ProductID"),
                    rs.getString("Name"),
                    rs.getString("Category"),
                    rs.getDouble("Price"),
                    rs.getBoolean("IsAvailable")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void deleteProduct(int productId) {
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM products WHERE ProductID = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
