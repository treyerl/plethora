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
  pTer = new Ple_Terrain(this, location,  10,10, 100, 100);
  
  //call some of the functions
  pTer.noiseHeight(0,600);
  
  pTer.initTiles();
}


void draw() {
  background(0);
  lights();
  
  //call some of the functions of the terrain
  stroke(255,100,0);
  pTer.display();
  //pTer.addPointZ(int(random(20)), int(random(20)), 1); 

  //pTer.setPointZ(20,20,500);
  
  
  noStroke();
  stroke(0,20);
  fill(255);
  //pTer.displayTiles();
  pTer.setAllTilesRecursion(1);
  pTer.setTileRecursion(2,2, 6);
  pTer.setAllTilesHeight(0.2);
  pTer.setAllTilesWeights(0.5,   0.5,   0.5,   0.5);
  pTer.setTileWeights(6,6, 0.2,   0.1,   0.7,   0.1);
  pTer.setTileHeight(6,6, 1);
  pTer.drawRecursiveTiles();


  noFill();
  stroke(255);
  box(1000);
}

