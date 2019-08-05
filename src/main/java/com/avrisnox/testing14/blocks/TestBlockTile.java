package com.avrisnox.testing14.blocks;

import com.avrisnox.testing14.Config;
import com.avrisnox.testing14.utils.EnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.avrisnox.testing14.blocks.ModBlocks.TEST_BLOCK_TILE;

public class TestBlockTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
	private int counter;

	public TestBlockTile() {
		super(TEST_BLOCK_TILE);
	}

	@Override
	public void tick() {
		if(world.isRemote)
			return;

		if (counter > 0) {
			counter--;
			if (counter <= 0)
				energy.ifPresent(e -> ((EnergyStorage) e).addEnergy(Config.TEST_BLOCK_GENERATE.get()));
			markDirty();
		}

		if(counter <= 0) {
			handler.ifPresent(h -> {
				ItemStack stack = h.getStackInSlot(0);
				if (stack.getItem() == Items.NETHER_STAR) {
					h.extractItem(0, 1, false);
					counter = Config.TEST_BLOCK_TICKS.get();
				}
			});
		}

		BlockState blockState = world.getBlockState(pos);
		if( blockState.get(BlockStateProperties.POWERED) != counter > 0)
			world.setBlockState(pos, blockState.with(BlockStateProperties.POWERED, counter > 0), 3);

		sendOutPower();
	}

	private void sendOutPower() {
		energy.ifPresent(e -> {
			AtomicInteger capacity = new AtomicInteger(e.getEnergyStored());
			if (capacity.get() > 0) {
				for (Direction direction : Direction.values()) {
					TileEntity te = world.getTileEntity(pos.offset(direction));
					if (te != null) {
						boolean cont = te.getCapability(CapabilityEnergy.ENERGY, direction).map(h -> {
									if (h.canReceive()) {
										int received = h.receiveEnergy(Math.min(capacity.get(), Config.TEST_BLOCK_OUTPUT.get()), false);
										capacity.addAndGet(-received);
										((EnergyStorage) e).subEnergy(received);
										markDirty();
										return capacity.get() > 0;
									} else
										return true;
								}
						).orElse(true);
						if (!cont)
							return;
					}
				}
			}
		});
	}

	@Override
	public void read(CompoundNBT tag) {
		CompoundNBT invTag = tag.getCompound("inv");
		handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
		CompoundNBT energyTag = tag.getCompound("energy");
		energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));
		super.read(tag);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		handler.ifPresent(h -> {
			CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
			tag.put("inv", compound);
		});
		energy.ifPresent(h -> {
			CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
			tag.put("energy", compound);
		});
		return super.write(tag);
	}

	private ItemStackHandler createHandler() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
			}

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return stack.getItem() == Items.NETHER_STAR;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (stack.getItem() != Items.NETHER_STAR)
					return stack;
				return super.insertItem(slot, stack, simulate);
			}
		};
	}

	private IEnergyStorage createEnergy() {
		return new EnergyStorage(Config.TEST_BLOCK_MAX_POWER.get(), 0);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return handler.cast();
		if (cap == CapabilityEnergy.ENERGY)
			return energy.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getType().getRegistryName().getPath());
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
		return new TestBlockContainer(i, world, pos, inv, player);
	}
}
