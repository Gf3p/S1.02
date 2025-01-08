import extensions.CSVFile;
class CourseAuxPt extends Program{
    final String[] PIONS= new String[]{"♙","♟"};

    Joueur newJoueur(String nom, int[] pos, int nbStar, int points){ //  Fonction qui crée un objet Joueur
        Joueur j = new Joueur();
        j.nom=nom;
        j.position=pos;
        j.nbStar=nbStar;
        j.points=points;
        return j;
    }

    Case newCase(boolean[] directions, String contenu ,boolean accessible){ //  Fonction qui crée un objet Case
        Case c = new Case();
        c.directions=directions;
        c.contenu=contenu;
        c.accessible=accessible;
        return c;
    }

    Question newQuestion(String contenu,String reponse ,String qcm){ //  Fonction qui crée un objet Question
        Question q = new Question();
        q.contenu = contenu;
        q.reponse = reponse;
        q.qcm = qcm;
        return q;
    }
 
    Joueur saisiJoueur(String nom){ //  Définit un joueur avec le nom entrée en paramètre
        return newJoueur(nom, new int[]{0,0},0 , 3000);
    }

    Joueur[] creationJoueurs(){ //  Crée un tableau de l'enssemble des joueurs de la partie
        Joueur[] joueurs = new Joueur[2];
        println("Voulez-vous charger une sauvegarde ? (o/n)");
        if(readChar()=='o'){
            joueurs = chargerSave();
        }else{
            int idxJ = 0;
            while(idxJ<length(joueurs)){
                println("Entrez le nom du Joueur " + (idxJ+1));
                String nomJ = readString();
                if(!equals(nomJ,"")){
                    joueurs[idxJ] = saisiJoueur(nomJ);
                    idxJ++;
                }else{
                    println("Nom invalide");
                }     
            }
        }
        return joueurs;
    }

    Case chargerCase(String c){ //  Crée un objet Case qui suit le caractéristique donné par le String c
        boolean[] caseDir = {false,false,false,false};
        String caseCont = "";
        boolean accessible = true;
        if(charAt(c,0)=='s'){
            caseCont = "Star";
        }
        if(charAt(c,1)=='g'){
            caseDir[0] = true;
        }
        if(charAt(c,2)=='h'){
            caseDir[1] = true;
        }
        if(charAt(c,3)=='d'){
            caseDir[2] = true;
        }
        if(charAt(c,4)=='b'){
            caseDir[3] = true;
        }
        if(equals(c,"     ")){
            accessible  = false;
        }
        return newCase(caseDir, caseCont , accessible);
    }

    Case[][] initialiserPlateau(){ //  Crée un plateau en suivant le plan donnée par plateau.csv
        Case[][] plateau = new Case[3][3];
        CSVFile p = loadCSV("ressources/plateau.csv");
        for(int i  = 0; i<rowCount(p) ; i++){
            for(int j  = 0; j<columnCount(p); j++){
                plateau[i][j]=chargerCase(getCell(p,i,j));
            }
        }
        return plateau;
    }

    boolean joueurEstIci(Joueur joueur1, int x, int y){ //  Renvoie un boolean qui indique si le joueur est aux coordonées (x,y)
        return joueur1.position[0]==x && joueur1.position[1]==y;
    }

    String plateauToString(Case[][] plateau,Joueur[] joueurs){ //  Renvoie le plateau sous forme de String pour permettre un affichage
        String res="";
        for(int i = 0; i<length(plateau,1);i++){
            for(int j = 0; j<length(plateau,2);j++){
                res+=" | ";
                if(!plateau[i][j].accessible){
                    res+=" X ";
                }else{
                    if(plateau[i][j].contenu != "" && equals(plateau[i][j].contenu,"Star")){
                        res+="★"; // Place les joueurs sur le plateau
                    }else{
                        res+=" ";
                    }
                    for(int k = 0; k<length(joueurs);k++){ // Place les joueurs sur le plateau
                        if(joueurEstIci(joueurs[k],i , j)){ //  Verifie si un joueur est présent sur la caes (i,j)
                            res+=PIONS[k];
                        }else{
                            res+=" ";
                        }
                    }
                }
            }
            res+=" |";
            res+="\n\n";
        }
        return res;
    }

