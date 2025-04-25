

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Servlet implementation class TestGameDisplay
 */
@WebServlet("/TestGameDisplay")
public class TestGameDisplay extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public TestGameDisplay() {super();}
    
    public TestGame gameInstance = new TestGame();
    private CardUtils cardUtils = new CardUtils("/CardGame_1"); // CHANGE THIS IF YOU SWITCH PROJECT NAME
    private String displayName = "TestGameDisplay";
    
    public boolean doInit = true;
    public int inputCheck = 0;
    // a way to detect refreshes. 
    // When a button is pressed, it is passed as the parameter "check", which is stored to B.
    // any time the screen refreshes, A increments.
    // If A and B are the same, it means the refresh was due to a button click.
    // technically this will fail if the user refreshes an absurd number of times but at that point you deserve the single frame of interaction.
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ctxtPath = request.getContextPath();
		PrintWriter out = response.getWriter(); // basics
		String header = "<head>\r\n"
				+ "    <meta charset=\"ISO-8859-1\">\r\n"
				+ "    <title>test game</title>\r\n"
				+ "    <link href=\'css_CardGame.css\' rel=\"stylesheet\" type=\"text/css\" media=\"all\">\r\n"
				+ "    <!-- js scripts go here if I need any  -->\r\n"
				+ "</head>";
		out.println(header);
		
		if(doInit) { // just started
			doInit = false;
			System.out.println("starting");
			request.setAttribute("check", inputCheck);
			
		}
		int oldInputCheck = inputCheck - 1; // default to reading as a refresh
		try {oldInputCheck = Integer.parseInt(request.getParameter("check"));} // if it was a button click, oldInputCheck = inputCheck
		catch(NumberFormatException e){
			System.out.println(" DISPLAY: Old Input Check was null. If no buttons have ever been pressed, this is normal. Otherwise, big problem.");
			System.out.println(" DISPLAY: Exception was (" + e + ")");
		}
		
		String gameInputs[] = {"refresh","none"}; // default behaviour: read interaction as a refresh.
		
		if(oldInputCheck == inputCheck) { // BUTTON
			String choice = request.getParameter("choice"); // if this is null but oic == ic, we are in trouble...
			gameInputs[0] = "clickCard";
			gameInputs[1] = choice;
			
		}
		inputCheck = inputCheck + 1;
		
		// GAME LOGIC TIME

		/** INPUTS:
		 *  0: last action the player took ("clickCard" / "refresh")
		 *  1: last button / card the player clicked (in this case, "card0", "card1" etc)
		 */
		ArrayList<ArrayList<String>> gameOutputs = gameInstance.runGameLogic(gameInputs[0], gameInputs[1]);
		ArrayList<String> playerHand = gameOutputs.get(0);
		ArrayList<String> gameStatus = gameOutputs.get(1);
		int deckSize = Integer.parseInt(gameStatus.get(0));
		int discardSize = Integer.parseInt(gameStatus.get(1));
		
		//out.println("<h1> Test Game </h1><br>"+ "<p>Deck contains " + deckSize + "</p>\r\n"+ "<p>Discard stact contains " + discardSize + "</p>\r\n"+ "<br>");
		

		out.println("<p> TEST GAME --- Click a card to replace it with a new one. Click the discard pile to shuffle it back into the deck. </p>");
		out.println("<div class = gameContainer width = '400px'>");
		
		String gameboard = "";
		
		int playerHandPosition[] = {500,360};
		int cardMargin[] = {120,180};
		int decksPosition[] = {400,90};
		int decksOfset[] = {200,0};
		int playerHandOfset[] = {cardMargin[0] * playerHand.size()/2,0};
		
		for (int i = 0; i < playerHand.size(); i++) { // draw hand
			// add card
			gameboard = gameboard + 
					cardUtils.surroundWithA( //make it a link
							cardUtils.generateImgFromCard(
									playerHand.get(i), 
									new int[] {playerHandPosition[0] + (cardMargin[0]*i)+4 - playerHandOfset[0],playerHandPosition[1]+4-playerHandOfset[1]}),
							(displayName+("?choice=card"+i)+"&check="+inputCheck));	// link back with choice & IC update
		}
		// draw deck
		String deckDraw = "";
		for(int i = 0; i < Math.min(deckSize, 5); i++) {
			deckDraw = deckDraw + cardUtils.generateImg("/img/cards/Hidden.png",
					new int[] {decksPosition[0]-decksOfset[0] + (i*2),decksPosition[1]+decksOfset[1] - (i*6)},
					"card");
		}
		gameboard = gameboard + "<a href = '"+displayName+"?choice=none&check="+oldInputCheck+"' title = 'Click to shuffle discards back into the deck.'>"
				+deckDraw + "</a>";
		gameboard = gameboard + cardUtils.generateTxt("x"+deckSize,
				new int[] {decksPosition[0]-decksOfset[0],decksPosition[1]+cardMargin[1]-40}, 
				"handSubtitle");
		// draw discard
		String discDraw = "";
		for(int i = 0; i < Math.min(discardSize, 5); i++) {
			String discCardImg = "Hidden";
			if(i+1 == Math.min(discardSize, 5)) {discCardImg = gameStatus.get(2);}
			discDraw = discDraw + cardUtils.generateImg("/img/cards/"+discCardImg+".png",
					new int[] {decksPosition[0]+decksOfset[0] + (i*2),decksPosition[1]+decksOfset[1] - (i*6)},
					"card");
		}
		gameboard = gameboard + "<a href = '"+displayName+"?choice=none&check="+oldInputCheck+"' title = 'Click to shuffle discards back into the deck.'>"
				+discDraw + "</a>";
		gameboard = gameboard + cardUtils.generateTxt("x"+discardSize,
				new int[] {decksPosition[0]+decksOfset[0],decksPosition[1]+cardMargin[1]-40}, 
				"handSubtitle");
		
		out.println(gameboard);
		out.println("</div>");
		
		
		out.close();
		System.out.println(" DISPLAY: Printing complete. IC="+inputCheck+" & OIC="+oldInputCheck+"\r\n");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {doGet(request, response);} // stub

	


}
