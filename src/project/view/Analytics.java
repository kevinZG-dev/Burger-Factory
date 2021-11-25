/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import project.model.dao.ItemDAOimpl;
import project.model.dao.SalesRecordDAOimpl;
import project.model.dao.SalesRecord_ItemDAOimpl;
import project.model.entity.Item;
import project.model.entity.SalesRecord_Item;

/**
 * Classe Analytics qui herite de la classe JFrame et qui 
 * permet l'affichage des statistiques
 *
 * @author Kévin
 */
public class Analytics extends JFrame {

    //Permet la connection a la base de donné
    private final static SalesRecord_ItemDAOimpl salesItDAO = new SalesRecord_ItemDAOimpl();
    private final static ItemDAOimpl itDAO = new ItemDAOimpl();
    private final static SalesRecordDAOimpl salesDAO = new SalesRecordDAOimpl();
    //Liste des salesRecords_item recupérer dans la base
    private ArrayList<SalesRecord_Item> mySalesRecord_Item;
    //Liste des Items en fonction du type
    private ArrayList<Item> myItemSells_burger;
    private ArrayList<Item> myItemSells_topping;
    private ArrayList<Item> myItemSells_drink;
    private ArrayList<Item> myItemSells_dessert;
    //Element swing pour la stat nombre tot de commande
    private JLabel jLabel_Nombretotal;
    private JLabel jLabel_fondbig;
    private JLabel jLabel_total;
    private JPanel jPanel_nombre_total;
    //Element swing pour la stat panier moyen d'un client
    private JLabel jLabel_AverageCart;
    private JLabel jLabel_fondbin;
    private JLabel jLabel_title;
    private JPanel jPanel_average_cart;
    //format 2 decimal max
    private DecimalFormat df;
    //4 camemberts 
    private PieDataset data_burger;
    private PieDataset data_topping;
    private PieDataset data_drink;
    private PieDataset data_dessert;
    //Declare les 4 jfreechart en envoyant les data precedents correspondant et 1 de fond
    private JFreeChart chart_back;
    private JFreeChart chart_burger;
    private JFreeChart chart_topping;
    private JFreeChart chart_drink;
    private JFreeChart chart_dessert;
    //Declare les chartpanel pour les afficher en envoyant les jfreechart
    private ChartPanel panel_back;
    private ChartPanel panel_burger;
    private ChartPanel panel_topping;
    private ChartPanel panel_drink;
    private ChartPanel panel_dessert;

    private static Timer timer;

