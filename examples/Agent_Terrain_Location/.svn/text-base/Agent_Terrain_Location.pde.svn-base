/**
 * Simple call for agent population with a flocking behavior based on Craig Reynolds
 * more info at www.plethora-project.com
 * requires toxiclibs and peasycam
 */

/* 
 * Copyright (c) 2011 Jose Sanchez
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import processing.opengl.*;
import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

//using peasycam
PeasyCam cam;

//declare plethora Terrain
Ple_Terrain pTer;

float DIMX = 1000;
float DIMY = 1000;
float DIMZ = 1000;

int pop = 100;

void setup() {
  size(1200, 600, OPENGL);
  smooth();
  cam = new PeasyCam(this, 600);

  //initialize the arrayList
  boids = new ArrayList <Ple_Agent>();

  //declare a vector as the location
  Vec3D location = new Vec3D(-DIMX/2, -DIMY/2, 0);
  //initialize the terrain, specifying columns and rows and cell Size
  pTer = new Ple_Terrain(this, location, 50, 50, 20, 20);

  //call some of the functions
  pTer.noiseHeight(0, 200);

  for (int i = 0; i < pop; i++) {

    //set the initial location as 0,0,0
    Vec3D v = new Vec3D (0, 0, 200);
    //create the plethora agents!
    Ple_Agent pa = new Ple_Agent(this, v);

    //generate a random initial velocity
    Vec3D initialVelocity = new Vec3D (random(-1, 1), random(-1, 1), random(-1, 1));

    //set some initial values:
    //initial velocity
    pa.setVelocity(initialVelocity);
    //initialize the tail
    pa.initTail(5);

    //add the agents to the list
    boids.add(pa);
  }
}

void draw() {
  background(235);

  //call some of the functions of the terrain
  stroke(0, 90);
  strokeWeight(2);
  pTer.display();

  

  //draw lines (horizontal, vertical or diagonal... or all of them!) 
  stroke(0, 90);
  strokeWeight(1);
  pTer.drawLines(true, true, false);

  stroke(0, 90);
  strokeWeight(1);
  noFill();
  rect(-DIMX/2, -DIMY/2, DIMX, DIMY);

  for (Ple_Agent pa : boids) {

    //wander: inputs: circleSize, distance, variation in radians
    pa.wander2D(5, 0, PI);

    //define the boundries of the space as bounce
    pa.bounceSpace(DIMX/2, DIMY/2, DIMY/2);

    //update the tail info every frame (1)
    pa.updateTail(1);

    //display the tail interpolating 2 sets of values:
    //R,G,B,ALPHA,SIZE - R,G,B,ALPHA,SIZE
    pa.displayTailPoints(0, 0, 0, 0, 1, 0, 0, 0, 255, 1);

    //set the max speed of movement:
    pa.setMaxspeed(3);
    //pa.setMaxforce(0.05);

    //update agents location based on past calculations
    pa.update();
    
    //get location in terrain (Z projection) RED
    Vec3D n = pTer.getLocInGrid(pa.loc);
    stroke(255,0,0,90);
    strokeWeight(4);
    point(n.x,n.y,n.z);
    strokeWeight(1);
    pa.vLine(pa.loc,n);
    
    //get closest node in terrain (distance check) BLUE
    Vec3D n2 = pTer.closestNode(pa.loc);
    stroke(0,80,255,90);
    strokeWeight(4);
    point(n2.x,n2.y,n2.z);
    strokeWeight(1);
    pa.vLine(pa.loc,n2);


    //Display the location of the agent with a point
    strokeWeight(2);
    stroke(0);
    pa.displayPoint();

    //Display the direction of the agent with a line
    strokeWeight(1);
    stroke(100, 90);
    pa.displayDir(pa.vel.magnitude()*3);
  }
}

