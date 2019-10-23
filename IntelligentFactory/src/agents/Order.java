package agents;

import behaviours.*;

import java.util.ArrayList;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;

public class Order extends Agent {
	String id;
	// int credits; //TODO: add later
	ArrayList<String> tasks;
	long finishTime;
	
	//constructor to initialise order
	public Order(String id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;
	}
	
	public String getId() {
		return this.id;
	}
	
	public ArrayList<String> getTasks() {
		return this.tasks;
	}
	
	public void SetFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	// class that is called when the agent starts
	public void setup() {
		System.out.print("Hello! I'm order " + id + ". My Tasks are: ");
		for (int i = 1; i <= this.tasks.size(); i++)
			System.out.print(this.tasks.get(i - 1) + "; ");
		System.out.println();

		this.addBehaviour(new OrderSendsArrivalMessage(this, new ACLMessage(ACLMessage.CFP)));
	}

}
