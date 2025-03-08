package com.app.DamageCalculator40k.UI;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

import android.content.Context;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.Enums.StatModifier;
import com.app.DamageCalculator40k.FileHandling.FileHandler;
import com.app.DamageCalculator40k.Identifiers.UIIdentifier;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;


public class EditModifierPopup {

    private boolean hasBeenInflated = false;
    private boolean isVisible = false;
    private final ViewGroup parent;
    private  View popup = null;
    private final LayoutInflater inflater;
    private final Matchup matchup;
    private final Context context;
    private final ModifierEditing modifierEditing;

    public EditModifierPopup(ViewGroup parent, LayoutInflater inflater, Matchup matchup, Context context, ModifierEditing modifierEditing)
    {
        this.parent = parent;
        this.inflater = inflater;
        this.matchup = matchup;
        this.context = context;
        this.modifierEditing = modifierEditing;
    }

    public View.OnClickListener GetOpenModifierPopupOnClickListener(GamePiece gamePiece, UIIdentifier uiId)
    {
        return new OpenModifierPopup(gamePiece,uiId);
    }
    public  int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
    private class OpenModifierPopup implements View.OnClickListener
    {
        private GamePiece gamePiece;
        private UIIdentifier uiId;


        public OpenModifierPopup(GamePiece gamePiece, UIIdentifier uiId)
        {
            this.gamePiece = gamePiece;
            this.uiId = uiId;
        }


        @Override
        public void onClick(View view) {
            if(!hasBeenInflated)
            {
                popup = inflater.inflate(R.layout.modifier_edit_popup,parent);
                TableLayout tableLayout = popup.findViewById(R.id.tableLayoutModifiersEdit);
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context,R.style.TableRowModifier);
                for(StatModifier statModifier : StatModifier.values())
                {
                    TableRow tableRow = new TableRow(contextThemeWrapper);
                    tableRow.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView textView = new TextView(contextThemeWrapper);
                    textView.setText(statModifier.name());
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);

                    tableRow.addView(textView);

                    EditText editText = new EditText(contextThemeWrapper);
                    editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText.setMaxLines(2);
                    editText.setText(context.getString(R.string.SingleIntegerText,gamePiece.getStatModifiers().GetModifier(statModifier)));
                    editText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);

                    ImageButton decreaseStat = new ImageButton(contextThemeWrapper);
                    decreaseStat.setImageResource(android.R.drawable.arrow_up_float);
                    decreaseStat.setRotation(270);
                    decreaseStat.setMinimumHeight(dpToPx(48));
                    decreaseStat.setMinimumWidth(48);
                    decreaseStat.setOnClickListener(new OnClickIncreaseStats(gamePiece, uiId,editText,statModifier,false));

                    tableRow.addView(decreaseStat);
                    tableRow.addView(editText);

                    ImageButton increaseStat = new ImageButton(contextThemeWrapper);
                    increaseStat.setImageResource(android.R.drawable.arrow_up_float);
                    increaseStat.setRotation(90);
                    increaseStat.setMinimumHeight(dpToPx(48));
                    increaseStat.setMinimumWidth(48);
                    increaseStat.setOnClickListener(new OnClickIncreaseStats(gamePiece, uiId,editText,statModifier,true));

                    tableRow.addView(increaseStat);
                    tableLayout.addView(tableRow);
                }
                hasBeenInflated = true;
            }
            else
            {
                ShowPopup();
            }
            isVisible = true;

            ((Button)popup.findViewById(R.id.SaveModelPopup)).setOnClickListener(new OnClickListenerModelSave(matchup));
        }
    }

    private class OnClickIncreaseStats implements View.OnClickListener
    {
        private final GamePiece gamePiece;
        private final EditText textToChange;
        private final StatModifier statModifier;
        private final UIIdentifier uiId;
        private final short amount;
        public OnClickIncreaseStats(GamePiece gamePiece, UIIdentifier uiId, EditText textToChange, StatModifier statModifier, boolean increase)
        {
            this.gamePiece = gamePiece;
            this.uiId = uiId;
            this.textToChange = textToChange;
            this.statModifier = statModifier;
            if (increase) amount = 1;
            else amount = -1;
        }
        @Override
        public void onClick(View buttonClicked) {
            gamePiece.getStatModifiers().AddToModifier(statModifier,amount);
            int value = gamePiece.getStatModifiers().GetModifier(statModifier);
            textToChange.setText(context.getString(R.string.SingleIntegerText,gamePiece.getStatModifiers().GetModifier(statModifier)));
            modifierEditing.UpdateModifierValue(statModifier, value,uiId);
        }
    }

    private class  OnClickListenerModelSave implements View.OnClickListener
    {
        private final Matchup matchup;

        public OnClickListenerModelSave( Matchup matchup)
        {
            this.matchup = matchup;
        }

        @Override
        public void onClick(View view)
        {
            FileHandler.GetInstance().saveMatchup(matchup);
            ClosePopup();
        }
    }


    public void ShowPopup()
    {
        popup.findViewById(R.id.ConstraintLayoutModelPopup).setVisibility(View.VISIBLE);
        isVisible = true;
    }

    public void ClosePopup()
    {
        if(hasBeenInflated)
        {
            popup.findViewById(R.id.ConstraintLayoutModelPopup).setVisibility(View.GONE);
            isVisible = false;
        }
    }
}
