package com.avrisnox.testing14;

import com.avrisnox.testing14.blocks.ModBlocks;
import com.avrisnox.testing14.blocks.TestBlock;
import com.avrisnox.testing14.blocks.TestBlockContainer;
import com.avrisnox.testing14.blocks.TestBlockTile;
import com.avrisnox.testing14.items.TestItem;
import com.avrisnox.testing14.utils.ClientProxy;
import com.avrisnox.testing14.utils.IProxy;
import com.avrisnox.testing14.utils.ModSetup;
import com.avrisnox.testing14.utils.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("testing14")
public class Testing14 {
	public static final String MODID = "testing14";
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	public static ModSetup setup = new ModSetup();
	private static final Logger LOGGER = LogManager.getLogger();

	public Testing14() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

		// Register the utils method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("testing14-client.toml"));
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("testing14-common.toml"));
	}

	private void setup(final FMLCommonSetupEvent event) {
		setup.init();
		proxy.init();
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
			event.getRegistry().register(new TestBlock());
			// TODO: Register blocks here
		}

		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
			Item.Properties properties = new Item.Properties().group(setup.itemGroup);

			event.getRegistry().register(new BlockItem(ModBlocks.TEST_BLOCK, properties).setRegistryName("testblock"));
			// TODO: Register block items here

			event.getRegistry().register(new TestItem());
			// TODO: Register items here
		}

		@SubscribeEvent
		public static void onTileEntityRegistery(final RegistryEvent.Register<TileEntityType<?>> event) {
			event.getRegistry().register(TileEntityType.Builder.create(TestBlockTile::new, ModBlocks.TEST_BLOCK).build(null).setRegistryName("testblock"));
		}

		@SubscribeEvent
		public static void onContainerRegistery(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            	BlockPos pos = data.readBlockPos();
            	return new TestBlockContainer(windowId, Testing14.proxy.getClientWorld(), pos, inv, Testing14.proxy.getClientPlayer());
			}).setRegistryName("testblock"));
		}
	}
}
