package com.chentong.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.chentong.game.FlyBird;

public class AndroidLauncher extends AndroidApplication {

    // Create the shared preference to store the best value in the game
    public static SharedPreferences sharedPreferences;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new FlyBird(), config);

		sharedPreferences = this.getSharedPreferences("com.chentong.game", Context.MODE_PRIVATE);
	}
}
