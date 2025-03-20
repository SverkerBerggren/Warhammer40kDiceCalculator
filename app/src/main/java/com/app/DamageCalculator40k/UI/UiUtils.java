package com.app.DamageCalculator40k.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.AbilityUIHolder;
import com.app.DamageCalculator40k.Activities.Activity_Edit_Abilities;
import com.app.DamageCalculator40k.Activities.EditUnitActivity;
import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.Enums.StatModifier;
import com.app.DamageCalculator40k.Identifiers.Identifier;
import com.app.DamageCalculator40k.Identifiers.IdentifierUtils;
import com.app.DamageCalculator40k.Identifiers.UIIdentifier;
import com.app.DamageCalculator40k.R;

import java.util.ArrayList;

public class UiUtils {

    public static void CreateModifiers(GamePiece gamePiece, UIIdentifier uiId , ImageButton button, TableLayout parentLayout, Context context, EditModifierPopup editModifierPopup)
    {
        parentLayout.setTag(uiId);

        ContextThemeWrapper themeNames = new ContextThemeWrapper(context, R.style.TableRowModifierName);
        ContextThemeWrapper themeValues = new ContextThemeWrapper(context,R.style.TableRowModifierValues);
        TableRow tableRowNames = new TableRow(themeNames);
        TableRow tableRowValues = new TableRow(themeValues);
        for(StatModifier statModifier : StatModifier.values())
        {
            TextView textViewName = new TextView(themeNames);
            textViewName.setText(statModifier.GetAbbreviation());
            tableRowNames.addView(textViewName);


            TextView textViewValues = new TextView(themeValues);
            textViewValues.setText( context.getString(R.string.SingleIntegerText,gamePiece.getStatModifiers().GetModifier(statModifier)));
            tableRowValues.addView(textViewValues);
        }
        parentLayout.addView(tableRowNames);
        parentLayout.addView(tableRowValues);

        button.setOnClickListener(editModifierPopup.GetOpenModifierPopupOnClickListener(gamePiece,uiId));
        button.setId(R.id.noId);
    }

