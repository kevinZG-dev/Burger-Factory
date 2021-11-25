/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;

/**
 * Classe objet salesrecordItem du model
 * qui permet d'associer les items aux commandes
 * et ainsi pouvoir accès aux détails des items
 * de la commandes
 * @author Kévin
 */
public class SalesRecord_Item {
    private int salesrecord_idsalesrecord;
    private int item_idItem;
    private int quantity;
    private String type;
    
    public SalesRecord_Item(int salesrecord_idsalesrecord, int item_idItem, int quantity, String type) {
        this.salesrecord_idsalesrecord = salesrecord_idsalesrecord;
        this.item_idItem = item_idItem;
        this.quantity = quantity;  
        this.type = type;
}

    /**
     * Getters et Setters
     * @return 
     */
    public int getSalesrecord_idsalesrecord() {
        return salesrecord_idsalesrecord;
    }

    public void setSalesrecord_idsalesrecord(int salesrecord_idsalesrecord) {
        this.salesrecord_idsalesrecord = salesrecord_idsalesrecord;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
    
}
