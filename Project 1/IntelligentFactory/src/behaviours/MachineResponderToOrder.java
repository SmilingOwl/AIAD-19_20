package behaviours;

import agents.Machine;
import jade.lang.acl.ACLMessage;
import jade.proto.SSIteratedContractNetResponder;

public class MachineResponderToOrder extends SSIteratedContractNetResponder {
	Machine parent;
	double expected_credits;
	int expectedTime;
	int iter = 0;
	
	public MachineResponderToOrder(Machine parent, ACLMessage cfp) {
		super(parent, cfp);
		this.parent = parent;
		this.expectedTime = this.parent.getAverageTime();
	}

	@Override
	protected ACLMessage handleCfp(ACLMessage msg) {
		this.iter++;
		this.parent.writeFW("<< Received Message: " + msg.getContent() + "\n");
		
		String[] msgContent = msg.getContent().split(" ");
		
		if(msgContent[0].equals("ARRIVED")) {
		
			int startTime = Integer.parseInt(msgContent[2]);
			this.expected_credits = Double.parseDouble(msgContent[3]);
			
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.PROPOSE);
			
			int[] time = this.parent.getExpectedFinishTime(startTime, this.expected_credits, this.expectedTime, this.iter);
			this.expectedTime = time[2];
			
			reply.setContent("ACCEPT " + this.parent.getId() + " " + this.parent.getRole() + " " + time[1]);
			this.parent.acceptOrder(msgContent[1], time);
			this.parent.writeFW(">> Sent Message: " + reply.getContent() + "\n");
			return reply;
		} else if(msgContent[0].equals("ACCEPT")) {
			return this.handleAcceptProposal(null, null, msg);
		} else if(msgContent[0].equals("REJECT")) {
			this.handleRejectProposal(null, null, msg);
		}
		return null;
	}

	@Override
	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
		String[] msgContent = reject.getContent().split(" ");
		parent.deleteFromPending(msgContent[1]);
		this.parent.writeFW("<< Received Message: " + reject.getContent() + "\n");	
	}

	@Override
	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
		this.parent.writeFW("<< Received Message: " + accept.getContent() + "\n");	
		String[] msgContent = accept.getContent().split(" ");
		parent.addOrdersTaken(msgContent[1], Double.parseDouble(msgContent[2]));
		
		ACLMessage reply = accept.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		
		reply.setContent("ALLOCATED "+ this.parent.getId() + " " + this.parent.getOrdersTaken().get(msgContent[1]).finishTime);
		this.parent.send(reply);
		this.parent.writeFW(">> Sent Message: " + reply.getContent() + "\n");
		return reply;
	}
	
}
