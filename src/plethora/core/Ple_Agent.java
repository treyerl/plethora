/**
 * 
 * @author		Jose Sanchez
 * @modified	13/07/2011
 * @version		0.1
 */

package plethora.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IEventScheduler.IAction;
import ch.fhnw.util.Pair;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.LineString;
import plethora.core.Ple_Terrain.IndexBox;

/**
 * This is an Agent Class. It works based on steering behaviors by Craig Reynolds implemented by Daniel Shiffman 
 * and Karsten Schmidt and vector movements inspired Langton's Ant. The Class uses toxiclibs, so please make sure 
 * you have toxiclibs installed. Check the example folder for more documentation.
 * The class works with the Ple_Terrain class, allowing terrain evaluation.
 * 
 * Written my Jose Sanchez - 2011
 * for feedback please contact me at: jomasan@gmail.com
 * 
 * @author jomasan 
 *
 */

public class Ple_Agent {

	private Vec3 loc;
	private Vec3 vel;
	private Vec3 acc;

	private Vec3 anchor;
	private boolean lock;
	
	private Vec3 v;
//	private float vx,vy,vz;

	private float maxspeed = 4;
	private float maxforce = 0.05f;

//	private boolean showSep = false;
//	private boolean showAli = false;
//	private boolean showCoh = false;

	private Vec3 [] tail = new Vec3 [1];
	private Vec3 [] velTail = new Vec3 [1];

	private float wandertheta = 0.0f;

	private List <Vec3> trail;
	
	private IController controller;


	/**
	 * 
	 * Basic constructor. Provide the PApplet refernce. (type 'this'). and the initial location of the agent. 
	 * By default agents will start without any initial velocity.
	 * 
	 * @param _p5
	 * @param _loc
	 */
	public Ple_Agent(IController controller, Vec3 _loc){
		this.controller = controller;
		loc = _loc;	

		vel = new Vec3(0,0,0);
		acc = new Vec3(0,0,0);	

		trail = new LinkedList<Vec3>();

		anchor = new Vec3(0,0,0);
		lock = false;

	}
	

	/**
	 * Method to update location. Addes the acceleration (acc) to the velocity which gets passed to the location. 
	 * The movement has momentum based on the acumulated velocity. Also has a maxspeed Value, that can be changed with
	 * the getter and setter methods. 
	 */
	public void update() {
		// Update velocity
		vel = vel.add(acc);
		// Limit speed
		vel = vel.normalize().scale(maxspeed);
		loc = loc.add(vel);
		// Reset accelertion to 0 each cycle; really??
		acc = new Vec3(0,0,0);
	}

	/**
	 * Allow to make2D. Will collapse the z values of the locations to the value provided.
	 * @param zValue - Value for location z.
	 */
	public void flatten (float zValue){
		loc = new Vec3(loc.x, loc.y, zValue);
	}

	/**
	 * Allows to flatten the velocity in order to get a 2D movement.
	 */
	public void flatVel(){
		acc = new Vec3(acc.x,acc.y,0);
		vel = new Vec3(vel.x,vel.y,0);
	}

	/**
	 * Method to update the location only by adding the velocity to the location. This simple update allows for more 
	 * 'mechanical' movements, not having the momentum of acceleration.
	 */
	public void updateSimple() {
		// Limit speed
		loc = loc.add(vel.normalize().scale(maxspeed));
	}


	/**
	 * This method searches for points in the Terrain class within certain range and angle.
	 * @param ter
	 * @param minRange - minimum distance
	 * @param maxRange - maximum distance
	 * @param minAngle - minimum angle
	 * @param maxAngle - maximum angle
	 * @return
	 */
	public List<Vec3> terrainPointsInRange(Ple_Terrain ter, float minRange,  float maxRange,  float minAngle,  float maxAngle){

		List<Vec3> pts = new LinkedList<>();

		for (int i = 0; i < ter.COLS; i++) {
			for (int j = 0; j < ter.ROWS; j++) {

				Vec3 ptLoc = ter.getPoint(j, i);

				float d = loc.distance(ptLoc);
				Vec3 dif = ptLoc.subtract(loc).normalize();
				float angle = MathUtilities.RADIANS_TO_DEGREES * vel.normalize().angle(dif);

				if(d < maxRange && d > minRange ) {
					if(angle < maxAngle && angle > minAngle) {

						pts.add(ptLoc);
					}
				}

			}
		}

		return pts;
	}

