public class FlatType{
    private String typeName;
    private int basePrice;
    private int availableUnits;

    public FlatType(String typeName, int basePrice, int availableUnits) {
        this.typeName = typeName;
        this.basePrice = basePrice;
        this.availableUnits = availableUnits;
    }
    public boolean isAvail() {
        return availableUnits > 0;
    }

    
    public void decrementUnit() {
        if (availableUnits > 0) {
            availableUnits--;
        }
    }

    
    public void incrementUnit() {
        availableUnits++;
    }
}