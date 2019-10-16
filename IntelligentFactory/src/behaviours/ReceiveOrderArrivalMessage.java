package behaviours;

import agents.Machine;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class ReceiveOrderArrivalMessage extends SimpleBehaviour {
	
	Machine parent;
	ACLMessage msg;

	public ReceiveOrderArrivalMessage(Machine parent) {
		this.parent = parent;
	}

	@Override
	public void action() {
		msg = this.parent.blockingReceive();
		if (msg != null) {
			System.out.println(" >>" + this.parent.getId() + ": " + msg);
		} else {
			block();
		}

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		if(msg != null)
			return true;
		return false;
	}

}
