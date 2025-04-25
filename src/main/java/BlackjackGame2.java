import java.util.ArrayList;
//import java.util.List;
import java.util.Scanner;



public class BlackjackGame2{
    private CardDeck deck;
    private Hand playerHand;
    private Hand dealerHand;
    private Hand playerReserveHand;
    private int playerWallet;
    private int playerWalletInitial;
    private Scanner scanner = new Scanner(System.in);
    private int currentBet;
    private String gameState = "reset"; 
    private String lastPlayerAction = "none";
    //private CardUtils cardUtils = new CardUtils();
    public int splitCount = 0;
    public int testMode = 0;
    /** GAME STATE
     * Since the game needs to return after every player interaction, we need to keep track of what part of the game we are in.
     * 
     * 
     * STATE "reset": initialize / reset. Set player wallet back to initial amount Deal to player & dealer. Show a "rules / start" screen
     *   --> "setup" if "start" button chosen
     *   
     * STATE "setup": bet amount
     *   --> "choice" on confirm bet amount
     *   --> "reset" on "reset" button chosen
     *   
     * STATE "choice": Choice. hands shown. Player has choice of buttons.
     * input runs modified playerActions()
     * --> "choice" if player can take more actions
     * --> "dealer" if player stood or if player's hand is over 21
     * 
     * STATE "dealer": Dealer's turn. 
     * Dealer acts according to modified dealerTurn() BEFORE drawing!
     * --> 3 if dealer's hand has a value less than 17 (hit)
     *   print that dealer hit
     * if dealer busted or stood, print that dealer busted or stood, then determine outcome.
     *   outcome is not revealed to player, they must click the "Resolve" button.
     * --> "resolve" if round has ended
     * 
     * STATE "resolve": Resolutions. Display outcome & chip count in big text.
     * --> "reset" if "reset" chosen
     * --> "setup" if "new round" chosen
     */
    
    /** OUTPUTS
     * 
     * unconditional
     *   [0][0] : gamestate
     *   [0][1] : player chips
     *  ([1]*  ): information about what limits to put on player interaction.
     *   [2]*   : dealer's hand
     *   [3]*   : player's hand
     *   [4]*   : aux player's hand, if any.
     *  
     * "start"
     *   no extra outputs
     *   
     * "setup"
     *   [1][0] : min number of chips to bet
     *   [1][1] : max number of chips to bet
     *   
     * "choice"
     *   [0][2] : what player chose to do last interaction. "none" if first time in state "choice"
     *   [0][3] : estimated dealer hand value
     *   [0][4] : player hand value
     *   [0][5] : bet amount
     *   [1][0] : 
     *   [1]*   : what options the player has open to them
     *   
     * "dealer"
     *   [0][2] : what dealer chose to do last interaction. This will never be "none" because dealer decides BEFORE we draw.
     *   [0][3] : dealer hand value
     *   [0][4] : player hand value
     *   [0][5] : bet amount
     *   [1][0] : what to write on the player button
     *   
     * resolution state
     *   [0][2] : resolution type ("playerWin", "dealerWin", "push") 
     *   [0][3] : dealer hand value
     *   [0][4] : player hand value
     *   [0][5] : bet amount
     *   [1][0] : "allow" if player can start a new round, "deny" or "none" if player must reset.
     */
    
    
    public BlackjackGame2(int initialWallet) {
        this.playerWallet = initialWallet;
        this.playerWalletInitial = initialWallet;
        this.deck = new CardDeck();
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.currentBet = 0;
        this.gameState = "reset";
        this.splitCount = 0;
    }
    
    public void printArr(String[] arlsstr) {
    	for(int i = 0; i < arlsstr.length; i++){
		      if(arlsstr[i]!= null){System.out.println("  GAME: [" + String.valueOf(i) + "] = " + arlsstr[i] );}
		    }
     }
    