	/**
	 * This method evaluates points in range and associates the data map with their location.
	 * It returns an arrayList of Ple_Nodes with both location and data.
	 * @param ter - Terrain of reference
	 * @param dataMap - 2 dimensional array that contains the info to be associated to the points.
	 * @param minRange - minimum distance
	 * @param maxRange - maximum distance
	 * @param minAngle - minimum angle
	 * @param maxAngle - maximum angle
	 * @return
	 */
	public List<Pair<Pair<Integer, Integer>, Float>> terrainIndexInRange(Ple_Terrain ter, float [][] dataMap, float minRange,  float maxRange,  float minAngle,  float maxAngle){
		List<Pair<Pair<Integer, Integer>, Float>> pts = new LinkedList<>();
		IndexBox b = ter.getIndexBox(loc.x - maxRange, loc.y - maxRange, loc.x + maxRange, loc.y + maxRange);
		for (int i = b.minX; i < b.maxX; i++) {
			for (int j = b.minY; j < b.maxY; j++) {
				Vec3 ptLoc = ter.getPoint(j, i);
				float d = loc.distance(ptLoc);
				Vec3 dir = ptLoc.subtract(loc).normalize();
				float angle = MathUtilities.RADIANS_TO_DEGREES * vel.normalize().angle(dir);

				if(d < maxRange && d > minRange ) {
					if(angle < maxAngle && angle > minAngle) {
						pts.add(new Pair<>(new Pair<>(i,j), dataMap[i][j]));
					}
				}
			}
		}
		return pts;
	}

	/**
	 * activates a spring at the specified location
	 * @param where
	 */
	public void dropAnchor(Vec3 where){
		//update 
		if(!lock)anchor = where;
		lock = true;
	}

	/**
	 * calculates the spring if the 'drop anchor method has been activated'
	 * @param stiffness - stiffness of the spring
	 * @param damping - damping of the spring
	 * @param threshold - distance to the anchor 
	 * @param show - display a line for the spring
	 */
	public void updateAnchor( float stiffness, float damping, float threshold, boolean show){
//		float gravity = 0.0f; //0.6f
		float mass = 1.0f; // 2.0

		if(lock){
			Vec3 dif = anchor.subtract(loc);
			loc = loc.add((v = v.add(dif.scale(stiffness/mass)).scale(damping)));
			if (dif.length() < threshold && show) 
				vLine(loc, anchor);
		}
	}
	
	/**
	 * calculates the spring if the 'drop anchor method has been activated'
	 * @param stiffness - stiffness of the spring
	 * @param damping - damping of the spring
	 * @param restLength - distance to the anchor 
	 * @param threshold - distance to the anchor 
	 * @param show - display a line for the spring
	 */
	public void updateAnchor2( float stiffness, float damping, float restLength, float scale, float threshold, boolean show){
//		float gravity = 0.0f; //0.6f
		float mass = 1.0f; // 2.0

		//float dist = anchor.distanceTo(loc);
		//float normDistStrength = (dist - restLength) / dist;
		
		if(lock){
			Vec3 dif = anchor.subtract(loc);
			acc = acc.add((v = v.add(dif.subtract(restLength).scale(stiffness/mass)).scale(damping)).scale(scale));
			if (dif.length() < threshold && show) 
				vLine(loc, anchor);
		}
	}


	/**
	 * This method will release the anchor.
	 * 
	 */
	public void setFree(){
		lock = false;	
	}

	/**
	 * This method will draw the Trail if the dropTrail method is activated. Make sure to have a trail to draw! (initTail).
	 * @param thresh - this threshold allows for not drawing the lines longer than certain distance. 
	 * Useful when using a wrapparound space border.
	 */
	public void drawTrail(float thresh){
		if (trail.size() > 1){
			for (int i = 1; i < trail.size(); i++){
				Vec3 v1 = (Vec3) trail.get(i);
				Vec3 v2 = (Vec3) trail.get(i-1);

				float d = v1.distance(v2);
				if(d < thresh){
					vLine(v1,v2);
				}
			}
		}
	}
	