    Case donneCaseJoueur(Joueur j, Case[][] plateau){ //  Renvoie l'objet Case sur laquelle est l'objet Joueur j 
        return plateau[j.position[0]][j.position[1]];
    }

    boolean deplacementValide(Case[][] plateau, Joueur j ,char depla){ //  Renvoie un boolean qui indique si un Joueur j peut se déplacer dans la direction depla 
        Case posJoueur = plateau[j.position[0]][j.position[1]];
        boolean valide = false;
        if(depla == 'g' && posJoueur.directions[0]){
            valide = true;
        }else if(depla == 'h' && posJoueur.directions[1]){
            valide = true;
        }else if(depla == 'd' && posJoueur.directions[2]){
            valide = true;              
        }else if(depla == 'b'&& posJoueur.directions[3]){
            valide = true;               
        }else{
            println("Impossible de se déplacer dans cette direction !");
        }
        return valide;
    }

    void realiseDepla(Case[][] plateau, Joueur j ,char depla){ //  Réalise le déplacement du Joueur J dans la direction depla
        int[] deplacement = new int[2];
        if(depla == 'g' ){
            deplacement[0] = 0;
            deplacement[1] = -1;
        }else if(depla == 'h'){
            deplacement[0] = -1;
            deplacement[1] = 0;
        }else if(depla == 'd'){
            deplacement[0] = 0;
            deplacement[1] = 1;              
        }else if(depla == 'b'){
            deplacement[0] = 1;
            deplacement[1] = 0;               
        }    
        j.position[0] += deplacement[0];
        j.position[1] += deplacement[1];
    }

    void verifStar(Joueur j, Case c){ //  Verifie si le joueur J est sur une Case c contenant un étoile, puis verifie si le joueur j a assez de points et lui propose de l'acheter
        if(equals(c.contenu,"Star") && j.points >= 3000){
            println("Vous avez assez de points pour acheter l'étoile. Voulez-vous l'acheter pour 3 000 pt ? (o/n)");
            if(readChar()=='o'){
                j.points-=3000;
                j.nbStar++;
                } 
        }else if(equals(c.contenu,"Star")){
            println("Vous n'avez pas assez de points pour acheter l'étoile.");
            
        }
    }

    Question chargerQuestion(String filename){ //  Retourne un objet Question du fichier questions.csv
        CSVFile questions = loadCSV(filename);
        int noQuest = (int)(random()*rowCount(questions)); //  Donne l'indice d'une question aleatoire 
        Question q = newQuestion(getCell(questions,noQuest,0),getCell(questions,noQuest,1),getCell(questions,noQuest,2));
        return q;
    }

    void poseQuestion(Joueur j,String filename){ //  Pose une question et attribue 
        Question q = chargerQuestion("ressources/"+filename);
        println(q.contenu);
        if(!equals(q.qcm,"false")){
            println(q.qcm);
        }
        long tempsDep = getTime(); 
        String repJ = readString();
        if(equals(repJ,q.reponse)){  //  Verifie si la réponse du joueur est la bonne réponse
            long tempsfin = getTime();
            long tempsRep = (tempsfin-tempsDep)/1000; //  Calcul le temps de réponse 
            if(tempsRep<=0){ //  Evite de diviser par un temps en seconde égal à 0
                tempsRep=1;
            }
            println("Vous avez répondu en " + tempsRep + "s");
            j.points += 1000/tempsRep;;
            println("Bonnne réponse +" + 1000/tempsRep + " Points ! ");
        }else{
            println("Mauvaise réponse. Pas de Points, la réponse était : " + q.reponse);
        }
    }

    void affichageStatPartie(Joueur[] joueurs){
        for(int i = 0; i<length(joueurs) ; i++){
            println("   "+ PIONS[i] + " - " + joueurs[i].nom + " " + joueurs[i].points + "pt " + joueurs[i].nbStar + "★");
        }
        println();
    }

