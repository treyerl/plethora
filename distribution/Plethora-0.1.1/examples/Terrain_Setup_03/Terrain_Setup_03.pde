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
  Vec3D location = new Vec3D(-500, -500, 0);
  //initialize the terrain, specifying columns and rows and cell Size
  pTer = new Ple_Terrain(this, location, 501, 500, 2, 2);

  //load data of height;
  Vec3D [] pts = loadFile("infoData.txt", 2, 2, 10);
  
  pTer.setLocFromData(pts);
}


void draw() {
  background(0);
  
  //define cropping
  pTer.setCropActive(true);
  pTer.crop(-1000,-1000,-1000,1000,500,1000);

  //call some of the functions of the terrain
  stroke(255, 100, 0, 90);
  pTer.drawLines(true, false, false);
}




Vec3D [] loadFile(String file, float distX, float distY, float scaleZ) {
  String [] data;
  data = loadStrings(file);
  float x = 0;
  float y = 0;

  String[] fields2 = split(data[1], ' ');

  int numPts = data.length * fields2.length;

  println(numPts);

  Vec3D [] pts = new Vec3D[numPts];
  int count = 0;

  for (int j = 0; j < data.length; j++) {
    String[] fields = split(data[j], ' ');
    
    for (int i = 0; i < fields.length; i++) {
      float z = float(fields[i]) * scaleZ;
      pts [count] = new Vec3D(x, y, z);
      count ++;

      x += distX;
    }
    y += distY;
    x = 0;
  }
  return pts;
}

