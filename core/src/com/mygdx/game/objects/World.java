package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Assets;

public class World {
    Space space;
    Ship ship;
    AlienArmy alienArmy;
    BitmapFont lifes, gameover, score;


    int WORLD_WIDTH, WORLD_HEIGHT;

    public World(int WORLD_WIDTH, int WORLD_HEIGHT){
        this.WORLD_WIDTH = WORLD_WIDTH;
        this.WORLD_HEIGHT = WORLD_HEIGHT;

        space = new Space();
        ship = new Ship(WORLD_WIDTH/2);
        alienArmy = new AlienArmy(WORLD_WIDTH, WORLD_HEIGHT);
        lifes = new BitmapFont();
        gameover = new BitmapFont();
        score = new BitmapFont();
    }

    public void render(float delta, SpriteBatch batch, Assets assets) throws InterruptedException {

        update(delta, assets);

        batch.begin();
        space.render(batch);
        ship.render(batch);
        alienArmy.render(batch);
        lifes.draw(batch, "VIDAS: " + ship.getLife(), 320, WORLD_HEIGHT - 3);
        lifes.setColor(Color.RED);
        score.draw(batch,"PUNTUACION: " + ship.getScore(), 5, WORLD_HEIGHT-3);
        score.setColor(Color.CYAN);

        if(ship.getLife()==0||ship.getLife()<0){
            gameover.draw(batch, "GAME OVER",WORLD_WIDTH/2-45, WORLD_HEIGHT/2);
            gameover.setColor(Color.RED);
            alienArmy.speedX=0;
            alienArmy.speedY=0;
        }
        batch.end();
    }

    void update(float delta, Assets assets){

        if(ship.life >0){
        space.update(delta, assets);
        ship.update(delta, assets);
        alienArmy.update(delta, assets);
        checkCollisions(assets);
        }
    }

    private void checkCollisions(Assets assets) {
        checkNaveInWorld();
        checkShootsInWorld();
        checkShootsToAlien(assets);
        checkShootsToShip(assets);
    }

    private void checkShootsToShip(Assets assets) {
        Rectangle shipRectangle = new Rectangle(ship.position.x, ship.position.y, ship.frame.getRegionWidth(), ship.frame.getRegionHeight());

        for(AlienShoot shoot: alienArmy.shoots){
            Rectangle shootRectangle = new Rectangle(shoot.position.x, shoot.position.y, shoot.frame.getRegionWidth(), shoot.frame.getRegionHeight());

            if (Intersector.overlaps(shootRectangle, shipRectangle)) {
                ship.damage();
                shoot.remove();
                if (ship.life ==0){
                    assets.gameOverSound.play();
                }

            }
        }
    }

    private void checkShootsToAlien(Assets assets) {
        for(Shoot shoot: ship.weapon.shoots){
            Rectangle shootRectangle = new Rectangle(shoot.position.x, shoot.position.y, shoot.frame.getRegionWidth(), shoot.frame.getRegionHeight());
            for(Alien alien: alienArmy.aliens){
                if(alien.isAlive()) {
                    Rectangle alienRectangle = new Rectangle(alien.position.x, alien.position.y, alien.frame.getRegionWidth(), alien.frame.getRegionHeight());

                    if (Intersector.overlaps(shootRectangle, alienRectangle)) {
                        alien.kill();
                        shoot.remove();
                        assets.aliendieSound.play();
                        ship.score =ship.getScore()+10;
                    }
                }
            }
        }
    }

    private void checkShootsInWorld() {
        for(Shoot shoot: ship.weapon.shoots){
            if(shoot.position.y > WORLD_HEIGHT){
                shoot.remove();
            }
        }

        for(AlienShoot shoot: alienArmy.shoots){
            if(shoot.position.y < 0){
                shoot.remove();
            }
        }
    }

    private void checkNaveInWorld() {
        if(ship.position.x > WORLD_WIDTH-32){
            ship.position.x = WORLD_WIDTH-32;
        } else if(ship.position.x < 0){
            ship.position.x = 0;
        }
    }
}
