import processing.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

PeasyCam cam;

float DIMX = 1200;
float DIMY = 300;
float DIMZ = 300;

void setup() {
  size(1200, 600, OPENGL);
  smooth();
  cam = new PeasyCam(this, 100);

  boids = new ArrayList <Ple_Agent>();

  buildAgents(800);
}

void draw() {
  background(0);
  buildBox(DIMX,DIMY,DIMZ);
  runAgents();
  
  Ple_Agent pa = (Ple_Agent) boids.get(0);
  
  println("maxspeed" + pa.maxspeed + "," + "maxforce" + pa.maxforce);
}


