import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class anapec {
    public static void main(String[] args) {
        Connection conn = connectToDatabase("jdbc:mysql://localhost:3306/emploi", "root", "");
        if (conn != null) {
            System.out.println("Connexion réussie");
        } else {
            System.out.println("Échec de la connexion");
        }


        try {
            String url = "https://anapec.ma/home-page-o1/chercheur-emploi/offres-demploi/";
            int maxPages = 1; // Change cette valeur pour parcourir plus de pages
            int currentPage = 1;

            while (currentPage <= maxPages) {
                Document doc = Jsoup.connect(url + "?pg=" + currentPage).get();

                // Extraction des liens des offres d'emploi
                Elements hrefs = doc.select(".offres-item .offre a");

                for (Element card : hrefs) {
                    String link = card.attr("href");

                    // Connexion à la page de l'offre
                    Document jobDoc = Jsoup.connect(link).get();

                    // Extraction des informations
                    // Extraction du titre et du nombre de postes
                    Element titElement = jobDoc.selectFirst("h5 p span");
                    String title = titElement != null ? titElement.text() : "NA";

                    int numberOfPosts = 1;
                    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\((\\d+)\\)").matcher(title);
                    if (matcher.find()) {
                        numberOfPosts = Integer.parseInt(matcher.group(1));
                    }
                    if (!title.equals("NA")) {
                        title = title.replaceAll("\\((\\d+)\\)", "").trim();
                    }

                    // Extraction de la date
                    Element dateElement = jobDoc.selectFirst("p.info_offre strong:contains(Date :)");
                    String date = dateElement != null ? dateElement.nextSibling().toString().trim().replace("\"", "") : "NA";

                    // Extraction de l'agence
                    Element agencyElement = jobDoc.selectFirst("p.info_offre strong:contains(Agence :)");
                    String adresseEntreprise = agencyElement != null ? agencyElement.nextSibling().toString().trim().replace("\"", "") : "NA";

                    // Extraction de la formation
                    Element formationElement = jobDoc.selectFirst("span:contains(Formation :)");
                    String formation = "NA";
                    if (formationElement != null && formationElement.nextElementSibling() != null) {
                        formation = formationElement.nextElementSibling().text();
                    }

                    // Extraction du lieu de travail (ville)
                    Element villeElement = jobDoc.selectFirst("span:contains(Lieu de travail :)");
                    String ville = villeElement != null && villeElement.nextElementSibling() != null ? villeElement.nextElementSibling().text() : "NA";
                    String region = villeElement != null && villeElement.nextElementSibling() != null ? villeElement.nextElementSibling().text() : "NA";
                    // Extraction du type de contrat
                    Element contratElement = jobDoc.selectFirst("span:contains(Type de contrat :)");
                    String contractType = contratElement != null && contratElement.nextElementSibling() != null ? contratElement.nextElementSibling().text() : "NA";

                    // Extraction du secteur d'activité
                    Element SecteurElement = jobDoc.selectFirst("span:contains(Secteur)");
                    String secteur = SecteurElement != null && SecteurElement.nextElementSibling() != null ? SecteurElement.nextElementSibling().text() : "NA";

                    // Extraction des caractéristiques
                    Element CaractElement = jobDoc.selectFirst("span:contains(Caractéristiques)");
                    String caract = CaractElement != null && CaractElement.nextElementSibling() != null ? CaractElement.nextElementSibling().text() : "NA";

                    // Extraction de la date de début
                    Element DebutElement = jobDoc.selectFirst("span:contains(Date de début)");
                    String Debut = DebutElement != null && DebutElement.nextElementSibling() != null ? DebutElement.nextElementSibling().text() : "NA";

                    // Extraction de la description
                    Element DescElement = jobDoc.selectFirst("span:contains(Description)");
                    String desc = DescElement != null && DescElement.parent().nextElementSibling() != null ?
                            DescElement.parent().nextElementSibling().text() : "NA";

                    Element ExpElement = jobDoc.selectFirst("span:contains(Expérience)");
                    String exper = ExpElement != null && ExpElement.nextElementSibling() != null ? ExpElement.nextElementSibling().text() : "NA";

                    Element posteElement = jobDoc.selectFirst("span:contains(Poste)");
                    String poste = posteElement != null && posteElement.nextElementSibling() != null ? posteElement.nextElementSibling().text() : "NA";
                    Element profilRechercheElement = jobDoc.selectFirst("span:contains(Description du profil :)");
                    String profilRecherche = "NA";
                    if (profilRechercheElement != null && profilRechercheElement.parent() != null &&
                            profilRechercheElement.parent().nextElementSibling() != null) {
                        profilRecherche = profilRechercheElement.parent().nextElementSibling().text();
                    }

                    String competencesRecommandees = jobDoc.select("p:contains(Compétences spécifiques)").select("span").text();
                    String langue = jobDoc.select("span:contains(Langues :) next p").select("span").text();
                    String datePoste="non spécifie ";
                    Element entrepriseElement = jobDoc.selectFirst("td:nth-child(6)");
                    String entreprise = entrepriseElement != null && entrepriseElement.nextElementSibling() != null ? entrepriseElement.nextElementSibling().text() : "NA";
                    String descrposte="non spécifie";
                    String metier ="non spécifie";
                    String educationLevel="non spécifie";
                    String traitsPersonnalite="non spécifie ";
                    String hardSkills="non spécifie";
                    String softSkills="non spécifie";
                    String niveauLangue=langue;
                    String salaireText ="non specifie";
                    String avantagesSociaux ="non specifie";
                    String teletravail ="non specifie";







                    String sitename = "Anapec";
                    // Afficher les résultats pour le débogage
                    System.out.println("Post Name: " + title);
                    System.out.println("Date de publication: " + datePoste);
                    System.out.println("Lien: " + url);
                    System.out.println("Site Name:"+ sitename);
                    System.out.println("Adresse Entreprise: " + (adresseEntreprise.isEmpty() ? "Non spécifié" : adresseEntreprise));
                    System.out.println("Entreprise: " + entreprise);
                    System.out.println("Description Entreprise: " + desc);
                    System.out.println("Description Poste: " + descrposte);
                    System.out.println("Région: " + region );
                    System.out.println("Ville: " + ville );
                    System.out.println("Secteur: " + secteur);
                    System.out.println("Métier: " + metier);
                    System.out.println("Type de Contrat: " + contractType);
                    System.out.println("Niveau d'Étude: " + educationLevel);
                    System.out.println("Spécialité Diplôme: " + formation);
                    System.out.println("Expérience: " + exper);
                    System.out.println("Profil Recherché: " + profilRecherche);
                    System.out.println("Traits de Personnalité: " + traitsPersonnalite);
                    System.out.println("Hard Skills: " + hardSkills);
                    System.out.println("Soft Skills: " + softSkills);
                    System.out.println("Compétences Recommandées: " + competencesRecommandees);
                    System.out.println("Langues: " + langue);
                    System.out.println("Niveau de Langue: " + niveauLangue);
                    System.out.println("Salaire: " + salaireText);
                    System.out.println("Avantages Sociaux: " + avantagesSociaux);
                    System.out.println("Télétravail: " + teletravail);

                    System.out.println("=========================================");

                    // Insertion dans la base de données
                    assert conn != null;
                    insertJobToDatabase(conn, title, link, sitename, date, datePoste,
                            adresseEntreprise, link, entreprise, desc,
                            descrposte, region, ville, secteur, metier,
                            contractType, educationLevel, formation,
                            exper, profilRecherche, traitsPersonnalite,
                            hardSkills, softSkills, competencesRecommandees,
                            langue, niveauLangue, salaireText, avantagesSociaux,
                            teletravail);

                }
                currentPage++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Connection connectToDatabase(String url, String user, String password) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données réussie !");
            return conn;
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
    }


    private static void insertJobToDatabase(Connection conn, String title, String url, String sitename, String date, String datePoste,
                                            String adresseEntreprise, String link, String entreprise, String desc,
                                            String descrposte, String region, String ville, String secteur, String metier,
                                            String contractType, String educationLevel, String formation,
                                            String exper, String profilRecherche, String traitsPersonnalite,
                                            String hardSkills, String softSkills, String competencesRecommandees,
                                            String langue, String niveauLangue, String salaireText, String avantagesSociaux,
                                            String teletravail) {
        String query = "INSERT INTO offres_emploi (titre, url, site_name, date_publication, date_postuler, " +
                "adresse_entreprise, site_web_entreprise, nom_entreprise, description_entreprise, " +
                "description_poste, region, ville, secteur_activite, metier, type_contrat, niveau_etudes, " +
                "specialite_diplome, experience, profil_recherche, traits_personnalite, hard_skills, " +
                "soft_skills, competences_recommandees, langue, niveau_langue, salaire, avantages_sociaux, " +
                "teletravail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, url);
            pstmt.setString(3, sitename);
            pstmt.setString(4, date);
            pstmt.setString(5, datePoste);
            pstmt.setString(6, adresseEntreprise);
            pstmt.setString(7, link);
            pstmt.setString(8, entreprise);
            pstmt.setString(9, desc);
            pstmt.setString(10, descrposte);
            pstmt.setString(11, region);
            pstmt.setString(12, ville);
            pstmt.setString(13, secteur);
            pstmt.setString(14, metier);
            pstmt.setString(15, contractType);
            pstmt.setString(16, educationLevel);
            pstmt.setString(17, formation);
            pstmt.setString(18, exper);
            pstmt.setString(19, profilRecherche);
            pstmt.setString(20, traitsPersonnalite);
            pstmt.setString(21, hardSkills);
            pstmt.setString(22, softSkills);
            pstmt.setString(23, competencesRecommandees);
            pstmt.setString(24, langue);
            pstmt.setString(25, niveauLangue);
            pstmt.setString(26, salaireText);
            pstmt.setString(27, avantagesSociaux);
            pstmt.setString(28, teletravail);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur d'insertion pour le titre : " + title + ", Message : " + ex.getMessage());
        }
    }
}
