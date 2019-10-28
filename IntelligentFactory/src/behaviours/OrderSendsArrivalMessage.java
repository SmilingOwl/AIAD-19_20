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
	String task;

	public OrderSendsArrivalMessage(Order parent, ACLMessage cfp) {
		super(parent, cfp);
		this.parent = parent;
	}

	protected Vector<ACLMessage> prepareCfps(ACLMessage msg) {
		Vector<ACLMessage> vector = new Vector<ACLMessage>();
		if(this.parent.getFinished())
			return vector;
		String content = "ARRIVED " + this.parent.getId() + " " + this.parent.getFinishTime() + " ";
		ArrayList<String> tasks = this.parent.getTasks();
		for (int i = 0; i < tasks.size(); i++) {
			content += tasks.get(i) + " ";
		}
		msg.setContent(content);

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		this.task = tasks.get(this.parent.getMachines().size());
		sd.setType(this.task);
		template.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this.parent, template);
			for (int j = 0; j < result.length; j++) {
				msg.addReceiver(result[j].getName());
			}
			if (result.length == 0) {
				this.parent.writeFW("\n\nRESULT: No machines to complete task "
						+ this.task + ".\n" + " Order not fulfilled.");
				this.parent.setFinished(true);
			}

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		if (!this.parent.getFinished()) {
			vector.add(msg);
			this.parent.writeFW(">> Sent Message: " + content + "\n");
		}
		return vector;
	}

	protected void handleAllResponses(Vector responses, Vector acceptances) {
		this.parent.writeFW("<< Received " + responses.size() + " responses: \n");

		ArrayList<String> machineIds = new ArrayList<String>();

		// id -> finish time
		HashMap<String, Integer> idFinishTime = new HashMap<String, Integer>();

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			this.parent.writeFW("  << '" + msg.getContent() + "'\n");

			machineIds.add(msgContent[1]);
			idFinishTime.put(msgContent[1], Integer.parseInt(msgContent[3]));

		}

		String acceptedMachine = "";

		if (machineIds.size() == 0) {
			this.parent.writeFW("\n\nRESULT: No machines to complete task " + this.task + "\n"
						+ " Order not fulfilled.");
			this.parent.setFinished(true);
		}
		if(!this.parent.getFinished()) {
			acceptedMachine = this.parent.comparingTimes(machineIds, idFinishTime);
			this.parent.writeFW(">> Accepted proposal from: " + acceptedMachine + " for task: " + this.task + "\n");
			this.parent.addMachine(this.task, acceptedMachine);
		}

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			ACLMessage reply = msg.createReply();

			if (acceptedMachine.equals(msgContent[1]) && !this.parent.getFinished()) {
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
		
		ACLMessage msg = (ACLMessage) resultNotifications.elementAt(0);
		String[] msgContent = msg.getContent().split(" ");
		int finishTime = Integer.parseInt(msgContent[2]);

		this.parent.setFinishTime(finishTime);
		this.parent.addMachinesFinishTime(msgContent[1], finishTime);
		this.parent.endTask();
	}
}
