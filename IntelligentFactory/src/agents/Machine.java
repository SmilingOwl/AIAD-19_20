package agents;
import utils.Proposal;
import behaviours.*;

import jade.core.Agent;
import java.util.ArrayList;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

public class Machine extends Agent {
	String id;
	String role;
	long averageTime;
	ArrayList<Long> availability; //contains the initial time to perform each task that is already allocated
	private DFAgentDescription dfd;
	
	//constructor to initialise machine
	
	public Machine(String id, String role, long averageTime) {
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
	}
	
	
	//class that starts when the agent is created
	public void setup() {
		System.out.println("I'm machine " + this.id + ". My role is " + this.role + " and my average time is " + this.averageTime + ".");
		this.addBehaviour(new ReceiveOrderArrivalMessage(this));
		this.register();
	}
	
	public String getId() {
		return this.id;
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
}

