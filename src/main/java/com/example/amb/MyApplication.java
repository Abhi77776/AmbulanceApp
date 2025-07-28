package com.example.amb; // Make sure this matches your actual package name

import android.app.Application;
import android.content.Context;
import androidx.preference.PreferenceManager; // Using AndroidX PreferenceManager
// If you are NOT using AndroidX for preferences (older projects), you might use:
// import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;

public class MyApplication extends Application {@Override
public void onCreate() {
    super.onCreate();

    Context ctx = getApplicationContext();

    // Important: Set the user agent to prevent issues with tile providers.
    // Using your app's package name is a good practice.
    Configuration.getInstance().setUserAgentValue(getPackageName());

    // Load osmdroid configuration.
    // This line uses AndroidX PreferenceManager.
    Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    // If you are using the older android.preference.PreferenceManager, the line would be:
    // Configuration.getInstance().load(ctx, android.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

    // You can add other global initializations here if needed.
}
}