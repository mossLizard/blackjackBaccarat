import java.util.*;

public class Baccarat2 {
    /*-----------------------------------------------------
     * Card Class
     * - Represents a playing card with a number, suit,
     *   and a hidden status.
     *-----------------------------------------------------*/
    static class Card {
        private String number;
        private String suit;
        private boolean hidden;

        public Card(String number, String suit) {
            this.number = number;
            this.suit = suit;
            this.hidden = false;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public boolean isHidden() {
            return hidden;
        }

        public int getBaccaratValue() {
            //if (hidden) return 0;
            switch (number) {
                case "Ace":   return 1;
                case "Two":   return 2;
                case "Three": return 3;
                case "Four":  return 4;
                case "Five":  return 5;
                case "Six":   return 6;
                case "Seven": return 7;
                case "Eight": return 8;
                case "Nine":  return 9;
                default:       return 10;
            }
        }

        @Override
        public String toString() {
            return number + "Of" + suit + (hidden ? "Hidden" : "");
        }
    }

    /*-----------------------------------------------------
     * Deck Class
     * - Manages creation, shuffling, and drawing of cards.
     *-----------------------------------------------------*/
    static class Deck {
        private List<Card> cards;
        private static final String[] VALID_NUMBERS = {
            "Ace","Two","Three","Four","Five","Six",
            "Seven","Eight","Nine","Ten","Jack","Queen","King"
        };
        private static final String[] VALID_SUITS = {"Spades","Diamonds","Clubs","Hearts"};

        public Deck(int numberOfDecks) {
            cards = new ArrayList<>();
            for (int d = 0; d < numberOfDecks; d++) {
                for (String num : VALID_NUMBERS) {
                    for (String suit : VALID_SUITS) {
                        cards.add(new Card(num, suit));
                    }
                }
            }
            shuffle();
        }
        
        public String toString(){
          String sto = "{";
          for(int i = 0; i < cards.size(); ++i){
            sto = sto + cards.get(i);
            if(i +1 < cards.size()){sto = sto + ", ";}
          }
          return sto + "}";
        }

        public void shuffle() {
            Collections.shuffle(cards);
        }

        public Card drawCard() {
            if (cards.isEmpty()) {
                Deck newDeck = new Deck(2);
                cards = newDeck.cards;
            }
            return cards.remove(0);
        }
    }

    private Deck deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> bankerHand;
    private String gameState = "reset"; 
    private String lastPlayerAction = "none";
    private CardUtils cardUtils = new CardUtils();
    int playerWallet = 200;
    int playerBet = 5;
    String betType = "tie";

    
    /** GAME STATE
     * 
     * We always start in the first state, "reset"
     * When the player clicks a button, the next state is chosen based on 
     *   the state of the game and/or what the player chose to click.
     * 
     * STATE "reset": Big title / start screen. Set player wallet back to initial amount and reset a few variables.
     * --> "setup" if "Start" button chosen
     * 
     * STATE "setup": Make a bet! Display player wallet. Player uses a spinbox / number entry to enter the number of chips to bet.
     * --> "playerTurn" if "Start Game" button chosen
     * --> "reset" on "Back" button chosen
     * --> "rules" if "Rules" button chosen
     * 
     * STATE "rules": display rules of the game
     * --> "setup"
     *   
     * STATE "playerTurn": Deal 2 cards each to player & dealer. Player and dealer's hands and totals are shown. The player makes a decision.
     * --> "resolve" if at least one hand has a total >= 8
     * --> "playerDraw" if player total is 0-5 
     * --> "playerStand" otherwise
     * 
     * STATE "playerDraw": Draw a third card for the player. Show hands. The dealer will now make a decision.
     * --> "dealerDraw" if dealer's total is within certain bounds, according to the below table
     * --> "dealerStand" otherwise
     * 
     * + 3rd card  + draw if total <
     * | A or face | 3
     * | 9 or 10   | 3
     * | 8         | 2
     * | 6 or 7    | 6
     * | 4 or 5    | 5
     * | 2 or 3    | 4
     * 
     * STATE "playerStand": The player does not draw a new card. Show hands. The dealer will now make a decision.
     * --> "dealerDraw" if player total is 0-5 
     * --> "dealerStand" otherwise
     * 
     * STATE "dealerDraw": Draw an extra card for the dealer. Show hands.
     * --> "resolve"
     * 
     * STATE "dealerStand": The dealer does not draw a third card. Show hands.
     * --> "resolve"
     * 
     * STATES "resolve": Resolutions. Calculate outcome and change the screen based on who won.
     *   I could make this a bunch of different states if need be, but one state worked fine for baccarat
     * --> "reset" if we clicked "Reset Game" button
     * --> "setup" if we clicked "Another Round" button
     */
    public Baccarat2() {
        deck = new Deck(2);
        System.out.println("  GAME: INIT");
        playerHand = new ArrayList<>();
        bankerHand = new ArrayList<>();
    }
    public void resetDecks() {
        deck = new Deck(2);
        System.out.println("  GAME: RESET");
        playerHand = new ArrayList<>();
        bankerHand = new ArrayList<>();    	
        playerWallet = 200;
    }
    public void testDraw() {
        deck = new Deck(2);
        System.out.println("  GAME: TESTDRAW");
        playerHand = new ArrayList<>();
        bankerHand = new ArrayList<>(); 
        playerHand.clear();
        bankerHand.clear();

        // Initial deal
        Card drawnCard = deck.drawCard();
        playerHand.add(drawnCard);
        drawnCard = deck.drawCard();
        playerHand.add(drawnCard);
        drawnCard = deck.drawCard();
        //drawnCard.setHidden(true);
        bankerHand.add(drawnCard);
        drawnCard = deck.drawCard();
        //drawnCard.setHidden(true);
        bankerHand.add(drawnCard);
    }
    