	public List<Vec3> getTrail(){
		return Collections.unmodifiableList(trail);
	}

	
	private IAction dropTrailAction(double every, int limit){
		return (time) -> {
			if(trail.size() < limit){
				trail.add(loc);
				controller.run(every, dropTrailAction(every, limit));
			} else {
				trail.remove(0);
			}
		};
	}
	
	/**
	 * This method allows for dropping a location vector to a list in order to generate a trail.
	 * @param every - drop vector every so many frames
	 * @param limit - limit the maximum number of drops.
	 */
	public void dropTrail(int every, int limit){
		controller.run((double)every, dropTrailAction(every, limit));
		
	}


	/**
	 * Method to calculate the normal to a spline based on the individual line segments of the spline. 
	 * It returns the closest normal. 
	 * @param sp - Spline3D to test
	 * @param v - vector to test
	 * @return
	 */
	public Vec3 closestNormalToLineString(LineString ls, Vec3 v){

		Vec3 target = null;
		//Vec3D dir = null;
		float cloDist = 1000000; 

		for (Pair<Vec3, Vec3> pair: ls)  {
			Vec3 a = pair.first;
			Vec3 b = pair.second;

			Vec3 normal = getNormalPoint(v,a,b);

			float da = normal.distance(a);
			float db = normal.distance(b);
			Vec3 line = b.subtract(a);

			if (da + db > line.length()+1) {
				normal = new Vec3(b.x, b.y, b.z);
			}

			float d = v.distance(normal);
			if (d < cloDist) {
				cloDist = d;
				target = normal;
				//dir = line.copy();
				//dir.normalize();
				//dir.scaleSelf(10);
			}		
		}
		return target;
	}

	/**
	 * Returns the normal to the spline plus the direction of the line, allows to flow along a line.
	 * @param ls - Spline3D to test
	 * @param v - vector to test
	 * @param amountOfDir - amount of directionality added to the normal vector.
	 * @return
	 */
	public Vec3 closestNormalandDirectionToLineString(LineString ls, Vec3 v, float amountOfDir){

		Vec3 target = null;
		Vec3 dir = null;
		float cloDist = 1000000;

		for (Pair<Vec3, Vec3> pair: ls) {
			Vec3 a = pair.first;
			Vec3 b = pair.second;

			Vec3 normal = getNormalPoint(v,a,b);

			float da = normal.distance(a);
			float db = normal.distance(b);
			Vec3 line = b.subtract(a);

			if (da + db > line.length()+1) {
				normal = new Vec3(b.x,b.y,b.z);
			}

			float d = v.distance(normal);
			if (d < cloDist) {
				cloDist = d;
				target = normal;

				dir = line.normalize().scale(amountOfDir);
			}

			if (target == null){
				System.out.println("upsy");
			}
			
			target = target.add(dir);

		}
		return target;
	}

	/**
	 * calculates the normal between 3 vectors
	 * @param p - vector 1
	 * @param a - vector 2
	 * @param b - vector 3
	 * @return
	 */
	public  Vec3 getNormalPoint(Vec3 p, Vec3 a, Vec3 b) {
		Vec3 ap = p.subtract(a);
		Vec3 ab = b.subtract(a).normalize();
		return a.add(ab.scale(ap.dot(ab)));
	}

	/**
	 * Calculates the future Location of the Agent.
	 * @param len - Length of the projection
	 * @return
	 */
	public Vec3 futureLoc(float len){
		return vel.normalize().scale(len).add(loc);
	}

//	/**
//	 * Calculates the update of the tail.
//	 * @param rate - updates the tail every so many frames, allows for longer tails, scattered.
//	 */
//	public void updateTail(int rate){	
//		if(p5.frameCount % rate == 0){
//			for (int i = 0; i < tail.length-1; i++){
//				tail[i] = tail[i+1];
//			}
//			tail[tail.length-1] = new Vec3(loc.x, loc.y, loc.z);
//		}
//	}

//	/**
//	 * Calculates the update of the velocity tail.
//	 * @param rate  - updates the tail every so many frames, allows for longer tails, scattered.
//	 */
//	public void updateVelTail(int rate){	
//		if(p5.frameCount % rate == 0){
//			for (int i = 0; i < velTail.length-1; i++){
//				velTail[i] = velTail[i+1];
//			}
//		}
//	}

