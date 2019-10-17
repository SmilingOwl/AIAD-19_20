package behaviours;

import agents.Order;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class ReceiveMachineMessage extends CyclicBehaviour{
	
	Order parent;
	
	public ReceiveMachineMessage(Order parent) {
		this.parent = parent;
	}

	@Override
	public void action() {
		ACLMessage msg = this.parent.blockingReceive();
		System.out.println(" > " + this.parent.getId() + " received message: " + msg.getContent());
	}

}
