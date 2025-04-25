import java.util.ArrayList;


public class CardUtils {
	
	public String context = "PROJECT_NAME_GOES_HERE";
	
	public final String[] VALID_NUMBERS = {"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King"};
	public final String[] VALID_SUITS = {"Spades", "Diamonds", "Clubs", "Hearts"};
	public final String[] UNIQUE_CARDS = {"Blank","Hidden","Joker"};
	
	public CardUtils() {
		System.out.println("CardUtils loaded! Today's card is the "+context+"/img/"+ randomCard()+".png");
	} // empty constructor just for completeness's sake
	public CardUtils(String newContext) {
		context = newContext;
		System.out.println("CardUtils loaded! Today's card is the "+context+"/img/"+ randomCard()+".png");
	}
	

	/**
	 * Returns a full deck of all combinations of suits and values. Cards are represented as Strings, such as "AceOfSpades" or "TenOfHearts"
	 */
	public ArrayList<String> generateDeck() {
		ArrayList<String> sto = new ArrayList<String>();
		for(int i=0; i < VALID_SUITS.length; i++) {
			for(int j = 0; j < VALID_NUMBERS.length; j++) {
				sto.add(VALID_SUITS[i]+"Of"+VALID_NUMBERS[j]);
			}
		}
		return sto;
	}
	/**
	 * Returns a full deck of all combinations of suits and values. Cards are represented as Strings, such as "AceOfSpades" or "TenOfHearts"
	 * @param count set this to add more than one of each card to the deck.
	 */
	public ArrayList<String> generateDeck(int count) {
		ArrayList<String> sto = new ArrayList<String>();
		for(int i=0; i < VALID_SUITS.length; i++) {
			for(int j = 0; j < VALID_NUMBERS.length; j++) {
				for(int k = 0; k < count; k++) { // I've been working with python and lua so long I forgot how much I like function overloading.
					sto.add(VALID_SUITS[i]+"Of"+VALID_NUMBERS[j]);
				}
			}
		}
		return sto;
	}

	private int rand(int maxRand) {
		return(int)((Math.random()*maxRand));
	}
	
	
	/**
	 * Returns a shuffled copy of the arrayList. Hopefully. It's late and I don't really want to test it so just trust.
	 */
	public ArrayList<String> shuffle(ArrayList<String> deck){
		ArrayList<String> newDeck = new ArrayList<String>();
		for(int i = 0; i < deck.size(); i++) {
		}
		return newDeck;
	}
	
	
	
	/**
	 * Takes a card string and returns 3 values corresponding to its value, 
	 *   its suit, and whether it is face up or face down. 
	 *   Keep in mind that values returned by this function may not match values
	 *   used for the game, but they will be consistent.
	 * @return element 0: value, or -1 for unique / invalid / blank | 
	 * element 1: suit, or -1 for unique / invalid | 
	 * element 2: face up (0) or face down (1). This will be -1 for invalid cards.
	 */
	public int[] readCardString(String cardString) {
		int sto[] = {-1,-1,-1}; //error by default
		for(int i = 0; i < UNIQUE_CARDS.length; i++) {
			if(UNIQUE_CARDS[i].equals(cardString)) {
				return new int[] {-1,-1,i};
			}
		} // not in uniques
		String[] cardStringSplit = cardString.split("Of"); // split into value and suit
		if(cardStringSplit.length != 2){return new int[] {-1,-1,-1};} // error if no Of
		String[] suitSplit = cardStringSplit[1].split("Hid"); // if there is a Hidden at the end, element 1 will be "den"
		// therefore, we can check whether a card is face down or not by checking the length of this array
		
		for(int i = 0; i < VALID_NUMBERS.length; i++) { // check card values
			if(cardStringSplit[0].equals(VALID_NUMBERS[i])) {
				sto[0] = i; // set value
				break;
			}
		}
		for(int i = 0; i < VALID_NUMBERS.length; i++) { // check card suits
			if(cardStringSplit[1].equals(VALID_NUMBERS[i])) {
				sto[1] = i; // set suit
				break;
			}
		}
		sto[2] = suitSplit.length-1; // 0 if face up, 1 if face down
		return sto;
	}
	/**
	 * Turns an array of ints back into a card string, after getting it from readCardString().
	 * @param vals array containing value, suit, face status.
	 * @return cardString
	 */
	public String composeCardString(int[] vals) {
		if(vals.length != 3) {return "Error";}
		String sto = "";
		if (vals[0] == -1 && vals[1] == -1) { //unique
			if(vals[2] < UNIQUE_CARDS.length && vals[2] >= 0)
				return UNIQUE_CARDS[vals[2]];
			return "InvalidUnique";
		}
		// standard
		if(vals[0] >= VALID_NUMBERS.length) return "InvalidValue";
		if(vals[1] >= VALID_SUITS.length) return "InvalidSuit";
		if(vals[2] == 1) sto = "Hidden";
		return VALID_NUMBERS[vals[0]] + "Of" + VALID_SUITS[vals[1]] + sto;
	}
	/**
	 * Generates a cardString from the given value & suit combination. Keep in mind that an Ace is value 0, a Two is value 1, etc.
	 * @param vals array containing value, suit, face status.
	 * @return cardString
	 */
	public String composeCardString(int val, int suit) {
		if(val >= VALID_NUMBERS.length) return "InvalidValue";
		if(suit >= VALID_SUITS.length) return "InvalidSuit";
		return VALID_NUMBERS[val] + "Of" + VALID_SUITS[suit];
	}
	/**
	 * Tests the readCardString and composeCardString functions. Ignore this.
	 * @hidden
	 */
	public String testCardString(String cardString) {
		int t[] = readCardString(cardString);
		return(cardString + " -> {" + t[0] + " " + t[1] + " " + t[2]+"} -> " + composeCardString(t));
		
	}
	/**
	 * Takes a card string and returns a version of its name that isn't all one word. eg, passing "QueenOfHearts" will return "Queen Of Hearts"
	 * 
	 */
	public String prettyTextFromCardString(String cardString) {
		
		for(int i =0; i < UNIQUE_CARDS.length; i++) {
			if(UNIQUE_CARDS[i].equals(cardString)) {
				return cardString;
			}
		} // not in uniques
		String[] cardStringSplit = cardString.split("Of"); // split into value and suit
		if(cardStringSplit.length != 2){return "Invalid Card";} // error if no Of
		return cardStringSplit[0] + " Of " + cardStringSplit[1];
		
	}
	/**
	 * Takes a cardString and returns the path to where the corresponding image should be. Whether this
	 * works or not depends on whether the context property was set correctly.
	 * 
	 */
	public String getCardPath(String cardString) {
		String sto = cardString;
		int t[] = readCardString(cardString);
		if(t[2] == 1) return context+"/img/cards/Hidden.png";
		//if(t[2] == -1) return context+"/img/Error.png";
		if(t[0] == -1 && t[1] >= 0) return context+"/img/cards/Blank.png";
		return context+"/img/cards/"+sto+".png";
	}
	
