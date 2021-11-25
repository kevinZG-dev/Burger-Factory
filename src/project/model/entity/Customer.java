/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;

import java.util.ArrayList;

/**
 * Classe objet customer du model qui herite de la classe person
 * @author Kévin
 */
public class Customer extends Person
{
    private int myIdCart;
    private Cart myCart; //Cart of the customer
    private ArrayList<SalesRecord> mySalesRecords;
     
    public Customer(){
       
    }

    public Cart getMycart(){
        return myCart;
    }
    public void setMyCart(Cart myCart){
       this.myCart = myCart;
    }
    
    public int getMyIdCart() {
        return myIdCart;
    }

    public void setMyIdCart(int myIdCart) {
        this.myIdCart = myIdCart;
    }

    public ArrayList<SalesRecord> getMySalesRecords() {
        return mySalesRecords;
    }

    public void setMySalesRecords(ArrayList<SalesRecord> mySalesRecords) {
        this.mySalesRecords = mySalesRecords;
    }
    

    
}

