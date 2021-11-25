/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.model.entity;


/**
 * Classe objet person du model
 * @author Kévin
 */
public class Person {
    protected int idPerson; //Unique person number
    protected String l_name; //Last name of the person
    protected String f_name; //First name of the person
    protected String adress; //Mailing adress of the person
    protected int zipcode; //Zipcode where the person live
    protected String city; //City where the person live
    protected int tel_nb; //Telephone number of the person
    protected String email; //Login/username of the account of the person
    protected String password; //Password of the account of the person
    

    
    public Person()
    {
   
    }

    public int getId(){
        return idPerson;
    }
    public void setId(int id){
        this.idPerson = id;
    }
    
    public String getl_name(){
        return l_name;
    }
    public void setl_name(String l_name){
        this.l_name = l_name;
    }
    
    public String getf_name(){
        return f_name;
    }
    public void setf_name(String f_name){
        this.f_name = f_name;
    }
    
    public String getAdress(){
        return adress;
    }
    public void setAdress(String adress){
        this.adress = adress;
    }
    
    public int getZipcode(){
        return zipcode;
    }
    public void setZipcode(int zipcode){
        this.zipcode = zipcode;
    }
    
    public String getCity(){
        return city;
    }
    public void setCity(String city){
        this.city = city;
    }
    
    public int getTel_nb(){
        return tel_nb;
    }
    public void setTel_nb(int tel_nb){
        this.tel_nb = tel_nb;
    }
    
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
