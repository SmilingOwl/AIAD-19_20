package factory;

import java.util.ArrayList;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

/*
 * In this class we can define which tasks exist in the factory and initialize the agents.
 * Basically to manage the project (create machines, orders and stuff).
 */
public class IntelligentFactory {

	private ArrayList<String> tasks = new ArrayList<String>();
	private int numberMachines;
	private int numberOrders;
	private int minNumberTasksPerOrder;
	private int maxNumberTasksPerOrder;
	private Runtime rt;
	private Profile p;
	private ContainerController cc;
	
	
	//constructor
	public IntelligentFactory(int numberMachines, int numberOrders, int minNumberTasksPerOrder, int maxNumberTasksPerOrder) {
		
		this.numberMachines = numberMachines;
		this.numberOrders = numberOrders;
		this.maxNumberTasksPerOrder = maxNumberTasksPerOrder;
		this.minNumberTasksPerOrder = minNumberTasksPerOrder;
		
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
		// random role + random average time + generate id
		
		
	}
	
	public void createOrders() {
		// random tasks + random number of tasks + generate id
		
	}
}
