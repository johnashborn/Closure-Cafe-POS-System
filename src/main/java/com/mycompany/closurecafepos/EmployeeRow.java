/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

/**
 *
 * @author arant
 */
public class EmployeeRow {
    private int userId;
    private String username;
    private String role;
    private String status;
    private String timeIn;
    private String timeOut;

    public EmployeeRow(int userId, String username, String role, String status, String timeIn, String timeOut) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.status = status;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public int getUserId()      { return userId; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public String getStatus()   { return status; }
    public String getTimeIn()   { return timeIn; }
    public String getTimeOut()  { return timeOut; }
}
