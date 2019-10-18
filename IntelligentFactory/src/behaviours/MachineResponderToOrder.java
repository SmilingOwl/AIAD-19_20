package behaviours;

import agents.Machine;

import jade.core.Agent;
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
		String[] msgContent = msg.getContent().split(" ");
		System.out.println(" > " + this.parent.getId() + ": " + msg.getContent());
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.PROPOSE);
		
		reply.setContent("ACCEPT " + this.parent.getId() + " " + this.parent.getRole() + " "
				+ this.parent.getExpectedFinishTime());
		this.parent.send(reply);
		return reply;
	}
	
	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
		String[] msgContent = reject.getContent().split(" ");
		parent.deleteFromPending(msgContent[1]);
		System.out.println("> " + this.parent.getId()+ " "+ reject.getContent());	
	}
	
	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
		String[] msgContent = accept.getContent().split(" ");
		parent.deleteFromPending(msgContent[1]);
		parent.addOrdersTaken(msgContent[1]);
		
		ACLMessage reply = accept.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		
		// TODO orders function
		reply.setContent("DONE "+ this.parent.getId() + " " + 5);
		this.parent.send(reply);
		return reply;
	}
	
}
