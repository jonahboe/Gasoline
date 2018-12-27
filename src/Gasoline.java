// We will be using Processing's OpenGL wrapper for this project.
import processing.core.PApplet;
// We will also be using the fisica wrapper for JBox2D.
import fisica.*;
// And the arrayList library.
import java.util.ArrayList;

/*****************************************************
 * This is the initial function for this project.
 *****************************************************/
public class Gasoline extends PApplet {

    FWorld world;
    Engine engine;

    FBox piston;
    FDistanceJoint sprocket;

    public static void main(String[] args) {
        PApplet.main("Gasoline");
    }

    public void settings(){

        size(400, 400);
        smooth();

    }

    public void setup(){

        Fisica.init(this);

        world = new FWorld();
        world.setGravity(0, 200);
        world.setEdges();
        world.setEdgesRestitution((float)0.5);

        engine = new Engine(world);
        engine.addSparkPlug(200,265,30,10,"plug1");
        engine.addFuel(200,250,4,50,"fuel");

        /*****************************************************
         * Stuff in the world
         *****************************************************/
        FBox b1 = new FBox(70,10);
        b1.setStatic(true);
        b1.setFill(0);
        b1.setPosition(200,275);
        b1.setDensity(1000);
        FBox b2 = new FBox(10,120);
        b2.setStatic(true);
        b2.setFill(0);
        b2.setPosition(175,225);
        b2.setDensity(1000);
        FBox b3 = new FBox(10,120);
        b3.setStatic(true);
        b3.setFill(0);
        b3.setPosition(225,225);
        b3.setDensity(1000);
        world.add(b1);
        world.add(b2);
        world.add(b3);

        FCircle c1 = new FCircle(100);
        c1.setStatic(false);
        c1.setFill(255,0,0,100);
        c1.setPosition(200,80);
        c1.setDensity(5);
        world.add(c1);
        FCircle c2 = new FCircle(10);
        c2.setStatic(true);
        c2.setFill(255,0,0);
        c2.setPosition(200,80);
        c2.setDensity(1);
        world.add(c2);
        FDistanceJoint j1 = new FDistanceJoint(c1, c2);
        j1.setAnchor1(0, 0);
        j1.setAnchor2(0,0);
        j1.setStrokeWeight(5);
        j1.setCollideConnected(false);
        j1.calculateLength();
        world.add(j1);

        piston = new FBox(39,30);
        piston.setStatic(false);
        piston.setFill(0,255,0,100);
        piston.setPosition(200,220);
        piston.setDensity(12);
        world.add(piston);
        sprocket = new FDistanceJoint(piston, c1);
        sprocket.setAnchor1(0, -10);
        sprocket.setAnchor2(20,20);
        sprocket.setStrokeWeight(5);
        sprocket.calculateLength();
        world.add(sprocket);

    }

    public void draw(){
        background(255);

        // Tell the spark plug when to ignite.
        if (sprocket.getAnchor2X() > 180 && sprocket.getAnchor2X() < 190) {
            if (sprocket.getAnchor2Y() > 97 && sprocket.getAnchor2Y() < 107)
                engine.getSparkPlug("plug1").setOn();
        }
        else
            engine.getSparkPlug("plug1").setOff();

        // Tell the engine when to refill.
        if (piston.getY() < 175) {
            for (int i = engine.getFuelList().size(); i < 12; i++) {
                engine.addFuel(200,250,4,1,"fuel");
            }
        }

        // Update any changes in engine objects.
        engine.updateFuelState();
        engine.removeDeadFire();

        // Take care of updating the world.
        world.step();
        world.draw(this);
    }

}

