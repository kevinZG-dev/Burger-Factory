/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;

/**
 * 
 * @author Kévin
 */
public interface PersonDAO {
    
    public Connection connect = ConnectionMySQL.getInstance();
    public abstract int customerOrAdminLogin(String email, String password);
}
