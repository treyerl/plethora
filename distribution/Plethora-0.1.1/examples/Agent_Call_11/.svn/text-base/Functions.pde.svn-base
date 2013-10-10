//FUNCTIONS

void runAgents() {
  for (Ple_Agent pa : boids) {

    stroke(255, 0, 169, 90);
    strokeWeight(1);

    //pa.wander2D(200, 20, 0.3);
    //pa.flock(boids, 20, 80*80, 60*60, 1.5, 1.5, 1.5);

    Vec3D fLoc = pa.futureLoc(50);
    //stroke(255, 0, 255, 90);
    //pa.vLine(fLoc, pa.loc);


    Vec3D n = pTerr.getLocInGrid(fLoc);
    stroke(255, 0, 255);
    strokeWeight(10);
    //point(n.x,n.y,n.z);
    strokeWeight(1);
    //pa.vLine(fLoc,n);
    
    pa.addForce(0.2,0,0);
    //pa.seek(n, 0.4);

    //pa.dropTrail(1, 800);

    stroke(0, 255, 100, 90);
    strokeWeight(1);
    //pa.drawTrail(20);
    
    pa.updateTail(1);
    pa.displayTailPoints(0,0,  255,100,   100,255,  100,100,  1,1);
    
    pa.customWalk(-60,60,2,30);

    if (frameCount > 200) {
      stroke(0, 0, 255,90);
      //pa.dropAnchor( 0.5, 0.1, 300, true);
    }

    pa.flatten(0);

    pa.updateSimple();
    //pa.update();
    pa.wrapSpace(DIMX/2, DIMY/2, DIMZ/2);

    stroke(0, 255, 169);
    strokeWeight(2);

    pa.vel.scaleSelf(1.5);
    pa.setMaxspeed(3);
    //pa.setMaxforce(0.05);

    strokeWeight(2);
    stroke(255, 0, 0);
    pa.displayPoint();

    strokeWeight(1);
    stroke(255);
    //pa.displayDir(pa.vel.magnitude()*5);
  }
}

void buildAgents(int pop) {
  for (int i = 0; i < pop; i++) {
    Vec3D v = new Vec3D (random(-DIMX/2, DIMX/2), random(-DIMY/2, DIMY/2), random(-DIMZ/2, DIMZ/2));
    Ple_Agent pa = new Ple_Agent(this, v);

    Vec3D initialVelocity = new Vec3D (random(-1, 1), random(-1, 1), random(-1, 1));
    // Vec3D initialVelocity = new Vec3D (0,0,0);
    pa.setVelocity(initialVelocity);

    pa.initTail(40);

    boids.add(pa);
  }
}

void buildBox(float x, float y, float z) {
  noFill();
  stroke(100);
  strokeWeight(1);
  pushMatrix();
  scale(x, y, z);
  box(1);
  popMatrix();
}

