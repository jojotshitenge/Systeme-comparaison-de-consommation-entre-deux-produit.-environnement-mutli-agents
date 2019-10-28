package tp.sma.ia.groupe5;

import java.io.IOException;  
import java.util.Random;  

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Consomateur extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idProduit;
	private int quantite = 0;
	private AID[] vendeurs;
	
	//Initiation de l'agent Consommateur
	public void setup() {
		
		//Message de bienvenue
		System.out.println("L'agent " + getAID().getLocalName() + " est pret");
		
		/*@Steven Cib.
		 * le produit à acheter
		 * 0 au cas où le consommateur ne veut rien acheter au moment de l'acheter
		 * 1 s'il veut acheter le premier produit
		 * 2 s'il veut acheter le second produit
		 * Cette valeur depend de la vonloté du consommateur
		 * @Steven Cib.
		 */	
		addBehaviour(new TickerBehaviour(this, 10000) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				Random rdn = new Random();
				idProduit = 0 + rdn.nextInt(3 - 0);
				
				if(idProduit > 0 && quantite <= 0) {
					
					quantite = 1 + rdn.nextInt(51 - 1);
					DFAgentDescription templete = new DFAgentDescription(); 
					ServiceDescription sd = new ServiceDescription();
					sd.setType("Vendeur");
					sd.setName("Vendeur-de-Produit");
					templete.addServices(sd);
					//Mise à jour de vendeurs du produit specifier par le consommateur
					try {
						DFAgentDescription[] allService = DFService.search(myAgent, templete);
						vendeurs  = new AID[allService.length];
						if(idProduit > 0)
							System.out.println("L'agent " + myAgent.getLocalName() +  " recherche les vendeurs du produit " + idProduit);

						for(int i = 0; i < allService.length; i++) {
							
							vendeurs[i] = allService[i].getName();
							System.out.println(vendeurs[i].getName());
						}
												
					}catch(FIPAException fe) {
						
						fe.printStackTrace();
					}
					//Aller acheter le produit
					
					myAgent.addBehaviour(new AcheterProduit());
				}
				else if(quantite > 0) {
					
					//Mise a jour de la liste de vendeurs
					DFAgentDescription templete = new DFAgentDescription(); 
					ServiceDescription sd = new ServiceDescription();
					sd.setType("Vendeur");
					sd.setName("Vendeur-de-Produit");
					templete.addServices(sd);
					//Mise à de vendeur du produit specifier par le consomateur
					try {
						DFAgentDescription[] allService = DFService.search(myAgent, templete);
						vendeurs  = new AID[allService.length];
						if(idProduit > 0)
							System.out.println("L'agent " + myAgent.getLocalName() +  " recherche les vendeurs du produit " + idProduit);

						for(int i = 0; i < allService.length; i++) {
							
							vendeurs[i] = allService[i].getName();
							System.out.println(i + " : " + vendeurs[i].getName());
						}
												
					}catch(FIPAException fe) {
						
						fe.printStackTrace();
					}		
					block();
				}
					
			}
		});	
	}
	private class AcheterProduit extends Behaviour{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private AID marchant;
		private int qtDispo;
		private int conteur = 0;
		private int cas = 0;
		private MessageTemplate mtemplete;
		
		@Override
		public void action() {
			// TODO Auto-generated method stub
			switch(cas) {
			case 0:
				
				if(vendeurs.length >= 1) {
					
					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					
					for(int i = 0; i < vendeurs.length; i++) {
						cfp.addReceiver(vendeurs[i]);
					}
					cfp.setContent(String.valueOf(idProduit));
					cfp.setConversationId("Sourcrir-Produit");
					cfp.setReplyWith("cfp" + System.currentTimeMillis());
					myAgent.send(cfp);
					
					mtemplete = MessageTemplate.and(MessageTemplate.MatchConversationId("Sourcrir-Produit"),
							MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
					
					System.out.println("# ->" + myAgent.getAID().getLocalName() + " recherche le vendeur du " + idProduit + " produit");
					
					cas = 1;
				}
				else {
					
					cas = 0;
				}
				break;
			case 1:
				
				ACLMessage reply = myAgent.receive(mtemplete);
				
				if(reply != null) {
					if(reply.getPerformative() == ACLMessage.INFORM) {
						
						int qtDisponible = Integer.parseInt(reply.getContent());
						
						if(marchant == null || qtDisponible > qtDispo) {
							
							qtDispo = qtDisponible;
							marchant = reply.getSender();
						}
					}
					conteur++;
					if(conteur >= vendeurs.length) {
						//On achete toute la quantite de ce vendeur puis on relance la commande à un autre vendeur
						if(qtDispo >= quantite) {
							
							int[] detail = new int[2];
							detail[0] = quantite;
							detail[1] = idProduit;
							
							ACLMessage commande = new ACLMessage(ACLMessage.CONFIRM);
							commande.addReceiver(marchant);
							try {
								commande.setContentObject(detail);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							commande.setConversationId("Sourcrir-Produit");
							commande.setReplyWith("cfp" + System.currentTimeMillis());
							myAgent.send(commande);
							
							mtemplete = MessageTemplate.and(MessageTemplate.MatchConversationId("Sourcrir-Produit"),
									MessageTemplate.MatchInReplyTo(commande.getReplyWith()));
							
							quantite = 0;
							
							System.out.println(quantite);
							
							cas = 2;
						}
						else {
							
							int qt = qtDispo;
							
							ACLMessage commande = new ACLMessage(ACLMessage.CONFIRM);
							commande.addReceiver(marchant);
							commande.setContent(String.valueOf(quantite));
							commande.setConversationId("Sourcrir-Produit");
							commande.setReplyWith("cfp" + System.currentTimeMillis());
							myAgent.send(commande);
							mtemplete = MessageTemplate.and(MessageTemplate.MatchConversationId("Sourcrir-Produit"),
									MessageTemplate.MatchInReplyTo(commande.getReplyWith()));
							
							quantite = quantite - qt;
							
							cas = 0;
						}
					}
				}
				else {
					block();
				}
				break;
			case 2:
				if(quantite >= 1) {
					
					cas = 0;
				}
				else {
					cas = 3;
				}
				break;
			}	
		}
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if((cas == 2) && quantite == 0)
				System.out.println(myAgent.getName() + "Achat effectuer avec succes");
			
			return (cas == 3) && (quantite == 0);
		}
	}
}
