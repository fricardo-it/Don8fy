package com.example.don8fy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.don8fy.databinding.ActivityMainBinding;
import com.example.don8fy.ui.account.AccountFragment;
import com.example.don8fy.ui.account.UserModel;
import com.example.don8fy.ui.item.ImageListAdapter;
import com.example.don8fy.ui.item.ItemModel;
import com.example.don8fy.ui.item.NewItemFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        binding.appBarMain.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Don8fy Main Menu", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.toolbar).show();
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

        adapter.setOnItemClickListener(new ImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemModel item) {
                // Navegar para DetailItemFragment com os detalhes do item
                Bundle bundle = new Bundle();
                bundle.putString("name", item.getName());
                bundle.putString("description", item.getDescription());
                bundle.putString("url", item.getImageUri());
                bundle.putString("itemId", item.getItemId());
                bundle.putString("positionMap", item.getPositionMap());
                bundle.putBoolean("isFavorite", item.getIsFavorite());


                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_detail_item, bundle);
            }

        });
        // Adiciona um listener para o NavigationView para tratar os cliques nos itens do menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Se já estamos na MainActivity, não faz nada
                    if (!getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
                        // Cria um Intent para abrir a MainActivity
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpa o topo da pilha de atividades
                        startActivity(intent);
                    }
                } else {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(id);
                }

                drawer.closeDrawers();

                return true;
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_item_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_item) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_new_item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNameUpdated(String newName) {
        // Update User Name
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nameHeaderTextView = headerView.findViewById(R.id.nameheader);
        nameHeaderTextView.setText(newName);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (currentFragment instanceof NewItemFragment) {
        } else {
            super.onBackPressed();
        }
    }

}
