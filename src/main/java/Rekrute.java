import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rekrute {

    public class DatabaseConnection {

        // Paramètres de connexion
        private static final String URL = "jdbc:mysql://localhost:3306/emploi"; // Remplacez 'emploi' par le nom de votre base
        private static final String USER = "root"; // Votre nom d'utilisateur
        private static final String PASSWORD = ""; // Votre mot de passe, laissez vide si aucun

        // Méthode pour établir la connexion
        public static Connection connect() {
            Connection conn = null;
            try {
                // Charger le pilote JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Établir la connexion
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données réussie.");

            } catch (ClassNotFoundException e) {
                System.out.println("Pilote JDBC introuvable !");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Erreur de connexion à la base de données.");
                e.printStackTrace();
            }
            return conn;
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

            for (int i = 0; i < 30; i++) { // Limitez à 2 pages pour le test
                Document doc = Jsoup.connect("https://www.rekrute.com/offres.html?s=3&p=" + i + "&o=1").get();
                Elements newsHeadlines = doc.select(".post-id");

                for (Element docuElement : newsHeadlines) {
                    if (k == 244 || k == 391) {
                        k += 1;
                        break;
                    }

                    // Extraire les données
                    String postNameText = "Non spécifié";
                    Element titreJobElement = docuElement.selectFirst(".titreJob");
                    if (titreJobElement != null) {
                        postNameText = titreJobElement.text();
                    }

                    Element lienElement = docuElement.selectFirst(".section h2 a");
                    String lien = (lienElement != null) ? "https://www.rekrute.com" + lienElement.attr("href") : "";
                    Document emploi = null;

                    try {
                        if (!lien.isEmpty()) {
                            emploi = Jsoup.connect(lien).get();
                        }
                    } catch (IOException e) {
                        System.out.println("Impossible de connecter à l'URL : " + lien);
                    }

                    // Informations sur l'offre d'emploi
                    String siteWebEntreprise = "Non spécifié";
                    if (emploi != null) {
                        Element companyWebsiteElement = emploi.selectFirst(".company-website");
                        if (companyWebsiteElement != null) {
                            siteWebEntreprise = companyWebsiteElement.text();
                        }
                    }

                    String descriptionPoste = "Non spécifié";
                    if (emploi != null) {
                        Element descriptionPosteElement = emploi.selectFirst(".job-description");
                        if (descriptionPosteElement != null) {
                            descriptionPoste = descriptionPosteElement.text();
                        }
                    }

                    String adresseEntreprise = "Non spécifié";
                    if (emploi != null) {
                        Element adresseElement = emploi.selectFirst(".company-address");
                        if (adresseElement != null) {
                            adresseEntreprise = adresseElement.text();
                        }
                    }

                    Element dateElement = docuElement.selectFirst(".date span");
                    String dateDePublicationText = (dateElement != null) ? dateElement.text() : "Non spécifié";

                    String typeDeContratText = "Non spécifié";
                    if (emploi != null) {
                        Element typeDeContratElement = emploi.selectFirst("span[title=Type de contrat]");
                        if (typeDeContratElement != null) {
                            typeDeContratText = typeDeContratElement.text();
                        }
                    }

                    String experienceText = "Non spécifié";
                    if (emploi != null) {
                        Element experienceElement = emploi.selectFirst("li[title=Expérience requise]");
                        if (experienceElement != null) {
                            experienceText = experienceElement.text();
                        }
                    }

                    String regionText = "Non spécifié";
                    if (emploi != null) {
                        Element regionElement = emploi.selectFirst("li[title=Région]");
                        if (regionElement != null) {
                            regionText = regionElement.text();
                        }
                    }

                    String ville = "Non spécifié";
                    if (emploi != null) {
                        Element villeElement = emploi.selectFirst("li[title=Ville]");
                        if (villeElement != null) {
                            ville = villeElement.text();
                        }
                    }

                    String niveauText = "Non spécifié";
                    if (emploi != null) {
                        Element niveauElement = emploi.selectFirst("li[title=\"Niveau d'étude et formation\"]");
                        if (niveauElement != null) {
                            niveauText = niveauElement.text();
                        }
                    }

                    String competenceText = "Non spécifié";
                    if (emploi != null) {
                        Element competenceElement = emploi.selectFirst(".tagSkills");
                        if (competenceElement != null) {
                            competenceText = competenceElement.text();
                        }
                    }

                    Element entreprise = emploi != null ? emploi.selectFirst("#recruiterDescription strong") : null;
                    String entrepriseText = entreprise != null ? entreprise.text() : "Non spécifié";

                    String secteurText = "Non spécifié";
                    if (emploi != null) {
                        Element secteurElement = emploi.selectFirst(".h2italic");
                        if (secteurElement != null) {
                            secteurText = secteurElement.text();
                        }
                    }

                    String datePostMaxText = "Non spécifié";
                    if (emploi != null) {
                        Element datePostMaxElement = emploi.selectFirst(".newjob b");
                        if (datePostMaxElement != null) {
                            datePostMaxText = datePostMaxElement.text();
                        }
                    }

                    Element descriptionElement = emploi != null ? emploi.selectFirst("#recruiterDescription p") : null;
                    String descriptionText = descriptionElement != null ? descriptionElement.text() : "Non spécifié";

                    Element profilRechercheElement = emploi != null ? emploi.selectFirst("div:has(h2:contains(Profil recherché))") : null;
                    String profilRechercheText = profilRechercheElement != null ? profilRechercheElement.text() : "Non spécifié";

                    String metier = "Non spécifié";
                    String specialiteDiplome = "Non spécifiée";

                    Element traitsPersonnalite = emploi != null ? emploi.select("h2:contains(Traits de personnalité souhaités) + p").first() : null;
                    String traitsPersonnaliteText = traitsPersonnalite != null ? traitsPersonnalite.text() : "Non spécifié";

                    Element salaireElement = emploi != null ? emploi.selectFirst("li[title=salaire]") : null;
                    String salaire = salaireElement != null ? salaireElement.text() : "Non spécifié";

                    String languesDetected = detectLangues((descriptionText + " " + profilRechercheText));
                    String hardSkillsDetected = detectHardSkills((descriptionText + " " + profilRechercheText));
                    String teletravail = "Non spécifié";
                    if (emploi != null) {
                        Element teletravailElement = emploi.selectFirst("li[title=Télétravail] .tagContrat");
                        if (teletravailElement != null) {
                            teletravail = teletravailElement.text();
                        }
                    }

                    String softSkills = "Non spécifié";
                    String niveauLangue = "Non spécifié";
                    String avantagesSociaux = "Non spécifié";
                    String url1="https://www.rekrute.com";
                    String datePublication = "";
                    String lienHref="Rekrute";

                    // Afficher les résultats
                    System.out.println("Titre : " +  postNameText);
                    System.out.println("URL : " + url1);
                    System.out.println("Date de publication : " + dateDePublicationText);
                    System.out.println("Date pour postuler : " + datePostMaxText);
                    System.out.println("Adresse entreprise : " + adresseEntreprise);
                    System.out.println("Site web entreprise : " + lienHref);
                    System.out.println("Nom entreprise : " + entrepriseText);
                    System.out.println("Description entreprise : " + descriptionText );
                    System.out.println("Description du poste : " + descriptionPoste);
                    System.out.println("Région : " + regionText);
                    System.out.println("Ville : " + ville);
                    System.out.println("Secteur d'activité : " + secteurText);
                    System.out.println("Métier : " + metier);
                    System.out.println("Type du contrat : " + typeDeContratText );
                    System.out.println("Niveau d'études : " + niveauText);
                    System.out.println("Spécialité / Diplôme : " + specialiteDiplome);
                    System.out.println("Expérience : " + experienceText);
                    System.out.println("Profil recherché : " + profilRechercheText);
                    System.out.println("Traits de personnalité : " + traitsPersonnaliteText);
                    System.out.println("Compétences requises (hard skills) : " + hardSkillsDetected);
                    System.out.println("Soft Skills : " + softSkills);
                    System.out.println("Compétences recommandées : " + competenceText);
                    System.out.println("Langue : " + languesDetected);
                    System.out.println("Niveau de la langue : " + niveauLangue);
                    System.out.println("Salaire : " + salaire);
                    System.out.println("Avantages sociaux : " + avantagesSociaux);
                    System.out.println("Télétravail : " + teletravail);
                    System.out.println("=========================================");
                    // Insertion dans la base de données
                    insertJobToDatabase(postNameText, url1, lienHref, datePublication, datePostMaxText, adresseEntreprise,
                            siteWebEntreprise, entrepriseText, descriptionText, descriptionPoste, regionText, ville, secteurText, metier,
                            typeDeContratText, niveauText, specialiteDiplome, experienceText, profilRechercheText, traitsPersonnaliteText,
                            hardSkillsDetected, softSkills, competenceText, languesDetected, niveauLangue, salaire, avantagesSociaux, teletravail);


                    k++;
                }
                System.out.println("Page " + (i + 1) + " collectée !");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du scraping : " + e.getMessage());
        }
    }

    private void insertJobToDatabase(String postNameText, String url1, String lienHref, String datePublication, String datePostMaxText, String adresseEntreprise, String siteWebEntreprise, String entrepriseText, String descriptionText, String descriptionPoste, String regionText, String ville, String secteurText, String metier, String typeDeContratText, String niveauText, String specialiteDiplome, String experienceText, String profilRechercheText, Element traitsPersonnalite, String hardSkillsDetected, String softSkills, String languesDetected, String niveauLangue, String salaire, String avantagesSociaux, String teletravail) {
        // Insérez ici le code pour ajouter les informations à la base de données
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
                detectedHardSkills.add(skill);
            }
        }
        return String.join(", ", detectedHardSkills); // Retourne une liste séparée par des virgules
    }

    // Méthode pour convertir la date au format 'yyyy-MM-dd'

    // Méthode pour insérer les données dans la base de données
    private void insertJobToDatabase(String titre, String url, String siteName, String datePublication,
                                     String datePostuler, String adresseEntreprise, String siteWebEntreprise,
                                     String nomEntreprise, String descriptionEntreprise, String descriptionPoste,
                                     String region, String ville, String secteurActivite, String metier,
                                     String typeContrat, String niveauEtudes, String specialiteDiplome,
                                     String experience, String profilRecherche, String traitsPersonnalite,
                                     String hardSkills, String softSkills, String competencesRecommandees,
                                     String langue, String niveauLangue, String salaire, String avantagesSociaux,
                                     String teletravail) {

        String query = "INSERT INTO offres_emploi (titre, url, site_name, date_publication, date_postuler, " +
                "adresse_entreprise, site_web_entreprise, nom_entreprise, description_entreprise, " +
                "description_poste, region, ville, secteur_activite, metier, type_contrat, niveau_etudes, " +
                "specialite_diplome, experience, profil_recherche, traits_personnalite, hard_skills, " +
                "soft_skills, competences_recommandees, langue, niveau_langue, salaire, avantages_sociaux, " +
                "teletravail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, titre);
            pstmt.setString(2, url);
            pstmt.setString(3, siteName);
            pstmt.setString(4, datePublication);
            pstmt.setString(5, datePostuler);
            pstmt.setString(6, adresseEntreprise);
            pstmt.setString(7, siteWebEntreprise);
            pstmt.setString(8, nomEntreprise);
            pstmt.setString(9, descriptionEntreprise);
            pstmt.setString(10, descriptionPoste);
            pstmt.setString(11, region);
            pstmt.setString(12, ville);
            pstmt.setString(13, secteurActivite);
            pstmt.setString(14, metier);
            pstmt.setString(15, typeContrat);
            pstmt.setString(16, niveauEtudes);
            pstmt.setString(17, specialiteDiplome);
            pstmt.setString(18, experience);
            pstmt.setString(19, profilRecherche);
            pstmt.setString(20, traitsPersonnalite);
            pstmt.setString(21, hardSkills);
            pstmt.setString(22, softSkills);
            pstmt.setString(23, competencesRecommandees);
            pstmt.setString(24, langue);
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
        Rekrute rekrute = new Rekrute();
        rekrute.scrapRekrute();
    }
}