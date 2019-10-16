package factory;

import agents.Order;
import agents.Machine;
import agents.Order;

import java.util.ArrayList;
import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.AgentController;

/*
 * In this class we can define which tasks exist in the factory and initialize the agents.
 * Basically to manage the project (create machines, orders and stuff).
 */
public class IntelligentFactory {

	private ArrayList<String> tasks = new ArrayList<String>();
	private ArrayList<Machine> machines = new ArrayList();
	private ArrayList<Order> orders = new ArrayList<Order>();
	private int numberMachines;
	private int numberOrders;
	private int minNumberTasksPerOrder;
	private int maxNumberTasksPerOrder;
	private int minTimePerTask;
	private int maxTimePerTask;
	private Runtime runTime;
	private Profile profile;
	private ContainerController containerController;

	// constructor
	public IntelligentFactory(int numberMachines, int numberOrders, int minNumberTasksPerOrder,
			int maxNumberTasksPerOrder, int minTimePerTask, int maxTimePerTask) {

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
		this.tasks.add("polishing");
		this.tasks.add("hammering");

		this.runTime = Runtime.instance();
		this.profile = new ProfileImpl(true);
		this.containerController = runTime.createMainContainer(profile);

		try {
			createMachines();
			Thread.sleep(500);
			createOrders();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createMachines() {
		int averageTime;
		int indexRole;

		for (int id = 1; id <= numberMachines; id++) {

			Random rd = new Random();
			averageTime = rd.nextInt((maxTimePerTask - minTimePerTask) + 1) + minTimePerTask;

			Random rd2 = new Random();
			indexRole = rd2.nextInt((tasks.size() - 1) + 1);

			Machine machine = new Machine("M" + id, tasks.get(indexRole), averageTime);
			machines.add(machine);
			try {
				AgentController agentControl = this.containerController.acceptNewAgent("m" + id, machine);
				agentControl.start();
			} catch (StaleProxyException ex) {
				ex.printStackTrace();
			}
		}

	}

	public void createOrders() {
		for (int i = 0; i < this.numberOrders; i++) {
			Random rand = new Random();
			ArrayList<String> order_tasks = new ArrayList<String>();
			int numberTasks = rand.nextInt(this.maxNumberTasksPerOrder - this.minNumberTasksPerOrder + 1)
					+ this.minNumberTasksPerOrder;
			
			if(numberTasks > this.tasks.size())
				numberTasks = this.tasks.size();
			
			for (int j = 0; j < numberTasks; j++) {
				rand = new Random();
				int new_task = rand.nextInt(this.tasks.size() - 1); // tasks can't be repeated
				
				if(!order_tasks.contains(this.tasks.get(new_task)))
					order_tasks.add(this.tasks.get(new_task));
			}
			Order new_order = new Order("O" + (i + 1), order_tasks);
			orders.add(new_order);
			try {
				AgentController agentControl = this.containerController.acceptNewAgent("o" + (i + 1), new_order);
				agentControl.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * to run, add arguments in the run configuration for this class; we can later
	 * make a menu that asks each of this arguments to make this easier on the user
	 */
	public static void main(String args[]) {
		IntelligentFactory factory = new IntelligentFactory(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]),
				Integer.parseInt(args[5]));
	}
}
