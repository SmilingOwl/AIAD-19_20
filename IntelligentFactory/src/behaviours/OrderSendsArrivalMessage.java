package behaviours;

import agents.Order;

import java.util.Vector;
import java.util.ArrayList;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

public class OrderSendsArrivalMessage extends ContractNetInitiator {
	Order parent;

	public OrderSendsArrivalMessage(Order parent, ACLMessage cfp) {
		super(parent, cfp);
		this.parent = parent;
	}

	protected Vector<ACLMessage> prepareCfps(ACLMessage msg) {
		Vector<ACLMessage> vector = new Vector<ACLMessage>();
		String content = "ARRIVED " + this.parent.getId() + " ";
		ArrayList<String> tasks = this.parent.getTasks();
		for (int i = 0; i < tasks.size(); i++) {
			content += tasks.get(i) + " ";
		}
		msg.setContent(content);
		
		for (int i = 0; i < tasks.size(); i++) {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(tasks.get(i));
			template.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(this.parent, template);
				for (int j = 0; j < result.length; j++) {
					msg.addReceiver(result[j].getName());
				}

			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
		vector.add(msg);		
		return vector;				
	}
}
