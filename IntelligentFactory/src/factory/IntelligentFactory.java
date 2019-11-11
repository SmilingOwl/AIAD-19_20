package factory;

import agents.Order;
import agents.Machine;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;

/*
 * Class which initializes the factory with machines and orders.
 */
public class IntelligentFactory {

	private ArrayList<String> tasks;
	private ArrayList<Machine> machines = new ArrayList<Machine>();
	private ArrayList<Order> orders = new ArrayList<Order>();
	private int numberMachines;
	private int numberOrders;
	private int minNumberTasksPerOrder;
	private int maxNumberTasksPerOrder;
	private int minTimePerTask;
	private int maxTimePerTask;
	private int minCredits;
	private int maxCredits;
	private Runtime runTime;
	private Profile profile;
	private ContainerController containerController;

	// constructor
	public IntelligentFactory(int numberMachines, int numberOrders, int minNumberTasksPerOrder,
			int maxNumberTasksPerOrder, int minTimePerTask, int maxTimePerTask, int minCredits, int maxCredits,
			ArrayList<String> tasks) {

		this.numberMachines = numberMachines;
		this.numberOrders = numberOrders;
		this.maxNumberTasksPerOrder = maxNumberTasksPerOrder;
		this.minNumberTasksPerOrder = minNumberTasksPerOrder;
		this.minTimePerTask = minTimePerTask;
		this.maxTimePerTask = maxTimePerTask;
		this.minCredits = minCredits;
		this.maxCredits = maxCredits;

		this.tasks = tasks;

		File message_dir = new File("messages");
		if (message_dir.exists()) {
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
			Thread.sleep(100);
			createOrders();

		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getResults();
	}

	public void createMachines() {
		int averageTime;
		int indexRole;
		double honesty;
		double proactivity;

		System.out.println("\n\nMachines:");

		for (int id = 1; id <= numberMachines; id++) {

			Random rd = new Random();
			averageTime = rd.nextInt((maxTimePerTask - minTimePerTask) + 1) + minTimePerTask;

			rd = new Random();
			indexRole = rd.nextInt((tasks.size() - 1) + 1);

			rd = new Random();
			proactivity = (rd.nextInt(101)) / 100.0;

			rd = new Random();
			honesty = (rd.nextInt(101)) / 100.0;

			Machine machine = new Machine("M" + id, tasks.get(indexRole), averageTime, proactivity, honesty);
			machines.add(machine);

			try {
				File machine_file = new File("messages/" + machine.getId() + ".txt");
				machine_file.createNewFile();
				AgentController agentControl = this.containerController.acceptNewAgent(machine.getId(), machine);
				agentControl.start();
			} catch (Exception ex) {
				ex.printStackTrace();

			}
			System.out.println(" - " + machine.getId() + " - Role: " + machine.getRole() + " - Average Time: " + averageTime);
		}

	}

	public void createOrders() {
		System.out.println("\n\nOrders:");

		for (int i = 0; i < this.numberOrders; i++) {
			Random rand = new Random();
			ArrayList<String> order_tasks = new ArrayList<String>();
			int numberTasks = rand.nextInt(this.maxNumberTasksPerOrder - this.minNumberTasksPerOrder + 1)
					+ this.minNumberTasksPerOrder;

			if (numberTasks > this.tasks.size())
				numberTasks = this.tasks.size();

			rand = new Random();
			double credits = (double) (rand.nextInt(this.maxCredits - this.minCredits) + this.minCredits);

			System.out.print(" - O" + (i + 1) + " - Credits Per Task: " + credits + " - Extra Credits: " 
					+ String.format("%.2f", credits * this.tasks.size() * 0.2) + " - Tasks: ");

			for (int j = 0; j < numberTasks; j++) {
				rand = new Random();
				int new_task = rand.nextInt(this.tasks.size()); // tasks can't be repeated

				if (!order_tasks.contains(this.tasks.get(new_task))) {
					order_tasks.add(this.tasks.get(new_task));
					System.out.print(this.tasks.get(new_task) + " ");
				}

			}
			System.out.println();
			Order new_order = new Order("O" + (i + 1), order_tasks, credits);
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
				e.printStackTrace();
			}
		}
	}

	public void getResults() {
		double average_credits = 0;
		ArrayList<String> finished_orders = new ArrayList<String>();
		int unfulfilled_orders = 0;
		while (finished_orders.size() < this.orders.size()) {
			for (int i = 0; i < this.orders.size(); i++) {
				if (!finished_orders.contains(this.orders.get(i).getId())) {
					if (this.orders.get(i).getFinished()) {
						finished_orders.add(this.orders.get(i).getId());
						if (this.orders.get(i).isUnfulfilled())
							unfulfilled_orders++;
					}
				}
			}
		}

		System.out.println("\n\nResults:\n");
		for (int i = 0; i < this.machines.size(); i++) {
			this.machines.get(i).doOrders();
			this.machines.get(i).finish();
			System.out.println(" - Machine: " + this.machines.get(i).getId() + " - Number of orders: "
					+ this.machines.get(i).getOrdersDone().size() + " - Number of lies: "
					+ this.machines.get(i).getNumberLies() + " - Honesty ratio: " + this.machines.get(i).getLiarRatio()
					+ " - Proactivity ratio: " + this.machines.get(i).getProactivityRatio() + " - Credits received: "
					+ String.format("%.2f", this.machines.get(i).getCredits()));
			average_credits += this.machines.get(i).getCredits();
		}

		double[] time = getFinalAverageTimePerOrder(unfulfilled_orders);
		System.out.println("\n\nAllocation Report:\n");
		System.out.println(" - Average Time Per Order: " + time[0]);
		System.out.println(" - Last Finish Time: " + time[1]);
		System.out.println(" - Number of Unfulfilled Orders: " + unfulfilled_orders);
		System.out.println(
				" - Average Credits Received: " + String.format("%.2f", average_credits / this.machines.size()));
		System.out
				.println("\nFor details relative to each order / machine, consult the respective txt file in messages");
	}

