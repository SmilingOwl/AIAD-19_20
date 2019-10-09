package agents;

import java.util.ArrayList;

import jade.core.Agent;

public class Order extends Agent {
	int id;
	//int credits; //TODO: add later
	ArrayList<String> tasks;
	
	//constructor to initialize order
	public Order(int id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;		
	}
	
	//class that is called when the agent starts
	public void setup() {
		System.out.println("Hello! I'm order " + id);
	}

}
