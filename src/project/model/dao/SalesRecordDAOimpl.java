/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import project.model.entity.SalesRecord;

/**
 * Classe DAO qui implemente le salesrecordDAO et qui définit les méthodes
 * @author Kévin
 */
public class SalesRecordDAOimpl implements SalesRecordDAO {
    //String contenant la commande au serveur sql ajouter une nouvelle commande
    private String INSERT_NEW_SALESRECORD = "INSERT INTO javaprojectdb.salesrecord" + "  (idSalesRecord,  total, email, fname,lname, adress, zipcode, city, telnb, receipt, date, heure) VALUES " + " (?, ?,?, ?, ?, ?, ?,?,?,?,?,?)";
    //2 decimal maxi
    private DecimalFormat df = new DecimalFormat("0.00");;
 
    /**
     * This will add a sales given as parameter to the mySql database.
     * @param commande 
     */
    @Override
    public void add(SalesRecord commande) {

        //Query that add an element into the SalesRecord table
        try (PreparedStatement pst_salesRecord = connect.prepareStatement(INSERT_NEW_SALESRECORD)){

            //Putting all the element of the object SalesRecord into the database :
            pst_salesRecord.setInt(1, commande.getIdSalesRecord());
            pst_salesRecord.setDouble(2, commande.getTotal());
            pst_salesRecord.setString(3, commande.getEmail());
            pst_salesRecord.setString(4, commande.getFname());
            pst_salesRecord.setString(5, commande.getLname());
            pst_salesRecord.setString(6, commande.getAdress());
            pst_salesRecord.setInt(7, commande.getZipcode());
            pst_salesRecord.setString(8, commande.getCity());
            pst_salesRecord.setInt(9, commande.getTelnb());
            pst_salesRecord.setString(10, commande.getReceipt());
            pst_salesRecord.setString(11, commande.getDate());
            pst_salesRecord.setString(12, commande.getHeure());

            //Execute the query:
            pst_salesRecord.executeUpdate();


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur dans SalesrecordDao pour ajouter un ");
        }
    }
    
    
    /**
     * Getting a new id for a sales record, it will runs throught all the sales record ids to find the 
     * bigger one and add +1 to it to get an unique id.
     * @return 
     */
    @Override
    public int newIdSales () {
        int id_salesRecord = 0; //The last idCart in the database  will be stored in this var
        try {
            //GET THE LAST ID OF THE TABLE CART
            
            Statement st_Id = connect.createStatement(); //Statement to handle the idCart
            ResultSet rs_Id;
            //Query that allow us to get id from the cart's table
            rs_Id = st_Id.executeQuery("SELECT * FROM  javaprojectdb.salesrecord");

            //Loop to have the last idCart enters the database
            //While there is a cart in the table
            while (rs_Id.next()) {
                //if the id_Cart is inferior than the next idCart in the database 
                //(Normally it will always be inferior)
                if (id_salesRecord < rs_Id.getInt("idSalesRecord")) {
                    id_salesRecord = rs_Id.getInt("idSalesRecord"); //We store the idCart in 'Id_Cart'
                }
            }
            //We are incrementing by +1 to always get an unique idCart
            id_salesRecord += 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur pour trouver un idSalesRecord");
        }
        return id_salesRecord;
    } 
    
    
    /**
     * The purpose of this method is to collect in an arrayList of string all the sales record 
     * that are in the database.
     * @return mySalesRecord that is a list of all sales record in the database
     */
    @Override
    public ArrayList<String> getListSalesRecord() {
        //Variables declaration
        ArrayList<String> mySalesRecord = new ArrayList<String>();
        int i = 0;
        String temp;

        try {
            Statement st_Sales = connect.createStatement();
            ResultSet rs_Sales;
            String query_getsalesRecord = "SELECT * FROM javaprojectdb.salesrecord";
            rs_Sales = st_Sales.executeQuery(query_getsalesRecord);

            //Loop that runs through all the sales record in the database
            while (rs_Sales.next()) {
                //Create the String with all the informations of a sales record stored in the database 
                temp = rs_Sales.getString(1) + "  " + df.format(Double.parseDouble(rs_Sales.getString(2))) + "€ - " +rs_Sales.getString("date")+" à "+rs_Sales.getString("heure")+" - "
                        + rs_Sales.getString(4) + " " + rs_Sales.getString(5) + "\n     "+ rs_Sales.getString(3) + " - " + rs_Sales.getString(6)
                        + ", " + rs_Sales.getString(7) + ", " + rs_Sales.getString(8) + "\n     +33 "
                        + rs_Sales.getString(9) + "\n\n";
                mySalesRecord.add(temp); //We add this sales record to the arraylist
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in SalesRecordDAOimpl / getListSalesRecord()");
        }

        return mySalesRecord;
    }

    /**
     * Methode DAO qui recoit un email en paramtre et qui va renvoyer
     * la liste des salesrecords associé a cette email
     * @param email
     * @return 
     */
    @Override
    public ArrayList<SalesRecord> selectByEmail(String email) {
      
        ArrayList<SalesRecord> salesRecords = new ArrayList<SalesRecord>();
       
        try(PreparedStatement pst_sales = connect.prepareStatement("SELECT * FROM javaprojectdb.salesrecord WHERE email=?")){
            pst_sales.setString(1, email);
            ResultSet rs_sales = pst_sales.executeQuery();
            
            while (rs_sales.next()) {
		int idSalesRecord = rs_sales.getInt("idSalesRecord");
                double total = rs_sales.getDouble("total");
                String fname = rs_sales.getString("fname");
                String lname = rs_sales.getString("lname");
                String adress = rs_sales.getString("adress");
                int zipcode = rs_sales.getInt("zipcode");
                String city = rs_sales.getString("city");
                int telnb = rs_sales.getInt("telnb");
                String receipt = rs_sales.getString("receipt");
                String date = rs_sales.getString("date");
                String heure = rs_sales.getString("heure");
 
		salesRecords.add(new SalesRecord(idSalesRecord, total, email, fname, lname, adress, zipcode,city, telnb, receipt, date, heure));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error in find SalesRecords");
        }
        return salesRecords;              
    }
    
    /**
     * Getting the receipt  (long text) stored in the database of sales record id
     * send as parameter.
     * @param id
     * @return 
     */
    @Override
    public String getReceipt(int id) {

        try {
            Statement st_Sales = connect.createStatement();
            ResultSet rs_Sales;
            //Query that allow to select the receipt of the sales record id we want
            String query_getsalesRecord = "SELECT idSalesRecord, receipt FROM javaprojectdb.salesrecord";
            rs_Sales = st_Sales.executeQuery(query_getsalesRecord);

            //Loop that runs through all the sales record in the database
            while (rs_Sales.next()) {
                //If the id that we are running throught is equal to the id send as parameter
                if (Integer.parseInt(rs_Sales.getString("idSalesRecord")) == id) {
                    return rs_Sales.getString("receipt"); //We return the receipt
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in SalesRecordDAOimpl/getReceipt()");
        }

        //if the id send as parameter does not exist in the database:
        return "No receipts available as \nthis id does not exist.";
    }
    
    /**
     * 
     * @return total
     */
    @Override
    public int countTotalOrder() {
        int total = 0;
        
        try(PreparedStatement pst_total = connect.prepareStatement("SELECT COUNT(*) AS idSalesRecord FROM javaprojectdb.salesrecord")) {
            ResultSet rst_total = pst_total.executeQuery();
            
            while (rst_total.next()) {
                total = rst_total.getInt("idSalesRecord");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error to count total order");
        }
        return total;
    }
    /**
     * Methode DAO qui permet de selectionné les prix totaux 
     * et de les mettre dans une liste de double
     * 
     * @return listTotal
     */
    @Override
    public ArrayList<Double> selectListTotal (){
        ArrayList<Double> listTotal = new ArrayList<Double>();
        
        try(PreparedStatement pst_list_total = connect.prepareStatement("SELECT total FROM javaprojectdb.salesrecord")) {
            ResultSet rst_list_total = pst_list_total.executeQuery();
            
            while (rst_list_total.next()) {
                listTotal.add(rst_list_total.getDouble("total"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error to select List total");
        }
        return listTotal;
    }

}
