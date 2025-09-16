package com.fox2code.tfcah.mixin;

import com.fox2code.tfcah.client.AnvilScreenAccessor;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen extends BlockEntityScreen<AnvilBlockEntity, AnvilContainer> implements AnvilScreenAccessor {
    public MixinAnvilScreen(AnvilContainer container, Inventory playerInventory, Component name, ResourceLocation texture) {
        super(container, playerInventory, name, texture);
    }


    @Override
    public AnvilBlockEntity tfcah$getAnvilBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public AnvilContainer tfcah$getAnvilContainer() {
        return this.menu;
    }
}
