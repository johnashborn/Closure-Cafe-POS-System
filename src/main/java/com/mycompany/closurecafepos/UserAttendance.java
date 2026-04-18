/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

/**
 *
 * @author arant
 */


public class UserAttendance {
    private String name;
    private String role;
    private String timeIn;
    private String position;

    public UserAttendance(String name, String role, String timeIn) {
        this.name = name;
        this.role = role;
        this.timeIn = timeIn;
        // derive position from role for display under name
        this.position = role.equals("Admin") ? "Supervisor" : "Cashier";
    }

    public String getName()     { return name; }
    public String getRole()     { return role; }
    public String getTimeIn()   { return timeIn; }
    public String getPosition() { return position; }
}