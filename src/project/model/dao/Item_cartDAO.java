/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import project.model.entity.Item_cart;

/**
 * Interface DAO qui permet de faire la connection avec la 
 * table Item cart de la base de donnée
 * @author Kévin
 */
public interface Item_cartDAO {
    
    public Connection connect = ConnectionMySQL.getInstance();
    
    public abstract ArrayList<Item_cart> selectItemsYourCart(int idCart);
    public abstract void deleteItemCartByCartId (int idCart);
    public abstract void SaveItemCart (ArrayList<Item_cart> itemCart);
    public abstract void deleteItemCartByItemId(int idItem);
    
}
