package agents;

import java.util.ArrayList;

import jade.core.Agent;

public class Order extends Agent {
	String id;
	//int credits; //TODO: add later
	ArrayList<String> tasks;
	
	//constructor to initialize order
	public Order(String id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;		
	}
	
	//class that is called when the agent starts
	public void setup() {
		System.out.print("Hello! I'm order " + id + ". My Tasks are: ");
		for(int i = 1; i <= this.tasks.size(); i++)
			System.out.print(this.tasks.get(i-1) + "; ");
		System.out.println();
		
	}

}