	/**
	 * Initialize Tail. Call in the setup.
	 * @param size - define the size of the Tail.
	 */
	public void initTail(int size){
		tail = new Vec3[size];
		for(int i = 0; i < size; i++){
			tail[i] = new Vec3(loc.x, loc.y, loc.z);
		}
	}

	/**
	 * Initialize Velocity Tail. Call in the setup.
	 * @param size  - define the size of the Tail.
	 */
	public void initVelTail(int size){
		velTail = new Vec3[size];
		for(int i = 0; i < size; i++){
			velTail[i] = new Vec3(vel.x, vel.y, vel.z);
		}
	}


	/**
	 * Display a point on Location.
	 */
	public void displayPoint(){
//		vPt(loc);
	}

	/**
	 * Display a Line with the direction of the agent.
	 * @param len - length of the line
	 */
	public void displayDir(float len){
		Vec3 v = vel.normalize().scale(len).add(loc);	
//		vLine(loc, v);
	}

	/**
	 * Display the points of the tails. It allows to use different styles at begining and end.
	 * @param r1 - red start.
	 * @param g1 - green start.
	 * @param b1 - blue start
	 * @param al1 - alpha start
	 * @param s1 - size start.
	 * @param r2 - red end.
	 * @param g2 - green end.
	 * @param b2 - blue end.
	 * @param al2 - alpha end.
	 * @param s2 - size end.
	 */
	public void displayTailPoints(float r1, float g1, float b1, float al1, float s1, float r2, float g2, float b2, float al2, float s2){
		for(int i = 0; i < tail.length; i++){

			float r = MathUtilities.map(i, 0, tail.length-1, r1,r2);
			float g = MathUtilities.map(i, 0, tail.length-1, g1,g2);
			float b = MathUtilities.map(i, 0, tail.length-1, b1,b2);

			float a = MathUtilities.map(i, 0, tail.length-1, al1,al2);

			float s = MathUtilities.map(i, 0, tail.length-1, s1,s2);

//			p5.strokeWeight(s);
//			p5.stroke(r,g,b, a);
//			vPt(tail[i]);

			//vLine(tail[i], tail[i-1]);
		}
	}


//	/**
//	 * Vector Point
//	 * @param v
//	 */
//	public void vPt(Vec3D v){
//		p5.point(v.x,v.y,v.z);
//	}


	/**
	 * Add a force to the acceleration vector.
	 * @param x - force in X
	 * @param y - force in Y
	 * @param z - force in Z
	 */
	public void addForce(float x, float y, float z){
		acc = acc.add(new Vec3(x,y,z));
	}

	/**
	 * Vector Line
	 * @param v1 - Vector 1
	 * @param v2 - Vector 2
	 */
	public void vLine(Vec3 v1, Vec3 v2){
//		controller.getScene().add3DObject(MeshUtilities.createLines(Arrays.asList(v1, v2), 1));
	}

	/**
	 * Boundary for agents. Agents will reappear in the opposite side. Wrap-Space.
	 * @param dX - bound for X, from -dX to dX.
	 * @param dY - bound for Y, from -dY to dY.
	 * @param dZ - bound for Z, from -dZ to dZ.
	 */
	public void wrapSpace(float dX, float dY, float dZ) {
		float x = loc.x, y = loc.y, z = loc.z;
		if (loc.x < -dX) x = dX;
		if (loc.y < -dY) y = dY;
		if (loc.z < -dZ) z = dZ;
		if (loc.x > dX) x = -dX;
		if (loc.y > dY) y = -dY;
		if (loc.z > dZ) z = -dZ;
		loc = new Vec3(x,y,z);
	}

