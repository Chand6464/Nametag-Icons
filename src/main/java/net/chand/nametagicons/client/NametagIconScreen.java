package net.chand.nametagicons.client;

import net.chand.nametagicons.Config;
import net.chand.nametagicons.NametagIcons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.swing.*;
import java.io.File;

public class NametagIconScreen extends Screen {
    private final Screen parent;
    private ButtonWidget toggleBackgroundBtn;
    private ButtonWidget toggleIconsBtn;
    private ButtonWidget chooseIconBtn;

    protected NametagIconScreen(Screen parent) {
        super(Text.literal("Nametag Icons"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        Config cfg = NametagIconsClient.config;

        int centerX = this.width / 2;
        int y = this.height / 4;

        this.toggleBackgroundBtn = this.addDrawableChild(new ButtonWidget(centerX - 100, y, 200, 20, Text.literal(getBackgroundText(cfg)), button -> {
            cfg.transparentNametagBackground = !cfg.transparentNametagBackground;
            button.setMessage(Text.literal(getBackgroundText(cfg)));
            cfg.save();
        }));

        y += 24;

        this.toggleIconsBtn = this.addDrawableChild(new ButtonWidget(centerX - 100, y, 200, 20, Text.literal(getIconsText(cfg)), button -> {
            cfg.nametagIconsEnabled = !cfg.nametagIconsEnabled;
            button.setMessage(Text.literal(getIconsText(cfg)));
            cfg.save();
        }));

        y += 24;

        this.chooseIconBtn = this.addDrawableChild(new ButtonWidget(centerX - 100, y, 200, 20, Text.literal(getChooseText(cfg)), button -> {
            // open native file chooser on separate AWT thread
            SwingUtilities.invokeLater(() -> {
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Choose PNG icon for nametag");
                    chooser.setAcceptAllFileFilterUsed(false);
                    chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
                    int result = chooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File f = chooser.getSelectedFile();
                        if (f != null && f.exists()) {
                            cfg.iconPath = f.getAbsolutePath();
                            cfg.save();
                            // update button label on render thread
                            this.client.execute(() -> this.chooseIconBtn.setMessage(Text.literal(getChooseText(cfg))));
                            NametagIcons.LOGGER.info("Selected icon: {}", cfg.iconPath);
                        }
                    }
                } catch (Exception e) {
                    NametagIcons.LOGGER.warn("Failed to open file chooser", e);
                }
            });
        }));

        y += 24;

        this.addDrawableChild(new ButtonWidget(centerX - 50, y, 100, 20, Text.literal("Done"), button -> this.client.setScreen(parent)));
    }

    private String getBackgroundText(Config cfg) {
        return "Transparent Nametag Background: " + (cfg.transparentNametagBackground ? "On" : "Off");
    }

    private String getIconsText(Config cfg) {
        return "Nametag Icons: " + (cfg.nametagIconsEnabled ? "On" : "Off");
    }

    private String getChooseText(Config cfg) {
        return "Choose Icon: " + (cfg.iconPath == null || cfg.iconPath.isEmpty() ? "(none)" : new File(cfg.iconPath).getName());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.textRenderer.drawWithShadow(matrices, this.title, (this.width - this.textRenderer.getWidth(this.title)) / 2f, 20f, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
