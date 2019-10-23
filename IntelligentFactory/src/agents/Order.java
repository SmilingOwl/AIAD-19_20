package agents;

import behaviours.*;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
	boolean finished;
	
	//constructor to initialise order
	public Order(String id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;
		this.finished = false;
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
	
	public void SetFinished(boolean finished) {
		this.finished = finished;
	}

	// class that is called when the agent starts
	public void setup() {
		System.out.print("Hello! I'm order " + id + ". My Tasks are: ");
		for (int i = 1; i <= this.tasks.size(); i++)
			System.out.print(this.tasks.get(i - 1) + "; ");
		System.out.println();
		this.addBehaviour(new OrderSendsArrivalMessage(this, new ACLMessage(ACLMessage.CFP)));
					}
	
	public String ComparingTimes(ArrayList<String> MachineId, HashMap<String,Long> FinishTimes){
		Long min = Long.MAX_VALUE;
		String id = MachineId.get(0);
		for (int i=0; i<MachineId.size(); i++) { 
			long FinishTime = FinishTimes.get(MachineId.get(i));
			if (FinishTime < min) {
				min = FinishTime;
				id = MachineId.get(i);
			}
		}
	return id;
	}

}


