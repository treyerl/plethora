/**
 * 
 * @author		Jose Sanchez
 * @modified	13/07/2011
 * @version		0.1
 */

package plethora.core;

import java.util.ArrayList;
import toxi.geom.*;
import processing.core.*;

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

	PApplet p5;

	public Vec3D loc;
	public Vec3D vel;
	public Vec3D acc;

	public Vec3D anchor;
	public boolean lock;

	public float vx,vy,vz;

	public float maxspeed = 4;
	public float maxforce = 0.05f;

	public boolean showSep = false;
	public boolean showAli = false;
	public boolean showCoh = false;

	public Vec3D [] tail = new Vec3D [1];
	public Vec3D [] velTail = new Vec3D [1];

	public float wandertheta = 0.0f;

	public ArrayList <Vec3D> trail;


	/**
	 * 
	 * Basic constructor. Provide the PApplet refernce. (type 'this'). and the initial location of the agent. 
	 * By default agents will start without any initial velocity.
	 * 
	 * @param _p5
	 * @param _loc
	 */
	public Ple_Agent(PApplet _p5, Vec3D _loc){
		loc = _loc;	
		p5 = _p5;

		vel = new Vec3D(0,0,0);
		acc = new Vec3D();	

		trail = new ArrayList<Vec3D>();

		anchor = new Vec3D();
		lock = false;

	}
	
	public void trolling(){
		
	}

	/**
	 * Method to update location. Addes the acceleration (acc) to the velocity which gets passed to the location. 
	 * The movement has momentum based on the acumulated velocity. Also has a maxspeed Value, that can be changed with
	 * the getter and setter methods. 
	 */
	public void update() {
		// Update velocity
		vel.addSelf(acc);
		// Limit speed
		vel.limit(maxspeed);
		loc.addSelf(vel);
		// Reset accelertion to 0 each cycle
		acc.clear();
	}

	/**
	 * Allow to make2D. Will collapse the z values of the locations to the value provided.
	 * @param zValue - Value for location z.
	 */
	public void flatten (float zValue){
		loc.z = zValue;
		//vel.z = zValue;
	}

	/**
	 * Allows to flatten the velocity in order to get a 2D movement.
	 */
	public void flatVel(){
		acc = new Vec3D(acc.x,acc.y,0);
		vel = new Vec3D(vel.x,vel.y,0);
	}

	/**
	 * Method to update the location only by adding the velocity to the location. This simple update allows for more 
	 * 'mechanical' movements, not having the momentum of acceleration.
	 */
	public void updateSimple() {
		// Limit speed
		vel.limit(maxspeed);
		loc.addSelf(vel);
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
	public ArrayList terrainPointsInRange(Ple_Terrain ter, float minRange,  float maxRange,  float minAngle,  float maxAngle){

		ArrayList pts = new ArrayList();

		for (int i = 0; i < ter.COLS; i++) {
			for (int j = 0; j < ter.ROWS; j++) {

				Vec3D ptLoc = new Vec3D(ter.field[i][j].x,ter.field[i][j].y,loc.z);


				float d = loc.distanceTo(ptLoc);
				Vec3D dif = ptLoc.sub(loc);
				dif.normalize();

				Vec3D v = vel.copy();
				v.normalize();

				float angle = p5.degrees(v.angleBetween(dif));

				if(d < maxRange && d > minRange ) {
					if(angle < maxAngle && angle > minAngle) {

						pts.add(ter.field[i][j]);
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
	 * @param dataMap - 2 dimentional array that contains the info to be associated to the points.
	 * @param minRange - minimum distance
	 * @param maxRange - maximum distance
	 * @param minAngle - minimum angle
	 * @param maxAngle - maximum angle
	 * @return
	 */
	public ArrayList terrainIndexInRange(Ple_Terrain ter, float [][] dataMap, float minRange,  float maxRange,  float minAngle,  float maxAngle){

		ArrayList pts = new ArrayList();

		for (int i = 0; i < ter.COLS; i++) {
			for (int j = 0; j < ter.ROWS; j++) {

				Vec3D ptLoc = new Vec3D(ter.field[i][j].x,ter.field[i][j].y,loc.z);

				float d = loc.distanceTo(ptLoc);
				Vec3D dif = ptLoc.sub(loc);
				dif.normalize();

				Vec3D v = vel.copy();
				v.normalize();

				float angle = p5.degrees(v.angleBetween(dif));

				if(d < maxRange && d > minRange ) {
					if(angle < maxAngle && angle > minAngle) {

						Ple_Node pn = new Ple_Node(ter.field[i][j]);
						pn.setIndex(i,j);

						pn.setData(dataMap[i][j]);

						pts.add(pn);
					}
				}
			}
		}
		return pts;
	}

	/**
	 * Returns the location of the node with the highest data.
	 * @param index
	 * @return
	 */
	public Vec3D getPointWithHighestData(ArrayList index){
		Vec3D sum = new Vec3D();
		int count = 0;
		for(int i = 0; i < index.size(); i++){

			Ple_Node node = (Ple_Node) index.get(i);

			float pheAmount = p5.map(node.data, 0,255,0,1);
			Vec3D dif = node.loc.sub(loc);
			//dif.normalize();
			dif.scaleSelf(pheAmount);

			sum.addSelf(dif);
			count++;

		}
		if(count > 0){
			sum.scaleSelf(1.0f/count);
		}
		sum.addSelf(loc);
		return sum;
	}

	/**
	 * activates a spring at the specified location
	 * @param where
	 */
	public void dropAnchor(Vec3D where){
		//update 
		if(!lock)anchor = where.copy();
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
		float gravity = 0.0f; //0.6f
		float mass = 1.0f; // 2.0

		if(lock){
			float forceX = (anchor.x - loc.x) * stiffness;
			float ax = forceX / mass;
			vx = damping * (vx + ax);
			loc.x += vx;
			float forceY = (anchor.y - loc.y) * stiffness;
			forceY += gravity;
			float ay = forceY / mass;
			vy = damping * (vy + ay);
			loc.y += vy;
			float forceZ = (anchor.z - loc.z) * stiffness;
			forceZ += gravity;
			float az = forceZ / mass;
			vz = damping * (vz + az);
			loc.z += vz;

			float d = loc.distanceTo(anchor);

			if(d < threshold){
				if(show){
					vLine(loc,anchor);	
				}
			}
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
				Vec3D v1 = (Vec3D) trail.get(i);
				Vec3D v2 = (Vec3D) trail.get(i-1);

				float d = v1.distanceTo(v2);
				if(d < thresh){
					vLine(v1,v2);
				}
			}
		}
	}

	/**
	 * This method allows for dropping a location vector to a list in order to generate a trail.
	 * @param every - drop vector every so many frames
	 * @param limit - limit the maximum number of drops.
	 */
	public void dropTrail(int every, int limit){
		if(trail.size() < limit){
			if(p5.frameCount % every == 0){
				trail.add(loc.copy());
			}
		}
	}


	/**
	 * Method to calculate the normal to a spline based on the individual line segments of the spline. 
	 * It returns the closest normal. 
	 * @param sp - Spline3D to test
	 * @param v - vector to test
	 * @return
	 */
	public Vec3D closestNormalToSpline(Spline3D sp, Vec3D v){

		Vec3D target = null;
		//Vec3D dir = null;
		float cloDist = 1000000; 

		for (int i = 1; i < sp.pointList.size(); i++) {

			Vec3D a = (Vec3D) sp.pointList.get(i);
			Vec3D b = (Vec3D) sp.pointList.get(i-1);

			Vec3D normal = getNormalPoint(v,a,b);

			float da = normal.distanceTo(a);
			float db = normal.distanceTo(b);
			Vec3D line = b.sub(a);

			if (da + db > line.magnitude()+1) {
				normal = b.copy();
			}

			float d = v.distanceTo(normal);
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
	 * @param sp - Spline3D to test
	 * @param v - vector to test
	 * @param amountOfDir - amount of directionality added to the normal vector.
	 * @return
	 */
	public Vec3D closestNormalandDirectionToSpline(Spline3D sp, Vec3D v, float amountOfDir){

		Vec3D target = null;
		Vec3D dir = null;
		float cloDist = 1000000; 

		for (int i = 1; i < sp.pointList.size(); i++) {

			Vec3D a = (Vec3D) sp.pointList.get(i);
			Vec3D b = (Vec3D) sp.pointList.get(i-1);

			Vec3D normal = getNormalPoint(v,a,b);

			float da = normal.distanceTo(a);
			float db = normal.distanceTo(b);
			Vec3D line = b.sub(a);

			if (da + db > line.magnitude()+1) {
				normal = b.copy();
			}

			float d = v.distanceTo(normal);
			if (d < cloDist) {
				cloDist = d;
				target = normal;

				dir = line.copy();
				dir.normalize();
				dir.scaleSelf(amountOfDir);
			}	

			target.addSelf(dir);

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
	public  Vec3D getNormalPoint(Vec3D p, Vec3D a, Vec3D b) {

		Vec3D ap = p.sub(a);

		Vec3D ab = b.sub(a);
		ab.normalize();
		// Project vector "diff" onto line by using the dot product
		ab.scaleSelf(ap.dot(ab));
		Vec3D normalPoint = a.add(ab);
		return normalPoint;
	}

	/**
	 * Calculates the future Location of the Agent.
	 * @param len - Length of the projection
	 * @return
	 */
	public Vec3D futureLoc(float len){
		Vec3D v = vel.copy();
		v.normalize();
		v.scaleSelf(len);

		v.addSelf(loc);

		return v;
	}

	/**
	 * Calculates the update of the tail.
	 * @param rate - updates the tail every so many frames, allows for longer tails, scattered.
	 */
	public void updateTail(int rate){	
		if(p5.frameCount % rate == 0){
			for (int i = 0; i < tail.length-1; i++){
				tail[i] = tail[i+1];
			}
			tail[tail.length-1] = loc.copy();
		}
	}

	/**
	 * Calculates the update of the velocity tail.
	 * @param rate  - updates the tail every so many frames, allows for longer tails, scattered.
	 */
	public void updateVelTail(int rate){	
		if(p5.frameCount % rate == 0){
			for (int i = 0; i < velTail.length-1; i++){
				velTail[i] = velTail[i+1];
			}
		}
	}

	/**
	 * Initialize Tail. Call in the setup.
	 * @param size - define the size of the Tail.
	 */
	public void initTail(int size){
		tail = new Vec3D [size];
		for(int i = 0; i < size; i++){
			tail[i] = loc.copy();
		}
	}

	/**
	 * Initialize Velocity Tail. Call in the setup.
	 * @param size  - define the size of the Tail.
	 */
	public void initVelTail(int size){
		velTail = new Vec3D [size];
		for(int i = 0; i < size; i++){
			velTail[i] = vel.copy();
		}
	}


	/**
	 * Display a point on Location.
	 */
	public void displayPoint(){
		vPt(loc);
	}

	/**
	 * Display a Line with the direction of the agent.
	 * @param len - length of the line
	 */
	public void displayDir(float len){
		Vec3D v = vel.copy();
		v.normalize();
		v.scaleSelf(len);
		v.addSelf(loc);	
		vLine(loc, v);
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

			float r = PApplet.map(i, 0, tail.length-1, r1,r2);
			float g = PApplet.map(i, 0, tail.length-1, g1,g2);
			float b = PApplet.map(i, 0, tail.length-1, b1,b2);

			float a = PApplet.map(i, 0, tail.length-1, al1,al2);

			float s = PApplet.map(i, 0, tail.length-1, s1,s2);

			p5.strokeWeight(s);
			p5.stroke(r,g,b, a);
			vPt(tail[i]);

			//vLine(tail[i], tail[i-1]);
		}
	}


	/**
	 * Vector Point
	 * @param v
	 */
	public void vPt(Vec3D v){
		p5.point(v.x,v.y,v.z);
	}


	/**
	 * Add a force to the acceleration vector.
	 * @param x - force in X
	 * @param y - force in Y
	 * @param z - force in Z
	 */
	public void addForce(float x, float y, float z){
		Vec3D v = new Vec3D(x,y,z);
		acc.addSelf(v);
	}

	/**
	 * Vector Line
	 * @param v1 - Vector 1
	 * @param v2 - Vector 2
	 */
	public void vLine(Vec3D v1, Vec3D v2){
		p5.line(v1.x,v1.y,v1.z, v2.x,v2.y,v2.z);
	}

	/**
	 * Boundary for agents. Agents will reappear in the opposite side. Wrap-Space.
	 * @param dX - bound for X, from -dX to dX.
	 * @param dY - bound for Y, from -dY to dY.
	 * @param dZ - bound for Z, from -dZ to dZ.
	 */
	public void wrapSpace(float dX, float dY, float dZ) {
		if (loc.x < -dX) loc.x = dX;
		if (loc.y < -dY) loc.y = dY;
		if (loc.z < -dZ) loc.z = dZ;
		if (loc.x > dX) loc.x = -dX;
		if (loc.y > dY) loc.y = -dY;
		if (loc.z > dZ) loc.z = -dZ;
	}

	/**
	 * bounce on borders.
	 * @param dX - bound for X, from -dX to dX.
	 * @param dY - bound for Y, from -dY to dY.
	 * @param dZ - bound for Z, from -dZ to dZ.
	 */
	public void bounceSpace(float dX, float dY, float dZ) {
		if (loc.x <= -dX) {
			vel.x *= -1;
			loc.x = -dX;
		}
		if (loc.y <= -dY) {
			vel.y *= -1;
			loc.y =  -dY;
		}
		if (loc.z <= -dZ) {
			vel.z *= -1;
			loc.z = -dZ;
		}
		if (loc.x >= dX) {
			vel.x *= -1;
			loc.x = dX;
		}
		if (loc.y >= dY) {
			vel.y *= -1;
			loc.y =  dY;

		}
		if (loc.z >= dZ) {
			vel.z *= -1;
			loc.z = dZ;
		}
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
			float d = loc.distanceTo(pa.loc);
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

			float d = loc.distanceTo(other.loc);
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
				float d = tail[j].distanceTo(other.tail[j]);
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

				float d = tail[j].distanceTo(other.tail[j]);
				if(d > fromDist && d < toDist){


					//Vec3D myVel = velTail[j].copy();
					Vec3D myVel = vel.copy();
					myVel.normalize();
					myVel.scaleSelf(scale1);
					myVel.addSelf(tail[j]);

					//Vec3D oVel = other.velTail[j].copy();
					Vec3D oVel = other.vel.copy();
					oVel.normalize();
					oVel.scaleSelf(scale2);
					oVel.addSelf(other.tail[j]);

					float d2 = myVel.distanceTo(oVel);
					if(d2 > fromDist && d2 < toDist){
						vBezier(tail[j], myVel, oVel, other.tail[j]);
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

			float d = loc.distanceTo(other.loc);
			if(d > fromDist && d < toDist){

				Vec3D myVel = vel.copy();
				myVel.scaleSelf(scale1);
				myVel.addSelf(loc);

				Vec3D oVel = other.vel.copy();
				oVel.scaleSelf(scale2);
				oVel.addSelf(other.loc);


				vBezier(loc, myVel, oVel, other.loc);
			}

		}
	}

	/**
	 * draw bezier from 4 vectors
	 * @param v1 - vector 1
	 * @param v2 - vector 2
	 * @param v3 - vector 3
	 * @param v4 - vector 4
	 */
	public void vBezier(Vec3D v1, Vec3D v2,Vec3D v3,Vec3D v4){
		p5.bezier(v1.x, v1.y, v1.z,    v2.x, v2.y, v2.z,   v3.x, v3.y, v3.z,    v4.x, v4.y, v4.z);
	};

	/**
	 * Wander behaivior in 2D. Based on the script by Daniel Shiffman on Craig Reynolds agents.
	 * @param wanderR - wander Radius.
	 * @param wanderD - wander distance.
	 * @param change - amount of change.
	 */
	public void wander2D(float wanderR, float wanderD, float change) {
		wandertheta += p5.random(-change,change);     // Randomly change wander theta

		// Now we have to calculate the new location to steer towards on the wander circle
		Vec3D circleloc = vel.copy();  // Start with velocity
		circleloc.normalize();            // Normalize to get heading
		circleloc.scaleSelf(wanderD);          // Multiply by distance
		circleloc.addSelf(loc);               // Make it relative to boid's location

		Vec3D circleOffSet = new Vec3D(wanderR* PApplet.cos(wandertheta),wanderR* PApplet.sin(wandertheta),0);
		Vec3D target = circleloc.add(circleOffSet);
		acc.addSelf(steer(target,false));  // Steer towards it 
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
		Vec3D circleloc = vel.copy();  // Start with velocity
		circleloc.normalize();            // Normalize to get heading
		circleloc.scaleSelf(wanderD);          // Multiply by distance
		circleloc.addSelf(loc);               // Make it relative to boid's location

		Vec3D circleOffSet = new Vec3D(wanderR* PApplet.cos(wandertheta),wanderR* PApplet.sin(wandertheta),0);
		Vec3D target = circleloc.add(circleOffSet);
		acc.addSelf(steer(target,false));  // Steer towards it 
	} 
	
	

	/**
	 * Follows a target.
	 * @param target - vector of target
	 */
	public void seek(Vec3D target, float factor) {
		Vec3D v = steer(target,false);
		v.scaleSelf(factor);
		acc.addSelf(v);
	}

	/**
	 * Follows a target slowing down at the moment of reaching.
	 * @param target - vector of target.
	 */
	public  void arrive(Vec3D target) {
		acc.addSelf(steer(target,true));
	}


	/**
	 * A method that calculates a steering vector towards a target
	 * Takes a second argument, if true, it slows down as it approaches the target
	 * 
	 * @param target
	 * @param slowdown
	 * @return
	 */
	public Vec3D steer(Vec3D target, boolean slowdown) {
		Vec3D steer; 
		Vec3D desired = target.sub(loc);  
		float d = desired.magnitude(); 	
		if (d > 0) {
			desired.normalize();
			if ((slowdown) && (d < 100.0f)) desired.scaleSelf(maxspeed*(d/100.0f));
			else desired.scaleSelf(maxspeed);
			steer = desired.sub(vel).limit(maxforce);
		} 
		else {
			steer = new Vec3D();
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
	public void flock(ArrayList <Ple_Agent> boids, float desCoh , float desAli, float  desSep , 
			float cohScale, float aliScale,float sepScale){

		//FLOCK : COH-ALI-SEP (true,true,true);
		//COH ---> Flock(true,false,false);
		//ALI ---> Flock(false,true,false); 
		//SEP ---> Flock(false,false,true);

		float cohNeighborDist = desCoh;
		float desiredSeparation = desSep;
		float aliNeighborDist = desAli;

		Vec3D coh = new Vec3D();
		Vec3D cohReturn = new Vec3D();

		Vec3D sep = new Vec3D();
		Vec3D sepReturn = new Vec3D();

		Vec3D ali = new Vec3D();
		Vec3D aliReturn = new Vec3D();

		int count1 = 0;
		int count2 = 0;
		int count3 = 0;

		for (int i = boids.size()-1 ; i >= 0 ; i--) {
			Ple_Agent other = (Ple_Agent) boids.get(i);
			//COHESION:
			if (this != other) {
				if (loc.distanceToSquared(other.loc) < cohNeighborDist*cohNeighborDist) {
					coh.addSelf(other.loc); 
					count1++;
				}
			}
			//SEPARATION:
			if (this != other) {
				float d = loc.distanceTo(other.loc);
				// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
				if (d < desiredSeparation) {
					// Calculate vector pointing away from neighbor
					Vec3D diff = loc.sub(other.loc);
					diff.normalizeTo(1.0f/d);
					sep.addSelf(diff);
					count2++;
				}
			}

			//ALIGNMENT:
			if (this != other) {
				if (loc.distanceToSquared(other.loc) < aliNeighborDist*aliNeighborDist) {
					ali.addSelf(other.vel);
					count3++;	
				}
			}		
		}
		//COHESION:
		if (count1 > 0) {
			coh.scaleSelf(1.0f/count1);
			cohReturn = steer(coh,false);
		}

		//SEPARATION:
		if (count2 > 0) {
			sep.scaleSelf(1.0f/count2);
		}


		if (sep.magSquared() > 0) {
			sep.normalizeTo(maxspeed);
			sep.subSelf(vel);
			sep.limit(maxforce);
		}
		sepReturn = sep;

		//ALIGNMENT:
		if (count3 > 0) {
			ali.scaleSelf(1.0f/count3);
		}
		if (ali.magSquared() > 0) {
			ali.normalizeTo(maxspeed);
			ali.subSelf(vel);
			ali.limit(maxforce);
		}
		aliReturn = ali;

		//---------------------------------------------------

		sepReturn.scaleSelf(sepScale);
		cohReturn.scaleSelf(cohScale);
		aliReturn.scaleSelf(aliScale);

		acc.addSelf(sepReturn);
		acc.addSelf(aliReturn);
		acc.addSelf(cohReturn);	
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
	public void alignmentCall(ArrayList <Ple_Agent> boids, float dist, float scale){		
		flock(boids,0,dist,0,0,scale,0);		
	}

	/**
	 * Method to call separation only
	 * @param boids
	 * @param dist
	 * @param scale
	 */
	public void separationCall(ArrayList <Ple_Agent> boids, float dist, float scale){		
		flock(boids,0,0,dist,0,0,scale);		
	}


	/**
	 * 
	 * @return
	 */
	public Vec3D getLocation(){
		return loc;
	}
	
	/**
	 * get the velocity
	 * @return
	 */
	public Vec3D getVelocity(){
		return vel;
	}
	
	/**
	 * get the acceleration
	 * @return
	 */
	public Vec3D getAcceleration(){
		return acc;
	}
	
	/**
	 * get the location of the anchor
	 * @return
	 */
	public Vec3D getAnchor(){
		return anchor;
	}
	
	/**
	 * method to get the array of tail points
	 * @return
	 */
	public Vec3D [] getTailPoints(){
		return tail;
	}
	
	/**
	 * method to get the array of past velocities
	 * @return
	 */
	public Vec3D [] getVelTailPoints(){
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
	public void setVelocity(Vec3D v){
		vel = v;
	}
	/**
	 * set the location
	 * @param v
	 */
	public void setLocation(Vec3D v){
		loc = v;
	}
	/**
	 * set the acceleration
	 * @param v
	 */
	public void setAcceleration(Vec3D v){
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
