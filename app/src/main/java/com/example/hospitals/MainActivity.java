package com.example.hospitals;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hospitals.databinding.ActivityMainBinding;

/**
 * MainActivity sets up the navigation drawer and toolbar using the Navigation Component.
 * It uses view binding to access views and configures top-level destinations.
 */
public class MainActivity extends AppCompatActivity {

    // AppBarConfiguration defines the top level destinations.
    private AppBarConfiguration mAppBarConfiguration;
    // View binding for activity_main.xml.
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using view binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar as the app bar.
        setSupportActionBar(binding.appBarMain.toolbar);

        // Set up the FloatingActionButton (FAB) click listener.
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab)
                        .show();
            }
        });

        // Retrieve the DrawerLayout and NavigationView from binding.
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set up AppBarConfiguration with top-level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_book_appointment, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        // Set up the NavController with the host fragment.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Configure the ActionBar to work with the NavController and AppBarConfiguration.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // Link the NavigationView with the NavController so menu items navigate accordingly.
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu; adds items to the action bar if present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle navigation when the user presses the "up" button in the app bar.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
