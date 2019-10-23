package agents;
import utils.Proposal;
import behaviours.*;

import jade.core.Agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
	HashMap<String, Long> ordersDone;
	long latestFinishTime;
	private DFAgentDescription dfd;
	
	//constructor to initialise machine
	
	public Machine(String id, String role, long averageTime) {
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
		this.ordersTaken = new ArrayList<String>();
		this.ordersPending = new ArrayList<String>();
		this.ordersDone = new HashMap<String, Long>();
		this.latestFinishTime = 0;
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
		return this.latestFinishTime + (this.ordersTaken.size()+1) * this.averageTime;
	}
	
	public long doOrder(String id) {
		long finishTime = this.latestFinishTime;
		if(!this.ordersTaken.contains(id)) {
			return -1;
		}
		Random random = new Random();
		long noise = random.nextInt((int) averageTime) - averageTime / 2;
		finishTime += averageTime + noise;
		this.ordersDone.put(id, finishTime);
		this.ordersTaken.remove(this.ordersTaken.indexOf(id));
		this.latestFinishTime = finishTime;
		return finishTime;
	}
	
	public void deleteFromPending(String id) {
		this.ordersPending.remove(id);
	}
	
	public void addOrdersTaken(String id) {
		this.ordersTaken.add(id);
	}
}