    public ArrayList<ArrayList<String>> runGameLogic(String lastAction, String lastTarget, String auxInputs[]){
        System.out.println(" GAME: inputs are "+lastAction + " & " + lastTarget +".");
    	ArrayList<ArrayList<String>> outputs = new ArrayList<ArrayList<String>>();
    	ArrayList<String> gameStateList = new ArrayList<String>(); //[0]
    	ArrayList<String> buttonParams = new ArrayList<String>(); //[1]
    	//outputs.add(new ArrayList<String>());
    	//outputs.get
    	// 0: dealer hand
    	// 1: player hand
    	
		if(lastAction.equals("click")){
		  if(gameState.equals("reset")){
		    System.out.println(" GAME: start state!");
			//if(lastAction.equals("click")){
			  if(lastTarget.equals("setup")) {
			  	gameStateList = setGameState("setup");
			    gameStateList.add("none");
			    buttonParams.add(String.valueOf(Math.min(playerWallet, 5)));
			    buttonParams.add(String.valueOf(Math.min(playerWallet, 100)));
			  }
			//}
		  }
		  else if(gameState.equals("setup")){
			System.out.println(" GAME: setup state!");
			//if(lastAction.equals("click")){
			  if(lastTarget.equals("reset")){
			  	gameStateList = setGameState("reset");
			    gameStateList.add("none");
			  }
			  else if(lastTarget.equals("submit")){
			    initialDeal();
			  	lastPlayerAction = "none";
			  	currentBet = Integer.parseInt(auxInputs[0]);
			  	gameStateList = setGameState("choice");
			    gameStateList.add(lastPlayerAction);
			    gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			    gameStateList.add(String.valueOf(playerHand.getValue(deck)));
			    gameStateList.add(String.valueOf(currentBet));
			    String[] validButtons = getPlayerOptions();
			    System.out.println(validButtons);
			    for(int i = 0; i < validButtons.length; i++){
			      if(validButtons[i]!= null){buttonParams.add(validButtons[i]);}
			    }
			  }
			//}
		  }
		  else if(gameState.equals("choice")){
			System.out.println(" GAME: choice state!");
			//if(lastAction.equals("click")){
			  if(lastTarget.equals("Hit")){
			    
			  	lastPlayerAction = takePlayerAction("Hit");;
			  }
			  else if(lastTarget.equals("Stand")){
			  	lastPlayerAction = takePlayerAction("Stand");;
			  }
			  else if(lastTarget.equals("Double")){
			  	lastPlayerAction = takePlayerAction("Double");;
			  }
			  else if(lastTarget.equals("Split")){
				  	lastPlayerAction = takePlayerAction("Split");;
				  }
			  else if(lastTarget.equals("Swap")){
				  	lastPlayerAction = takePlayerAction("Swap");;
				  }
			  
			  if(playerHand.getValue(deck) > 21 || lastPlayerAction == "Stand"){
			    gameStateList = setGameState("resolvePlayer");
			  	//lastPlayerAction = "none";
			    gameStateList.add(lastPlayerAction);
			    gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			    gameStateList.add(String.valueOf(playerHand.getValue(deck)));
			    gameStateList.add(String.valueOf(currentBet));
			    buttonParams.add("Okay");
			  }
			  else { // more choices availabielalewsl
			    gameStateList = setGameState("choice");
			    gameStateList.add(lastPlayerAction);
			    gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			    gameStateList.add(String.valueOf(playerHand.getValue(deck)));
			    gameStateList.add(String.valueOf(currentBet));
			    String[] validButtons = getPlayerOptions();
			    System.out.println(validButtons);
			    for(int i = 0; i < validButtons.length; i++){
			      if(validButtons[i]!= null){buttonParams.add(validButtons[i]);}
			    }
			  }
			//}
		  }
		  else if(gameState.equals("resolvePlayer")){
			System.out.println(" GAME: resolvePlayer state!");
			gameStateList = setGameState("dealer");
			//buttonParams.add("Okay");
		  }
		  else if(gameState.equals("resolveDealer")){
			System.out.println(" GAME: resolveDealer state!");
			String gameResult = determineWinner(currentBet);
			gameStateList = setGameState("resolve");
			gameStateList.add(gameResult);
			gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			gameStateList.add(String.valueOf(playerHand.getValue(deck)));
			gameStateList.add(String.valueOf(currentBet));
			buttonParams.add("Okay");
		
		}
		else if(gameState.equals("resolve")){
			System.out.println(" GAME: RESOLVE state!");
		      buttonParams.add(String.valueOf(Math.min(playerWallet, 5)));
			if(splitCount == 0) { // reset to start
			    gameStateList = setGameState("setup");
		        gameStateList.add("none");
			    buttonParams.add(String.valueOf(Math.min(playerWallet, 5)));
			    buttonParams.add(String.valueOf(Math.min(playerWallet, 100)));
			}
			else
			{
			  splitCount -= 1;
			  gameStateList = setGameState("choice");
			  System.out.println(" GAME: Getting secondary hand...");
			  playerHand = playerReserveHand;
			  String[] validButtons = getPlayerOptions();
			  buttonParams = new ArrayList<String>(); // WHY IS THERE A FIVE HERE
			  for(int i = 0; i < validButtons.length; i++){
				System.out.println(" GAME: button " + String.valueOf(i) + " = " + validButtons[i]);
				if(validButtons[i]!= null){buttonParams.add(validButtons[i]);}
			  }
			  
			  gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			  gameStateList.add(String.valueOf(playerHand.getValue(deck)));
		      gameStateList.add(String.valueOf(currentBet));
		      gameStateList.add(String.valueOf(playerWallet));
		      
			}
		}
		if(gameState.equals("dealer")){ // NOT supposed to be if else. I pretend to know what I am doing
			System.out.println(" GAME: dealer state!");
			if(lastAction.equals("click")){
				String dealerAction = dealerTurn();
				if(dealerAction.equals("Stand") || dealerAction.equals("HitFailed")){
			    	gameStateList = setGameState("resolveDealer");
			    	gameStateList.add(dealerAction);
			    	gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			    	gameStateList.add(String.valueOf(playerHand.getValue(deck)));
			    	gameStateList.add(String.valueOf(currentBet));
					buttonParams.add("Okay");
				}
				else if(dealerAction.equals("HitSuccess") ){
			    	gameStateList = setGameState("dealer");
			    	gameStateList.add("Hit");
			    	gameStateList.add(String.valueOf(dealerHand.getValue(deck)));
			    	gameStateList.add(String.valueOf(playerHand.getValue(deck)));
			    	gameStateList.add(String.valueOf(currentBet));
					buttonParams.add("Okay");
				}
			}
		  }
		}
		else{ // lastAction != click, treat as a refresh
		    System.out.println(" GAME: Refresh!");
	    	gameStateList = setGameState("reset");
	        playerWallet = playerWalletInitial;
	        deck = new CardDeck();
	        playerHand = new Hand();
	        dealerHand = new Hand();
	        currentBet = 0;
	        splitCount = 0;
		}
		if(gameStateList.size() == 0){
    	  gameStateList.add(gameState); // [0][0]
    	  gameStateList.add(String.valueOf(playerWallet)); // [0][1]
		}
		
    	outputs.add(gameStateList); // [0]
    	outputs.add(buttonParams); // [1]
    	outputs.add((ArrayList<String>)dealerHand.getCards()); // [2]
    	outputs.add((ArrayList<String>)playerHand.getCards()); // [3]
    	
    	return outputs;
    }
    
