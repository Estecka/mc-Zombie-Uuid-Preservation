package fr.estecka.uuidpreserve.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.conversion.EntityConversionContext.Finalizer;
import net.minecraft.entity.mob.MobEntity;


@Mixin(MobEntity.class)
public abstract class MobEntityMixin
{
	/**
	 * Reimplements most of the tail end of the method, but performs the actions
	 * in a different order. The old entity needs to be discarded before the new
	 * one is spawned in order to avoid UUID conflicts.
	 */
	@Inject( method="convertTo", cancellable=true, at=@At(value="INVOKE", target="net/minecraft/server/world/ServerWorld.spawnEntity(Lnet/minecraft/entity/Entity;)Z") )
	private void PreserveUuid(EntityType<?> neoType, EntityConversionContext context, SpawnReason reason, Finalizer<?> finalizer, CallbackInfoReturnable<MobEntity> ci, @Local MobEntity neoEntity)
	{
		if (context.type().shouldDiscardOldEntity()) {
			MobEntity oldEntity = (MobEntity)(Object)this;
			oldEntity.discard();
			neoEntity.setUuid(oldEntity.getUuid());
			oldEntity.getWorld().spawnEntity(neoEntity);
			ci.setReturnValue(neoEntity);
		}
	}
}
