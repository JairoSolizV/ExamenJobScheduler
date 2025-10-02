package com.example.examenjobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int JOB_ID = 1;

    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
            String mensaje;
            if (isAirplaneModeOn){
                mensaje = "El modo Avión está activado";
            }
            else{
                mensaje = "El modo Avión está DESactivado";
            }
            // Verifica el estado del modo avión y crea el mensaje correspondiente
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
            JobInfo jobInfo = getJobInfo(MainActivity.this);
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler != null) {
                scheduler.cancel(JOB_ID);

                int result = scheduler.schedule(jobInfo);
                if (result == JobScheduler.RESULT_SUCCESS) {
                    //Modifica el mensaje para que se pueda ver en un textview
                    tvStatus.setText(mensaje);
                    Toast.makeText(context, "Job programado al cambiar modo avión", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Fallo al programar el job en el servicio", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private static JobInfo getJobInfo(MainActivity broadcastDinamicoActivity) {
        ComponentName componentName = new ComponentName(broadcastDinamicoActivity, JobNotificacion.class);

        return new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(2000)
                .setOverrideDeadline(5000)
                .setPersisted(false)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneReceiver);
    }
}