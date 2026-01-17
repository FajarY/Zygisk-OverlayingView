package com.fajary.zygisk_overlayingviewui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button saveButton;
    Button startButton;
    EditText targetPackageText;
    TextView infoView;
    String targetPackageConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }
    private void initialize()
    {
        startButton = findViewById(R.id.startButton);
        saveButton = findViewById(R.id.saveButton);
        targetPackageText = findViewById(R.id.targetPackage);
        infoView = findViewById(R.id.infoView);

        saveButton.setOnClickListener(saveClick -> {
            SaveClick();
        });
        startButton.setOnClickListener(startClick -> {
            StartClick();
        });

        targetPackageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                targetPackageConfig = editable.toString();
            }
        });

        infoView.setVisibility(View.INVISIBLE);

        JSONObject save = FileUtility.readJson(getFilesDir().getAbsolutePath(), "config.json");
        targetPackageConfig = targetPackageText.getText().toString();
        if(save != null)
        {
            try
            {
                targetPackageConfig = save.getString("target_package");
                targetPackageText.setText(targetPackageConfig);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Debug.logE(e.getMessage());
            }
        }
    }
    private void SaveClick()
    {
        boolean succesfullSave = false;

        try
        {
            JSONObject saveObject = new JSONObject();
            saveObject.put("target_package", targetPackageConfig);

            boolean result = FileUtility.writeJson(saveObject, getFilesDir().getAbsolutePath(), "config.json");
            succesfullSave = result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Debug.logE(e.getMessage());
        }

        String infoText =
                "Current Saved Data\n" +
                "Package: " + targetPackageText.getText() + "\n";
        if(!succesfullSave)
        {
            infoText = "Failed to save config!";
        }

        infoView.setText(infoText);
        infoView.setVisibility(View.VISIBLE);
        infoView.setAlpha(1f);
        infoView.animate().alpha(0f).setDuration(5000).start();
    }
    private void StartClick()
    {
        if(Settings.canDrawOverlays(this))
        {
            startService(new Intent(this, OverlayService.class));
        }
        else
        {
            Toast.makeText(this, "Permission insuficient to start overlaying!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
    }
}