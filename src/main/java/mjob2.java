import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class mjob2 {

    public static void main(String[] args) {
        String url = "https://jobs-ma.com/view.php?job_id=962167";
        try {
            // Connexion au site
            Document document = Jsoup.connect(url).get();

            // Extraction des données avec gestion des erreurs
            String titre = document.selectFirst("h3") != null ? document.selectFirst("h3").text() : "Titre indisponible";

            // Tentative de reformulation du sélecteur pour Description
            Element descriptionElement = document.selectFirst("h4:containsOwn(Description de l\\'offre) + p");
            String descriptionText = descriptionElement != null ? descriptionElement.text() : "Description indisponible";

            Element profilElement = document.selectFirst("h4:contains(Profil / Compétences:) + p");
            String profil = profilElement != null ? profilElement.text() : "Profil indisponible";

            String secteur = document.selectFirst("td:contains(Secteur:) + td") != null ?
                    document.selectFirst("td:contains(Secteur:) + td").text() : "Secteur indisponible";

            String salaire = document.selectFirst("td:contains(Salaire:) + td") != null ?
                    document.selectFirst("td:contains(Salaire:) + td").text() : "Salaire indisponible";

            String typeContrat = document.selectFirst("td:contains(Type de contrat:) + td") != null ?
                    document.selectFirst("td:contains(Type de contrat:) + td").text() : "Type de contrat indisponible";
            String niveauetude = document.selectFirst("td:contains(Niveau d\\'études:) + td") != null ?
                    document.selectFirst("td:contains(Niveau d\\'études:) + td").text() : "niveau indisponible";
            String experinece = document.selectFirst("td:contains(Expérience (nb. années):) + td") != null ?
                    document.selectFirst("td:contains(Expérience (nb. années):) + td").text() : "Type de contrat indisponible";
            String lieu = document.selectFirst("td:contains(Lieu du poste:) + td") != null ?
                    document.selectFirst("td:contains(Lieu du poste:) + td").text() : "lieu indisponible";

            String adresse = document.selectFirst("td:contains(Adresse:) + td") != null ?
                    document.selectFirst("td:contains(Adresse:) + td").text() : "Adresse indisponible";

            String entreprise = document.selectFirst("td:contains(Société:) + td") != null ?
                    document.selectFirst("td:contains(Société:) + td").text() : "Entreprise indisponible";

            String email = document.selectFirst("td:contains(E-mail du contact:) a") != null ?
                    document.selectFirst("td:contains(E-mail du contact:) a").text() : "Email indisponible";
            String Datepublication = document.selectFirst("td:contains(Date publication:) td") != null ?
                    document.selectFirst("td:contains(E-mail du contact:) a").text() : "date indisponible";
            String companyname = document.selectFirst("td:contains(Société:) td") != null ?
                    document.selectFirst("td:contains(E-mail du contact:) a").text() : "langue indisponible";
            String langue = document.selectFirst("td:contains(Des exigences linguistiques:) td li") != null ?
                    document.selectFirst("td:contains(Des exigences linguistiques:) a").text() : "langue indisponible";
            // Affichage des données pour vérification
            System.out.println("Titre : " + titre);
            System.out.println("Description : " + descriptionText);
            System.out.println("Profil : " + profil);
            System.out.println("Secteur : " + secteur);
            System.out.println("Salaire : " + salaire);
            System.out.println("Type de contrat : " + typeContrat);
            System.out.println("Adresse : " + adresse);
            System.out.println("Entreprise : " + entreprise);
            System.out.println("Email : " + email);
            System.out.println("niveau etude  : " + niveauetude);
            System.out.println("experience : " + experinece);
            System.out.println("lieu : " + lieu);
            System.out.println("date de publication  : " + Datepublication);
            System.out.println("langue : " + langue);




        } catch (Exception e) {
            System.out.println("Une erreur s'est produite : " + e.getMessage());
            e.printStackTrace();
        }
    }
}