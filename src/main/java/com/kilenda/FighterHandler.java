package com.kilenda;
import java.awt.*;


public class FighterHandler {
    static BattleArenaPanel gamePanel;
    static int arenaPower = 1;

    static int betaCount = 0;
    static int alphaCount = 0;
    static int gammaCount = 0;
    static int omegaCount = 0;
    static int sigmaCount = 0;
    FighterHandler(BattleArenaPanel panel){
        gamePanel = panel;
    }

    static void addModifiers(Humanoid subject) {
        addModifiers(subject, -1);
    }

    static void addModifiers(Humanoid subject, double luck){
        if (luck < 0) {
            luck = Math.random() * 100;
        }
        String type = "";
        int multiplyer = 1;
        int myCount = 1;

        if (luck > 99.995) {
            type = "Omega";
            multiplyer = 81;
            subject.globalPowerValue += 50;
            omegaCount ++;
            myCount = omegaCount;
        } else if (luck > 99.895) {
            type = "Gamma";
            multiplyer = 27;
            gammaCount ++;
            subject.globalPowerValue += 15;
            myCount = gammaCount;
        } else if (luck > 98.895) {
            type = "Alpha";
            multiplyer = 9;
            subject.globalPowerValue += 3;
            alphaCount ++;
            myCount = alphaCount;
        } else if (luck > 93.895) {
            type = "Beta";
            multiplyer = 3;
            subject.globalPowerValue += 0.5;
            betaCount ++;
            myCount = betaCount;
        } else if (luck < 0.00000125){ // 0.00000125
            type = "Sigma";
            multiplyer = 243;
            subject.globalPowerValue += 80;
            sigmaCount ++;
            myCount = sigmaCount;
        }
        int multiplyerLevel = (int) (Math.log(multiplyer) / Math.log(3)) + 1;

        if (myCount % (multiplyerLevel * multiplyerLevel * multiplyerLevel) == 0 && multiplyer > 2){
            type = type + "-Prime";
            multiplyer =(int) (multiplyer * (5.0/3.0));
        } else if (myCount % (multiplyerLevel * multiplyerLevel ) == 0 && multiplyer > 2){
            type = type + "-Semiprime";
            multiplyer =(int) (multiplyer * (4.0/3.0));
        }

        subject.applicableForce *= multiplyer;
        subject.weight *= multiplyer;
        subject.maxHealth *= multiplyer;
        subject.health = subject.maxHealth;
        subject.size = (subject.size + multiplyer * 5);
        subject.defenseEffectiveness = (Math.random() * 15 * (Math.log(multiplyer) / Math.log(3) + 1)) / 100;
        double armorLuck = Math.random();

        if (armorLuck > Math.min(7.0/8.0,  1/multiplyer)){
            subject.defense = Math.random() * subject.health * 0.5 * Math.sqrt(multiplyer);
        }
        if(subject instanceof Thrower t){
            t.throwPower = t.throwPower + multiplyer * 5;
            t.throwRate = (int) (t.throwRate / (0.5 * multiplyer));
            t.throwRate = Math.max(1, t.throwRate);
        }
        if (!type.isEmpty()){
            subject.name = type + " " + subject.name;
        }
    }
    public void spawnTeam(int size){
        int team = (int) (Math.random() * 10000);
        spawnTeam(size, team);
    }

    public void spawnTeam(int size, int team){
        //Color color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        int squadSize = size;

        for (int i = 1; i <= squadSize; i++){
            Thrower rand;
            int x = (int)(Math.random() * gamePanel.canvasWidth);
            int y = (int)(Math.random() * gamePanel.canvasHeight);
            if (Math.random() < 0.2) {
                rand = new Ricocheteur(x, y);
            } else {
                rand = new Thrower(x, y);
            }
            rand.name = "Team " + team + " " + rand.getClass().getSimpleName();
            rand.applicableForce = Math.random() * 8;
            rand.weight = Math.random() * 2.5;
            rand.team = team;
            rand.facingAngleDegrees = 180;
            rand.maxHealth = (int)(Math.random() * 200);
            rand.health = rand.maxHealth;
            rand.renderName = true;
            rand.throwRate = (int) ((Math.random() * 20) + 20);
            FighterHandler.addModifiers(rand);
            gamePanel.toAdd.add(rand);
        }
        Thrower rand;
        int x = (int)(Math.random() * gamePanel.canvasWidth);
        int y = (int)(Math.random() * gamePanel.canvasHeight);
        if (Math.random() < 0.2) {
            rand = new Ricocheteur(x, y);
        } else {
            rand = new Thrower(x, y);
        }
        rand.name = "Team " + team + " " + rand.getClass().getSimpleName() + " Leader";
        rand.applicableForce = Math.random() * 16;
        rand.weight = Math.random() * 5;
        rand.size = 115;
        rand.team = team;
        rand.facingAngleDegrees = 180;
        rand.maxHealth = (int)(Math.random() * 400);
        rand.health = rand.maxHealth;
        rand.renderName = true;
        rand.throwPower = rand.throwPower + 5;
        rand.throwRate = (int) ((Math.random() * 15) + 15);
        rand.globalPowerValue += 0.25;
        rand.defense += rand.health / 2;
        FighterHandler.addModifiers(rand);
        gamePanel.toAdd.add(rand);
    }

