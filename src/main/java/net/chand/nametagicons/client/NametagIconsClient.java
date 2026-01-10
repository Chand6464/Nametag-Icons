package net.chand.nametagicons.client;

import com.mojang.blaze3d.platform.InputUtil;
import net.chand.nametagicons.Config;
import net.chand.nametagicons.NametagIcons;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NametagIconsClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;
    public static Config config;

    @Override
    public void onInitializeClient() {
        NametagIcons.LOGGER.info("Initializing NametagIcons client");

        // load config
        config = Config.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nametagicons.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.nametagicons"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null) {
                    Screen current = mc.currentScreen;
                    mc.setScreen(new NametagIconScreen(current));
                }
            }
        });
    }
}
