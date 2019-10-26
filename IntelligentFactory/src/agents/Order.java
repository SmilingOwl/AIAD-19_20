package agents;

import behaviours.OrderSendsArrivalMessage;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class Order extends Agent {
	private String id;
	private ArrayList<String> tasks;
	private long finishTime;
	private boolean finished;
	private HashMap<String, String> machines;
	private HashMap<String, Long> machinesFinishTime;
	private FileWriter fw;

	// constructor
	public Order(String id, ArrayList<String> tasks) {
		this.id = id;
		this.tasks = tasks;
		this.finished = false;
		this.machines = new HashMap<String, String>();
		this.machinesFinishTime = new HashMap<String, Long>();
	}

	public String getId() {
		return this.id;
	}
	
	public boolean getFinished() {
		return this.finished;
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

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
		try {
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setMachines(HashMap<String, String> machines) {
		this.machines = machines;
	}

	public void setMachinesFinishTime(HashMap<String, Long> mft) {
		this.machinesFinishTime = mft;
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
		this.addBehaviour(new OrderSendsArrivalMessage(this, new ACLMessage(ACLMessage.CFP)));
	}

	public String comparingTimes(ArrayList<String> MachineId, HashMap<String, Long> FinishTimes) {
		Long min = Long.MAX_VALUE;
		String id = MachineId.get(0);
		for (int i = 0; i < MachineId.size(); i++) {
			long FinishTime = FinishTimes.get(MachineId.get(i));
			if (FinishTime < min) {
				min = FinishTime;
				id = MachineId.get(i);
			}
		}
		return id;
	}
	
	public void writeResult() {
		String report = "\n\nRESULT: Order Fulfilled after " + this.finishTime + " time unities.\n\n";
		for(int i = 0; i < this.tasks.size(); i++) {
			String machine_id = this.machines.get(this.tasks.get(i));
			report += " Task: " + this.tasks.get(i) + "\n";
			report += "    Machine: " + machine_id + "\n";
			report += "    Finish Time: " + this.machinesFinishTime.get(machine_id) + "\n\n";
		}
		this.writeFW(report);
	}

}
