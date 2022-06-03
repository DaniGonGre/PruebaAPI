import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

public class Prueba {

    public static void main(String[] args) {

        final String token = args[0];
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();

        // Creamos el embed para la imágen
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Grogu")
                .image("attachment://imagenNueva.jpeg")
                .build();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();

            /**
             * El bot al recibir el comando "!imgDrive" insertará la imágen que
             * anteriormente buscamos en Google  Drive y descargamos en un fichero nuevo
             */

            if ("!imgDrive".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();

                InputStream fileAsInputStream = null;
                try {
                    fileAsInputStream = new FileInputStream("/home/dam1/IdeaProjects/PruebaAPI/src/main/resources/imagenNueva.jpeg");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                channel.createMessage(MessageCreateSpec.builder()
                        .addEmbed(embed)
                        .addFile("/home/dam1/IdeaProjects/PruebaAPI/src/main/resources/imagenNueva.jpeg", fileAsInputStream)
                        .build()).subscribe();
            }

            /**
             * El bot al recibir el comando "/pdf" mandará un archivo .pfg que
             * hemos extraído de Google Drive y descargado en un fichero nuevo
             */

            if ("/pdf".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();

                InputStream fileAsInputStream = null;
                try {
                    fileAsInputStream = new FileInputStream("/home/dam1/IdeaProjects/PruebaAPI/src/main/resources/googleDoc.pdf");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                channel.createMessage(MessageCreateSpec.builder()
                        .addFile("googleDoc.pdf", fileAsInputStream)
                        .build()).subscribe();
            }

        });

        gateway.onDisconnect().block();
    }
}





