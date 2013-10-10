//FUNCTIONS

void runAgents() {
  for (Ple_Agent pa : boids) {
    
    stroke(255,0,169,90);
    strokeWeight(1);
    
    pa.wander2D(200,20,0.55);
    
    pa.update();
    pa.wrapSpace(600, 600, 150);
    
    
    Vec3D n = pTerr.getLocInGrid(pa.loc);
    stroke(255,0,255);
    strokeWeight(10);
    point(n.x,n.y,n.z);
    strokeWeight(1);
    pa.vLine(pa.loc,n);
    
    
    int ptX = pTerr.xLocInGrid(pa.loc);
    int ptY = pTerr.yLocInGrid(pa.loc);
    
    pTerr.addPointZ(ptX, ptY, 1);
    
    if(data[ptX][ptY] < 255 && data[ptX][ptY] > 0){
      data[ptX][ptY] -= 5;
    }
    println(data[ptX][ptY]);
    
    stroke(0,255,169);
    strokeWeight(2);
 
    
    pa.setMaxspeed(2);
    //pa.setMaxforce(0.05);
  
    strokeWeight(3);
    stroke(255, 0, 0);
    pa.displayPoint();
    
    strokeWeight(1);
    stroke(255);
    pa.displayDir(pa.vel.magnitude()*5);
    
  }
}

void buildAgents(int pop) {
  for (int i = 0; i < pop; i++) {
    Vec3D v = new Vec3D (random(-DIMX/2, DIMX/2), random(-DIMY/2, DIMY/2), random(-DIMZ/2, DIMZ/2));
    Ple_Agent pa = new Ple_Agent(this, v);

    Vec3D initialVelocity = new Vec3D (random(-1, 1), random(-1, 1), random(-1, 1));
   // Vec3D initialVelocity = new Vec3D (0,0,0);
    pa.setVelocity(initialVelocity);
    
    pa.initTail(30);
    
    boids.add(pa);
    
    
  }
}

void buildBox(float x,float y, float z) {
  noFill();
  stroke(100);
  strokeWeight(1);
  pushMatrix();
  scale(x,y,z);
  box(1);
  popMatrix();
}

