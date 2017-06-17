package kr.ac.knu.dustwave;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewAction();
    }


    public void setViewAction() {
        mapButton = (Button)findViewById(R.id.map_button);
        mapButton.setOnClickListener(this);

    }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.map_button :

                //Open Map Activity
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                Log.d(this.getLocalClassName(), "map_button clicked ! ");


                break;



            default:

                break;
        }
    }
}
