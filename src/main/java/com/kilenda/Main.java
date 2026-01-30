package com.kilenda;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.kilenda.FighterHandler;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseMotionListener;

public class Main {
    static int fps = 24;
    static int frameDelay = 1000 / fps;
    static int tickN = 0;
    static FighterHandler handler;
    static Thrower mouseEntity;

    static double findPointAngle (double dx, double dy){
        double hyp = Math.sqrt(dx * dx + dy * dy);
        double absoluteAngle = Math.toDegrees(Math.asin(Math.abs(dy) / hyp));
        double theta = 0;

        if (dx > 0 && dy > 0){
            theta = absoluteAngle;
        }else if (dx < 0 && dy > 0){
            theta = 180 - absoluteAngle;
        }else if (dx < 0 && dy < 0){
            theta = 180 + absoluteAngle ;
        }else if (dx > 0 && dy < 0){
            theta = -absoluteAngle;
        }
        return theta;
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("tTEst Simulatr");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BattleArenaPanel arenaPanel = new BattleArenaPanel();
        handler = new FighterHandler(arenaPanel);
        frame.add(arenaPanel);
        frame.pack();
        /*for (int i = 1; i < 5; i++){
            handler.spawnRandomNPC();
        }
        handler.spawnTeam(3);
        handler.spawnTeam(3);*/
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Insets insets = frame.getInsets();
        frame.setSize(arenaPanel.canvasWidth + insets.left + insets.right,
                arenaPanel.canvasHeight + insets.top + insets.bottom);

        new javax.swing.Timer(frameDelay, e -> {
            arenaPanel.updateUnits();
            arenaPanel.repaint();
            tickN++;
        }).start();


        /*Thrower mouseEntity = new Thrower(0, 0, new Color(0,0,0, 0));
        handler.addModifiers(mouseEntity);
        mouseEntity.size = 35;
       mouseEntity.weight = 2.5;
        mouseEntity.inGame = true;
        BattleArenaPanel.entities.add(mouseEntity);*/

        mouseEntity = (Thrower) handler.getRandomNPC();
        mouseEntity.inGame = true;
        mouseEntity.name = "Hero";
        mouseEntity.team = 8333;
        mouseEntity.defenseEffectiveness = 0.75;
        mouseEntity.defense = 3500;
        mouseEntity.applicableForce = 10;
        mouseEntity.weight = 10;
        mouseEntity.throwPower = 65;
        mouseEntity.maxHealth = 1800;
        mouseEntity.health = 1800;
        handler.spawnTeam(30, 8333);

        mouseEntity.moveBySelf = false;

        arenaPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (Main.mouseEntity != null) {
                    mouseEntity.pointTowards(e.getX(), e.getY());
                    if (mouseEntity.findDistance(e.getX(), e.getY()) > mouseEntity.size * 0.5){
                        mouseEntity.moveForward();
                    }
                }
            }

        });

        arenaPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (Main.mouseEntity != null) {
                    mouseEntity.throwProjectile();
                }

            }
        });




        /*arenaPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mouseEntity.x = e.getX() - mouseEntity.size / 2;
                mouseEntity.y = e.getY() - mouseEntity.size / 2;
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                mouseEntity.x = e.getX() - mouseEntity.size / 2;
                mouseEntity.y = e.getY() - mouseEntity.size / 2;
            }
        });*/





    }
}

class BattleArenaPanel extends JPanel {
    List<Entity> toAdd = new ArrayList<>();
    List<Entity> toRemove = new ArrayList<>();
    static double globalPower = 1;
    static int humanoidsInGame = 0;
    static final int canvasWidth = 1920;
    static final int canvasHeight = 1080;
    static ArrayList<Entity> entities = new ArrayList<>();
    private final Renderer renderer = new Renderer();
    private boolean spawnAllowed = true; // whether NPCs can spawn normally
    private boolean lastHumanoidChecked = false; // to track giving extra modifier
    boolean allSameTeam = true;




