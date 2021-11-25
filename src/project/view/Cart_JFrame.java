/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.view;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import project.model.dao.ItemDAOimpl;
import project.model.dao.SalesRecordDAOimpl;
import project.model.entity.Item;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.text.DefaultCaret;
import project.model.dao.CustomerDAOimpl;
import project.model.dao.Item_cartDAOimpl;
import project.model.dao.SalesRecord_ItemDAOimpl;
import project.model.entity.Cart;
import project.model.entity.Customer;
import project.model.entity.Item_cart;
import project.model.entity.SalesRecord;
import project.model.entity.SalesRecord_Item;

/**
 * Classe Cart_JFrame qui hérite de la classe JFrame
 * et qui permet l'affchage de l'interface client du programme
 * @author Kévin
 */
public class Cart_JFrame extends javax.swing.JFrame {

    //Connection a la base de donnée avec le patern DAO
    private final ItemDAOimpl itDAO = new ItemDAOimpl();
    private final SalesRecordDAOimpl salesDAO = new SalesRecordDAOimpl();
    private final Item_cartDAOimpl itCartDAO = new Item_cartDAOimpl();
    private final CustomerDAOimpl custDAO = new CustomerDAOimpl();
    private final SalesRecord_ItemDAOimpl salesItDAO = new SalesRecord_ItemDAOimpl();
    //Listes des shops pour chaque type
    private ArrayList<Item> shop_burger;
    private ArrayList<Item> shop_topping;
    private ArrayList<Item> shop_drink;
    private ArrayList<Item> shop_dessert;

    //models pour les Jlists du shop
    private DefaultListModel model1;
    private DefaultListModel model2;
    private DefaultListModel model3;
    private DefaultListModel model4;
    private DefaultListModel model_salesrecord;
    //Customer qui entre dans son espace cart
    private static Customer myCustomer;
    //Creation d'un objet Cart
    private Cart myCart;
    private SalesRecord mySalesRecord;
    //Liste d'item_cart qui permettent de chargér le dernier cart sauvegardé dans la bdd
    private ArrayList<Item_cart> myItem_cart;
    private ArrayList<SalesRecord_Item> mySalesRecord_Items;
    //Date et heure
    private Date d = new Date();
    private SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat t = new SimpleDateFormat("hh:mm:ss");
    //2 decimal maxi
    private DecimalFormat df;

    /**
     * On recupere le customer qui s'est connecté
     * Constructeur qui appelle toutes les méthodes lors de son instanciation
     *
     * @param myCust
     */
    public Cart_JFrame(Customer myCust) {
        
        initComponents();

        initJtabbed();
        closeWithCross();
        //initialise format 2 decimal maxi
        df = new DecimalFormat("0.00");
        initJlists();//initialise les models et les lists
        initShops(); //instancie et charge ensuite la liste des items du shop
        myCustomer = myCust; //on recupere les infos du customer qui s'est connecté
        initHistory();//Initialisation de l'historique de commande
        initLabels();//Initialise les labels
        initCart(); //On instancie le cart
        initSalesRecord();//Initialise le salesrecord
        initReceipt(); //initialise le tickets de caisse
        // On rend le spinner non editable avec le clavier
        ((DefaultEditor) jSpinner_Qty.getEditor()).getTextField().setEditable(false);
        setVisible(true);
        //

    }

