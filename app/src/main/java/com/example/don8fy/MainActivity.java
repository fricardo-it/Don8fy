package com.example.don8fy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.don8fy.databinding.ActivityMainBinding;
import com.example.don8fy.ui.account.AccountFragment;
import com.example.don8fy.ui.item.ImageListAdapter;
import com.example.don8fy.ui.item.ItemModel;
import com.example.don8fy.ui.account.UserModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AccountFragment.OnNameUpdateListener {
    RecyclerView recyclerView;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private ImageListAdapter adapter;
    private ArrayList<ItemModel> itemList;

    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Don8fy Main Menu", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_favorites, R.id.nav_account, R.id.nav_search, R.id.nav_settings, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView nameHeaderTextView = headerView.findViewById(R.id.nameheader);
        TextView emailHeaderTextView = headerView.findViewById(R.id.emailheader);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String nameUser = prefs.getString("name", "");
        String emailUser = prefs.getString("email", "");
        String passwUser = prefs.getString("password", "");

        currentUser = new UserModel(nameUser, emailUser, passwUser);

        // user data header
        nameHeaderTextView.setText(nameUser);
        emailHeaderTextView.setText(emailUser);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new ImageListAdapter(MainActivity.this, itemList);
        recyclerView.setAdapter(adapter);

        adapter.getItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onNameUpdated(String newName) {
        // Update User Name
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nameHeaderTextView = headerView.findViewById(R.id.nameheader);
        nameHeaderTextView.setText(newName);
    }
}