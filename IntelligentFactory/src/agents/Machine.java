package agents;
import utils.Proposal;
import behaviours.*;

import jade.core.Agent;
import java.util.ArrayList;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Machine extends Agent {
	String id;
	String role;
	long averageTime;
	ArrayList<String> ordersTaken; //contains the initial time to perform each task that is already allocated
	ArrayList<String> ordersPending;
	private DFAgentDescription dfd;
	
	//constructor to initialise machine
	
	public Machine(String id, String role, long averageTime) {
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
		this.ordersTaken = new ArrayList<String>();
		this.ordersPending = new ArrayList<String>();
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getRole() {
		return this.role;
	}
	
	//class that starts when the agent is created
	public void setup() {
		System.out.println("I'm machine " + this.id + ". My role is " + this.role + " and my average time is " + this.averageTime + ".");
		this.addBehaviour(new MachineResponderToOrder(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
		this.register();
	}
	
	// register on yellow pages TODO: test
	public void register() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(this.role);
		sd.setName(getLocalName());

		this.dfd = new DFAgentDescription();
		
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, this.dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	//for now, the machine accepts all orders
	public boolean acceptOrder(String order_id) {
		this.ordersPending.add(order_id);
		return true;
	}
	
	//we can improve this function by also taking into account the pending orders
	public long getExpectedFinishTime() {
		return (this.ordersTaken.size()+1) * this.averageTime;
	}
	
	public void doOrders() {
		
	}
}