    public ArrayList<String> handAsStrings(ArrayList<Card> inputHand){
      ArrayList<String> sto = new ArrayList<String>();
      for(int i = 0; i < inputHand.size(); ++i){
        sto.add(inputHand.get(i).toString());
      }
      return sto;
    }
    // Calculate total modulo 10
    private int calculateHandValue(List<Card> hand) {
        int sum = 0;
        for (Card c : hand) sum += c.getBaccaratValue();
        return sum % 10;
    }
    
    // Play one round with full third-card logic
    public void playRound() {
        playerHand.clear();
        bankerHand.clear();

        // Initial deal
        playerHand.add(deck.drawCard());
        playerHand.add(deck.drawCard());
        bankerHand.add(deck.drawCard());
        bankerHand.add(deck.drawCard());

        int playerTotal = calculateHandValue(playerHand);
        int bankerTotal = calculateHandValue(bankerHand);

        System.out.println("Initial Player Hand: " + playerHand + " | Total: " + playerTotal);
        System.out.println("Initial Banker Hand: " + bankerHand + " | Total: " + bankerTotal);

        // Natural check
        if (playerTotal >= 8 || bankerTotal >= 8) {
            System.out.println("Natural - no extra cards.");
            //determineWinner(playerTotal, bankerTotal);
            return;
        }

        // Player third card rule
        Card playerThird = null;
        if (playerTotal <= 5) {
            playerThird = deck.drawCard();
            playerHand.add(playerThird);
            playerTotal = calculateHandValue(playerHand);
            System.out.println("Player draws: " + playerThird + " | New Total: " + playerTotal);
        } else {
            System.out.println("Player stands.");
        }

        // Banker third card rule
        if (playerThird == null) {
            if (bankerTotal <= 5) {
                Card bDraw = deck.drawCard();
                bankerHand.add(bDraw);
                bankerTotal = calculateHandValue(bankerHand);
                System.out.println("Banker draws (player stood): " + bDraw + " | New Total: " + bankerTotal);
            } else {
                System.out.println("Banker stands (player stood).");
            }
        } else {
            int pVal = playerThird.getBaccaratValue();
            if (bankerTotal <= 2
             || (bankerTotal == 3 && pVal != 8)
             || (bankerTotal == 4 && pVal >= 2 && pVal <= 7)
             || (bankerTotal == 5 && pVal >= 4 && pVal <= 7)
             || (bankerTotal == 6 && (pVal == 6 || pVal == 7))) {
                Card bDraw = deck.drawCard();
                bankerHand.add(bDraw);
                bankerTotal = calculateHandValue(bankerHand);
                System.out.println("Banker draws: " + bDraw + " | New Total: " + bankerTotal);
            } else {
                System.out.println("Banker stands.");
            }
        }

        //determineWinner(playerTotal, bankerTotal);
    }
    
    public String resolveBet(String gameResult){
      float betChange = 0.0f;
      if(gameResult.equals(betType)){
        betChange = (playerBet * 1.0f);
      }
      else{
        betChange =(playerBet * -0.85f); // math.floor doesn't seem to be a thing????
      }
      playerWallet = (playerWallet + (int)betChange);
      if(betChange > 0){
        return "You have won " + (int)betChange + " chips!";
      }
      return "You have lost " + ((int)betChange*-1) + " chips!";
    };
    
