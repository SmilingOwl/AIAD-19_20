package factory;

import agents.Order;
import agents.Machine;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;

/*
 * In this class we can define which tasks exist in the factory and initialize the agents.
 * Basically to manage the project (create machines, orders and stuff).
 */
public class IntelligentFactory {

	private ArrayList<String> tasks = new ArrayList<String>();
	private ArrayList<Machine> machines = new ArrayList<Machine>();
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
		
		File message_dir = new File("messages");
        if(message_dir.exists()) {
        	String[] file_list = message_dir.list();
            for (int i = 0; i < file_list.length; i++) {
                File f = new File(message_dir, file_list[i]);
                f.delete();
            }
            message_dir.delete();
        }
        message_dir.mkdir();

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
		this.getResults();
	}

	public void createMachines() {
		int averageTime;
		int indexRole;
		
		System.out.println("\n\nMachines:");

		for (int id = 1; id <= numberMachines; id++) {

			Random rd = new Random();
			averageTime = rd.nextInt((maxTimePerTask - minTimePerTask) + 1) + minTimePerTask;

			Random rd2 = new Random();
			indexRole = rd2.nextInt((tasks.size() - 1) + 1);

			Machine machine = new Machine("M" + id, tasks.get(indexRole), averageTime);
			machines.add(machine);
		
			try {
				File machine_file = new File("messages/" + machine.getId() + ".txt");
				machine_file.createNewFile();
				AgentController agentControl = this.containerController.acceptNewAgent(machine.getId(), machine);
				agentControl.start();
			} catch (Exception ex) {
				ex.printStackTrace();
				
			}
			System.out.println(" - " + machine.getId() + ", role: " + machine.getRole() + ", initial average time: " + averageTime);
		}

	}

	public void createOrders() {
		System.out.println("\n\nOrders:");
		
		for (int i = 0; i < this.numberOrders; i++) {
			Random rand = new Random();
			ArrayList<String> order_tasks = new ArrayList<String>();
			int numberTasks = rand.nextInt(this.maxNumberTasksPerOrder - this.minNumberTasksPerOrder + 1)
					+ this.minNumberTasksPerOrder;
			
			if(numberTasks > this.tasks.size())
				numberTasks = this.tasks.size();
			
			System.out.print(" - O" + (i+1)  + ", tasks: ");
			
			for (int j = 0; j < numberTasks; j++) {
				rand = new Random();
				int new_task = rand.nextInt(this.tasks.size()); // tasks can't be repeated
				
				if(!order_tasks.contains(this.tasks.get(new_task))) {
					order_tasks.add(this.tasks.get(new_task));
					System.out.print(this.tasks.get(new_task) + " ");
				}
				
			}
			System.out.println();
			Order new_order = new Order("O" + (i + 1), order_tasks);
			orders.add(new_order);
			
			try {
				File order_file = new File("messages/" + new_order.getId() + ".txt");
				order_file.createNewFile();
				AgentController agentControl = this.containerController.acceptNewAgent(new_order.getId(), new_order);
				agentControl.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getResults() {
		ArrayList<String> finished_orders = new ArrayList<String>();
		int unfulfilled_orders = 0;
		while(finished_orders.size() < this.orders.size()) {
			for(int i = 0; i < this.orders.size(); i++) {
				if(!finished_orders.contains(this.orders.get(i).getId())) {
					if(this.orders.get(i).getFinished()) {
						finished_orders.add(this.orders.get(i).getId());
						if(this.orders.get(i).isUnfulfilled())
							unfulfilled_orders++;
					}
				}
			}
		}

		System.out.println("\n\nResults:\n");
		for(int i = 0; i < this.machines.size(); i++) {
			this.machines.get(i).finish();
			System.out.println(" - Machine: " + this.machines.get(i).getId() + " - Number of orders: " + this.machines.get(i).getOrdersDone().size());
		}
		
		//calculate average time per order
		//final time (last finish time)
		System.out.println("\n - Number of Unfulfilled Orders: " + unfulfilled_orders);
		System.out.println("\nFor details relative to each order / machine, consult the respective txt file in folder messages");
	}
	
	/*
	 * to run, add arguments in the run configuration for this class; we can later
	 * make a menu that asks each of this arguments to make this easier on the user
	 */
	public static void main(String args[]) {
		new IntelligentFactory(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]),
				Integer.parseInt(args[5]));
	}
}
