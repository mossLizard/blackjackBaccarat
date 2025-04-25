import java.util.*;

public class TestGame{
	
	// Cleaned-up test game, demonstrating how I intend the displays to work.
	/**
	 * The "game" shows the player five cards and allows them to pick one to replace with another card from the deck.
	 * INPUTS:  The "game" will need to know which card the player clicked, or if they just refreshed the page.
	 * OUTPUTS: The "game" will need to draw the cards in hand, the number of cards left in the deck, and the number of cards that have been discarded
	 */
	

    private ArrayList<String> deck = new ArrayList<String>();
    private ArrayList<String> discards = new ArrayList<String>();
    private static final String[] VALID_NUMBERS = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Jack", "Queen", "King"};
    private static final String[] VALID_SUITS = {"Spades", "Diamonds", "Clubs", "Hearts"};
    private static final String[] UNIQUE_CARDS = {"Blank","Hidden"};
    
    //any other variables we need here
    
    private ArrayList<String> playerHand = new ArrayList<String>();
	String cardSlots[] = {"card0","card1","card2","card3","card4"};

	public TestGame() {
		// set up deck & hand
		deck = createDeck(1);
		deck.add("Joker");
		Collections.shuffle(deck);
		// discard is already empty. I hope.
		for(int i = 0; i < cardSlots.length; i++){
			playerHand.add(drawFromDeck());
		}
		System.out.println("    GAME: Player's hand = " + playerHand.toString());
	}
	
	public String drawFromDeck() { // modified a bit to recycle cards from discard. Avoids duplicates.
        if (deck.isEmpty()) {
        	moveDiscardsToDeck();
			Collections.shuffle(deck);
        }
        return deck.remove(0);
    }
    
    public ArrayList<String> createDeck(int numberOfDecks) { // thank you S. Jacobs
        ArrayList<String> newDeck = new ArrayList<>();
        for (int d = 0; d < numberOfDecks; d++) {
            for (String number : VALID_NUMBERS) {
                for (String suit : VALID_SUITS) {
                    newDeck.add(number + "Of" + suit);
                }
            }
        }
        return newDeck;
    }
    
    public String replaceCard(int cardIndex){
    	String cardToReplace = playerHand.get(cardIndex); // copy card
    	playerHand.set(cardIndex, drawFromDeck()); // overwrite original
    	discards.add(cardToReplace); // send copy to discards.
    	return cardToReplace; // return copy just for fun.
	}
	
	public void moveDiscardsToDeck(){
		deck.addAll(discards); // move discards to deck
		discards.clear(); // clear discards
	}
    
    
    // THIS IS THE THING CALLED BY THE DISPLAY
    
	public ArrayList<ArrayList<String>> runGameLogic(String lastAction, String lastButton) {
		/** INPUTS:
		 *  0: last action the player took (in this case, "refresh" or "clickCard")
		 *  1: last button / card the player clicked (in this case, "card0", "card1" etc)
		 */
		//String lastAction = inputs.get(0); 
		/** OUTPUTS:
		 *  0: The player's hand
		 *   [0-4]: individual cardStrings
		 *  1: Deck information
		 *   [0]: number of cards left in deck (as a string. Display can parse it if need be)
		 *   [1]: number of cards that have been discarded (I could derive this but I'm lazy...)
		 *   [2]: last card discarded (so it can appear on top of the draw pile)
		 */
		ArrayList<ArrayList<String>> outputs = new ArrayList<ArrayList<String>>();
		// it's nested like this so you can return as many lists OR values as you want
		
		// GAME LOGIC -------------------------------
		String lastCard = "Hidden";
		if(lastAction.equals("refresh")){
		    System.out.println("    GAME: Refresh detected. Clearing discards.");
		    moveDiscardsToDeck();
		}
		else if(lastAction.equals("clickCard")){
			int cardClicked = 0;
			for(String slot : cardSlots){
				if(lastButton.equals(slot)){break;}
				cardClicked = cardClicked + 1;
			} // cardClicked might end up out of range here, but not to worry ...
			if(cardClicked < playerHand.size()){ // ... I have a permit.
				lastCard = replaceCard(cardClicked);
				System.out.println("    GAME: Replaced card #" + cardClicked + ".");
			}
			else {System.out.println("    GAME: !Registered click but didn't find a matching card or button! Ignoring input.");}
		}
		// END GAME LOGIC --------------------------------
		
		// add elements to the output list
		outputs.add(playerHand); // output 0
		ArrayList<String> gameStatus = new ArrayList<String>();
		gameStatus.add("" + deck.size());
		gameStatus.add("" + discards.size());
		gameStatus.add(lastCard);
		outputs.add(gameStatus); // output 1
		
		
		return outputs;
		// END OF runGameLogic(). The display will now print to the screen based on what you gave as outputs.
	}
	
	
}