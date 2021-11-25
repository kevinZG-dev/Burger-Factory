/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import project.model.entity.SalesRecord_Item;

/**
 * Classe DAO qui implemente le salesrecord_itemDAO et qui définit les méthodes
 * @author Kévin
 */
public class SalesRecord_ItemDAOimpl implements SalesRecord_ItemDAO{
    
    /**
     * Methode DAO qui permet de selection dans la table salesrecord_item
     * selon l'id du salesrecord
     * @param idSalesRecord
     * @return 
     */
    @Override
    
        public ArrayList<SalesRecord_Item> selectItemsYourCart(int idSalesRecord) {
        ArrayList<SalesRecord_Item> items_Cart = new ArrayList<SalesRecord_Item>();

        try (PreparedStatement pst_itemCart = connect.prepareStatement("SELECT * FROM javaprojectdb.salesrecord_item WHERE salesrecord = item =?")) {
            pst_itemCart.setInt(1, idSalesRecord);
            ResultSet rs_sales = pst_itemCart.executeQuery();

            while (rs_sales.next()) {
                int id_Item = rs_sales.getInt("Item_idItem");
                int quantity = rs_sales.getInt("quantity");
                String type = rs_sales.getString("type");

                items_Cart.add(new SalesRecord_Item(id_Item, idSalesRecord, quantity, type));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in find salesrecord_item");
        }
        return items_Cart;
    }
    
    
    /**
     * Methode DAO qui permet de sauvegarder dans la bdd
     * les salesrecord_iteem lorsqu'on effectue une commande
     * @param itemSalesRecord 
     */
    @Override
    public void SaveItemSalesRecord (ArrayList<SalesRecord_Item> itemSalesRecord){
        try (PreparedStatement pst_sales_item = connect.prepareStatement("INSERT INTO javaprojectdb.salesrecord_item" + "  (salesrecord_idSalesRecord,  item_idItem, quantity, type) VALUES " + " (?, ?, ?, ?)")) {
            for (int i = 0 ; i < itemSalesRecord.size() ; i++) {
                pst_sales_item.setInt(1, itemSalesRecord.get(i).getSalesrecord_idsalesrecord());
                pst_sales_item.setInt(2, itemSalesRecord.get(i).getItem_idItem());
                pst_sales_item.setInt(3, itemSalesRecord.get(i).getQuantity());
                pst_sales_item.setString(4, itemSalesRecord.get(i).getType());
                pst_sales_item.executeUpdate();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in add salesrecord_item");
        }
    }
    /**
     * Methode DAO qui permet de selectionner dans la base de donnée 
     * les salesrecord_item selon son type pour pouvoir effectuer 
     * des statistiques en séparant selon le type d'item
     * @param type
     * @return 
     */
    @Override
    public ArrayList<SalesRecord_Item> selectSalesRecord_ItemByType(String type) {
        ArrayList<SalesRecord_Item> salesRecord_Items = new ArrayList<SalesRecord_Item>();

        try (PreparedStatement pst_sales_item = connect.prepareStatement("SELECT salesrecord_idSalesRecord ,item_idItem, quantity FROM javaprojectdb.salesrecord_item WHERE type =?")) {
            pst_sales_item.setString(1, type);
            ResultSet rs_sales_item= pst_sales_item.executeQuery();

            while (rs_sales_item.next()) {
                int salesrecordId = rs_sales_item.getInt("salesrecord_idSalesRecord");
                int id_Item = rs_sales_item.getInt("item_idItem");
                int quantity = rs_sales_item.getInt("quantity");

                salesRecord_Items.add(new SalesRecord_Item(salesrecordId, id_Item, quantity, type));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in find item_cart");
        }
        return salesRecord_Items;
    }
    
    /**
     * Methode qui permet de delete les items d'un cart en recevant l'id de l'item
     * @param idItem
     */
    @Override
    public void deleteItemSalesByItemId(int idItem) {
        
        try (PreparedStatement pst_salesItem= connect.prepareStatement("DELETE FROM javaprojectdb.salesrecord_item WHERE Item_idItem =?")){
            
            pst_salesItem.setInt(1, idItem);
            pst_salesItem.executeUpdate();
        } catch (Exception e) {
        }
        
    }
}

