<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Instructions</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
    <div class="container">
        <h1>Game Instructions</h1>
        <div class="instructions-content">
            <%
                String game = request.getParameter("game");
                if ("blackjack".equals(game)) {
            %>
                <h2>How to Play Blackjack</h2>
                <p>Blackjack is a card game where players try to get a hand value as close to 21 as possible without exceeding it. Each player is dealt two cards and can choose to "hit" (get another card) or "stand" (keep their current hand). Face cards are worth 10 points, aces can be worth 1 or 11 points, and other cards are worth their face value.</p>
            <%
                } else if ("baccarat".equals(game)) {
            %>
                <h2>How to Play Baccarat</h2>
                <p>Baccarat is a card game where players bet on either the "player" or "banker" hand. The goal is to have a hand value closest to 9. Each hand is dealt two cards, and additional cards may be drawn based on specific rules. Face cards and tens are worth 0 points, aces are worth 1 point, and other cards are worth their face value.</p>
            <%
                }
            %>
        </div>
        <a href="index.jsp" class="back-link">Back to Games</a>
    </div>
</body>
</html>