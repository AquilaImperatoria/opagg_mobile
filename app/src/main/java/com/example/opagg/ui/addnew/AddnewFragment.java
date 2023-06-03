package com.example.opagg.ui.addnew;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.opagg.Place;
import com.example.opagg.RetrofitManager;
import com.example.opagg.databinding.FragmentAddnewBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddnewFragment extends Fragment {

    private RetrofitManager retrofitManager;
    private FragmentAddnewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        retrofitManager = RetrofitManager.getInstance();
        AddnewViewModel addnewViewModel =
                new ViewModelProvider(this).get(AddnewViewModel.class);

        binding = FragmentAddnewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button postbutton = (Button) binding.button;

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String request = String.valueOf(binding.inputtext.getText());
                Call<Place> call = retrofitManager.getPointsService().addPlace(request);

                call.enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        Toast.makeText(getContext(), "success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Place> call, Throwable t) {
                        Toast.makeText(getContext(), "fail", Toast.LENGTH_LONG).show();
                        Log.d("TAG", "onFailure: ", t);
                    }


                });
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}