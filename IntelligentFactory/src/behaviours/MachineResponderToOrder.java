package behaviours;

import agents.Machine;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class MachineResponderToOrder extends ContractNetResponder{
	Machine parent;
	
	public MachineResponderToOrder(Machine parent, MessageTemplate mt) {
		super(parent, mt);
		this.parent = parent;
	}
	
	protected ACLMessage handleCfp(ACLMessage msg) {
		this.parent.writeFW("<< Received Message: " + msg.getContent() + "\n");
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.PROPOSE);
		
		String[] msgContent = msg.getContent().split(" ");
		int startTime = Integer.parseInt(msgContent[2]);
		
		int[] time = this.parent.getExpectedFinishTime(startTime);
		
		reply.setContent("ACCEPT " + this.parent.getId() + " " + this.parent.getRole() + " "
				+ time[1]);
		this.parent.acceptOrder(msgContent[1], time);
		this.parent.send(reply);
		this.parent.writeFW(">> Sent Message: " + reply.getContent() + "\n");
		return reply;
	}
	
	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
		String[] msgContent = reject.getContent().split(" ");
		parent.deleteFromPending(msgContent[1]);
		this.parent.writeFW("<< Received Message: " + reject.getContent() + "\n");	
	}
	
	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
		this.parent.writeFW("<< Received Message: " + accept.getContent() + "\n");	
		String[] msgContent = accept.getContent().split(" ");
		parent.addOrdersTaken(msgContent[1]);
		
		ACLMessage reply = accept.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		
		reply.setContent("ALLOCATED "+ this.parent.getId() + " " + this.parent.getOrdersTaken().get(msgContent[1]).finishTime);
		this.parent.send(reply);
		this.parent.writeFW(">> Sent Message: " + reply.getContent() + "\n");
		return reply;
	}
	
}
