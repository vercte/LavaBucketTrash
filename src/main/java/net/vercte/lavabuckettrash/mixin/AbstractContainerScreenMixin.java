package net.vercte.lavabuckettrash.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.vercte.lavabuckettrash.LavaBucketTrash;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Accessor public abstract T getMenu();
    @Accessor public abstract Slot getHoveredSlot();

    public AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderTooltip", at = @At("TAIL"))
    protected void renderTooltip(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(!getMenu().getCarried().isEmpty() && getHoveredSlot() != null && getHoveredSlot().hasItem()) {
            ItemStack itemStack = this.getHoveredSlot().getItem();
            ItemStack incoming = getMenu().getCarried();
            if(LavaBucketTrash.canTrashItem(itemStack, incoming, getHoveredSlot(), player)) {
                List<Component> text = List.of(Component.translatable("lavabuckettrash.tooltip").withStyle(ChatFormatting.RED));
                guiGraphics.renderTooltip(this.font, text, itemStack.getTooltipImage(), i, j);
            }
        }
    }
}
