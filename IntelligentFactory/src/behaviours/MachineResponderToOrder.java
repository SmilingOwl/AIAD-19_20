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
	
}