    Joueur maxStar(Joueur[] joueurs){ //  Donne le joueurs avec le plus d'étoiles
        int idxJ = 0;
        for(int i = 1; i<length(joueurs); i++){
            if(joueurs[i].nbStar>joueurs[idxJ].nbStar){
                idxJ=i;
            }
        }
        return joueurs[idxJ];
    }

    void controleSaisi(Joueur[] joueurs,int idxJ,Case[][] plateau){ //  Contrôle la saisi du joueur et réalise des actions en fonction de la saisie
        boolean valide = false;
        println("g - Aller sur la case à gauche  h - Aller sur la case en haut    d - Aller sur la case à droite     b - Aller sur la case en bas     s - Pour Sauvegarder ");
        println("Entrez une lettre pour réaliser une action :");
        while(!valide){
            char entree = readChar();
            if(entree == 's'){
                sauvegarde(joueurs,idxJ);
                controleSaisi(joueurs ,idxJ ,plateau);
                valide = true;
            }else if((entree == 'g' || entree == 'h'|| entree == 'd' ||entree == 'b') && deplacementValide(plateau, joueurs[idxJ] , entree)){
                realiseDepla(plateau ,joueurs[idxJ] ,entree);
                valide = true;
            }else{
                println("Entrez une lettre valide.");
            }
        }
    }

    void sauvegarde(Joueur[] joueurs,int tour){ //  Sauvegarde les joueurs dans un fichier avec le nom
        println("Donnez un nom à la sauvegarde : ");
        String filename = readString();
        String[][] jeu = new String[length(joueurs)][6];
        for(int i = 0; i<length(joueurs); i++){
            jeu[i][0] = joueurs[i].nom;
            jeu[i][1] = joueurs[i].position[0] + "";
            jeu[i][2] = joueurs[i].position[1] + "";
            jeu[i][3] = joueurs[i].nbStar + "";
            jeu[i][4] = joueurs[i].points + "";
            if(tour>length(joueurs)-1){
                tour=0;
            }
            jeu[i][5] = tour + ""; // Permet de sauvegarder et de savoir à qui c'était le tour en modifiant l'ordre du Joueur dans la liste joueurs lors du chargement de la sauvegarde pour une prochaine partie
            tour++;
        }
        saveCSV(jeu,filename+"_save"+".csv");
    }

    Joueur[] chargerSave(){ //  récupert la sauvegarde des joueurs de la partie précédente
        println("Entrez le nom de la sauvegarde");
        CSVFile save = loadCSV(readString() + "_save"+ ".csv");
        Joueur[] joueurs = new Joueur[rowCount(save)];
        for(int i = 0; i<length(joueurs) ;i++){
            int idxJ = stringToInt(getCell(save,i,5));
            String nom = getCell(save,i,0);
            int nbStar = stringToInt(getCell(save,i,3));
            int points = stringToInt(getCell(save,i,4));
            int[] position = new int[]{stringToInt(getCell(save,i,1)),stringToInt(getCell(save,i,2))};
            joueurs[idxJ]=newJoueur(nom,position,nbStar, points); //  Charge un joueur de la sauvegarde dans la liste joueurs en fonction de l'ordre sauvegarder
        }
        return joueurs;
    }

    void algorithm(){
        println("test6");
        Joueur[] joueurs = creationJoueurs();
        Case[][] plateau = initialiserPlateau();
        int tour = 0;
        while(joueurs[0].nbStar<1 && joueurs[1].nbStar<1){
            println("Tour de " + joueurs[tour].nom);
            affichageStatPartie(joueurs);
            println(plateauToString(plateau,joueurs));
            controleSaisi(joueurs,tour,plateau);
            println(plateauToString(plateau,joueurs));
            poseQuestion(joueurs[tour],"questions.csv");
            verifStar(joueurs[tour], donneCaseJoueur(joueurs[tour],plateau));
            tour++;
            if(tour>length(joueurs)-1){
                tour=0;
            }
        }
        println(maxStar(joueurs).nom + " a Gagné !");
    }
}
