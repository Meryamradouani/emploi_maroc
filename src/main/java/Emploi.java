import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Emploi {

    // Configuration de la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emploi_maroc"; // Remplacez avec votre URL
    private static final String DB_USER = "root"; // Remplacez avec votre utilisateur MySQL
    private static final String DB_PASSWORD = ""; // Remplacez avec votre mot de passe MySQL

    public void scraperOffres() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connexion à la base de données réussie.");

            for (int i = 0; i < 5; i++) { // Pagination
                String url = "https://www.emploi.ma/recherche-jobs-maroc/?f%5B0%5D=im_field_offre_metiers%3A31&o=" + (i + 1);
                Document doc = Jsoup.connect(url).get();

                // Sélectionner toutes les offres
                Elements offres = doc.select(".card.card-job.featured");

                if (offres.isEmpty()) {
                    System.out.println("Aucune offre trouvée à la page " + (i + 1));
                    continue;
                }

                for (Element offre : offres) {
                    try {
                        // Récupération des données
                        Element titreElement = offre.selectFirst(".card-job-detail h3 a");
                        String titre = (titreElement != null) ? titreElement.text() : "Titre non spécifié";
                        String relativeUrl = (titreElement != null) ? titreElement.attr("href") : "";
                        String fullUrl = "https://www.emploi.ma" + relativeUrl;

                        Document detailDoc = Jsoup.connect(fullUrl).get();

                        Element descriptionElement = detailDoc.selectFirst(".truncated p");
                        String description = (descriptionElement != null) ? descriptionElement.text() : "Description non spécifiée";

                        Element dateElement = detailDoc.selectFirst(".page-application-details p");
                        String dateposte = (dateElement != null) ? dateElement.text() : "Date non spécifiée";

                        Elements entrepriseElement = detailDoc.select(".card-block-company h3 a");
                        String companyName = (entrepriseElement != null) ? entrepriseElement.text() : "Nom non spécifié";

                        Element skillsElement = detailDoc.selectFirst("ul.skills");
                        String hardSkills = (skillsElement != null) ? skillsElement.text() : "Non spécifié";

                        Element experienceElement = detailDoc.selectFirst("li:contains(Niveau d\\'expérience) span");
                        String experienceRequired = (experienceElement != null) ? experienceElement.text() : "Non spécifié";

                        Element niveauElement = detailDoc.selectFirst("li:contains(Niveau d\\'études) span");
                        String educationLevel = (niveauElement != null) ? niveauElement.text() : "Non spécifié";

                        Element secteurElement = detailDoc.selectFirst("li:contains(Secteur d\\´activité) span");
                        String sector = (secteurElement != null) ? secteurElement.text() : "Non spécifié";

                        Element contratElement = detailDoc.selectFirst("li:contains(Type de contrat) span");
                        String contractType = (contratElement != null) ? contratElement.text() : "Non spécifié";

                        Element regionElement = detailDoc.selectFirst("li:contains(Région) span");
                        String region = (regionElement != null) ? regionElement.text() : "Non spécifié";

                        Element languesElement = detailDoc.selectFirst("li:contains(Langues exigées) span");
                        String languages = (languesElement != null) ? languesElement.text() : "Non spécifié";

                        // Préparation de l'insertion dans la base sans unique_key ni source_id
                        String insertQuery = "INSERT INTO rekrute_jobs (title, description, date_posted, contract_type, experience_required, region, education_level, hard_skills, languages, company_name, sector, lien) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                            pstmt.setString(1, titre);
                            pstmt.setString(2, description);
                            pstmt.setString(3, dateposte);
                            pstmt.setString(4, contractType);
                            pstmt.setString(5, experienceRequired);
                            pstmt.setString(6, region);
                            pstmt.setString(7, educationLevel);
                            pstmt.setString(8, hardSkills);
                            pstmt.setString(9, languages);
                            pstmt.setString(10, companyName);
                            pstmt.setString(11, sector);
                            pstmt.setString(12, fullUrl);

                            pstmt.executeUpdate();
                            System.out.println("Offre insérée : " + titre);
                        }
                    } catch (Exception e) {
                        System.out.println("Erreur lors du traitement d'une offre : " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion ou du scraping : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Emploi scraper = new Emploi();
        scraper.scraperOffres();
    }
}
