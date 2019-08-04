package com.avrisnox.testing14.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.avrisnox.testing14.blocks.ModBlocks.TEST_BLOCK_TILE;

public class TestBlockTile extends TileEntity implements ITickableTileEntity {
	private ItemStackHandler handler;

	public TestBlockTile() {
		super(TEST_BLOCK_TILE);
	}

	@Override
	public void tick() {
	}

	@Override
	public void read(CompoundNBT tag) {
		CompoundNBT invTag = tag.getCompound("inv");
		getHandler().deserializeNBT(invTag);
		super.read(tag);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		CompoundNBT compound = getHandler().serializeNBT();
		tag.put("inv", compound);
		return super.write(tag);
	}

	private ItemStackHandler getHandler() {
		if (handler == null)
			handler = new ItemStackHandler(1) {
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return stack.getItem() == Items.NETHER_STAR;
				}

				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
					if(stack.getItem() != Items.NETHER_STAR)
						return stack;
					return super.insertItem(slot, stack, simulate);
				}
			};
		return handler;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> (T) getHandler());
		}
		return super.getCapability(cap, side);
	}
}
