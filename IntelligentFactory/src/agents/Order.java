package agents;

import behaviours.OrderSendsArrivalMessage;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class Order extends Agent {
	private String id;
	private ArrayList<String> tasks;
	private int finishTime;
	private boolean finished;
	private HashMap<String, String> machines;
	private HashMap<String, Integer> machinesFinishTime;
	private FileWriter fw;

	// constructor
	public Order(String id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;
		this.finished = false;
		this.finishTime = 0;
		this.machines = new HashMap<String, String>();
		this.machinesFinishTime = new HashMap<String, Integer>();
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
	
	public void endTask() {
		if(this.machines.size() == this.tasks.size())
			this.setFinished(true);
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
	
	public void writeResult() {
		String report = "\n\nRESULT: Task Fulfilled after " + this.finishTime + " time unities.\n\n";
		for(int i = 0; i < this.tasks.size(); i++) {
			String machine_id = this.machines.get(this.tasks.get(i));
			report += " Task: " + this.tasks.get(i) + "\n";
			report += "    Machine: " + machine_id + "\n";
			report += "    Finish Time: " + this.machinesFinishTime.get(machine_id) + "\n\n";
		}
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
		content += "\n\n";
		this.writeFW(content);
		SequentialBehaviour askMachines = new SequentialBehaviour();
		for(int i = 0; i < this.tasks.size(); i++) {
			askMachines.addSubBehaviour(new OrderSendsArrivalMessage(this, new ACLMessage(ACLMessage.CFP)));
		}
		this.addBehaviour(askMachines);
	}

}
