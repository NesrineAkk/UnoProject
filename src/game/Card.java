package game;


public class Card {
    public enum Color {
        Red, Blue, Green, Yellow, Black;
        
        private static final Color[] colors = Color.values();
        
        public static Color getColor(int i) {
            return Color.colors[i];
        }
    }
   

    public enum Value {
        Zero, One, Two, Three, Four, Five, Six, Seven, Eight, Nine,
        DrawTwo, Skip, Reverse, Wild, WildFour;
        
        private static final Value[] values = Value.values();
        
        public static Value getValue(int i) {
            return Value.values[i];
        }
    }
    
    

    private Color color;
    private Value value;
    
    public Card(Color color, Value value) {
        this.color = color;
        this.value = value;
    }
    
    public String getType() {
        switch (this.value) {
            case DrawTwo:
                return "+2";
            case Skip:
                return "Skip";
            case Reverse:
                return "Reverse";
            case Wild:
                return "Wild";
            case WildFour:
                return "Wild+4";
            default:
                return "Number";
        }
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Value getValue() {
        return value;
    }
    public String getImageName() {
        String colorCode = "";

        switch (color) {
            case Red: colorCode = "r"; break;
            case Blue: colorCode = "b"; break;
            case Green: colorCode = "g"; break;
            case Yellow: colorCode = "y"; break;
            case Black:
                if (value == Value.Wild) return "Kw";
                if (value == Value.WildFour) return "Kw+";
        }

        String valueCode = "";

        switch (value) {
            case Zero: valueCode = "0"; break;
            case One: valueCode = "1"; break;
            case Two: valueCode = "2"; break;
            case Three: valueCode = "3"; break;
            case Four: valueCode = "4"; break;
            case Five: valueCode = "5"; break;
            case Six: valueCode = "6"; break;
            case Seven: valueCode = "7"; break;
            case Eight: valueCode = "8"; break;
            case Nine: valueCode = "9"; break;
            case DrawTwo: valueCode = "+"; break;
            case Skip: valueCode = "@"; break;
            case Reverse: valueCode = "$"; break;
            default: return "unknown_card.png"; 
        }

        return  colorCode + valueCode ;
    }
    
    @Override
    public String toString() {
        return displayWithBorder();
    }
    
    public String displayWithBorder() {
        String border = "--------------\n";
        String cardInfo;
        
        // For Wild cards, show both the card type and the chosen color if not Black
        if (value == Value.Wild || value == Value.WildFour) {
            if (color == Color.Black) {
                cardInfo = String.format("| %-13s |\n", value);
            } else {
                // Show both the wild card type and the chosen color
                cardInfo = String.format("| %-13s |\n", value) + 
                          String.format("| COLOR: %-7s |\n", color);
            }
        } 
        // For regular cards, show color and value
        else {
            String cardText = color + " " + value;
            cardInfo = String.format("| %-13s |\n", cardText);
        }
        
        return border + cardInfo + border;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Card other = (Card) obj;
        // For special comparison logic based on UNO rules
        return this.value == other.value && this.color == other.color;
    }
}