	/**
	 * bounce on borders.
	 * @param dX - bound for X, from -dX to dX.
	 * @param dY - bound for Y, from -dY to dY.
	 * @param dZ - bound for Z, from -dZ to dZ.
	 */
	public void bounceSpace(float dX, float dY, float dZ) {
		float x = loc.x, y = loc.y, z = loc.z;
		float vx = vel.x, vy = vel.y, vz = vel.z;
		if (loc.x <= -dX) {
			vx *= -1;
			x = -dX;
		}
		if (loc.y <= -dY) {
			vy *= -1;
			y =  -dY;
		}
		if (loc.z <= -dZ) {
			vz *= -1;
			z = -dZ;
		}
		if (loc.x >= dX) {
			vx *= -1;
			x = dX;
		}
		if (loc.y >= dY) {
			vy *= -1;
			y =  dY;

		}
		if (loc.z >= dZ) {
			vz *= -1;
			z = dZ;
		}
		loc = new Vec3(x,y,z);
		vel = new Vec3(vx,vy,vz);
	}

	/**
	 * Returns the closes agent.
	 * @param boids - list of agents in which to search for.
	 * @return - returns the closest agent.
	 */
	public Ple_Agent closestAgent(ArrayList <Ple_Agent> boids){

		float cloDist = 1000000;
		int cloId = 0;

		for (int i = 0; i < boids.size(); i ++){
			Ple_Agent pa  = (Ple_Agent)boids.get(i);
			float d = loc.distance(pa.loc);
			if(d < cloDist && d > 0){
				cloDist = d;
				cloId = i;
			}		
		}		
		Ple_Agent closestpa  = (Ple_Agent)boids.get(cloId);

		return closestpa;
	}




	/**
	 * This method allows to draw a line between the agents in the distance specified.
	 * @param boids - list of agents
	 * @param fromDist - min range
	 * @param toDist - max range
	 */
	public void drawLinesInRange(ArrayList<Ple_Agent> boids, float fromDist, float toDist){
		for (int i = 0; i < boids.size(); i++) {
			Ple_Agent other = (Ple_Agent) boids.get(i);

			float d = loc.distance(other.loc);
			if(d > fromDist && d < toDist){
				vLine(loc, other.loc);
			}

		}	
	}


	/**
	 * This method allows for drawing lines between agents tails.
	 * @param boids - list of agents
	 * @param fromDist - min distance
	 * @param toDist - max distance
	 */
	public void drawLinesBetweenTails(ArrayList<Ple_Agent> boids, float fromDist, float toDist){
		for (int i = 0; i < boids.size(); i++) {
			Ple_Agent other = (Ple_Agent) boids.get(i);
			for(int j = 0; j < tail.length; j++){
				float d = tail[j].distance(other.tail[j]);
				if(d > fromDist && d < toDist){
					vLine(tail[j], other.tail[j]);
				}
			}
		}
	}


	/**
	 * draws beziers between tail points, needs both tail and velocity tail. 
	 * @param boids - list of agents
	 * @param fromDist - min distance
	 * @param toDist - max distance
	 * @param scale1 - scale of the bezier handle 1 based on velocity tail.
	 * @param scale2 - scale of the bezier handle 2 based on velocity tail.
	 */
	public void drawBeziersBetweenTails(ArrayList<Ple_Agent> boids, float fromDist, float toDist, float scale1, float scale2){
		for (int i = 0; i < boids.size(); i++) {
			Ple_Agent other = (Ple_Agent) boids.get(i);

			for(int j = 0; j < tail.length; j++){

				float d = tail[j].distance(other.tail[j]);
				if(d > fromDist && d < toDist){


					//Vec3D myVel = velTail[j].copy();
					Vec3 myVel = vel.normalize().scale(scale1).add(tail[j]);

					//Vec3D oVel = other.velTail[j].copy();
					Vec3 oVel = other.vel.normalize().scale(scale2).add(other.tail[j]);

					float d2 = myVel.distance(oVel);
					if(d2 > fromDist && d2 < toDist){
//						vBezier(tail[j], myVel, oVel, other.tail[j]);
					}
				}
			}
		}
	}


