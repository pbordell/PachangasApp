package com.pbs.pachangasapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pbs.pachangasapp.databinding.DialogCreateMarkerBinding;

import org.osmdroid.util.GeoPoint;

import java.util.Calendar;
import java.util.Locale;

public class MarkerDialogFragment extends DialogFragment {

    public interface OnMarkerCreatedListener {
        void onMarkerCreated(Match newMatch);
    }

    private DialogCreateMarkerBinding binding;
    private OnMarkerCreatedListener listener;
    private GeoPoint geoPoint;
    private String selectedMatchDate = "";

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 1. Inflar el View Binding usando el LayoutInflater correcto del contexto
        binding = DialogCreateMarkerBinding.inflate(LayoutInflater.from(requireContext()));

        // 2. Construir la ventana mediante un AlertDialog seguro de Material/AppCompat
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());
        Dialog dialog = builder.create();

        // 3. CONFIGURAR LA LÓGICA DE INTERACCIÓN DE LOS BOTONES

        // Botón del Calendario
        binding.btnSelectDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        selectedMatchDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, (selectedMonth + 1), selectedDay);
                        binding.textSelectedDate.setText("Fecha: " + selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    }, year, month, day);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // Botón Crear / Publicar Pachanga
        binding.btnCreate.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();

            if (title.isEmpty()) {
                binding.etTitle.setError("El título es obligatorio");
                return;
            }

            if (selectedMatchDate.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null && geoPoint != null) {
                Match newMatch = new Match();
                newMatch.setTitle(title);
                newMatch.setDescription(description);
                newMatch.setMatchDate(selectedMatchDate);
                newMatch.setLatitude(geoPoint.getLatitude());
                newMatch.setLongitude(geoPoint.getLongitude());
                newMatch.setCreatedAt(System.currentTimeMillis());
                newMatch.setOccupiedPlaces(0);
                newMatch.setStatus("ACTIVO");

                listener.onMarkerCreated(newMatch);
            }
            dismiss();
        });

        // Botón Cancelar
        binding.btnCancel.setOnClickListener(v -> dismiss());

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evita pérdidas de memoria (Memory Leaks)
    }
}