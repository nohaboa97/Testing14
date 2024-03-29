package com.avrisnox.testing14.blocks;

import com.avrisnox.testing14.utils.EnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import static com.avrisnox.testing14.blocks.ModBlocks.TEST_BLOCK_CONTAINER;

public class TestBlockContainer extends Container {
	private TileEntity tileEntity;
	private PlayerEntity playerEntity;
	private IItemHandler playerInventory;

	public TestBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv, PlayerEntity player) {
		super(TEST_BLOCK_CONTAINER, id);
		tileEntity = world.getTileEntity(pos);
		tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		playerEntity = player;
		playerInventory = new InvWrapper(inv);

		tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
			addSlot(new SlotItemHandler(h, 0, 64, 24));
		});
		layoutPlayerInventorySlots(10, 70);

		func_216958_a(new IntReferenceHolder() {

			@Override
			public int get() {
				return getEnergy();
			}

			@Override
			public void set(int value) {
				tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> ((EnergyStorage)h).setEnergy(value));
			}
		});
	}

	public int getEnergy() {
		return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			itemstack = stack.copy();
			if (index == 0) {
				if (!this.mergeItemStack(stack, 1, 37, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(stack, itemstack);
			} else {
				if (stack.getItem() == Items.NETHER_STAR) {
					if (!this.mergeItemStack(stack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 28) {
					if (!this.mergeItemStack(stack, 28, 37, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 37 && !this.mergeItemStack(stack, 1, 28, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (stack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, stack);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, ModBlocks.TEST_BLOCK);
	}

	private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
		for (int i = 0; i < amount; i++) {
			addSlot(new SlotItemHandler(handler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
		for (int j = 0; j < verAmount; j++) {
			index = addSlotRange(handler, index, x, y, horAmount, dx);
			y += dy;
		}
		return index;
	}

	private void layoutPlayerInventorySlots(int leftCol, int topRow) {
		addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

		topRow += 58;
		addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
	}
}
