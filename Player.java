package com.mhstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.mhstudios.grafics.Spritesheet;
import com.mhstudios.main.Game;
import com.mhstudios.world.Camera;
import com.mhstudios.world.World;

public class Player extends Entity {
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1;
	
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	
	private boolean hasGun = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public double life = 100, maxlife = 100;
	public int mx, my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
	     super(x, y, width, height, sprite);
	     
	     rightPlayer = new BufferedImage[4];
	     leftPlayer = new BufferedImage[4];
	     playerDamage = Game.spritesheet.getSprite(96, 16, 16, 16);
	     for(int i = 0; i < 4; i++) {
	     leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
	}
	     for(int i = 0; i < 4; i++) {
		    rightPlayer[i] = Game.spritesheet.getSprite(80 + (i*-16), 0, 16, 16);
	     }
	}

	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x+speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if(left && World.isFree((int)(x-speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			y-=speed;
		}
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index >maxIndex)
					index = 0;
			}
		   
			}
 		checkCollisionLifePack();
 		checkCollisionAmmo(); 
 		checkCollisionGun();
 		
 		if(isDamaged) {
 			this.damageFrames++;
 			if(this.damageFrames == 8) {
 				this.damageFrames = 0;
 				isDamaged = false;
 			}
 		}
 			if(shoot ) {
				shoot = false;
				if(hasGun && ammo > 0) {
				ammo--;
				//Criar bala e atirar
				shoot = false;
				int dx = 0;
				int px = 0;
				int py = 6;
				if(dir == right_dir) {
			     px = 11;
				 dx = 1;
				}else {
			     px = -1;
			     dx = -1;
				}
				
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0 );
				Game.bullets.add(bullet);
			}
			}
			if(mouseShoot) {
				mouseShoot = false;
				if(hasGun && ammo > 0) {
				ammo--;
				//Criar bala e atirar
				int px = 0, py = 8;
				double angle = 0;
				if(dir == right_dir) {
				     px = 20;
				     angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX ()+px - Camera.x));
					}else {
				     px = -10;
				     angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX ()+px - Camera.x));
					}
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy );
				Game.bullets.add(bullet);
			}
			}
	
 			if(life <=0 ) {
 				life = 0;
 				Game.gameState = "GAME_OVER";
 			
 		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y =Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}

	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					ammo+= 20;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	public void checkCollisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColidding(this, atual)) {
					hasGun = true;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isColidding(this, atual)) {
					life+=8;
					if(life >= 100)
						life = 100;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

	public void render(Graphics g) {
		if(dir == right_dir) {
		g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y,  null);
		if(hasGun) {
			//Desenhar arma para direita.
			g.drawImage(Entity.GUN_RIGHT, this.getX()+8 - Camera.x, this.getY() - Camera.y, null);
		}
	}else if(dir == left_dir) {
		g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y,  null);
		if(hasGun) {
			//Desenhar arma para esquerda.
			g.drawImage(Entity.GUN_LEFT, this.getX()-8 - Camera.x, this.getY() - Camera.y, null);
		}
	}if(isDamaged){
		g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null); 
	}
	}
}