    public static int GetUIResourceId(WidgetType widgetType, Identifier identifier)
    {
        switch (widgetType)
        {
            case AbilityEditButton:
                if(identifier.GetIdentifierEnum().equals(IdentifierType.MODEL))
                {
                    return R.id.EditModelAbilities;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.UNIT))
                {
                    return R.id.EditUnitAbilities;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.ARMY))
                {
                    return R.id.ArmyEditAbilitiesButton;
                }
                break;
            case AbilityLayout:
                if(identifier.GetIdentifierEnum().equals(IdentifierType.MODEL))
                {
                    return R.id.AbilityLayoutModels;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.UNIT))
                {
                    return R.id.AbilityLayoutUnits;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.ARMY))
                {
                    return  R.id.ArmyAbilityLayout;
                }
        }
        return 0;
    }

    public static class OnClickListenerEditAbilites implements View.OnClickListener
    {
        private final UIIdentifier uiId;
        private final ActivityResultLauncher<Intent> editActivity;
        private final Context context;
        private final Identifier identifier;

        // edit activity can be null. That means that there is no callback that needs to be handled form the result of the activity
        public OnClickListenerEditAbilites( Identifier identifier, UIIdentifier uiId, ActivityResultLauncher<Intent> editActivity, Context context)
        {
            this.uiId = uiId;
            this.editActivity = editActivity;
            this.context = context;
            this.identifier = identifier;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, Activity_Edit_Abilities.class);

            //TODO: ASAP maste fixa att R.string.UNIT_IDENTIFIER ar en enum eller gettas. Enum eller kosntant?
            IdentifierUtils.FillIntentWithIdentifier(intent,identifier);

            IdentifierUtils.FillIntentWithIdentifier(intent,identifier);
            intent.putExtra("" +R.string.UI_IDENTIFIER, uiId.widgetType);
            if(editActivity != null)
            {
                editActivity.launch(intent);
            }
            else
            {
                // TODO: Ar detta bugg maxxing?
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity(intent);
            }
        }
    }

    public static class UpdateUiActivityCallbackAbilities implements ActivityResultCallback<ActivityResult>
    {
        private final AbilityUIHolder abilityUIHolder;
        public UpdateUiActivityCallbackAbilities(AbilityUIHolder abilityUIHolder)
        {
            this.abilityUIHolder = abilityUIHolder;
        }
        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            Intent data = result.getData();
            Identifier abilityHolderIdentifier = IdentifierUtils.GetIdentifierFromExtra(data);

            ArrayList<String> abilitiesToRemove = data.getStringArrayListExtra("abilitiesRemoved");
            ArrayList<String> abilitiesToAdd = data.getStringArrayListExtra("abilitiesAdded");

            UIIdentifier uiId = new UIIdentifier((WidgetType) data.getSerializableExtra(""+ R.string.UI_IDENTIFIER),abilityHolderIdentifier);

            abilityUIHolder.UpdateAddedAndRemovedAbilities(abilitiesToAdd,abilitiesToRemove,uiId);
        }
    }

    public static void UpdateAbilityRow(ArrayList<String> abilitiesToAdd, ArrayList<String> abilitiesToRemove, UIIdentifier uiId, Context context, View parentView)
    {
        TableLayout tableLayout = parentView.findViewWithTag(uiId);
        tableLayout.getChildAt(0).setBackgroundColor( Color.parseColor("#AA00FF"));
        ArrayList<View> viewsToRemove = new ArrayList<>();
        for(int i = 0; i < tableLayout.getChildCount(); i++)
        {
            TableRow childView = (TableRow) tableLayout.getChildAt(i);

            for(int z = 0; z < childView.getChildCount(); z++)
            {

                TextView text = (TextView) childView.getChildAt(z);

                if(abilitiesToRemove.contains(text.getText().toString()))
                {
                    viewsToRemove.add(childView);
                }
            }
        }
        for( View view : viewsToRemove)
        {
            tableLayout.removeView(view);
        }

        for(int i = 0; i < abilitiesToAdd.size(); i++)
        {
            tableLayout.addView(CreateAbilityTableRow(context,abilitiesToAdd.get(i)));
        }
    }

    public static View CreateAbilityTableRow(Context context, String abilityName)
    {
        ContextThemeWrapper abilityTheme = new ContextThemeWrapper(context,R.style.AbilitiesTable);

        TableRow tableRow = new TableRow(abilityTheme);
        TextView abilityTextView = new TextView(abilityTheme);
        abilityTextView.setText(abilityName);
        tableRow.addView(abilityTextView);

        return tableRow;
    }

    public static void CreateGamePieceAbilities(View parentView, GamePiece gamePiece, Identifier identifier, Context context, ActivityResultLauncher<Intent> editAbilitiesResultLauncher)
    {
        TableLayout abilityTable =  parentView.findViewById(GetUIResourceId(WidgetType.AbilityLayout,identifier));

        ContextThemeWrapper abilityTheme = new ContextThemeWrapper(context,R.style.AbilitiesTable);
        for(Ability ability : gamePiece.GetAbilities())
        {
            abilityTable.addView(CreateAbilityTableRow(context, ability.name));
        }
        TableRow tableRowButton = new TableRow(abilityTheme);
        UIIdentifier uiId = new UIIdentifier(WidgetType.AbilityEditButton,identifier);
        ImageButton addButton = parentView.findViewById(GetUIResourceId(WidgetType.AbilityEditButton,identifier));

        addButton.setOnClickListener(new OnClickListenerEditAbilites(identifier, uiId,editAbilitiesResultLauncher,context));
        addButton.setTag(identifier.GetIdentifierEnum().GetResourceId(), identifier);
        abilityTable.addView(tableRowButton);
        abilityTable.setTag(uiId);
    }

    public static void DropDownClick(View v)
    {
        ViewGroup viewGroup =  (ViewGroup) v.getParent();

        for(int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View childView = viewGroup.getChildAt(i);

            if(childView.getTag() != null && childView.getTag().equals(R.string.ExcludeDropDownTag))
            {
                continue;
            }
            if(childView != v)
            {
                if(childView.getVisibility() == View.VISIBLE)
                {
                    childView.setVisibility(View.GONE);
                }
                else
                {
                    childView.setVisibility(View.VISIBLE);
                }
            }
        }

    }
}
