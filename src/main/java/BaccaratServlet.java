import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet({"/BaccaratServlet"})
public class BaccaratServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
   private CardUtils cardUtils = new CardUtils("/CardGame_1");
   private Baccarat2 gameInstance = new Baccarat2();
   public boolean doInit = true;
   public int inputCheck = 0;
   public String gameState;
   
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     System.out.println("");
     //String ctxtPath = request.getContextPath();
	 PrintWriter out = response.getWriter(); // basics
     if(doInit) { // just started
	   doInit = false;
	    System.out.println("starting");
	    request.setAttribute("check", inputCheck);	
		}
	  String gameInputs[] = {"refresh","none"}; // default behaviour: read interaction as a refresh.
	  int oldInputCheck = inputCheck - 1; // default to reading as a refresh
	  try {
		  oldInputCheck = Integer.parseInt(request.getParameter("check"));// if it was a button click, oldInputCheck = inputCheck
			System.out.println(" DISPLAY: Old Input Check passed. OIC = " + oldInputCheck + " IC = " + inputCheck);
		  if(oldInputCheck == inputCheck) { // BUTTON
				String choice = request.getParameter("choice"); // if this is null but oic == ic, we are in trouble...
				System.out.println(" DISPLAY: Clicked!");
				gameInputs[0] = "click";
				gameInputs[1] = choice;
			  }
		  else {
			  gameInputs[0] = "refresh";
			  gameInputs[1] = "none";}
	  } 
	  catch(NumberFormatException e){
		System.out.println(" DISPLAY: Old Input Check was null. If no buttons have ever been pressed, this is normal. Otherwise, big problem.");
		System.out.println(" DISPLAY: Exception was (" + e + ")");
	  }
	  inputCheck = inputCheck + 1;
	  
	  

		String auxInputs[] = new String[16];
		String betAmount = request.getParameter("betAmount");
		if(betAmount==null){System.out.println(" DISPLAY: no betAmount");}
		else{
			auxInputs[0] = betAmount;
			System.out.println(" DISPLAY: yes betAmount (" + betAmount + ")");
			auxInputs[1] = request.getParameter("betType");
		}
	  
	  // 	GAME LOGIC
	  

	  //gameInstance.testDraw();
	  ArrayList<ArrayList<String>> gameOutputs = gameInstance.runGameLogic(gameInputs[0], gameInputs[1], auxInputs);
	  System.out.println(gameOutputs);
	  String gameState = gameOutputs.get(0).get(0);
	  ArrayList<String> playerHand = gameOutputs.get(2);
	  ArrayList<String> dealerHand = gameOutputs.get(3);
      
		
		
      String header = "<head>\r\n    <meta charset=\"ISO-8859-1\">\r\n    <title>It is a Baccarat!</title>\r\n    <link href='styles.css' rel=\"stylesheet\" type=\"text/css\" media=\"all\">\r\n    <!-- js scripts go here if I need any  -->\r\n</head>";
      out.println(header);
      out.println("<body background = '/CardGame_1/img/ui/noisyBg.png'>");
		out.println("<a href=\"index.jsp\" class=\"back-link\">Back to Games</a>");
      

      // set up "game board"
      
		String gameboard = "";
		String bigOlText = "";
		//String lilText = "";
		int gameWindowHeight = 650;
		//gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
		
		switch (gameState) {
		  case "reset":
			//out.println("RESET STATE <br>\r\n");	
			bigOlText = "Welcome to Baccarat!";
			break;
		  case "setup":
			bigOlText = "Place your bets!";
			gameboard = gameboard + drawSpinbox(new int[] {5,5}, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "rules":
			bigOlText = "How do you play?";
			gameboard = gameboard + "<p> I'll add pictures and whatnot later for now just a copy paste.... </p> <br> "
					+ "<p> The objective of Baccarat is to bet on which hand -the Player or the Banker- will have a total closest to 9.\r\n Players can also bet on a Tie, where both hands have the same total.</p>"
					+ "<p>Once bets are placed, the dealer distributes two cards to both the Player and Banker. Each card has a specific value: Aces are worth 1 point, 2 through 9 are worth their face value, and 10, J, Q, and K are worth 0 points. The total value of a hand is determined by adding the two card values together and using only the rightmost digit. <br> For example, if a hand contains an 8 and a 7, the total is 15, but only the rightmost digit (5) is counted.</p>"
					+ "<p>If either the Player or Banker has a total of 8 or 9 in the first two cards, it is called a “Natural Win”, and no more cards are drawn. The hand with the highest total wins immediately.</p>"
					+ "<p>If there is no Natural Win, the game may require a third card to be drawn. The Player’s third card rule states that if the Player’s total is 0 to 5, they must draw a third card. If the Player’s total is 6 or 7, they stand and do not draw another card.</p>"
					+ "<p>The Banker’s third card rule is more complex and depends on whether the Player has drawn a third card. If the Player stands with two cards, the Banker follows the same rule (drawing a third card if they have 0-5 and standing if they have 6-7). However, if the Player draws a third card, the Banker’s decision depends on the Player’s third card. For example, the Banker will always draw if their total is 0, 1, or 2, but if the Banker has a total of 3, they will draw unless the Player’s third card is an 8. The Banker’s drawing rules continue to vary based on the Player’s third card, following a fixed set of conditions.</p>"
					+ "<p>Once all cards are drawn, the hand with the highest total (closest to 9) wins. Winning bets on the Player are paid 1:1. Winning bets on the Banker are also paid 1:1, but a 5% commission is usually deducted. A winning Tie bet typically pays 8:1 or 9:1, depending on casino rules. If the game ends in a Tie, bets on the Player and Banker are refunded. </p>";
			gameWindowHeight = 1200;
			break;
		  case "playerTurn":
			bigOlText = "The first cards are revealed!";
			//ArrayList<String> secondOuts = gameOutputs.get(1);
			//secondOuts.set(1,"???");
			//gameOutputs.set(1, secondOuts);
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "playerDraw":
			bigOlText = "The player draws a third card.";
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "playerStand":
			bigOlText = "The player stands.";
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "bankerDraw":
			bigOlText = "The banker draws a card.";
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "bankerStand":
			bigOlText = "The banker stands.";
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "resolve":
			bigOlText = gameOutputs.get(0).get(3);
			gameboard = gameboard + cardUtils.generateTxt(gameOutputs.get(0).get(4), new int[] {5,75}, "mono");
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  case "invalid":
			bigOlText = "oops";
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(2));
			break;
		  default:
			bigOlText = ("<p> ERROR INVALID GAMESTATE " + gameState + "! </p>/r/n <br>");
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			break;
		}
	  out.println("<div background = '/CardGame_1/img/ui/noisyBg.png' class='gameContainer' style='height:"+gameWindowHeight+"px'>\r\n");
	  //ArrayList<String> testBttn1 = new ArrayList<String>();
	  //testBttn1.add("");
      //gameboard = gameboard + drawButtons(testBttn1,testBttn1,new int[] {-40,-50},new int[] {0,0});
      gameboard = gameboard + drawButtons(gameOutputs.get(4),gameOutputs.get(4),new int[] {5,5},new int[] {100,0});
      out.println("<br><br> <div class='bigOlText'> "+bigOlText+" </div>");
      out.println(gameboard);
      out.println("</div>\r\n<br>");
      out.println("</body>");
      out.close();
   }

	
	private String drawAHand(ArrayList<String> hand, int[] pos, int[] ofset) {
		String sto = "";
		for(int i = 0; i < hand.size(); i++) {
			sto = sto +
				cardUtils.generateImgFromCard(hand.get(i), 
						new int[] {pos[0] + (ofset[0] * i) , pos[1] + (ofset[1] * i)});	// link back with choice & IC update
		}
		return sto;
	}
	
	private String drawButtons(ArrayList<String> buttonNames, ArrayList<String> buttonLinks, int[] pos, int[] ofset) {
		String sto = "";
		for(int i = 0; i < buttonNames.size(); i++) {
			sto = sto +
				cardUtils.generateButton(buttonNames.get(i), 
						new int[] {pos[0] + (ofset[0] * i) , pos[1] + (ofset[1] * i)}, "button","BaccaratServlet?choice="+buttonLinks.get(i)+"&check="+inputCheck);	// link back with choice & IC update
		}
		return sto;
	}
	
	private String gimmeCardsHandsw(ArrayList<String> playerHand, ArrayList<String> dealerHand, ArrayList<ArrayList<String>> gameOutputs) {
		String sto = "";
		sto = sto + drawAHand(playerHand, new int[] {40,180}, new int[] {110,0});
		sto = sto + cardUtils.generateTxt("Your hand (" + gameOutputs.get(1).get(0)+")", new int[] {50,300}, "handSubtitle");
		sto = sto + drawAHand(dealerHand, new int[] {40,400}, new int[] {110,0});
		sto = sto + cardUtils.generateTxt("Banker's hand (" + gameOutputs.get(1).get(1)+")", new int[] {50,520}, "handSubtitle");
		//sto = sto + drawButtons(gameOutputs.get(1),new int[] {20,75}, new int[] {90,0});
	return sto;
	}
	
	private String drawSpinbox(int[] position, ArrayList<ArrayList<String>> gameOutputs) {
		String sto = "";
		sto = sto + ("<form method='GET' style='position: absolute; left:"+position[0]+"px; top:"+position[1]+"px'>\r\n"
				+ "<input type=\"hidden\" name=\"check\" value=\""+inputCheck+"\" />"
				+ "<input type=\"hidden\" name=\"choice\" value=\"submit\" />"
				+ "<input type=\"number\" name=\"betAmount\" min=\""+5+"\"  max=\""+gameOutputs.get(0).get(1)+"\" value=\""+gameOutputs.get(0).get(2)+"\" font-size=\"30px\" required >\r\n"
				+ "<select name=\"betType\" id=\"betType\">\r\n"
				+ "  <option value=\"player\">I bet the PLAYER will win!</option>\r\n"
				+ "  <option value=\"banker\">I bet the BANKER will win!</option>\r\n"
				+ "  <option value=\"tie\">I bet there will be A TIE!</option>\r\n"
				+ "</select>"
				+ "<input type=\"submit\" value=\"Start Game\">\r\n"
				+ "</form>");
		return sto;
	}
	
	private String drawChipCounter(String totalChips, String betAmount) {
		return drawChipCounter(totalChips, betAmount, new int[] {420, 150});
	}
	private String drawChipCounter(String totalChips, String betAmount, int[] pos) {
		String sto = "";
		sto = sto + cardUtils.generateImg("/img/ui/bigChipWithX.png", new int[] {pos[0], pos[1]}, "card");
		sto = sto + cardUtils.generateTxt("" + betAmount + "/" + totalChips, new int[] {pos[0] - 20, pos[1] + 65}, "handSubtitle");
		return sto;
	}
	
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.doGet(request, response);
   }
}

