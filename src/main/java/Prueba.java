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

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Grogu")
                .image("attachment://imagenNueva.jpeg")
                .build();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();

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

        });

        gateway.onDisconnect().block();
    }
}





