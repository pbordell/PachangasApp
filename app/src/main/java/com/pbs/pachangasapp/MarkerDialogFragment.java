package com.pbs.pachangasapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pbs.pachangasapp.databinding.DialogCreateMarkerBinding;

import org.osmdroid.util.GeoPoint;

public class MarkerDialogFragment extends DialogFragment {

    public interface OnMarkerCreatedListener {
        void onMarkerCreated(String title, String description, GeoPoint point);
    }

    private DialogCreateMarkerBinding binding;
    private OnMarkerCreatedListener listener;
    private GeoPoint geoPoint;

    public static MarkerDialogFragment newInstance(GeoPoint point) {
        MarkerDialogFragment fragment = new MarkerDialogFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", point.getLatitude());
        args.putDouble("lon", point.getLongitude());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnMarkerCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar OnMarkerCreatedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            double lat = getArguments().getDouble("lat");
            double lon = getArguments().getDouble("lon");
            geoPoint = new GeoPoint(lat, lon);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCreateMarkerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCreate.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString();
            String description = binding.etDescription.getText().toString();

            if (title.isEmpty()) {
                binding.etTitle.setError("El título es obligatorio");
                return;
            }

            if (listener != null) {
                listener.onMarkerCreated(title, description, geoPoint);
            }
            dismiss();
        });

        binding.btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
