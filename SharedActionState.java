import java.net.*;
import java.io.*;

public class SharedActionState{
	
	private SharedActionState mySharedObj;
	private String myThreadName;
	private double mySharedVariable;
	private boolean accessing=false; // true a thread has a lock, false otherwise
	private int threadsWaiting=0; // number of waiting writers

// Constructor	
	
	SharedActionState(double SharedVariable) {
		mySharedVariable = SharedVariable;
	}

//Attempt to aquire a lock
	
	  public synchronized void acquireLock() throws InterruptedException{
	        Thread me = Thread.currentThread(); // get a ref to the current thread
	        System.out.println(me.getName()+" is attempting to acquire a lock!");	
	        ++threadsWaiting;
		    while (accessing) {  // while someone else is accessing or threadsWaiting > 0
		      System.out.println(me.getName()+" waiting to get a lock as someone else is accessing...");
		      //wait for the lock to be released - see releaseLock() below
		      wait();
		    }
		    // nobody has got a lock so get one
		    --threadsWaiting;
		    accessing = true;
		    System.out.println(me.getName()+" got a lock!"); 
		  }

		  // Releases a lock to when a thread is finished
		  
		  public synchronized void releaseLock() {
			  //release the lock and tell everyone
		      accessing = false;
		      notifyAll();
		      Thread me = Thread.currentThread(); // get a ref to the current thread
		      System.out.println(me.getName()+" released a lock!");
		  }
	
	
    /* The processInput method */

		    /* The processInput method */
			public synchronized String processInput(String myThreadName, String theInput) {
			    System.out.println(myThreadName + " received " + theInput);
			    String theOutput = null;
			    if (theInput.equalsIgnoreCase("Check_space")|| theInput.equalsIgnoreCase("Remove_car") || theInput.equalsIgnoreCase("Add_car") ) {
			        if (myThreadName.equals("ActionServerThread2") || myThreadName.equals("ActionServerThread1")) {

			        	if (theInput.equalsIgnoreCase("Check_space")) {
			                if (mySharedVariable > 0) {
			                    theOutput = "Parking spaces: " + mySharedVariable;
			                } else {
			                    theOutput = "car park full!";
			                }
			            } else if (theInput.equalsIgnoreCase("Add_car")) {

			                if (mySharedVariable > 0) {
			                	mySharedVariable--;
			                    theOutput = "Car parked successfully. Available spaces: " + mySharedVariable;
			                } else {
			                    theOutput = "The car park is full!";
			                }
			            } else {
			                theOutput = myThreadName + " received an invalid request.";
			            }
			        } else if (myThreadName.equals("ActionServerThread3") || myThreadName.equals("ActionServerThread4")) {
			        	if (theInput.equalsIgnoreCase("Remove_car")) {
			                if (mySharedVariable < 5) {
			                	mySharedVariable++;
			                    theOutput = "Car removed from the park. Available spaces: " + mySharedVariable;
			                } else {
			                    theOutput = "No cars in the car park.";
			                }
			            } else {
			                theOutput = myThreadName + " received an invalid request.";
			            }
			        } else {
			            System.out.println("Error - thread call not recognised.");
			        }
			    } else {
			        theOutput = " incorrect request - only understand \"Check_space\" or \"Add\" or \"Remove\"";
			    }
			    if (mySharedVariable < 1 || mySharedVariable > 5) {
			        System.out.println("Error: Request out of range!");
			        theOutput = "Error: Request out of range!";
			    }
			    System.out.println("Available spaces: " + mySharedVariable);
			    return theOutput;

			}	
}
