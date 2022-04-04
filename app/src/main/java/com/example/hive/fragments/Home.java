package com.example.hive.fragments;

import static com.example.hive.utils.Helpers.toLowerCase;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hive.R;
import com.example.hive.models.User;
import com.example.hive.services.Response;
import com.example.hive.services.UserService;
import com.example.hive.utils.Helpers;

import java.util.ArrayList;

public class Home extends Fragment {
    EditText searchBar;
    RecyclerView searchResults;
    Button cancelSearchBtn;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<User> tmpUsers = new ArrayList<>();
    UsersRecyclerViewAdapter adapter;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBar = view.findViewById(R.id.searchBar);
        cancelSearchBtn = view.findViewById(R.id.cancelSearchBtn);
        searchResults = view.findViewById(R.id.searchResultsView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);

        // set recycler view things
        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        layoutMgr.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new UsersRecyclerViewAdapter(users);
        searchResults.setLayoutManager(layoutMgr);
        searchResults.setAdapter(adapter);
        searchResults.setItemAnimator(new DefaultItemAnimator());

        if (searchBar != null) {
            searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // call users
                        UserService.getAllUsers(new Response() {
                            @Override
                            public void onSuccess(Object data) {
                                users = User.fromObjects(data);
                                adapter.setData(users);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure() {

                            }
                        });

                        params.weight = 1;
                        Helpers.slideViewUp(searchResults);
                    }
                    cancelSearchBtn.setLayoutParams(params);
                }
            });

            searchBar.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    tmpUsers.removeIf(user -> !user.getFullName().toLowerCase().contains(toLowerCase(charSequence)));
                    adapter.setData(tmpUsers);
                    adapter.notifyDataSetChanged();
                    tmpUsers = (ArrayList<User>) users.clone();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }

        cancelSearchBtn.setOnClickListener(l -> {
            Helpers.slideViewDown(searchResults);
            params.weight = 0;
            cancelSearchBtn.setLayoutParams(params);
        });
    }
}