    public BattleArenaPanel() {
        /*int armorSteps = 50;
        int totalColumns = 10; // full grid
        int perRow = 8;         // 8 NPCs per row (middle 8)
        int rowSpacing = 150;

        for (int i = 0; i <= armorSteps; i++) {
            double armorLevel = (double) i / armorSteps;

            int columnInRow = i % perRow; // 0–7 for 8 NPCs
            int row = i / perRow;

            // map to original 10-slot positions, skipping first and last
            int column = columnInRow + 1; // now column 1–8 (slot 0 and 9 empty)

            double xLevel = (double) column / (totalColumns - 1); // 1/9 … 8/9
            double scaledX = xLevel * canvasWidth;

            Thrower npc = new Thrower(
                    (int) scaledX,
                    canvasHeight / 8 + row * rowSpacing,
                    Color.BLUE
            );
            float hue = (float) (armorLevel * 4.0/6.0);
            Color defenseColor = Color.getHSBColor(hue, 1.0f, 1.0f);
            npc.name = "Armor " + (int) (armorLevel * 100) + "%";
            npc.team = (int) (Math.random() * 10000);
            npc.moveBySelf = true;
            npc.renderName = true;
            npc.facingAngleDegrees = 180;
            npc.maxHealth = 1000;
            npc.health = 1000;
            npc.color = defenseColor;
            npc.defense = 1000;
            npc.defenseEffectiveness = armorLevel;
            npc.applicableForce = 0.5;
            npc.weight = 1;
            npc.regen = 0.1;
            npc.throwPower = 30;
            npc.anchored = false;

            entities.add(npc);
        } /*





        /*setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        Thrower red = new Thrower(300,300, Color.RED);
        red.name = "Red The OG";
        red.team = 1;
        red.applicableForce = 7;
        red.weight = 1.5;
        red.renderName = true;
        entities.add(red);

        Thrower blue = new Thrower((int)( Math.random() * canvasWidth),(int)(Math.random() * canvasHeight), Color.BLUE);
        blue.name = "blue the boxer";
        blue.applicableForce = 4;
        blue.team = 2;
        blue.facingAngleDegrees = 180;
        blue.renderName = true;
        entities.add(blue);

        Ricocheteur green = new Ricocheteur((int)( Math.random() * canvasWidth),(int)(Math.random() * canvasHeight));
        green.name = "green the gamer";
        green.applicableForce = 20;
        green.weight = 10;
        green.throwPower = 50;
        green.maxHealth = 500;
        green.health = green.maxHealth;
        green.team = 2500;
        green.regen = 0.25;
        green.facingAngleDegrees = 180;
        green.renderName = true;
        entities.add(green);

        Thrower yellow = new Thrower((int)( Math.random() * canvasWidth),(int)(Math.random() * canvasHeight), Color.YELLOW);
        yellow.name = "yellow the yeller";
        yellow.applicableForce = 2;
        yellow.weight = 0.25;
        yellow.team = 4;
        yellow.facingAngleDegrees = 180;
        yellow.renderName = true;
        entities.add(yellow);*/

        int baseDelay = 80; // 80 ticks
        javax.swing.Timer spawnTimer = new javax.swing.Timer(baseDelay * Main.frameDelay, e -> {

            int adjustedDelay = (int) Math.max(5 * Main.frameDelay, baseDelay / globalPower * Main.frameDelay);
            ((javax.swing.Timer) e.getSource()).setDelay(adjustedDelay);

            if (globalPower < 100) {
                spawnAllowed = true;
            } else {
                spawnAllowed = false;
                if (allSameTeam) {
                    for (Entity entity : entities) {
                        if (entity instanceof Humanoid h) {
                            h.team = (int)(Math.random() * 10000);
                        }
                    }
                }

            }
            int firstTeam = -1;
            int changes = 0;
            for (Entity entity : entities) {
                if (entity instanceof Humanoid h) {
                    if (h.team != firstTeam){
                        firstTeam = h.team;
                        changes ++;
                    }
                }
            }
            if (changes >=2 ){
                allSameTeam = false;
            } else{
                allSameTeam = true;
            }
            if (humanoidsInGame == 1 && !lastHumanoidChecked) {
                lastHumanoidChecked = true;

                for (Entity entity : BattleArenaPanel.entities) {
                    if (entity instanceof Humanoid h) {
                        //FighterHandler.addModifiers(h);
                        break;
                    }
                }

            } else if (humanoidsInGame > 1) {
                lastHumanoidChecked = false;
            }

            if (spawnAllowed && humanoidsInGame < 100) {
                Main.handler.spawnRandomNPC();
            }

        });
        spawnTimer.start();


    }


