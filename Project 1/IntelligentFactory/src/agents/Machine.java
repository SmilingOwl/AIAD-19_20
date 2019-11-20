package agents;
import behaviours.MachineResponderDispatcher;
import utils.TimeSlot;

import jade.core.Agent;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Machine extends Agent {
	private String id;
	private int numberLies;
	private String role;
	private int averageTime;
	private double proactivity;
	private double liar; //lies if liar < 0.3
	private HashMap<String, TimeSlot> ordersTaken; //contains the initial time to perform each task that is already allocated
	private HashMap<String, TimeSlot> ordersPending;
	private DFAgentDescription dfd;
	private FileWriter fw;
	private ArrayList<TimeSlot> allocatedTime;
	private double credits;
	
	//constructor
	
	public Machine(String id, String role, int averageTime, double proactivity, double honesty) {		
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
		this.numberLies = 0;
		this.ordersTaken = new HashMap<String, TimeSlot>();
		this.ordersPending = new HashMap<String, TimeSlot>();
		this.proactivity = proactivity;
		this.liar = honesty;
		this.allocatedTime = new ArrayList<TimeSlot>();
		this.credits = 0;
	}
		
	//class that starts when the agent is created
	public void setup() {
		try {
			this.fw = new FileWriter("messages/" + this.getId() + ".txt", false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.writeFW("Machine: " + this.id + "\n Role: " + this.role + "\n Average time: " + this.averageTime + "\n\n");
		MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(
				FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP));
		this.addBehaviour(new MachineResponderDispatcher(this, template));//new MachineResponderToOrder(this, new ACLMessage(ACLMessage.CFP)));//MessageTemplate.MatchPerformative(ACLMessage.CFP)));
		this.register();
		
	}
	
	public void register() {
		ServiceDescription sd = new ServiceDescription();
		
		sd.setType(this.role);
		sd.setName(getLocalName());

		this.dfd = new DFAgentDescription();
		
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, this.dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	public int getAverageTime() {
		return averageTime;
	}
	
	public String getId() {
		return this.id;
	}
	
	public int getNumberLies() {
		return this.numberLies;
	}
	
	public String getRole() {
		return this.role;
	}
	
	public double getLiarRatio() {
		return this.liar;
	}
	
	public double getCredits() {
		return this.credits;
	}
	
	public double getProactivityRatio() {
		return this.proactivity;
	}
	
	public boolean acceptOrder(String order_id, int[] time) {
		TimeSlot t = new TimeSlot(order_id, time[0], time[1]);
		this.ordersPending.put(order_id, t);
		return true;
	}
	
	public int[] getExpectedFinishTime(int startTime, double order_credits, int expectedTimeToTake, int iter) {
		int[] expectedTime = new int[3];
		if(iter == 1) {
			Random r = new Random();
			if(r.nextInt(101) / 100.0 > this.liar) {
				expectedTimeToTake =  expectedTimeToTake - (int)((this.proactivity-0.5) * expectedTimeToTake);
				this.numberLies++;
			}
		}
		else {
			double perc = this.orderPerCreditsPercentage(order_credits);
			if(perc >= 0) {
				expectedTimeToTake -= perc * expectedTimeToTake;
				if(expectedTimeToTake < this.averageTime / 2) {
					expectedTimeToTake = this.averageTime / 2;
				}
			} else {
				expectedTimeToTake += 0.1 * perc * expectedTimeToTake;
			} 
		}
		
		int i = startTime;
		int j = startTime + expectedTimeToTake;
		expectedTime[0] = i;
		expectedTime[1] = j;
		expectedTime[2] = expectedTimeToTake;
		
		for(int n = 0; n < allocatedTime.size(); n++) {
			int value0 = allocatedTime.get(n).startTime;
			int value1 = allocatedTime.get(n).finishTime;
			if(i < value0 && j <= value0 || i >= value1 && j > value1) {
				expectedTime[0] = i;
				expectedTime[1] = j;
			} else {
				i = value1;
				j = i + expectedTimeToTake;
				n = -1;
			}
		}
		
		return expectedTime;
			
	}
	
	public void deleteFromPending(String id) {
		this.ordersPending.remove(id);
	}
	
	public void addOrdersTaken(String id, double credits) {
		this.credits += credits;
		this.ordersTaken.put(id, this.ordersPending.get(id));
		this.allocatedTime.add(this.ordersPending.get(id));
		this.deleteFromPending(id);
	}
	
	public HashMap<String, TimeSlot> getOrdersTaken() {
		return this.ordersTaken;
	}
	
	public void writeFW(String content) {
		try {
			this.fw.write(content);
			this.fw.flush();
		} catch (Exception ex) {
			try {
				this.fw = new FileWriter("messages/" + this.getId() + ".txt", false);
				this.writeFW(content);
				this.fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void finish() {
		try {
			this.fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public double orderPerCreditsPercentage(double order_credits) {
		double perc = 0.2;
		if(ordersTaken.size() != 0)
			perc *= (order_credits - (credits/ordersTaken.size())) / (credits/ordersTaken.size());
		return perc;
	}
	
	public void increase_credits(double order_credits) {
		this.credits += order_credits;
	}
}

