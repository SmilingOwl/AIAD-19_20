package agents;

import java.util.ArrayList;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;

public class Order extends Agent {
	String id;
	// int credits; //TODO: add later
	ArrayList<String> tasks;
	
	//constructor to initialise order
	public Order(String id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;
	}

	// class that is called when the agent starts
	public void setup() {
		System.out.print("Hello! I'm order " + id + ". My Tasks are: ");
		for (int i = 1; i <= this.tasks.size(); i++)
			System.out.print(this.tasks.get(i - 1) + "; ");
		System.out.println();

		this.send_arrived_message();
	}

	public void send_arrived_message() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		String content = "ARRIVED " + this.id + " ";
		for (int i = 0; i < this.tasks.size(); i++) {
			content += this.tasks.get(i) + " ";
		}
		msg.setContent(content);
		for (int i = 0; i < this.tasks.size(); i++) {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(this.tasks.get(i));
			template.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(this, template);
				for (int j = 0; j < result.length; j++) {
					msg.addReceiver(result[j].getName());
					this.send(msg);
				}

			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
	}

}
