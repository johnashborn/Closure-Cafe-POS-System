/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.closurecafepos;

/**
 *
 * @author arant
 */
public class SessionManager {
    private static int loggedInUserId;
    private static String username;
    
    public static void setLoggedInUserName(String name){
        username = name;
    }
    
    public static String getLoggedInUserName(){
        return username;
    }

    public static void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }
 
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }
    
}
