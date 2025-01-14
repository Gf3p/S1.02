
// Effects are made by objects that you can find in the game.

enum Effet{
    VOLER,              // Takes N-amount of points from choosen player.
    REPONDRE,           // Answers the question and gives you 500 points.
    INDICE              // Hint. Opens the N first characters in the answer.
    }

// Class of an object.

class Objet{
    String nom;         // Name of the object
    Char icon;          // Emoji that represents
    Effet effet;        // Effect that does
    int effet_value;    // Effect value (strength)
    boolean actif;      // Is object active
    int durabilite;     // HP of an object
}