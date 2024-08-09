package fr.estecka.uuidpreserve.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;


@Mixin(MobEntity.class)
public abstract class MobEntityMixin
{
	/**
	 * Reimplements most of the tail end of the method, but performs the actions
	 * in a different order. The current entity needs to be discarded before the
	 * new one is spawned in order to avoid UUID conflicts.
	 */
	@Inject( method="convertTo", cancellable=true, at=@At(value="INVOKE", target="net/minecraft/world/World.spawnEntity(Lnet/minecraft/entity/Entity;)Z") )
	private void PreserveUuid(EntityType<?> neoType, boolean keepEquipment, CallbackInfoReturnable<MobEntity> ci, @Local MobEntity neoEntity)
	{
		MobEntity me = (MobEntity)(Object)this;
		Entity vehicle = me.getVehicle();

		me.discard();
		neoEntity.setUuid(me.getUuid());
		me.getWorld().spawnEntity(neoEntity);
		if (vehicle != null)
			neoEntity.startRiding(vehicle);

		ci.setReturnValue(neoEntity);
	}
}
