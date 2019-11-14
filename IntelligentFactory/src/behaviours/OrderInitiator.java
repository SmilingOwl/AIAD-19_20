package behaviours;

import agents.Order;

import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;

import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

public class OrderInitiator extends ContractNetInitiator {
	Order parent;
	String task;
	int iterations;
	double credits;

	public OrderInitiator(Order parent, ACLMessage cfp, String task) {
		super(parent, cfp);
		this.parent = parent;
		this.iterations = 0;
		this.credits = this.parent.getCreditsPerTask();
		this.task = task;
	}

	protected void handleAllResponses(Vector responses, Vector acceptances) {
		if(this.parent.getFinished())
			return;
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

		if (machineIds.size() == 0 && !this.parent.getFinished()) {
			this.parent.writeFW("\n\nRESULT: No machines to complete task " + this.task + "\n"
						+ " Order not fulfilled.");
			this.parent.setFinished(true);
		} else if(this.parent.getFinished()) {
			return;
		}
		
		boolean new_iteration = false;
		ArrayList<String> acceptedMachines = new ArrayList<String>();
		if(!this.parent.getFinished()) {
			String best_id = this.parent.comparingTimes(machineIds, idFinishTime);
			acceptedMachines.add(best_id);
			machineIds.remove(best_id);
			if(this.iterations < 3 && machineIds.size() >= 1) {
				int bestFinishTime = idFinishTime.get(best_id);
				int secondBestFinishTime = idFinishTime.get(this.parent.comparingTimes(machineIds, idFinishTime));
				if(!this.parent.isSatisfied(bestFinishTime, secondBestFinishTime)) {
					new_iteration = true;
					this.iterations++;
					this.credits = this.parent.increase_credits_iteration(this.credits, bestFinishTime, secondBestFinishTime);
					for(int n = 0; n < machineIds.size(); n++) {
						if(bestFinishTime >= idFinishTime.get(machineIds.get(n)) - bestFinishTime * 0.5) {
							if(!acceptedMachines.contains(machineIds))
								acceptedMachines.add(machineIds.get(n));
						}
					}
				}
			}
		}

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			ACLMessage reply = msg.createReply();

			if (!new_iteration && acceptedMachines.contains(msgContent[1]) && !this.parent.getFinished()) {
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent("ACCEPT " + this.parent.getId() + " " + this.credits);
				this.parent.writeFW(">> Accepted proposal from: " + msgContent[1] + " for task: " + this.task + "\n");
				this.parent.addMachine(this.task, msgContent[1]);
			} else if(new_iteration && acceptedMachines.contains(msgContent[1]) && !this.parent.getFinished()) {
				reply.setPerformative(ACLMessage.CFP);
				reply.setContent("ARRIVED " + this.parent.getId() + " " + this.parent.getFinishTime() + " " + this.credits + " " + this.task);
			} else {
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				reply.setContent("REJECT " + this.parent.getId());
			}

			acceptances.add(reply);
		}
		if(new_iteration && !this.parent.getFinished()) {
			this.parent.writeFW(">> Sent Message: ARRIVED " + this.parent.getId() + " " + this.parent.getFinishTime() + " " + this.credits + " " + this.task + "\n");
			newIteration(acceptances);
		}
	}

	protected void handleAllResultNotifications(Vector resultNotifications) {
		ACLMessage msg = (ACLMessage) resultNotifications.elementAt(0);
		if(!this.parent.getFinished())
			this.parent.writeFW("<< Received Message: " + msg.getContent() + "\n");
		else 
			return;
		String[] msgContent = msg.getContent().split(" ");
		int finishTime = Integer.parseInt(msgContent[2]);

		this.parent.setFinishTime(finishTime);
		this.parent.addMachinesFinishTime(msgContent[1], finishTime);
		this.parent.endTask();
	}
}
