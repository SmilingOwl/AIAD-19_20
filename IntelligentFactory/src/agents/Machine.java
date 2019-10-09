package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

public class Machine extends Agent {

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

