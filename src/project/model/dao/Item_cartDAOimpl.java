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

import project.model.entity.Item_cart;

/**
 * Classe DAO qui implemente l'item_cartDAO et qui définit les méthodes
 * @author Kévin
 */
public class Item_cartDAOimpl implements Item_cartDAO {

    /**
     * Methode qui permet de retrouver dans la base de donnée, la sauvegarde du
     * cart
     *
     * @param idCart
     * @return items_Cart
     */
    @Override
    public ArrayList<Item_cart> selectItemsYourCart(int idCart) {
        ArrayList<Item_cart> items_Cart = new ArrayList<Item_cart>();

        try (PreparedStatement pst_itemCart = connect.prepareStatement("SELECT Item_idItem,quantity FROM javaprojectdb.item_cart WHERE Cart_idCart =?")) {
            pst_itemCart.setInt(1, idCart);
            ResultSet rs_items = pst_itemCart.executeQuery();

            while (rs_items.next()) {
                int id_Item = rs_items.getInt("Item_idItem");
                int quantity = rs_items.getInt("quantity");

                items_Cart.add(new Item_cart(id_Item, idCart, quantity));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in find item_cart");
        }
        return items_Cart;
    }

    /**
     * Methode qui permet de delete les items d'un cart en recevant l'id du cart
     * @param idCart
     */
    @Override
    public void deleteItemCartByCartId(int idCart) {
        
        try (PreparedStatement pst_itemCart = connect.prepareStatement("DELETE FROM javaprojectdb.item_cart WHERE Cart_idCart =?")){
            
            pst_itemCart.setInt(1, idCart);
            pst_itemCart.executeUpdate();
        } catch (Exception e) {
        }
        
    }

    
    /**
     * Methode qui permet de delete les items d'un cart en recevant l'id de l'item
     * @param idItem
     */
    @Override
    public void deleteItemCartByItemId(int idItem) {
        
        try (PreparedStatement pst_itemCart = connect.prepareStatement("DELETE FROM javaprojectdb.item_cart WHERE Item_idItem =?")){
            
            pst_itemCart.setInt(1, idItem);
            pst_itemCart.executeUpdate();
        } catch (Exception e) {
        }
        
    }
    
    /**
     * Methode qui permet de sauvegarder un cart dans la bdd
     * @param itemCart
     */
    @Override
    public void SaveItemCart(ArrayList<Item_cart> itemCart) {
        try (PreparedStatement pst_item_cart = connect.prepareStatement("INSERT INTO javaprojectdb.item_cart" + "  (Item_idItem,  Cart_idCart, quantity) VALUES " + " (?, ?, ?)")) {
            for (Item_cart itemCart1 : itemCart) {
                pst_item_cart.setInt(1, itemCart1.getItem_idItem());
                pst_item_cart.setInt(2, itemCart1.getCart_idCart());
                pst_item_cart.setInt(3, itemCart1.getQuantity());
                pst_item_cart.executeUpdate();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in add itemCart");
        }
    }
}
