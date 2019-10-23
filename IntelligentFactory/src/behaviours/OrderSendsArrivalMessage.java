package behaviours;

import agents.Order;

import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;


import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

public class OrderSendsArrivalMessage extends ContractNetInitiator {
	Order parent;
	//private static HashMap <String, ArrayList<String>> Results = null;
	//private static HashMap <String, Long> Results2 = null;

	public OrderSendsArrivalMessage(Order parent, ACLMessage cfp) {
		super(parent, cfp);
		this.parent = parent;
	}

	protected Vector<ACLMessage> prepareCfps(ACLMessage msg) {
		Vector<ACLMessage> vector = new Vector<ACLMessage>();
		String content = "ARRIVED " + this.parent.getId() + " ";
		ArrayList<String> tasks = this.parent.getTasks();
		for (int i = 0; i < tasks.size(); i++) {
			content += tasks.get(i) + " ";
		}
		msg.setContent(content);
		
		for (int i = 0; i < tasks.size(); i++) {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(tasks.get(i));
			template.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(this.parent, template);
				for (int j = 0; j < result.length; j++) {
					msg.addReceiver(result[j].getName());
				}

			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
		vector.add(msg);		
		return vector;				
	}
	
	protected void handleAllResponses(Vector responses, Vector acceptances) {

		// tasks, ArrayList<ids>
		HashMap<String, ArrayList<String>> tasksMachineIds = new HashMap<String,ArrayList<String>>();
		
		// id -> finish time
		HashMap <String, Long> idFinishTime = new HashMap <String,Long>();
		
		int numberTasks = parent.getTasks().size();
		for (int i = 0; i < numberTasks; i++) {
			ArrayList<String> ids = new ArrayList<String>();
			tasksMachineIds.put(parent.getTasks().get(i), ids);
		}

		for (int i = 0; i < responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			
			tasksMachineIds.get(msgContent[2]).add(msgContent[1]);
			idFinishTime.put(msgContent[1], Long.parseLong(msgContent[3]));
			
		}
		
		// return the machine id with the min finish time
		// send ACCEPT for that machine id and REJECT for the others
	
		for (int n=0 ; n < this.parent.getTasks().size(); n++) {
			ArrayList<String> MachinesIds = tasksMachineIds.get(this.parent.getTasks().get(n));
			if (MachinesIds.size()==0) {
				return;
			}
			this.parent.ComparingTimes(MachinesIds,idFinishTime);
		}
		
		
		for (int i =0; i< responses.size(); i++) {
			ACLMessage msg = (ACLMessage) responses.elementAt(i);
			String[] msgContent = msg.getContent().split(" ");
			ACLMessage reply = msg.createReply();
			
			
			//if(msgContent[1] == returned id ){
			//reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			//reply.setContent("ACCEPT " + this.parent.getId());}
			
			//else{
			reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
			reply.setContent("REJECT " + this.parent.getId());
			//}
			
			acceptances.add(reply);{
				}
			
			}
			
			
		      }
			

			//this.setResults(tasksMachineIds); 
			//this.setResults2(idFinishTime);
			
		
	
	
	protected void handleAllResultNotifications(Vector resultNotifications) {
		// save in the order the finish time of each task
		}
	
}