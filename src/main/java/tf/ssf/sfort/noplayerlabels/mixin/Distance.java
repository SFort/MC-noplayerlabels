package tf.ssf.sfort.noplayerlabels.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Environment(EnvType.CLIENT)
@Mixin(value = PlayerEntityRenderer.class, priority = 4100)
public class Distance {
	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		ClientPlayerEntity p= MinecraftClient.getInstance().player;
		if ( p != null && p.getBlockPos().isWithinDistance(abstractClientPlayerEntity.getPos(), Config.distance) ^ Config.wall)
			info.cancel();
	}
}
