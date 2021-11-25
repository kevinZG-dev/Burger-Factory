/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import project.model.entity.Item_cart;
import project.model.entity.SalesRecord_Item;

/**
 * Interface DAO qui permet de faire la connection avec la 
 * table salesrecord item de la base de donnée
 * @author Kévin
 */
public interface SalesRecord_ItemDAO {
    
    public Connection connect = ConnectionMySQL.getInstance();
    
    public abstract ArrayList<SalesRecord_Item> selectItemsYourCart(int idSalesRecord);
    public abstract void SaveItemSalesRecord (ArrayList<SalesRecord_Item> itemSalesRecord);
    public abstract ArrayList<SalesRecord_Item> selectSalesRecord_ItemByType(String type);
    public abstract void deleteItemSalesByItemId(int idItem);
}
