package me.sigreturn.censor;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.sigreturn.censor.natives.censor.Censor;
import me.sigreturn.censor.natives.util.Natives;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.logging.Level;

public class CensorPlugin extends JavaPlugin {
    private Censor censor;

    public CensorPlugin(@NonNullDecl final JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        censor = Natives.censor.get().create();

        getLogger().at(Level.INFO).log("Censor is using " + Natives.censor.getLoadedVariant() + " variant");

        registerEventHandlers();
    }

    private void registerEventHandlers() {
        getEventRegistry().registerGlobal(
                PlayerChatEvent.class,
                this::onPlayerChat
        );
    }

    private void onPlayerChat(final PlayerChatEvent event) {
        final String content = event.getContent();

        event.setContent(censor.censor(content));
    }
}
