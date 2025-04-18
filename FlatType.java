public class FlatType {
    private String typeName;
    private int basePrice;
    private int availableUnits;
    
    public FlatType(String typeName, int availableUnits, double basePrice) {
        this.typeName = typeName;
        this.availableUnits = availableUnits;
        this.basePrice = (int) basePrice;
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
    
    public int getRemainingUnits() {
        return availableUnits;
    }
    
    public String getName() {
        return typeName;
    }
    
    public double getPrice() {
        return basePrice;
    }
    
    public int getUnitCount() {
        return availableUnits;
    }
    
    public void setUnitCount(int count) {
        if (count >= 0) {
            this.availableUnits = count;
        }
    }
}
