package com.app.DamageCalculator40k.UI;

import android.view.View;

import core.DatasheetModeling.DeactivatableInterface;
import com.app.DamageCalculator40k.FileHandling.AndroidFileHandler;
import com.app.DamageCalculator40k.Matchup;

public class OnClickDeactivate implements  View.OnClickListener
{
    public DeactivatableInterface deactivatable;
    private final Matchup matchup;
    public OnClickDeactivate(DeactivatableInterface deactivatable, Matchup matchup)
    {
        this.deactivatable = deactivatable;
        this.matchup = matchup;
    }
    @Override
    public void onClick(View view) {
        deactivatable.FlipActive();

        AndroidFileHandler.GetInstance().saveMatchup(matchup);
    }
}
