import extensions.CSVFile;
class CourseAuxPt extends Program{
    final String[] PIONS= new String[]{"♙","♟","♖","♜"};
    final String[] FLECHES = new String[]{"←","↑","→","↓"};
    final String BARRE = " - - - +";
    Objet[] listeDesObjets = chargerObjet();
    int nbJoueur;

    Joueur newJoueur(String nom, int[] pos, int nbStar, int points){ //  Fonction qui crée un objet Joueur
        Joueur j = new Joueur();
        j.nom=nom;
        j.position=pos;
        j.nbStar=nbStar;
        j.points=points;
        j.inventaire=new Objet[]{listeDesObjets[0],listeDesObjets[1]};
        return j;
    }

    Case newCase(boolean[] directions, String contenu ,boolean accessible){ //  Fonction qui crée un objet Case
        Case c = new Case();
        c.directions=directions;
        c.contenu=contenu;
        c.accessible=accessible;
        return c;
    }

    Question newQuestion(String contenu,String reponse){ //  Fonction qui crée un objet Question
        Question q = new Question();
        q.contenu = contenu;
        q.reponse = reponse;
        return q;
    }
    
    Objet newObjet(String nom, String icon, Effet effet, int effet_value, boolean actif, int prix){
        Objet o = new Objet();
        o.nom = nom;
        o.icon = icon;
        o.effet = effet;
        o.effet_value = effet_value;
        o.actif = actif;
        o.prix = prix;
        return o;
    }
    
    Joueur saisiJoueur(String nom){ //  Définit un joueur avec le nom entrée en paramètre
        return newJoueur(nom, new int[]{0,0},0 , 0);
    }