    public ArrayList<String> setGameState(String newState){
      gameState = newState;
      ArrayList<String> gameStateList = new ArrayList<String>();
      gameStateList.add(newState); // [0][0]
      gameStateList.add(String.valueOf(playerWallet));
      return gameStateList;
    }
    
    public void initialDeal(){

        deck = new CardDeck();
        playerHand = new Hand();
        dealerHand = new Hand();
        String testCard = "";
        switch(testMode) {
          case 1:
        	testCard = deck.DrawCard(); // for testing splits
            playerHand.addCard(testCard);
            dealerHand.addCard(deck.DrawCard());
            playerHand.addCard(testCard);
            dealerHand.addCard(deck.DrawCard());
          break;
          default:
        	//testCard = deck.DrawCard();
            playerHand.addCard(deck.DrawCard());
            dealerHand.addCard(deck.DrawCard());
            playerHand.addCard(deck.DrawCard());
            dealerHand.addCard(deck.DrawCard());
          break;
        }
    }

    public void startGame() {
        while (playerWallet > 0) {
            currentBet = getBet();
            if (currentBet == 0) {
                System.out.println("Thanks for playing!");
                break;
            }

            deck = new CardDeck();
            playerHand = new Hand();
            dealerHand = new Hand();

            // Initial deal
            playerHand.addCard(deck.DrawCard());
            dealerHand.addCard(deck.DrawCard());
            playerHand.addCard(deck.DrawCard());
            dealerHand.addCard(deck.DrawCard());

            System.out.println("\nYour hand: " + playerHand + " (Value: " + playerHand.getValue(deck) + ")");
            System.out.println("Dealer's first card: " + dealerHand.getCards().get(0));
            System.out.println("Dealer's second card is facedown.");

            // Player actions: Double Down, Split, Hit, Stand
            playerActions();

            // Dealer's turn if player didn't bust or split and bust
            if (playerHand.getCards().size() > 0 && playerHand.getValue(deck) <= 21) {
                dealerTurn();
                determineWinner(currentBet);
            }

            System.out.println("\nYour current chip count: " + playerWallet);
            if (playerWallet <= 0) {
                System.out.println("You are out of chips! Game over.");
            } else {
                if (!playAgain()) {
                    break;
                }
            }
        }
        System.out.println("Final chip count: " + playerWallet);
    }

