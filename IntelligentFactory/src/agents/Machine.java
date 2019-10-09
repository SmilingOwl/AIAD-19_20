package agents;
import utils.Proposal;

import jade.core.Agent;
import java.util.ArrayList;

public class Machine extends Agent {
	String id;
	String role;
	long average_time;
	ArrayList<Long> availability; //contains the initial time to perform each task that is already allocated
	ArrayList<Proposal> proposals;
	
	//constructor to initialize machine
	public Machine() {
		
	}
	
	//class that starts when the agent is created
	public void setup() {
		
	}
}
