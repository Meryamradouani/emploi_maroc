import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Rekrute {

    public static class DatabaseConnection {
        public static Connection connect() {
            String url = "jdbc:mysql://localhost:3306/emploi_maroc";
            String username = "root";
            String password = ""; // Votre mot de passe MySQL ici

            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connexion réussie !");
                return connection;
            } catch (SQLException e) {
                System.out.println("Erreur de connexion : " + e.getMessage());
                return null;
            }
        }
    }

    static List<String> hardSkillsList = Arrays.asList(
            "Java", "Python", "C++", "JavaScript", "C#", "Ruby", "Swift", "PHP", "HTML/CSS", "React", "Angular", "Vue.js",
            "Node.js", "Express.js", "RESTful API", "SQL", "MySQL", "PostgreSQL", "Oracle", "NoSQL", "MongoDB", "Cassandra",
            "Linux", "Unix", "Windows Server", "Docker", "Kubernetes", "TensorFlow", "PyTorch", "Machine learning",
            "Data Science", "Blockchain", "CI/CD", "Git", "Spring", "Jira", "Trello"
    );

    static List<String> languesList = Arrays.asList("français", "anglais", "espagnol", "allemand", "italien", "portugais", "néerlandais", "russe", "chinois", "japonais");

    public void scrapRekrute() {
        try {
            int k = 1;

            for (int i = 0; i < 2; i++) { // Limitez à 2 pages pour le test
                Document doc = Jsoup.connect("https://www.rekrute.com/offres.html?s=3&p=" + i + "&o=1").get();
                Elements newsHeadlines = doc.select(".post-id");

                for (Element docuElement : newsHeadlines) {
                    if (k == 244 || k == 391) {
                        k += 1;
                        break;
                    }

                    // Extraire les données
                    String postNameText = "";
                    Element titreJobElement = docuElement.selectFirst(".titreJob");
                    if (titreJobElement != null) {
                        postNameText = titreJobElement.text();
                    }

                    String dateDePublicationText = docuElement.selectFirst(".date span").text();
                    String lienHref = "https://www.rekrute.com" + docuElement.selectFirst(".section h2 a").attr("href");
                    Document emploi = Jsoup.connect(lienHref).get();
                    String typeDeContratText = emploi.selectFirst("span[title=Type de contrat]").text();
                    String experienceText = emploi.selectFirst("li[title=Expérience requise]").text();
                    String regionText = emploi.selectFirst("li[title=Région]").text();
                    String niveauText = emploi.selectFirst("li[title=\"Niveau d'étude et formation\"]").text();
                    String competenceText = emploi.selectFirst(".tagSkills").text();
                    Element entreprise = emploi.selectFirst("#recruiterDescription p");
                    String entrepriseText = (entreprise != null) ? entreprise.text() : "";
                    String secteurText = emploi.selectFirst(".h2italic").text();
                    String datePostMaxText = emploi.selectFirst(".newjob b").text();
                    Element descriptionElement = emploi.selectFirst("#recruiterDescription p");
                    String descriptionText = (descriptionElement != null) ? descriptionElement.text() : "";
                    Element profilRechercheElement = emploi.selectFirst("div:has(h2:contains(Profil recherché))");
                    String profilRechercheText = (profilRechercheElement != null) ? profilRechercheElement.text() : "";

                    // Détecter langues et hard skills
                    String languesDetected = detectLangues(descriptionText + " " + profilRechercheText);
                    String hardSkillsDetected = detectHardSkills(descriptionText + " " + profilRechercheText);

                    // Afficher les résultats
                    System.out.println("Post Name: " + postNameText);
                    System.out.println("Date de publication: " + dateDePublicationText);
                    System.out.println("Lien: " + lienHref);
                    System.out.println("Type de contrat: " + typeDeContratText);
                    System.out.println("Expérience: " + experienceText);
                    System.out.println("Région: " + regionText);
                    System.out.println("Niveau d'étude: " + niveauText);
                    System.out.println("Compétences: " + competenceText);
                    System.out.println("Entreprise: " + entrepriseText);
                    System.out.println("Secteur: " + secteurText);
                    System.out.println("Date post max: " + datePostMaxText);
                    System.out.println("Langues détectées: " + languesDetected);
                    System.out.println("Hard Skills détectés: " + hardSkillsDetected);
                    System.out.println("Description: " + descriptionText);
                    System.out.println("Profil recherché: " + profilRechercheText);
                    System.out.println("=========================================");

                    // Convertir les dates au format correct
                    String formattedDate = convertDateFormat(dateDePublicationText);
                    String formattedMaxPostDate = convertDateFormat(datePostMaxText); // Conversion pour max_post_date
                    if (formattedDate != null && formattedMaxPostDate != null) {
                        // Insertion dans la base de données
                        insertJobToDatabase(postNameText, descriptionText, formattedDate, typeDeContratText,
                                experienceText, regionText, niveauText, lienHref, hardSkillsDetected, languesDetected,
                                entrepriseText, secteurText, formattedMaxPostDate);
                    }
                    k++;
                }
                System.out.println("Page " + (i + 1) + " collectée !");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du scraping : " + e.getMessage());
        }
    }

    // Méthode pour détecter les langues dans un texte
    private static String detectLangues(String text) {
        List<String> detectedLangues = new ArrayList<>();
        for (String langue : languesList) {
            if (text.toLowerCase().contains(langue.toLowerCase())) {
                detectedLangues.add(langue);
            }
        }
        return String.join(", ", detectedLangues); // Retourne une liste séparée par des virgules
    }

    // Méthode pour détecter les hard skills dans un texte
    private static String detectHardSkills(String text) {
        List<String> detectedHardSkills = new ArrayList<>();
        for (String skill : hardSkillsList) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                boolean add = detectedHardSkills.add(skill);
            }
        }
        return String.join(", ", detectedHardSkills); // Retourne une liste séparée par des virgules
    }

    // Méthode pour convertir la date au format 'yyyy-MM-dd'
    private static String convertDateFormat(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            System.out.println("Erreur de conversion de la date : " + e.getMessage());
            return null;
        }
    }

    // Méthode pour insérer les données dans la base de données
    private static void insertJobToDatabase(String title, String description, String datePosted, String contractType,
                                            String experienceRequired, String region, String educationLevel, String lien,
                                            String hardSkills, String languages, String companyName, String sector, String maxPostDate) {
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn != null) {
                String query = "INSERT INTO rekrute_jobs (title, description, date_posted, contract_type, experience_required, region, " +
                        "education_level, lien, hard_skills, languages, company_name, sector, max_post_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, title);
                    stmt.setString(2, description);
                    stmt.setString(3, datePosted); // La date convertie
                    stmt.setString(4, contractType);
                    stmt.setString(5, experienceRequired);
                    stmt.setString(6, region);
                    stmt.setString(7, educationLevel);
                    stmt.setString(8, lien);
                    stmt.setString(9, hardSkills);
                    stmt.setString(10, languages);
                    stmt.setString(11, companyName);
                    stmt.setString(12, sector);
                    stmt.setString(13, maxPostDate); // La date convertie pour max_post_date

                    stmt.executeUpdate();
                    System.out.println("Job inséré avec succès dans la base de données !");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion dans la base de données : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Rekrute rekrute = new Rekrute();
        rekrute.scrapRekrute();
    }
}
