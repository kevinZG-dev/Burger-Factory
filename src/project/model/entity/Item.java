/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;

import javax.swing.ImageIcon;

/**
 * Classe objet item du model
 * @author Kévin
 */
public class Item {

    private int idItem; //Unique item number
    private String name; //Name of the item
    private double price; //Unit price of the item
    private String description;//description de l'item
    private String type;//type de l item
    private double prixTotal ;
    private double bulkPrice; //Bulk price of the item
    private int bulkQuantity; //Quantity from which the bulk price applies.
    private int quantity;//Quantité ajouté par le client
    private int sellsquantity;
    private ImageIcon picture;//Photo de l'item
    
    
    public Item() {
        this.quantity = 1;
        this.prixTotal = 0;
        this.sellsquantity = 0;
    }

    public Item(int idItem, String name, double price, String type, String description, double bulkPrice, int bulkQuantity, ImageIcon picture) {
        this.idItem = idItem;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
        this.bulkPrice = bulkPrice;
        this.bulkQuantity = bulkQuantity;
        this.quantity = 1;
        this.prixTotal = 0;
        this.picture = picture;
    }
    public Item(Item it, int quantity) {
        this.idItem = it.getIdItem();
        this.name = it.getName();
        this.price = it.getPrice();
        this.type = it.getType();
        this.description = it.getDescription();
        this.bulkPrice = it.getBulkPrice();
        this.bulkQuantity = it.getBulkQuantity();
        this.quantity = quantity;
        this.prixTotal = 0;
 
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int id) {
        this.idItem = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public double getBulkPrice() {
        return bulkPrice;
    }

    public void setBulkPrice(double bulkPrice) {
        this.bulkPrice = bulkPrice;
    }

    public int getBulkQuantity() {
        return bulkQuantity;
    }

    public void setBulkQuantity(int bulkQuantity) {
        this.bulkQuantity = bulkQuantity;
    }



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ImageIcon getPicture() {
        return picture;
    }

    public void setPicture(ImageIcon picture) {
        this.picture = picture;
    }

    public int getSellsquantity() {
        return sellsquantity;
    }

    public void setSellsquantity(int sellsquantity) {
        this.sellsquantity = sellsquantity;
    }

}
