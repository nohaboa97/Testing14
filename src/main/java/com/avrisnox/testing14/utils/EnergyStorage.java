package com.avrisnox.testing14.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class EnergyStorage extends net.minecraftforge.energy.EnergyStorage implements INBTSerializable<CompoundNBT> {
	public EnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void addEnergy(int energy) {
		this.energy += energy;
		if(this.energy > getMaxEnergyStored())
			this.energy = getEnergyStored();
	}

	public void subEnergy(int energy) {
		this.energy -= energy;
		if(this.energy < 0)
			this.energy = 0;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("energy", getEnergyStored());
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setEnergy(nbt.getInt("energy"));
	}
}
