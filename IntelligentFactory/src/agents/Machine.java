package agents;
import behaviours.MachineResponderToOrder;
import utils.TimeSlot;

import jade.core.Agent;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import jade.domain.DFService;
import jade.domain.FIPAException;
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
	private ArrayList<TimeSlot> ordersDone;
	private HashMap<String, TimeSlot> ordersPending;
	private DFAgentDescription dfd;
	private FileWriter fw;
	private ArrayList<TimeSlot> allocatedTime;
	
	//constructor
	
	public Machine(String id, String role, int averageTime) {
		Random rand = new Random();
		
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
		this.numberLies = 0;
		this.ordersTaken = new HashMap<String, TimeSlot>();
		this.ordersPending = new HashMap<String, TimeSlot>();
		this.ordersDone = new ArrayList<TimeSlot>();
		// between [0,1]
		this.proactivity = (rand.nextInt(101))/100.0;
		this.liar = (rand.nextInt(101))/100.0;
		this.allocatedTime = new ArrayList<TimeSlot>();
	}
		
	//class that starts when the agent is created
	public void setup() {
		try {
			this.fw = new FileWriter("messages/" + this.getId() + ".txt", false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.writeFW("Machine: " + this.id + "\n Role: " + this.role + "\n Average time: " + this.averageTime + "\n\n");
		this.addBehaviour(new MachineResponderToOrder(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
		this.register();
		
	}
	
	// register on yellow pages TODO: test
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
	
	public double getProactivityRatio() {
		return this.proactivity;
	}
	
	public ArrayList<TimeSlot> getOrdersDone() {
		return this.ordersDone;
	}
	
	public boolean acceptOrder(String order_id, int[] time) {
		TimeSlot t = new TimeSlot(order_id, time[0], time[1]);
		this.ordersPending.put(order_id, t);
		return true;
	}
	
	public int[] getExpectedFinishTime(int startTime) {
		int expectedTimeToTake = this.averageTime;
		Random r = new Random();
		if(r.nextInt(101) / 100.0 > this.liar) { // liar
			expectedTimeToTake =  expectedTimeToTake - (int)((this.proactivity-0.5) * expectedTimeToTake);
			this.numberLies++;
		}
		
		int i = startTime;
		int j = startTime + expectedTimeToTake;
		int[] expectedTime = new int[2];
		expectedTime[0] = i;
		expectedTime[1] = j;
		
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
	
	public void addOrdersTaken(String id) {
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
		this.writeFW("\n\nFinal Average Time: " + this.averageTime + "\n");
		try {
			this.fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doOrders() {
		Collections.sort(allocatedTime);
		int next_start_time = 0;
		for(int i = 0; i < allocatedTime.size(); i++) {
			if(next_start_time < allocatedTime.get(i).startTime)
				next_start_time = allocatedTime.get(i).startTime;
			Random random = new Random();
			int noise = 0;
			if(random.nextInt(3) != 0) 
				noise = random.nextInt(averageTime + 1) - averageTime / 2;
			int finishTime = allocatedTime.get(i).startTime + noise;
			this.ordersDone.add(new TimeSlot(allocatedTime.get(i).order, next_start_time, finishTime));
			this.ordersTaken.remove(allocatedTime.get(i).order);
			next_start_time = finishTime;
			this.averageTime += (int)(noise / this.ordersDone.size());
		}
	}
}