	/**
	 * @return Returns a random face-up cardString. Useful for testing purposes
	 */
    public String randomCard() {
    	String sto = "";
    	String prefixes[] = {"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King"};
    	String suffixes[] = {"Spades","Clubs","Hearts","Diamonds"};
    	sto = prefixes[rand(prefixes.length)] + "Of" + suffixes[rand(suffixes.length)];
    	return sto;
    }
    /** 
     * Generates html for an image of the given card at the given position.
     * @param cardString
     * @param cardPosition A "2D vector" corresponding to the card's upper left corner.
     * @return returns HTML markup corresponding to a decorative card at the given position
     * */
    public String generateImgFromCard(String cardString, int cardPosition[]) {
    	
    	String sto = "";
    	sto = "<img src = '"+getCardPath(cardString)+"' style = 'position:absolute; "
    			+ "left: " + cardPosition[0] + "px; "
    			+ "top: "  + cardPosition[1] + "px;' "
    			+ "title = 'The "+prettyTextFromCardString(cardString)+"' class = card> ";
    	return sto;
    }
    public String generateImgFromCard(String cardString, int cardPosition[], String style) {
    	
    	String sto = "";
    	sto = "<img src = '"+getCardPath(cardString)+"' style = 'position:absolute; "
    			+ "left: " + cardPosition[0] +"px; "
    			+ "top: "  + cardPosition[1] + "px;' "
    			+ "title = 'The "+prettyTextFromCardString(cardString)+"' class = "+style+"> ";
    	return sto;
    }
    public String generateImg(String image, int uiPosition[]) {
    	String sto = "<img src = '"+context+image+"' style = 'position:absolute; "
    			+ "left: " + uiPosition[0]+"px; "
    			+ "top: "  + uiPosition[1]+"px;'>";
    	return sto;
    }
    public String generateImg(String image, int uiPosition[], String style) {
    	String sto = "<img src = '"+context+image+"' style = 'position:absolute; "
    			+ "left: " + uiPosition[0]+"px; "
    			+ "top: "  + uiPosition[1]+"px;' "
    			+ "class = "+style+">";
    	return sto;
    }
    public String generateTxt(String text, int uiPosition[], String style) {
    	String sto = "<div style = 'position:absolute; "
    				+ "left: "+uiPosition[0]+"px; "
    				+ "top: "  +uiPosition[1]+"px;'"
    				+ "class = "+style+">"
    			+ text
    			+ "</div>";
    	return sto;
    }
    public String generateButton(String text, int uiPosition[], String style, String link) {
    	String sto = "<a href = "+link+"> <span style = 'position:absolute; "
    				+ "left: "+uiPosition[0]+"px; "
    				+ "top: "  +uiPosition[1]+"px;"
    				+"' "
    				+ "class = "+style+">"
    			+ "<b>" + text + "</b>"
    			+ "</span> </a>";
    	return sto;
    }
    public String generateButtonRelative(String text, String buttonClass, String link) {
    	String sto = "<a href = "+link+"> <span "
    				+ "class = "+buttonClass+">"
    			+ "<b>" + text + "</b>"
    			+ "</span> </a>";
    	return sto;
    }
    
    /**
     * 
    				+ "	background-color:#80bf5e;\r\n"
    				+ "	border:4px;\r\n"
    				+ "	border-color: #a7d781;\r\n"
    				+ "	border-style: outset;\r\n"
    				+ "	padding: 4px;\r\n"
    				+ "	font-family: \"Garamond\", \"Times New Roman\", serif;\r\n"
    				+ "	font-size: 20px;\r\n"
    				+ "	color:#336600;'"*/
 

    /**
     * Surrounds the given HTML markup with a link.
     * @param target The HTML you want to surround.
     * @param link href
     * @return modified HTML
     */
    public String surroundWithA(String target, String link) {
    	return "<a href = '"+link+"'>" + target + "</a>";
    	}

	
	
}