    /**
     * Instancie le Cart qui recupere dans son constructeur l'IdCart du customer
     * qui s'est connecté ainsi que le customer On set dans le customer le cart
     * On charge aussi à partir de la base de donnée, le cart du customers qui a
     * été sauvegardé si il a cancel avec un cart
     */
    public void initCart() {

        try {
            //Chargé les infos de item_cart pour chargé le dernier cart du customer
            myItem_cart = itCartDAO.selectItemsYourCart(myCustomer.getMyIdCart());
            myCart = new Cart(myCustomer.getMyIdCart(), myCustomer);
            myCustomer.setMyCart(myCart);

            for (int i = 0; i < myItem_cart.size(); i++) {

                myCart.getMyItems().add(itDAO.find(myItem_cart.get(i).getItem_idItem(), true));
                myCart.getMyItems().get(i).setQuantity(myItem_cart.get(i).getQuantity());
                /**
                 * Si l'item selectionné possede un prix discount et si sa
                 * nouvelle quantité permet d'en profiter
                 */
                if (myCart.getMyItems().get(i).getBulkPrice() > 0 && myCart.getMyItems().get(i).getBulkQuantity() > 0
                        && myCart.getMyItems().get(i).getQuantity() >= myCart.getMyItems().get(i).getBulkQuantity()) {
                    //Je fait la division de la quantité avec le nombre du bulkquantity pour avoir la partie entière
                    int nbrLot = (Integer) myCart.getMyItems().get(i).getQuantity() / myCart.getMyItems().get(i).getBulkQuantity();
                    //Je fais le reste de la div euclidienne de cette meme div pour avoir le reste qui sera facturé en prix unitaire
                    int nbr_en_unit = (Integer) myCart.getMyItems().get(i).getQuantity() % myCart.getMyItems().get(i).getBulkQuantity();
                    // On effectue ensuite le calcul du prix total de l'item selon sa quantité, son prix discount en lot
                    double price = nbrLot * myCart.getMyItems().get(i).getBulkPrice() + nbr_en_unit * myCart.getMyItems().get(i).getPrice();
                    //On le set dans le cart
                    myCart.getMyItems().get(i).setPrixTotal(price);

                } else {
                    //On set le prix total en faisant juste quantité x prix unitaire
                    myCart.getMyItems().get(i).setPrixTotal(myCart.getMyItems().get(i).getPrice() * myCart.getMyItems().get(i).getQuantity());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Cart");
        }

        updateTotal();
        totalToDisplay();
    }

    public void initLabels() {

        try {
            this.setTitle("Burger Factory");
            //instancie le message de bienvenue
            jLabel_bonjour.setText("Welcome " + myCustomer.getf_name());
            jLabel_email.setText(myCustomer.getEmail());
            jLabel_firstname.setText(myCustomer.getf_name());
            jLabel_lastname.setText(myCustomer.getl_name());
            jLabel_adress.setText(myCustomer.getAdress());
            jLabel_zipcode.setText(Integer.toString(myCustomer.getZipcode()));
            jLabel_city.setText(myCustomer.getCity());
            jLabel_phone.setText("+33 " + Integer.toString(myCustomer.getTel_nb()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Labels");
        }

    }

    public void initJtabbed() {
        try {
            //jTabbed pour mettre les différentes fenetre a droite
            jTabbedPane_compte.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            jTabbedPane_compte.setTabPlacement(JTabbedPane.TOP);
            ImageIcon icon_cart = new ImageIcon(this.getClass().getResource("/images/icon_cart.png"));
            ImageIcon icon_order = new ImageIcon(this.getClass().getResource("/images/icon_order.png"));
            ImageIcon icon_account = new ImageIcon(this.getClass().getResource("/images/icon_account.png"));
            //Instancie les icones des jList
            ImageIcon icon_burger = new ImageIcon(this.getClass().getResource("/images/icon_burger.png"));
            ImageIcon icon_topping = new ImageIcon(this.getClass().getResource("/images/icon_topping.png"));
            ImageIcon icon_drink = new ImageIcon(this.getClass().getResource("/images/icon_drink.png"));
            ImageIcon icon_dessert = new ImageIcon(this.getClass().getResource("/images/icon_dessert.png"));

            jTabbedPane_compte.addTab("Cart", icon_cart, jPanel_Cart);
            jTabbedPane_compte.addTab("Order", icon_order, jPanel_Orders);
            jTabbedPane_compte.addTab("Account", icon_account, jPanel_Account);
            //On add les icons a chaque pane correspondant
            jTabbedPane2.addTab("Burger", icon_burger, jPanel_burger);
            jTabbedPane2.addTab("Topping", icon_topping, jPanel_topping);
            jTabbedPane2.addTab("Drink", icon_drink, jPanel_drink);
            jTabbedPane2.addTab("Dessert", icon_dessert, jPanel_dessert);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Jtabbed");
        }

    }

    /**
     * methode qui initialise les jLists
     */
    @SuppressWarnings("unchecked")
    public void initJlists() {

        try {
            //Instancie les models pour les jLists
            model1 = new DefaultListModel<>();
            model2 = new DefaultListModel<>();
            model3 = new DefaultListModel<>();
            model4 = new DefaultListModel<>();
            model_salesrecord = new DefaultListModel();

            //On set les models dans les jLists correspondantes
            jList_burger.setModel(model1);
            jList_topping.setModel(model2);
            jList_drink.setModel(model3);
            jList_dessert.setModel(model4);
            jList_orderhistory.setModel(model_salesrecord);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Jlists");
        }

    }

    /**
     * On initialise les listes d'objet items selon son type On load a paritr de
     * la bdd les differents items, dans sa liste crrespondante On affiche dans
     * les jLists les items correspondants
     */
    public void initShops() {

        try {
            //Init juste des reglages pour le textarea de la description
            jTextArea_description.setLineWrap(true);
            jTextArea_description.setWrapStyleWord(true);

            dontAutoScroll(jTextArea_Ticket);//Empeche le scroll en bas auto de du textarea receipt

            //Init shops
            shop_burger = new ArrayList<Item>();
            shop_topping = new ArrayList<Item>();
            shop_drink = new ArrayList<Item>();
            shop_dessert = new ArrayList<Item>();

            //Load shops a partir de la base de donnée
            shop_burger = itDAO.selectItemsByType("burger");
            shop_topping = itDAO.selectItemsByType("topping");
            shop_drink = itDAO.selectItemsByType("drink");
            shop_dessert = itDAO.selectItemsByType("dessert");

            //On affiche les shops sur les jList
            shopToDisplay(shop_burger, model1);
            shopToDisplay(shop_topping, model2);
            shopToDisplay(shop_drink, model3);
            shopToDisplay(shop_dessert, model4);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Shops");
        }

    }

    /**
     * Methode qui initialise l'historique des commande du customer
     */
    public void initHistory() {

        try {
            //On empeche l'autoscroll du textfield receipt
            dontAutoScroll(jTextArea_Ticket2);
            //Instancie la liste Array de toutes les salesrecord du customer
            ArrayList<SalesRecord> allmySalesRecords;
            //On appelle le DAO qui va renvoyé la liste de l'historique du customer
            allmySalesRecords = salesDAO.selectByEmail(myCustomer.getEmail());
            myCustomer.setMySalesRecords(allmySalesRecords);
            //On fait afficher la liste array dans le model jList en appellant la methode suivante
            historyToDisplay(myCustomer.getMySalesRecords(), model_salesrecord);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Orders History");
        }

    }

    /**
     * Methode pour initialise le receipt
     */
    public final void initReceipt() {

        try {
            jTextArea_Ticket.setEditable(false);  //We can't edit the text area  .
            dontAutoScroll(jTextArea_Ticket); //Empeche l'autoscroll du receipt en bas
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init Receipt");
        }

        loadReceipt();//On charge le receipt

    }

    /**
     * Methode qui instancie le salesrecord
     */
    public void initSalesRecord() {
        mySalesRecord_Items = new ArrayList<SalesRecord_Item>();
        try {
            mySalesRecord = new SalesRecord();
            mySalesRecord.setIdSalesRecord(salesDAO.newIdSales());
            mySalesRecord.setDate(s.format(d));
            mySalesRecord.setHeure(t.format(d));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to init SalesRecord");
        }
     

    }

    /**
     * Methode qui permet de refresh la receipt en appelant les methodes
     * necessaires
     */
    public void loadReceipt() {
        try {
            jTextArea_Ticket.setText("");
            topReceipt();
            itemsReceipt();
            totalReceipt();
            bottomReceipt();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to load receipt");
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel_logo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane_compte = new javax.swing.JTabbedPane();
        jPanel_Cart = new javax.swing.JPanel();
        jLabel_bonjour = new javax.swing.JLabel();
        jPanel_order = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea_Ticket = new javax.swing.JTextArea();
        jLabel_orderedburger = new javax.swing.JLabel();
        jButton_remove = new javax.swing.JButton();
        jButton_clear = new javax.swing.JButton();
        jPanel_for_payment_cancel = new javax.swing.JPanel();
        jButton_payment = new javax.swing.JButton();
        jButton_cancel = new javax.swing.JButton();
        jPanel_price = new javax.swing.JPanel();
        jLabel_total_amount = new javax.swing.JLabel();
        jTextField_total = new javax.swing.JTextField();
        jPanel_shop = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel_burger = new javax.swing.JPanel();
        jScrollPane_burger = new javax.swing.JScrollPane();
        jList_burger = new javax.swing.JList();
        jPanel_topping = new javax.swing.JPanel();
        jScrollPane_topping = new javax.swing.JScrollPane();
        jList_topping = new javax.swing.JList();
        jPanel_drink = new javax.swing.JPanel();
        jScrollPane_drink = new javax.swing.JScrollPane();
        jList_drink = new javax.swing.JList();
        jPanel_dessert = new javax.swing.JPanel();
        jScrollPane_dessert = new javax.swing.JScrollPane();
        jList_dessert = new javax.swing.JList();
        jButton_add = new javax.swing.JButton();
        jButton_discount = new javax.swing.JButton();
        jLabel_qty = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel_descrption = new javax.swing.JLabel();
        jLabel_carte = new javax.swing.JLabel();
        jTextField_price = new javax.swing.JTextField();
        jLabel_price = new javax.swing.JLabel();
        jSpinner_Qty = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea_description = new javax.swing.JTextArea();
        jPanel_show_item = new javax.swing.JPanel();
        jLabel_image = new javax.swing.JLabel();
        jLabel_ardoise1 = new javax.swing.JLabel();
        jPanel_Orders = new javax.swing.JPanel();
        jLabel_orderhistory = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList_orderhistory = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea_Ticket2 = new javax.swing.JTextArea();
        jButton_imprim5 = new javax.swing.JButton();
        jButton_email = new javax.swing.JButton();
        jLabel_receipt = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel_ardoise2 = new javax.swing.JLabel();
        jPanel_Account = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jButton_savechanges = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        jLabel_email = new javax.swing.JLabel();
        jLabel_firstname = new javax.swing.JLabel();
        jLabel_lastname = new javax.swing.JLabel();
        jLabel_adress = new javax.swing.JLabel();
        jLabel_zipcode = new javax.swing.JLabel();
        jLabel_city = new javax.swing.JLabel();
        jLabel_phone = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jPassword_current = new javax.swing.JPasswordField();
        jPassword_new = new javax.swing.JPasswordField();
        jPassword_confirm = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel_ardoise3 = new javax.swing.JLabel();
        jLabel_ardoise = new javax.swing.JLabel();
        jLabel_back = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo_burger_petit.png"))); // NOI18N
        jLabel_logo.setBorder(new javax.swing.border.MatteBorder(null));
        getContentPane().add(jLabel_logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 0, 90, 90));

        jPanel1.setPreferredSize(new java.awt.Dimension(954, 650));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane_compte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51), 0));
        jTabbedPane_compte.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jPanel_Cart.setBackground(new java.awt.Color(0, 0, 0));
        jPanel_Cart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 0));
        jPanel_Cart.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_bonjour.setBackground(new java.awt.Color(204, 204, 204));
        jLabel_bonjour.setFont(new java.awt.Font("Freestyle Script", 0, 48)); // NOI18N
        jLabel_bonjour.setForeground(new java.awt.Color(255, 153, 0));
        jPanel_Cart.add(jLabel_bonjour, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 280, 50));

        jPanel_order.setBackground(new java.awt.Color(209, 225, 255));
        jPanel_order.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(47, 85, 151), new java.awt.Color(47, 85, 151)));
        jPanel_order.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea_Ticket.setColumns(20);
        jTextArea_Ticket.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea_Ticket.setRows(5);
        jTextArea_Ticket.setAutoscrolls(false);
        jScrollPane1.setViewportView(jTextArea_Ticket);

        jPanel_order.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 260, 280));

        jLabel_orderedburger.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_orderedburger.setForeground(new java.awt.Color(0, 51, 255));
        jLabel_orderedburger.setText("Order");
        jPanel_order.add(jLabel_orderedburger, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 20));

        jButton_remove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove_button.png"))); // NOI18N
        jButton_remove.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove_button_selected.png"))); // NOI18N
        jButton_remove.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove_button_entered.png"))); // NOI18N
        jButton_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_removeActionPerformed(evt);
            }
        });
        jPanel_order.add(jButton_remove, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 72, 59, 59));

        jButton_clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/claer_button.jpg"))); // NOI18N
        jButton_clear.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/claer_button_selected.jpg"))); // NOI18N
        jButton_clear.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/claer_button_entered.jpg"))); // NOI18N
        jButton_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_clearActionPerformed(evt);
            }
        });
        jPanel_order.add(jButton_clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 200, 59, 59));

        jPanel_Cart.add(jPanel_order, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 40, 340, 320));

        jPanel_for_payment_cancel.setBackground(new java.awt.Color(209, 225, 255));
        jPanel_for_payment_cancel.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(47, 85, 151), new java.awt.Color(47, 85, 151)));
        jPanel_for_payment_cancel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton_payment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payment_button.png"))); // NOI18N
        jButton_payment.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payment_button_selected.png"))); // NOI18N
        jButton_payment.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payment_button_entered.png"))); // NOI18N
        jButton_payment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_paymentActionPerformed(evt);
            }
        });
        jPanel_for_payment_cancel.add(jButton_payment, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 130, 80));

        jButton_cancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel_button_cart.png"))); // NOI18N
        jButton_cancel.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel_button_cart_entered.png"))); // NOI18N
        jButton_cancel.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel_button_cart_selected.png"))); // NOI18N
        jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelActionPerformed(evt);
            }
        });
        jPanel_for_payment_cancel.add(jButton_cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 130, 80));

        jPanel_Cart.add(jPanel_for_payment_cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 450, 340, 100));
        jPanel_for_payment_cancel.getAccessibleContext().setAccessibleName("jPanel_for_payment_and_cancel");

        jPanel_price.setBackground(new java.awt.Color(209, 225, 255));
        jPanel_price.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(47, 85, 151), new java.awt.Color(47, 85, 151)));
        jPanel_price.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_total_amount.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel_total_amount.setForeground(new java.awt.Color(0, 51, 255));
        jLabel_total_amount.setText("Total Amount");
        jPanel_price.add(jLabel_total_amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 7, -1, -1));

        jTextField_total.setEditable(false);
        jTextField_total.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTextField_total.setForeground(new java.awt.Color(0, 102, 102));
        jTextField_total.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_total.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));
        jPanel_price.add(jTextField_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 23, 110, 40));

        jPanel_Cart.add(jPanel_price, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 380, 160, 80));

        jPanel_shop.setBackground(new java.awt.Color(209, 225, 255));
        jPanel_shop.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(47, 85, 151), new java.awt.Color(47, 85, 151)));
        jPanel_shop.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));

        jPanel_burger.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jList_burger.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jList_burger.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_burger.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_burger.setFixedCellHeight(30);
        jList_burger.setFixedCellWidth(2);
        jList_burger.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_burgerValueChanged(evt);
            }
        });
        jScrollPane_burger.setViewportView(jList_burger);

        jPanel_burger.add(jScrollPane_burger, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 310));

        jTabbedPane2.addTab("Burger", jPanel_burger);

        jPanel_topping.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jList_topping.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jList_topping.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_topping.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_topping.setFixedCellHeight(30);
        jList_topping.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_toppingValueChanged(evt);
            }
        });
        jScrollPane_topping.setViewportView(jList_topping);

        jPanel_topping.add(jScrollPane_topping, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 310));

        jTabbedPane2.addTab("Topping", jPanel_topping);

        jPanel_drink.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jList_drink.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jList_drink.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_drink.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_drink.setFixedCellHeight(30);
        jList_drink.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_drinkValueChanged(evt);
            }
        });
        jScrollPane_drink.setViewportView(jList_drink);

        jPanel_drink.add(jScrollPane_drink, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 310));

        jTabbedPane2.addTab("Drink", jPanel_drink);

        jPanel_dessert.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jList_dessert.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jList_dessert.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_dessert.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_dessert.setFixedCellHeight(30);
        jList_dessert.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_dessertValueChanged(evt);
            }
        });
        jScrollPane_dessert.setViewportView(jList_dessert);

        jPanel_dessert.add(jScrollPane_dessert, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 310));

        jTabbedPane2.addTab("Dessert", jPanel_dessert);

        jPanel_shop.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 340, 340));

        jButton_add.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton_add.setText("+");
        jButton_add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_addMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_addMouseExited(evt);
            }
        });
        jButton_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_addActionPerformed(evt);
            }
        });
        jPanel_shop.add(jButton_add, new org.netbeans.lib.awtextra.AbsoluteConstraints(382, 185, -1, 45));

        jButton_discount.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton_discount.setText("$");
        jButton_discount.setEnabled(false);
        jButton_discount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_discountActionPerformed(evt);
            }
        });
        jPanel_shop.add(jButton_discount, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 50, -1, 30));

        jLabel_qty.setText("Qty:");
        jPanel_shop.add(jLabel_qty, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 120, -1, -1));
        jPanel_shop.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 370, -1, -1));

        jLabel_descrption.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_descrption.setForeground(new java.awt.Color(0, 51, 255));
        jLabel_descrption.setText("Description");
        jPanel_shop.add(jLabel_descrption, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 375, -1, -1));

        jLabel_carte.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_carte.setForeground(new java.awt.Color(0, 51, 255));
        jLabel_carte.setText("Carte");
        jPanel_shop.add(jLabel_carte, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jTextField_price.setEditable(false);
        jTextField_price.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTextField_price.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_price.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));
        jPanel_shop.add(jTextField_price, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 290, 80, 80));

        jLabel_price.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_price.setForeground(new java.awt.Color(0, 51, 255));
        jLabel_price.setText("Price");
        jPanel_shop.add(jLabel_price, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 270, -1, -1));

        jSpinner_Qty.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jSpinner_Qty.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        jSpinner_Qty.setName(""); // NOI18N
        jSpinner_Qty.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner_QtyStateChanged(evt);
            }
        });
        jPanel_shop.add(jSpinner_Qty, new org.netbeans.lib.awtextra.AbsoluteConstraints(379, 140, 53, 33));

        jScrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea_description.setEditable(false);
        jTextArea_description.setColumns(20);
        jTextArea_description.setFont(new java.awt.Font("Lucida Bright", 0, 14)); // NOI18N
        jTextArea_description.setRows(5);
        jTextArea_description.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 0));
        jScrollPane3.setViewportView(jTextArea_description);

        jPanel_shop.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 395, 300, 100));

        jPanel_show_item.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_show_item.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));
        jPanel_show_item.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel_show_item.add(jLabel_image, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 90, 90));

        jPanel_shop.add(jPanel_show_item, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 395, 100, 100));

        jPanel_Cart.add(jPanel_shop, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 40, 460, 510));

        jLabel_ardoise1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_back_ardoise (3).jpg"))); // NOI18N
        jLabel_ardoise1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel_Cart.add(jLabel_ardoise1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 610));

        jTabbedPane_compte.addTab("Cart", jPanel_Cart);

        jPanel_Orders.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_orderhistory.setFont(new java.awt.Font("Freestyle Script", 1, 48)); // NOI18N
        jLabel_orderhistory.setForeground(new java.awt.Color(255, 153, 0));
        jLabel_orderhistory.setText("Your order history :");
        jPanel_Orders.add(jLabel_orderhistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0), 2));

        jList_orderhistory.setBackground(new java.awt.Color(255, 255, 220));
        jList_orderhistory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0), 2));
        jList_orderhistory.setFont(new java.awt.Font("Lucida Bright", 0, 12)); // NOI18N
        jList_orderhistory.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_orderhistory.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_orderhistory.setFixedCellHeight(29);
        jList_orderhistory.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_orderhistoryValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList_orderhistory);

        jPanel5.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 490, 360));

        jPanel_Orders.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 490, 360));

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea_Ticket2.setEditable(false);
        jTextArea_Ticket2.setColumns(20);
        jTextArea_Ticket2.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea_Ticket2.setRows(5);
        jTextArea_Ticket2.setAutoscrolls(false);
        jTextArea_Ticket2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jScrollPane6.setViewportView(jTextArea_Ticket2);

        jPanel_Orders.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 260, 310));

        jButton_imprim5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_imprim.png"))); // NOI18N
        jButton_imprim5.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_imprim_entered.png"))); // NOI18N
        jButton_imprim5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprim5ActionPerformed(evt);
            }
        });
        jPanel_Orders.add(jButton_imprim5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 420, 40, 40));

        jButton_email.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_parmail.png"))); // NOI18N
        jButton_email.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_parmail_entered.png"))); // NOI18N
        jButton_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_emailActionPerformed(evt);
            }
        });
        jPanel_Orders.add(jButton_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 420, 40, 40));

        jLabel_receipt.setFont(new java.awt.Font("Freestyle Script", 1, 48)); // NOI18N
        jLabel_receipt.setForeground(new java.awt.Color(255, 153, 0));
        jLabel_receipt.setText("Receipt :");
        jPanel_Orders.add(jLabel_receipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, -1, -1));
        jPanel_Orders.add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 480, 650, 10));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Contact us");
        jPanel_Orders.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 485, -1, -1));

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel_Orders.add(jSeparator13, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 505, 20, 55));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("+33 6 20 94 46 90");
        jPanel_Orders.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 510, -1, -1));

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("+33 6 90 46 94 20");
        jPanel_Orders.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 540, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("burgerfactorykfc@gmail.com");
        jPanel_Orders.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 510, 160, -1));

        jLabel_ardoise2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_back_ardoise (3).jpg"))); // NOI18N
        jLabel_ardoise2.setText("jLabel4");
        jLabel_ardoise2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel_Orders.add(jLabel_ardoise2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 610));

        jTabbedPane_compte.addTab("Orders", jPanel_Orders);

        jPanel_Account.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(209, 225, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(47, 85, 151), new java.awt.Color(47, 85, 151)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 51), 2), "Your Account", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Bright", 1, 14), new java.awt.Color(51, 51, 51))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("E-mail :");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Firstname :");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Lastname :");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Adress :");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Zipcode :");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("City :");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Phone :");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, -1, -1));

        jLabel9.setFont(new java.awt.Font("Lucida Bright", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Change Password");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 270, -1, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Current :");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 305, -1, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("New :");
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 335, -1, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Confirm :");
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 365, -1, -1));

        jSeparator1.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 300, 10));

        jSeparator2.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jPanel4.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 300, 10));

        jButton_savechanges.setText("Save changes");
        jButton_savechanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_savechangesActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_savechanges, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 403, 150, 27));

        jSeparator3.setForeground(new java.awt.Color(204, 204, 204));
        jPanel4.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 270, 10));

        jSeparator4.setForeground(new java.awt.Color(204, 204, 204));
        jPanel4.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 270, 10));

        jSeparator5.setForeground(new java.awt.Color(204, 204, 204));
        jPanel4.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 270, 10));

        jSeparator6.setForeground(new java.awt.Color(204, 204, 204));
        jPanel4.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 270, 10));

        jSeparator7.setForeground(new java.awt.Color(204, 204, 204));
        jPanel4.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, 270, 10));

        jSeparator8.setForeground(new java.awt.Color(204, 204, 204));
        jPanel4.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, 270, 10));

        jSeparator9.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jPanel4.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 300, 10));

        jSeparator10.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel4.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 20, 210));

        jLabel_email.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 46, 160, 20));

        jLabel_firstname.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_firstname, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 76, 160, 20));

        jLabel_lastname.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_lastname, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 107, 160, 20));

        jLabel_adress.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_adress, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 137, 160, 20));

        jLabel_zipcode.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_zipcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 167, 160, 20));

        jLabel_city.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_city, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 197, 170, 20));

        jLabel_phone.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.add(jLabel_phone, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 227, 160, 20));

        jSeparator11.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jPanel4.add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 300, 10));

        jPassword_current.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPassword_currentActionPerformed(evt);
            }
        });
        jPanel4.add(jPassword_current, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 302, 140, -1));
        jPanel4.add(jPassword_new, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 332, 140, -1));

        jPassword_confirm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPassword_confirmKeyPressed(evt);
            }
        });
        jPanel4.add(jPassword_confirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 362, 140, -1));

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 370, 450));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/derouleburger.gif"))); // NOI18N
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 60, 470));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/derouleburger_reverse.gif"))); // NOI18N
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 50, 470));

        jPanel_Account.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 710, 490));

        jLabel_ardoise3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_back_ardoise (3).jpg"))); // NOI18N
        jLabel_ardoise3.setText("jLabel4");
        jLabel_ardoise3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel_Account.add(jLabel_ardoise3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 610));

        jTabbedPane_compte.addTab("Account", jPanel_Account);

        jPanel1.add(jTabbedPane_compte, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 860, 600));

        jLabel_ardoise.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_back_ardoise (3).jpg"))); // NOI18N
        jLabel_ardoise.setText("jLabel4");
        jLabel_ardoise.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.add(jLabel_ardoise, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 610));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 860, 630));

        jLabel_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/login_back_burger.png"))); // NOI18N
        jLabel_back.setText("jLabel1");
        getContentPane().add(jLabel_back, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButton_addMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_addMouseEntered
        // TODO add your handling code here:
        jButton_add.setForeground(Color.green);
    }//GEN-LAST:event_jButton_addMouseEntered

    private void jButton_addMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_addMouseExited
        // TODO add your handling code here:
        jButton_add.setForeground(Color.black);
    }//GEN-LAST:event_jButton_addMouseExited

    /**
     * Evenement bouton paiement
     *
     * @param evt
     */
    private void jButton_paymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_paymentActionPerformed

        try {
            if (!myCart.getMyItems().isEmpty()) {
                int dialogResult = JOptionPane.YES_NO_OPTION;
                int dialogbutton = JOptionPane.showConfirmDialog(null, "Confirm your payment?", "Burger Factory", dialogResult);

                if (dialogbutton == JOptionPane.YES_OPTION) {

                    sentOrder();
                    saveSalesRecord_item();
                    Receipt_JDialog receipt_JDialog = new Receipt_JDialog(this, true, jTextArea_Ticket.getText(), myCustomer.getEmail(), mySalesRecord.getIdSalesRecord());
                    receipt_JDialog.setVisible(true);
                    //Close the customer interface
                    this.dispose();
                    //We call the login window
                    Login_JFrame login = new Login_JFrame();
                }

            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Your cart is empty.");
        }

    }//GEN-LAST:event_jButton_paymentActionPerformed

    /**
     * Evenement bouton clear qui remove le dernier item qui a été ajouté
     *
     * @param evt
     */
    private void jButton_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_clearActionPerformed

        int dialogResult = JOptionPane.YES_NO_OPTION;
        if (!myCart.getMyItems().isEmpty()) {
            int dialogbutton = JOptionPane.showConfirmDialog(null, "Confirm if you want to clear your cart?", "Burger Factory", dialogResult);

            if (dialogbutton == JOptionPane.YES_OPTION) {
                myCart.getMyItems().clear();
                myCart.setTotal(0);
                jTextField_total.setText(df.format(myCart.getTotal()) + " " + "€");//Remet a "1" la valeur du spinner
                loadReceipt();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Your cart is already empty.");
        }

    }//GEN-LAST:event_jButton_clearActionPerformed

    /**
     * Evenement bouton Cancel
     *
     * @param evt
     */
    private void jButton_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cancelActionPerformed
        try {
            int dialogResult = JOptionPane.YES_NO_OPTION;
            int dialogbutton = JOptionPane.showConfirmDialog(null, "Confirm if you want to cancel your cart?", "Burger Factory", dialogResult);

            if (dialogbutton == JOptionPane.YES_OPTION) {
                //On sauvegarde le cart
                saveItemCart();
                //Close the employee interface
                this.dispose();
                //We call the login window
                Login_JFrame login = new Login_JFrame();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error when we push disconnetion button.");
        }
    }//GEN-LAST:event_jButton_cancelActionPerformed

    /**
     * Evenement quand on change la valeur dans le spinner On teste si il y a
     * selection dans les 4 listes On affiche le prix correspondant
     *
     * @param evt
     */
    private void jSpinner_QtyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner_QtyStateChanged

        priceToDisplay(); // Actualise le prix a afficher
    }//GEN-LAST:event_jSpinner_QtyStateChanged

    /**
     * Bouton pour ajouter un item au cart
     *
     * @param evt
     */
    private void jButton_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_addActionPerformed
