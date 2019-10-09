package agents;
import utils.Proposal;

import jade.core.Agent;
import java.util.ArrayList;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

public class Machine extends Agent {
	String id;
	String role;
	long average_time;
	ArrayList<Long> availability; //contains the initial time to perform each task that is already allocated
	ArrayList<Proposal> proposals;
	
	//constructor to initialize machine

	private DFAgentDescription dfd;
	
	//constructor to initialise machine
	public Machine() {
		
		
	}
	
	//class that starts when the agent is created
	public void setup() {
		
		register();
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

