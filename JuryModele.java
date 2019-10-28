package tp.sma.ia.groupe5;

import javafx.beans.property.IntegerProperty; 
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class JuryModele {
	
	private final StringProperty nomProduit1;
    private final StringProperty nomProduit2;
    private final StringProperty uniteMesure;
    private final IntegerProperty qtInitiale;
    private final IntegerProperty nbrVendeur1;
    private final IntegerProperty nbrVendeur2;
    private final IntegerProperty produit1;
    private final IntegerProperty produit2;
    
    public JuryModele() {
    	
    	this(null, null, null, 0, 0, 0, 0, 0);
    }
    public JuryModele(String nomProduit1, String nomProduit2, String uniteMesure, int qtInitiale, int nbrVendeur1, int nbrVendeur2, int produit1, int produit2) {
	   
	   	this.nomProduit1 =  new SimpleStringProperty(nomProduit1);
	   	this.nomProduit2 = new SimpleStringProperty(nomProduit2);
	   	this.uniteMesure = new SimpleStringProperty(uniteMesure);
	   	this.qtInitiale = new SimpleIntegerProperty(qtInitiale);
	   	this.nbrVendeur1 = new SimpleIntegerProperty(nbrVendeur1);
	   	this.nbrVendeur2 = new SimpleIntegerProperty(nbrVendeur2);
	   	this.produit1 = new SimpleIntegerProperty(produit1);
	   	this.produit2 = new SimpleIntegerProperty(produit2);
    }
   public String getNomproduit1() {
	   
	   return nomProduit1.get();
   }
   public void setNomproduit1(String nom) {
	   
	   this.nomProduit1.set(nom);
   }
   public StringProperty nomProduit1Property() {
	   
	   return nomProduit1;
   }
   public String getNomproduit2() {
	   
	   return nomProduit2.get();
   }
   public void setNomproduit2(String nom) {
	   
	   this.nomProduit2.set(nom);
   }
   public StringProperty nomProduit2Property() {
	   
	   return nomProduit2;
   }
   public String getUniteMesure() {
	   
	   return this.uniteMesure.get();
   }
   public void setUniteMesure(String mesure) {
	   
	   this.uniteMesure.set(mesure);
   }
   public StringProperty uniteMesureProperty(){
	   
	   return uniteMesure;
   }
   public int getQtInitiale() {
	   
	   return this.qtInitiale.get();
   }
   public void setQtInitiale(int qt) {
	   
	   this.qtInitiale.set(qt);
   }
   public IntegerProperty qtInitiale() {
	   
	   return qtInitiale;
   }
   public int getnbrVendeur1() {
	   
	   return this.nbrVendeur1.get();
   }
   public void setnbrVendeur1(int nbr) {
	   
	   this.nbrVendeur1.set(nbr);
   }
   public IntegerProperty nbrVendeur1Property() {
	   
	   return nbrVendeur1;
   }
   public int getnbrVendeur2() {
	   
	   return this.nbrVendeur2.get();
   }
   public void setnbrVendeur2(int nbr) {
	   
	   this.nbrVendeur2.set(nbr);
   }
   public IntegerProperty nbrVendeur2Property() {
	   
	   return nbrVendeur2;
   }
   public int getProduit1() {
	   
	   return this.produit1.get();
   }
   public void setProduit1(int nbr) {
	   
	   this.produit2.set(nbr);
   }
   public IntegerProperty produit1Property() {
	   
	   return produit1;
   }
   public int getProduit2() {
	   
	   return this.produit1.get();
   }
   public void setProduit2(int nbr) {
	   
	   this.produit2.set(nbr);
   }
   public IntegerProperty produit2Property() {
	   
	   return produit1;
   }
}
