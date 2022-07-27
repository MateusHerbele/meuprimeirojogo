package com.mhstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.mhstudios.entities.BulletShoot;
import com.mhstudios.entities.Enemy;
import com.mhstudios.entities.Entity;
import com.mhstudios.entities.Player;
import com.mhstudios.grafics.Spritesheet;
import com.mhstudios.grafics.UI;
import com.mhstudios.world.Camera;
import com.mhstudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener{
	
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	private final int SCALE = 3;
	
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	private final BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;

	public static Random rand;
	
	public UI ui;
	
	public static String gameState = "GAME_OVER";
	private boolean showMessageGameOver = false;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	
	//private int x = 0;
	
	public Game() {
        rand = new Random();
        addMouseListener(this);
		addKeyListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//inicializando objetos.
		
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 16, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
	
	}
	public void initFrame() {
		frame = new JFrame("Meu jogo");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();		 
	}
	public synchronized void stop() {
		
	}
	public static void main(final String[] args) {
		final Game game = new Game();
		game.start();
		
	}
	public void tick() {
		this.restartGame = false;
		if(gameState == "NORMAL") {
		for(int i = 0; i < entities.size(); i++) {
			final Entity e = entities.get(i);
			e.tick();
			
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).tick();
		}
		if(enemies.size() == 0) {
			//Avançar para o próximo level
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL) {
				CUR_LEVEL = 1;
			}
			String newWorld = "level"+CUR_LEVEL+" .png";
			World.restartGame(newWorld);
		}
		}else if(gameState == "GAME_OVER") {
		this.framesGameOver++;
		if(this.framesGameOver == 25) {
			this.framesGameOver = 0;
			if(this.showMessageGameOver)
				this.showMessageGameOver = false;
			else
				this.showMessageGameOver =true;
		}
		if(restartGame) {
			this.restartGame = false;
			this.gameState = "NORMAL";
			CUR_LEVEL = 1;
			String newWorld = "level"+CUR_LEVEL+" .png";
			World.restartGame(newWorld);
		}
		}
	}
	public void render(){
		final BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
	
		/* Renderização do jogo*/
		//Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		for(int i = 0; i < entities.size(); i++) {
			final Entity e = entities.get(i);
			e.render(g);
	}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("arial", Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo,600,20);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD,36));
			g.setColor(Color.white);
			g.drawString("GAME OVER!",(WIDTH*SCALE) / 2 - 100, (WIDTH*SCALE) / 2 -120);
			g.setFont(new Font("arial", Font.BOLD,32));
			g.setColor(Color.white);
			if(showMessageGameOver)
			g.drawString("Pressione Enter para recomeçar",(WIDTH*SCALE) / 2 - 240, (WIDTH*SCALE) / 2 - 40);
		}
		bs.show();
	}
	public void run() {
		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		final double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer= System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
		final long now = System.nanoTime();
		delta+= (now - lastTime) / ns;
		lastTime = now;
		
		if(delta >= 1) {
			tick();
			render();
			frames++;
			delta--;
		}
		if(System.currentTimeMillis() - timer >= 1000){
			System.out.println("FPS: "+frames);
			frames = 0;
			timer+=1000; 
				}
		stop(); 	
	}
	}
	@Override
	public void keyPressed(final KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
			
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
	
	if(e.getKeyCode() == KeyEvent.VK_ENTER) {
		this.restartGame = true;
		}
	}
	@Override
	public void keyReleased(final KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
			
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
			
		}
		
	}
	@Override
	public void keyTyped(final KeyEvent e) {
		
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
	
		
		
	


