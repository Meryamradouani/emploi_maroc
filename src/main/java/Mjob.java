import java.io.IOException;
import java.sql.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Mjob {
    public static void main(String[] args) {

        String baseUrl = "https://m-job.ma/recherche/informatique-internet-multimedia-2";

        // Connexion à la base de données
        Connection conn = connectToDatabase("jdbc:mysql://localhost:3306/emploi", "root", "");
        if (conn != null) {
            System.out.println("Connexion réussie");
        } else {
            System.out.println("Échec de la connexion");
        }

        String datePoste = null;
        String titrePoste = null;
        String companyName = null;
        String descrEnt = null;
        String descrposte = null;
        Element region = null;
        Element ville = null;
        String secteur = null;
        String metier = null;
        String contractType = null;
        String experience = null;
        String profilRecherche = null;
        String langue = null;
        String salaireText = null;
        Elements content;
        try {
            int page = 1; // Commencer par la première page
            boolean hasNextPage = true;
            conn.setAutoCommit(false);  // Désactiver le commit automatique pour batch insert

            while (hasNextPage) {
                String url = baseUrl + "?page=" + page; // Construire l'URL pour chaque page
                System.out.println("Scraping page: " + page);
                Document doc = Jsoup.connect(url).get();

                // Sélection des offres sur la page courante
                Elements offres = doc.select(".offer-box");
                if (offres.isEmpty()) {
                    hasNextPage = false; // Arrêter si aucune offre n'est trouvée
                }

                for (Element offre : offres) {
                    Elements of = offre.select("h3 a");
                    String hr = of.attr("href");


                    // Extraction de la date, de la localisation et autres informations de l'offre
                    Elements date = offre.select(".date-buttons span");
                    datePoste = date.text();


                    ville = offre.selectFirst(".location");
                    region = offre.selectFirst(".location");


                    try {
                        Document docu = Jsoup.connect(hr).get();
                        Element titre = docu.selectFirst(".offer-title");
                        titrePoste = titre != null ? titre.text() : "Non spécifié";

                        // Extraction des détails spécifiques
                        Element ul = docu.selectFirst(".list-details");
                        Elements societe = ul.select("li:nth-child(1) h3");
                        companyName = societe.text();

                        Elements contrat = ul.select("li:nth-child(2) h3");
                        contractType = contrat.text();

                        Elements salaire = ul.select("li:nth-child(3) h3");
                        salaireText = salaire.text();
                        content = docu.select(".the-content div");
                        descrEnt = content.size() > 0 ? content.get(0).text() : "Non spécifié";
                        descrposte = content.size() > 1 ? content.get(1).text() : "Non spécifié";
                        profilRecherche = content.size() > 2 ? content.get(2).text() : "Non spécifié";
                        secteur = content.size() > 3 ? content.get(3).text() : "Non spécifié";
                        metier = content.size() > 4 ? content.get(4).text() : "Non spécifié";
                        experience = content.size() > 5 ? content.get(5).text() : "Non spécifié";
                        String etude = content.size() > 6 ? content.get(6).text() : "Non spécifié";
                        langue = content.size() > 7 ? content.get(7).text() : "Non spécifié";


                        String adresseEntreprise = "";
                        String Url = "Mjob";
                        String educationLevel = "non spécifie";
                        String specialiteDiplome = "non spécifie";
                        String traitsPersonnalite = "non spécifie";
                        String avantagesSociaux = "non spécifie";
                        String teletravail = "non spécifie";
                        String niveauLangue = "non spécifie";
                        String competencesRecommandees = "non spécifie";
                        String softSkills = "non spécifie";
                        String hardSkills = "non spécifie";

                        // Afficher les résultats pour le débogage
                        System.out.println("Post Name: " + titrePoste);
                        System.out.println("Date de publication: " + datePoste);
                        System.out.println("Lien: " + baseUrl);
                        System.out.println("Site Name: Mjob");
                        System.out.println("Adresse Entreprise: " + (adresseEntreprise.isEmpty() ? "Non spécifié" : adresseEntreprise));
                        System.out.println("Entreprise: " + companyName);
                        System.out.println("Description Entreprise: " + descrEnt);
                        System.out.println("Description Poste: " + descrposte);
                        System.out.println("Région: " + (region != null ? region.text() : "Non spécifié"));
                        System.out.println("Ville: " + (ville != null ? ville.text() : "Non spécifié"));
                        System.out.println("Secteur: " + secteur);
                        System.out.println("Métier: " + metier);
                        System.out.println("Type de Contrat: " + contractType);
                        System.out.println("Niveau d'Étude: " + educationLevel);
                        System.out.println("Spécialité Diplôme: " + specialiteDiplome);
                        System.out.println("Expérience: " + experience);
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
                        System.out.println("URL Base: " + baseUrl);
                        System.out.println("=========================================");
                        // Insérer les informations dans la base de données
                        // Insérer les informations dans la base de données
                        insertJobToDatabase(conn, titrePoste, hr, "Mjob", datePoste, "","", baseUrl, companyName, descrEnt,
                                descrposte, region != null ? region.text() : "Non spécifié", ville != null ? ville.text() : "Non spécifié",
                                secteur, metier, contractType, "non spécifié", "non spécifié", experience, profilRecherche,
                                "non spécifié", "non spécifié", "non spécifié", langue, "non spécifié", salaireText,
                                "non spécifié", "non spécifié");

                    } catch (Exception e) {
                        System.out.println("Erreur lors du traitement de l'offre : " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Vérifier s'il y a une page suivante
                Element nextPage = doc.selectFirst(".pagination .next");
                if (nextPage == null) {
                    hasNextPage = false;
                } else {
                    page++; // Passer à la page suivante
                }
            }

            // Effectuer un commit final des insertions en batch
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }




    }

    // Méthode pour se connecter à la base de données
    private static Connection connectToDatabase(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
    }

    // Méthode pour insérer les données dans la base de données
    private static void insertJobToDatabase(Connection conn, String titrePoste, String Url, String siteName, String datePoste,
                                            String adresseEntreprise, String baseUrl, String companyName, String descrEnt,
                                            String descrposte, String region, String ville, String secteur, String metier,
                                            String contractType, String educationLevel, String specialiteDiplome,
                                            String experience, String profilRecherche, String traitsPersonnalite,
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
            pstmt.setString(1, titrePoste);
            pstmt.setString(2, baseUrl);
            pstmt.setString(3, siteName);
            pstmt.setString(4, datePoste);
            pstmt.setString(5, datePoste);
            pstmt.setString(6, adresseEntreprise);
            pstmt.setString(7, Url);
            pstmt.setString(8, companyName);
            pstmt.setString(9, descrEnt);
            pstmt.setString(10, descrposte);
            pstmt.setString(11, region);
            pstmt.setString(12, ville);
            pstmt.setString(13, secteur);
            pstmt.setString(14, metier);
            pstmt.setString(15, contractType);
            pstmt.setString(16, educationLevel);
            pstmt.setString(17, specialiteDiplome);
            pstmt.setString(18, experience);
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
            System.out.println("Erreur d'insertion pour le titre : " + titrePoste + ", Message : " + ex.getMessage());
        }
    }
}