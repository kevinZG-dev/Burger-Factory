/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;

/**
 * Classe objet item_cart du model qui permet de charger
 * les items d'un cart issu d'une sauvegarde du cart lors d'une deconnection
 * sans payer
 * 
 * @author Kévin
 */
public class Item_cart {

    private int cart_idCart;
    private int item_idItem;
    private int quantity;

    public Item_cart(int item_idItem, int cart_idCart, int quantity) {
        this.cart_idCart = cart_idCart;
        this.item_idItem = item_idItem;
        this.quantity = quantity;
    }

    public int getCart_idCart() {
        return cart_idCart;
    }

    public void setCart_idCart(int cart_idCart) {
        this.cart_idCart = cart_idCart;
    }

    public int getItem_idItem() {
        return item_idItem;
    }

    public void setItem_idItem(int item_idItem) {
        this.item_idItem = item_idItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
