package com.syldarion.catastrophe;

import com.syldarion.catastrophe.block.UpheavalBlock;
import com.syldarion.catastrophe.entity.TileEntityData;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftGame;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("catastrophe")
public class Catastrophe
{
    public static UpheavalBlock upheavalBlock;
    public static BlockItem itemUpheavalBlock;
    public static TileEntityType<TileEntityData> tileEntityDataType;

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    // 20 ticks per second
    private static final int TICKS_PER_DAY = 24000;
    private final int nextCatastropheMinTicks = TICKS_PER_DAY / 2;
    private final int nextCatastropheMaxTicks = TICKS_PER_DAY * 7;

    private int nextCatastropheTicks = 0;
    private int currentCatastropheTick = 0;

    public Catastrophe() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("RUNNING SETUP CODE");

        nextCatastropheTicks = rollNextCatastropheTicks();
        LOGGER.info(String.format("Next Catastrophe in %d ticks", nextCatastropheTicks));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        // LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        // InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            return;
        }

        if(currentCatastropheTick >= nextCatastropheTicks) {
            LOGGER.info("CATASTROPHE SPAWN");
            currentCatastropheTick = 0;
            nextCatastropheTicks = rollNextCatastropheTicks();
        }
        else {
            currentCatastropheTick++;
        }
    }

    @SubscribeEvent
    public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
        upheavalBlock = (UpheavalBlock)(new UpheavalBlock().setRegistryName("catastrophe", "upheaval_spawn"));
        blockRegisterEvent.getRegistry().register(upheavalBlock);
    }

    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        final int MAX_STACK_SIZE = 1;

        Item.Properties itemSimpleProperties = new Item.Properties()
                .maxStackSize(MAX_STACK_SIZE)
                .group(ItemGroup.BUILDING_BLOCKS);
        itemUpheavalBlock = new BlockItem(upheavalBlock, itemSimpleProperties);
        itemUpheavalBlock.setRegistryName(upheavalBlock.getRegistryName());
        itemRegisterEvent.getRegistry().register(itemUpheavalBlock);
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
        tileEntityDataType = TileEntityType.Builder.create(TileEntityData::new, upheavalBlock).build(null);
        tileEntityDataType.setRegistryName("catastrophe:entity_upheaval_block");
        event.getRegistry().register(tileEntityDataType);
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    private int rollNextCatastropheTicks() {
        return new Random().nextInt(nextCatastropheMaxTicks - nextCatastropheMinTicks) + nextCatastropheMinTicks;
    }
}
