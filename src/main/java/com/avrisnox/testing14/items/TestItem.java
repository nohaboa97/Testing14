package com.avrisnox.testing14.items;

import com.avrisnox.testing14.Testing14;
import net.minecraft.item.Item;

public class TestItem extends Item {
	public TestItem() {
		super(new Item.Properties()
				.maxStackSize(69)
				.group(Testing14.setup.itemGroup));
		setRegistryName("testitem");
	}
}
