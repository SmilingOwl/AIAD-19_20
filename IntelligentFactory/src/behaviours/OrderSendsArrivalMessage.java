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
				if(result.length == 0) {
					this.parent.writeFW("\n\nRESULT: No machines to complete task " + tasks.get(i) + "\n" + 
							" Order not fulfilled.");
					this.parent.setFinished(true);
					break;
				}

			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
		if(!this.parent.getFinished()) {
			vector.add(msg);
			this.parent.writeFW(">> Sent Message: " + content + "\n");
		}
		return vector;
	}

	protected void handleAllResponses(Vector responses, Vector acceptances) {
		this.parent.writeFW("<< Received " + responses.size() + " responses: \n");

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
			this.parent.writeFW("  << '" + msg.getContent() + "'\n");

			tasksMachineIds.get(msgContent[2]).add(msgContent[1]);
			idFinishTime.put(msgContent[1], Long.parseLong(msgContent[3]));

		}

		ArrayList<String> acceptedMachines = new ArrayList<String>();
		HashMap<String, String> machinesTasks = new HashMap<String, String>();

		for (int n = 0; n < this.parent.getTasks().size(); n++) {
			ArrayList<String> machinesIds = tasksMachineIds.get(this.parent.getTasks().get(n));
			if (machinesIds.size() == 0) {
				this.parent.writeFW("\n\nRESULT: No machines to complete task " + this.parent.getTasks().get(n) + "\n" + 
						" Order not fulfilled.");
				this.parent.setFinished(true);
				break;
			}
			String id = this.parent.comparingTimes(machinesIds, idFinishTime);
			acceptedMachines.add(id);
			machinesTasks.put(this.parent.getTasks().get(n), id);
			this.parent.writeFW(">> Accepted proposal from: " + id + " for task: " + this.parent.getTasks().get(n) + "\n");
		}
		this.parent.setMachines(machinesTasks);

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			ACLMessage reply = msg.createReply();

			if (acceptedMachines.contains(msgContent[1]) && !this.parent.getFinished()) {
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
		this.parent.setFinishTime(maxValueInHashMap);
		this.parent.setMachinesFinishTime(idFinishTime);
		this.parent.writeResult();
		this.parent.setFinished(true);
	}
}
