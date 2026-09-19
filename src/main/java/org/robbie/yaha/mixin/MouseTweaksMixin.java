package org.robbie.yaha.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.robbie.yaha.features.bundles.IotaHolderBundle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yalter.mousetweaks.Main;

@Mixin(Main.class)
public class MouseTweaksMixin {
    @Definition(id = "stackOnMouse", local = @Local(type = ItemStack.class, argsOnly = true))
    @Definition(id = "getItem", method = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")
    @Definition(id = "BundleItem", type = BundleItem.class)
    @Expression("stackOnMouse.getItem() instanceof BundleItem")
    @ModifyExpressionValue(method = "rmbTweakMaybeClickSlot", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean yaha_isYahaBundle(boolean original, @Local ItemStack stackOnMouse) {
        return original || (stackOnMouse.getItem() instanceof IotaHolderBundle) ;
    }
}