    Joueur[] creationJoueurs(){ //  Crée un tableau de l'enssemble des joueurs de la partie
        Joueur[] joueurs;
        if(controleValidation("Voulez-vous charger une sauvegarde ? (o/n)")){
            joueurs = chargerSave();
        }else{
            int nbJoueur = controleNbJoueur();
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
        }else if(charAt(c,0)=='m'){
            caseCont = "Magasin";
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
        if(equals(substring(c,1,length(c)),"    ")){
            accessible  = false;
        }
        return newCase(caseDir, caseCont , accessible);
    }

    Case[][] initialiserPlateau(){ //  Crée un plateau en suivant le plan donnée par plateau.csv
        CSVFile p = loadCSV("ressources/plateau.csv");
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

    String tableauLigneCase(String[] ligne){ //Renvoie une ligne de case sous forme de String 
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
        }else if(equals(c.contenu,"Magasin")){
            ligne[1]+="♛";
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
    int nbToursPartie(){
        boolean valide = false;
        int nbTours = 0;
        while(!valide){
            nbTours = controleSaisi(0,1000,"Combiez de tours voulez-vous que la Partie dure ?");
            if(nbTours >= 10 && nbTours <= 100){
                valide = true;
            }else{
                println("Le nombre de tours ne peut pas être moins de 10 et plus de 100");
            }
        }
        return nbTours;
    }

    Case donneCaseJoueur(Joueur j, Case[][] plateau){ //  Renvoie l'objet Case sur laquelle est l'objet Joueur j 
        return plateau[j.position[0]][j.position[1]];
    }

    boolean deplacementValide(Case[][] plateau, Joueur j ,char depla){ //  Renvoie un boolean qui indique si un Joueur j peut se déplacer dans la direction depla 
        Case posJoueur = plateau[j.position[0]][j.position[1]];
        boolean valide = false;
        if(depla == 'q' && posJoueur.directions[0]){
            valide = true;
        }else if(depla == 'z' && posJoueur.directions[1]){
            valide = true;
        }else if(depla == 'd' && posJoueur.directions[2]){
            valide = true;              
        }else if(depla == 's'&& posJoueur.directions[3]){
            valide = true;               
        }else{
            clearScreen();
            println("Impossible de se déplacer dans cette direction !");
            delay(1000);
        }
        return valide;
    }

    void realiseDepla(Case[][] plateau, Joueur j ,char depla){ //  Réalise le déplacement du Joueur J dans la direction depla
        int[] deplacement = new int[2];
        if(depla == 'q' ){
            deplacement[0] = 0;
            deplacement[1] = -1;
        }else if(depla == 'z'){
            deplacement[0] = -1;
            deplacement[1] = 0;
        }else if(depla == 'd'){
            deplacement[0] = 0;
            deplacement[1] = 1;              
        }else if(depla == 's'){
            deplacement[0] = 1;
            deplacement[1] = 0;               
        }    
        j.position[0] += deplacement[0];
        j.position[1] += deplacement[1];
    }

    boolean joueurSurStar(int[] cooStar, int[] cooJoueur){
        return cooStar[0] == cooJoueur[0] && cooStar[1] == cooJoueur[1];
    }

    void verifStar(Joueur j, Case[][] plateau, int[] cooStarActu){ //  Verifie si le joueur J est sur une Case c contenant un étoile, puis verifie si le joueur j a assez de points et lui propose de l'acheter
        if(j.points >= 3000){
            if(controleValidation("Vous avez assez de points pour acheter l'étoile. Voulez-vous l'acheter pour 3 000 pt ? (o/n)")){
                j.points-=3000;
                j.nbStar++;
                modifeIdxStar(plateau, cooStarActu);
            } 
        }else{
            println("Vous n'avez pas assez de points pour acheter l'étoile.");
            
        }
    }

    int[] donneIdxStar(Case[][] plateau){ // Donne les coordonnées de l'étoile sur le plateau
        int[] coo = new int[2];
        for(int i = 0; i<length(plateau,1); i++){
            for(int j = 0; j<length(plateau,2); j++){
                if(equals(plateau[i][j].contenu,"Star")){
                    coo[0] = i;
                    coo[1] = j;
                }
            }
        }
        return coo;
    }

    int[] donneCooRandom(int lengthX, int lengthY){ //Donne des coordonnée aléatoire dans les intervales 0,lengthX et 0,lengthY
        int x = (int)(lengthX*random());
        int y = (int)(lengthY*random());
        int[] coo = new int[]{x,y};
        return coo;
    }

    void modifeIdxStar(Case[][] plateau, int[] cooStarActu){
        plateau[cooStarActu[0]][cooStarActu[1]].contenu = "";
        int[] newCoo = donneCooRandom(length(plateau,1),length(plateau,1));
        boolean place = false;
        while(!place){
            if(plateau[newCoo[0]][newCoo[1]].accessible && equals(plateau[newCoo[0]][newCoo[1]].contenu,"")){
                plateau[newCoo[0]][newCoo[1]].contenu = "Star";
                place = true;
            }else{
                newCoo = donneCooRandom(length(plateau,1),length(plateau,1));
            }
        }
        cooStarActu = newCoo;
    }

    Question chargerQuestion(String filename){ //  Retourne un objet Question du fichier questions.csv
        CSVFile questions = loadCSV(filename);
        int noQuest = (int)(random()*rowCount(questions)); //  Donne l'indice d'une question aleatoire 
        Question q = newQuestion(getCell(questions,noQuest,0),getCell(questions,noQuest,1));
        return q;
    }

    void poseQuestion(Joueur j,String filename){ //  Pose une question et attribue 
        Question q = chargerQuestion(filename);
        println(q.contenu);
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
        println("Tour de " + joueurs[tour].nom + " " + PIONS[tour]);
        for(int i = 0; i<length(joueurs) ; i++){
            println("   "+ PIONS[i] + " - " + joueurs[i].nom + " " + joueurs[i].points + "pt " + joueurs[i].nbStar + "★");
        }
        println();
    }

    String maxStar(Joueur[] joueurs){ //  Donne le joueurs avec le plus d'étoiles
        int nbGagnant = 1;
        Joueur[] gagnants = new Joueur[length(joueurs)];
        gagnants[0] = joueurs[0];
        for(int i = 1; i<length(joueurs); i++){
            if(joueurs[i].nbStar > gagnants[0].nbStar){
                gagnants = new Joueur[length(joueurs)];
                gagnants[0] = joueurs[i];
                nbGagnant = 1;
            }else if((joueurs[i].nbStar == gagnants[0].nbStar)){
                gagnants[nbGagnant] = joueurs[i];
                nbGagnant++;
            }
        }
        String res = gagnants[0].nom;
        for(int j = 1; j<nbGagnant; j++){
            res += "   et   "+gagnants[j].nom;
        }
        return res;
    }

    void controleSaisi(Joueur[] joueurs,int idxJ,Case[][] plateau,String plateauAffichable){ //  Contrôle la saisi du joueur et réalise des actions en fonction de la saisie
        boolean valide = false;
        clearScreen();
        while(!valide){
            affichageStatPartie(joueurs,idxJ);
            println(plateauAffichable);
            println("q - Aller sur la case à gauche \nz - Aller sur la case en haut \nd - Aller sur la case à droite \ns - Aller sur la case en bas \nc - Pour Sauvegarder \ni - Pour accéder à l'inventaire");
            println("Entrez une lettre pour réaliser une action :");
            String temp = toLowerCase(readString());
            if(temp != "" && length(temp) < 2){
                char entree = charAt(temp,0);
                if(entree == 'c'){
                    sauvegarde(joueurs,idxJ);
                    clearScreen();
                }else if((entree == 'q' || entree == 'z'|| entree == 's' ||entree == 'd') && deplacementValide(plateau, joueurs[idxJ] , entree)){
                    realiseDepla(plateau ,joueurs[idxJ] ,entree);
                    valide = true;
                }else if(entree == 'i'){
                    clearScreen();
                    utilisationInventaire(joueurs, idxJ, plateau );
                    println("Appuyer sur Entrer pour quitter l'inventaire");
                    readString();
                    clearScreen();
                }else{
                    clearScreen();
                    println("Entrez une lettre valide ou une direction valide.");
                    delay(1000);
                    clearScreen();
                }
            }else{
                clearScreen();
                println("Entrez une lettre valide ou une direction valide.");
            }
        }
    }

    int controleNbJoueur(){
        int nbJoueur = controleSaisi(0, 100, "Entrée le nombre de joueur");
        while(nbJoueur < 2 || nbJoueur > 4){
            println("Entrée invalide le nombre de joueurs doit être entre 2 et 4");
            nbJoueur = controleSaisi(0, 100, "Entrez le nombre de joueur");
        }
        return nbJoueur;
    }

    int controleSaisi(int idxDeb,int idxFin,String question){//Controle si l'indexe saisie est dans la liste et est valide 
        println(question);
        String temp = readString();
        boolean valide = false;
        int val = -999;
        while(!valide){
            if(!equals(temp,"") && estInt(temp)){
                val = stringToInt(temp);
                if(val >= idxDeb || val <= idxFin){
                    valide = true;
                }else{
                    println("Entrée invalide !");
                    println(question);
                    temp = readString();
                }
            }else{
                    println("Entrée invalide !");
                    println(question);
                    temp = readString();
            }
        }
        return val;
    }

    boolean estInt(String truc){// verifie si un String entré en paramètre peut être transformer en Int avec la fonction StringToInt()
        boolean valide = true;
        int idx = 0;
        while(valide && idx < length(truc)){
            if(charAt(truc,idx) < '0' || charAt(truc,idx) > '9'){
                valide = false;
            }
            idx++;
        }
        return valide;
    }

    boolean controleValidation(String question){
        println(question);
        String entree = readString();
        boolean res = true;
        char val ;
        boolean entreeValide = false;
        while(!entreeValide){
            if(length(entree)==1){
                val =  charAt(entree,0);
                if(val != 'o' && val != 'O' && val != 'n' && val != 'N'){
                    println("Entrée invalide !");
                    println(question);
                    entree = readString();
                    val  = charAt(entree,0);
                }else{
                    entreeValide = true;
                    if(val != 'o' && val != 'O'){
                        res = false;
                    }
                }
            }else{
                println("Entrée invalide !");
                    println(question);
                    entree = readString();
                    val = charAt(entree,0);
            }
        }
        return res;
    }

    void sauvegarde(Joueur[] joueurs,int tour){ //  Sauvegarde les joueurs dans un fichier avec le nom
        println("Donnez un nom à la sauvegarde : ");
        String filename = readString();
        String[][] jeu = new String[length(joueurs)][6];
        int temp;
        for(int i = length(joueurs)-1; i>-1; i--){
            jeu[i][0] = joueurs[i].nom;
            jeu[i][1] = joueurs[i].position[0] + "";
            jeu[i][2] = joueurs[i].position[1] + "";
            jeu[i][3] = joueurs[i].nbStar + "";
            jeu[i][4] = joueurs[i].points + "";
            if((i-tour)<0){
                tour = - length(joueurs) + 1;
            }
            temp = i - tour;
            jeu[i][5] = temp + ""; // Permet de sauvegarder et de savoir à qui c'était le tour en modifiant l'ordre du Joueur dans la liste joueurs lors du chargement de la sauvegarde pour une prochaine partie
        }
        saveCSV(jeu,"save/"+filename+".csv");
    }

    Joueur[] chargerSave(){ //  récupert la sauvegarde des joueurs de la partie précédente
        String[] listeSave = getAllFilesFromDirectory("save");
        println(listerSave(listeSave));
        int idxsave = controleSaisi(1,length(listeSave),"Entrez le numéro de la sauvegarde")-1;
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

    Effet StringToEffet(String temp){
        Effet ef = Effet.NULL;
        if(equals(temp,"VOLER")){
            ef = Effet.VOLER;
        }else{
            ef = Effet.TELEPORTER;
        }
        return ef;
    }

    Objet[] chargerObjet(){
        CSVFile save = loadCSV("ressources/objet.csv");
        Objet[] objets = new Objet[rowCount(save)];
        for(int i = 0; i<length(objets) ;i++){
            String nom = getCell(save,i,0);
            String icon = getCell(save,i,1);
            Effet effet = StringToEffet(getCell(save,i,2));
            int effet_value = stringToInt(getCell(save,i,3));
            boolean actif = false;
            int prix =stringToInt(getCell(save,i,5));
            objets[i] = newObjet(nom , icon, effet, effet_value, actif ,prix);
        }
        return objets;
    }
    
    String inventaireToString(Objet[] inv){
        String res="Inventaire\n\n";
        for(int i = 0; i<length(inv); i++){
            res+=i+1+")  "+inv[i].icon+"  "+inv[i].nom+"\n";
        }
        return res;
    }

    void ajoutObjet(Joueur j,Objet o){
        Objet[] newInv = new Objet[length(j.inventaire)+1];
        for(int i = 0; i<length(j.inventaire); i++){
            newInv[i] = j.inventaire[i];
        }
        newInv[length(j.inventaire)] = o;
        j.inventaire = newInv;
    }

    void suprObjet(Joueur j,int idxObj){
        Objet[] newInv = new Objet[length(j.inventaire)-1];
        for(int i = 0; i<length(j.inventaire); i++){
            if(i != idxObj && i>idxObj){
                newInv[i-1] = j.inventaire[i];
            }else if(i != idxObj){
                newInv[i] = j.inventaire[i];
            }
        }
        j.inventaire = newInv;
    }

    void utilisationInventaire(Joueur[] joueurs, int idxUtilisateur,Case[][] plateau){
        println(inventaireToString(joueurs[idxUtilisateur].inventaire));
        if(length(joueurs[idxUtilisateur].inventaire) > 0 && controleValidation("Voulez-vous utiliser un objet ? (o/n)") ){
            clearScreen();
            println(inventaireToString(joueurs[idxUtilisateur].inventaire));
            int temp = controleSaisi(1, length(joueurs[idxUtilisateur].inventaire),"Quel objet voulez-vous utiliser ?")-1;
            joueurs[idxUtilisateur].inventaire[temp].actif = true;
            gestionInventaire(joueurs, idxUtilisateur, plateau);
        }
    }

    void gestionInventaire(Joueur[] joueurs, int idxJ,Case[][] plateau){
        for(int i = 0; i < length(joueurs[idxJ].inventaire); i++){
            if(joueurs[idxJ].inventaire[i].actif){
                utilisationEffet(joueurs, idxJ, i, plateau);
                suprObjet(joueurs[idxJ],i);
            }
        }
    }

    void utilisationEffet(Joueur[] joueurs, int idxJ, int indxObj, Case[][] plateau){
        if(joueurs[idxJ].inventaire[indxObj].effet == Effet.VOLER){
            volePoint(joueurs, joueurs[idxJ].inventaire[indxObj].effet_value, idxJ);
            clearScreen();
        }else if(joueurs[idxJ].inventaire[indxObj].effet == Effet.TELEPORTER){
            if(joueurs[idxJ].inventaire[indxObj].effet_value == 0){
                teleportJoueur(joueurs, plateau);
                clearScreen();
            }else{
                teleportStar();
                clearScreen();
            }
        }
    }
    
    void volePoint(Joueur[] joueurs, int qt, int idxUser){
        String res = "";
        int idx = 1;
        for(int i = 0; i < length(joueurs); i++){
            if((i)!=idxUser){
                res += idx+") "+joueurs[i].nom+"\n";
                idx++;
            }
        }
        println(res);
        int temp = controleSaisi(1, length(joueurs),"Quel joueur voulez-vous voler ?");
        if(temp >= idxUser){
            temp ++;
        }
        joueurs[temp-1].points-=qt;
        joueurs[idxUser].points+=qt;
    }
    
    void teleportJoueur(Joueur[] joueurs, Case[][] plateau){
        int[] randomCoo = donneCooRandom(length(plateau,1),length(plateau,2));
        Case randomCase=plateau[randomCoo[0]][randomCoo[1]];
        while(!randomCase.accessible && equals(randomCase.contenu,"")){
            randomCoo = donneCooRandom(length(plateau,1),length(plateau,2));
            randomCase = plateau[randomCoo[0]][randomCoo[1]];
        }
        String res = "";
        for(int i = 1; i < length(joueurs)+1; i++){
            res += i+") "+joueurs[i-1].nom+"\n";
        }
        println(res);
        int temp = controleSaisi(1, length(joueurs),"Quel joueur voulez-téléporter ?");
        joueurs[temp-1].position = randomCoo;
    }

    void teleportStar(){

    }

    boolean verifieMagasin(Case c){
        return true;
    }

    void achatMagasin(Joueur j, int idxObj){
        if(j.points>=listeDesObjets[idxObj].prix){
            ajoutObjet(j,listeDesObjets[idxObj]);
            j.points = j.points - listeDesObjets[idxObj].prix;
        }else{
            println("Tu n'as pas assez de Points");
        }
        
    }

    void gestionMagasin(Joueur j){
        clearScreen();
        println("Magasin \n\nTu as " + j.points + " Points \n");
        String res = "";
        for(int i = 1; i < length(listeDesObjets)+1; i++){
            res += i+") "+ listeDesObjets[i-1].icon +" "+listeDesObjets[i-1].nom+ " "+listeDesObjets[i-1].prix +" Points \n";
        }
        println(res);
        if(controleValidation("Voulez-vous acheter quelque-chose ? (o/n)")){
            clearScreen();
            println("Magasin \n\nTu as " + j.points + " Points \n");
            println(res);
            int temp = controleSaisi(1, length(listeDesObjets),"Quel objet voulez vous acheter ?");
            achatMagasin(j,temp-1);
        }
        println(res);
    }
    
    void algorithm(){
        clearScreen();
        Joueur[] joueurs = creationJoueurs();
        Case[][] plateau = initialiserPlateau();
        int idxTourJoueur = 0;
        int[] cooStar = donneIdxStar(plateau); 
        clearScreen();
        int nbTours = nbToursPartie();
        while(nbTours > 0){
            String plateauAffichable = plateauToString(plateau,joueurs);
            controleSaisi(joueurs,idxTourJoueur,plateau,plateauAffichable);
            clearScreen();
            affichageStatPartie(joueurs,idxTourJoueur);
            println(plateauToString(plateau,joueurs));
            poseQuestion(joueurs[idxTourJoueur],"ressources/questions.csv");
            delay(2000);
            clearScreen();
            if(joueurSurStar(cooStar,joueurs[idxTourJoueur].position)){
                verifStar(joueurs[idxTourJoueur],plateau,cooStar);
                cooStar = donneIdxStar(plateau);
            }  
            idxTourJoueur++;
            if(idxTourJoueur>length(joueurs)-1){
                idxTourJoueur=0;
            }else if(verifieMagasin(plateau[joueurs[idxTourJoueur].position[0]][joueurs[idxTourJoueur].position[1]])){
                gestionMagasin(joueurs[idxTourJoueur]);
            }
            nbTours--;
        }
        println(maxStar(joueurs) + " a Gagné !");
    }
}