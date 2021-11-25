/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;


/**
 * Classe DAO qui implemente le personDAO et qui définit les méthodes
 * @author Kévin
 */
public class PersonDAOimpl implements PersonDAO{

    //String contenant la commande au serveur sql
    private static final String QUERY_CUSTOMER = "SELECT * FROM javaprojectdb.customer WHERE email=? and password=?";
    private static final String QUERY_EMPLOYEE = "SELECT * FROM javaprojectdb.employee WHERE email=? and password=?";
    
    /**
     * Methode DAO qui recoit en paramatre un email et un mot de passe
     * et permet de se connecter a une interface customer ou client
     * @param email
     * @param password
     * @return 
     */
    @Override
    public int customerOrAdminLogin(String email, String password) {
        PreparedStatement pst_customer;
        PreparedStatement pst_employee;
        ResultSet rs_customer;
        ResultSet rs_employee;

         try
            {
            
            //If the person is a customer
            pst_customer = connect.prepareStatement(QUERY_CUSTOMER);
            pst_customer.setString(1, email);
            pst_customer.setString(2, password);
            rs_customer = pst_customer.executeQuery();
            
            //If the person is an admin
            pst_employee = connect.prepareStatement(QUERY_EMPLOYEE);
            pst_employee.setString(1, email);
            pst_employee.setString(2, password);
            rs_employee = pst_employee.executeQuery();
            
            if(rs_customer.next())
            {  
                return 1;   
            }
            else if(rs_employee.next())
            {
                return 2;
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Email and password did not matched :\n\n"
                    + "-Please try again if you have an account\n\n-If not go create a new one by clicking on register");
            }       
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, "Error connect person");
        }
        return 3;
    }
    
    
    
}