	/**
	 * Draw a bezier in Range
	 * @param boids - list of agents
	 * @param fromDist - min distance
	 * @param toDist - max distance
	 * @param scale1 - scale of bezier handle 1
	 * @param scale2 - scale of bezier handle 1
	 */
	public void drawBezierInRange(ArrayList<Ple_Agent> boids, float fromDist, float toDist, float scale1, float scale2){
		for (int i = 0; i < boids.size(); i++) {
			Ple_Agent other = (Ple_Agent) boids.get(i);

			float d = loc.distance(other.loc);
			if(d > fromDist && d < toDist){

				Vec3 myVel = vel.scale(scale1).add(loc);
				Vec3 oVel = other.vel.scale(scale2).add(other.loc);

//				vBezier(loc, myVel, oVel, other.loc);
			}

		}
	}

//	/**
//	 * draw bezier from 4 vectors
//	 * @param v1 - vector 1
//	 * @param v2 - vector 2
//	 * @param v3 - vector 3
//	 * @param v4 - vector 4
//	 */
//	public void vBezier(Vec3D v1, Vec3D v2,Vec3D v3,Vec3D v4){
//		p5.bezier(v1.x, v1.y, v1.z,    v2.x, v2.y, v2.z,   v3.x, v3.y, v3.z,    v4.x, v4.y, v4.z);
//	};

	/**
	 * Wander behaivior in 2D. Based on the script by Daniel Shiffman on Craig Reynolds agents.
	 * @param wanderR - wander Radius.
	 * @param wanderD - wander distance.
	 * @param change - amount of change.
	 */
	public void wander2D(float wanderR, float wanderD, float change) {
		wandertheta += MathUtilities.random(-change,change);     // Randomly change wander theta

		// Now we have to calculate the new location to steer towards on the wander circle
		Vec3 circleloc = vel.normalize().scale(wanderD).add(loc);

		Vec3 circleOffSet = new Vec3(wanderR* Math.cos(wandertheta),wanderR* Math.sin(wandertheta),0);
		Vec3 target = circleloc.add(circleOffSet);
		acc = acc.add(steer(target,false));  // Steer towards it 
	} 
	
	/**
	 * @param wanderR - wander Radius.
	 * @param wanderD - wander distance.
	 * @param amplitude - amplitud of the occilation wave.
	 * @param period - period of the occilation wave.
	 * @param strength - how strong is the force of occilation.
	 */
	public void Occilate2D(float wanderR, float wanderD, float amplitude, float period, float strength) {
		int framecount = (int) (controller.getScheduler().getTime() / 10 /* workaround for bug*/ * 60);
		float occ = (float) (amplitude * Math.cos(Math.PI * 2 * framecount / period));
		wandertheta = occ;     // Randomly change wander theta
		

		// Now we have to calculate the new location to steer towards on the wander circle
		Vec3 circleloc = vel.normalize().scale(wanderD).add(loc);               // Make it relative to boid's location

		// heading XY
		float h = (float) Math.atan2(vel.y, vel.x);
		
		Vec3 circleOffSet = new Vec3(wanderR* Math.cos(wandertheta + h),wanderR* Math.sin(wandertheta + h),0);
		Vec3 target = circleloc.add(circleOffSet);
		//seek(target, strength);
		acc = acc.add(steer(target,false));  // Steer towards it 
	} 
	
	/**
	 * @param wanderR - wander Radius.
	 * @param wanderD - wander distance.
	 * @param amplitude - amplitud of the occilation wave.
	 * @param period - period of the occilation wave.
	 * @param strength - how strong is the force of occilation.
	 * @param occHeight - height amplitud of occilation.
	 */
	public void Occilate3D(float wanderR, float wanderD, float amplitude, float period, float strength, float occHeight) {
		int framecount = (int) (controller.getScheduler().getTime() / 10 /* workaround for bug*/ * 60);
		float occ = (float) (amplitude * Math.cos(Math.PI * 2 * framecount / period));
		wandertheta = occ;     // Randomly change wander theta
		

		// Now we have to calculate the new location to steer towards on the wander circle
		Vec3 circleloc = vel.normalize().scale(wanderD).add(loc);

		// heading XY
		float h = (float) Math.atan2(vel.y, vel.x);
		float occZ = MathUtilities.map(occ, -amplitude, amplitude, -occHeight,occHeight);
		
		Vec3 circleOffSet = new Vec3(wanderR* Math.cos(wandertheta + h),wanderR* Math.sin(wandertheta + h), 0);
		Vec3 target = circleloc.add(circleOffSet);
		target = new Vec3(target.x, target.y, occZ);
		//seek(target, strength);
		acc = acc.add(steer(target,false));  // Steer towards it 
	} 
	
