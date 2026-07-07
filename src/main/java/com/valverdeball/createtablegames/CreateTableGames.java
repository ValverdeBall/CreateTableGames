package com.valverdeball.createtablegames;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import java.util.function.Supplier;

@Mod("createtablegames")
  public class CreateTableGames {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = 
    DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "createtablegames");

    public static final Supplier<AttachmentType<PlayerFaction>> PLAYER_FACTION = ATTACHMENTS.register(
      "player_faction",
      () -> AttachmentType.serializable(PlayerFaction::new).copyOnDeath().build()
    );

    public CreateTableGames(IEventBus modEventBus) {
      ModBlocks.register(modEventBus);
      ModItems.register(modEventBus);
      ModBlockEntities.register(modEventBus);
      ModMenuTypes.register(modEventBus);
      ATTACHMENTS.register(modEventBus);
      modEventBus.addListener(this::registerNetwork);
      modEventBus.addListener(this::commonSetup);
      
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
      ModStressValues.register();
    }

    private void registerNetwork(final RegisterPayloadHandlersEvent event) {
      final PayloadRegistrar registrar = event.registrar("1.0.0");
      registrar.playToServer(FactionSelectPayload.TYPE, FactionSelectPayload.CODEC, FactionSelectPayload::handle);
    }
  }