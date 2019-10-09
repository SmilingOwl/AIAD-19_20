package agents;
import utils.Proposal;

import jade.core.Agent;
import java.util.ArrayList;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

public class Machine extends Agent {
	int id;
	String role;
	long averageTime;
	ArrayList<Long> availability; //contains the initial time to perform each task that is already allocated
	ArrayList<Proposal> proposals;
	
	//constructor to initialise machine

	private DFAgentDescription dfd;
	
	public Machine(int id, String role, long averageTime) {
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
	}
	
	
	
	//class that starts when the agent is created
	public void setup() {
		System.out.println("I'm machine " + this.id);
	}
	
	// register on yellow pages
	public void register() {
		ServiceDescription sd = new ServiceDescription();
		//sd.setType();
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

