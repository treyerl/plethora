import peasy.*;
import processing.opengl.*;
import toxi.geom.*;
import toxi.util.*;
import plethora.core.*;

PeasyCam cam;

//1 declare plethora Terrain
Ple_Terrain pTer;

//declare a float array
float [][] heights;

void setup() {
  size(800, 600, OPENGL);

  cam = new PeasyCam(this, 100);
  
  //declare a vector as the location
  Vec3D location = new Vec3D(-500,-500,0);
  //initialize the terrain, specifying columns and rows and cell Size
  pTer = new Ple_Terrain(this, location,  200,200, 5, 5);
  
  
  
  //build a float array buffer and then load as height
  heights = pTer.loadImageToBuffer("image.jpg");
  pTer.loadBufferasHeight(heights, 0  , 40);
}


void draw() {
  background(0);
  
  //call some of the functions of the terrain
  stroke(255,100,0,90);
  pTer.drawLines(true, false, false);
  
  //display a float array as grayScale 
  pTer.drawDataMap(heights, 0,255, 0,255);
  




  noFill();
  stroke(255);
  box(1000);
}

