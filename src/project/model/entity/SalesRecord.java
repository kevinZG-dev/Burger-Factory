/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;

import java.util.ArrayList;

/**
 * Classe objet salesrecord du model
 * @author Kévin
 */
public class SalesRecord {

    private int idSalesRecord;
    private double total;
    private String email;
    private String fname;
    private String lname;
    private String adress;
    private int zipcode;
    private String city;
    private int telnb;
    private String receipt;
    private ArrayList<Item> listCommand;
    private String date;
    private String heure;

    public SalesRecord() {
    }
    
    public SalesRecord(int idSalesRecord, double total, String email, String fname, String lname, String adress, 
            int zipcode, String city, int telnb, String receipt, String date, String heure) {
        
        this.idSalesRecord = idSalesRecord;
        this.total = total;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.adress = adress;
        this.zipcode = zipcode;
        this.city = city;
        this.telnb = telnb;
        this.receipt = receipt;
        this.date = date;
        this.heure = heure;
    
    }

    /**
     * Getters et Setters
     * @return 
     */
    public int getIdSalesRecord() {
        return idSalesRecord;
    }

    public void setIdSalesRecord(int idSalesRecord) {
        this.idSalesRecord = idSalesRecord;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTelnb() {
        return telnb;
    }

    public void setTelnb(int telnb) {
        this.telnb = telnb;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public ArrayList<Item> getListCommand() {
        return listCommand;
    }

    public void setListCommand(ArrayList<Item> listCommand) {
        this.listCommand = listCommand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

}
