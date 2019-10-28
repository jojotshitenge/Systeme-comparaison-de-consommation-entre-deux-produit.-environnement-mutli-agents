package tp.sma.ia.groupe5;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import tp.sma.ia.groupe5.gui.JuryForm;

public class Jury extends Agent{

	private JuryForm myForm;
	private JuryModele juryModele;
	
	public IntegerProperty nbrVendeur1 = new SimpleIntegerProperty(this, "Vendeur-Produit-1", 0);
	public IntegerProperty nbrVendeur2 = new SimpleIntegerProperty(this, "Vendeur-Produit-2", 0);

	public IntegerProperty qtVendue1 = new SimpleIntegerProperty(this, "Quantite-Vendue-1", 0);
	public IntegerProperty qtVendue2 = new SimpleIntegerProperty(this, "Quqntite-Vendue-2", 0);

	public void setup() {
	
		/* @Steven Cib.
		 * Message de Bienvenue de l'agent Jury chargé de sondage
		 * @Steven Cib.
		 */
		System.out.println("\t\t\t" + getAID().getName() + " :");
		System.out.println("==============================================================================");
		System.out.println("******************************************************************************");
		System.out.println("* Mise en ligne de service de comparaison de consomation entre deux produits *");
		System.out.println("******************************************************************************");
		try {
			
			myForm = new JuryForm(this);
			myForm.qtVendue1.bind(qtVendue1);
			myForm.qtVendue2.bind(nbrVendeur2);
			
			juryModele = new JuryModele();
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Comparateur-Produit");
			sd.setName("Jade-Comparateur-de-produit");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			}catch(FIPAException fe) {
				
				fe.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur");
			e.printStackTrace();
		}
		addBehaviour(new ListeProduit());
	}
	public void modeleJury(JuryModele modelJ) {
		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// TODO Auto-generated method stub
				juryModele.setNomproduit1(modelJ.getNomproduit1());
				juryModele.setNomproduit2(modelJ.getNomproduit2());
				juryModele.setUniteMesure(modelJ.getUniteMesure());
				juryModele.setQtInitiale(modelJ.getQtInitiale());
				
			}
		});
	}public void comparTerminer() {
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// TODO Auto-generated method stub
				DFAgentDescription templete = new DFAgentDescription(); 
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Vendeur");
				sd.setName("Vendeur-de-Produit");
				templete.addServices(sd);
				
				try {
						DFAgentDescription[] allService = DFService.search(myAgent, templete);
						AID [] vendeurs  = new AID[allService.length];
					
						for(int i = 0; i < allService.length; i++) {
						
							vendeurs[i] = allService[i].getName();
						}
						if(vendeurs.length >= 1) {
							
							ACLMessage cfp = new ACLMessage(ACLMessage.INFORM_REF);
							
							for(int i = 0; i < vendeurs.length; i++) {
								cfp.addReceiver(vendeurs[i]);
							}
							cfp.setContent("Comparaison termine");
							cfp.setConversationId("Compar-Finish");
							myAgent.send(cfp);
						}					
				 }catch(FIPAException fe) {
					
					fe.printStackTrace();
				 }
			}
		});
	}
	private class ListeProduit extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mtemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),
					MessageTemplate.MatchConversationId("liste"));
			
			ACLMessage msg = myAgent.receive(mtemplate);
			
			if(msg != null) {
				
				ACLMessage reponse = msg.createReply();
				
				if(juryModele.getNomproduit1() != null) {
					
					String[] produit = new String[4];
					
					produit[0] = juryModele.getNomproduit1();
					produit[1] = juryModele.getNomproduit2();
					produit[2] = juryModele.getUniteMesure();
					produit[3] = String.valueOf(juryModele.getQtInitiale());
					
					reponse.setPerformative(ACLMessage.PROPOSE);
					try {
						reponse.setContentObject(produit);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				else {
					System.out.println("Aucune comparaison en ligne ");
					reponse.setPerformative(ACLMessage.REFUSE);
					reponse.setContent("Aucune comparaison n'est mis en ligne");
					
				}
				myAgent.send(reponse);
			}
			// Chaque fois que un membre est lancer dans le systeme
			MessageTemplate mt0 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("Nouveau-vendeur"));
			
			ACLMessage msg0 = myAgent.receive(mt0);
			
			if(msg0 != null) {
				
				String str = msg0.getContent();
				String[] strIp = str.split(" ");
				
				
				int idP1 = (Integer.parseInt(strIp[0]) > 0)? 1 : 0;
				int idP2 = (Integer.parseInt(strIp[1]) > 0)? 1 : 0;
				
				nbrVendeur1.set(nbrVendeur1.get() + idP1);
				nbrVendeur2.set(nbrVendeur2.get() + idP2);
				
				myForm.setNbrVendeur(nbrVendeur1.get(), nbrVendeur2.get());
				
				if(idP1 > 0) {
					String nouveauVendeur = "#-> Nouvel agent vendeur " + msg0.getSender().getName() + " du produit " + Integer.parseInt(strIp[0]); 
					myForm.nouveauVendeur(nouveauVendeur);
				}
				if(idP2 > 0) {
					String nouveauVendeur = "#-> Nouvel agent vendeur " + msg0.getSender().getName() + " du produit " + Integer.parseInt(strIp[1]); 
					myForm.nouveauVendeur(nouveauVendeur);
				}
			}
			// Chaque fois qu'un consomateur achete un produit
			MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF),
					MessageTemplate.MatchConversationId("Achat-Produit"));
			
			ACLMessage msg1 = myAgent.receive(mt1);
			
			if(msg1 != null) {
				/*
				 *  0 id du Produit acheter
				 *  1 Quantite achetee
				 *  2 Unite de mesure
				 *  3 Consomateur
				 */
				String[] msg1Content = msg1.getContent().split(" ");
				
				int idProduit = Integer.parseInt(msg1Content[0]);
				int qt = Integer.parseInt(msg1Content[1]);
				String uniteM = msg1Content[2];
				String acheteur = msg1Content[3];
				
				String appendMsg = "#-> Le consommateur " + acheteur + " vient d'acheter " + qt + uniteM + " du produit " + idProduit;
				if(idProduit == 1)
					qtVendue1.set(qtVendue1.get() + qt);
				else
					qtVendue2.set(qtVendue2.get() + qt);
				
				myForm.nouveauAchat(appendMsg, qtVendue1.get(), qtVendue2.get());
			}
		}
	}
}
