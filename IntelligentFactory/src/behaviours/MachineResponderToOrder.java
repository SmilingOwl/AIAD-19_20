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
		
		reply.setContent("ACCEPT " + this.parent.getId() + " " + this.parent.getRole() + " "
				+ this.parent.getExpectedFinishTime());
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
		parent.deleteFromPending(msgContent[1]);
		parent.addOrdersTaken(msgContent[1]);
		
		ACLMessage reply = accept.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		
		reply.setContent("DONE "+ this.parent.getId() + " " + this.parent.doOrder(msgContent[1]));
		this.parent.send(reply);
		this.parent.writeFW(">> Sent Message: " + reply.getContent() + "\n");
		return reply;
	}
	
}
