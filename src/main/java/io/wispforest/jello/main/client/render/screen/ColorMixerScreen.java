package io.wispforest.jello.main.client.render.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.items.ArtistPalette;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.mixin.mixins.client.HandledScreenAccessor;
import io.wispforest.jello.main.network.ColorMixerSearchPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class ColorMixerScreen extends HandledScreen<ColorMixerScreenHandler> {
    private static final Identifier SCROLL = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier TEXTURE = new Identifier("jello", "textures/gui/color_mixer.png");

    private float scrollPosition;
    private boolean scrolling;

    private TextFieldWidget searchBox;
    private boolean ignoreTypedCharacter;

    public ColorMixerScreen(ColorMixerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 204;

        this.handler.artistInventory.addListener(sender -> {
            this.searchBox.active = sender.getStack(0).isOf(ItemRegistry.MainItemRegistry.ARTIST_PALETTE);
            this.searchBox.setVisible(this.searchBox.active);
        });
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.searchBox.getText();
        this.init(client, width, height);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.search();
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.ignoreTypedCharacter) {
            return false;
        } else {
            String string = this.searchBox.getText();
            if (this.searchBox.charTyped(chr, modifiers)) {
                if (!Objects.equals(string, this.searchBox.getText())) {
                    this.search();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;

        boolean bl = !this.isDyeListSlot(this.focusedSlot) || this.focusedSlot.hasStack();
        boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).toInt().isPresent();
        if (bl && bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
            this.ignoreTypedCharacter = true;
            return true;
        } else {
            String string = this.searchBox.getText();
            if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                if (!Objects.equals(string, this.searchBox.getText())) {
                    this.search();
                }

                return true;
            } else {
                return this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE || super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    private boolean isDyeListSlot(@Nullable Slot slot) {
        return slot != null && slot.inventory == this.getScreenHandler().dyeInventory;
    }

    private void search() {
        Jello.CHANNEL.clientHandle().send(new ColorMixerSearchPacket(this.searchBox.getText()));
        this.handler.search(this.searchBox.getText());
    }

    @Override
    public void handledScreenTick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        ItemStack stack = this.getScreenHandler().slots.get(0).getStack();

        int progress = 0;

        if (stack.isOf(ItemRegistry.MainItemRegistry.ARTIST_PALETTE)) {
            progress = Math.min(MathHelper.ceil((ArtistPalette.MAX_USES - stack.getOrCreateNbt().getInt("TimesUsed")) * .2421875), 62);
        }

        drawTexture(matrices, x + 19, y + 72 - progress, 176, 62 - progress, 6, 62);

        RenderSystem.setShaderTexture(0, SCROLL);

        int i = this.x + 156;
        int j = this.y + 24;
        int k = 73;

        this.drawTexture(matrices, i, j + (int) ((float) (k) * this.scrollPosition), 232, 0, 12, 15);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.isClickInScrollbar(mouseX, mouseY)) {
                this.scrolling = true;
                return true;
            } else {
                this.scrolling = false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        return mouseX >= this.x + 156 && mouseX <= this.x + 167 && mouseY >= this.y + 24 && mouseY <= this.y + 111;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int scrollbarTopY = this.y + 24;
            int scrollbarBottomY = scrollbarTopY + 88;
            this.scrollPosition = MathHelper.clamp((float) (mouseY - scrollbarTopY - 7.5) / ((scrollbarBottomY - scrollbarTopY) - 15), 0, 1);
            this.handler.scrollItems(this.scrollPosition);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (Screen.hasControlDown() || !this.handler.getCursorStack().isEmpty()) {
            final var slot = ((HandledScreenAccessor) this).jello$getSlotAt(mouseX, mouseY);
            if (slot != null) {
                this.client.interactionManager.clickSlot(this.handler.syncId, slot.id, 0, SlotActionType.PICKUP, this.client.player);
            }
        } else {
            float scrollBy = (float) (amount / Math.max(1, (int) Math.ceil(this.handler.itemList.size() / 6f) - 5));
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - scrollBy, 0.0F, 1.0F);
            this.handler.scrollItems(this.scrollPosition);
        }
        return true;
    }

    @Override
    protected void init() {
        super.init();

        // Center the title
        playerInventoryTitleX = 69420;
        playerInventoryTitleY = 69420;
        titleX = 69420;

        this.client.keyboard.setRepeatEvents(true);
        this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 44, this.y + 8, 106, 10, new TranslatableText("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setDrawsBackground(false);
        this.searchBox.setEditableColor(16777215);
        this.addSelectableChild(this.searchBox);

        this.searchBox.setVisible(true);
        this.searchBox.setFocusUnlocked(false);
        this.searchBox.setTextFieldFocused(true);

        this.searchBox.setText("");

        this.search();

//        this.listener =  new ScreenHandlerListener() {
//            @Override
//            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
//
//            }
//
//            @Override
//            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
//
//            }
//        }
    }
}