    public void updateUnits() {
        globalPower = 1;
        humanoidsInGame = 0;
        for (Entity unit : entities) {
            unit.update();
            if (unit.deletable){
                if (unit instanceof Humanoid h){
                    globalPower = globalPower - h.globalPowerValue;
                }
                toRemove.add(unit);
                continue;
            }
            if(unit instanceof Humanoid h){
                //h.takeDamage(0.25);
                globalPower = globalPower + h.globalPowerValue;
                humanoidsInGame = humanoidsInGame + 1;
            }

        }
        for (Entity entity : toAdd){
            entity.inGame = true;
        }
        entities.addAll(toAdd);
        entities.removeAll(toRemove);
        toAdd.clear();
        toRemove.clear();
        if (Main.mouseEntity == null || Main.mouseEntity.deletable) {
            Thrower newHost = null;
            for (Entity e : entities) {
                if (e instanceof Thrower t && !t.deletable && t.health > 0) {
                    newHost = t;
                    break;
                }
            }

            if (newHost != null) {
                newHost.moveBySelf = false;
                Main.mouseEntity = newHost;
            } else {
                Main.mouseEntity = null;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.renderBackground(g, canvasWidth, canvasHeight);

        List<Entity> safeEntity;
        synchronized (entities) {
            safeEntity = new ArrayList<>(entities);
        }
        for (Entity unit : safeEntity) {
            renderer.renderEntity(g, unit);
        }

    }
}

class Entity {
    Boolean airborne = false;
    Boolean anchored = false;
    double x;
    double y;
    double weight = 1;
    double xAxisForce = 0.0;
    double yAxisForce = 0.0;
    double xAxisSpeed= 0.0;
    double yAxisSpeed = 0.0;
    int size = 100;
    double facingAngleDegrees = 0; // 0 is to right, then increments by degrees in clockwise position
    Boolean inGame = false;
    String name;
    Color color;

    Boolean deletable = false;

    static double globalFriction = 0.7; // 0 no move, 1 no friction, >1 antifriction
    static double airborneFriction = 0.97; // for projectiles

    public Entity(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void update() {
        if (this.anchored){
            xAxisSpeed = 0;
            xAxisForce = 0;
            yAxisSpeed = 0;
            yAxisForce = 0;
        }
        xAxisSpeed = xAxisSpeed + xAxisForce / weight;
        yAxisSpeed = yAxisSpeed + yAxisForce / weight;

        if (this.airborne){
            xAxisSpeed = xAxisSpeed * airborneFriction;
            yAxisSpeed = yAxisSpeed * airborneFriction;
        } else{
            xAxisSpeed = xAxisSpeed * globalFriction;
            yAxisSpeed = yAxisSpeed * globalFriction;
        }

        x = x + xAxisSpeed;
        y = y + yAxisSpeed;

        xAxisForce = 0;
        yAxisForce = 0;

        if (x + size/2 > BattleArenaPanel.canvasWidth){
            x = BattleArenaPanel.canvasWidth - size/2;
            xAxisSpeed = -xAxisSpeed / 2;
            reflect();
        }
        if (x - size/2 < 0) {
            x = size/2;
            xAxisSpeed = -xAxisSpeed / 2;
            reflect();
        }
        // Bottom edge
        if (y + size/2 > BattleArenaPanel.canvasHeight) {
            y = BattleArenaPanel.canvasHeight - size/2;
            yAxisSpeed = -yAxisSpeed / 2;
            reflect();
        }
        // Top edge
        if (y - size/2 < 0) {
            y = size/2;
            yAxisSpeed = -yAxisSpeed / 2;
            reflect();
        }

        ArrayList<Entity> allentities = BattleArenaPanel.entities;
        for (Entity e : allentities){
            if (this.isColliding(e) && !this.equals(e)){
                if (findDistance(e) == 0) {
                    continue;
                }
                if (this instanceof Projectile p && e == p.parent) {
                    continue;
                }
                if (this instanceof Humanoid h && e instanceof Projectile s && s.parent != null && s.parent.equals(h)) {
                    continue;
                }


                double dx = e.x - this.x;
                double dy = e.y - this.y;

                double distance = findDistance(e);

                double nx = dx / distance;
                double ny = dy / distance;

                double overlapSum = (this.size / 2 + e.size / 2);
                double rawOverlap = overlapSum - distance;
                double normalizedOverlap = 0;


                if (rawOverlap > 0) {
                    normalizedOverlap = rawOverlap / overlapSum;
                }

                double totalWeight = this.weight + e.weight;
                double eGeneralSpeed = Math.sqrt(e.xAxisSpeed * e.xAxisSpeed + e.yAxisSpeed * e.yAxisSpeed);
                double pushThis = e.weight * eGeneralSpeed * normalizedOverlap * 3; //(e.weight / totalWeight) * overlap * totalWeight;


                if (e instanceof Projectile p && this instanceof Humanoid h) {
                    //System.out.println("Collision: Projectile team=" + p.team + " parent=" + (p.parent!=null?p.parent.name:"null") + " vs Humanoid team=" + h.team + " name=" + h.name);
                    if (p.parent == h){
                        continue;
                    }
                    p.strikeForceMultiplyer = p.strikeForceMultiplyer * 0.75;
                    if (p.team == h.team) {
                        //p.parent = null;
                        p.takeDamage(100);
                        this.xAxisForce -= nx * pushThis;
                        this.yAxisForce -= ny * pushThis;
                        continue;
                    }


                    double damage = (p.strikeForceMultiplyer * e.weight * eGeneralSpeed);
                    h.takeDamage(damage);
                    p.takeDamage(100);
                    p.team = h.team;
                    p.parent = null;
                }

                if (e instanceof Projectile p && !(this instanceof Humanoid)){
                    p.parent = null;
                    p.team = 0;
                }


                this.xAxisForce -= nx * pushThis;
                this.yAxisForce -= ny * pushThis;


            }
        }


    }
    public void reflect(){

        facingAngleDegrees = Main.findPointAngle(xAxisSpeed, yAxisSpeed);
        if (facingAngleDegrees < 0) facingAngleDegrees = facingAngleDegrees + 360;
        if (this instanceof Projectile){
            ((Projectile) this).takeDamage(125);
            ((Projectile) this).parent = null;
            ((Projectile) this).team = 0;
        }
    }

    public double findDistance(Entity other){
        return findDistance((int) other.x, (int) other.y);
    }

    public double findDistance(int xc, int yc){
        double dx = Math.abs(this.x - xc);
        double dy = Math.abs(this.y - yc);
        double hyp = Math.sqrt(dx*dx+dy*dy);
        return hyp;
    }

    public boolean isColliding(Entity other) {
        double distance = findDistance(other);
        return distance < (this.size / 2.0 + other.size / 2.0);
    }


    public void pointTowards(Entity other){
        pointTowards((int)other.x, (int) other.y);
    }

    public void pointTowards(int x, int y){
        double xSide = x - this.x;
        double ySide = y - this.y;
        this.facingAngleDegrees = Main.findPointAngle(xSide, ySide);
    }
}

class Humanoid extends Entity{
    double globalPowerValue = 0.1;
    double health = 100;
    double maxHealth = 100;
    double regen = 0.1;
    double applicableForce = 1; // fancy name for speed
    double incomingDamage = 0;
    double defense = 0;
    double defenseEffectiveness = 0.1; // 0 is no protection, 1 is full reflection
    int team = 0;
    Boolean renderName = false;
    Boolean colorProvided;

    public Humanoid(int x, int y, Color color) {
        super(x, y, color);
        colorProvided = true;
    }

    public Humanoid(int x, int y) {
        super(x, y, null);
        this.color = computeColor();
        colorProvided = false;
    }

    public Color computeColor() {
        float hue = (team % 10000) / 10000f;

        float maxExpectedWeight = 10f;
        float saturation = Math.min(1f, (float) weight / maxExpectedWeight);

        // Brightness based on ratio of applicableForce to weight
        float ratio = (float) (applicableForce / (weight + 0.01)); // avoid divide by zero
        float brightness = Math.min(1f, Math.max(0.05f, ratio)); // keep min 0.2 so not too dark

        return Color.getHSBColor(hue, saturation, brightness);
    }

    public void update() {
        super.update();
        this.regen = this.maxHealth / 1000;
        if (!colorProvided){
            this.color = computeColor();
        }

        double healthDamage = 0;
        double defenseDamage = 0;
        if (defense >= defenseEffectiveness * incomingDamage){
            defenseDamage = defenseEffectiveness * incomingDamage;
            healthDamage = (1 - defenseEffectiveness) * incomingDamage;
        } else if (defense >= 1){
            //double rawDefense = defenseEffectiveness * incomingDamage;
            defenseDamage = defense;
            healthDamage = incomingDamage - defenseDamage;
        } else{
            healthDamage = incomingDamage;
        }

        health = health - healthDamage + regen;
        defense = defense - defenseDamage;
        incomingDamage = 0;
        if (health <= 0){
            this.deletable = true;
        } else if (health > maxHealth){
            health = maxHealth;
        }
    }

    public void moveForward(){
        double hyp  = this.applicableForce;
        double theta = Math.toRadians(this.facingAngleDegrees);
        double yMovement = hyp * Math.sin(theta);
        double xMovement = hyp * Math.cos(theta);

        xAxisForce = xAxisForce + xMovement;
        yAxisForce = yAxisForce + yMovement;

    }

    public void takeDamage(double dmg){
        this.incomingDamage = incomingDamage + dmg;
    }

    public Humanoid findClosestEnemy() {
        Humanoid closestHumanoid = null;
        Double closestDistance = 9999999.9;
        for (Entity h : BattleArenaPanel.entities) {
            if (h instanceof Humanoid){
                if (findDistance(h) < closestDistance && this.team != ((Humanoid) h).team){
                    closestHumanoid = (Humanoid) h;
                    closestDistance = findDistance(h);
                }
            }
        }
        return closestHumanoid;
    }
}

class Projectile extends Entity{
    double lifespan = 200;
    static double projectileInitialSpeed = 6;
    double damageOnForce = 1;
    double incomingDamage = 0;
    double strikeForceMultiplyer = 3;
    static int DFprojectileSize = 35;
    int team = 0;
    Boolean renderName = false;
    Entity parent;

    public Projectile(Entity parent) {
        this(parent, projectileInitialSpeed);
    }

    public Projectile(Entity parent, Double force) {
        super((int) parent.x, (int) parent.y, Color.DARK_GRAY);
        this.parent = parent;
        this.size = 35;
        this.projectileInitialSpeed = force;
        this.weight = 0.5;
        this.airborne = true;
        this.facingAngleDegrees = parent.facingAngleDegrees;
        double theta = Math.toRadians(parent.facingAngleDegrees);
        this.xAxisSpeed = (Math.cos(theta) * projectileInitialSpeed + parent.xAxisSpeed );
        this.yAxisSpeed = (Math.sin(theta) * projectileInitialSpeed + parent.yAxisSpeed );

        if (parent instanceof Humanoid human){
            this.team = ((Humanoid) parent).team;
        }
    }
    public void update() {
        super.update();
        if (this.inGame){
            lifespan = lifespan - incomingDamage - 1;
        }

        incomingDamage = 0;
        if (lifespan <= 0){
            this.deletable = true;
        }
    }

    public void takeDamage(int dmg){
        this.incomingDamage = incomingDamage + dmg;
    }
}

class Renderer {

    public void renderBackground(Graphics g, int width, int height) {

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
    }

    public void renderEntity(Graphics g, Entity h) {
        double theta = Math.toRadians(h.facingAngleDegrees);
        double dx = Math.cos(theta);
        double dy = Math.sin(theta);

        double rad = h.size / 2.0;

        int drawX = (int)(h.x - h.size / 2.0);
        int drawY = (int)(h.y - h.size / 2.0);

        int startX = (int) (drawX + rad);
        int startY = (int) (drawY + rad);
        int endX = (int) (startX + dx * rad);
        int endY = (int) (startY + dy * rad);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(h.color);
        //g2d.fillOval((int) h.x, (int) h.y, h.size, h.size);
        g2d.fillOval(drawX, drawY, h.size, h.size);

        if (h instanceof Humanoid){
            g2d.setColor(Color.BLACK);
            g2d.drawLine(startX, startY, endX, endY);
        }


        if (h instanceof Humanoid p && p.renderName && h.name != null) {
            /*g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(h.name);
            int textX = (int) (h.x + h.size / 2 - textWidth / 2);
            int textY = (int) (h.y - 8); // above the circle
            g2d.drawString(h.name, textX, textY);

            int healthInt = (int) p.health;
            int mHealthInt = (int) p.maxHealth;
            textWidth = fm.stringWidth( healthInt + "/" + mHealthInt);
            textX = (int) (h.x + h.size / 2 - textWidth / 2);
            textY = (int) (h.y - 24); // above the circle
            g2d.drawString(healthInt + "/" + mHealthInt, textX, textY);*/

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 25));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(h.name);
            int textX = (int) (drawX + h.size / 2 - textWidth / 2);
            int textY = (int) (drawY - 10); // above the circle
            g2d.drawString(h.name, textX, textY);

            int healthInt = (int) p.health;
            int mHealthInt = (int) p.maxHealth;
            int defense = (int) p.defense;
            double defenseStrength = p.defenseEffectiveness;
            if (defense >= 1){
                textWidth = fm.stringWidth( healthInt + "/" + mHealthInt + " + " + defense);
            } else{
                textWidth = fm.stringWidth( healthInt + "/" + mHealthInt);
            }
            textX = (int) (drawX + h.size / 2 - textWidth / 2);
            textY = (int) (drawY- 30); // above the circle

            if (defense >= 1){
                float hue = (float) (defenseStrength * 4.0/6.0);
                Color defenseColor = Color.getHSBColor(hue, 1.0f, 1.0f);

                g2d.setColor(defenseColor);
                g2d.drawString(healthInt + "/" + mHealthInt + " + " + defense, textX, textY);
            } else{
                g2d.drawString(healthInt + "/" + mHealthInt, textX, textY);
            }
        }
    }
}