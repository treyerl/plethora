package plethora.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.util.math.Vec3;

public class Ple_Util {

	IController controller;

	public Ple_Util(IController controller){
		this.controller = controller;
	}

	/**
	 * 
	 * @param boids
	 * @param filename
	 * @throws IOException 
	 */
	public void createLocationFile (ArrayList <Ple_Agent> boids, String filename, String sepSymbol, boolean block) 
			throws IOException{
		
		File f = new File(String.format(filename+".%.3f.csv", controller.getScheduler().getTime()));
		f.createNewFile();
		PrintWriter output = new PrintWriter(f); 

		System.out.println("creating csv file");

		for (Ple_Agent pa : boids) {
			if(!block){
				output.println(pa.getLocation().x + "," + pa.getLocation().y + "," + pa.getLocation().z);
			}else{
				output.print(pa.getLocation().x + "," + pa.getLocation().y + "," + pa.getLocation().z + sepSymbol);
			}
		}

		output.flush();
		output.close();

		System.out.println(f + " created.");
	}
	
	/**
	 * 
	 * @param boids
	 * @param filename
	 * @throws IOException 
	 */
	public void createTrailFile (ArrayList <Ple_Agent> boids, String filename) throws IOException{
		File f = createIFNotExists(String.format(filename+".%.3f.csv", controller.getScheduler().getTime()));
		PrintWriter output = new PrintWriter(f);

		System.out.println("creating csv file");

		for (Ple_Agent pa : boids) {
			output.println("agent");
			for(Vec3 v: pa.getTrail()){
				if(v != null){
				//output.println(v.x + "," + v.y + "," + v.z + "," + count + "," + count2);
				output.println(v.x + "," + v.y + "," + v.z);
				}
			}
		}

		output.flush();
		output.close();

		System.out.println(f + " created.");
	}
	
	
	/**
	 * 
	 * @param boids
	 * @param filename
	 * @throws IOException 
	 */
	public void createAnchoredData (ArrayList <Ple_Agent> boids, String filename) throws IOException{
		String time = String.format(".%.3f", controller.getScheduler().getTime());
		PrintWriter output = new PrintWriter(createIFNotExists("data/loc" + filename + time + ".csv")); 
		PrintWriter output2 = new PrintWriter(createIFNotExists("data/vels" + filename + time + ".csv")); 
		PrintWriter output3 = new PrintWriter(createIFNotExists("data/anchor" + filename + time + ".csv")); 

		System.out.println("creating csv file");

		for (Ple_Agent pa : boids) {
			if(pa.getLockState()){
				output.println(pa.getLocation().x + "," + pa.getLocation().y + "," + pa.getLocation().z);
				output2.println(pa.getVelocity().x + "," + pa.getVelocity().y + "," + pa.getVelocity().z);
				output3.println(pa.getAnchor().x + "," + pa.getAnchor().y + "," + pa.getAnchor().z);
			}
			
		}

		output.flush();
		output.close();
		
		output2.flush();
		output2.close();
		
		output3.flush();
		output3.close();

		System.out.println("data/loc" + filename + time + ".csv" + " created.");
	}
	
	private File createIFNotExists(String filename) throws IOException{
		File f = new File(filename);
		f.createNewFile();
		return f;
	}
	
	
	public void createTailFile (ArrayList <Ple_Agent> boids, String filename) throws IOException{
		String time = String.format(".%.3f", controller.getScheduler().getTime());
		PrintWriter output = new PrintWriter(createIFNotExists("data/tailLocs" + filename + time + ".csv")); 
		PrintWriter output2 = new PrintWriter(createIFNotExists("data/index" + filename + time + ".csv"));
		PrintWriter output3 = new PrintWriter(createIFNotExists("data/vels" + filename + time + ".csv"));
		PrintWriter output4 = new PrintWriter(createIFNotExists("data/tailIndex" + filename + time + ".csv"));

		System.out.println("creating csv file");

		int agentId = 0;
		
		for (Ple_Agent pa : boids) {
			for(int i = 0; i < pa.getTrail().size(); i ++){	
			
				//output.println(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z);
				output.println(pa.getTrail().get(i).x + "," + pa.getTrail().get(i).y + "," + pa.getTrail().get(i).z);
				output2.println(agentId);
				output3.println(pa.getVelocity().x + "," + pa.getVelocity().y + "," + pa.getVelocity().z);
				output4.println(i);
				
			}
			agentId++;
			
				//output.print(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z + sepSymbol);
			
		}

		output.flush();
		output.close();
		
		output2.flush();
		output2.close();
		
		output3.flush();
		output3.close();
		
		output4.flush();
		output4.close();

		System.out.println("data/loc" + filename + time + ".csv" + " created.");
	}



	/**
	 * 
	 * @param boids
	 * @param filename
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void createVelocityFile (ArrayList <Ple_Agent> boids, String filename, String sepSymbol, boolean block) 
			throws FileNotFoundException, IOException{
		String time = String.format(".%.3f", controller.getScheduler().getTime());
		PrintWriter output = new PrintWriter(createIFNotExists("data/vel" + filename + time + ".csv")); 

		System.out.println("creating csv file");

		for (Ple_Agent pa : boids) {
			if(!block){
				output.println(pa.getVelocity().x + "," + pa.getVelocity().y + "," + pa.getVelocity().z);
			}else{
				output.print(pa.getVelocity().x + "," + pa.getVelocity().y + "," + pa.getVelocity().z  + sepSymbol);
			}
		}

		output.flush();
		output.close();

		System.out.println("data/vel" + filename + time + ".csv" + " created.");
	}
}
