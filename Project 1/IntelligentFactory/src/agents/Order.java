package agents;

import behaviours.OrderInitiator;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Order extends Agent {
	private String id;
	private ArrayList<String> tasks;
	private double credits_per_task;
	private double credits_available;
	private int finishTime;
	private boolean finished;
	private HashMap<String, String> machines;
	private HashMap<String, Integer> machinesFinishTime;
	private FileWriter fw;

	// constructor
	public Order(String id, ArrayList<String> tasks, double credits_per_task) {
		this.id = id;
		this.tasks = tasks;
		this.finished = false;
		this.finishTime = 0;
		this.machines = new HashMap<String, String>();
		this.machinesFinishTime = new HashMap<String, Integer>();
		this.credits_per_task = credits_per_task;
		this.credits_available = this.credits_per_task * this.tasks.size() * 0.2;
	}

	public String getId() {
		return this.id;
	}
	
	public boolean getFinished() {
		return this.finished;
	}
	
	public HashMap<String, String> getMachines() {
		return this.machines;
	}
	
	public boolean isUnfulfilled() {
		if(this.machines.size() != this.tasks.size())
			return true;
		return false;
	}

	public void writeFW(String content) {
		try {
			this.fw.write(content);
			this.fw.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<String> getTasks() {
		return this.tasks;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
	
	public int getFinishTime() {
		return this.finishTime;
	}
	
	public double getCreditsPerTask() {
		return this.credits_per_task;
	}
	
	public void endTask() {
		if(this.machines.size() == this.tasks.size())
			this.setFinished(true);
		else
			this.addBehaviour(new OrderInitiator(this, this.getInitialMessage(this.tasks.get(this.machines.size())), this.tasks.get(this.machines.size())));
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
		
		if(!this.isUnfulfilled()) {
			this.writeResult();
		}
		try {
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addMachine(String task, String id) {
		this.machines.put(task, id);
	}

	public void addMachinesFinishTime(String id, Integer ft) {
		this.machinesFinishTime.put(id, ft);
	}
	
	public String comparingTimes(ArrayList<String> MachineId, HashMap<String, Integer> FinishTimes) {
		Integer min = Integer.MAX_VALUE;
		String id = MachineId.get(0);
		for (int i = 0; i < MachineId.size(); i++) {
			int FinishTime = FinishTimes.get(MachineId.get(i));
			if (FinishTime < min) {
				min = FinishTime;
				id = MachineId.get(i);
			}
		}
		return id;
	}
	
	public boolean isSatisfied(int bestFinishTime, int secondBestFinishTime) {
		if(bestFinishTime < secondBestFinishTime - bestFinishTime * 0.2 || this.credits_available <= 0)
			return true;
		return false;
	}
	
	public double increase_credits_iteration(double credits, int bestFinishTime, int secondBestFinishTime) {
		double c = this.credits_available * (bestFinishTime - (secondBestFinishTime - bestFinishTime * 0.2)) / bestFinishTime;
		double new_credits = credits + c;
		this.credits_available -= c;
		if(this.credits_available < 0) {
			new_credits += this.credits_available;
			this.credits_available = 0;
		}
		return new_credits;
	}
	
	public void writeResult() {
		String report = "\n\nRESULT: Tasks fulfilled after " + this.finishTime + " time unities.\n\n";
		for(int i = 0; i < this.tasks.size(); i++) {
			String machine_id = this.machines.get(this.tasks.get(i));
			report += " Task: " + this.tasks.get(i) + "\n";
			report += "    Machine: " + machine_id + "\n";
			report += "    Finish Time: " + this.machinesFinishTime.get(machine_id) + "\n\n";
		}
		report += " Remaining credits: " + this.credits_available;
		this.writeFW(report);
	}
	
	// class that is called when the agent starts
	public void setup() {
		try {
			this.fw = new FileWriter("messages/" + this.getId() + ".txt", false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String content = "New order: " + id + "\n Tasks: ";
		for (int i = 1; i <= this.tasks.size(); i++)
			content += this.tasks.get(i - 1) + "; ";
		content += "\n";
		content += "Credits per task: " + this.credits_per_task + "\nExtra credits: " + this.credits_available + "\n\n";
		this.writeFW(content);
		this.addBehaviour(new OrderInitiator(this, this.getInitialMessage(this.tasks.get(0)), this.tasks.get(0)));
	}
	
	public ACLMessage getInitialMessage(String task) {
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		if(this.getFinished())
			return null;
		String content = "ARRIVED " + this.getId() + " " + this.getFinishTime() + " " + this.getCreditsPerTask() + " " + task;
		msg.setContent(content);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(task);
		template.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this, template);
			for (int j = 0; j < result.length; j++) {
				msg.addReceiver(result[j].getName());
			}
			if (result.length == 0) {
				this.writeFW("\n\nRESULT: No machines to complete task "
						+ task + ".\n" + " Order not fulfilled.");
				this.setFinished(true);
			}

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		if (!this.getFinished()) {
			this.writeFW(">> Sent Message: " + content + "\n");
			return msg;
		}
		return null;
	}

}
