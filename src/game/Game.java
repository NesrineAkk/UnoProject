package game;

import java.util.LinkedList;
import java.util.Random;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean reverse;
    private boolean skipNextPlayer;
    private boolean hasCalledUno;
    private boolean unoPenaltyDue;
    private int lastPlayerWithOneCard;
    
    // New variables for card stacking
    private int stackedDrawCards;
    private String currentStackType; // "+2" or "Wild+4"
 
    public Game(List<Player> players) {
        deck = new Deck();
        this.players = players;
        currentPlayerIndex = 0;
        reverse = false;
        skipNextPlayer = false;
        hasCalledUno = false;
        unoPenaltyDue = false;
        lastPlayerWithOneCard = -1;
        
        // Initialize stacking variables
        stackedDrawCards = 0;
        currentStackType = "";
    }
 
    public void initializeGame() {
        deck.initializeDeck();  // 1️⃣ Initialiser le deck
        deck.shuffleDeck();     // 2️⃣ Mélanger le deck

        System.out.println("LE JEU COMMENCE ! 🎮 \n");

        // 3️⃣ Distribuer les cartes aux joueurs
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addCard(deck.drawCard());
            }
            System.out.println(player.getName() + " A REÇU SES CARTES ! 🃏 \n");
        }

        // 4️⃣ Ajouter une carte correcte à la défausse
        Card firstCard = deck.drawCard();

        // Vérifier que la première carte n'est pas une Wild+4 ou Skip
        while (firstCard.getValue() == Card.Value.WildFour || firstCard.getValue() == Card.Value.Skip) {
            firstCard = deck.drawCard();
        }

        deck.addToDiscardPile(firstCard); // 5️⃣ Ajout final de la carte
    }

    
    public Deck getDeck() {
        return deck;
    }
 
    public void startGame() {
        Card initialCard;
        
        // The first card cannot be a Wild+4 or an action card according to official UNO rules
        do {
            initialCard = deck.drawCard();
            
            if (initialCard.getType().equals("Wild+4") || 
                initialCard.getType().equals("Skip") || 
                initialCard.getType().equals("Reverse") || 
                initialCard.getType().equals("+2")) {
                deck.addToDiscardPile(initialCard);
                deck.reshuffleDeck();
            }
        } while (initialCard.getType().equals("Wild+4") || 
                initialCard.getType().equals("Skip") || 
                initialCard.getType().equals("Reverse") || 
                initialCard.getType().equals("+2"));
        
        // If it's a wild card, set a random color
        if (initialCard.getType().equals("Wild")) {
            initialCard.setColor(chooseRandomColor());
        }
        
        deck.addToDiscardPile(initialCard);
        System.out.println("LA PARTIE COMMENCE 🎉 \n");
        System.out.println("CARTE INITIALE: \n" + initialCard.toString());
        
        while(true) {
            playTurn();
            if(checkWinner()) {
                break;
            }
        }
    }
    
    // Modified method: Player only draws one card, then their turn ends if they can't play it
    private void CaseNoValidCards(Player current) {
        // If there's a stack of draw cards, the player must draw all of them
        if (stackedDrawCards > 0) {
            System.out.println(current.getName() + " DOIT PIOCHER " + stackedDrawCards + " CARTES! 🃏🔄 \n");
            for (int i = 0; i < stackedDrawCards; i++) {
                current.addCard(deck.drawCard());
            }
            // Reset the stack
            stackedDrawCards = 0;
            currentStackType = "";
            return;
        }
        
        Card drawnCard = deck.drawCard();
        current.addCard(drawnCard);
        System.out.println(current.getName() + " N'A AUCUNE CARTE VALIDE, IL PIOCHE UNE CARTE. 🃏🔄 \n");
        System.out.println(current.getName() + " A PIOCHE LA CARTE SUIVANTE :\n" + drawnCard.toString());

        List<Card> validCards = current.getValidCards(deck.getTopCard());
        boolean canPlay = false;

        // Check if the drawn card can be played
        for (Card card : validCards) {
            if (card.equals(drawnCard)) {
                canPlay = true;
                break;
            }
        }

        if (canPlay) {
            System.out.println(current.getName() + " JOUE LA CARTE PIOCHÉE:\n" + drawnCard.toString());
            current.playCard(drawnCard);
            enforceRules(drawnCard);
            deck.addToDiscardPile(drawnCard);
            
            // Check if player should say UNO
            if (current.getHand().size() == 1) {
                checkAndCallUno(current);
            }
            
        } else {
            System.out.println(current.getName() + " NE PEUT PAS JOUER LA CARTE PIOCHEE, PASSE SON TOUR\n");
        }

        if (deck.isEmpty()) {
            deck.reshuffleDeck();
            System.out.println("LE DECK EST VIDE, ON REBAT LES CARTES DE LA PIOCHE \n");
        }
    }
 
    public void playTurn() {
        Player current = players.get(currentPlayerIndex);
        System.out.println("----------------");
        System.out.println("CARTE DU DESSUS :");
        System.out.println(deck.getTopCard().toString());

        // ✅ Afficher la couleur choisie pour les cartes Wild
        Card topCard = deck.getTopCard();
        if ((topCard.getValue() == Card.Value.Wild || topCard.getValue() == Card.Value.WildFour) && 
            topCard.getColor() != Card.Color.Black) {
            System.out.println("COULEUR CHOISIE: " + topCard.getColor() + " 🎨");
        }

        // ✅ Afficher les cartes accumulées si un effet "+2" ou "Wild+4" est actif
        if (stackedDrawCards > 0) {
            System.out.println("CARTES À PIOCHER ACCUMULÉES: " + stackedDrawCards + " 🃏");
        }

        System.out.println("----------------");

        // ✅ Vérifier et appliquer les pénalités UNO si nécessaire
        checkUnoPenalties();

        // ✅ Vérifier si le joueur doit être **sauté** à cause d'une carte "Skip"
        if (skipNextPlayer) {
            System.out.println(current.getName() + " EST SAUTÉ À CAUSE D'UNE CARTE SKIP ! ⏭️");
            skipNextPlayer = false;
            currentPlayerIndex = getNextPlayerIndex();
            return;
        }

        // ✅ Afficher l'état du jeu si c'est un joueur humain
        if (current.isHuman()) {
            displayGameState();
        }

        // ✅ Réinitialiser le statut UNO pour le nouveau tour
        hasCalledUno = false;

        // ✅ Vérifier si le joueur peut contrer un effet "+2" ou "Wild+4"
        boolean canDefendAgainstStack = stackedDrawCards > 0 && checkDefenseAgainstStack(current);

        // ✅ Si une accumulation est active et que le joueur ne peut pas contrer → Il doit piocher
        if (stackedDrawCards > 0 && !canDefendAgainstStack) {
            System.out.println(current.getName() + " DOIT PIOCHER " + stackedDrawCards + " CARTES! 🃏🔄");
            for (int i = 0; i < stackedDrawCards; i++) {
                current.addCard(deck.drawCard());
            }
            // Réinitialisation de l'effet
            stackedDrawCards = 0;
            currentStackType = "";
            currentPlayerIndex = getNextPlayerIndex();
            return;
        }

        // ✅ Gestion du tour du **joueur humain**
        if (current instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) current;
            Card playedCard = humanPlayer.chooseCardPlay(deck.getTopCard());

            if (playedCard != null) {
                System.out.println(current.getName() + " JOUE LA CARTE\n" + playedCard.toString());
                current.playCard(playedCard);
                enforceRules(playedCard);
                deck.addToDiscardPile(playedCard);

                // ✅ Vérifier si le joueur doit crier UNO
                if (current.getHand().size() == 1) {
                    checkAndCallUno(current);
                    lastPlayerWithOneCard = currentPlayerIndex;
                }
            } else {
                CaseNoValidCards(current);
                if (current.getHand().size() == 1) {
                    lastPlayerWithOneCard = currentPlayerIndex;
                }
            }
        }

        // ✅ Gestion du tour du **joueur IA**
        else if (current instanceof AIPlayer) {
            System.out.println(current.getName() + " EST UN JOUEUR AI 🤖 \n");
            AIPlayer aiPlayer = (AIPlayer) current;
            Card playedCard = aiPlayer.ChooseCardToPlay(deck.getTopCard());

            if (playedCard != null) {
                System.out.println(current.getName() + " JOUE LA CARTE\n" + playedCard.toString());
                current.playCard(playedCard);
                enforceRules(playedCard);
                deck.addToDiscardPile(playedCard);

                // ✅ Vérifier si l'IA doit crier UNO (80% de chance)
                if (current.getHand().size() == 1) {
                    if (new Random().nextInt(100) < 80) {
                        System.out.println(current.getName() + " CRIE UNO! 🎮");
                        hasCalledUno = true;
                    } else {
                        System.out.println(current.getName() + " A OUBLIÉ DE CRIER UNO! 🤔");
                        unoPenaltyDue = true;
                    }
                    lastPlayerWithOneCard = currentPlayerIndex;
                }
            } else {
                CaseNoValidCards(current);
                if (current.getHand().size() == 1) {
                    lastPlayerWithOneCard = currentPlayerIndex;
                }
            }
        }

        // ✅ Passer au joueur suivant après le tour
        currentPlayerIndex = getNextPlayerIndex();
    }

    
    // Method to check if a player can defend against a stack
    private boolean checkDefenseAgainstStack(Player player) {
        boolean canDefend = false;
        List<Card> hand = player.getHand();
        
        for (Card card : hand) {
            // If stack is +2, can only defend with another +2
            if (currentStackType.equals("+2") && card.getType().equals("+2")) {
                canDefend = true;
                break;
            }
            // If stack is Wild+4, can only defend with another Wild+4
            else if (currentStackType.equals("Wild+4") && card.getType().equals("Wild+4")) {
                canDefend = true;
                break;
            }
        }
        
        return canDefend;
    }
    
    // Fixed calculation of next player index
    public int getNextPlayerIndex() {
        int nextIndex;
        if (reverse) {
            nextIndex = currentPlayerIndex - 1;
            if (nextIndex < 0) {
                nextIndex = players.size() - 1;
            }
        } else {
            nextIndex = currentPlayerIndex + 1;
            if (nextIndex >= players.size()) {
                nextIndex = 0;
            }
        }
        return nextIndex;
    }
    
    public boolean playCard(String cardCode) {
        Player currentPlayer = players.get(currentPlayerIndex);
        Card cardToPlay = null;
        
        // For wild cards where color has been chosen (e.g., "rw" for red wild)
        if (cardCode.length() > 1 && (cardCode.endsWith("w") || cardCode.endsWith("w+"))) {
            String colorChar = cardCode.substring(0, 1);
            String cardType = cardCode.substring(1);
            
            // Find the matching wild card in hand
            for (Card card : currentPlayer.getHand()) {
                if ((card.getValue() == Card.Value.Wild && cardType.equals("w")) || 
                    (card.getValue() == Card.Value.WildFour && cardType.equals("w+"))) {
                    cardToPlay = card;
                    
                    // Set the chosen color
                    Card.Color chosenColor = null;
                    switch (colorChar) {
                        case "r": chosenColor = Card.Color.Red; break;
                        case "g": chosenColor = Card.Color.Green; break;
                        case "b": chosenColor = Card.Color.Blue; break;
                        case "y": chosenColor = Card.Color.Yellow; break;
                    }
                    
                    if (chosenColor != null) {
                        card.setColor(chosenColor);
                    }
                    
                    break;
                }
            }
        } else {
            // For normal cards
            for (Card card : currentPlayer.getHand()) {
                if (card.getImageName().equals(cardCode)) {
                    cardToPlay = card;
                    break;
                }
            }
        }

        if (cardToPlay == null) {
            System.out.println("⚠️ Erreur : Le joueur ne possède pas cette carte !");
            return false;
        }

        // Check if the card is valid to play
        Card topCard = deck.getTopCard();
        boolean validPlay = false;
        
        // Wild cards can always be played
        if (cardToPlay.getColor() == Card.Color.Black) {
            validPlay = true;
        } 
        // Check if matches color or value
        else if (cardToPlay.getColor() == topCard.getColor() || 
                 cardToPlay.getValue() == topCard.getValue() ||
                 (topCard.getColor() != Card.Color.Black && topCard.getValue() == Card.Value.Wild) ||
                 (topCard.getColor() != Card.Color.Black && topCard.getValue() == Card.Value.WildFour)) {
            validPlay = true;
        }
        
        if (!validPlay) {
            System.out.println("🚫 Carte invalide !");
            return false;
        }

        // Card is valid, play it
        currentPlayer.removeCard(cardToPlay);
        
        // Apply card effects
        enforceRules(cardToPlay);
        
        // Next turn
        nextTurn();
        
        return true;
    }
    
    public void nextTurn() {
        if (skipNextPlayer) {
            // Skip the next player (move two positions)
            if (reverse) {
                currentPlayerIndex = (currentPlayerIndex - 2 + players.size()) % players.size();
            } else {
                currentPlayerIndex = (currentPlayerIndex + 2) % players.size();
            }
            skipNextPlayer = false;
        } else {
            // Normal progression (move one position)
            if (reverse) {
                currentPlayerIndex--;
                if (currentPlayerIndex < 0) {
                    currentPlayerIndex = players.size() - 1;
                }
            } else {
                currentPlayerIndex++;
                if (currentPlayerIndex >= players.size()) {
                    currentPlayerIndex = 0;
                }
            }
        }
    }
    
    // Method to check and enforce UNO penalties
    private void checkUnoPenalties() {
        if (unoPenaltyDue && lastPlayerWithOneCard != -1 && 
            players.get(lastPlayerWithOneCard).getHand().size() == 1 && !hasCalledUno) {
            
            Player playerToPenalize = players.get(lastPlayerWithOneCard);
            System.out.println(playerToPenalize.getName() + " N'A PAS DIT UNO ET EST PÉNALISÉ DE 2 CARTES! 🚨");
            
            // Add penalty cards
            playerToPenalize.addCard(deck.drawCard());
            playerToPenalize.addCard(deck.drawCard());
            
            // Reset the flag
            unoPenaltyDue = false;
        }
    }
    
    private void checkAndCallUno(Player player) {
        // If player has only one card left, they must say UNO
        if (player.getHand().size() == 1) {
            if (player.isHuman()) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("VOUS N'AVEZ PLUS QU'UNE CARTE! TAPEZ 'UNO' POUR L'ANNONCER (ou autre chose pour ne rien dire):");
                String input = scanner.nextLine().trim().toUpperCase();
                
                if (input.equals("UNO")) {
                    System.out.println(player.getName() + " CRIE UNO! 🎮");
                    hasCalledUno = true;
                } else {
                    System.out.println(player.getName() + " N'A PAS ANNONCÉ UNO! (Vous serez pénalisé si quelqu'un le remarque)");
                    unoPenaltyDue = true;
                }
            }
        }
    }
    
    // Method to allow players to catch others who didn't say UNO
    public void catchMissingUno(int playerIndex) {
        if (unoPenaltyDue && lastPlayerWithOneCard != -1 && 
            players.get(lastPlayerWithOneCard).getHand().size() == 1 && !hasCalledUno) {
            
            Player playerToPenalize = players.get(lastPlayerWithOneCard);
            Player playerCatching = players.get(playerIndex);
            
            System.out.println(playerCatching.getName() + " A REMARQUÉ QUE " + 
                              playerToPenalize.getName() + " N'A PAS DIT UNO! PÉNALITÉ DE 2 CARTES! 🚨");
            
            // Add penalty cards
            playerToPenalize.addCard(deck.drawCard());
            playerToPenalize.addCard(deck.drawCard());
            
            // Reset the flag
            unoPenaltyDue = false;
        } else {
            System.out.println("PERSONNE N'A OUBLIÉ DE DIRE UNO OU LE JOUEUR A DÉJÀ PLUS D'UNE CARTE.");
        }
    }
    
    // New method to handle catching players by name (used by HumanPlayer)
    public void catchPlayerByName(String playerName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equalsIgnoreCase(playerName)) {
                catchMissingUno(i);
                return;
            }
        }
        System.out.println("JOUEUR NON TROUVÉ! PERSONNE N'EST PÉNALISÉ.");
    }
    
    public void displayGameState() {
        Player player = players.get(currentPlayerIndex);
        if(player.isHuman()) {
            System.out.println("LA MAIN DU JOUEUR:\n ");
            System.out.println(player.getName() + " A " + player.getHand().size() + " CARTES \n");
            for (int i = 0; i < player.getHand().size(); i++) {
                System.out.println((i + 1) + ". " + player.getHand().get(i).toString());
            }
        }
        
        // Display everyone's card count
        System.out.println("--------------------");
        System.out.println("NOMBRE DE CARTES PAR JOUEUR:");
        for (Player p : players) {
            System.out.println(p.getName() + ": " + p.getHand().size() + " cartes");
        }
        
        System.out.println("--------------------");
        System.out.println("C'EST LE TOUR DE : " + players.get(currentPlayerIndex).getName());
        System.out.println("--------------------");
    }
 
    // Modified enforceRules method to implement card stacking
    public boolean enforceRules(Card playedCard) {
        System.out.println("CARTE JOUÉE :\n " + playedCard.toString());   
        Card topCard = deck.getTopCard();
        
        // Vérifier si la carte respecte les règles du jeu
        boolean validPlay = playedCard.getColor() == topCard.getColor() || 
                            playedCard.getValue() == topCard.getValue() || 
                            playedCard.getColor() == Card.Color.Black;
        
        if (validPlay) {
            deck.addToDiscardPile(playedCard); // ✅ Ajouter la carte à la défausse
        }
        switch (playedCard.getType()) {
            case "+2":
                // If there's already a +2 stack, add to it
                if (currentStackType.equals("+2")) {
                    stackedDrawCards += 2;
                    System.out.println("CARTES À PIOCHER ACCUMULÉES: " + stackedDrawCards + " 🃏🃏");
                } else {
                    // Start a new stack
                    currentStackType = "+2";
                    stackedDrawCards = 2;
                    System.out.println("LE JOUEUR SUIVANT DOIT PIOCHER 2 CARTES OU JOUER UN +2! 🃏🃏");
                }
                break;

            case "Skip":
                skipNextPlayer = true;
                System.out.println("LE TOUR DU JOUEUR SUIVANT EST SAUTÉ! ⏭️");
                break;

            case "Reverse":
                reverse = !reverse;
                System.out.println("LE SENS DU JEU EST INVERSÉ! 🔄");
                // In a two-player game, Reverse acts like Skip according to official rules
                if (players.size() == 2) {
                    skipNextPlayer = true;
                    System.out.println("COMME IL Y A DEUX JOUEURS, CETTE CARTE AGIT COMME UN SKIP! ⏭️");
                }
                break;

            case "Wild":
                // Let the player choose a color
                ChooseColor(playedCard, players.get(currentPlayerIndex).isHuman());
                System.out.println("COULEUR CHOISIE: " + playedCard.getColor() + " 🎨");
                
                // Store the chosen color in the deck for the next player to use
                deck.setNextTopCardColor(playedCard.getColor());
                break;

            case "Wild+4":
                // If there's already a Wild+4 stack, add to it
                if (currentStackType.equals("Wild+4")) {
                    stackedDrawCards += 4;
                    System.out.println("CARTES À PIOCHER ACCUMULÉES: " + stackedDrawCards + " 🃏🃏🃏🃏");
                } else {
                    // Start a new stack
                    currentStackType = "Wild+4";
                    stackedDrawCards = 4;
                    System.out.println("LE JOUEUR SUIVANT DOIT PIOCHER 4 CARTES OU JOUER UN WILD+4! 🃏🃏🃏🃏");
                }
                
                // According to official rules, Wild+4 can be challenged if the player has a matching color
                boolean challengeSuccess = false;
                int nextPlayerIndex_wild = getNextPlayerIndex();
                Player nextPlayerWild = players.get(nextPlayerIndex_wild);
                
                if (players.size() > 1 && nextPlayerWild.isHuman() && stackedDrawCards == 4) {
                    // Check if current player has a card matching the previous top card's color
                    boolean hasMatchingColor = false;
                    for (Card card : players.get(currentPlayerIndex).getHand()) {
                        if (card.getColor() == deck.getTopCard().getColor() && 
                            card.getType() != "Wild" && card.getType() != "Wild+4") {
                            hasMatchingColor = true;
                            break;
                        }
                    }
                    
                    // If a human player wants to challenge
                    Scanner scanner = new Scanner(System.in);
                    System.out.println(nextPlayerWild.getName() + ", VOULEZ-VOUS CONTESTER LE WILD+4? (o/n)");
                    String input = scanner.nextLine().toLowerCase();
                    
                    if (input.startsWith("o")) {
                        // Reveal the player's hand for challenge verification
                        System.out.println("VÉRIFICATION DU CHALLENGE...");
                        System.out.println("MAIN DE " + players.get(currentPlayerIndex).getName() + ":");
                        for (Card card : players.get(currentPlayerIndex).getHand()) {
                            System.out.println(card.toString());
                        }
                        
                        if (hasMatchingColor) {
                            challengeSuccess = true;
                            System.out.println("CHALLENGE RÉUSSI! LE JOUEUR " + 
                                              players.get(currentPlayerIndex).getName() + 
                                              " DOIT PIOCHER 4 CARTES! 🚨");
                            for (int i = 0; i < 4; i++) {
                                players.get(currentPlayerIndex).addCard(deck.drawCard());
                            }
                            // Reset the stack if challenge successful
                            stackedDrawCards = 0;
                            currentStackType = "";
                        } else {
                            System.out.println("CHALLENGE ÉCHOUÉ! VOUS DEVEZ PIOCHER 6 CARTES! 🚨");
                            for (int i = 0; i < 6; i++) {
                                nextPlayerWild.addCard(deck.drawCard());
                            }
                            // Reset the stack if challenge failed
                            stackedDrawCards = 0;
                            currentStackType = "";
                        }
                    }
                }
                
                // Let player choose a color
                ChooseColor(playedCard, players.get(currentPlayerIndex).isHuman());
                System.out.println("COULEUR CHOISIE: " + playedCard.getColor() + " 🎨");
                
                // Store the chosen color in the deck
                deck.setNextTopCardColor(playedCard.getColor());
                break;
        }
        return validPlay;
    }
 
    private void ChooseColor(Card playedCard, boolean isHuman) {
        Card.Color color;
        if (isHuman) {
            System.out.println("Choisissez une couleur : Red (R), Blue (B), Green (G), Yellow (Y)");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().toUpperCase();
            
            // Accept single letter inputs for convenience
            if (input.equals("R")) input = "Red";
            else if (input.equals("B")) input = "Blue";
            else if (input.equals("G")) input = "Green";
            else if (input.equals("Y")) input = "Yellow";
            else if (input.length() == 1) {
                // Default to red if invalid single letter
                System.out.println("Couleur invalide, Rouge choisi par défaut");
                input = "Red";
            }
            
            // Otherwise, try to parse the full color name
            if (input.length() > 1) {
                input = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
                while (!(input.equals("Red") || input.equals("Blue") ||
                        input.equals("Green") || input.equals("Yellow"))) {
                    System.out.println("Couleur invalide, choisissez une couleur : Rouge (R), Bleu (B), Vert (G), Jaune (Y)");
                    input = scanner.nextLine().toUpperCase();
                    if (input.equals("R")) input = "Red";
                    else if (input.equals("B")) input = "Blue";
                    else if (input.equals("G")) input = "Green";
                    else if (input.equals("Y")) input = "Yellow";
                    else input = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
                }
            }
            color = Card.Color.valueOf(input);
        } else {
            // AI tries to choose the most advantageous color
            // It will pick the color it has the most of
            Player current = players.get(currentPlayerIndex);
            int[] colorCounts = new int[4]; // Red, Blue, Green, Yellow
            
            for (Card card : current.getHand()) {
                if (card.getColor() == Card.Color.Red) colorCounts[0]++;
                else if (card.getColor() == Card.Color.Blue) colorCounts[1]++;
                else if (card.getColor() == Card.Color.Green) colorCounts[2]++;
                else if (card.getColor() == Card.Color.Yellow) colorCounts[3]++;
            }
            
            int maxIndex = 0;
            for (int i = 1; i < 4; i++) {
                if (colorCounts[i] > colorCounts[maxIndex]) {
                    maxIndex = i;
                }
            }
            
            switch (maxIndex) {
                case 0: color = Card.Color.Red; break;
                case 1: color = Card.Color.Blue; break;
                case 2: color = Card.Color.Green; break;
                default: color = Card.Color.Yellow; break;
            }
            
            System.out.println(current.getName() + " a choisi la couleur : " + color);
        }

        playedCard.setColor(color);
    }
    private Card.Color chooseRandomColor() {
        Card.Color[] colors = {Card.Color.Red, Card.Color.Blue, Card.Color.Green, Card.Color.Yellow};
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }
 
    public boolean checkWinner() {
        for(Player player : players) {
            if(player.getHand().isEmpty()) {
                System.out.println("LE JEU EST TERMINÉ ! 🎉 FÉLICITATIONS À " + player.getName());
                return true;
            }
        }
        return false;
    }

	public int getCurrentPlayerIndex() {
		// TODO Auto-generated method stub
		return currentPlayerIndex;
	}
	public void drawCard(Player player) {
	    Card card = deck.drawCard();
	    if (card != null) {
	        player.addCard(card);
	        System.out.println(player.getName() + " a pioché une carte :\n" + card);
	    } else {
	        System.out.println("Le deck est vide, impossible de piocher.");
	    }
	}
	
	public int getStackedDrawCards() {
	    return stackedDrawCards;
	}

	public String getCurrentStackType() {
	    return currentStackType;
	}

	public void resetStackedDrawCards() {
	    stackedDrawCards = 0;
	    currentStackType = "";
	}

}
