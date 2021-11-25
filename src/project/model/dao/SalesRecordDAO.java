/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import project.model.entity.SalesRecord;

/**
 * Interface DAO qui permet de faire la connection avec la 
 * table salesrecord de la base de donnée
 * @author maximeattal
 */
public interface SalesRecordDAO {
    
    public Connection connect = ConnectionMySQL.getInstance();
    
    public abstract  int newIdSales();
    public abstract void add(SalesRecord commande);
    public abstract ArrayList<String> getListSalesRecord();
    public abstract String getReceipt(int id);
    public abstract ArrayList<SalesRecord> selectByEmail(String email);
    public abstract int countTotalOrder();
    public abstract ArrayList<Double> selectListTotal();
    
}