	/**
	 * Spiral movement based on wander logic
	 * @param wanderR - wander Radius.
	 * @param wanderD - wander distance.
	 * @param angle
	 */
	public void spiral2D(float wanderR, float wanderD, float angle) {
		wandertheta += angle;     // Randomly change wander theta

		// Now we have to calculate the new location to steer towards on the wander circle
		Vec3 circleloc = vel.normalize().scale(wanderD).add(loc);

		Vec3 circleOffSet = new Vec3(wanderR* Math.cos(wandertheta),wanderR* Math.sin(wandertheta),0);
		Vec3 target = circleloc.add(circleOffSet);
		acc = acc.add(steer(target,false));  // Steer towards it 
	} 
	
	

	/**
	 * Follows a target.
	 * @param target - vector of target
	 */
	public void seek(Vec3 target, float factor) {
		Vec3 steered = steer(target, false);
		Vec3 toAdd = steered.scale(factor);
		Vec3 newAcc = acc.add(toAdd);
		acc = newAcc;
	}

	/**
	 * Follows a target slowing down at the moment of reaching.
	 * @param target - vector of target.
	 */
	public  void arrive(Vec3 target) {
		acc = acc.add(steer(target, true));
	}


	/**
	 * A method that calculates a steering vector towards a target
	 * Takes a second argument, if true, it slows down as it approaches the target
	 * 
	 * @param target
	 * @param slowdown
	 * @return
	 */
	public Vec3 steer(Vec3 target, boolean slowdown) {
		Vec3 steer; 
		Vec3 desired = target.subtract(loc);  
		float d = desired.length(); 	
		if (d > 0) {
			desired = desired.normalize();
			if ((slowdown) && (d < 100.0f)) 
				desired = desired.scale(maxspeed*(d/100.0f));
			else desired = desired.scale(maxspeed);
			steer = desired.subtract(vel).normalize().scale(maxforce);
		} 
		else {
			steer = new Vec3(0,0,0);
		}
		return steer;
	}	


	/**
	 * Flock method contains a distance check to calculate Cohesion-Alignment-Separation based on Craig Reynolds Flock algorithm
	 * Code based on Daniel Shiffman and Karsten Schmidt (toxi)
	 * 
	 * @param boids
	 * @param desCoh
	 * @param desAli
	 * @param desSep
	 * @param cohScale
	 * @param aliScale
	 * @param sepScale
	 * @param bCoh - activate cohesion
	 * @param cAli - activate alignment
	 * @param bSep - activate separation
	 */
	public void flock(List<Ple_Agent> boids, float desCoh , float desAli, float  desSep , 
			float cohScale, float aliScale,float sepScale){

		//FLOCK : COH-ALI-SEP (true,true,true);
		//COH ---> Flock(true,false,false);
		//ALI ---> Flock(false,true,false); 
		//SEP ---> Flock(false,false,true);

		float cohNeighborDist = desCoh;
		float desiredSeparation = desSep;
		float aliNeighborDist = desAli;

		Vec3 coh = new Vec3(0,0,0);
		Vec3 cohReturn = new Vec3(0,0,0);

		Vec3 sep = new Vec3(0,0,0);
		Vec3 sepReturn = new Vec3(0,0,0);

		Vec3 ali = new Vec3(0,0,0);
		Vec3 aliReturn = new Vec3(0,0,0);

		int count1 = 0;
		int count2 = 0;
		int count3 = 0;

		for (int i = boids.size()-1 ; i >= 0 ; i--) {
			Ple_Agent other = (Ple_Agent) boids.get(i);
			//COHESION:
			if (this != other) {
				if (loc.squaredDistance(other.loc) < cohNeighborDist*cohNeighborDist) {
					coh = coh.add(other.loc); 
					count1++;
				}
			}
			//SEPARATION:
			if (this != other) {
				float d = loc.distance(other.loc);
				// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
				if (d < desiredSeparation) {
					// Calculate vector pointing away from neighbor
					sep = sep.add(loc.subtract(other.loc).normalize().scale(1/d));
					count2++;
				}
			}

			//ALIGNMENT:
			if (this != other) {
				if (loc.squaredDistance(other.loc) < aliNeighborDist*aliNeighborDist) {
					ali = ali.add(other.vel);
					count3++;	
				}
			}		
		}
		//COHESION:
		if (count1 > 0) {
			coh = coh.scale(1.0f/count1);
			cohReturn = steer(coh,false);
		}

		//SEPARATION:
		if (count2 > 0) {
			sep = sep.scale(1.0f/count2);
		}


		if (sep.squaredLength() > 0) {
			sep = sep.normalize().scale(maxspeed).subtract(vel).normalize().scale(maxforce);
		}
		sepReturn = sep;

		//ALIGNMENT:
		if (count3 > 0) {
			ali = ali.scale(1.0f/count3);
		}
		if (ali.squaredLength() > 0) {
			ali = ali.normalize().scale(maxspeed).subtract(vel).normalize().scale(maxforce);
		}
		aliReturn = ali;

		//---------------------------------------------------
		
		acc = acc.add((sepReturn = sepReturn.scale(sepScale)))
				 .add((cohReturn = cohReturn.scale(cohScale)))
				 .add((aliReturn = aliReturn.scale(aliScale)));
	}

