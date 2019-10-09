package factory;

import java.util.ArrayList;
import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

import agents.Machine;
import agents.Order;

/*
 * In this class we can define which tasks exist in the factory and initialize the agents.
 * Basically to manage the project (create machines, orders and stuff).
 */
public class IntelligentFactory {

	private ArrayList<String> tasks = new ArrayList<String>();
	private ArrayList<Machine> machines = new ArrayList();
	private int numberMachines;
	private int numberOrders;
	private int minNumberTasksPerOrder;
	private int maxNumberTasksPerOrder;
	private int minTimePerTask;
	private int maxTimePerTask;
	private Runtime rt;
	private Profile p;
	private ContainerController cc;
	
	
	//constructor
	public IntelligentFactory(int numberMachines, int numberOrders, int minNumberTasksPerOrder, int maxNumberTasksPerOrder, int minTimePerTask, int maxTimePertask) {
		
		this.numberMachines = numberMachines;
		this.numberOrders = numberOrders;
		this.maxNumberTasksPerOrder = maxNumberTasksPerOrder;
		this.minNumberTasksPerOrder = minNumberTasksPerOrder;
		this.minTimePerTask = minTimePerTask;
		this.maxTimePerTask = maxTimePerTask;
		
		this.tasks.add("snipping");
		this.tasks.add("screwing");
		this.tasks.add("sawing");
		this.tasks.add("sewing");
		this.tasks.add("mixing");
		
		this.rt = Runtime.instance();
		this.p = new ProfileImpl(true);
		this.cc = rt.createMainContainer(p);
		
		try {
			createMachines();
			createOrders();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createMachines() {
		int averageTime;
		int indexRole;
		
		for (int id=1; id<numberMachines; id++ ) {
			
			Random rd = new Random();
			averageTime = rd.nextInt((maxTimePerTask - minTimePerTask) + 1) + minTimePerTask;
			
			Random rd2 = new Random();
			indexRole = rd2.nextInt( (tasks.size()-1) + 1);
			
			Machine machine = new Machine(id,tasks.get(indexRole),averageTime);
			machines.add(machine);
		}

		
	}
	
	public void createOrders() {
		// random tasks + random number of tasks + generate id
		
	}
}
