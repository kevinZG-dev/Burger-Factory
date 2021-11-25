/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;


import java.util.*;

/**
 * Classe objet cart du model
 * @author maximeattal
 */
public class Cart {
    private int idCart; //Unique cart number
    private double total; //Total price of the cart when the user goes to the checkout
    private ArrayList<Item> myItems; //List of items in the cart
    private Customer myCustomer; //Customer who owns the cart
    
    /** Constructor:
    *   Parameters: - 'id' : Cart's unique id
    *               - 't' : Cart's total price 
    *               - 'itemsxQty' : List of items with qty in the cart 
    */
    public Cart(int id, Customer myCustomer){
        idCart = id; this.myCustomer = myCustomer;
        myItems = new ArrayList<Item>();
    }
    
    public int getId(){
        return idCart;
    }
    public void setId(int id){
        this.idCart = id;
    }
    
    public double getTotal(){
        return total;
    }
    public void setTotal(double total){
        this.total = total;
    }
    
    public ArrayList<Item> getMyItems(){
        return myItems;
    }
    public void setMyItems(Item item){
        myItems.add(item);
    }
    
    public Customer getMyCustomer(){
        return myCustomer;
    }
    public void setMyCustomer(Customer myCustomer){
        this.myCustomer = myCustomer;
    }
}
