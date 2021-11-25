/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import project.model.entity.Item;

/**
 * Interface DAO qui permet de faire la connection avec la 
 * table item de la base de donnée
 * @author Kévin
 */
public interface ItemDAO {
    
    public Connection connect = ConnectionMySQL.getInstance();
    
    public abstract Item find(int id, boolean withImage);
    public abstract void add(Item entity, String chemin);
    public abstract void updatePrice (int idItem, double newPrice);
    public abstract void updateDiscount(String id, String bprice, String bquantity);
    public abstract void updatePicture(int idItem, String chemin);
    public abstract void deleteItem(String id);
    public abstract String getItemDescription(int id);
    public abstract ArrayList<Item> selectItemsByType(String type);
}
