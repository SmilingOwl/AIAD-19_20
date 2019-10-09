package utils;

public class Proposal {
	enum State {
		ACCEPTED, REFUSED, PENDING, CONSTRUCTION
	}
	
	State current_state;
	String []machines;
	long time_needed;
	int nr_tasks_without_machine;
	
	public Proposal(String owner_id, int nr_tasks, int owner_task) {
		this.current_state = State.CONSTRUCTION;
		this.machines = new String[nr_tasks];
		this.machines[owner_task] = owner_id;
		this.nr_tasks_without_machine = nr_tasks;
	}
	
	public State get_current_state() {
		return this.current_state;
	}
	
	public String[] get_machines() {
		return this.machines;
	}
		
	public long get_time_needed() {
		return this.time_needed;
	}
	
	public int get_nr_tasks_without_machine() {
		return this.nr_tasks_without_machine;
	}
	
	public void accept_proposal() {
		this.current_state = State.ACCEPTED;		
	}
	
	public void refuse_proposal() {
		this.current_state = State.REFUSED;		
	}
	
	public void set_as_pending() {
		this.current_state = State.PENDING;
	}
	
	public void add_machine(String id_machine, int task) {
		this.machines[task] = id_machine;
		this.nr_tasks_without_machine--;
	}
		
	public void set_time_needed(long time_needed) {
		this.time_needed = time_needed;
	}
	
	public boolean is_ready() {
		if(nr_tasks_without_machine == 0)
			return true;
		return false;
	}

	
}
