package tf.ssf.sfort.noplayerlabels.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class AlwaysOff {
	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
			info.cancel();
	}
}
