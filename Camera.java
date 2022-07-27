package com.mhstudios.world;

public class Camera {
	
	public static int y;
	public static int x;
public static int clamp(int Atual, int Min, int Max){
		if(Atual < Min){
			Atual = Min;
		}if(Atual > Max){
			Atual = Max;
		}
        return Atual;
	}
}
