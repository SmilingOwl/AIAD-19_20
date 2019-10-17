package behaviours;

import agents.Machine;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class ReceiveOrderArrivalMessage extends CyclicBehaviour {

	Machine parent;

	public ReceiveOrderArrivalMessage(Machine parent) {
		this.parent = parent;
	}

	@Override
	public void action() {
		ACLMessage msg = this.parent.blockingReceive();
		if (msg != null) {
			System.out.println(" > " + this.parent.getId() + ": " + msg.getContent());
			String[] msgContent = msg.getContent().split(" ");
			if (this.parent.acceptOrder(msgContent[1])) {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent("ACCEPT " + this.parent.getId() + " " + this.parent.getRole() + " "
						+ this.parent.getExpectedFinishTime());
				this.parent.send(reply);
			}
		} else {
			block();
		}

	}
}