    public void spawnRandomNPC() {
        if (Math.random() < 0.1){
            spawnTeam((int) (Math.random() * 6 + 1));
        }
       // Color color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        Thrower rand;
        int x = (int)(Math.random() * gamePanel.canvasWidth);
        int y = (int)(Math.random() * gamePanel.canvasHeight);
        if (Math.random() < 0.2) {
            rand = new Ricocheteur(x, y);
        } else {
            rand = new Thrower(x, y);
        }
        rand.name = rand.getClass().getSimpleName();;
        rand.applicableForce = Math.random() * 8;
        rand.weight = Math.random() * 2.5;
        rand.team = (int) (Math.random() * 10000);
        rand.facingAngleDegrees = 180;
        rand.maxHealth = (int)(Math.random() * 200);
        rand.health = rand.maxHealth;
        rand.renderName = true;
        rand.throwRate = (int) ((Math.random() * 20)) + 20;
        FighterHandler.addModifiers(rand);
        gamePanel.toAdd.add(rand);
    }

    public Humanoid getRandomNPC() {
        // Color color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        Thrower rand;
        int x = (int)(Math.random() * gamePanel.canvasWidth);
        int y = (int)(Math.random() * gamePanel.canvasHeight);
        if (Math.random() < 0.2) {
            rand = new Ricocheteur(x, y);
        } else {
            rand = new Thrower(x, y);
        }
        rand.name = rand.getClass().getSimpleName();;
        rand.applicableForce = Math.random() * 8;
        rand.weight = Math.random() * 2.5;
        rand.team = (int) (Math.random() * 10000);
        rand.facingAngleDegrees = 180;
        rand.maxHealth = (int)(Math.random() * 200);
        rand.health = rand.maxHealth;
        rand.renderName = true;
        rand.throwRate = (int) ((Math.random() * 20)) + 20;
        FighterHandler.addModifiers(rand);
        gamePanel.toAdd.add(rand);
        return rand;
    }

}


class Thrower extends Humanoid{
    boolean moveBySelf = true; // new
    int throwRate = (int) (Math.random() * 30 + 15);
    int ogThrow = throwRate;
    int throwOffset = (int) (Math.random() * throwRate);
    int range = 1;
    int safeDistance = 125;
    double throwPower = 10;

    public Thrower(int x, int y, Color color) {
        super(x, y, color);

    }

    public Thrower(int x, int y) {
        super(x, y);

    }
    protected void throwProjectile(){
        Projectile projectile = new Projectile(this, throwPower);
        projectile.projectileInitialSpeed = throwPower;
        FighterHandler.gamePanel.toAdd.add(projectile);
    }
    protected void humanoidUpdate() {
        super.update(); // actually calls Humanoid.update()
    }


    public void update() {


        super.update();
        Humanoid enemy = this.findClosestEnemy();
        if (enemy != null && this.moveBySelf){
            this.pointTowards(enemy);
            double distance = findDistance(enemy);
            this.moveForward();
            if (distance > range){
                if ((Main.tickN + throwOffset) % throwRate == 0){
                    throwProjectile();
                }
            } else{

            }

        }
        if (throwRate != ogThrow){
            int throwOffset = (int) (Math.random() * throwRate);
            ogThrow = throwRate;
        }
    }
}

class Ricocheteur extends Thrower{

    public Ricocheteur(int x, int y, Color color) {

        super(x, y, color);
        this.throwRate = this.throwRate * 2; // throws half as often
        this.throwPower = this.throwPower * 1.5;


    }
    public Ricocheteur(int x, int y) {
        super(x, y);
        this.throwRate = this.throwRate * 2; // throws half as often
        this.throwPower = this.throwPower * 1.5;


    }

    @Override
    protected void throwProjectile() {
        double inaccuracy = (Math.random() - 0.5) * 30; // ±15 degrees
        this.facingAngleDegrees += inaccuracy;

        Projectile projectile = new Projectile(this, throwPower);
        projectile.projectileInitialSpeed = throwPower;
        projectile.strikeForceMultiplyer = projectile.strikeForceMultiplyer * 0.75;
        projectile.color = Color.GREEN;
        projectile.lifespan = 1000;
        projectile.size = 30;
        FighterHandler.gamePanel.toAdd.add(projectile);
    }

    @Override
    public void update() {
        humanoidUpdate();
        Humanoid enemy = this.findClosestEnemy();



        if (enemy != null && this.moveBySelf){
            this.pointTowards(enemy);
            double distance = findDistance(enemy);
            this.moveForward();
            if (distance > range){
                if ((Main.tickN + throwOffset) % throwRate == 0){
                    throwProjectile();
                }
            }
        }
        if (throwRate != ogThrow){
            int throwOffset = (int) (Math.random() * throwRate);
            ogThrow = throwRate;
        }
    }
}
