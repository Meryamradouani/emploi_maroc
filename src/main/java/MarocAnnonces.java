import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarocAnnonces {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emploi"; // Remplacez avec votre URL
    private static final String DB_USER = "root"; // Remplacez avec votre utilisateur MySQL
    private static final String DB_PASSWORD = ""; // Remplacez avec votre mot de passe MySQL

    public List<Map<String, Object>> fetchJobOffers() {
        Connection conn = connectToDatabase(DB_URL, DB_USER, DB_PASSWORD);
        if (conn != null) {
            System.out.println("Connexion réussie");
        } else {
            System.out.println("Échec de la connexion");
        }

        List<Map<String, Object>> jobOffers = new ArrayList<>();
        try {
            for (int i = 1; i <= 8; i++) {
                String urli = "https://www.marocannonces.com/categorie/309/Emploi/Offres-emploi/" + i + ".html";
                Document document = Jsoup.connect(urli).get();
                Elements hrefs = document.select("#main #twocolumns #content .used-cars .content_box a");

                for (Element href : hrefs) {
                    String link = href.attr("href");
                    Document jobDoc = Jsoup.connect("https://www.marocannonces.com/" + link).get();
                    Element descriptionElement = jobDoc.select("#main #twocolumns #content .description").first();

                    if (descriptionElement != null) {
                        Map<String, Object> jobDetails = new HashMap<>();
                        Element titleElement = descriptionElement.getElementsByTag("h1").first();
                        String jobTitle = Objects.requireNonNull(titleElement).text();

                        Element locElement = descriptionElement.select(".info-holder li").get(0);
                        String location = locElement.text();

                        Element publicationElement = descriptionElement.select(".info-holder li").get(1);
                        String publicationTime = publicationElement.text()
                                .replaceAll("Publiée le:", "")
                                .replaceAll("\\s+", "")
                                .replace("-", " ")
                                .replaceAll("(?<=\\d)([A-Za-z])", " $1");

                        int currentYear = Year.now().getValue();
                        publicationTime = currentYear + " " + publicationTime;

                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy d MMM HH:mm", Locale.ENGLISH);
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        String formattedDate = "NA";
                        try {
                            LocalDateTime dateTime = LocalDateTime.parse(publicationTime, inputFormatter);
                            formattedDate = dateTime.format(outputFormatter);
                        } catch (DateTimeParseException e) {
                            System.out.println("Error parsing date: " + e.getMessage());
                        }

                        Element infoElement = descriptionElement.select(".block ").first();
                        String jobInfo = Objects.requireNonNull(infoElement).text();

                        Elements all = descriptionElement.select("#extraQuestionName li");
                        List<String> termsToCheck = Arrays.asList("Domaine", "Fonction", "Contrat", "Entreprise", "Sa", "Niveau d'étude");

                        String domaineName = "NA", fonctionName = "NA", contractName = "NA", entrepriseName = "NA", salaireName = "NA", nivName = "NA";

                        for (int j = 0; j < all.size(); j++) {
                            String elementText = all.get(j).text();
                            if (elementText.contains(termsToCheck.get(j))) {
                                Element h = all.get(j).select("a").first();
                                String value = h != null ? h.text() : "NA";

                                switch (j) {
                                    case 0 -> domaineName = value;
                                    case 1 -> fonctionName = value;
                                    case 2 -> contractName = value;
                                    case 3 -> entrepriseName = value;
                                    case 4 -> salaireName = value.contains("discuter") ? "NA" : value;
                                    case 5 -> nivName = value;
                                }
                            }
                        }
                        String fullUrl = "https://www.marocannonces.com/" + link;
                        String siteName = "MarocAnnonces";

                        jobDetails.put("Job Title", jobTitle);
                        jobDetails.put("Published Date", formattedDate);
                        jobDetails.put("Location", location);
                        jobDetails.put("Domaines", domaineName);
                        jobDetails.put("Fonctions", fonctionName);
                        jobDetails.put("Contract", contractName);
                        jobDetails.put("Entreprise", entrepriseName);
                        jobDetails.put("Salaire", salaireName);
                        jobDetails.put("Niveau d'étude", nivName);
                        jobDetails.put("More Info", jobInfo);
                        jobDetails.put("URL", fullUrl);

                        jobOffers.add(jobDetails);
                        insertJobToDatabase(conn, jobTitle, fullUrl, siteName, formattedDate, location, fullUrl,
                                entrepriseName, "Description", "Poste", location, location, domaineName, fonctionName,
                                contractName, nivName, "Specialite", "Experience", "Profil", "Traits", "Hard Skills",
                                "Soft Skills", "Competences", "Languages", "Langue Niveau", salaireName, "Avantages", "Teletravail");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion ou de traitement : " + e.getMessage());
        }
        return jobOffers;
    }

    private static Connection connectToDatabase(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
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
        MarocAnnonces scraper = new MarocAnnonces();
        scraper.fetchJobOffers();
        System.out.println("Récupération des offres d'emploi terminée.");
    }
}
