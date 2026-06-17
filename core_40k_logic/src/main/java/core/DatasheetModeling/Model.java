package core.DatasheetModeling;


import java.util.ArrayList;

public class Model extends GamePiece  implements DeactivatableInterface{

    public String name;
    public int toughness = -1;;
    public int strength = -1;
    public int armorSave = -1;
    //Todo: Fix invulnerable save condition
    public int invulnerableSave = 7;
    public int wounds = -1;
    public int attacks =  -1;

    public boolean active = true;


    public ArrayList<Weapon> weapons = new ArrayList<>();

    public Model Copy()
    {
        Model modelToReturn = new Model();
        modelToReturn.toughness = toughness;
        modelToReturn.strength = strength;
        modelToReturn.armorSave = armorSave;
        modelToReturn.invulnerableSave = invulnerableSave;
        modelToReturn.wounds = wounds;
        modelToReturn.attacks=  attacks;
        modelToReturn.active = active;
        modelToReturn.name = name;
        modelToReturn.setStatModifiers( getStatModifiers().Copy());
        ArrayList<Weapon> newList = new ArrayList<>();

        for(Weapon weapon : weapons)
        {
            newList.add(weapon.Copy());
        }

        modelToReturn.weapons = newList;
        return modelToReturn;

    }

    public Model()
    {

    }

    @Override
    public void FlipActive() {
        active = !active;
    }
}