//Si il y a selection dans une des quatres jLists
        //On ajoute au cart
        if (jList_burger.getSelectedIndex() != (-1)) {
            addToCart(shop_burger, jList_burger);
        } else if (jList_topping.getSelectedIndex() != (-1)) {
            addToCart(shop_topping, jList_topping);
        } else if (jList_drink.getSelectedIndex() != (-1)) {
            addToCart(shop_drink, jList_drink);
        } else if (jList_dessert.getSelectedIndex() != (-1)) {
            addToCart(shop_dessert, jList_dessert);
        } else {
            JOptionPane.showMessageDialog(null, "Please select an item to add");
        }
        // On affiche le prix total dans son emplacement
        totalToDisplay();
        jSpinner_Qty.setValue(1);//Remet a "1" la valeur du spinner
        loadReceipt();//On actualise le receipt
    }//GEN-LAST:event_jButton_addActionPerformed

    /**
     * Recoit un jTextArea et empeche l'autoscroll jusqu'en bas
     *
     * @param jtext
     */
    public void dontAutoScroll(JTextArea jtext) {

        DefaultCaret caret = (DefaultCaret) jtext.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }

    private void jButton_discountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_discountActionPerformed

    }//GEN-LAST:event_jButton_discountActionPerformed

    private void jButton_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_removeActionPerformed

        if (!myCart.getMyItems().isEmpty()) {
            myCart.getMyItems().remove(myCart.getMyItems().size() - 1);
            updateTotal();
            loadReceipt();
            totalToDisplay();
        } else {
            JOptionPane.showMessageDialog(null, "Your cart is already empty.");
        }
    }//GEN-LAST:event_jButton_removeActionPerformed

    /**
     * Evenement quand un item dessert est select on prend l'index de la
     * selection Si !=-1(-1 qd rien n'ai selectionné) on appelle la methode
     * itemSelection en envoyant la bonne ararylist et la bonne jlist, et on
     * clear la selection des 3autres list pour pas de conflits
     *
     * @param evt
     */
    private void jList_dessertValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_dessertValueChanged
        if (jList_dessert.getSelectedIndex() != -1) {

            //On appelle la fonction itemSelection pour avoir toutes les infos de l'item selectionné
            itemSelection(shop_dessert, jList_dessert);
            jList_burger.clearSelection();
            jList_topping.clearSelection();
            jList_drink.clearSelection();
        }
    }//GEN-LAST:event_jList_dessertValueChanged

    /**
     * Evenement quand un item drink est select on prend l'index de la selection
     * Si !=-1(-1 qd rien n'ai selectionné) on appelle la methode itemSelection
     * en envoyant la bonne ararylist et la bonne jlist, et on clear la
     * selection des 3autres list pour pas de conflits
     *
     * @param evt
     */
    private void jList_drinkValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_drinkValueChanged
        if (jList_drink.getSelectedIndex() != -1) {

            //On appelle la fonction itemSelection pour avoir toutes les infos de l'item selectionné
            itemSelection(shop_drink, jList_drink);
            jList_burger.clearSelection();
            jList_topping.clearSelection();
            jList_dessert.clearSelection();
        }
    }//GEN-LAST:event_jList_drinkValueChanged

    /**
     * Evenement quand un item topping est select on prend l'index de la
     * selection Si !=-1(-1 = qd rien n'ai selectionné) on appelle la methode
     * itemSelection en envoyant la bonne ararylist et la bonne jlist, et on
     * clear la selection des 3autres list pour pas de conflits
     *
     * @param evt
     */
    private void jList_toppingValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_toppingValueChanged
        //Si un item est selectionné :
        if (jList_topping.getSelectedIndex() != -1) {

            //On appelle la fonction itemSelection pour avoir toutes les infos de l'item selectionné
            itemSelection(shop_topping, jList_topping);
            //On enleve toute selection des autres listes si il y a selection dans cette liste
            jList_burger.clearSelection();
            jList_drink.clearSelection();
            jList_dessert.clearSelection();
        }
    }//GEN-LAST:event_jList_toppingValueChanged

    /**
     * Evenement quand un item burger est select on prend l'index de la
     * selection Si !=-1(-1 qd rien n'ai selectionné) on appelle la methode
     * itemSelection en envoyant la bonne ararylist et la bonne jlist, et on
     * clear la selection des 3autres list pour pas de conflits
     *
     * @param evt
     */
    private void jList_burgerValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_burgerValueChanged
        //Si un item est selectionné :
        if (jList_burger.getSelectedIndex() != -1) {
            //On appelle la fonction itemSelection pour avoir toutes les infos de l'item selectionné
            itemSelection(shop_burger, jList_burger);

            //On enleve toute selection des autres listes si il y a selection dans cette liste
            jList_topping.clearSelection();
            jList_drink.clearSelection();
            jList_dessert.clearSelection();
        }
    }//GEN-LAST:event_jList_burgerValueChanged

    private void jButton_savechangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_savechangesActionPerformed
        //J'appelle la méthode pour changer le mot de passe
        changePassword();
    }//GEN-LAST:event_jButton_savechangesActionPerformed

    private void jButton_imprim5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprim5ActionPerformed
        try {

            if (jList_orderhistory.getSelectedIndex() == -1) {
                throw new Exception();
            } else {
                jTextArea_Ticket2.print(null, null, false, null, null, false);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No order has been selected");
        }

    }//GEN-LAST:event_jButton_imprim5ActionPerformed

    private void jButton_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_emailActionPerformed
        sendEmail();
    }//GEN-LAST:event_jButton_emailActionPerformed

    private void jList_orderhistoryValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_orderhistoryValueChanged
        int iSelected = jList_orderhistory.getSelectedIndex();
        if (iSelected != -1) {
            jTextArea_Ticket2.setText(myCustomer.getMySalesRecords().get(iSelected).getReceipt());
        }
    }//GEN-LAST:event_jList_orderhistoryValueChanged

    private void jPassword_confirmKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPassword_confirmKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            changePassword();
        }
    }//GEN-LAST:event_jPassword_confirmKeyPressed

    private void jPassword_currentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPassword_currentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPassword_currentActionPerformed

    /**
     * Methode qui permet aux customers de modifier leur mot de passe avec tous
     * les blindages possible
     */
    public void changePassword() {
        try {
            //Si aucun champs n'est rempli
            if (jPassword_current.getPassword().length == 0
                    && jPassword_new.getPassword().length == 0
                    && jPassword_confirm.getPassword().length == 0) {
                //On force l'erreur et on affiche juste un message qui l'indique
                throw new Exception();
            } else {//Sinon
                try {
                    //Si au moins un des champs n'est pas rempli 
                    if (jPassword_current.getPassword().length == 0
                            || jPassword_new.getPassword().length == 0
                            || jPassword_confirm.getPassword().length == 0) {
                        //On force l'erreur
                        throw new Exception();
                    } else {//Sinon
                        try {
                            // Si le mot de passe actuel n'est pas le bon
                            if (!myCustomer.getPassword().equals(String.valueOf(jPassword_current.getPassword()))) {
                                //On force l'erreur
                                throw new Exception();
                            } else { //Sinon
                                try {
                                    //Si le nouveau mot de passe rentré n'est pas le meme que celui de la confirmation
                                    if (!String.valueOf(jPassword_new.getPassword()).equals(String.valueOf(jPassword_confirm.getPassword()))) {
                                        //On force l'erreur
                                        throw new Exception();
                                    } else {
                                        myCustomer.setPassword(String.valueOf(jPassword_new.getPassword()));
                                        custDAO.updatePassword(myCustomer);
                                        //On vide les champs
                                        jPassword_current.setText("");
                                        jPassword_new.setText("");
                                        jPassword_confirm.setText("");
                                        JOptionPane.showMessageDialog(null, "You have changed your password!");
                                    }
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, "The new password does not match with the confirm password !");
                                    //On vide les champs
                                    jPassword_current.setText("");
                                    jPassword_new.setText("");
                                    jPassword_confirm.setText("");
                                }
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Your current password is wrong !");
                            //On vide les champs
                            jPassword_current.setText("");
                            jPassword_new.setText("");
                            jPassword_confirm.setText("");
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "All three fields are required !");
                    //On vide les champs
                    jPassword_current.setText("");
                    jPassword_new.setText("");
                    jPassword_confirm.setText("");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No fields are filled in !");

        }
    }

    /**
     * Methode qui fait tous les changements (de prix description etc) lorsque
     * on selectionne un item.
     *
     * @param shopArray
     * @param shopJlist
     */
    public void itemSelection(ArrayList<Item> shopArray, JList shopJlist) {
        //Indice de la selection de la liste
        int iSelected = shopJlist.getSelectedIndex();

        //Si un item est selectionné :
        if (iSelected != -1) {
            //Si il possède un bulkPrice et un bulkQuantity
            if (shopArray.get(iSelected).getBulkQuantity() != 0 && shopArray.get(iSelected).getBulkPrice() != 0) {

                //J'active le petit icon dollar en vert pour indiquer qu'il y a possibilité d'avoir une reduction 
                jButton_discount.setEnabled(true); //Activer le bouton
                jButton_discount.setForeground(Color.green); //Set le dollar en vert

            } else {//Sinon
                //Je desactive le bouton
                jButton_discount.setEnabled(false); //On désactive le bouton
                jButton_discount.setForeground(Color.BLACK); //Set le dollar en noir

            }
            jSpinner_Qty.setValue(1);//Remet a "1" la valeur du spinner quand on change de selection
            //On setText le prix de l'item selectionné x la quantité
            double priceXqty = shopArray.get(iSelected).getPrice() * (Integer) jSpinner_Qty.getValue();
            jTextField_price.setText(df.format(priceXqty) + " €"); //On setText le prix correspondant 
            jTextArea_description.setText(shopArray.get(iSelected).getDescription()); //On setText la description de l'item
            jLabel_image.setIcon(shopArray.get(iSelected).getPicture());
        }
    }

    /**
     * Methode pour ajouter au Cart un item selectionné et ajouté par le client
     * Recoit en paramètre un des quatres shopArray et sa jList correspondante
     * Si l'item possède une reduction une certaine quantité, elle sera
     * automatiquement mise Permet de add l'item avec sa quatité dans le cart si
     * il n'existe pas encore dedans Si il existe deja, il va juste ajouté la
     * nouvelle qty à l'ancienne On set aussi le prix total du cart
     *
     * @param shopArray
     * @param shopJList
     */
    public void addToCart(ArrayList<Item> shopArray, JList shopJList) {

        int valid = 0;
        int nbrLot;
        int nbr_en_unit;
        double price;
        try {
            //Parcours de la liste des items du cart 
            for (int i = 0; i < myCart.getMyItems().size(); i++) {

                //Si l'item selectionné est deja dans le cart 
                if (myCart.getMyItems().get(i).getIdItem() == shopArray.get(shopJList.getSelectedIndex()).getIdItem()) {

                    //Nouvelle quantité calculé en faisant : ancienne quantité de l'item + nouvelle qty du spinner
                    int newQuantity = myCart.getMyItems().get(i).getQuantity() + (Integer) jSpinner_Qty.getValue();
                    //Je recupere l'item dans le cart et je lui set ensuite la quantité correspondante
                    myCart.getMyItems().get(i).setQuantity(newQuantity);
                    /**
                     * Si l'item selectionné possede un prix discount et si sa
                     * nouvelle quantité permet d'en profiter
                     */
                    if (myCart.getMyItems().get(i).getBulkPrice() > 0 && myCart.getMyItems().get(i).getBulkQuantity() > 0
                            && myCart.getMyItems().get(i).getQuantity() >= myCart.getMyItems().get(i).getBulkQuantity()) {
                        //Je fait la division de la quantité avec le nombre du bulkquantity pour avoir la partie entière
                        nbrLot = (Integer) myCart.getMyItems().get(i).getQuantity() / myCart.getMyItems().get(i).getBulkQuantity();
                        //Je fais le reste de la div euclidienne de cette meme div pour avoir le reste qui sera facturé en prix unitaire
                        nbr_en_unit = (Integer) myCart.getMyItems().get(i).getQuantity() % myCart.getMyItems().get(i).getBulkQuantity();
                        // On effectue ensuite le calcul du prix total de l'item selon sa quantité, son prix discount en lot
                        price = nbrLot * myCart.getMyItems().get(i).getBulkPrice() + nbr_en_unit * myCart.getMyItems().get(i).getPrice();
                        //On le set dans le cart
                        myCart.getMyItems().get(i).setPrixTotal(price);

                    } else {
                        //On set le prix total en faisant juste quantité x prix unitaire
                        myCart.getMyItems().get(i).setPrixTotal(myCart.getMyItems().get(i).getPrice() * myCart.getMyItems().get(i).getQuantity());
                    }

                    valid = 1;
                }
            }
            //Si nouvel item
            if (valid == 0) {

                /**
                 * J'instancie un nouvel item avec les infos de l'item selec,
                 * j'envoie aussi la valeur du spinner pour la quantité
                 */
                Item newItem = new Item(shopArray.get(shopJList.getSelectedIndex()), (Integer) jSpinner_Qty.getValue());
                //Si l'item selectionné possede un prix discount et si sa quantité permet d'en profiter
                if (newItem.getBulkPrice() > 0 && newItem.getBulkQuantity() > 0
                        && newItem.getQuantity() >= newItem.getBulkQuantity()) {

                    //Div pour avoir le nombre de lot qui profiteront de la reduction
                    nbrLot = (Integer) jSpinner_Qty.getValue() / newItem.getBulkQuantity();
                    //Reste de la div euclidienne pour avoir le reste qui va etre facturé avec le prix unitaire
                    nbr_en_unit = (Integer) jSpinner_Qty.getValue() % newItem.getBulkQuantity();
                    //Prix total
                    price = nbrLot * newItem.getBulkPrice() + nbr_en_unit * newItem.getPrice();
                    newItem.setPrixTotal(price);

                } else {//Sinon
                    //Prix total prix de l'item x la quantité
                    newItem.setPrixTotal(newItem.getPrice() * newItem.getQuantity());
                }

                //Je add dans la liste des items du cart l'item
                myCart.getMyItems().add(newItem);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to add to Cart");
        }

        updateTotal();// On met à jour le prix total
    }

    /**
     * Methode qui va set la commande dans un objet sales record qui va etre
     * ensuite envoyé a la base de donné
     */
    public void sentOrder() {
        try {
            mySalesRecord.setTotal(myCart.getTotal());
            mySalesRecord.setEmail(myCustomer.getEmail());
            mySalesRecord.setFname(myCustomer.getf_name());
            mySalesRecord.setLname(myCustomer.getl_name());
            mySalesRecord.setAdress(myCustomer.getAdress());
            mySalesRecord.setZipcode(myCustomer.getZipcode());
            mySalesRecord.setCity(myCustomer.getCity());
            mySalesRecord.setTelnb(myCustomer.getTel_nb());
            mySalesRecord.setReceipt(jTextArea_Ticket.getText());
            mySalesRecord.setListCommand(myCart.getMyItems());
            salesDAO.add(mySalesRecord);
            //Delete tous les anciens items du cart sauvegardés
            itCartDAO.deleteItemCartByCartId(myCart.getId());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to sent order");
        }

    }

    /**
     * Actualise le total en le recalculant
     */
    public void updateTotal() {

        try {
            //On set le total du cart a 0
            myCart.setTotal(0);
            //On parcours toute la liste des items dans le cart et on calcul le prix total
            for (int i = 0; i < myCart.getMyItems().size(); i++) {

                myCart.setTotal(myCart.getTotal() + myCart.getMyItems().get(i).getPrixTotal());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to update total");
        }

    }
    /**
     * Methode qui permet d'envoyer un mail (ici le reçu de la commande) 
     * a partir d'une boite d'envoie gmail
     */
    public void sendEmail (){
        
        String to_email = myCustomer.getEmail();//email receveur
        final String from_email = "burgerfactorykfc@gmail.com";//email envoyeur
        String subject = "Receipt - Order n°" + mySalesRecord.getIdSalesRecord();//Objet du mail
        String host = "smtp.gmail.com";//host

        //On put les properties 
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.transport.protocol", "smtp");

        //On instancie la session pour se connecter au compte d'envoie
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //On return le mail et le mot de passe
                return new PasswordAuthentication("burgerfactorykfc@gmail.com", "wordmotdepasse123789");
            }
        });
        //On instancie en Mimemessage en envoyant en parametre la session
        MimeMessage msg = new MimeMessage(session);
        try {
            
            msg.setFrom(new InternetAddress(from_email)); //On set l'email de l'envoyeur
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to_email)); //l'email du destinataire
            msg.setSubject(subject);//On set l'objet du mail
            msg.setText(jTextArea_Ticket2.getText());//On set le corps du mail dans ce cas le jTextarea du receipt
            Transport.send(msg);//On envoie le message
            JOptionPane.showMessageDialog(null, "An email has just been sent to you");

        } catch (MessagingException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Error to send a email");
        }
    }

    /**
     * Methode qui recoit la liste et le model correspondante au type Permet
     * d'afficher tous les items dans sa liste correspondante
     *
     * @param shopArray
     * @param listModel
     */
    @SuppressWarnings("unchecked")
    public void shopToDisplay(ArrayList<Item> shopArray, DefaultListModel listModel) {

        try {
            //On load toutes les listes pour les afficher dans les jLists et si on peut acheter
            //en bulk on afficher le prix du bulk ainsi que sa quantité
            for (Item shopArray1 : shopArray) {
                //Si pas de discount afficher juste le prix unitaire
                if (shopArray1.getBulkPrice() == 0 || shopArray1.getBulkQuantity() == 0) {
                    listModel.addElement(shopArray1.getName() + " - " + df.format(shopArray1.getPrice()) + " € ");
                    //Sinon afficher le lot et pour combien
                } else {
                    listModel.addElement(shopArray1.getName() + " - " + df.format(shopArray1.getPrice())
                            + " €  " + " (" + df.format(shopArray1.getBulkPrice()) + " € for " + shopArray1.getBulkQuantity() + ")");
                }
            }
        } catch (Exception e) {
            JOptionPane.showInternalMessageDialog(null, "Error to display shops");
        }

    }
    @SuppressWarnings("unchecked")
    public void historyToDisplay(ArrayList<SalesRecord> salesList, DefaultListModel salesModel) {
        try {
            for (SalesRecord salesList1 : salesList) {
                salesModel.addElement(salesList1.getIdSalesRecord() + " - BF®  - "
                        + df.format(salesList1.getTotal()) + "€ - " + salesList1.getDate() + " à " + salesList1.getHeure()
                        + " - " + salesList1.getAdress() + " " + salesList1.getZipcode() + " " + salesList1.getCity());
            }
        } catch (Exception e) {
            JOptionPane.showInternalMessageDialog(null, "Error to display history");
        }
    }

    /**
     * Methode pour afficher le prix total dans son textField
     */
    public void totalToDisplay() {
        jTextField_total.setText(df.format(myCart.getTotal()) + " " + "€");//Remet a "1" la valeur du spinner
    }

    /**
     * Methode qui actualise le prix a afficher On teste si il y a selection
     * dans les 4 listes Calcul du prix a affiché selon l'item selectionné ainsi
     * que la quantité On affiche le prix correspondant
     */
    public void priceToDisplay() {
        try {
            if (jList_burger.getSelectedIndex() != -1) {

                //On setText le prix de l'item selectionné x la quantité
                double priceXqty = shop_burger.get(jList_burger.getSelectedIndex()).getPrice() * (Integer) jSpinner_Qty.getValue();
                jTextField_price.setText(df.format(priceXqty) + " €"); //df.format() sert a n'avoir que 2 decimal
            } else if (jList_topping.getSelectedIndex() != -1) {

                //On setText le prix de l'item selectionné x la quantité
                double priceXqty = shop_topping.get(jList_topping.getSelectedIndex()).getPrice() * (Integer) jSpinner_Qty.getValue();
                jTextField_price.setText(df.format(priceXqty) + " €");
            } else if (jList_drink.getSelectedIndex() != -1) {

                //On setText le prix de l'item selectionné x la quantité
                double priceXqty = shop_drink.get(jList_drink.getSelectedIndex()).getPrice() * (Integer) jSpinner_Qty.getValue();
                jTextField_price.setText(df.format(priceXqty) + " €");
            } else if (jList_dessert.getSelectedIndex() != -1) {

                //On setText le prix de l'item selectionné x la quantité
                double priceXqty = shop_dessert.get(jList_dessert.getSelectedIndex()).getPrice() * (Integer) jSpinner_Qty.getValue();
                jTextField_price.setText(df.format(priceXqty) + " €");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error to display the Price");
        }

    }

    /**
     * Methode qui affiche le haut du receipt avec juste le nom du fast food
     * ainsi que la reference du receipt
     */
    public void topReceipt() {
        jTextArea_Ticket.append("          Burger Factory           \n"
                + " Ref:\t\t\t    " + mySalesRecord.getIdSalesRecord()
                + "\n ================================\n");
    }

    /**
     * Methode qui affiche la corps principal du receipt avec la liste des items
     * la quantité et le prix de chanque item Affiche un peu différente selon si
     * le client a ajouté en bulk ou en unitaire
     */
    public void itemsReceipt() {
        //Parcours de la liste du cart
        for (int i = 0; i < myCart.getMyItems().size(); i++) {
            //Si l'item a été ajouté en lot ca affiche un peu differement
            if (myCart.getMyItems().get(i).getQuantity() >= myCart.getMyItems().get(i).getBulkQuantity()
                    && myCart.getMyItems().get(i).getBulkPrice() > 0 && myCart.getMyItems().get(i).getBulkQuantity() > 0) {

                int qty = myCart.getMyItems().get(i).getQuantity();
                jTextArea_Ticket.append(myCart.getMyItems().get(i).getName() + ": "
                        + myCart.getMyItems().get(i).getPrice() + "€/u ("
                        + myCart.getMyItems().get(i).getBulkPrice() + "€ for "
                        + myCart.getMyItems().get(i).getBulkQuantity() + ")\n"
                        + "\tQty: " + Integer.toString(qty) + "\t\t" + "   "
                        + df.format(myCart.getMyItems().get(i).getPrixTotal()) + "\n\n");

            } else {
                int qty = myCart.getMyItems().get(i).getQuantity();
                jTextArea_Ticket.append(myCart.getMyItems().get(i).getName() + ": "
                        + myCart.getMyItems().get(i).getPrice() + "€/u\n"
                        + "\tQty: " + Integer.toString(qty) + "\t\t" + "   "
                        + df.format(myCart.getMyItems().get(i).getPrixTotal()) + "\n\n");
            }
        }
    }

    /**
     * Methode qui affiche le total du prix sur le receipt
     */
    public void totalReceipt() {
        jTextArea_Ticket.append(" \t\t ================\n"
                + "\t\t Total    " + df.format(myCart.getTotal()) + "€\n");
    }

    /**
     * Methode qui affiche partie basse du receipt avec date heure et coordonné
     * du fast food
     */
    public void bottomReceipt() {
        jTextArea_Ticket.append(" ================================\n"
                + "Date: " + mySalesRecord.getDate()
                + "    Time: " + mySalesRecord.getHeure() + "\n"
                + "\t  +33 1 43 99 27 42\t\t\n"
                + "     burgerfactorykfc@gmail.com  ");
    }

    /**
     * Methode qui permet de sauvegarder un cart si un customer se déconnecte
     */
    public void saveItemCart() {
        //Delete tous les anciens items du cart sauvegardés
        itCartDAO.deleteItemCartByCartId(myCart.getId());
        //On instancie a nouveau le Item_Cart pour qu'il soit reinitialisé
        myItem_cart = new ArrayList<Item_cart>();

        //Parcours tout le cart et un set les infos necessaires a la sauvegarde
        for (int i = 0; i < myCart.getMyItems().size(); i++) {

            int idItem = myCart.getMyItems().get(i).getIdItem();
            int idCart = myCart.getId();
            int quantity = myCart.getMyItems().get(i).getQuantity();

            //Liste des iditem, idCart, quantitypour pouvoir retrouver les items 
            myItem_cart.add(new Item_cart(idItem, idCart, quantity));
        }
        //On envoie les infos a la base de données
        itCartDAO.SaveItemCart(myItem_cart);

    }
    
    /**
     * Methode qui permet de sauvegarder les items du salesrecord
     */
    public void saveSalesRecord_item() {

        //Parcours tout le salesrecord et un set les infos necessaires a la sauvegarde des items
        for (int i = 0; i < mySalesRecord.getListCommand().size(); i++) {

            int idSalesItem = mySalesRecord.getIdSalesRecord();
            int idItem = mySalesRecord.getListCommand().get(i).getIdItem();
            int quantity = mySalesRecord.getListCommand().get(i).getQuantity();
            String type = mySalesRecord.getListCommand().get(i).getType();

            //Liste des idsales, idItem, quantity, type pour pouvoir retrouver les items 
            mySalesRecord_Items.add(new SalesRecord_Item(idSalesItem, idItem, quantity, type));
        }
        //On envoie les infos a la base de données
        salesItDAO.SaveItemSalesRecord(mySalesRecord_Items);
    }

    /**
     * Methode pour save le cart meme en fermant la fenetre
     */
    public void closeWithCross() {
        /**
         * Je crée un evenement windowListener pour permettre a l'utilisateur de
         * fermé la fenêtre avec la croix rouge sans reboucler la page discount
         */
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                //On sauvegarde le cart
                saveItemCart();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cart_JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cart_JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cart_JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cart_JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Cart_JFrame(myCustomer).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_add;
    private javax.swing.JButton jButton_cancel;
    private javax.swing.JButton jButton_clear;
    private javax.swing.JButton jButton_discount;
    private javax.swing.JButton jButton_email;
    private javax.swing.JButton jButton_imprim5;
    private javax.swing.JButton jButton_payment;
    private javax.swing.JButton jButton_remove;
    private javax.swing.JButton jButton_savechanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_adress;
    private javax.swing.JLabel jLabel_ardoise;
    private javax.swing.JLabel jLabel_ardoise1;
    private javax.swing.JLabel jLabel_ardoise2;
    private javax.swing.JLabel jLabel_ardoise3;
    private javax.swing.JLabel jLabel_back;
    private javax.swing.JLabel jLabel_bonjour;
    private javax.swing.JLabel jLabel_carte;
    private javax.swing.JLabel jLabel_city;
    private javax.swing.JLabel jLabel_descrption;
    private javax.swing.JLabel jLabel_email;
    private javax.swing.JLabel jLabel_firstname;
    private javax.swing.JLabel jLabel_image;
    private javax.swing.JLabel jLabel_lastname;
    private javax.swing.JLabel jLabel_logo;
    private javax.swing.JLabel jLabel_orderedburger;
    private javax.swing.JLabel jLabel_orderhistory;
    private javax.swing.JLabel jLabel_phone;
    private javax.swing.JLabel jLabel_price;
    private javax.swing.JLabel jLabel_qty;
    private javax.swing.JLabel jLabel_receipt;
    private javax.swing.JLabel jLabel_total_amount;
    private javax.swing.JLabel jLabel_zipcode;
    private javax.swing.JList jList_burger;
    private javax.swing.JList jList_dessert;
    private javax.swing.JList jList_drink;
    private javax.swing.JList jList_orderhistory;
    private javax.swing.JList jList_topping;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_Account;
    private javax.swing.JPanel jPanel_Cart;
    private javax.swing.JPanel jPanel_Orders;
    private javax.swing.JPanel jPanel_burger;
    private javax.swing.JPanel jPanel_dessert;
    private javax.swing.JPanel jPanel_drink;
    private javax.swing.JPanel jPanel_for_payment_cancel;
    private javax.swing.JPanel jPanel_order;
    private javax.swing.JPanel jPanel_price;
    private javax.swing.JPanel jPanel_shop;
    private javax.swing.JPanel jPanel_show_item;
    private javax.swing.JPanel jPanel_topping;
    private javax.swing.JPasswordField jPassword_confirm;
    private javax.swing.JPasswordField jPassword_current;
    private javax.swing.JPasswordField jPassword_new;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane_burger;
    private javax.swing.JScrollPane jScrollPane_dessert;
    private javax.swing.JScrollPane jScrollPane_drink;
    private javax.swing.JScrollPane jScrollPane_topping;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSpinner jSpinner_Qty;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane_compte;
    private javax.swing.JTextArea jTextArea_Ticket;
    private javax.swing.JTextArea jTextArea_Ticket2;
    private javax.swing.JTextArea jTextArea_description;
    private javax.swing.JTextField jTextField_price;
    private javax.swing.JTextField jTextField_total;
    // End of variables declaration//GEN-END:variables
}
