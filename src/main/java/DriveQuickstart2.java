import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart2 {
    /** Creamos una constante con el nombre de la aplicación. */
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    /** Creamos una constante para instanciar el JSON */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Creamos una constante con el token de autorización para esta aplicación. */
    private static final String TOKENS_DIRECTORY_PATH = "resources";
    /** Creamos una constante de tipo colección que contiene los permisos requeridos por la aplicación para entrar a drive*/
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    /** Creamos una constante que contiene la ruta del archivo de la credenciales */
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Buscamos las credenciales del cliente, añadiendo la excepción por si no se encuentra el archivo.
        InputStream in = DriveQuickstart2.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Muestra la solicitud de autorización al usuario
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("695023622635-cq4mq9hmfd598jahj2r7ivhnq7egncno.apps.googleusercontent.com");
        // Devuelve la creencial que debemos añadir con anterioridad, la de la ID de clientes OAuth
        return credential;
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Filtra para encontrar el archivo con el documento de google guardado en la carpeta 'documentoBot'.
        FileList result = service.files().list()
                .setQ("name contains 'documentoBot' and mimeType = 'application/vnd.google-apps.folder'")
                .setPageSize(100)
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .execute();

        // Creamos una lista que recoja los ficheros filtrados anteriormente
        List<File> files = result.getFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            String dirDocumento = null;
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());

                // Recogemos el ID de la imágen en una variable para poder buscarla
                dirDocumento = file.getId();
            }

            // Buscamos el documento que contenga la palabra 'googleDoc'
            FileList resultDocuments = service.files().list()
                    .setQ("name contains 'googleDoc' and parents in '" + dirDocumento + "'")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> filesDocuments = resultDocuments.getFiles();
            for (File file : filesDocuments) {
                System.out.printf("Documentos: %s\n", file.getName());

                // Guardamos en el fichero googleDoc.pdf después de crearlo
                OutputStream outputStream = new FileOutputStream("/home/dam1/IdeaProjects/PruebaAPI/src/main/resources/googleDoc.pdf");
                service.files().get(file.getId())
                        .executeMediaAndDownloadTo(outputStream);
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}
