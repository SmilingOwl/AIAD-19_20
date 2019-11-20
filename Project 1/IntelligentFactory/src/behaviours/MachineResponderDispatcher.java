package behaviours;

import agents.Machine;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSResponderDispatcher;

public class MachineResponderDispatcher extends SSResponderDispatcher {
	Machine parent;

	public MachineResponderDispatcher(Agent a, MessageTemplate tpl) {
		super(a, tpl);
		if(a instanceof Machine)
			this.parent = (Machine) a;
	}

	@Override
	protected Behaviour createResponder(ACLMessage arg0) {
		return new MachineResponderToOrder(this.parent, arg0);
	}

}
