package tp.sma.ia.groupe5;

import java.io.IOException; 

import javax.swing.JOptionPane;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import tp.sma.ia.groupe5.gui.MarchantForm;

public class MarchantProduit extends Agent{
	
	private int qtStock = -1 ;
	
	public String produit1;
	public String produit2;
	public String uniteMesure;
	public int qtInitiale;
	
	
	private AID jury;
	private MarchantForm myForm;
	private MessageTemplate mtemplete;
	private MarchantProduit parent;
	private MarchantModele modeleMarchant;
	
	public void setup() {
		
		parent = this;
		modeleMarchant = new MarchantModele();
		
		DFAgentDescription templete = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Comparateur-Produit");
		templete.addServices(sd);
		try {
			DFAgentDescription [] result = DFService.search(this, templete);
			
			if(result.length >= 1) {
				
				jury = result[0].getName();
			}
			else {
				
				//Si aucune comparaison produit n'est disponible impossible de creer l'agent vendeur
				
				System.out.println("# ->" + getAID().getName());
				System.out.println("------------------------------------");
				System.out.println("Aucun sercice de comparateur de consomation de produit n'est disponible");
			    JOptionPane.showMessageDialog(null, "Aucun service de comparateur de consomation de produit n'est disponible"); 
			    
			    doDelete();
			}
			
		}catch(FIPAException fe) {
			
		}
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd0 = new ServiceDescription();
		sd0.setType("Vendeur");
		sd0.setName("Vendeur-de-Produit");
		dfd.addServices(sd0);
		try {
			
			DFService.register(this, dfd);
			
		}catch(FIPAException fe) {
			
			fe.printStackTrace();
		}
		addBehaviour(new TickerBehaviour(this, 2000) {

			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				if(qtStock == 0) {
					
					doDelete();
				}
			}
			
		});
		addBehaviour(new DemandeProduitAcompare(this));
		
		addBehaviour(new VenteProduit());
	}
	protected void takeDown() {
		
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myForm.dispose();
		
		System.out.println("Le vendeur de Produit " + getAID().getName() + " n'est plus disponible");
	}
	public void setModeleProduit(MarchantModele model) {
		
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// TODO Auto-generated method stub
				modeleMarchant.setNomproduit1(model.getNomproduit1());
				modeleMarchant.setNomproduit2(model.getNomproduit2());
				modeleMarchant.setidProduit1(model.getidProduit1());
				modeleMarchant.setidProduit2(model.getidProduit2());
				
				setNouveauVendeur(model.getidProduit1(),model.getidProduit2());
			}
		});
	}
	public void setNouveauVendeur(int idP1, int idP2) {
		
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// TODO Auto-generated method stub
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(jury);
				msg.setConversationId("Nouveau-vendeur");
				msg.setContent(idP1 + " " + idP2);
				myAgent.send(msg);
			}
			
		});
	}
	private class DemandeProduitAcompare extends Behaviour{

		private int step = 0;
		private MessageTemplate mt;
		private MarchantProduit parent;
		
		public DemandeProduitAcompare(MarchantProduit machantProduit) {
			// TODO Auto-generated constructor stub
			this.parent = machantProduit;
		}
		@Override
		public void action() {
			// TODO Auto-generated method stub
			switch(step) {
			case 0:
				
				ACLMessage  inform = new ACLMessage(ACLMessage.CFP);
				inform.addReceiver(jury);
				inform.setContent("LISTE-PRODUIT-A-COMPARER");
				inform.setConversationId("liste");
				inform.setReplyWith("cfp"+System.currentTimeMillis());
				myAgent.send(inform);
					
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("liste"),
							MessageTemplate.MatchInReplyTo(inform.getReplyWith()));
				step = 1;
				break;
			case 1:
				
				ACLMessage reply = myAgent.receive(mt);
				String[] detail =  null;
				
				if(reply != null) {	
					
					if(reply.getPerformative() == ACLMessage.PROPOSE) {
						
						try {
							detail = (String[]) reply.getContentObject();
							produit1 = detail[0];
							produit2 = detail[1];
							uniteMesure = detail[2];
							qtInitiale = Integer.parseInt(detail[3]);
							qtStock = qtInitiale;
							
							
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						myForm = new MarchantForm(parent);
						
						step = 2;
					}
					else {
						
						String reponse = reply.getContent();
						System.out.println(reponse);
						step = 0;
					}
				}
				else {
					
					block();
				}
			}
		}
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			
			
			return (step == 2);
		}	
	}
	private class VenteProduit extends CyclicBehaviour{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),
					MessageTemplate.MatchConversationId("Sourcrir-Produit"));
			
			ACLMessage msg = myAgent.receive(mt);
			
			if(msg != null){
				
				int idProduit = Integer.parseInt(msg.getContent());
				ACLMessage msg0 = msg.createReply();
				
				if(modeleMarchant.getidProduit1() == idProduit)
				{
					msg0.setPerformative(ACLMessage.INFORM);
					msg0.setContent(String.valueOf(qtStock));
				}
				else if(modeleMarchant.getidProduit2() == idProduit) {
					
					msg0.setPerformative(ACLMessage.INFORM);
					msg0.setContent(String.valueOf(qtStock));
				}
				else {
					msg0.setPerformative(ACLMessage.INFORM);
					msg0.setContent(String.valueOf(qtStock));
				}
				myAgent.send(msg0);
			}
			MessageTemplate mt0 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("Sourcrir-Produit"));
			
			ACLMessage msg1 = myAgent.receive(mt0);
			
			if(msg1 != null) {
			
				int[] commande;
				int qtRechercher = 0;
				int idProduit = 0;
				try {
					commande = (int[]) msg1.getContentObject();
					qtRechercher = commande[0];
					idProduit = commande[1];
					
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ACLMessage reply0 = msg1.createReply();
				
				if(qtStock >= qtRechercher) {
					
					reply0.setPerformative(ACLMessage.AGREE);
					reply0.setContent("Merci et a plus !");
					
					qtStock = qtStock - qtRechercher;
				}
				else {
					reply0.setPerformative(ACLMessage.AGREE);
					reply0.setContent("Merci et a plus !");
					
					qtStock = 0;
				}
				myAgent.send(reply0);
				
				System.out.println(myAgent.getName() + " :");
				System.out.println("=================================");
				System.out.println("# -> " + msg1.getSender().getName() + " vient d'acheter " + qtRechercher + " " + uniteMesure + " du produit " + idProduit);
				System.out.println("Quantite en stock :" + qtStock + " " + uniteMesure);
				
				ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM_REF);
				
				msg2.addReceiver(jury);
				msg2.setConversationId("Achat-Produit");
				msg2.setContent(idProduit + " " + qtRechercher + " " + uniteMesure + " " + msg1.getSender().getName());
				myAgent.send(msg2);
			}
			MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF),
					MessageTemplate.MatchConversationId("Compar-Finish"));
			
			ACLMessage msg2 = myAgent.receive(mt1);
			
			if(msg2 != null) {
				
				doDelete();
			}
		}
	}
}
