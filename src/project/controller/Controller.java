/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller;

import javax.swing.JOptionPane;
import project.view.Login_JFrame;

/**
 * Classe controller qui permet d'appeller en premier
 * la JFRAME LOGIN
 * @author Kévin
 */
public class Controller {
    
    public static void controller() {
        try{
            Login_JFrame login = new Login_JFrame();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "Error to create a login jframe in the main");
        }
    }
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        controller();
    }
}
