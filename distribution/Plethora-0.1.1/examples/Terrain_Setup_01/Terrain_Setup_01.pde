import peasy.*;
import processing.opengl.*;
import toxi.geom.*;
import toxi.util.*;
import plethora.core.*;

PeasyCam cam;

//1 declare plethora Terrain
Ple_Terrain pTer;

void setup() {
  size(800, 600, OPENGL);

  cam = new PeasyCam(this, 100);
  
  //declare a vector as the location
  Vec3D location = new Vec3D(-500,-500,0);
  //initialize the terrain, specifying columns and rows and cell Size
  pTer = new Ple_Terrain(this, location,  200,200, 5, 5);
  
  //call some of the functions
  pTer.noiseHeight(0,100);
}


void draw() {
  background(0);
  
  //call some of the functions of the terrain
  stroke(255,100,0);
  pTer.display();




  noFill();
  stroke(255);
  box(1000);
}