	private double[] getFinalAverageTimePerOrder(int unfulfilled) {
		double[] time = new double[2];
		time[0] = 0;
		time[1] = 0;
		for (int i = 0; i < this.orders.size(); i++) {
			if (!this.orders.get(i).isUnfulfilled()) {
				int f = this.orders.get(i).getFinishTime();
				time[0] += f;
				if (time[1] < f)
					time[1] = f;
			}
		}
		time[0] = time[0] / (this.orders.size() - unfulfilled);
		return time;
	}

	public static void main(String args[]) {
		if(args.length == 0)
			menu();
		else {
			ArrayList<String> tasks = new ArrayList<String>();
			tasks.add("snipping");
			tasks.add("screwing");
			tasks.add("sawing");
			tasks.add("sewing");
			tasks.add("mixing");
			tasks.add("polishing");
			tasks.add("painting");
			tasks.add("gluing");
			tasks.add("hammering");
			new IntelligentFactory(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
					Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]),
					Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]), tasks);
		}

	}

	public static void menu() {
		System.out.println("This project is about a factory which has machines and orders. The objective is to allocate machines to fulfill each orders tasks.");
		System.out.println("\nSelect an option to run the project:");
		System.out.println("1. One task, 5 Machines, 3 Orders;");
		System.out.println("2. One task, 10 Machines, 30 Orders;");
		System.out.println("3. One task, 5 Machines, 100 Orders, same average time;");
		System.out.println("4. One task, 5 Machines, 100 Orders;");
		System.out.println("5. Two tasks, 5 Machines, 3 Orders;");
		System.out.println("6. Two tasks, 10 Machines, 50 Orders;");
		System.out.println("7. Four tasks, 5 Machines, 3 Orders;");
		System.out.println("8. Four tasks, 10 Machines, 50 Orders;");
		System.out.println("9. Nine tasks, 10 Machines, 50 Orders;");
		System.out.println("10. Nine tasks, 30 Machines, 200 Orders.");
		System.out.print("\nTo run with custom arguments run in the command line: \njava IntelligentFactory <number of machines> ");
		System.out.print("<number of orders> <minimum number of tasks per order> <maximum number of tasks per order>");
		System.out.print("<minimum average time per machine> <maximum average time per machine> <minimum number of credits>");
		System.out.println("<maximum number of credits>");
		System.out.println("Example: java IntelligentFactory 10 20 2 8 20 100 200 400\n");
		int option = 0;
		do {
			try {
				Scanner sc = new Scanner(System.in);
				System.out.println("\nOption:");
				option = sc.nextInt();
				sc.close();
			} catch (Exception e) {
				System.out.println("Invalid Input");
				continue;
			}
			if (option <= 0 || option >= 11) {
				System.out.println("Invalid Option");
			}
		} while (option <= 0 || option >= 11);
		receiveInput(option);
	}

	public static void receiveInput(int input) {
		ArrayList<String> tasks = new ArrayList<String>();
		tasks.add("snipping");
		switch (input) {
		case 1:
			new IntelligentFactory(5, 3, 1, 1, 40, 60, 200, 400, tasks);
			break;
		case 2:
			new IntelligentFactory(10, 30, 1, 1, 60, 80, 200, 400, tasks);
			break;
		case 3:
			new IntelligentFactory(5, 100, 1, 1, 50, 50, 200, 400, tasks);
			break;
		case 4:
			new IntelligentFactory(5, 100, 1, 1, 20, 100, 200, 400, tasks);
			break;
		case 5:
			tasks.add("screwing");
			new IntelligentFactory(5, 3, 1, 2, 40, 60, 200, 400, tasks);
			break;
		case 6:
			tasks.add("screwing");
			new IntelligentFactory(10, 50, 1, 2, 40, 60, 200, 400, tasks);
			break;
		case 7:
			tasks.add("screwing");
			tasks.add("sawing");
			tasks.add("sewing");
			new IntelligentFactory(5, 3, 1, 4, 40, 60, 200, 400, tasks);
			break;
		case 8:
			tasks.add("screwing");
			tasks.add("sawing");
			tasks.add("sewing");
			tasks.add("mixing");
			tasks.add("polishing");
			new IntelligentFactory(10, 50, 1, 4, 20, 80, 200, 400, tasks);
			break;
		case 9:
			tasks.add("screwing");
			tasks.add("sawing");
			tasks.add("sewing");
			tasks.add("mixing");
			tasks.add("polishing");
			tasks.add("painting");
			tasks.add("gluing");
			tasks.add("hammering");
			new IntelligentFactory(10, 50, 2, 4, 20, 80, 200, 400, tasks);
			break;
		case 10:
			tasks.add("screwing");
			tasks.add("sawing");
			tasks.add("sewing");
			tasks.add("mixing");
			tasks.add("polishing");
			tasks.add("painting");
			tasks.add("gluing");
			tasks.add("hammering");
			new IntelligentFactory(30, 200, 2, 8, 20, 100, 200, 400, tasks);
			break;
		}
	}
}
