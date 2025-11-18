package game;
import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner scanner = new Scanner(System.in);

    public HumanPlayer(String name) {
        super(name, true); // Player is human
    }

    public Card chooseCardPlay(Card topCard) {
        // Get valid cards
        List<Card> validCards = getValidCards(topCard);

        // If no valid cards, return null to trigger card drawing
        if (validCards.isEmpty()) {
            System.out.println("VOUS N'AVEZ AUCUNE CARTE VALIDE. VOUS DEVEZ PIOCHER UNE CARTE.");
            return null;
        }

        // Display top card and valid cards
        displayTopCard(topCard);
        displayValidCards(validCards);

        // Additional options
        System.out.println("0. PIOCHER UNE CARTE");

        if (getHand().size() == 2) {
            System.out.println("U. CRIER 'UNO'");
        }

        System.out.println("C. DÉNONCER UN ADVERSAIRE QUI A OUBLIÉ DE DIRE 'UNO'");

        // Get user choice
        int choice = getUserChoice(validCards);

        if (choice == -1) {
            return null; // Draw a card
        }

        // Return the chosen card
        return validCards.get(choice);
    }

    // Display the top card with borders
    private void displayTopCard(Card topCard) {
        System.out.println("CARTE DU DESSUS:");
        System.out.println(topCard.toString());
    }

    // Display valid user cards with borders
    private void displayValidCards(List<Card> validCards) {
        System.out.println("VOS CARTES VALIDES:");
        for (int i = 0; i < validCards.size(); i++) {
            System.out.println((i + 1) + ". " + validCards.get(i).toString());
        }
        System.out.println("-------------------");
    }

    // Ask the user to choose a card by number
    private int getUserChoice(List<Card> validCards) {
        int choice = -2;
        while (choice < -1 || choice >= validCards.size()) {
            System.out.println("CHOISISSEZ UNE CARTE PAR NUMÉRO (0 POUR PIOCHER, U POUR DIRE UNO, C POUR DÉNONCER):");
            String input = scanner.nextLine().trim();

            // Check for UNO call
            if (input.equalsIgnoreCase("U") || input.equalsIgnoreCase("UNO")) {
                if (getHand().size() == 2) {
                    System.out.println(getName() + " A CRIÉ UNO PRÉVENTIVEMENT! 🎮");
                } else {
                    System.out.println("VOUS NE POUVEZ PAS CRIER UNO MAINTENANT!");
                }
                continue;
            }

            // Check for catching someone who didn't say UNO
            if (input.equalsIgnoreCase("C")) {
                catchUnoCheater();
                continue;
            }

            try {
                choice = Integer.parseInt(input) - 1; // Adjust for 0-indexed array
                if (choice == -1) {
                    // Player wants to draw a card
                    return -1;
                }
                
                if (choice < 0 || choice >= validCards.size()) {
                    System.out.println("CHOIX INVALIDE! VEUILLEZ CHOISIR UN NUMÉRO ENTRE 0 ET " + validCards.size());
                }
            } catch (NumberFormatException e) {
                System.out.println("ENTRÉE INVALIDE! VEUILLEZ ENTRER UN NUMÉRO.");
            }
        }
        return choice;
    }

    // Function to handle catching players who didn't say UNO
    private void catchUnoCheater() {
        System.out.println("QUEL JOUEUR ACCUSEZ-VOUS D'AVOIR OUBLIÉ DE DIRE UNO? (ENTREZ LE NOM)");
        String accusedName = scanner.nextLine().trim();
        
        // The Game class will handle the actual checking logic
        System.out.println("VOUS ACCUSEZ " + accusedName + " D'AVOIR OUBLIÉ DE DIRE UNO!");
        
        // Note: The actual enforcement will need to be done in the Game class
        // We'll add a method to Game to handle this accusation
    }
}