//Terrain closest point and point on grid

import processing.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

Ple_Util pu;
Ple_Terrain pTerr;

PeasyCam cam;

float [][] data;

float DIMX = 1200;
float DIMY = 1200;
float DIMZ = 300;

void setup() {
  size(1200, 600, OPENGL);
  //size(1200, 600, P3D);
  smooth();
  cam = new PeasyCam(this, 100);
  
  pu = new Ple_Util(this);
  
  Vec3D origin = new Vec3D(-600,-600,-150);
  pTerr = new Ple_Terrain(this,origin, 60,60,20,20);
  //pTerr.noiseHeight(0,200);

  boids = new ArrayList <Ple_Agent>();

  buildAgents(10);
  
  data = pTerr.loadImageToBuffer("dot2.jpg");
  
  
}

void draw() {
  background(0);
  buildBox(DIMX,DIMY,DIMZ);
  runAgents();
  
  stroke(0,255,0);
  strokeWeight(2);
  pTerr.display();
  
  pTerr.drawDataMap(data, 0,255, 0,255);


}


