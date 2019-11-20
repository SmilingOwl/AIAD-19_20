package utils;

public class TimeSlot implements Comparable<TimeSlot> {
	public int startTime;
	public int finishTime;
	public String order;
	
	public TimeSlot(String order, int startTime, int finishTime) {
		this.order = order;
		this.startTime = startTime;
		this.finishTime = finishTime;
	}
	
	
	public int compareTo(TimeSlot t) {
		return this.startTime - t.startTime;
	}
}
