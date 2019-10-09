package factory;

import agents.Order;

import java.util.ArrayList;
import java.util.Random;

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
	private ArrayList<Order> orders = new ArrayList<Order>();
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
		this.tasks.add("polishing");
		this.tasks.add("hammering");
		
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
		for(int i = 0; i < this.numberOrders; i++) {
			Random rand = new Random();
			int numberTasks = rand.nextInt(this.maxNumberTasksPerOrder - this.minNumberTasksPerOrder + 1) + this.minNumberTasksPerOrder;
			ArrayList<String> order_tasks = new ArrayList<String>();
			for(int j = 0; j < numberTasks; j++) {
				rand = new Random();
				int new_task = rand.nextInt(this.tasks.size() - 1); //tasks can be repeated
				order_tasks.add(this.tasks.get(new_task));
			}
			Order new_order = new Order(i+1, order_tasks);
			orders.add(new_order);
		}
		
		
	}
}