    public ArrayList<ArrayList<String>> runGameLogic(String lastAction, String lastTarget, String auxInputs[]){
      ArrayList<ArrayList<String>> outputs = new ArrayList<ArrayList<String>>();
      ArrayList<String> firstOuts = new ArrayList<String>();
      ArrayList<String> secondOuts = new ArrayList<String>();
      ArrayList<String> buttons = new ArrayList<String>();
      String winText = "No Winner";
      String secondWinText = "The game continues...";
      int playerTotal = calculateHandValue(playerHand);
      int bankerTotal = calculateHandValue(bankerHand);
      System.out.println("  GAME: TEST IN " + lastAction + " " + lastTarget + " " + auxInputs);
      if(lastAction.equals("click")){
		switch (gameState) {
		  case "reset":
		    resetDecks();
		    gameState = "setup";
		    //buttons.add("start");
		    //buttons.add("rules");
			break;
		  case "rules":
		    gameState = "setup";
		    //buttons.add("start");
		    //buttons.add("rules");
			break;
		  case "setup":
		    if(lastTarget.equals("rules")){
		      gameState = "rules";
		      buttons.add("back");
		    }
		    else if(lastTarget.equals("submit")){
		      playerBet = Integer.parseInt(auxInputs[0]);
		      betType = auxInputs[1];
		      gameState = "playerTurn";
		      testDraw();
		      playerTotal = calculateHandValue(playerHand);
              bankerTotal = calculateHandValue(bankerHand);
		      // draw cards
		      buttons.add("Continue");
		    }
		    else{
		      gameState = "rules";
		      buttons.add("back");
		    }
			break;
		  case "playerTurn":
		    //check for natural win
		    if (playerTotal >= 8 || bankerTotal >= 8) {
              System.out.println("Natural - no extra cards.");
              if(playerTotal > bankerTotal){
                winText = "Player wins naturally!";
                secondWinText = "The player's hand score was above 7, so they win immediately! "+resolveBet("player");
              }
              else if(bankerTotal > playerTotal){
                winText = "Banker wins naturally!";
                secondWinText = "The banker's hand score was above 7, so they win immediately! "+resolveBet("banker");
              }
              else{
                winText = "A tie occured naturally!";
                secondWinText = "Both player and banker had matched scores above 7, so the game ends here! "+resolveBet("tie");
              }
              gameState = "resolve";
    	      buttons.add("Okay");
            } 
            else{ // no natural win
        	  if (playerTotal <= 5) {
     	        Card playerThird = deck.drawCard();
    	        playerHand.add(playerThird);
    	        playerTotal = calculateHandValue(playerHand);
    	        System.out.println("  GAME: Player draws: " + playerThird + " | New Total: " + playerTotal);
    	        gameState = "playerDraw";
    	        buttons.add("Continue");
        	  } else {
                System.out.println("  GAME: Player stands.");
                gameState = "playerStand";
    	        buttons.add("Continue");
        	  }
            }
			break;
		  case "playerDraw":
        	if (playerHand.size() < 3) { // no third card
              if (bankerTotal <= 5) {
                  Card bDraw = deck.drawCard();
                  bankerHand.add(bDraw);
                  bankerTotal = calculateHandValue(bankerHand);
                  System.out.println("  GAME: Banker draws (player stood): " + bDraw + " | New Total: " + bankerTotal);
                  gameState = "bankerDraw";
              } else {
                  System.out.println("  GAME: Banker stands (player stood).");
                  gameState = "bankerStand";
              }
          } else {
              Card playerThird = playerHand.get(2);
              int pVal = playerThird.getBaccaratValue();
              if (bankerTotal <= 2
               || (bankerTotal == 3 && pVal != 8)
               || (bankerTotal == 4 && pVal >= 2 && pVal <= 7)
               || (bankerTotal == 5 && pVal >= 4 && pVal <= 7)
               || (bankerTotal == 6 && (pVal == 6 || pVal == 7))) { // baccarat truth table from hell!
                  Card bDraw = deck.drawCard();
                  bankerHand.add(bDraw);
                  bankerTotal = calculateHandValue(bankerHand);
                  System.out.println("  GAME: Banker draws: " + bDraw + " | New Total: " + bankerTotal);
                  gameState = "bankerDraw";
              } else {
                  System.out.println("  GAME: Banker stands.");
                  gameState = "bankerStand";
              }
        	}
		    //gameState = "reset";
		    buttons.add("Continue");
			break;
		  case "playerStand":
        	if (playerHand.size() < 3) { // COPY FROM PLAYERDRAW STATE because I amtoo tired for this
              if (bankerTotal <= 5) {
                  Card bDraw = deck.drawCard();
                  bankerHand.add(bDraw);
                  bankerTotal = calculateHandValue(bankerHand);
                  System.out.println("  GAME: Banker draws (player stood): " + bDraw + " | New Total: " + bankerTotal);
                  gameState = "bankerDraw";
              } else {
                  System.out.println("  GAME: Banker stands (player stood).");
                  gameState = "bankerStand";
              }
          } else {
              Card playerThird = playerHand.get(2);
              int pVal = playerThird.getBaccaratValue();
              if (bankerTotal <= 2
               || (bankerTotal == 3 && pVal != 8)
               || (bankerTotal == 4 && pVal >= 2 && pVal <= 7)
               || (bankerTotal == 5 && pVal >= 4 && pVal <= 7)
               || (bankerTotal == 6 && (pVal == 6 || pVal == 7))) { // baccarat truth table from hell!
                  Card bDraw = deck.drawCard();
                  bankerHand.add(bDraw);
                  bankerTotal = calculateHandValue(bankerHand);
                  System.out.println("  GAME: Banker draws: " + bDraw + " | New Total: " + bankerTotal);
                  gameState = "bankerDraw";
              } else {
                  System.out.println("  GAME: Banker stands.");
                  gameState = "bankerStand";
              }
        	}
		    //gameState = "resolve";
		    buttons.add("Continue");
			break;
		  case "bankerStand":
		    gameState = "resolve";
		    buttons.add("Okay");
		    if(playerTotal > bankerTotal){
                winText = "Player wins!";
                secondWinText = "The player's hand score was above the banker's! "+resolveBet("player");
              }
              else if(bankerTotal > playerTotal){
                winText = "Banker wins!";
                secondWinText = "The banker's hand score was above the player's! "+resolveBet("banker");
                
              }
              else{
                winText = "A tie occured!";
                secondWinText = "Both player and banker had matched scores! "+resolveBet("tie");
              }
			break;
		  case "bankerDraw":
		    gameState = "resolve";
		    buttons.add("Okay");
		    if(playerTotal > bankerTotal){
                winText = "Player wins!";
                secondWinText = "The player's hand score was above the banker's! "+resolveBet("player");
              }
              else if(bankerTotal > playerTotal){
                winText = "Banker wins!";
                secondWinText = "The banker's hand score was above the player's! "+resolveBet("banker");
                
              }
              else{
                winText = "A tie occured!";
                secondWinText = "Both player and banker had matched scores! "+resolveBet("tie");
              }
			break;
		  case "invalid":
		    gameState = "reset";
		    buttons.add("reset");
			break;
		  case "resolve":
		    gameState = "setup";
		    //buttons.add("start");
		    //buttons.add("rules");
			break;
		  default:
		    System.out.println("  GAME: ERROR INVALID GAMESTATE '" + gameState + "'");
		    gameState = "invalid";
		    buttons.add("Okay");
		    resetDecks();
			break;
		}
	  
      }
      else {
		    gameState = "reset";
		    buttons.add("Begin");
		    resetDecks();
      }
      firstOuts.add(gameState);
      firstOuts.add(String.valueOf(playerWallet));
      firstOuts.add(String.valueOf(playerBet));
      firstOuts.add(winText);
      firstOuts.add(secondWinText);
      secondOuts.add(String.valueOf(playerTotal));
      secondOuts.add(String.valueOf(bankerTotal));
      outputs.add(firstOuts);
      outputs.add(secondOuts);
      outputs.add(handAsStrings(playerHand));
      outputs.add(handAsStrings(bankerHand));
      //if(buttons.size() == 0){
        //buttons.add("Start");
      //}
      outputs.add(buttons);
	  System.out.println("  GAME: Outputs: " + outputs);
      return outputs;
    }

    // Simple CLI for testing
    public void startGame() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Enter 'play' to deal or 'exit' to quit: ");
            String cmd = sc.next();
            if (cmd.equalsIgnoreCase("play")) {
                playRound();
            } else if (cmd.equalsIgnoreCase("exit")) {
                break;
            }
        }
        sc.close();
    }
}