    private int getBet() {
        int playerBet = 0;
        String validBet = "no";
        while (!validBet.equals("yes")) {
            System.out.println("How much would you like to bet? (min = 5, max = 100, or 0 to quit)");
            try {
                playerBet = scanner.nextInt();
                if (playerBet == 0) {
                    return 0;
                } else if (playerBet >= 5 && playerBet <= 100 && playerBet <= playerWallet) {
                    validBet = "yes";
                } else if (playerBet > playerWallet) {
                    System.out.println("Sorry, that bet amount exceeds the amount of chips you have.");
                } else {
                    System.out.println("Sorry, that bet is invalid.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
        return playerBet;
    }
    
    private String[] getPlayerOptions(){
        boolean bothCardsMatch = (playerHand.getCards().get(0).split("Of")[0].equals(playerHand.getCards().get(1).split("Of")[0]));
        boolean canDouble = (playerWallet >= currentBet && playerHand.getCards().size() == 2);
        boolean canSplit = (playerWallet >= currentBet && playerHand.getCards().size() == 2 && bothCardsMatch && splitCount == 0);
        boolean canSwap = (splitCount > 0);
        String outputs[] = {"Stand","Hit",null,null,null};
        if(canDouble){ outputs[2] = "Double";}
        if(canSplit){ outputs[3] = "Split";}
        if(canSwap){ outputs[4] = "Swap";}
        return outputs;
    }
    
    private String takePlayerAction(String action){
        //System.out.print("Hit (h), Stand (s)");

        if (action.equals("Hit")) {
            playerHand.addCard(deck.DrawCard());
            System.out.println("You hit. Your new hand: " + playerHand + " (Value: " + playerHand.getValue(deck) + ")");
            if (playerHand.getValue(deck) > 21) {return "HitFailed";}
            return "HitSuccess";
        } 
        else if (action.equals("Stand")) {
            System.out.println("You stand.");
            return "Stand";
        } 
        else if (action.equals("Double")) {
                playerWallet -= currentBet;
                currentBet *= 2;
                playerHand.addCard(deck.DrawCard());
                System.out.println("You double down. Your final hand: " + playerHand + " (Value: " + playerHand.getValue(deck) + ")");
                if (playerHand.getValue(deck) > 21) {
                    System.out.println("You busted!");
                    return "DoubleFailed";
                }
                return "DoubleSuccess";
        } 
        else if (action.equals("Split")) {
            splitHand();
            return "Split"; // After splitting, we handle the hands separately (simplified here)
        } 
        else if (action.equals("Swap")) {
            //splitHand();
        	Hand tmpHand = new Hand();
        	tmpHand = playerHand;
        	playerHand = playerReserveHand;
        	playerReserveHand = tmpHand;
            return "Swap"; // After splitting, we handle the hands separately (simplified here)
        } 
    return "InvalidAction";
    }

    private void playerActions() {}

    private void splitHand() {
        if (playerHand.getCards().size() == 2 &&
            playerHand.getCards().get(0).split("Of")[0].equals(playerHand.getCards().get(1).split("Of")[0]) &&
            playerWallet >= currentBet) {
            playerWallet -= currentBet;
            Hand hand1 = new Hand();
            Hand hand2 = new Hand();
            hand1.addCard(playerHand.getCards().remove(0));
            hand1.addCard(deck.DrawCard());
            hand2.addCard(playerHand.getCards().remove(0));
            hand2.addCard(deck.DrawCard());
            playerReserveHand = hand2;
            playerHand = hand1;
            splitCount += 1;
            System.out.println("  GAME: Splitting hands...");
        } else {
            System.out.println("Cannot split hand.");
        }
    }

    private String dealerTurn() {
        System.out.println("\nDealer's turn. Dealer's full hand: " + dealerHand + " (Value: " + dealerHand.getValue(deck) + ")");
        if (dealerHand.getValue(deck) < 17) {
            System.out.println("Dealer hits.");
            dealerHand.addCard(deck.DrawCard());
            System.out.println("Dealer's new hand: " + dealerHand + " (Value: " + dealerHand.getValue(deck) + ")");
            if (dealerHand.getValue(deck) > 21) {
                System.out.println("Dealer busted!");
                return "HitFailed";
            }
            else{
            	return "HitSuccess";
            }
        }
        System.out.println("Dealer stands.");
        return("Stand");
    }

    private String determineWinner(int bet) {
        int playerValue = playerHand.getValue(deck);
        int dealerValue = dealerHand.getValue(deck);
        if (playerValue > 21) {
            System.out.println("Dealer wins!");
            playerWallet -= bet;
            return "dealerWin";
        } else if (dealerValue > 21) {
            System.out.println("You win!");
            playerWallet += bet;
            return "playerWin";
        } else if (playerValue > dealerValue) {
            System.out.println("You win!");
            playerWallet += bet;
            return "playerWin";
        } else if (dealerValue > playerValue) {
            System.out.println("Dealer wins!");
            playerWallet -= bet;
            return "dealerWin";
        } else {
            System.out.println("It's a push!");
            return "push";
        }
    }

    private void determineSplitWinner(Hand playerHand, Hand dealerHand, int bet) {
        int playerValue = playerHand.getValue(deck);
        int dealerValue = dealerHand.getValue(deck);

        System.out.println("\nComparing hand: " + playerHand + " (Value: " + playerValue + ") with Dealer: " + dealerHand + " (Value: " + dealerValue + ")");

        if (playerValue > 21) {
            System.out.println("Hand busted!");
            playerWallet -= bet;
        } else if (dealerValue > 21) {
            System.out.println("Hand wins!");
            playerWallet += bet;
        } else if (playerValue > dealerValue) {
            System.out.println("Hand wins!");
            playerWallet += bet;
        } else if (dealerValue > playerValue) {
            System.out.println("Dealer wins against hand!");
            playerWallet -= bet;
        } else {
            System.out.println("Hand is a push!");
        }
    }

    private boolean playAgain() {
        System.out.println("\nDo you want to play another round? (yes/no)");
        String playAgain = scanner.next().toLowerCase();
        return playAgain.equals("yes");
    }
}



 