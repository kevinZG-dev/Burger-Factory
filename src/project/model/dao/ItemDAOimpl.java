/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.awt.HeadlessException;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static java.lang.Integer.parseInt;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import project.model.entity.Item;

/**
 * Classe DAO qui implemente l'itemDAO et qui définit les méthodes
 * @author Kévin
 */
public class ItemDAOimpl implements ItemDAO {

    //String contenant la commande au serveur sql
    private static final String INSERT_ITEMS_SQL = "INSERT INTO javaprojectdb.item" + "  (idItem,  name, price, type,description, bulkprice, bulkquantity, image) VALUES " + " (?, ?,?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ITEMS_BY_TYPE = "SELECT idItem, name, price, type ,description ,bulkprice, bulkquantity, image FROM javaprojectdb.item WHERE type =?";

    /**
     * This method find an item by it's id and will create and Item object by
     * getting all the attribute of this item in the database.
     *
     * @param id
     * @return
     */
    @Override
    public Item find(int id, boolean withImage) {
        Item it = null;
        String selectItemById;
        if (withImage) {
            selectItemById = "SELECT idItem, name, price, type, description, bulkprice, bulkquantity, image FROM javaprojectdb.item WHERE idItem =?";
        } else {
            selectItemById = "SELECT idItem, name, price, type, description, bulkprice, bulkquantity FROM javaprojectdb.item WHERE idItem =?";
        }

        try (PreparedStatement pst_item = connect.prepareStatement(selectItemById)) {
            pst_item.setInt(1, id);
            ResultSet rs_item = pst_item.executeQuery();
            ImageIcon myImage = null;
            while (rs_item.next()) {
                //Getting all the attribute of the item in the database
                String name = rs_item.getString("name");
                double price = rs_item.getDouble("price");
                String type = rs_item.getString("type");
                String description = rs_item.getString("description");
                double bulkprice = rs_item.getDouble("bulkprice");
                int bulkquantity = rs_item.getInt("bulkquantity");

                if (withImage) {
                    byte[] img = rs_item.getBytes("image");
                    if (img != null) {
                        myImage = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
                    } else {
                        myImage = null;
                    }
                }
                //Creating an item object:
                it = new Item();

                //Setting its attribute:
                it.setIdItem(id);
                it.setName(name);
                it.setPrice(price);
                it.setType(type);
                it.setDescription(description);
                it.setBulkPrice(bulkprice);
                it.setBulkQuantity(bulkquantity);
                if (withImage) {
                    it.setPicture(myImage);
                }

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in find item");
        }
        return it;

    }

    /**
     * This method add an Item object in the database.
     *
     * @param entity
     * @param chemin
     */
    @Override
    public void add(Item entity, String chemin) {

        try (PreparedStatement pst_item = connect.prepareStatement(INSERT_ITEMS_SQL)) {

            //GET THE LAST ID OF THE TABLE ITEM
            int id_Item = 0; //The last idItem in the database  will be stored in this var
            Statement st_Item = connect.createStatement(); //Statement to handle the idItem
            ResultSet rs_Item;
            //Query that allow us to get id from the item's table
            rs_Item = st_Item.executeQuery("SELECT * FROM  javaprojectdb.item");

            //Loop to have the last idItem enters the database
            //While there is an item in the table
            while (rs_Item.next()) {
                //if the id_Cart is inferior than the next idItem in the database 
                //(Normally it will always be inferior)
                if (id_Item < rs_Item.getInt("idItem")) {
                    id_Item = rs_Item.getInt("idItem"); //We store the idItem in 'Id_Item'
                }
            }
            //We are incrementing by +1 to always get an unique idItem
            id_Item += 1;
            pst_item.setString(1, Integer.toString(id_Item));
            pst_item.setString(2, entity.getName());
            pst_item.setString(3, Double.toString(entity.getPrice()));
            pst_item.setString(4, entity.getType());
            pst_item.setString(5, entity.getDescription());
            pst_item.setString(6, Double.toString(entity.getBulkPrice()));
            pst_item.setString(7, Integer.toString(entity.getBulkQuantity()));

            if (chemin.length() != 0) {
                InputStream img_to_save = new FileInputStream(new File(chemin));
                pst_item.setBlob(8, img_to_save);
            } else {
                pst_item.setString(8, null);
            }

            //Execute the query:
            pst_item.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in ItemDAOimpl/add()");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ItemDAOimpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * The purpose of this method is to add or edit the discount, so the bulk
     * price and the bulk quantity of an item in the database.
     *
     * @param id item's id we want to change it discount
     * @param bprice the bulk price
     * @param bquantity the bulk quantity
     */
    @Override
    public void updateDiscount(String id, String bprice, String bquantity) {

        PreparedStatement pst_item_update;

        try {
            //CHECK IF THE ID EXIST IN THE TABLE ITEM
            int valid = 0; //If valid = 0 -> The item's id that we search is not in the database    
            //         = 1 -> The item's id that we search is in the database
            Statement st_Item = connect.createStatement(); //Statement to handle the idItem
            ResultSet rs_Item;
            //Query that allow us to get id from the item's table
            rs_Item = st_Item.executeQuery("SELECT * FROM  javaprojectdb.item");

            //Loop that runs all items to check if 'id' exist in the table 'Item'
            //The loop stop when we found the item (if valid is not equal to 0 anymore)
            while ((rs_Item.next()) && (valid == 0)) {
                if (rs_Item.getInt("IdItem") == parseInt(id)) {
                    valid = 1; //We have found a match beetween the id we search and the database
                }
            }
            //If the item' id is find in the database, we modify the value of the bulk price and quantity
            if (valid == 1) {
                //Query that allow to change the value of the bulk price and quantity
                String insert = "UPDATE javaprojectdb.item SET bulkprice = ?, bulkquantity = ? WHERE idItem = ?";

                pst_item_update = connect.prepareStatement(insert);

                //Setting the values of the discount
                pst_item_update.setString(1, bprice);  //The price dicount
                pst_item_update.setString(2, bquantity); //The quantity to get the price discount
                pst_item_update.setString(3, id);
                pst_item_update.execute();
            } else {
                //Else we can't find the item's id in the database
                JOptionPane.showMessageDialog(null, "We couldn't find the item'id : " + id + ".");
            }

        } catch (Exception e) {
            System.out.println("Error in ItemDiscount_JFrame/ButtonConfirm");
            JOptionPane.showMessageDialog(null, "Error: - Bulk Price and Bulk quantity can't be empty\n"
                    + "         - Decimal numbers have to be seperated by a '.' and not a ','");
        }
    }

    /**
     * The purpose of this method is to edit the price of an item in the
     * database.
     *
     * @param idItem
     * @param newPrice
     */
    @Override
    public void updatePrice(int idItem, double newPrice) {

        try (PreparedStatement pst_updateprice = connect.prepareStatement("UPDATE javaprojectdb.item SET price = ? WHERE idItem = ?")) {

            pst_updateprice.setDouble(1, newPrice);
            pst_updateprice.setInt(2, idItem);

            pst_updateprice.executeUpdate();

        } catch (Exception e) {
        }
    }

    /**
     * Methode DAO qui recoit en parametre l'id de l'item et en String le chemin
     * de l'image ajouté pour mettre a jour ce dernier
     *
     * @param idItem
     * @param chemin
     */
    @Override
    public void updatePicture(int idItem, String chemin) {

        try (PreparedStatement pst_updateprice = connect.prepareStatement("UPDATE javaprojectdb.item SET image = ? WHERE idItem = ?")) {

            //On crée un input stream
            InputStream img_to_save = null;
            //On recupere le nouveau fichier avec le chemin recu
            try {
                img_to_save = new FileInputStream(new File(chemin));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ItemDAOimpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            //On envoie le input stream pour mettre a jour l'image selon l'iditem
            pst_updateprice.setBlob(1, img_to_save);
            pst_updateprice.setInt(2, idItem);
            //On execute la commande
            pst_updateprice.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error to update  picture");
        }
    }

    /**
     * The purpose of this method is to an existing item from the database.
     *
     * @param id
     */
    @Override
    public void deleteItem(String id) {

        PreparedStatement pst_item_delete;
        try {
            //CHECK IF THE ID EXIST IN THE TABLE ITEM
            int valid = 0; //If valid = 0 -> The item's id that we search is not in the database    
            //         = 1 -> The item's id that we search is in the database
            Statement st_Item = connect.createStatement(); //Statement to handle the idItem
            ResultSet rs_Item;
            //Query that allow us to get id from the item's table
            rs_Item = st_Item.executeQuery("SELECT * FROM  javaprojectdb.item");

            //Loop that runs all items to check if 'id' exist in the table 'Item'
            //The loop stop when we found the item (if valid is not equal to 0 anymore)
            while ((rs_Item.next()) && (valid == 0)) {
                if (rs_Item.getInt("IdItem") == parseInt(id)) {
                    valid = 1; //We have found a match beetween the id we search and the database
                }
            }
            //If the item id is find in the database, we delete the item in the database
            if (valid == 1) {

                String insert = "DELETE FROM javaprojectdb.item WHERE idItem = ?";

                pst_item_delete = connect.prepareStatement(insert);

                //Deleting the item by puting the id of the item which is the primary key of the table
                pst_item_delete.setString(1, id);
                pst_item_delete.execute();
            } else {
                //Else we can't find the item's id in the database
                JOptionPane.showMessageDialog(null, "We couldn't find the item'id : " + id + ".");
            }

        } catch (SQLException | NumberFormatException | HeadlessException e) {
            System.out.println("Error in Employee/DeleteItem()");
            JOptionPane.showMessageDialog(null, "Error: - To delete an item you have to enter it's id.");
        }
    }

    /**
     * The purpose of this method is to collect the description in the data base
     * of the item id that is passed as parameters.
     *
     * @param id
     * @return description : the description of the id send as parameter
     */
    public String getItemDescription(int id) {
        //Variables declaration
        String description = "";
        int valid = 0;

        try {
            Statement st_Items = connect.createStatement();
            ResultSet rs_Items;
            String query_getitems = "SELECT * FROM javaprojectdb.item";
            rs_Items = st_Items.executeQuery(query_getitems);
            //Loop that runs through all the items in the database
            //if the loop stop and valid is stil equal to zero it mean that the id doesn't exist
            while ((rs_Items.next()) && (valid == 0)) {
                //If the ids match
                if (parseInt(rs_Items.getString("idItem")) == id) {
                    description = rs_Items.getString("description"); //Put the description of the item in the string
                    valid = 1; //We can stop the loop
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in ItemDAOimpl/getItemDescription");
        }

        return description;
    }

    /**
     * Selectionne dans la base de donnée les items en fonction du type
     *
     * @param type
     * @return list of item
     */
    @Override
    public ArrayList<Item> selectItemsByType(String type) {
        ArrayList<Item> items = new ArrayList<Item>();

        try (PreparedStatement pst_item = connect.prepareStatement(SELECT_ITEMS_BY_TYPE)) {
            pst_item.setString(1, type);
            ResultSet rs_items = pst_item.executeQuery();
            ImageIcon myImage;
            while (rs_items.next()) {
                int id = rs_items.getInt("idItem");
                String name = rs_items.getString("name");
                double price = rs_items.getDouble("price");
                String description = rs_items.getString("description");
                double bulkPrice = rs_items.getDouble("bulkprice");
                int bulkQuantity = rs_items.getInt("bulkquantity");
                byte[] img = rs_items.getBytes("image");

                if (img != null) {
                    myImage = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
                } else {
                    myImage = null;
                }
                items.add(new Item(id, name, price, type, description, bulkPrice, bulkQuantity, myImage));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in find item");
        }
        return items;
    }
}
