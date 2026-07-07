package com.valverdeball.createtablegames;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "createtablegames", value = Dist.CLIENT)
  public class ClientModEvents {
    
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(ModMenuTypes.CHESS_TABLE_MENU.get(), ChessTableScreen::new);
    }
  }