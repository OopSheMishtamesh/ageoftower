package com.nsw.ageoftower;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nsx.ageoftower.AgeOfTower;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "age-of-tower";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		 
		new LwjglApplication(new AgeOfTower(), cfg);
	}
}
