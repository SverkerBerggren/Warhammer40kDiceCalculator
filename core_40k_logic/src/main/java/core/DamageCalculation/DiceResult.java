package core.DamageCalculation;

public class DiceResult {

    public int result;

    public boolean isD6Roll = false;
    public boolean isD3Roll = false; // HELLLOOOOO

    // According to the rules a dice can never be re rolled more than once
    public boolean hasBeenReRolled = false;

    // Signifies if the dice should still go through the "regular" steps or not. Devastating wounds stops the regular wound sequence for example
    public boolean continueRegularCalculation = true;

    public DiceResult(int result)
    {
        this.result = result;
    }
}
