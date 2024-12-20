import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Emploi {

    // Configuration de la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emploi"; // Remplacez avec votre URL
    private static final String DB_USER = "root"; // Remplacez avec votre utilisateur MySQL
    private static final String DB_PASSWORD = ""; // Remplacez avec votre mot de passe MySQL

    public void scraperOffres() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connexion à la base de données réussie.");

            for (int i = 0; i < 30; i++) { // Pagination
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

                        Element descriptionPosteElement = detailDoc.selectFirst(".job-description li");
                        String descriptionPoste = (descriptionPosteElement != null) ? descriptionPosteElement.text() : "Description non spécifiée";

                        Element dateElement = detailDoc.selectFirst(".page-application-details p");
                        String dateposte = (dateElement != null) ? dateElement.text() : "Date non spécifiée";

                        Elements entrepriseElement = detailDoc.select(".card-block-company h3 a");
                        String companyName = (entrepriseElement != null) ? entrepriseElement.text() : "Nom non spécifié";

                        Element skillsElement = detailDoc.selectFirst("ul.skills");
                        String hardSkills = (skillsElement != null) ? skillsElement.text() : "Non spécifié";

                        Element experienceElement = detailDoc.selectFirst("li:contains(Niveau d\\'expérience) span");
                        String experienceRequired = (experienceElement != null) ? experienceElement.text() : "Non spécifié";

                        Element MetierElement = detailDoc.selectFirst("li:contains( Métier) span");
                        String Metier = (MetierElement != null) ? MetierElement.text() : "Non spécifié";

                        Element niveauElement = detailDoc.selectFirst("li:contains(Niveau d\\'études) span");
                        String educationLevel = (niveauElement != null) ? niveauElement.text() : "Non spécifié";

                        Element secteurElement = detailDoc.selectFirst("li:contains(Secteur d\\´activité) span");
                        String sector = (secteurElement != null) ? secteurElement.text() : "Non spécifié";

                        Element contratElement = detailDoc.selectFirst("li:contains(Type de contrat) span");
                        String contractType = (contratElement != null) ? contratElement.text() : "Non spécifié";

                        Element regionElement = detailDoc.selectFirst("li:contains(Région) span");
                        String region = (regionElement != null) ? regionElement.text() : "Non spécifié";

                        Element profilerechercherElement = detailDoc.selectFirst(".job-qualifications li");
                        String profilerechercher = (profilerechercherElement != null) ? profilerechercherElement.text() : "Non spécifié";

                        Element villeElement = detailDoc.selectFirst("li:contains(Ville) span");
                        String ville = (villeElement != null) ? villeElement.text() : "Non spécifié";

                        Element languesElement = detailDoc.selectFirst("li:contains(Langues exigées) span");
                        String languages = (languesElement != null) ? languesElement.text() : "Non spécifié";

                        Element NmbreposteElement = detailDoc.selectFirst("li:contains(Nombre de poste(s)) span");
                        String Nmbreposte = (NmbreposteElement != null) ? NmbreposteElement.text() : "Non spécifié";

                        String siteName = "emploi.ma";
                        String adresseEntreprise = "non spécifie";
                        String specialiteDiplome = "non spécifie";
                        String traitsPersonnalite = "non spécifie";
                        String softSkills = "non spécifie";
                        String competencesRecommandees = descriptionPoste;
                        String niveauLangue = languages;
                        String salaire = "non spécifie";
                        String avantagesSociaux = "non spécifie";
                        String teletravail = "non spécifie";

                        // Insertion dans la base
                        insertJobToDatabase(conn, titre, fullUrl, siteName, dateposte, adresseEntreprise, fullUrl,
                                companyName, description, descriptionPoste, region, ville, sector, Metier,
                                contractType, educationLevel, specialiteDiplome, experienceRequired,
                                profilerechercher, traitsPersonnalite, hardSkills, softSkills,
                                competencesRecommandees, languages, niveauLangue, salaire,
                                avantagesSociaux, teletravail);
                    } catch (Exception e) {
                        System.out.println("Erreur lors du traitement d'une offre : " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion ou de traitement : " + e.getMessage());
        }
    }

    private void insertJobToDatabase(Connection conn, String titre, String url, String siteName, String dateElement,
                                     String adresseEntreprise, String fullUrl, String companyName, String description,
                                     String descriptionPoste, String region, String ville, String sector, String Metier,
                                     String contractType, String educationLevel, String specialiteDiplome,
                                     String experienceRequired, String profilerechercher, String traitsPersonnalite,
                                     String hardSkills, String softSkills, String competencesRecommandees,
                                     String languages, String niveauLangue, String salaire, String avantagesSociaux,
                                     String teletravail) {
        String query = "INSERT INTO offres_emploi (titre, url, site_name, date_publication, date_postuler, " +
                "adresse_entreprise, site_web_entreprise, nom_entreprise, description_entreprise, " +
                "description_poste, region, ville, secteur_activite, metier, type_contrat, niveau_etudes, " +
                "specialite_diplome, experience, profil_recherche, traits_personnalite, hard_skills, " +
                "soft_skills, competences_recommandees, langue, niveau_langue, salaire, avantages_sociaux, " +
                "teletravail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, url);
            pstmt.setString(3, siteName);
            pstmt.setString(4, dateElement);
            pstmt.setString(5, dateElement);
            pstmt.setString(6, adresseEntreprise);
            pstmt.setString(7, fullUrl);
            pstmt.setString(8, companyName);
            pstmt.setString(9, description);
            pstmt.setString(10, descriptionPoste);
            pstmt.setString(11, region);
            pstmt.setString(12, ville);
            pstmt.setString(13, sector);
            pstmt.setString(14, Metier);
            pstmt.setString(15, contractType);
            pstmt.setString(16, educationLevel);
            pstmt.setString(17, specialiteDiplome);
            pstmt.setString(18, experienceRequired);
            pstmt.setString(19, profilerechercher);
            pstmt.setString(20, traitsPersonnalite);
            pstmt.setString(21, hardSkills);
            pstmt.setString(22, softSkills);
            pstmt.setString(23, competencesRecommandees);
            pstmt.setString(24, languages);
            pstmt.setString(25, niveauLangue);
            pstmt.setString(26, salaire);
            pstmt.setString(27, avantagesSociaux);
            pstmt.setString(28, teletravail);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur d'insertion pour le titre : " + titre + ", Message : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        Emploi scraper = new Emploi();
        scraper.scraperOffres();
    }
}
