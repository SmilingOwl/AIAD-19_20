package agents;
import utils.Proposal;

import jade.core.Agent;
import java.util.ArrayList;

public class Machine extends Agent {
	int id;
	String role;
	long averageTime;
	ArrayList<Long> availability; //contains the initial time to perform each task that is already allocated
	ArrayList<Proposal> proposals;
	
	//constructor to initialize machine
	public Machine(int id, String role, long averageTime) {
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
	}
	
	
	
	//class that starts when the agent is created
	public void setup() {
		System.out.println("I'm machine " + this.id);
	}
}
