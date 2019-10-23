package behaviours;

import agents.Order;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

public class OrderSendsArrivalMessage extends ContractNetInitiator {
	Order parent;
	// private static HashMap <String, ArrayList<String>> Results = null;
	// private static HashMap <String, Long> Results2 = null;

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

	protected void handleAllResponses(Vector responses, Vector acceptances) {
		System.out.println(">> " + this.parent.getId() + " received " + responses.size() + " responses: ");

		// tasks, ArrayList<ids>
		HashMap<String, ArrayList<String>> tasksMachineIds = new HashMap<String, ArrayList<String>>();

		// id -> finish time
		HashMap<String, Long> idFinishTime = new HashMap<String, Long>();

		int numberTasks = parent.getTasks().size();
		for (int i = 0; i < numberTasks; i++) {
			ArrayList<String> ids = new ArrayList<String>();
			tasksMachineIds.put(parent.getTasks().get(i), ids);
		}

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			System.out.println(">> " + msg.getContent());

			tasksMachineIds.get(msgContent[2]).add(msgContent[1]);
			idFinishTime.put(msgContent[1], Long.parseLong(msgContent[3]));

		}

		ArrayList<String> acceptedMachines = new ArrayList<String>();

		for (int n = 0; n < this.parent.getTasks().size(); n++) {
			ArrayList<String> MachinesIds = tasksMachineIds.get(this.parent.getTasks().get(n));
			if (MachinesIds.size() == 0) {
				return;
			}
			acceptedMachines.add(this.parent.ComparingTimes(MachinesIds, idFinishTime));
		}

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			ACLMessage reply = msg.createReply();

			if (acceptedMachines.contains(msgContent[1])) {
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent("ACCEPT " + this.parent.getId());
			} else {
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				reply.setContent("REJECT " + this.parent.getId());
			}

			acceptances.add(reply);
		}
	}

	protected void handleAllResultNotifications(Vector resultNotifications) {

		HashMap<String, Long> idFinishTime = new HashMap<String, Long>();

		for (int i = 0; i < resultNotifications.size(); i++) {
			ACLMessage msg = (ACLMessage) resultNotifications.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			idFinishTime.put(msgContent[1], Long.parseLong(msgContent[2]));
		}

		// obtain max value
		long maxValueInHashMap = (Collections.max(idFinishTime.values()));
		parent.SetFinishTime(maxValueInHashMap);
	}
}
