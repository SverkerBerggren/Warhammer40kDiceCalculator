package com.app.DamageCalculator40k;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.app.DamageCalculator40k.Abilities.Ability;

import java.util.HashMap;

public class Weapon_Popup extends AppCompatActivity {

    HashMap<String, Ability> map = new HashMap<String, Ability>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_popup);


    }

    public void ShowPopup(View v)
    {
      //  View popup = findViewById(R.id.Popup);
      //  popup.setVisibility(View.VISIBLE);
    }

    public void ClosePopup(View v)
    {
        View popup = findViewById(R.id.ConstraintLayoutPopup);
        popup.setVisibility(View.GONE);
    }

   //public void ShowSearch(View v)
   //{
   //    MainActivity mainActivity = new MainActivity();
   //    WeaponIdentifier weaponIdentifier = new WeaponIdentifier();
   //    mainActivity.Search( weaponIdentifier);
   //}
}