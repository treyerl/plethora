//WANDER EXPORT

import processing.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

Ple_Util pu;

PeasyCam cam;

float DIMX = 1200;
float DIMY = 300;
float DIMZ = 300;

void setup() {
  size(1200, 600, OPENGL);
  smooth();
  cam = new PeasyCam(this, 100);
  pu = new Ple_Util(this);

  boids = new ArrayList <Ple_Agent>();

  buildAgents(1200);
}

void draw() {
  background(0);
  buildBox(DIMX,DIMY,DIMZ);
  runAgents();
  
  Ple_Agent pa = (Ple_Agent) boids.get(0);
  
  println("maxspeed" + pa.maxspeed + "," + "maxforce" + pa.maxforce);
  
  if(keyPressed){
  pu.createLocationFile(boids, "_plethora", " ", false);
  pu.createVelocityFile(boids, "_plethora", " ", false);
  }
}


