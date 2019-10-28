package agents;
import utils.Proposal;
import behaviours.*;

import jade.core.Agent;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Machine extends Agent {
	private String id;
	private int numberLies=0;
	private String role;
	private long averageTime;
	private double proactivity;
	private double liar; //lies if liar < 0.3
	private ArrayList<String> ordersTaken; //contains the initial time to perform each task that is already allocated
	private ArrayList<String> ordersPending;
	private HashMap<String, Long> ordersDone;
	private long latestFinishTime;
	private DFAgentDescription dfd;
	private FileWriter fw;
	
	//constructor
	
	public Machine(String id, String role, long averageTime) {
		Random rand = new Random();
		
		this.id = id;
		this.role = role;
		this.averageTime = averageTime;
		this.ordersTaken = new ArrayList<String>();
		this.ordersPending = new ArrayList<String>();
		this.ordersDone = new HashMap<String, Long>();
		this.latestFinishTime = 0;
		// between [0,1]
		this.proactivity = (rand.nextInt(101))/100.0;
		this.liar = (rand.nextInt(101))/100.0;
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
	
	public HashMap<String, Long> getOrdersDone() {
		return this.ordersDone;
	}
	
	public String getRole() {
		return this.role;
	}
	
	//for now, the machine accepts all orders
	public boolean acceptOrder(String order_id) {
		this.ordersPending.add(order_id);
		return true;
	}
	
	//we can improve this function by also taking into account the pending orders
	public long getExpectedFinishTime() {
		long expectedFinishTime = this.latestFinishTime + (this.ordersTaken.size()+1) * this.averageTime;
		
		if(this.liar < 0.3) { // liar
			expectedFinishTime =  expectedFinishTime - (long)((this.proactivity-0.5) * expectedFinishTime);
			this.numberLies++;
		}
		return expectedFinishTime;
			
	}
	
	public long doOrder(String id) {
		long finishTime = this.latestFinishTime;
		if(!this.ordersTaken.contains(id)) {
			return -1;
		}
		Random random = new Random();
		long noise = random.nextInt((int) averageTime) - averageTime / 2;
		finishTime += averageTime + noise;
		this.ordersDone.put(id, finishTime);
		this.ordersTaken.remove(this.ordersTaken.indexOf(id));
		this.latestFinishTime = finishTime;
		this.averageTime += (long)(noise / this.ordersDone.size());
		return finishTime;
	}
	
	public void deleteFromPending(String id) {
		this.ordersPending.remove(id);
	}
	
	public void addOrdersTaken(String id) {
		this.ordersTaken.add(id);
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
}

