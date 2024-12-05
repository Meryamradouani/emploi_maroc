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
        Connection conn = connectToDatabase("jdbc:mysql://localhost:3306/emploi_maroc", "root", "");

        if (conn == null) {
            System.out.println("Connexion à la base échouée. Arrêt du programme.");
            return;
        }

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
                    String datePoste = date.text();  // Date de publication


                    Elements region = offre.select(".location");
                    String ville = region.text();  // Ville (région)


                    try {
                        Document docu = Jsoup.connect(hr).get();
                        Element titre = docu.selectFirst(".offer-title");
                        String titrePoste = titre != null ? titre.text() : "Non spécifié";

                        // Extraction des détails spécifiques
                        Element ul = docu.selectFirst(".list-details");
                        Elements societe = ul.select("li:nth-child(1) h3");
                        String companyName = societe.text();

                        Elements contrat = ul.select("li:nth-child(2) h3");
                        String contractType = contrat.text();

                        Elements salaire = ul.select("li:nth-child(3) h3");
                        String salaireText = salaire.text();

                        Elements content = docu.select(".the-content div");
                        String descrEnt = content.size() > 0 ? content.get(0).text() : "Non spécifié";
                        String descrposte = content.size() > 1 ? content.get(1).text() : "Non spécifié";
                        String profilRecherche = content.size() > 2 ? content.get(2).text() : "Non spécifié";
                        String secteur = content.size() > 3 ? content.get(3).text() : "Non spécifié";
                        String metier = content.size() > 4 ? content.get(4).text() : "Non spécifié";
                        String experience = content.size() > 5 ? content.get(5).text() : "Non spécifié";
                        String etude = content.size() > 6 ? content.get(6).text() : "Non spécifié";
                        String langue = content.size() > 7 ? content.get(7).text() : "Non spécifié";

                        // Afficher les résultats pour le débogage
                        System.out.println("Post Name: " + titrePoste);
                        System.out.println("Date de publication: " + datePoste);
                        System.out.println("Lien: " + hr);
                        System.out.println("Type de contrat: " + contractType);
                        System.out.println("Expérience: " + experience);
                        System.out.println("Ville: " + ville);
                        System.out.println("Niveau d'étude: " + etude);
                        System.out.println("Compétences: " + profilRecherche);
                        System.out.println("Entreprise: " + companyName);
                        System.out.println("Secteur: " + secteur);
                        System.out.println("Salaire: " + salaireText);
                        System.out.println("Langues détectées: " + langue);
                        System.out.println("Description: " + descrEnt);
                        System.out.println("=========================================");

                        // Insérer les informations dans la base de données
                        insertJobToDatabase(titrePoste, datePoste, baseUrl, contractType, experience, ville, etude,
                                profilRecherche, companyName, secteur, langue, descrposte, profilRecherche);
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
    private static void insertJobToDatabase(String titre, String datePoste, String baseUrl, String contractType,
                                            String experience, String ville, String etude, String profilRecherche,
                                            String companyName, String secteur, String langues,
                                            String descrPoste, String profilRecherche1) {
        try (Connection conn = connectToDatabase("jdbc:mysql://localhost:3306/emploi_maroc", "root", "")) {
            if (conn != null) {
                String query = "INSERT INTO rekrute_jobs (title, description, date_posted, contract_type, experience_required, region, " +
                        "education_level, lien, hard_skills, languages, company_name, sector) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    // Paramètres de la requête préparée
                    stmt.setString(1, titre);  // Titre de l'offre
                    stmt.setString(2, descrPoste);  // Description du poste
                    stmt.setString(3, datePoste);  // Date de publication
                    stmt.setString(4, contractType);  // Type de contrat
                    stmt.setString(5, experience);  // Expérience requise
                    stmt.setString(6, ville);  // Ville
                    stmt.setString(7, etude);  // Niveau d'études
                    stmt.setString(8, baseUrl);  // Lien vers l'offre
                    stmt.setString(9, profilRecherche);  // Compétences requises
                    stmt.setString(10, langues);  // Langues
                    stmt.setString(11, companyName);  // Nom de l'entreprise
                    stmt.setString(12, secteur);  // Secteur

                    stmt.executeUpdate();
                    System.out.println("Offre insérée avec succès dans la base de données !");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion dans la base de données : " + e.getMessage());
        }
    }
}
