import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;

public class anapec {
    private static int globalCounter = 1;

    public static void main(String[] args) {
        int totalPages = 30; // You need to determine the total number of pages

        for (int page = 1; page <= totalPages; page++) {
            String searchUrl = "http://anapec.org/sigec-app-rv/chercheurs/resultat_recherche/page:" + page + "/tout:all/language:fr";

            try {
                Document document = Jsoup.connect(searchUrl).timeout(10000 /* temps en millisecondes */).get();


                // Select the table containing job offers
                Element table = document.select("table#myTable").first();

                // Select all rows in the table
                Elements rows = table.select("tbody tr");

                // Iterate over each job posting on the current page
                for (Element row : rows) {
                    // Extract common information
                    String reference = row.select("td:nth-child(2) a").text();
                    String date = row.select("td:nth-child(3)").text();
                    String intitule = row.select("td:nth-child(4)").text();
                    String nombrePostes = row.select("td:nth-child(5)").text();
                    String entreprise = row.select("td:nth-child(6)").text();
                    String lieuTravail = row.select("td:nth-child(7)").text();

                    String jobDetailsUrl = "http://anapec.org" + row.select("td:nth-child(2) a").attr("href");

                    // Call the method to scrape detailed information for the job posting
                    scrapeJobDetails(jobDetailsUrl, reference, date, intitule, nombrePostes, entreprise, lieuTravail);

                    System.out.println("--------------------------------------------");
                    globalCounter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void scrapeJobDetails(String jobDetailsUrl, String reference, String date, String intitule, String nombrePostes, String entreprise, String lieuTravail) {
        try {
            Document document = Jsoup.connect(jobDetailsUrl).get();

            // Extract detailed information
            String descriptionEntreprise = document.select("h1:contains(Description de l\\'entreprise)").next().text();
            String descriptionPoste = document.select("h1:contains(Description de Poste)").next().text();
            String typeContrat = document.select("p:contains(Type de contrat)").select("span").text().replace("Type de contrat :", "").trim();
            String lieuTravailDetail = document.select("p:contains(Lieu de travail)").select("span").text();
            String salaireMensuel = document.select("p:contains(Salaire mensuel)").select("span").text().replace("Salaire mensuel :", "").trim();
            String secteur = document.select("p:contains(Secteur d’activité)").select("span").text().replace("Secteur d’activité : ", "").trim();

            String caracteristiquesPoste = document.select("p:contains(Caractéristiques du poste)").select("p").text().replace("Caractéristiques du poste :", "").trim();
            ;
            String formation = document.select("h1:contains(Formation)").next().text();
            String pratiqueLangue = document.select("p:contains(Langues)").select("span").text();
            String competance = document.select("p:contains(Compétences spécifiques)").select("span").text();
            String sitename = "Anapec";


            // Display the extracted information
            System.out.println("Offre #" + globalCounter);
            System.out.println("Site : " + sitename);
            System.out.println("Référence : " + reference);
            System.out.println("Date : " + date);
            System.out.println("Intitulé : " + intitule);
            System.out.println("Nombre de postes : " + nombrePostes);
            System.out.println("Entreprise : " + entreprise);
            System.out.println("lieu de travail:" + lieuTravail);
            System.out.println("URL de l'offre : " + jobDetailsUrl);
            System.out.println("Description de l'entreprise: \n" + descriptionEntreprise);
            System.out.println("formation: \n" + formation);
            System.out.println(pratiqueLangue);
            System.out.println(secteur);
            System.out.println("competance: \n" + competance);


            System.out.println("Description du poste: \n" + descriptionPoste);
            System.out.println(typeContrat);
            System.out.println(salaireMensuel);
            System.out.println(caracteristiquesPoste);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}