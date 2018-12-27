import java.util.ArrayList;
import java.util.Random;

import fisica.FWorld;
import fisica.FBody;
import fisica.FCircle;
import fisica.FBox;

import static java.lang.Math.sqrt;

/*****************************************************
 * This is the controller class for all of the engine
 * parts.
 *****************************************************/
public class Engine {

    FWorld myWorld;

    private ArrayList<FFuel> fuels;
    private ArrayList<FFire> fires;
    private ArrayList<FSparkPlug> plugs;

    Engine(FWorld w) {
        myWorld = w;

        fuels = new ArrayList<>();
        fires = new ArrayList<>();
        plugs = new ArrayList<>();
    }

    // These functions add, remove, and get spark plugs from list.
    public void addSparkPlug(float x, float y, float w, float h, String id){
        FSparkPlug p = new FSparkPlug(x,y,w,h,id);
        plugs.add(p);
        myWorld.add(p);
    }
    public void removeSparkPlug(String id){
        for (int i = plugs.size()-1; i >= 0; i--) {
            if (plugs.get(i).id.equals(id)) {
                myWorld.remove(plugs.get(i));
                plugs.remove(plugs.get(i));
            }
        }
    }
    public FSparkPlug getSparkPlug(String id){
        for (int i = plugs.size()-1; i >= 0; i--) {
            if (plugs.get(i).id.equals(id)) {
                return plugs.get(i);
            }
        }
        return null;
    }
    public ArrayList<FSparkPlug> getSparkPlugList(){
        return plugs;
    }

    // These functions add, remove, and get fire list.
    public void addFire(float x, float y, float r, int count, String id){
        for (int i = 0; i < count; i++) {
            FFire f = new FFire(x, y, r, id);
            fires.add(f);
            myWorld.add(f);
        }
    }
    public void removeFire(String id){
        for (int i = plugs.size()-1; i >= 0; i--) {
            if (plugs.get(i).id.equals(id)) {
                myWorld.remove(fires.get(i));
                plugs.remove(fires.get(i));
            }
        }
    }
    public void removeDeadFire(){
        for (int i = fires.size()-1; i >= 0; i--) {
            if (!fires.get(i).isAlive()) {
                myWorld.remove(fires.get(i));
                fires.remove(fires.get(i));
            }
        }
    }
    public ArrayList<FFire> getFireList(){
        return fires;
    }

    // These functions add, remove, and get fuel list. Also update fuel state.
    public void addFuel(float x, float y, float r, int count, String id){
        for (int i = 0; i < count; i++) {
            FFuel f = new FFuel(x, y, r, id);
            fuels.add(f);
            myWorld.add(f);
        }
    }
    public void removeFule(String id){
        for (int i = fuels.size()-1; i >= 0; i--) {
            if (fuels.get(i).id.equals(id)) {
                myWorld.remove(fuels.get(i));
                plugs.remove(fuels.get(i));
            }
        }
    }
    public void updateFuelState(){
        for (int i = fuels.size()-1; i >= 0;  i--) {
            fuels.get(i).update();
            if (fuels.get(i).gotLit()) {
                this.addFire(fuels.get(i).getX(), fuels.get(i).getY(),4, 1, "from_fuel");
                myWorld.remove(fuels.get(i));
                fuels.remove(fuels.get(i));
            }
        }
    }
    public ArrayList<FFuel> getFuelList(){
        return fuels;
    }



    /*****************************************************
     * This is a class for spark plugs.
     *****************************************************/
    public class FSparkPlug extends FBox {

        private String id;
        private boolean on;

        public FSparkPlug(float x, float y, float w, float h, String i) {
            super(w, h);
            id = i;

            this.setNoStroke();
            this.setStatic(true);
            this.setOff();
            this.setPosition(x, y);
        }

        public boolean isOn() {
            return on;
        }

        public boolean isOff() {
            return !on;
        }

        public void setOn() {
            on = true;
            this.setName("SparkPlug_On");
            this.setFill(254, 192, 65);
        }

        public void setOff() {
            on = false;
            this.setName("SparkPlug_Off");
            this.setFill(125);
        }

    }



    /*****************************************************
     * This is a class for fire.
     *****************************************************/
    public class FFire extends FCircle {

        private String id;

        public FFire(float x, float y, float r, String i){
            super(r);
            id = i;

            this.setNoStroke();
            this.setFill(255,0,0);
            this.setPosition(x, y);
            this.setDensity(1000);
            this.setName("fire");

            Random rand = new Random();
            float dx = rand.nextInt(1000);
            float dy = 1000 - dx;
            int ran = rand.nextInt(10);
            if (ran < 5)
                dx *= -1;
            ran = rand.nextInt(10);
            if (ran < 5)
                dy *= -1;
            this.setVelocity(dx,dy);
        }

        private boolean isAlive(){
            float x = this.getVelocityX();
            float y = this.getVelocityY();
            x *= x;
            y *= y;

            double v = sqrt((double)(x+y));
            if (v < 100)
                return false;

            return true;
        }

    }



    /*****************************************************
     * This is a class for fuel, both liquid and gas.
     *****************************************************/
    public class FFuel extends FCircle {

        private String id;
        private boolean isGas;

        public FFuel(float x, float y, float r, String i){
            super(r);
            id = i;

            this.setAsGas();
            this.setNoStroke();
            this.setPosition(x, y);
        }

        public boolean isGas(){ return isGas; }
        public boolean isLiquid(){ return !isGas; }

        public void setAsGas(){
            isGas = true;
            this.setFill(200,80);
            this.setDensity(1);
            this.setRestitution((float)0.9);
            this.setName("fuel_gas");
        }

        public void setAsLiquid(){
            isGas = false;
            this.setFill(43,125,159,180);
            this.setDensity((float)10.0);
            this.setRestitution((float)0.5);
            this.setName("fuel_liquid");
        }

        private void update(){
            if (this.isGas())
                this.addForce(0, -6);
            else {
                ArrayList<FBody> group;
                group = this.getTouching();
                if (group.size() < 2)
                    this.setAsGas();
            }
        }

        private boolean gotLit(){
            if (this.getName().equals("fuel_liquid"))
                return false;

            ArrayList<FBody> group;
            group = this.getTouching();
            for (FBody b : group){
                if (b.getName() != null) {
                    if (b.getName().equals("fire") || b.getName().equals("SparkPlug_On"))
                        return true;
                }
            }

            return false;
        }

    }

}