    /**
     * Constructeur
     * Creates new form Analytics_JDialog
     */
    public Analytics() {
        initComponents();
        df = new DecimalFormat("0.00");//2 decimal format
        initChiffres();//init la stat des chiffres
        initAllSalesRecord();//Init la list des salesrecord
        initCamembert();//Init les camembert ainsi que les datas
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Timer action listener qui refresh les datas toutes les secondes
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                refreshData();
            }
        });
        timer.start();//lance le timer
        //Ajout window listener qui stop le timer lorsquel'on ferme la page
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                timer.stop();
            }
        });
    }

    /**
     * Methode qui initilialise les panels, labels statistique en chiffre c'est
     * a dire celui qui affiche le nombre de commande total et celui qui affiche
     * la moyenne du prix de chaque commande
     */
    public void initChiffres() {
        /**
         * POUR LA STAT NOMBRE TOTAL DE COMMANDE
         */
        //Intancie les quatres element graphique pour la stat
        //nombre total de commande
        jPanel_nombre_total = new JPanel();
        jLabel_total = new JLabel();
        jLabel_Nombretotal = new JLabel();
        jLabel_fondbig = new JLabel();

        //Set la couleur, les bords, et le layout du panel nombre total
        jPanel_nombre_total.setBackground(new Color(255, 255, 255));
        jPanel_nombre_total.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 10));
        jPanel_nombre_total.setLayout(new AbsoluteLayout());

        //Set la police, la couleur, et le texte du titre
        jLabel_total.setFont(new Font("Tahoma", 1, 18));
        jLabel_total.setForeground(new Color(255, 255, 255));
        jLabel_total.setText("The Total Nomber of Orders");
        //add dans le panel
        jPanel_nombre_total.add(jLabel_total, new AbsoluteConstraints(100, 50, -1, -1));

        //Set la police, la couleur, l'alignement, le texte, et lesbords 
        //du label qui affiche le total de commandes
        jLabel_Nombretotal.setFont(new Font("Lucida Bright", 1, 36));
        jLabel_Nombretotal.setForeground(new Color(255, 255, 255));
        jLabel_Nombretotal.setHorizontalAlignment(SwingConstants.CENTER);
        //Recupere le nombre de commande dans la base de donnée
        jLabel_Nombretotal.setText(Integer.toString(salesDAO.countTotalOrder()));//On la set dans l'emplacement voulu
        jLabel_Nombretotal.setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153), 5));
        jPanel_nombre_total.add(jLabel_Nombretotal, new AbsoluteConstraints(130, 130, 190, 70));

        //On set dans le label prevu, le fond bigdata et on add dans le panel
        jLabel_fondbig.setIcon(new ImageIcon(getClass().getResource("/images/bigdata.gif")));
        jPanel_nombre_total.add(jLabel_fondbig, new AbsoluteConstraints(10, 22, 430, -1));

        /**
         * POUR LA STAT PANIER MOYEN D'UN CLIENT
         */
        //
        //Intancie les quatres element graphique pour la stat
        //panier moyen des clients
        jPanel_average_cart = new JPanel();
        jLabel_title = new JLabel();
        jLabel_AverageCart = new JLabel();
        jLabel_fondbin = new JLabel();

        //Set la couleur, les bords, et le layout du panel nombre total
        jPanel_average_cart.setBackground(new Color(255, 255, 255));
        jPanel_average_cart.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 10));
        jPanel_average_cart.setLayout(new AbsoluteLayout());

        //Set la police, la couleur, et le texte du titre
        jLabel_title.setFont(new Font("Tahoma", 1, 18));
        jLabel_title.setForeground(new Color(255, 255, 255));
        jLabel_title.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel_title.setText("The Average Shopping Cart");
        //add dans le panel
        jPanel_average_cart.add(jLabel_title, new AbsoluteConstraints(100, 50, -1, -1));

        //Set la police, la couleur, l'alignement, le texte, et lesbords 
        //du label qui affiche le total de commandes
        jLabel_AverageCart.setFont(new Font("Lucida Bright", 1, 36));
        jLabel_AverageCart.setForeground(new Color(255, 255, 255));
        jLabel_AverageCart.setHorizontalAlignment(SwingConstants.CENTER);
        //Recupere le nombre de commande dans la base de donnée
        String total = calculMoyenne();
        jLabel_AverageCart.setText(total + "€ / Order");//On la set dans l'emplacement voulu
        jLabel_AverageCart.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 0), 5));
        jPanel_average_cart.add(jLabel_AverageCart, new AbsoluteConstraints(80, 130, 300, 70));

        //On set dans le label prevu, le fond bigdata et on add dans le panel
        jLabel_fondbin.setIcon(new ImageIcon(getClass().getResource("/images/binaire.gif")));
        jPanel_average_cart.add(jLabel_fondbin, new AbsoluteConstraints(10, 22, 430, -1));

    }

    //Methode qui calcul la moyenne du panier d'un client durant une commande
    public String calculMoyenne() {
        ArrayList<Double> listTotal = salesDAO.selectListTotal();
        double total = 0;
        for (Double listTotal1 : listTotal) {
            total += listTotal1;
        }
        total = total / listTotal.size();
        String tot = df.format(total);
        return tot;
    }

    /**
     * Methode qui initialise les listes des items
     */
    public void initAllSalesRecord() {

        myItemSells_burger = new ArrayList<Item>();
        myItemSells_topping = new ArrayList<Item>();
        myItemSells_drink = new ArrayList<Item>();
        myItemSells_dessert = new ArrayList<Item>();

        initSalesRecordItem("burger", myItemSells_burger);
        initSalesRecordItem("topping", myItemSells_topping);
        initSalesRecordItem("drink", myItemSells_drink);
        initSalesRecordItem("dessert", myItemSells_dessert);
    }

    /**
     * Méthode qui recoit en param le type et la liste selon le type qui
     * recupere dans la base de donnée les items vendu
     *
     * @param type
     * @param myItemSells
     */
    public void initSalesRecordItem(String type, ArrayList<Item> myItemSells) {
        /**
         * variable de validation : 1 si item a déja été vendu une fois 0 sinon
         */
        int valid;

        //Recupere la liste des salesrecord_item dans la bdd selon le type
        mySalesRecord_Item = salesItDAO.selectSalesRecord_ItemByType(type);

        // Si la liste precedente n'est pas vide
        if (!mySalesRecord_Item.isEmpty()) {

            //Si la liste d'item est vide (1 fois)
            if (myItemSells.isEmpty()) {
                // variale qui recupere l'id du 1er item dans la liste salesrecordItem
                int indice = mySalesRecord_Item.get(0).getItem_idItem();
                //On add en recuperant l'item dans la bdd avec l'id
                myItemSells.add(itDAO.find(indice,false));
            }
            //On parcours la liste des salesrecordItem 
            for (SalesRecord_Item mySalesRecord_Item1 : mySalesRecord_Item) {
                //varaible de validation remis a 0
                valid = 0;
                //Parcours liste des items
                for (Item myItemSell : myItemSells) {
                    //Si id item = celui du parcours de la liste des salesrecordItem
                    if (mySalesRecord_Item1.getItem_idItem() == myItemSell.getIdItem()) {
                        //Alors j'ajoute juste la quantité a l'ancienne
                        myItemSell.setSellsquantity(myItemSell.getSellsquantity() + mySalesRecord_Item1.getQuantity());
                        valid = 1;//validation a 1
                    }
                }
                //Si validation = 0
                if (valid == 0) {
                    //on add en temps que nv item
                    int indice1 = mySalesRecord_Item1.getItem_idItem();
                    myItemSells.add(itDAO.find(indice1,false));
                    //On set sa quantité
                    myItemSells.get(myItemSells.size() - 1).setSellsquantity(mySalesRecord_Item1.getQuantity());
                }
            }
        }
    }

    /**
     * Methode qui initialise les camemberts
     */
    public void initCamembert() {

        //Instancie les 4 piedataset en apelant la methode recup data 
        //ou en envoie la liste des items selon son type
        data_burger = recupData(myItemSells_burger);
        data_topping = recupData(myItemSells_topping);
        data_drink = recupData(myItemSells_drink);
        data_dessert = recupData(myItemSells_dessert);

        //Intancie les 4 jfreechart en envoyant les data precedents correspondant et 1 de fond
        chart_back = createChart(null, "");
        chart_burger = createChart(data_burger, "Burger best sells");
        chart_topping = createChart(data_topping, "Topping best sells");
        chart_drink = createChart(data_drink, "Drink best sells");
        chart_dessert = createChart(data_dessert, "Dessert best sells");

        //Instancie les chartpanel pour les afficher en envoyant les jfreechart
        panel_back = new ChartPanel(chart_back);
        panel_burger = new ChartPanel(chart_burger);
        panel_topping = new ChartPanel(chart_topping);
        panel_drink = new ChartPanel(chart_drink);
        panel_dessert = new ChartPanel(chart_dessert);

        //On set les dimensions de chaque panel affiché
        panel_burger.setPreferredSize(new Dimension(450, 330));
        panel_topping.setPreferredSize(new Dimension(450, 330));
        panel_drink.setPreferredSize(new Dimension(450, 330));
        panel_dessert.setPreferredSize(new Dimension(450, 330));

        //On set le fond
        setContentPane(panel_back);
        //On y ajoute les 4 camembert selon une disposition adequat ainsi que les deux stats chiffrées
        getContentPane().add(panel_burger);
        getContentPane().add(panel_topping);
        getContentPane().add(jPanel_nombre_total);
        getContentPane().add(jPanel_average_cart);
        getContentPane().add(panel_drink);
        getContentPane().add(panel_dessert);

    }

    /**
     * Methode qui refresh les data des graphes et des stat chiffrés
     */
    public void refreshData() {

        myItemSells_burger.clear();
        myItemSells_topping.clear();
        myItemSells_drink.clear();
        myItemSells_dessert.clear();

        initSalesRecordItem("burger", myItemSells_burger);
        initSalesRecordItem("topping", myItemSells_topping);
        initSalesRecordItem("drink", myItemSells_drink);
        initSalesRecordItem("dessert", myItemSells_dessert);

        //Instancie les 4 piedataset en apelant la methode recup data 
        //ou en envoie la liste des items selon son type
        data_burger = recupData(myItemSells_burger);
        data_topping = recupData(myItemSells_topping);
        data_drink = recupData(myItemSells_drink);
        data_dessert = recupData(myItemSells_dessert);

        //Intancie les 4 jfreechart en envoyant les data precedents correspondant et 1 de fond
        //chart_back = createChart(null, "");
        chart_burger = createChart(data_burger, "Burger best sells");
        chart_topping = createChart(data_topping, "Topping best sells");
        chart_drink = createChart(data_drink, "Drink best sells");
        chart_dessert = createChart(data_dessert, "Dessert best sells");

        //Instancie les chartpanel pour les afficher en envoyant les jfreechart
        //panel_back = new ChartPanel(chart_back);
        panel_burger.setChart(chart_burger);
        panel_topping.setChart(chart_topping);
        panel_drink.setChart(chart_drink);
        panel_dessert.setChart(chart_dessert);

        jLabel_Nombretotal.setText(Integer.toString(salesDAO.countTotalOrder()));//On la set dans l'emplacement voulu
        String total = calculMoyenne();
        jLabel_AverageCart.setText(total + "€ / Order");//On la set dans l'emplacement voulu

        //On set le fond
        //setContentPane(panel_back);
        //On y ajoute les 4 camembert selon une disposition adequat ainsi que les deux stats chiffrées
        getContentPane().add(panel_burger);
        getContentPane().add(panel_topping);
        getContentPane().add(jPanel_nombre_total);
        getContentPane().add(jPanel_average_cart);
        getContentPane().add(panel_drink);
        getContentPane().add(panel_dessert);
    }

    /**
     * Methode qui recupere les datas voulus
     *
     * @param myItemSells
     * @return
     */
    public PieDataset recupData(ArrayList<Item> myItemSells) {
        DefaultPieDataset result = new DefaultPieDataset();

        //Parcours 
        for (Item myItemSell : myItemSells) {
            //On set les valeurs de result  
            result.setValue(myItemSell.getName(), myItemSell.getSellsquantity());
        }
        //On return le resultat
        return result;
    }

    /**
     * Methode qui crée les charts et les affiches
     *
     * @param data
     * @param title
     *
     * @return chart
     */
    public JFreeChart createChart(PieDataset data, String title) {
        //Affiche les camembert selon la data et le titre
        JFreeChart chart = ChartFactory.createPieChart3D(title, data, true, true, false);
        //affiche en 3d
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(0);//On set start angle a 0    
        plot.setDirection(Rotation.CLOCKWISE);// On set direction 
        plot.setForegroundAlpha(0.5f);
        //on return le chart
        return chart;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel_burger_best_sells = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel_burger_best_sells.setBackground(new java.awt.Color(204, 204, 204));
        jPanel_burger_best_sells.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 810, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 530, Short.MAX_VALUE)
        );

        jPanel_burger_best_sells.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 810, 530));

        getContentPane().add(jPanel_burger_best_sells, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1510, 740));

        pack();
    }// </editor-fold>                        

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
            java.util.logging.Logger.getLogger(Analytics.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Analytics.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Analytics.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Analytics.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Analytics dialog = new Analytics();

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        timer.stop();
                        System.exit(0);

                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_burger_best_sells;
    // End of variables declaration                   
}
