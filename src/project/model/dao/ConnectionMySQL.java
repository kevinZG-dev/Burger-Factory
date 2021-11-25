/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Classe ConnectionMySQL ui permet d'établir la connexion
 * avec la base de donnée MySQL
 * @author Kévin
 */
public class ConnectionMySQL {
    //URL de connection
    private static String url = "jdbc:mysql://burger-factorydb.csmv1hcyzwv4.eu-west-3.rds.amazonaws.com:3306/javaprojectdb?useSSL=false"; 
    
    //Nom de l'utilisateur
    private static String user = "admin";
    
    //mot de pass
    private static String password = "burger_factorypassword";
    
    //objet connexion
    private static Connection connect;
    
    //Methode qui va retourner notre instance et la créer si elle n'existe pas
    public static Connection getInstance(){
        if(connect == null){
            try {
                connect = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }        
        return connect;    
    }    
}
