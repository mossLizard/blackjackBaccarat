
import java.util.ArrayList;
import java.util.*;
import java.util.List;

public class CardDeck 
{
   
    private List<String> Deck;
    private static final String[] VALID_NUMBERS = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Jack", "Queen", "King"};
    private static final String[] VALID_SUITS = {"Spades", "Diamonds", "Clubs", "Hearts"};
    private static final String[] UNIQUE_CARDS = {"Blank","Hidden"};
    private int CardValue;  
    private List<Integer> CardValues;
    private Random Random;
    
 
        public CardDeck() {
        this.Deck = new ArrayList<>(); 
        this.CardValues = new ArrayList<>(); 
        this.Random = new Random();
        CreateDeck(1); 
        ShuffleDeck();
    } 
    
    public void CreateDeck(int numberOfDecks) { // Modified to match TestGame's signature
        for (int d = 0; d < numberOfDecks; d++) {
            for (String number : VALID_NUMBERS) {
                for (String suit : VALID_SUITS) {
                    Deck.add(number + "Of" + suit); // Changed to match TestGame's card string format
                    CardValues.add(GetCardValue(number)); // Get value based on the 'number' (rank)
                }
            }
        }
    }

     public void ShuffleDeck() {
        Collections.shuffle(Deck);
    } 
    
    public int GetCardValue(String card) {
        switch (card) {
            case "Ace":
                return 11; // Ace can be 1 or 11, we'll handle the 1 case later
            case "Two":
                return 2;
            case "Three":
                return 3;
            case "Four":
                return 4;
            case "Five":
                return 5;
            case "Six":
                return 6;
            case "Seven":
                return 7;
            case "Eight":
                return 8;
            case "Nine":
                return 9;
            case "Ten":
            case "Jack":
            case "Queen":
            case "King":
                return 10;
            default:
                return 0; // Should not happen
        }
    }

    public String DrawCard() {
        if (Deck.isEmpty()) {
            return null; // Deck is empty
        }
        int RandomIndex = Random.nextInt(Deck.size());
        String DrawnCard = Deck.remove(RandomIndex);
        // We are not directly using CardValues here in the drawCard method
        return DrawnCard;
    }
    



    public boolean isEmpty(){
        return Deck.isEmpty();
    }

    public int size(){
        return Deck.size();
    }

    // Example usage 
   // public static void main(String[] args) {
       // CardDeck deck = new CardDeck();
        //System.out.println(deck.drawCard());
       // System.out.println(deck.drawCard()); 
        //System.out.println(deck.drawCard());
       // System.out.println(deck.drawCard());
        //System.out.println(deck.drawCard());
       // System.out.println(deck.drawCard());
       // System.out.println(deck.drawCard());
        //System.out.println(deck.drawCard());
        //System.out.println(deck.size());
    //}
}
    


