package net.vercte.lavabuckettrash;

import net.fabricmc.api.ModInitializer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LavaBucketTrash implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("lavabuckettrash");

	public static boolean canTrashItem(ItemStack stack, ItemStack incoming, Slot slot, Player player) {
		boolean isShulkerBox = false;
		if(incoming.getItem() instanceof BlockItem) {
			BlockItem blockItem = (BlockItem)incoming.getItem();
			isShulkerBox = blockItem.getBlock().defaultBlockState().is(BlockTags.SHULKER_BOXES);
		}

		boolean verdict = stack.getItem() == Items.LAVA_BUCKET
				&& !incoming.isEmpty()
				&& !player.getAbilities().instabuild
				&& slot.allowModification(player)
				&& slot.mayPlace(stack)
				&& !incoming.getItem().isFireResistant()
				&& !isShulkerBox;
		
		return verdict;
	}

	public static boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if(action != ClickAction.SECONDARY) return false;
		if(canTrashItem(stack, incoming, slot, player)) {
			incoming.setCount(0);
			if(!player.level().isClientSide) {
				player.level().playSound(null, player.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.25F, 2F + (float)Math.random());
			}
			return true;
		}
		return false;
	}

	@Override
	public void onInitialize() {}
}