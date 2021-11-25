/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;
import project.model.entity.Customer;

/**
 * Interface DAO qui permet de faire la connection avec la 
 * table customer de la base de donnée
 * @author Kévin
 */
public interface CustomerDAO {
  
    public Connection connect = ConnectionMySQL.getInstance();
    
    public abstract Customer find(String email);
    public abstract void add(Customer entity);
    public abstract void updatePassword(Customer entity);

}

