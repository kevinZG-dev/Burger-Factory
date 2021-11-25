/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.awt.HeadlessException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import project.model.entity.Customer;

/**
 * Classe DAO qui implemente le customerDAO et qui définit les méthodes
 * @author Kévin
 */
public class CustomerDAOimpl implements CustomerDAO{
    
    //String contenant la commande au serveur sql pour trouver un customer selon son email
    private static final String SELECT_CUSTOMER_BY_EMAIL = "SELECT email, fname, lname, adress, zipcode, city, telnb, password,Cart_idCart FROM javaprojectdb.customer WHERE email =?";
    
    /**
     * Methode DAO qui reçoit en paramètre un email et qui 
     * renvoie le customer correspondant a cette email
     * @param email
     * @return customer
     */
    @Override
    public Customer find(String email) {
         Customer cust = null;
        
        //On effectue la requete parametré pour chercher un customer
        try(PreparedStatement pst_customer = connect.prepareStatement(SELECT_CUSTOMER_BY_EMAIL)){
            //selon son email
            pst_customer.setString(1, email);
            //On recupere les resultats de la requete
            ResultSet rs_item = pst_customer.executeQuery();
            //boucle
            while (rs_item.next()) {
                //On recupere tous les attributs pour créer l'objet customer
                String fname = rs_item.getString("fname");
                String lname = rs_item.getString("lname");
                String adress = rs_item.getString("adress");
                int zipcode = rs_item.getInt("zipcode");
                String city = rs_item.getString("city");
                int telnb = rs_item.getInt("telnb");
                String password = rs_item.getString("password");
                int Cart_idCart = rs_item.getInt("Cart_idCart");
                // Instancie l'objet customer
                cust = new Customer();
                //On lui set toutes les infos recupérés
                cust.setEmail(email);
                cust.setf_name(fname);
                cust.setl_name(lname);
                cust.setAdress(adress);
                cust.setZipcode(zipcode);
                cust.setCity(city);
                cust.setTel_nb(telnb);
                cust.setPassword(password);
                cust.setMyIdCart(Cart_idCart);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error in find customer");
        }
        return cust;
    }

    /**
     * Methode DAO qui recoit en parametre un objet customer 
     * et qui permet de l'ajouter a la base de donné
     * @param entity 
     */
    @Override
    public void add(Customer entity) {
  
        PreparedStatement pst_customer = null;
        ResultSet rs_customer = null;   
        try
        {
            //GET THE LAST ID OF THE TABLE CART
            int id_Cart = 0; //The last idCart in the database  will be stored in this var
            Statement st_Id = connect.createStatement(); //Statement to handle the idCart
            ResultSet rs_Id; 
            //Query that allow us to get id from the cart's table
            rs_Id = st_Id.executeQuery("SELECT * FROM  javaprojectdb.cart");
            
            //Loop to have the last idCart enters the database
            //While there is a cart in the table
            while (rs_Id.next())
            {
                //if the id_Cart is inferior than the next idCart in the database 
                //(Normally it will always be inferior)
                if (id_Cart < rs_Id.getInt("idCart"))
                {
                    id_Cart = rs_Id.getInt("idCart"); //We store the idCart in 'Id_Cart'
                }
            }
            //We are incrementing by +1 to always get an unique idCart
            id_Cart+=1;
            
            //For the customer
            String query_customer= "SELECT * FROM javaprojectdb.customer WHERE email=?";
            pst_customer = connect.prepareStatement(query_customer);
            pst_customer.setString(1, entity.getEmail());
            rs_customer = pst_customer.executeQuery();
        
            if(rs_customer.next())
            {
                JOptionPane.showMessageDialog(null, "This email already exist, please use a new one");
            }
            else
            {
                //CREATING NEW CART (when we create a new customer his cart have to be created)
                String insertCart="INSERT INTO javaprojectdb.cart VALUES (?)";
                PreparedStatement pst_Cart;
                
                pst_Cart = connect.prepareStatement(insertCart);
                //Storing 'id_Cart' in the idCart field of the table
                //'id_Cart' is obtained above and is unique
                pst_Cart.setString(1, Integer.toString(id_Cart));
                pst_Cart.execute();
                
                //CREATING NEW CUSTOMER 
                String insert="INSERT INTO javaprojectdb.customer VALUES (?,?,?,?,?,?,?,?,?)";
                
                pst_customer = connect.prepareStatement(insert);
                
                pst_customer.setString(1, entity.getEmail());
                pst_customer.setString(2, entity.getf_name());
                pst_customer.setString(3, entity.getl_name());
                pst_customer.setString(4, entity.getAdress());
                pst_customer.setString(5, Integer.toString(entity.getZipcode()));
                pst_customer.setString(6, entity.getCity());
                pst_customer.setString(7, Integer.toString(entity.getTel_nb()));
                pst_customer.setString(8, entity.getPassword());
                pst_customer.setString(9, Integer.toString(id_Cart));
                pst_customer.execute();
                
                JOptionPane.showMessageDialog(null, "Your account was created");
                
            }
        }
          catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null,"Erreur dans customer ");
        }
    }
 
    /**
     * Methode DAO qui permet de recevoir en parametre un objet customer 
     * et qui lui met a jour son mot de passe
     * @param entity 
     */
    @Override
    public void updatePassword(Customer entity) {
        
        try(PreparedStatement pst_up_pass = connect.prepareStatement("UPDATE javaprojectdb.customer SET password=? WHERE email =?")) {

            pst_up_pass.setString(1, entity.getPassword());
            pst_up_pass.setString(2, entity.getEmail());
            pst_up_pass.executeUpdate();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error to update password ");
        }
    }
    
}
