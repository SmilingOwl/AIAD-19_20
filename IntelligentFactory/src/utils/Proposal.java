package utils;

public class Proposal {
	enum State {
		ACCEPTED, REFUSED, PENDING, CONSTRUCTION
	}
	
	State currentState;
	int []machines;
	long timeNeeded;
	int nrTasksWithoutMachine;
	int id;
	
	public Proposal(int ownerId, int nrTasks, int ownerTask) {
		this.currentState = State.CONSTRUCTION;
		this.machines = new int[nrTasks];
		this.machines[ownerTask] = ownerId;
		this.nrTasksWithoutMachine = nrTasks;
	}
	
	public State getCurrentState() {
		return this.currentState;
	}
	
	public int[] getMachines() {
		return this.machines;
	}
		
	public long getTimeNeeded() {
		return this.timeNeeded;
	}
	
	public int getNrTasksWithoutMachine() {
		return this.nrTasksWithoutMachine;
	}
	
	public void acceptProposal() {
		this.currentState = State.ACCEPTED;		
	}
	
	public void refuseProposal() {
		this.currentState = State.REFUSED;		
	}
	
	public void setAsPending() {
		this.currentState = State.PENDING;
	}
	
	public void addMachine(int idMachine, int task) {
		this.machines[task] = idMachine;
		this.nrTasksWithoutMachine--;
	}
		
	public void setTimeNeeded(long timeNeeded) {
		this.timeNeeded = timeNeeded;
	}
	
	public boolean isReady() {
		if(nrTasksWithoutMachine == 0)
			return true;
		return false;
	}

	
}
