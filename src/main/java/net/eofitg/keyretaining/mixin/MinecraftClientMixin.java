package net.eofitg.keyretaining.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void onScreenSet(Screen screen, CallbackInfo ci) {
        if (screen == null) {
            restoreKeyboardState();
        }
    }

    @Unique
    private void restoreKeyboardState() {
        MinecraftClient client = (MinecraftClient)(Object)this;
        if (client.options == null || client.getWindow() == null) return;

        Window window = client.getWindow();
        GameOptions options = client.options;

        System.out.println("[KeyRetaining] GUI closed, restoring keyboard state");

        for (KeyBinding keyBinding : options.allKeys) {
            try {
                InputUtil.Key key = keyBinding.getDefaultKey();
                int keyCode = key.getCode();
                boolean isDown = InputUtil.isKeyPressed(window, keyCode);
                KeyBinding.setKeyPressed(key, isDown);
            } catch (Exception ignored) {
            }
        }
    }
}