	/**
	 * Method to call cohesion only
	 * @param boids
	 * @param desCoh
	 * @param cohScale
	 */
	public void cohesionCall(ArrayList <Ple_Agent> boids, float dist, float scale){		
		flock(boids,dist,0,0,scale,0,0);		
	}

	/**
	 * Method to call alignment only
	 * @param boids
	 * @param dist
	 * @param scale
	 */
	public void alignmentCall(List <Ple_Agent> boids, float dist, float scale){		
		flock(boids,0,dist,0,0,scale,0);		
	}

	/**
	 * Method to call separation only
	 * @param boids
	 * @param dist
	 * @param scale
	 */
	public void separationCall(List <Ple_Agent> boids, float dist, float scale){		
		flock(boids,0,0,dist,0,0,scale);		
	}


	/**
	 * 
	 * @return
	 */
	public Vec3 getLocation(){
		return loc;
	}
	
	/**
	 * get the velocity
	 * @return
	 */
	public Vec3 getVelocity(){
		return vel;
	}
	
	/**
	 * get the acceleration
	 * @return
	 */
	public Vec3 getAcceleration(){
		return acc;
	}
	
	/**
	 * get the location of the anchor
	 * @return
	 */
	public Vec3 getAnchor(){
		return anchor;
	}
	
	/**
	 * method to get the array of tail points
	 * @return
	 */
	public Vec3 [] getTailPoints(){
		return tail;
	}
	
	/**
	 * method to get the array of past velocities
	 * @return
	 */
	public Vec3 [] getVelTailPoints(){
		return velTail;	
	}
	
	/**
	 * get the state of lock boolean
	 * @return
	 */
	public boolean getLockState(){
		return lock;
	}
	
	/**
	 * get the max speed value
	 * @return
	 */
	public float getMaxSpeed(){
		return maxspeed;
	}
	
	/**
	 * get the max force value
	 * @return
	 */
	public float getMaxForce(){
		return maxforce;
	}
	
	/**
	 * set the velocity
	 * @param v
	 */
	public void setVelocity(Vec3 v){
		vel = v;
	}
	/**
	 * set the location
	 * @param v
	 */
	public void setLocation(Vec3 v){
		loc = v;
	}
	/**
	 * set the acceleration
	 * @param v
	 */
	public void setAcceleration(Vec3 v){
		acc = v;
	}
	/**
	 * set the max Speed
	 * @param f
	 */
	public void setMaxspeed(float f){
		maxspeed = f;
	}
	/**
	 * set the max force
	 * @param f
	 */
	public void setMaxforce(float f){
		maxforce = f;
	}
	
	

}
