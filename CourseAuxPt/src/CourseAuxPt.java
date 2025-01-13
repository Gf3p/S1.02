import extensions.CSVFile;
class CourseAuxPt extends Program{
    final String[] PIONS= new String[]{"♙","♟","♙","♟"};
    final String[] FLECHES = new String[]{"←","↑","→","↓"};
    final String BARRE = " - - - +";
    int nbJoueur;

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
        Joueur[] joueurs;
        println("Voulez-vous charger une sauvegarde ? (o/n)");
        if(readChar()=='o'){
            joueurs = chargerSave();
        }else{
            nbJoueur = readInt(); // !!  Créer une fonct qui verif si nbJoueur  >= 2 et <=4
            joueurs = new Joueur[nbJoueur];
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
        CSVFile p = loadCSV("plateau.csv");
        int nbRow = rowCount(p);
        int nbCol = columnCount(p);
        Case[][] plateau = new Case[nbRow][nbCol];
        for(int i  = 0; i<nbRow; i++){
            for(int j  = 0; j<nbCol; j++){
                plateau[i][j]=chargerCase(getCell(p,i,j));
            }
        }
        return plateau;
    }

    boolean joueurEstIci(Joueur joueur1, int x, int y){ //  Renvoie un boolean qui indique si le joueur est aux coordonées (x,y)
        return joueur1.position[0]==x && joueur1.position[1]==y;
    }

    String tableauLigneCase(String[] ligne){
        String res="";
        for(int i = 0; i < length(ligne) ;i++){
            res+=ligne[i]+"\n";
        }
        return res;
    }

    String[] creerCase(Case c, String[] joueurAPlacer,String[] ligne){
        int idx = 0;
        ligne[0]+=joueurAPlacer[idx];
        idx++;
        ligne[0]+="  ";
        if(c.directions[1]){
            ligne[0]+=FLECHES[1];
        }else{
            ligne[0]+=" ";
        }
        ligne[0]+="  ";
        ligne[0]+=joueurAPlacer[idx]+"|";
        if(c.directions[0]){
            ligne[1]+=FLECHES[0]+"  ";
        }else{
            ligne[1]+="   ";
        }
        if(equals(c.contenu,"Star")){
            ligne[1]+="★";
        }else{
            ligne[1]+=" ";
        }
        if(c.directions[2]){
            ligne[1]+="  "+FLECHES[2];
        }else{
            ligne[1]+="   ";
        }
        ligne[1]+="|";
        idx++;
        ligne[2]+=joueurAPlacer[idx];
        ligne[2]+="  ";
        if(c.directions[3]){
            ligne[2]+=FLECHES[3];
        }else{
            ligne[2]+=" ";
        }
        ligne[2]+="  "; 
        idx++;
        ligne[2]+=joueurAPlacer[idx]+"|";
        return ligne;
    }

    String plateauToString(Case[][] plateau,Joueur[] joueurs){ //  Renvoie le plateau sous forme de String pour permettre un affichage
        String res="";
        String bordureHoriz = "+";
        for(int i = 0; i<length(plateau,1);i++){
            bordureHoriz = "+";
            String[] ligne = new String[]{"|","|","|"};
            for(int j = 0; j<length(plateau,2);j++){
                String[] joueurAPlacer = new String[]{" "," "," "," "};
                bordureHoriz+=BARRE;
                for(int k = 0; k<length(joueurs); k++){
                    if(joueurs[k].position[0]==i && joueurs[k].position[1]==j){
                        joueurAPlacer[k]=PIONS[k];
                    }
                }
                ligne = creerCase(plateau[i][j],joueurAPlacer,ligne);
            }
            res+=tableauLigneCase(ligne);
            res+=bordureHoriz+"\n";
        }
        res = bordureHoriz + "\n" + res;
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
        Question q = chargerQuestion(filename);
        println(q.contenu);
        if(!equals(q.qcm,"false")){
            println(q.qcm);
        }
        long tempsDep = getTime(); 
        String repJ = readString();
        clearScreen();
        if(equals(toLowerCase(repJ),q.reponse)){  //  Verifie si la réponse du joueur est la bonne réponse
            long tempsfin = getTime();
            long tempsRep = (tempsfin-tempsDep)/1000; //  Calcul le temps de réponse 
            if(tempsRep<=0){ //  Evite de diviser par un temps en seconde égal à 0
                tempsRep=1;
            }
            j.points += 1000/tempsRep;;
            println("Bonnne réponse vous avez répondu en " + tempsRep + "s \n Vous gagnez : " + 1000/tempsRep + " Points ! ");
        }else{
            println("Mauvaise réponse. Pas de Points, la réponse était : " + q.reponse);
        }
    }

    void affichageStatPartie(Joueur[] joueurs, int tour){
        println("Tour de " + PIONS[tour] +joueurs[tour].nom);
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
    void controleValidation(){

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
        saveCSV(jeu,"save/"+filename+".csv");
    }

    Joueur[] chargerSave(){ //  récupert la sauvegarde des joueurs de la partie précédente
        String[] listeSave = getAllFilesFromDirectory("save");
        println(listerSave(listeSave) + "\nEntrez le numéro de la sauvegarde");
        int idxsave = readInt()-1;
        CSVFile save = loadCSV("save/"+listeSave[idxsave]);
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
    String listerSave(String[] listeSave){
        String res = "";
        String save;
        for(int i = 1; i < length(listeSave)+1; i++){
            save = listeSave[i-1];
            res += i+") "+substring(save,0,length(save)-4)+"\n";
        }
        return res;
    }
    void algorithm(){
        Joueur[] joueurs = creationJoueurs();
        Case[][] plateau = initialiserPlateau();
        int idxTourJoueur = 0;
        clearScreen();
        while(joueurs[0].nbStar<1 && joueurs[1].nbStar<1){
            affichageStatPartie(joueurs,idxTourJoueur);
            println(plateauToString(plateau,joueurs));
            controleSaisi(joueurs,idxTourJoueur,plateau);
            clearScreen();
            affichageStatPartie(joueurs,idxTourJoueur);
            println(plateauToString(plateau,joueurs));
            poseQuestion(joueurs[idxTourJoueur],"questions.csv");
            delay(3000);
            clearScreen();
            verifStar(joueurs[idxTourJoueur], donneCaseJoueur(joueurs[idxTourJoueur],plateau));
            idxTourJoueur++;
            if(idxTourJoueur>length(joueurs)-1){
                idxTourJoueur=0;
            }
        }
        println(maxStar(joueurs).nom + " a Gagné !");
    }
}