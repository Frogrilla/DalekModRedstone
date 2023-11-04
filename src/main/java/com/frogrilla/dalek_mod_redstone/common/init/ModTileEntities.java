package com.frogrilla.dalek_mod_redstone.common.init;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimPanelTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
        DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DalekModRedstone.MOD_ID);

    public static RegistryObject<TileEntityType<RemoteLockTile>> REMOTE_LOCK_TILE =
            TILE_ENTITIES.register("remote_lock_tile", () -> TileEntityType.Builder.of(
                    RemoteLockTile::new, ModBlocks.REMOTE_LOCK.get()).build(null));

    public static RegistryObject<TileEntityType<StattenheimPanelTile>> STATTENHEIM_BLOCK_TILE =
            TILE_ENTITIES.register("stattenheim_panel_tile", () -> TileEntityType.Builder.of(
                    StattenheimPanelTile::new, ModBlocks.STATTENHEIM_PANEL.get()).build(null));
    public static void register(IEventBus eventBus){
        TILE_ENTITIES.register(eventBus);
    }
}
