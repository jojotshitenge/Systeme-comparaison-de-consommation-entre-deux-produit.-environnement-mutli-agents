package tp.sma.ia.groupe5;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MarchantModele {
	private final StringProperty nomProduit1;
    private final StringProperty nomProduit2;
    private final IntegerProperty idP1;
    private final IntegerProperty idP2;
    
    public MarchantModele() {
    	
    	this(null, null, 0, 0);
    }
    public MarchantModele(String nomProduit1, String nomProduit2,int idP1, int idP2) {
	   
	   	this.nomProduit1 =  new SimpleStringProperty(nomProduit1);
	   	this.nomProduit2 = new SimpleStringProperty(nomProduit2);
	   	this.idP1 = new SimpleIntegerProperty(idP1);
	   	this.idP2 = new SimpleIntegerProperty(idP2);
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
   public int getidProduit1() {
	   
	   return this.idP1.get();
   }
   public void setidProduit1(int id) {
	   
	   this.idP1.set(id);
   }
   public IntegerProperty idProduit1Property() {
	   
	   return idP1;
   }
   public int getidProduit2() {
	   
	   return this.idP2.get();
   }
   public void setidProduit2(int id) {
	   
	   this.idP2.set(id);
   }
   public IntegerProperty idProduit2Property() {
	   
	   return idP2;
   }
}
